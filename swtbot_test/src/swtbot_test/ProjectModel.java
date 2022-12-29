package swtbot_test;

public class ProjectModel {
	private String projectName;
	private String toolchain;
	private String targetBoard;
	private String rtosType;
	private String rtosVersion;
	private int application;

<<<<<<< HEAD
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
=======
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
	
>>>>>>> dbe78b6994fcf864c2352d408ec9096ce2c1f3b3

}
