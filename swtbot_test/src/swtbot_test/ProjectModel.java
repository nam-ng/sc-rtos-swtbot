package swtbot_test;

public class ProjectModel {
	private String projectName;
	private String toolchain;
	private String targetBoard;
	private String rtosType;
	private String rtosVersion;
	private int application;

	public String getProjectName() {
		return projectName;
	}
	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}
	public String getTargetBoard() {
		return targetBoard;
	}
	public void setTargetBoard(String targetBoard) {
		this.targetBoard = targetBoard;
	}
	public String getRtosType() {
		return rtosType;
	}
	public void setRtosType(String rtosType) {
		this.rtosType = rtosType;
	}
	public String getRtosVersion() {
		return rtosVersion;
	}
	public void setRtosVersion(String rtosVersion) {
		this.rtosVersion = rtosVersion;
	}
	
	public String getToolchain() {
		return toolchain;
	}
	public int getApplication() {
		return application;
	}
	public void setApplication(int application) {
		this.application = application;
	}
	public void setToolchain(String toolchain) {
		this.toolchain = toolchain;
	}
	public ProjectModel() {
		super();
	}
	

}
