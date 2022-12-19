package model;

import java.util.Arrays;
import java.util.List;

import swtbot_test.ProjectModel;
import swtbot_test.ProjectParameters;
import swtbot_test.TestUtils;

public class LowPower implements IApplication {
	private static final String APPLICATION = "lowPower";
	private static final int APPLICATION_NUMBER = 15;
	private static final boolean GCCExecuted = true;
	private static final boolean CCRXExecuted = true;
	private List<Integer> gccExecuted = Arrays.asList(1, 1, 1, 1, 1, 1, 1, 1, 1);
	private List<Integer> ccrxExecuted = Arrays.asList(1, 1, 1, 1, 1, 1, 1, 1, 1);
	private List<String> board = Arrays.asList("RSKRX65N-2MB", "CloudKitRX65N", "CK-RX65N", "Custom",
			"TargetBoardRX130", "RSKRX140", "RSKRX660", "EnvisionKitRX72N", "RSKRX671");

	public LowPower() {
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
