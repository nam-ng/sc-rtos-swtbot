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
public class FilterPartNumber {
	private static SWTWorkbenchBot bot;
	private static Robot robot;
	private static ProjectModel projectModelSpecific1 = new ProjectModel();
	private static ProjectModel projectModelSpecific2 = new ProjectModel();
	private static ProjectModel projectModelSpecific3 = new ProjectModel();

	private static final String PLATFORM_XML_FILE = "xml/platformdata.xml";
	private static final String RTOS_PG_XML_FILE = "xml/rtospg.xml";
	private SWTBotView consoleView = bot.viewById("org.eclipse.ui.console.ConsoleView");

	@BeforeClass
	public static void beforeClass() throws Exception {
		bot = new SWTWorkbenchBot();
		PlatformModel.loadPlatformModel(new File(Utility.getBundlePath(LogUtil.PLUGIN_ID, PLATFORM_XML_FILE)));
		RTOSManager.loadRTOSModel(new File(Utility.getBundlePath(LogUtil.PLUGIN_ID, RTOS_PG_XML_FILE)));
		projectModelSpecific1 = PGUtility.prepareProjectModel(RTOSType.FREERTOSIOTLTS, RTOSVersion.IoTLTS_202210_1_1_3,
				RTOSApplication.IOT_LTS_BOOTLOADER, Constants.CCRX_TOOLCHAIN, TargetBoard.DEVICE_R5F572NDDxBG);
		projectModelSpecific2 = PGUtility.prepareProjectModel(RTOSType.FREERTOSIOTLTS, RTOSVersion.IoTLTS_202210_1_1_3,
				RTOSApplication.IOT_LTS_BOOTLOADER, Constants.CCRX_TOOLCHAIN, TargetBoard.DEVICE_R5F572NDDxBD);
		projectModelSpecific3 = PGUtility.prepareProjectModel(RTOSType.FREERTOSIOTLTS, RTOSVersion.IoTLTS_202210_1_1_3,
				RTOSApplication.IOT_LTS_BOOTLOADER, Constants.CCRX_TOOLCHAIN, TargetBoard.DEVICE_R5F565NCDxBG);
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
		Utility.changeModuleDownloadLocation(robot, ProjectParameters.FileLocation.TEST_FILTER_PARTNUMBER_TARGET,
				true);
		Utility.changeModuleDownloadLocation(robot, ProjectParameters.FileLocation.NEWEST_FIT_MODULES_LOCATION, false);
		Utility.reFocus(robot);
	}
	
	@Test
	public void tc_01_TestValidateForTarget() throws Exception {
		bot.sleep(3000);
		bot.menu(MenuName.MENU_FILE).menu(MenuName.MENU_NEW)
				.menu(MenuName.MENU_C_CPP_PROJECT).menu(MenuName.MENU_RENESAS_RX).click();
		bot.table().select(2);
		bot.button(ButtonAction.BUTTON_NEXT).click();
		bot.waitUntil(Conditions.waitForWidget(WidgetMatcherFactory.withText(LabelName.LABEL_PROJECT_NAME)), 10000);
		bot.textWithLabel(LabelName.LABEL_PROJECT_NAME).setText("testFilterPartNumber");
		bot.waitUntil(Conditions.widgetIsEnabled(bot.button(ButtonAction.BUTTON_NEXT)), 10000);
		
		bot.button(ButtonAction.BUTTON_NEXT).click();
		bot.comboBoxWithLabel(LabelName.LABEL_TOOLCHAIN_VERSION).setSelection(0);
		
		bot.comboBoxWithLabel(LabelName.LABEL_RTOS).setSelection(RTOSDisplay.FREERTOSIOTLTS);
		bot.comboBoxWithLabel(LabelName.LABEL_RTOS_VERSION).setSelection(RTOSVersion.IoTLTS_202210_1_1_3);
		
		bot.styledText().setText(TargetBoard.DEVICE_R5F572NDDxBG);
		bot.sleep(3000);
		Utility.reFocus(robot);
		boolean isValidateMessageEmpty = bot.textWithLabel("").getText().contains("Please configure your board related settings and put them into your code. (pins, clock settings, etc)");
		boolean isButtonEnable1 = bot.button(ButtonAction.BUTTON_NEXT).isEnabled();
		
		bot.styledText().setText(TargetBoard.DEVICE_R5F572NDDxBD);
		bot.sleep(3000);
		Utility.reFocus(robot);
		boolean isValidateMessageContains = bot.textWithLabel("").getText().contains("FreeRTOS202210.01-LTS-rx-1.1.3 does not support selected device \n(Support: Devices satisfied expression [R5F572N[DN][DH]x((BG)|(FC))(_DUAL)?])");
		boolean isButtonEnable2 = bot.button(ButtonAction.BUTTON_NEXT).isEnabled();
		
		bot.styledText().setText(TargetBoard.DEVICE_R5F565NCDxBG);
		bot.sleep(3000);
		Utility.reFocus(robot);
		boolean isValidateMessageEmpty2 = bot.textWithLabel("").getText().contains("Please configure your board related settings and put them into your code. (pins, clock settings, etc)");
		boolean isButtonEnable3 = bot.button(ButtonAction.BUTTON_NEXT).isEnabled();
		
		bot.button(ButtonAction.BUTTON_CANCEL).click();
		if (!isValidateMessageEmpty || !isButtonEnable1 || !isValidateMessageContains || isButtonEnable2 || !isValidateMessageEmpty2 || !isButtonEnable3) {
			assertFalse(true);
		}

	}
	
	@Test
	public void tc_02_ChangeRTOSLocationForApp() throws Exception {
		Utility.changeModuleDownloadLocation(robot, ProjectParameters.FileLocation.TEST_FILTER_PARTNUMBER_APP,
				true);
		Utility.changeModuleDownloadLocation(robot, ProjectParameters.FileLocation.NEWEST_FIT_MODULES_LOCATION, false);
		Utility.reFocus(robot);
	}

	@Test
	public void tc_03_TestValidateForApp() throws Exception {
		bot.sleep(3000);
		bot.menu(MenuName.MENU_FILE).menu(MenuName.MENU_NEW)
				.menu(MenuName.MENU_C_CPP_PROJECT).menu(MenuName.MENU_RENESAS_RX).click();
		bot.table().select(2);
		bot.button(ButtonAction.BUTTON_NEXT).click();
		bot.waitUntil(Conditions.waitForWidget(WidgetMatcherFactory.withText(LabelName.LABEL_PROJECT_NAME)), 10000);
		bot.textWithLabel(LabelName.LABEL_PROJECT_NAME).setText("testFilterPartNumber");
		bot.waitUntil(Conditions.widgetIsEnabled(bot.button(ButtonAction.BUTTON_NEXT)), 10000);
		
		bot.button(ButtonAction.BUTTON_NEXT).click();
		bot.comboBoxWithLabel(LabelName.LABEL_TOOLCHAIN_VERSION).setSelection(0);
		
		bot.comboBoxWithLabel(LabelName.LABEL_RTOS).setSelection(RTOSDisplay.FREERTOSIOTLTS);
		bot.comboBoxWithLabel(LabelName.LABEL_RTOS_VERSION).setSelection(RTOSVersion.IoTLTS_202210_1_1_3);
		
		bot.styledText().setText(TargetBoard.DEVICE_R5F572NDDxBG);
		bot.button(ButtonAction.BUTTON_NEXT).click();
		bot.button(ButtonAction.BUTTON_NEXT).click();
		bot.radio(0).click();
		
		boolean isValidateMessageEmpty = bot.textWithLabel("").getText().contains("Select RTOS Project Settings");
		boolean isButtonEnable1 = bot.button(ButtonAction.BUTTON_FINISH).isEnabled();
		
		bot.button(ButtonAction.BUTTON_BACK).click();
		bot.button(ButtonAction.BUTTON_BACK).click();
		
		bot.styledText().setText(TargetBoard.DEVICE_R5F572NDDxBD);
		bot.button(ButtonAction.BUTTON_NEXT).click();
		bot.button(ButtonAction.BUTTON_NEXT).click();
		bot.radio(0).click();
		boolean isValidateMessageContains = bot.textWithLabel("").getText().contains("Selected Application does not support target device: R5F572NDDxBD \n(Support: Devices satisfied expression [R5F572N[DN][DH]x((BG)|(FC))(_DUAL)?])");
		boolean isButtonEnable2 = bot.button(ButtonAction.BUTTON_FINISH).isEnabled();
		
		bot.button(ButtonAction.BUTTON_BACK).click();
		bot.button(ButtonAction.BUTTON_BACK).click();
		
		bot.styledText().setText(TargetBoard.DEVICE_R5F565NCDxBG);
		bot.button(ButtonAction.BUTTON_NEXT).click();
		bot.button(ButtonAction.BUTTON_NEXT).click();
		boolean isValidateMessageContains2 = bot.textWithLabel("").getText().contains("Selected Application does not support target device: R5F565NCDxBG \n(Support: Devices satisfied expression [R5F572N[DN][DH]x((BG)|(FC))(_DUAL)?])");
		boolean isButtonEnable3 = bot.button(ButtonAction.BUTTON_FINISH).isEnabled();
		
		bot.button(ButtonAction.BUTTON_CANCEL).click();
		if (!isValidateMessageEmpty || !isButtonEnable1 || !isValidateMessageContains || isButtonEnable2 || !isValidateMessageContains2 || isButtonEnable3) {
			assertFalse(true);
		}
	}
	
	@Test
	public void tc_04_ChangeRTOSLocationForOther() throws Exception {
		Utility.changeModuleDownloadLocation(robot, ProjectParameters.FileLocation.TEST_FILTER_PARTNUMBER_OTHER,
				true);
		Utility.changeModuleDownloadLocation(robot, ProjectParameters.FileLocation.NEWEST_FIT_MODULES_LOCATION, false);
		Utility.reFocus(robot);
	}

	@Test
	public void tc_05_TestResourceCopyFilterPartNumber() throws Exception{
		PGUtility.createProject(RTOSType.FREERTOSIOTLTS, RTOSVersion.IoTLTS_202210_1_1_3,
				RTOSApplication.IOT_LTS_BOOTLOADER, Constants.CCRX_TOOLCHAIN, TargetBoard.DEVICE_R5F572NDDxBG);
		
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
		
		PGUtility.createProject(RTOSType.FREERTOSIOTLTS, RTOSVersion.IoTLTS_202210_1_1_3,
				RTOSApplication.IOT_LTS_BOOTLOADER, Constants.CCRX_TOOLCHAIN, TargetBoard.DEVICE_R5F572NDDxBD);
		
		String buildType2 = projectModelSpecific2.getActiveBuildConfiguration();
		bot.tree().getTreeItem(projectModelSpecific2.getProjectName() + " [" + buildType2 + "]").expand();
		bot.tree().getTreeItem(projectModelSpecific2.getProjectName() + " [" + buildType2 + "]")
				.getNode(ProjectParameters.FolderAndFile.FOLDER_SRC).expand();
		List<String> sourceFolderItem2 = bot.tree().getTreeItem(projectModelSpecific2.getProjectName() + " [" + buildType1 + "]")
				.getNode(ProjectParameters.FolderAndFile.FOLDER_SRC).getNodes();
		
		boolean isFileEnable2 = false;
		for (String item : sourceFolderItem2) {
			if (item.contains(ProjectParameters.FolderAndFile.FILE_BOOTLOADER_H)) {
				isFileEnable2 = true;
			}
		}
		
		Utility.deleteProject(projectModelSpecific2.getProjectName(), true);
		if (bot.activeShell().getText().equals(ProjectParameters.WINDOW_SAVE_RESOURCES)) {
			bot.button(ButtonAction.BUTTON_DONT_SAVE).click();
		}
		
		PGUtility.createProject(RTOSType.FREERTOSIOTLTS, RTOSVersion.IoTLTS_202210_1_1_3,
				RTOSApplication.IOT_LTS_BOOTLOADER, Constants.CCRX_TOOLCHAIN, TargetBoard.DEVICE_R5F565NCDxBG);
		
		String buildType3 = projectModelSpecific3.getActiveBuildConfiguration();
		bot.tree().getTreeItem(projectModelSpecific3.getProjectName() + " [" + buildType3 + "]").expand();
		bot.tree().getTreeItem(projectModelSpecific3.getProjectName() + " [" + buildType3 + "]")
				.getNode(ProjectParameters.FolderAndFile.FOLDER_SRC).expand();
		List<String> sourceFolderItem3 = bot.tree().getTreeItem(projectModelSpecific3.getProjectName() + " [" + buildType1 + "]")
				.getNode(ProjectParameters.FolderAndFile.FOLDER_SRC).getNodes();
		
		boolean isFileEnable3 = false;
		for (String item : sourceFolderItem3) {
			if (item.contains(ProjectParameters.FolderAndFile.FILE_BOOTLOADER_H)) {
				isFileEnable3 = true;
			}
		}
		
		Utility.deleteProject(projectModelSpecific3.getProjectName(), true);
		if (bot.activeShell().getText().equals(ProjectParameters.WINDOW_SAVE_RESOURCES)) {
			bot.button(ButtonAction.BUTTON_DONT_SAVE).click();
		}
		
		if (!isFileEnable1 || isFileEnable2 || isFileEnable3) {
			assertFalse(true);
		}
	}
}
