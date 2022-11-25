package model;

import java.util.Arrays;
import java.util.List;

import swtbot_test.ProjectModel;
import swtbot_test.ProjectParameters;
import swtbot_test.TestUtils;

public class LowPower extends AbstractApplication {
	private static final String APPLICATION = "lowPower";
	private static final int APPLICATION_NUMBER = 11;
	private static final boolean GCCExecuted = true;
	private static final boolean CCRXExecuted = true;
	private List<Integer> gccExecuted = Arrays.asList(1,1,1,1,1,1,1,1,1);
	private List<Integer> ccrxExecuted = Arrays.asList(1,1,1,1,1,1,1,1,1);
	private List<String> board = Arrays.asList("RSKRX65N-2MB","CloudKitRX65N","CK-RX65N","Custom","TargetBoardRX130", "RSKRX140",
            "RSKRX660", "EnvisionKitRX72N", "RSKRX671");
	
	public LowPower() {
		super();
	}

	public static boolean isGccexecuted() {
		return GCCExecuted;
	}

	public static boolean isCcrxexecuted() {
		return CCRXExecuted;
	}

	@Override
	public void gccExecuted(ProjectModel projectModel) {
		// TODO Auto-generated method stub
		for (int i = 0; i < board.size(); i++) {
			if (gccExecuted.get(i) == 1) {
				projectModel.setRtosType(ProjectParameters.RTOSType.AZURE);
				projectModel.setRtosVersion(ProjectParameters.RTOSVersion.NEWEST);
				projectModel.setTargetBoard(board.get(i));
				projectModel.setApplication(APPLICATION_NUMBER);
				projectModel.setToolchain("GCC");
				projectModel.setProjectName(APPLICATION+"GCC" + i);
				TestUtils.createProject(projectModel);
				TestUtils.buildProject(projectModel);
			}
		}
	}
	@Override
	public void ccrxExecuted(ProjectModel projectModel) {
		// TODO Auto-generated method stub
		for (int j = 0; j < board.size(); j++) {
			if (ccrxExecuted.get(j) == 1) {
				projectModel.setRtosType(ProjectParameters.RTOSType.AZURE);
				projectModel.setRtosVersion(ProjectParameters.RTOSVersion.NEWEST);
				projectModel.setTargetBoard(board.get(j));
				projectModel.setApplication(APPLICATION_NUMBER);
				projectModel.setToolchain("CCRX");
				projectModel.setProjectName(APPLICATION+"CCRX" + j);
				TestUtils.createProject(projectModel);
				TestUtils.buildProject(projectModel);
			}
		}
	}
}
