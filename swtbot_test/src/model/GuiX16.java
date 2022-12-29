package model;

import java.util.Arrays;
import java.util.List;

import swtbot_test.ProjectModel;
import swtbot_test.ProjectParameters;
import swtbot_test.TestUtils;

public class GuiX16 implements IApplication {
	private static final String APPLICATION = "guix16pp";
	private static final int APPLICATION_NUMBER = 11;
	private List<String> toolchain = Arrays.asList("GCC", "CCRX");
	private List<Integer> statusToolchain = Arrays.asList(1,1);
	private List<Integer> gccExecuted = Arrays.asList(1, 1);
	private List<Integer> ccrxExecuted = Arrays.asList(1, 1);
	private List<String> board = Arrays.asList("RSKRX65N-2MB", "EnvisionKitRX72N");

	public GuiX16() {
		super();
	}
	
	@Override
	public List<String> getToolchain() {
		return toolchain;
	}
	
	@Override
	public List<Integer> getStatusToolchain() {
		return statusToolchain;
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
	