package testsuitesfor202404;

import java.awt.Robot;
import java.io.File;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
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
import parameters.ProjectParameters.RTOSDisplayImport;
import parameters.ProjectParameters.RTOSType;
import parameters.ProjectParameters.RTOSVersion;
import parameters.ProjectParameters.TargetBoard;
import platform.PlatformModel;
import utilities.PGUtility;
import utilities.Utility;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ImportGithubFeature {
	private static SWTWorkbenchBot bot;
	private static Robot robot;
	private static ProjectModel projectModelSpecific = new ProjectModel();
	private static final String PLATFORM_XML_FILE = "xml/platformdata.xml";
	private static final String RTOS_PG_XML_FILE = "xml/rtospg.xml";
	private SWTBotView consoleView = bot.viewById("org.eclipse.ui.console.ConsoleView");

	@BeforeClass
	public static void beforeClass() throws Exception {
		bot = new SWTWorkbenchBot();
		PlatformModel.loadPlatformModel(new File(Utility.getBundlePath(LogUtil.PLUGIN_ID, PLATFORM_XML_FILE)));
		RTOSManager.loadRTOSModel(new File(Utility.getBundlePath(LogUtil.PLUGIN_ID, RTOS_PG_XML_FILE)));
		projectModelSpecific = PGUtility.prepareProjectModel(RTOSType.FREERTOSIOTLTS, RTOSVersion.IoTLTS_202210_1_1_3,
				RTOSApplication.IOT_LTS_ETHER_PUBSUB, Constants.CCRX_TOOLCHAIN, TargetBoard.BOARD_CK_RX65N);
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
	public void tc_00_ChangeRTOSLocation() throws Exception{
		Utility.changeModuleDownloadLocation(robot, ProjectParameters.FileLocation.IMPORT_GITHUB_PACKAGE_LOCATION, true);
		Utility.changeModuleDownloadLocation(robot, ProjectParameters.FileLocation.NEWEST_FIT_MODULES_LOCATION, false);
		Utility.reFocus(robot);
	}

	@Test
	public void tc_01_DownloadAndImportRX() throws Exception{
		bot.sleep(1000);
		bot.menu(MenuName.MENU_FILE).menu(MenuName.MEMU_IMPORT).click();
		bot.tree().getTreeItem(ProjectParameters.FolderAndFile.FOLDER_GENERAL).expand();
		bot.tree().getTreeItem(ProjectParameters.FolderAndFile.FOLDER_GENERAL)
		.getNode(ProjectParameters.FolderAndFile.RENESAS_GITHUB).select();
		bot.button(ButtonAction.BUTTON_NEXT).click();
		bot.link("<a>Manage RTOS Versions...</a>").click();
		bot.comboBoxWithLabel(LabelName.LABEL_RTOS).setSelection(RTOSDisplayImport.RX);
		bot.button(ButtonAction.BUTTON_OK).click();
		bot.sleep(20000);
		bot.table().getTableItem(bot.table().indexOf(RTOSVersion.IoTLTS_202210_1_1_3_with_v, "Rev.")).check();
		bot.button(ButtonAction.BUTTON_DOWNLOAD).click();
		bot.button(ButtonAction.BUTTON_ACCEPT).click();
		while (bot.activeShell().getText().equals(ProjectParameters.WINDOW_PROGRESS_INFORMATION)) {
			bot.sleep(5000);	
		}
		bot.button(ButtonAction.BUTTON_BROWSE).click();
		Utility.copyLocationToClipBoard(ProjectParameters.FileLocation.EMPTY_FOLDER_FOR_IMPORT_RX);
		Utility.pressCtrlV(robot);
		
		Utility.pressEnter(robot);
		Utility.pressEnter(robot);
				
		bot.sleep(5000);
		bot.comboBoxWithLabel(LabelName.LABEL_VERSION).setSelection(RTOSVersion.IoTLTS_202210_1_1_3_with_v);
		bot.button(ButtonAction.BUTTON_NEXT).click();
		if (bot.activeShell().getText().equals(ProjectParameters.WINDOW_COPY_RESOURCES)) {
			bot.button(ButtonAction.BUTTON_YES).click();
			while (bot.activeShell().getText().equals(ProjectParameters.WINDOW_PROGRESS_INFORMATION)) {
				bot.sleep(5000);	
			}
		} else {
			bot.sleep(20000);
		}
		bot.tree().getAllItems()[0].check();
		bot.button(ButtonAction.BUTTON_FINISH).click();
		
		Utility.deleteProject("aws_ether_ck_rx65n", false);
		if (bot.activeShell().getText().equals(ProjectParameters.WINDOW_SAVE_RESOURCES)) {
			bot.button(ButtonAction.BUTTON_DONT_SAVE).click();
		}
	}
	
	@Test
	public void tc_02_DownloadAndImportRL78() throws Exception{
		bot.sleep(1000);
		bot.menu(MenuName.MENU_FILE).menu(MenuName.MEMU_IMPORT).click();
		bot.tree().getTreeItem(ProjectParameters.FolderAndFile.FOLDER_GENERAL).expand();
		bot.tree().getTreeItem(ProjectParameters.FolderAndFile.FOLDER_GENERAL)
		.getNode(ProjectParameters.FolderAndFile.RENESAS_GITHUB).select();
		bot.button(ButtonAction.BUTTON_NEXT).click();
		bot.link("<a>Manage RTOS Versions...</a>").click();
		bot.comboBoxWithLabel(LabelName.LABEL_RTOS).setSelection(RTOSDisplayImport.RL78);
		bot.button(ButtonAction.BUTTON_OK).click();
		bot.sleep(20000);
		bot.table().getTableItem(bot.table().indexOf(RTOSVersion.IoTLTS_202210_rl78_1_0_0_with_v, "Rev.")).check();
		bot.button(ButtonAction.BUTTON_DOWNLOAD).click();
		bot.button(ButtonAction.BUTTON_ACCEPT).click();
		while (bot.activeShell().getText().equals(ProjectParameters.WINDOW_PROGRESS_INFORMATION)) {
			bot.sleep(5000);	
		}
		bot.button(ButtonAction.BUTTON_BROWSE).click();
		Utility.copyLocationToClipBoard(ProjectParameters.FileLocation.EMPTY_FOLDER_FOR_IMPORT_RL78);
		Utility.pressCtrlV(robot);
		
		Utility.pressEnter(robot);
		Utility.pressEnter(robot);
				
		bot.sleep(5000);
		bot.comboBoxWithLabel(LabelName.LABEL_VERSION).setSelection(RTOSVersion.IoTLTS_202210_rl78_1_0_0_with_v);
		bot.button(ButtonAction.BUTTON_NEXT).click();
		if (bot.activeShell().getText().equals(ProjectParameters.WINDOW_COPY_RESOURCES)) {
			bot.button(ButtonAction.BUTTON_YES).click();
			while (bot.activeShell().getText().equals(ProjectParameters.WINDOW_PROGRESS_INFORMATION)) {
				bot.sleep(5000);	
			}
		} else {
			bot.sleep(20000);
		}
		bot.tree().getAllItems()[0].check();
		bot.button(ButtonAction.BUTTON_FINISH).click();
		
		Utility.deleteProject("aws_ryz024a_rl78g23-fpb", false);
		if (bot.activeShell().getText().equals(ProjectParameters.WINDOW_SAVE_RESOURCES)) {
			bot.button(ButtonAction.BUTTON_DONT_SAVE).click();
		}
	}

	@Test
	public void tc_03_DownloadAndImportLegacy() throws Exception{
		bot.sleep(1000);
		bot.menu(MenuName.MENU_FILE).menu(MenuName.MEMU_IMPORT).click();
		bot.tree().getTreeItem(ProjectParameters.FolderAndFile.FOLDER_GENERAL).expand();
		bot.tree().getTreeItem(ProjectParameters.FolderAndFile.FOLDER_GENERAL)
		.getNode(ProjectParameters.FolderAndFile.RENESAS_GITHUB).select();
		bot.button(ButtonAction.BUTTON_NEXT).click();
		bot.link("<a>Manage RTOS Versions...</a>").click();
		bot.comboBoxWithLabel(LabelName.LABEL_RTOS).setSelection(RTOSDisplayImport.LEGACY);
		bot.button(ButtonAction.BUTTON_OK).click();
		bot.sleep(20000);
		bot.table().getTableItem(bot.table().indexOf(RTOSVersion.Amazon_202107_1_0_1, "Rev.")).check();
		bot.button(ButtonAction.BUTTON_DOWNLOAD).click();
		bot.button(ButtonAction.BUTTON_ACCEPT).click();
		while (bot.activeShell().getText().equals(ProjectParameters.WINDOW_PROGRESS_INFORMATION)) {
			bot.sleep(5000);	
		}
		bot.button(ButtonAction.BUTTON_BROWSE).click();
		Utility.copyLocationToClipBoard(ProjectParameters.FileLocation.EMPTY_FOLDER_FOR_IMPORT_LEGACY);
		Utility.pressCtrlV(robot);
		
		Utility.pressEnter(robot);
		Utility.pressEnter(robot);
				
		bot.sleep(5000);
		bot.comboBoxWithLabel(LabelName.LABEL_VERSION).setSelection(RTOSVersion.Amazon_202107_1_0_1);
		bot.button(ButtonAction.BUTTON_NEXT).click();
		if (bot.activeShell().getText().equals(ProjectParameters.WINDOW_COPY_RESOURCES)) {
			bot.button(ButtonAction.BUTTON_YES).click();
			while (bot.activeShell().getText().equals(ProjectParameters.WINDOW_PROGRESS_INFORMATION)) {
				bot.sleep(5000);	
			}
		} else {
			bot.sleep(20000);
		}
		bot.tree().getAllItems()[0].check();
		bot.button(ButtonAction.BUTTON_FINISH).click();
		
		Utility.deleteProject("aws_demos", false);
		if (bot.activeShell().getText().equals(ProjectParameters.WINDOW_SAVE_RESOURCES)) {
			bot.button(ButtonAction.BUTTON_DONT_SAVE).click();
		}
	}
}
