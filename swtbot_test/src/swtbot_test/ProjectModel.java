package swtbot_test;

public class ProjectModel {
	private String projectName;
	private String toolchain;
	private String targetBoard;
	private String rtosType;
	private String rtosVersion;
	private int application;

	protected String getProjectName() {
		return projectName;
	}

	protected void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	protected String getTargetBoard() {
		return targetBoard;
	}

	protected void setTargetBoard(String targetBoard) {
		this.targetBoard = targetBoard;
	}

	protected String getRtosType() {
		return rtosType;
	}

	protected void setRtosType(String rtosType) {
		this.rtosType = rtosType;
	}

	protected String getRtosVersion() {
		return rtosVersion;
	}

	protected void setRtosVersion(String rtosVersion) {
		this.rtosVersion = rtosVersion;
	}

	protected String getToolchain() {
		return toolchain;
	}

	protected int getApplication() {
		return application;
	}

	protected void setApplication(int application) {
		this.application = application;
	}

	protected void setToolchain(String toolchain) {
		this.toolchain = toolchain;
	}

	protected ProjectModel() {
		super();
	}
}
