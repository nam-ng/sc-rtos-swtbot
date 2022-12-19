package model;

import java.util.Arrays;
import java.util.List;

import swtbot_test.ProjectModel;
import swtbot_test.ProjectParameters;
import swtbot_test.TestUtils;

public class IotSdkPnpEwf implements IApplication {
	private static final String APPLICATION = "iotSdkPnpEwf";
	private static final int APPLICATION_NUMBER = 7;
	private static final boolean GCCExecuted = true;
	private static final boolean CCRXExecuted = false;
	private List<Integer> gccExecuted = Arrays.asList(1);
	private List<Integer> ccrxExecuted = Arrays.asList(1);
	private List<String> board = Arrays.asList("CK-RX65N");

	public IotSdkPnpEwf() {
		super();
	}

	@Override
	public boolean isGccexecuted() {
		return GCCExecuted;
	}

	@Override
	public boolean isCcrxexecuted() {
		return CCRXExecuted;
	}

	@Override
	public List<String> getBoard() {
		return board;
	}

	@Override
	public String getApplication() {
		return APPLICATION;
	}

	@Override
	public int getApplicationNumber() {
		return APPLICATION_NUMBER;
	}

	@Override
	public List<Integer> getGccExecuted() {
		return gccExecuted;
	}

	@Override
	public List<Integer> getCcrxExecuted() {
		return ccrxExecuted;
	}

}	
	