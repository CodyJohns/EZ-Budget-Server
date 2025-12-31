package app.ezbudget.server.ezbudgetserver.service;

import app.ezbudget.server.ezbudgetserver.dao.DAOFactory;
import app.ezbudget.server.ezbudgetserver.exceptions.DailyItemNotFoundException;
import app.ezbudget.server.ezbudgetserver.model.Purchase;
import app.ezbudget.server.ezbudgetserver.model.PurchasedExpense;
import app.ezbudget.server.ezbudgetserver.model.User;
import app.ezbudget.server.ezbudgetserver.model.VariableExpense;
import app.ezbudget.server.ezbudgetserver.model.plaid.PlaidItem;
import app.ezbudget.server.ezbudgetserver.util.HTTPResponse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class PurchasesService extends JointService {

    public PurchasesService(DAOFactory factory) {
        super(factory);
    }

    public HTTPResponse getPurchases(String authtoken) {

        User user = getTargetUser(this.factory.getUserDAO().getUserByAuthtoken(authtoken));

        Map<String, PurchasedExpense> items = getDailyPurchases(user);

        return new HTTPResponse(200, "Ok", items);
    }

    public HTTPResponse savePurchases(String authtoken, String key, List<Purchase> items)
            throws DailyItemNotFoundException {

        User user = getTargetUser(this.factory.getUserDAO().getUserByAuthtoken(authtoken));

        float sum = 0;

        for (Purchase p : items)
            sum += p.amount;

        Map<String, PurchasedExpense> daily_items = getDailyPurchases(user);

        try {
            daily_items.get(key).amount = sum;
            daily_items.get(key).setPurchases(items);
        } catch (NullPointerException e) {
            throw new DailyItemNotFoundException("Item \"" + key + "\" was not found.");
        }

        this.factory.getPurchaseDAO().save(user.getAuthtoken(), daily_items);

        return new HTTPResponse(200, "Saved.");
    }

    private Map<String, PurchasedExpense> getDailyPurchases(User user) {
        PlaidService plaidService = new PlaidService(factory);

        Map<String, PurchasedExpense> existing;
        try {
            existing = factory.getPurchaseDAO().getExpensesWithPurchases(user.getAuthtoken());
        } catch (NullPointerException e) {
            existing = new HashMap<>();
        }

        // Build what the result should be based on user's current variable expenses
        Map<String, VariableExpense> presetsByName = user.getVariablePresets().stream()
                .collect(java.util.stream.Collectors.toMap(
                        VariableExpense::getName,
                        ve -> ve,
                        (a, b) -> b, // if duplicate names, last wins (or throw instead)
                        java.util.HashMap::new));

        // Find which ones have been removed
        List<PurchasedExpense> removed = existing.entrySet().stream()
                .filter(e -> !presetsByName.containsKey(e.getKey()))
                .map(Map.Entry::getValue)
                .toList();

        // Rebuild next map from presets, reusing existing PurchasedExpense when
        // possible
        boolean modified = false;
        List<String> itemIdsToSync = new ArrayList<>();

        Map<String, PurchasedExpense> next = new HashMap<>();

        for (var entry : presetsByName.entrySet()) {
            String name = entry.getKey();
            VariableExpense preset = entry.getValue();

            PurchasedExpense pe = existing.get(name);
            if (pe == null) {
                pe = new PurchasedExpense(preset);
                modified = true;

                if (preset.plaid_item_id != null) {
                    itemIdsToSync.add(preset.plaid_item_id);
                }
            } else {
                // Update fields on the reused object if preset changed
                modified |= applyPreset(pe, preset);
            }

            next.put(name, pe);
        }

        // Handle cleanup for removed entries and for plaid items
        if (!removed.isEmpty()) {
            modified = true;

            List<String> removeItemIds = removed.stream()
                    .map(pe -> pe.plaid_item_id)
                    .filter(Objects::nonNull)
                    .distinct()
                    .toList();

            if (!removeItemIds.isEmpty()) {
                for (String itemId : removeItemIds) {
                    PlaidItem item = factory.getTransactionDAO().getItem(itemId);
                    if (item != null) {
                        factory.getTransactionDAO().deleteItem(item);
                    }

                    // remove token from user
                    user.setPlaidAccessTokens(
                            user.getPlaidAccessTokens().stream()
                                    .filter(t -> !itemId.equals(t.item_id))
                                    .toList());

                    try {
                        plaidService.removeItem(item);
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                    }
                }
                factory.getUserDAO().save(user);
            }
        }

        // Save + kick off sync if changed
        if (modified) {
            factory.getPurchaseDAO().save(user.getAuthtoken(), next);

            // Temp code until inngest: sync newly added item ids
            for (String itemId : itemIdsToSync) {
                try {
                    plaidService.updateUserPurchasesSkipSave(itemId);
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            }
        }

        return next;
    }

    /**
     * Helper function to check for differences between a PurchasedExpense and a
     * VariableExpense preset, applying any changes to the PurchasedExpense.
     */
    private boolean applyPreset(PurchasedExpense pe, VariableExpense preset) {
        boolean changed = false;

        if (pe.getMax() != preset.getMax()) {
            pe.max = preset.getMax();
            changed = true;
        }

        if (pe.isAccount() != preset.isAccount()) {
            pe.is_account = preset.isAccount();
            changed = true;
        }

        if (pe.getId() != preset.getId()) {
            pe.id = preset.getId();
            changed = true;
        }

        if (!Objects.equals(pe.plaid_item_id, preset.plaid_item_id)) {
            pe.plaid_item_id = preset.plaid_item_id;
            changed = true;
        }

        return changed;
    }

}
