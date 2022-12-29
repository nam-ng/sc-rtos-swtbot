package model;

import java.util.Arrays;
import java.util.List;

import swtbot_test.ProjectModel;
import swtbot_test.ProjectParameters;
import swtbot_test.TestUtils;

public class IoTSdkEwf implements IApplication {
	private static final String APPLICATION = "iotSdkEwf";
	private static final int APPLICATION_NUMBER = 5;
<<<<<<< HEAD
	private List<String> toolchain = Arrays.asList("GCC", "CCRX");
	private List<Integer> statusToolchain = Arrays.asList(1,0);
=======
	private static final boolean GCCExecuted = true;
	private static final boolean CCRXExecuted = false;
>>>>>>> dbe78b6994fcf864c2352d408ec9096ce2c1f3b3
	private List<Integer> gccExecuted = Arrays.asList(1);
	private List<Integer> ccrxExecuted = Arrays.asList(1);
	private List<String> board = Arrays.asList("CK-RX65N");

	public IoTSdkEwf() {
		super();
	}

	@Override
<<<<<<< HEAD
	public List<String> getToolchain() {
		return toolchain;
	}
	
	@Override
	public List<Integer> getStatusToolchain() {
		return statusToolchain;
=======
	public boolean isGccexecuted() {
		return GCCExecuted;
	}

	@Override
	public boolean isCcrxexecuted() {
		return CCRXExecuted;
>>>>>>> dbe78b6994fcf864c2352d408ec9096ce2c1f3b3
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
	