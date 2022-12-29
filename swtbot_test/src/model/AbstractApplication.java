package model;

import java.util.List;

public abstract class AbstractApplication {

	protected String application;
	protected int applicationNumber;
	protected List<String> toolchain;
	protected List<Integer> statusToolchain;

	protected List<Integer> gccExecuted;
	protected List<Integer> ccrxExecuted;
	protected List<String> board;

	public List<String> getToolchain() {
		return toolchain;
	}

	public List<Integer> getStatusToolchain() {
		return statusToolchain;
	}

	public List<String> getBoard() {
		return board;
	}

	public String getApplication() {
		return application;
	}

	public int getApplicationNumber() {
		return applicationNumber;
	}

	public List<Integer> getGccExecuted() {
		return gccExecuted;
	}

	public List<Integer> getCcrxExecuted() {
		return ccrxExecuted;
	}
}
