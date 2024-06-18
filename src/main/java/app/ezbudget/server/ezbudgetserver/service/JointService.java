package app.ezbudget.server.ezbudgetserver.service;

import app.ezbudget.server.ezbudgetserver.dao.DAOFactory;
import app.ezbudget.server.ezbudgetserver.model.User;

/**
 * To be inherited by all classes that will need to use user information that can be accessed 
 * by an added joint user.
 */
public class JointService {

	protected DAOFactory factory;

	public JointService(DAOFactory factory) {
		this.factory = factory;
	}

	protected User getTargetUser(User user) {
		if(user.getJointData().isJointWith())
			return this.factory.getUserDAO().getUserByAuthtoken(user.getJointData().getJointHostID());

		return user;
	}
}
