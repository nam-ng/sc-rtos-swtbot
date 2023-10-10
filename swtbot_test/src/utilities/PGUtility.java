package utilities;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.swtbot.eclipse.finder.matchers.WidgetMatcherFactory;
import org.eclipse.swtbot.eclipse.finder.waits.Conditions;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;

import common.Constants;
import model.Application;
import model.Board;
import model.Config;
import model.GroupSetting;
import model.Language;
import model.Project;
import model.ProjectConfiguration;
import model.ProjectModel;
import model.RTOSManager;
import model.RTOSVersion;
import model.RXCLinkerFile;
import model.TC;
import model.Target;
import model.Toolchain;
import parameters.ProjectParameters;
import parameters.ProjectParameters.BuildType;
import parameters.ProjectParameters.ButtonAction;
import parameters.ProjectParameters.LabelName;
import parameters.ProjectParameters.MenuName;
import parameters.ProjectParameters.RTOSApplication;
import parameters.ProjectParameters.RTOSDisplay;
import parameters.ProjectParameters.RTOSType;
import parameters.ProjectParameters.ToolchainType;
import platform.PlatformModel;
import testcase.TCExecute;

public class PGUtility extends Utility {
	public static Map<String, String> projNames = new HashMap<>();
	static {
		projNames.put("RSKRX65N-2MB", "rsk65n");
		projNames.put("RSKRX65N-2MB(TSIP)", "rsk65ntsip");
		projNames.put("CK-RX65N", "ckrx65n");
		projNames.put("CloudKitRX65N", "ckitrx65n");
		projNames.put("RSKRX671", "rsk671");
		projNames.put("EnvisionKitRX72N", "ekrx72n");
		
		projNames.put("RSKRX65N-2MB(DUAL)", "rsk65n");
		projNames.put("RSKRX65N-2MB(TSIP)(DUAL)", "rsk65ntsip");
		projNames.put("CK-RX65N(DUAL)", "ckrx65n");
		projNames.put("CloudKitRX65N(DUAL)", "ckitrx65n");
		projNames.put("RSKRX671(DUAL)", "rsk671");
		projNames.put("EnvisionKitRX72N(DUAL)", "ekrx72n");
		
		projNames.put("TargetBoardRX130", "targetboard130");
		projNames.put("RSKRX140", "rsk140");
		projNames.put("RSKRX660", "rsk660");
		projNames.put("RSKRX66T", "rsk66t");
		projNames.put("MCB-RX26T Type A", "mcb26tA");
		projNames.put("MCB-RX26T Type B", "mcb26tB");
	}

	public static void createProject(String rtosType, String version, String appId) {
		Collection<ProjectModel> list = prepareProjectModel(rtosType, version, appId);
		// create project
		for (ProjectModel model : list) {
			internalCreateProject(model);
		}
	}

	public static void createProject(String rtosType, String version, String appId, String toolchain, String board) {
		ProjectModel model = prepareProjectModel(rtosType, version, appId, toolchain, board);
		if (model == null) {
			return;
		}
		internalCreateProject(model);
	}

	public static Collection<ProjectModel> createProjectByTC(TC tc) {
		Collection<ProjectModel> list = prepareProjectModel(tc);
		// create project
		Map<String,String> boardAndTime;
		for (ProjectModel model : list) {
			long start = System.currentTimeMillis();
			internalCreateProject(model);
			long end = System.currentTimeMillis();
			long timeExecute = end-start;
			double createTime = (double) timeExecute/1000.0;
			if (model.getToolchain().equals("CCRX")) {
				boardAndTime = TCExecute.PGTimeForCCRX.get(model.getApplication());
				if (boardAndTime == null) {
					boardAndTime = new HashMap<>();
				}
				boardAndTime.put(getProjectNameByBoard(model.getBoard()), Double.toString(createTime));
				TCExecute.PGTimeForCCRX.put(model.getApplication(), boardAndTime);
			} else if (model.getToolchain().equals("GCC")) {
				boardAndTime = TCExecute.PGTimeForGCC.get(model.getApplication());
				if (boardAndTime == null) {
					boardAndTime = new HashMap<>();
				}
				boardAndTime.put(getProjectNameByBoard(model.getBoard()), Double.toString(createTime));
				TCExecute.PGTimeForGCC.put(model.getApplication(), boardAndTime);
			}
		}
		return list;
	}
	
	public static void createProjectByAllTC(Collection<TC> tces) {
		for (TC tc : tces) {
			Collection<ProjectModel> list = prepareProjectModel(tc);
			for (ProjectModel model : list) {
				internalCreateProject(model);
			}
		}
	}

	public static Collection<ProjectModel> prepareProjectModel(String rtosType, String versionId, String appId) {
		Collection<ProjectModel> list = new ArrayList<>();
		RTOSVersion version = RTOSManager.getVersionById(rtosType, versionId);
		if (version == null) {
			return list;
		}
		Application app = RTOSManager.getApplication(rtosType, versionId, appId);
		if (app == null) {
			return list;
		}
		for (Target target : app.getTargets()) {
			for (Toolchain toolchain : target.getSupportedToolchains()) {
				int i = 0;
				for (Board board : target.getSupportedBoards()) {
					i++;
					ProjectModel model = new ProjectModel();
					model.setLanguage(getLanguage(app.getLanguages(), toolchain.getName(), board.getBoard()));
					model.setRtosType(Utility.convertRTOSTypeToDisplay(rtosType));
					model.setRtosVersion(versionId);
					model.setFamilyName(PlatformModel.getFamilyName(board.getBoard()));
					model.setBoard(board.getBoard());
					model.setApplication(appId);
					model.setApplicationOrder(app.getApplicationOrder());
					model.setToolchain(toolchain.getName());
					model.setProjectName(
							(appId + "_" + toolchain.getName() + "_" + getProjectNameByBoard(board.getBoard())).replaceAll("-", "_"));
					model.setSkipApplication(version.isSkipAppSelection());
					ProjectConfiguration filtered = getProjectConfiguration(app.getProjectConfiguration(), toolchain.getName(), board.getBoard());
					if (filtered == null) {
						model.setBuildType(BuildType.HARDWARE, true);
					} else {
						for (Config config : filtered.getConfigs()) {
							model.setBuildType(config.getId(), config.isActive());
						}
					}
					model.setRXCLinkerFile(getRXCLinkerFile(app.getLinkerFiles(), toolchain.getName(), board.getBoard()));
					list.add(model);
				}
			}
		}
		return list;
	}

	public static ProjectModel prepareProjectModel(String rtosType, String versionId, String appId, String toolchain,
			String board) {
		ProjectModel model = null;
		RTOSVersion version = RTOSManager.getVersionById(rtosType, versionId);
		if (version == null) {
			return model;
		}
		Application app = RTOSManager.getApplication(rtosType, versionId, appId, toolchain, board);
		if (app == null) {
			return model;
		}
		model = new ProjectModel();
		model.setLanguage(getLanguage(app.getLanguages(), toolchain, board));
		model.setRtosType(Utility.convertRTOSTypeToDisplay(rtosType));
		model.setRtosVersion(versionId);
		model.setFamilyName(PlatformModel.getFamilyName(board));
		model.setBoard(board);
		model.setApplication(appId);
		model.setApplicationOrder(app.getApplicationOrder());
		model.setToolchain(toolchain);
		model.setProjectName(
				(appId + "_" + toolchain + "_" + getProjectNameByBoard(board)).replaceAll("-", "_"));
		model.setSkipApplication(version.isSkipAppSelection());
		ProjectConfiguration filtered = getProjectConfiguration(app.getProjectConfiguration(), toolchain, board);
		if (filtered == null) {
			model.setBuildType(BuildType.HARDWARE, true);
		} else {
			for (Config config : filtered.getConfigs()) {
				model.setBuildType(config.getId(), config.isActive());
			}
		}
		model.setRXCLinkerFile(getRXCLinkerFile(app.getLinkerFiles(), toolchain, board));
		return model;
	}


	public static Collection<ProjectModel> prepareProjectModel(TC tc) {
		Collection<ProjectModel> results = new ArrayList<>();
		for (Project project : tc.getProjects()) {
			GroupSetting toolSetting = project.getGroupSettingById("toolchain");
			GroupSetting deviceSetting = project.getGroupSettingById("device");
			GroupSetting configSetting = project.getGroupSettingById("configuration");
			if (toolSetting == null) {
				return results;
			}
			for (Toolchain toolchain : toolSetting.getToolchains()) {
				if (deviceSetting == null) {
					continue;
				}
				for (Board board : deviceSetting.getBoards()) {
					for (Application app : project.getApps()) {
						ProjectModel model = new ProjectModel();
						if (toolSetting.getLanguage() != null) {
							model.setLanguage(toolSetting.getLanguage().getId());
						}
						model.setFamilyName(PlatformModel.getFamilyName(board.getBoard()));
						model.setToolchain(toolchain.getName());
						model.setToolchainVersion(toolchain.getVersion());
						model.setRtosType(Utility.convertRTOSTypeToDisplay(toolSetting.getRTOSType()));
						model.setRtosVersion(toolSetting.getRTOSVersion());
						model.setBoard(board.getBoard());
						if (configSetting != null && !configSetting.getConfigs().isEmpty()) {
							for (Config config : configSetting.getConfigs()) {
								model.setBuildType(config.getId(), config.isActive());
							}
						} else {
							model.setBuildType(BuildType.HARDWARE, true);
						}
						model.setProjectName((app.getApplicationId() + "_" + toolchain.getName() + "_"
								+ getProjectNameByBoard(board.getBoard())).replaceAll("-", "_"));
						model.setApplication(app.getApplicationId());
						model.setApplicationOrder(app.getApplicationOrder());
						model.setSkipApplication(app.isSkipApp());
						model.setRXCLinkerFile(
								getRXCLinkerFile(app.getLinkerFiles(), toolchain.getName(), board.getBoard()));
						results.add(model);
					}
				}
			}
		}
		return results;
	}

	private static void createRZProject(ProjectModel model) {
		// not yet implemented
	}

	private static void internalCreateProject(ProjectModel model) {
		// create project
		if (model.getFamilyName().equalsIgnoreCase(Constants.FAMILY_DEVICE_RX)) {
			createRXProject(model);
			BuildUtility.setBuildConfiguration(model);
			// extension process for IoT ADU and Bootloader project
			// currently hard-code these applications by name and order
			// will find better way for the improvement
//			if ((model.getApplication().equals(RTOSApplication.AZURE_IOT_ADU) || model.getApplicationOrder() == 16)
//					|| (model.getApplication().equals(RTOSApplication.AZURE_BOOTLOADER)
//							|| model.getApplicationOrder() == 17)) {
//				Utility.changeBoard(model, "", "", true, false);
//				if (model.getToolchain().equals(ToolchainType.GCC_TOOLCHAIN)) {
//					Utility.updateGCCLinkerScriptFile(model);
//				} else if (model.getToolchain().equals(ToolchainType.CCRX_TOOLCHAIN)
//						&& !model.getRXCLinkerFile().isEmpty()) {
//					Utility.updateRXCLinkerSection(model, model.getRXCLinkerFile());
//				}
//			}
			Utility.getProjectTreeItem(model).collapse();
			bot.closeAllEditors();
		} else if (model.getFamilyName().equalsIgnoreCase(Constants.FAMILY_DEVICE_RZ)) {
			createRZProject(model);
		}
	}

	private static void createRXProject(ProjectModel model) {
		bot.sleep(3000);
		bot.menu(MenuName.MENU_FILE).menu(MenuName.MENU_NEW)
				.menu(MenuName.MENU_C_CPP_PROJECT).menu(MenuName.MENU_RENESAS_RX).click();
		if (model.getToolchain().equals("GCC")) {
			bot.table().select(0);
		} else if (model.getToolchain().equals("CCRX")) {
			bot.table().select(2);
		}
		bot.button(ButtonAction.BUTTON_NEXT).click();
		bot.waitUntil(Conditions.waitForWidget(WidgetMatcherFactory.withText(LabelName.LABEL_PROJECT_NAME)), 10000);
		bot.textWithLabel(LabelName.LABEL_PROJECT_NAME).setText(model.getProjectName());
		bot.waitUntil(Conditions.widgetIsEnabled(bot.button(ButtonAction.BUTTON_NEXT)), 10000);

		bot.button(ButtonAction.BUTTON_NEXT).click();
		if (model.getLanguage().equals("C")) {
			bot.radio(0).click();
		} else {
			bot.radio(1).click();
		}
		if (Arrays.asList(bot.comboBoxWithLabel(LabelName.LABEL_TOOLCHAIN_VERSION).items()).contains(model.getToolchainVersion())) {
			bot.comboBoxWithLabel(LabelName.LABEL_TOOLCHAIN_VERSION).setSelection(model.getToolchainVersion());
		} else {
			bot.comboBoxWithLabel(LabelName.LABEL_TOOLCHAIN_VERSION).setSelection(0);
		}
	
		bot.comboBoxWithLabel(LabelName.LABEL_RTOS).setSelection(model.getRtosType());
		bot.comboBoxWithLabel(LabelName.LABEL_RTOS_VERSION).setSelection(model.getRtosVersion());
		if (PlatformModel.isTargetBoard(model.getBoard())) {
			bot.comboBoxWithLabel(LabelName.LABEL_TARGET_BOARD).setSelection(model.getBoard());
		}
		if (PlatformModel.isCustomBoard(model.getBoard())) {
			bot.styledText().setText(model.getBoard());
		}

		// set Configurations
		if (!model.isUseHardwareDebugConfiguration()) {
			bot.checkBox(0).deselect();
		} 
		if (model.isUseDebugConfiguration()) {
			bot.checkBox(1).click();
		} else {
			bot.checkBox(1).deselect();
		}
		if (model.isUseReleaseConfiguration()) {
			bot.checkBox(2).click();
		} else {
			bot.checkBox(2).deselect();
		}

		bot.button(ButtonAction.BUTTON_NEXT).click();
		bot.button(ButtonAction.BUTTON_NEXT).click();
		if (!model.isSkipApplication()) {
			bot.radio(model.getApplicationOrder()).click();
		}
		bot.button(ButtonAction.BUTTON_FINISH).click();
		bot.sleep(3000);

		if(model.getRtosType().equalsIgnoreCase(RTOSDisplay.AZURE) || model.getRtosType().equalsIgnoreCase(RTOSDisplay.FREERTOSIOTLTS)) {
			loopForPGAzureAndLTS();
		} else {
			loopForPGOther();
		}

	}
	
	public static void loopForPGAzureAndLTS() {
		boolean breakLoop = false;
		while (true) {
			SWTBotShell[] shells = bot.shells();
			for (SWTBotShell shell : shells) {
				if (shell.isActive()) {
					if (shell.getText().equals(ProjectParameters.WINDOW_OPEN_ASSOCIATED_PERSPECTIVE)) {
						shell.bot().button(ButtonAction.BUTTON_OPEN_PERSPECTIVE).click();
					}
					if (shell.getText().contains(ProjectParameters.CODE_GENERATING)) {
						shell.bot().button(ProjectParameters.ButtonAction.BUTTON_PROCEED).click();
					}
					if (shell.getText().equals(ProjectParameters.WINDOW_MARKETPLACE)) {
						shell.bot().button(ButtonAction.BUTTON_CANCEL).click();
						breakLoop = true;
						break;
					}
				}
			}
			bot.sleep(2000);
			if (breakLoop) {
				// close all Editors
				bot.closeAllEditors();
				if (bot.activeShell().getText().contains("Save Resource")) {
					bot.button("Don't Save").click();
				}
				bot.sleep(2000);
				break;
			}
		}
	}
	
	public static void loopForPGOther() {
		boolean breakLoop = false;
		while (true) {
			SWTBotShell[] shells = bot.shells();
			for (SWTBotShell shell : shells) {
				if (shell.isActive()) {
					if (shell.getText().contains(ProjectParameters.CODE_GENERATING)) {
						shell.bot().button(ProjectParameters.ButtonAction.BUTTON_PROCEED).click();
					}
					if (shell.getText().equals(ProjectParameters.WINDOW_OPEN_ASSOCIATED_PERSPECTIVE)) {
						shell.bot().button(ButtonAction.BUTTON_NO).click();
						breakLoop = true;
						break;
					}
				}
			}
			bot.sleep(10000);
			if (breakLoop) {
				// close all Editors
				bot.closeAllEditors();
				if (bot.activeShell().getText().contains("Save Resource")) {
					bot.button("Don't Save").click();
				}
				bot.sleep(2000);
				break;
			}
		}
	}

	private static String getLanguage(Collection<Language> model, String selectedToolchain, String selectedBoard) {
		Collection<Language> filtered = Utility.filterXMLData(model, selectedToolchain, selectedBoard);
		for (Language item : filtered) {
			return item.getId();
		}
		return "C";
	}

	private static ProjectConfiguration getProjectConfiguration(Collection<ProjectConfiguration> model,
			String selectedToolchain, String selectedBoard) {
		Collection<ProjectConfiguration> filtered = Utility.filterXMLData(model, selectedToolchain, selectedBoard);
		if (filtered.isEmpty()) {
			return null;
		}
		return filtered.stream().findFirst().get();
	}

	public static String getProjectNameByBoard(String board) {
		if (PlatformModel.isTargetBoard(board)) {
			return projNames.get(board);
		} else {
			// return project in format: <group><device_name_from_index_7>
			return PlatformModel.getGroupNameById(board) + board.substring(7);
		}
	}

	private static String getRXCLinkerFile(Collection<RXCLinkerFile> model,
			String selectedToolchain, String selectedBoard) {
		Collection<RXCLinkerFile> filtered = Utility.filterXMLData(model, selectedToolchain, selectedBoard);
		if (filtered.isEmpty()) {
			return "";
		}
		return filtered.stream().findFirst().get().getLinkerPath();
	}
}
