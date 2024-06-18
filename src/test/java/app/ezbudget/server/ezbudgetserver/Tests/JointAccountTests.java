package app.ezbudget.server.ezbudgetserver.Tests;

import app.ezbudget.server.ezbudgetserver.model.User;
import app.ezbudget.server.ezbudgetserver.service.AccountService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

public class JointAccountTests extends BaseTest {

	User user1;
	User user2;
	AccountService service;

	@BeforeEach
	void before() {
		this.setup();
	}

	@Override
	public void otherSetup() {
		service = new AccountService(factory);
		user1 = new User("user1", "1234567890", "a1b2c3d4e5", "user1@gmail.com");
		user2 = new User("user2", "1234567890", "x1y2z3", "user2@gmail.com");
		Mockito.when(userDAO.getUserByAuthtoken(user1.getAuthtoken())).thenReturn(user1);
		Mockito.when(userDAO.getUserByAuthtoken(user2.getAuthtoken())).thenReturn(user2);
		Mockito.when(userDAO.getUserByUsername(user1.getUsername())).thenReturn(user1);
		Mockito.when(userDAO.getUserByUsername(user2.getUsername())).thenReturn(user2);
	}

	@Test
	void sendRequest() {
		service.sendJointAccountHolderRequest(user1.getAuthtoken(), user2.getUsername());

		Integer responseData = (Integer) service.getSentJointAccountRequests(user1.getAuthtoken()).getData();
		HashMap<String, String> responseData2 = (HashMap<String, String>) service.getPendingJointAccountRequests(user2.getAuthtoken()).getData();

		assertEquals(1, responseData);
		assertEquals(1, responseData2.size());
	}

	@Test
	void cancelRequest() {
		service.sendJointAccountHolderRequest(user1.getAuthtoken(), user2.getUsername());

		Integer responseData = (Integer) service.getSentJointAccountRequests(user1.getAuthtoken()).getData();
		HashMap<String, String> responseData2 = (HashMap<String, String>) service.getPendingJointAccountRequests(user2.getAuthtoken()).getData();

		assertEquals(1, responseData);
		assertEquals(1, responseData2.size());

		service.cancelSentJointAccountRequests(user1.getAuthtoken());

		Integer responseData3 = (Integer) service.getSentJointAccountRequests(user1.getAuthtoken()).getData();
		HashMap<String, String> responseData4 = (HashMap<String, String>) service.getPendingJointAccountRequests(user2.getAuthtoken()).getData();

		assertEquals(0, responseData3);
		assertEquals(0, responseData4.size());
	}

	@Test
	void acceptRequest() {
		service.sendJointAccountHolderRequest(user1.getAuthtoken(), user2.getUsername());

		Integer responseData = (Integer) service.getSentJointAccountRequests(user1.getAuthtoken()).getData();
		HashMap<String, String> responseData2 = (HashMap<String, String>) service.getPendingJointAccountRequests(user2.getAuthtoken()).getData();

		assertEquals(1, responseData);
		assertEquals(1, responseData2.size());

		service.acceptJointAccountHolderRequest(user2.getAuthtoken(), user1.getAuthtoken());

		Integer responseData3 = (Integer) service.getSentJointAccountRequests(user1.getAuthtoken()).getData();
		HashMap<String, String> responseData4 = (HashMap<String, String>) service.getPendingJointAccountRequests(user2.getAuthtoken()).getData();

		assertEquals(0, responseData3);
		assertEquals(0, responseData4.size());
	}

	@Test
	void rejectRequest() {
		service.sendJointAccountHolderRequest(user1.getAuthtoken(), user2.getUsername());

		Integer responseData = (Integer) service.getSentJointAccountRequests(user1.getAuthtoken()).getData();
		HashMap<String, String> responseData2 = (HashMap<String, String>) service.getPendingJointAccountRequests(user2.getAuthtoken()).getData();

		assertEquals(1, responseData);
		assertEquals(1, responseData2.size());

		service.rejectJointAccountHolderRequest(user2.getAuthtoken(), user1.getAuthtoken());

		Integer responseData3 = (Integer) service.getSentJointAccountRequests(user1.getAuthtoken()).getData();
		HashMap<String, String> responseData4 = (HashMap<String, String>) service.getPendingJointAccountRequests(user2.getAuthtoken()).getData();

		assertEquals(0, responseData3);
		assertEquals(0, responseData4.size());
	}

	@Test
	void checkCurrentSharers() {
		service.sendJointAccountHolderRequest(user1.getAuthtoken(), user2.getUsername());

		Integer responseData = (Integer) service.getSentJointAccountRequests(user1.getAuthtoken()).getData();
		HashMap<String, String> responseData2 = (HashMap<String, String>) service.getPendingJointAccountRequests(user2.getAuthtoken()).getData();

		assertEquals(1, responseData);
		assertEquals(1, responseData2.size());

		service.acceptJointAccountHolderRequest(user2.getAuthtoken(), user1.getAuthtoken());

		HashMap<String, String> responseData3 = (HashMap<String, String>) service.getJointAccountHolders(user2.getAuthtoken()).getData();

		assertEquals(1, responseData3.size());
		assertEquals(user1.getUsername(), responseData3.get(user1.getAuthtoken()));
		assertEquals(user2.getAuthtoken(), user1.getJointData().getJointHostID());
	}

	@Test
	void checkNotJoint() {
		service.sendJointAccountHolderRequest(user1.getAuthtoken(), user2.getUsername());

		Integer responseData = (Integer) service.getSentJointAccountRequests(user1.getAuthtoken()).getData();
		HashMap<String, String> responseData2 = (HashMap<String, String>) service.getPendingJointAccountRequests(user2.getAuthtoken()).getData();

		assertEquals(1, responseData);
		assertEquals(1, responseData2.size());

		service.acceptJointAccountHolderRequest(user2.getAuthtoken(), user1.getAuthtoken());

		HashMap<String, String> responseData3 = (HashMap<String, String>) service.getJointAccountHolders(user2.getAuthtoken()).getData();

		assertEquals(1, responseData3.size());
		assertEquals(user1.getUsername(), responseData3.get(user1.getAuthtoken()));
		assertEquals(user2.getAuthtoken(), user1.getJointData().getJointHostID());

		service.removeJointAccountHolder_JointPerspective(user1.getAuthtoken());

		HashMap<String, String> responseData4 = (HashMap<String, String>) service.getJointAccountHolders(user2.getAuthtoken()).getData();

		assertNull(user1.getJointData().getJointHostID());
		assertEquals(0, responseData4.size());
	}

	@Test
	void checkRemoveByHost() {
		service.sendJointAccountHolderRequest(user1.getAuthtoken(), user2.getUsername());

		Integer responseData = (Integer) service.getSentJointAccountRequests(user1.getAuthtoken()).getData();
		HashMap<String, String> responseData2 = (HashMap<String, String>) service.getPendingJointAccountRequests(user2.getAuthtoken()).getData();

		assertEquals(1, responseData);
		assertEquals(1, responseData2.size());

		service.acceptJointAccountHolderRequest(user2.getAuthtoken(), user1.getAuthtoken());

		HashMap<String, String> responseData3 = (HashMap<String, String>) service.getJointAccountHolders(user2.getAuthtoken()).getData();

		assertEquals(1, responseData3.size());
		assertEquals(user1.getUsername(), responseData3.get(user1.getAuthtoken()));
		assertEquals(user2.getAuthtoken(), user1.getJointData().getJointHostID());

		service.removeJointAccountHolder_HostPerspective(user2.getAuthtoken(), user1.getAuthtoken());

		HashMap<String, String> responseData4 = (HashMap<String, String>) service.getJointAccountHolders(user2.getAuthtoken()).getData();

		assertNull(user1.getJointData().getJointHostID());
		assertEquals(0, responseData4.size());
	}
}
