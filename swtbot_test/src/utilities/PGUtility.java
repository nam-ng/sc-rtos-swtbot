package utilities;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import org.eclipse.swtbot.eclipse.finder.matchers.WidgetMatcherFactory;
import org.eclipse.swtbot.eclipse.finder.waits.Conditions;

import model.Application;
import model.Board;
import model.Config;
import model.Language;
import model.ProjectConfiguration;
import model.ProjectModel;
import model.RTOSManager;
import model.RTOSVersion;
import model.Target;
import model.Toolchain;
import parameters.ProjectParameters;
import parameters.ProjectParameters.BuildType;
import parameters.ProjectParameters.ButtonAction;
import parameters.ProjectParameters.LabelName;
import parameters.ProjectParameters.MenuName;
import common.Constants;
import platform.PlatformModel;

public class PGUtility extends Utility {

	public static void createProject(String rtosType, String version, String appId) {
		Collection<ProjectModel> list = prepareProjectModel(rtosType, version, appId);
		// create project
		for (ProjectModel model : list) {
			if (model.getFamilyName().equalsIgnoreCase(Constants.FAMILY_DEVICE_RX)) {
				createRXProject(model);
				BuildUtility.setBuildConfiguration(model);
			} else if (model.getFamilyName().equalsIgnoreCase(Constants.FAMILY_DEVICE_RZ)) {
				createRZProject(model);
			}
		}
	}

	public static void createProject(String rtosType, String version, String appId, String toolchain, String board) {
		ProjectModel model = prepareProjectModel(rtosType, version, appId, toolchain, board);
		if (model == null) {
			return;
		}
		// create project
		if (model.getFamilyName().equalsIgnoreCase(Constants.FAMILY_DEVICE_RX)) {
			createRXProject(model);
			BuildUtility.setBuildConfiguration(model);
		} else if (model.getFamilyName().equalsIgnoreCase(Constants.FAMILY_DEVICE_RZ)) {
			createRZProject(model);
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
					model.setProjectName(appId + toolchain.getName() + i);
					model.setSkipApplication(version.isSkipAppSelection());
					ProjectConfiguration filtered = getProjectConfiguration(app.getProjectConfiguration(), toolchain.getName(), board.getBoard());
					if (filtered == null) {
						model.setBuildType(BuildType.HARDWARE, true);
					} else {
						for (Config config : filtered.getConfigs()) {
							model.setBuildType(config.getId(), config.isActive());
						}
					}
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
		model.setProjectName(appId + toolchain);
		model.setSkipApplication(version.isSkipAppSelection());
		ProjectConfiguration filtered = getProjectConfiguration(app.getProjectConfiguration(), toolchain, board);
		if (filtered == null) {
			model.setBuildType(BuildType.HARDWARE, true);
		} else {
			for (Config config : filtered.getConfigs()) {
				model.setBuildType(config.getId(), config.isActive());
			}
		}
		return model;
	}

	private static void createRZProject(ProjectModel model) {
		// not yet implemented
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
		bot.sleep(20000);
		while (true) {
			if (bot.activeShell().getText().contains(ProjectParameters.WINDOW_INSTALL)) {
				bot.button(ButtonAction.BUTTON_CANCEL).click();
			}
			if (bot.activeShell().getText().contains(ProjectParameters.WINDOW_OPEN_ASSOCIATED_PERSPECTIVE)) {
				bot.button(ButtonAction.BUTTON_OPEN_PERSPECTIVE).click();
			}
			if (bot.activeShell().getText().contains(ProjectParameters.WINDOW_FIT)) {
				bot.button(ButtonAction.BUTTON_YES).click();
				bot.comboBox(0).setSelection("Singapore/South &Southeast Asia/Oceania");
				bot.button(ButtonAction.BUTTON_OK).click();
				bot.sleep(5000);
				bot.button(ButtonAction.BUTTON_SELECT_ALL).click();
				bot.button(ButtonAction.BUTTON_DOWNLOAD).click();
				bot.button(ButtonAction.BUTTON_ACCEPT).click();
				bot.shell(ProjectParameters.WINDOW_OPEN_ASSOCIATED_PERSPECTIVE).activate();
			}
			if (bot.activeShell().getText().contains(ProjectParameters.CODE_GENERATING)) {
				bot.button(ButtonAction.BUTTON_PROCEED).click();
			}
			if (bot.activeShell().getText().contains(ProjectParameters.WINDOW_MARKETPLACE)) {
				bot.button(ButtonAction.BUTTON_CANCEL).click();
				break;
			}
			bot.sleep(3000);
		}

		bot.closeAllEditors();
		if (bot.activeShell().getText().contains("Save Resource")) {
			bot.button("Don't Save").click();
		}
		bot.sleep(10000);

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
}
