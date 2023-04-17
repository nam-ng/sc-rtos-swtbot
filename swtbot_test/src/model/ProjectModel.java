package model;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import parameters.ProjectParameters.BuildType;

public class ProjectModel {
	private String language;
	private String familyName;
	private String projectName;
	private String toolchain;
	private String toolchainVersion;
	private String board;
	private String rtosType;
	private String rtosVersion;
	private String rtosApplication;
	private int applicationOrder;
	private boolean skipApplication = true;
	private Map<String, Boolean> buildType = new HashMap<>();
	private String rxcLinkerFile;

	public ProjectModel() {
		// do nothing
	}

	public Map<String, Boolean> getBuildType() {
		return buildType;
	}

	public boolean isUseHardwareDebugConfiguration() {
		if (buildType.isEmpty()) {
			return true;
		}
		for (Entry<String, Boolean> entry : buildType.entrySet()) {
			if (entry.getKey().equalsIgnoreCase(BuildType.HARDWARE)) {
				return true;
			}
		}
		return false;
	}

	public boolean isUseDebugConfiguration() {
		if (buildType.isEmpty()) {
			return false;
		}
		for (Entry<String, Boolean> entry : buildType.entrySet()) {
			if (entry.getKey().equalsIgnoreCase(BuildType.DEBUG)) {
				return true;
			}
		}
		return false;
	}

	public boolean isUseReleaseConfiguration() {
		if (buildType.isEmpty()) {
			return false;
		}
		for (Entry<String, Boolean> entry : buildType.entrySet()) {
			if (entry.getKey().equalsIgnoreCase(BuildType.RELEASE)) {
				return true;
			}
		}
		return false;
	}

	public String getActiveBuildConfiguration() {
		if (buildType.isEmpty()) {
			return BuildType.HARDWARE;
		}
		for (Entry<String, Boolean> entry : buildType.entrySet()) {
			if (Boolean.TRUE.equals(entry.getValue())) {
				return entry.getKey();
			}
		}
		return BuildType.HARDWARE;
	}

	public void setBuildType(String buidType, boolean isActive) {
		buildType.put(buidType, isActive);
	}

	public void setFamilyName(String name) {
		familyName = name;
	}

	public String getFamilyName() {
		return familyName;
	}

	public String getProjectName() {
		return projectName;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	public String getBoard() {
		return board;
	}

	public void setBoard(String board) {
		this.board = board;
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

	public void setToolchain(String toolchain) {
		this.toolchain = toolchain;
	}

	public String getToolchainVersion() {
		return toolchainVersion;
	}

	public void setToolchainVersion(String version) {
		toolchainVersion = version;
	}
	public String getApplication() {
		return rtosApplication;
	}

	public void setApplication(String application) {
		this.rtosApplication = application;
	}

	public int getApplicationOrder() {
		return applicationOrder;
	}

	public void setApplicationOrder(int order) {
		applicationOrder = order;
	}


	public void setSkipApplication(boolean skipApp) {
		skipApplication = skipApp;
	}

	public boolean isSkipApplication() {
		return skipApplication;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public void setRXCLinkerFile(String file) {
		rxcLinkerFile = file;
	}

	public String getRXCLinkerFile() {
		return rxcLinkerFile;
	}
}
