package app.ezbudget.server.ezbudgetserver.model;

import java.util.HashMap;

public class JointAccountData {

	/*
	 * {
	 * 		"authtoken": "username"
	 * }
	 */
	public HashMap<String, String> allowedJointUsers;
	public String jointWithID;
	public HashMap<String, String> pendingJointRequests;
	public HashMap<String, String> sentJointRequests;

	public JointAccountData() {
		this.allowedJointUsers = new HashMap<>();
		this.pendingJointRequests = new HashMap<>();
		this.sentJointRequests = new HashMap<>();
	}

	public HashMap<String, String> getAllowedJointUsers() { return allowedJointUsers; }

	/**
	 * Check if the user has a joint user allowed on their account.
	 * 
	 * @return True if a joint user has been added to their account
	 */
	public boolean isJointWith() { return jointWithID != null; }

	public String getJointHostID() { return jointWithID; }

	public HashMap<String, String> getJointAccountRequests() { return pendingJointRequests; }
	public HashMap<String, String> getSentJointRequests() { return sentJointRequests; }

	public void setJointHostID(String ID) { this.jointWithID = ID; }
}
