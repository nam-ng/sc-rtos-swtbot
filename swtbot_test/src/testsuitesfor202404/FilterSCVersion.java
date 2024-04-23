package testsuitesfor202404;

import static org.junit.Assert.assertFalse;

import java.awt.Robot;
import java.io.File;
import java.util.List;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.eclipse.finder.matchers.WidgetMatcherFactory;
import org.eclipse.swtbot.eclipse.finder.waits.Conditions;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotView;
import org.eclipse.swtbot.swt.finder.utils.SWTBotPreferences;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import common.Constants;
import common.LogUtil;
import model.ProjectModel;
import model.RTOSManager;
import parameters.ProjectParameters;
import parameters.ProjectParameters.ButtonAction;
import parameters.ProjectParameters.LabelName;
import parameters.ProjectParameters.MenuName;
import parameters.ProjectParameters.RTOSApplication;
import parameters.ProjectParameters.RTOSDisplay;
import parameters.ProjectParameters.RTOSType;
import parameters.ProjectParameters.RTOSVersion;
import parameters.ProjectParameters.TargetBoard;
import platform.PlatformModel;
import utilities.PGUtility;
import utilities.Utility;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class FilterSCVersion {
	private static SWTWorkbenchBot bot;
	private static Robot robot;
	private static ProjectModel projectModelSpecific1 = new ProjectModel();

	private static final String PLATFORM_XML_FILE = "xml/platformdata.xml";
	private static final String RTOS_PG_XML_FILE = "xml/rtospg.xml";
	private SWTBotView consoleView = bot.viewById("org.eclipse.ui.console.ConsoleView");

	@BeforeClass
	public static void beforeClass() throws Exception {
		bot = new SWTWorkbenchBot();
		PlatformModel.loadPlatformModel(new File(Utility.getBundlePath(LogUtil.PLUGIN_ID, PLATFORM_XML_FILE)));
		RTOSManager.loadRTOSModel(new File(Utility.getBundlePath(LogUtil.PLUGIN_ID, RTOS_PG_XML_FILE)));
		projectModelSpecific1 = PGUtility.prepareProjectModel(RTOSType.FREERTOSIOTLTS, RTOSVersion.IoTLTS_202210_1_1_3,
				RTOSApplication.IOT_LTS_BOOTLOADER, Constants.CCRX_TOOLCHAIN, TargetBoard.BOARD_CK_RX65N);
		robot = new Robot();
		Display.getDefault().syncExec(new Runnable() {

			@Override
			public void run() {
				bot.getDisplay().getActiveShell().setMaximized(true);
			}
		});
		SWTBotPreferences.TIMEOUT = 20000;
		SWTBotPreferences.PLAYBACK_DELAY = 30;
		closeWelcomePage();
	}

	private static void closeWelcomePage() {
		for (SWTBotView view : bot.views()) {
			if (view.getTitle().equals("Welcome")) {
				view.close();
			}
		}
	}

	@Test
	public void tc_00_ChangeRTOSLocationForTarget() throws Exception {
		Utility.changeModuleDownloadLocation(robot, ProjectParameters.FileLocation.TEST_FILTER_SCVERSION_TARGET,
				true);
		Utility.changeModuleDownloadLocation(robot, ProjectParameters.FileLocation.NEWEST_FIT_MODULES_LOCATION, false);
		Utility.reFocus(robot);
	}

	@Test
	public void tc_01_FilterSCVersionTarget() throws Exception {
		bot.sleep(3000);
		bot.menu(MenuName.MENU_FILE).menu(MenuName.MENU_NEW)
				.menu(MenuName.MENU_C_CPP_PROJECT).menu(MenuName.MENU_RENESAS_RX).click();
		bot.table().select(2);
		bot.button(ButtonAction.BUTTON_NEXT).click();
		bot.waitUntil(Conditions.waitForWidget(WidgetMatcherFactory.withText(LabelName.LABEL_PROJECT_NAME)), 10000);
		bot.textWithLabel(LabelName.LABEL_PROJECT_NAME).setText("testFilterSCVersion");
		bot.waitUntil(Conditions.widgetIsEnabled(bot.button(ButtonAction.BUTTON_NEXT)), 10000);
		
		bot.button(ButtonAction.BUTTON_NEXT).click();
		bot.comboBoxWithLabel(LabelName.LABEL_TOOLCHAIN_VERSION).setSelection(0);
		
		bot.comboBoxWithLabel(LabelName.LABEL_RTOS).setSelection(RTOSDisplay.FREERTOSIOTLTS);
		bot.comboBoxWithLabel(LabelName.LABEL_RTOS_VERSION).setSelection(RTOSVersion.IoTLTS_202210_1_1_3);
		
		bot.styledText().setText(TargetBoard.BOARD_CK_RX65N);
		
		bot.sleep(3000);
		Utility.reFocus(robot);
		boolean isValidateMessageContains = bot.textWithLabel("").getText().contains("FreeRTOS202210.01-LTS-rx-1.1.3 does not support current Smart Configurator V2.21.0 \n(Support: from V3.00.0)");
		boolean isButtonEnable = bot.button(ButtonAction.BUTTON_NEXT).isEnabled();
		
		bot.button(ButtonAction.BUTTON_CANCEL).click();
		if (!isValidateMessageContains || isButtonEnable) {
			assertFalse(true);
		}
	}
	
	@Test
	public void tc_02_ChangeRTOSLocationForApp() throws Exception {
		Utility.changeModuleDownloadLocation(robot, ProjectParameters.FileLocation.TEST_FILTER_SCVERSION_APP,
				true);
		Utility.changeModuleDownloadLocation(robot, ProjectParameters.FileLocation.NEWEST_FIT_MODULES_LOCATION, false);
		Utility.reFocus(robot);
	}
	
	@Test
	public void tc_03_FilterSCVersionApp() throws Exception {
		bot.sleep(3000);
		bot.menu(MenuName.MENU_FILE).menu(MenuName.MENU_NEW)
				.menu(MenuName.MENU_C_CPP_PROJECT).menu(MenuName.MENU_RENESAS_RX).click();
		bot.table().select(2);
		bot.button(ButtonAction.BUTTON_NEXT).click();
		bot.waitUntil(Conditions.waitForWidget(WidgetMatcherFactory.withText(LabelName.LABEL_PROJECT_NAME)), 10000);
		bot.textWithLabel(LabelName.LABEL_PROJECT_NAME).setText("testFilterSCVersion");
		bot.waitUntil(Conditions.widgetIsEnabled(bot.button(ButtonAction.BUTTON_NEXT)), 10000);
		
		bot.button(ButtonAction.BUTTON_NEXT).click();
		bot.comboBoxWithLabel(LabelName.LABEL_TOOLCHAIN_VERSION).setSelection(0);
		
		bot.comboBoxWithLabel(LabelName.LABEL_RTOS).setSelection(RTOSDisplay.FREERTOSIOTLTS);
		bot.comboBoxWithLabel(LabelName.LABEL_RTOS_VERSION).setSelection(RTOSVersion.IoTLTS_202210_1_1_3);
		
		bot.styledText().setText(TargetBoard.BOARD_CK_RX65N);
		bot.button(ButtonAction.BUTTON_NEXT).click();
		bot.button(ButtonAction.BUTTON_NEXT).click();
		bot.radio(0).click();
		
		boolean isValidateMessageContains = bot.textWithLabel("").getText().contains("Selected application does not support the current Smart Configurator V2.21.0\n(Support: from V3.00.0)");
		boolean isButtonEnable = bot.button(ButtonAction.BUTTON_FINISH).isEnabled();
		
		bot.button(ButtonAction.BUTTON_CANCEL).click();
		if (!isValidateMessageContains || isButtonEnable) {
			assertFalse(true);
		}
	}

	@Test
	public void tc_04_ChangeRTOSLocationForOther() throws Exception {
		Utility.changeModuleDownloadLocation(robot, ProjectParameters.FileLocation.TEST_FILTER_SCVERSION_OTHER,
				true);
		Utility.changeModuleDownloadLocation(robot, ProjectParameters.FileLocation.NEWEST_FIT_MODULES_LOCATION, false);
		Utility.reFocus(robot);
	}
	
	@Test
	public void tc_05_FilterSCVersionOther() throws Exception {
		PGUtility.createProject(RTOSType.FREERTOSIOTLTS, RTOSVersion.IoTLTS_202210_1_1_3,
				RTOSApplication.IOT_LTS_BOOTLOADER, Constants.CCRX_TOOLCHAIN, TargetBoard.BOARD_CK_RX65N);
		
		String buildType1 = projectModelSpecific1.getActiveBuildConfiguration();
		bot.tree().getTreeItem(projectModelSpecific1.getProjectName() + " [" + buildType1 + "]").expand();
		bot.tree().getTreeItem(projectModelSpecific1.getProjectName() + " [" + buildType1 + "]")
				.getNode(ProjectParameters.FolderAndFile.FOLDER_SRC).expand();
		List<String> sourceFolderItem1 = bot.tree().getTreeItem(projectModelSpecific1.getProjectName() + " [" + buildType1 + "]")
				.getNode(ProjectParameters.FolderAndFile.FOLDER_SRC).getNodes();
		
		boolean isFileEnable1 = false;
		for (String item : sourceFolderItem1) {
			if (item.contains(ProjectParameters.FolderAndFile.FILE_BOOTLOADER_H)) {
				isFileEnable1 = true;
			}
		}
		
		Utility.deleteProject(projectModelSpecific1.getProjectName(), true);
		if (bot.activeShell().getText().equals(ProjectParameters.WINDOW_SAVE_RESOURCES)) {
			bot.button(ButtonAction.BUTTON_DONT_SAVE).click();
		}
		
		if (isFileEnable1) {
			assertFalse(true);
		}
	}
	
	@Test
	public void tc_06_ChangeRTOSLocationForPassCase() throws Exception {
		Utility.changeModuleDownloadLocation(robot, ProjectParameters.FileLocation.TEST_FILTER_SCVERSION_PASS,
				true);
		Utility.changeModuleDownloadLocation(robot, ProjectParameters.FileLocation.NEWEST_FIT_MODULES_LOCATION, false);
		Utility.reFocus(robot);
	}
	
	@Test
	public void tc_07_FilterSCVersionPassCase() throws Exception {
		PGUtility.createProject(RTOSType.FREERTOSIOTLTS, RTOSVersion.IoTLTS_202210_1_1_3,
				RTOSApplication.IOT_LTS_BOOTLOADER, Constants.CCRX_TOOLCHAIN, TargetBoard.BOARD_CK_RX65N);
		
		String buildType1 = projectModelSpecific1.getActiveBuildConfiguration();
		bot.tree().getTreeItem(projectModelSpecific1.getProjectName() + " [" + buildType1 + "]").expand();
		bot.tree().getTreeItem(projectModelSpecific1.getProjectName() + " [" + buildType1 + "]")
				.getNode(ProjectParameters.FolderAndFile.FOLDER_SRC).expand();
		List<String> sourceFolderItem1 = bot.tree().getTreeItem(projectModelSpecific1.getProjectName() + " [" + buildType1 + "]")
				.getNode(ProjectParameters.FolderAndFile.FOLDER_SRC).getNodes();
		
		boolean isFileEnable1 = false;
		for (String item : sourceFolderItem1) {
			if (item.contains(ProjectParameters.FolderAndFile.FILE_BOOTLOADER_H)) {
				isFileEnable1 = true;
			}
		}
		
		Utility.deleteProject(projectModelSpecific1.getProjectName(), true);
		if (bot.activeShell().getText().equals(ProjectParameters.WINDOW_SAVE_RESOURCES)) {
			bot.button(ButtonAction.BUTTON_DONT_SAVE).click();
		}
		
		if (!isFileEnable1) {
			assertFalse(true);
		}
	}
}
