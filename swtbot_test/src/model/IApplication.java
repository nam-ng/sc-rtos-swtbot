package model;

import java.util.List;

import swtbot_test.ProjectModel;

public interface IApplication {

<<<<<<< HEAD
=======
	public abstract boolean isGccexecuted();

	public abstract boolean isCcrxexecuted();

>>>>>>> dbe78b6994fcf864c2352d408ec9096ce2c1f3b3
	public abstract List<String> getBoard();

	public abstract String getApplication();

	public abstract int getApplicationNumber();

	public abstract List<Integer> getGccExecuted();

	public abstract List<Integer> getCcrxExecuted();
<<<<<<< HEAD

	public abstract List<String> getToolchain();

	public abstract List<Integer> getStatusToolchain();
=======
>>>>>>> dbe78b6994fcf864c2352d408ec9096ce2c1f3b3
}
