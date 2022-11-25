package model;

import swtbot_test.ProjectModel;

public abstract class AbstractApplication {

	public AbstractApplication() {
		super();
		// TODO Auto-generated constructor stub
	}
	public abstract void gccExecuted(ProjectModel projectModel);
	public abstract void ccrxExecuted(ProjectModel projectModel);
}
