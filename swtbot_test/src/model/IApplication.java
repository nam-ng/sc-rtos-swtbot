package model;

import java.util.List;

import swtbot_test.ProjectModel;

public interface IApplication {

	public abstract List<String> getBoard();

	public abstract String getApplication();

	public abstract int getApplicationNumber();

	public abstract List<Integer> getGccExecuted();

	public abstract List<Integer> getCcrxExecuted();

	public abstract List<String> getToolchain();

	public abstract List<Integer> getStatusToolchain();

}
