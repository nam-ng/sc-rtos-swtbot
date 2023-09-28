package legacytestsuites;

import static org.junit.Assert.assertFalse;

import java.awt.Robot;
import java.io.File;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotEditor;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotView;
import org.eclipse.swtbot.swt.finder.utils.SWTBotPreferences;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import common.LogUtil;
import model.RTOSManager;
import parameters.ProjectParameters;
import parameters.ProjectParameters.ButtonAction;
import parameters.ProjectParameters.MenuName;
import platform.PlatformModel;
import utilities.Utility;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class DownloadMissingPackage {
	private static SWTWorkbenchBot bot;
	private static Robot robot;
	private static final String PLATFORM_XML_FILE = "xml/platformdata.xml";
	private static final String RTOS_PG_XML_FILE = "xml/rtospg.xml";
	private SWTBotView consoleView = bot.viewById("org.eclipse.ui.console.ConsoleView");

	@BeforeClass
	public static void beforeClass() throws Exception {
		bot = new SWTWorkbenchBot();
		PlatformModel.loadPlatformModel(new File(Utility.getBundlePath(LogUtil.PLUGIN_ID, PLATFORM_XML_FILE)));
		RTOSManager.loadRTOSModel(new File(Utility.getBundlePath(LogUtil.PLUGIN_ID, RTOS_PG_XML_FILE)));
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
		changeView();
	}

	private static void closeWelcomePage() {
		for (SWTBotView view : bot.views()) {
			if (view.getTitle().equals("Welcome")) {
				view.close();
			}
		}
	}

	private static void changeView() {
		bot.defaultPerspective().activate();
	}

	@Test
	public void tc_00_ChangeRTOSLocation() throws Exception {
		Utility.changeModuleDownloadLocation(robot, ProjectParameters.FileLocation.AMAZON_RTOS_LOCATION, true);
		Utility.changeModuleDownloadLocation(robot, ProjectParameters.FileLocation.FIT_MODULES_LOCATION, false);
		Utility.reFocus(robot);
	}

	@Test
	public void tc_01_DownloadMissingPackage() throws Exception {
		Utility.reFocus(robot);
		Utility.copyLocationToClipBoard(ProjectParameters.FileLocation.IOT_EXPORT);
		bot.menu(MenuName.MENU_FILE).menu(MenuName.MEMU_IMPORT).click();
		bot.tree().getTreeItem(ProjectParameters.FolderAndFile.FOLDER_GENERAL).expand();
		bot.tree().getTreeItem(ProjectParameters.FolderAndFile.FOLDER_GENERAL)
				.getNode(ProjectParameters.FolderAndFile.EXISTING_PROJECTS).select();

		bot.button(ButtonAction.BUTTON_NEXT).click();
		bot.radio(1).click();
		Utility.reFocus(robot);
		bot.button(ButtonAction.BUTTON_BROWSE,1).click();

		Utility.pressCtrlV(robot);
		Utility.pressEnter(robot);
		Utility.pressEnter(robot);

		bot.button(ButtonAction.BUTTON_FINISH).click();

		Utility.reFocus(robot);
		bot.tree().getTreeItem("iot_on_04").select();
		bot.tree().getTreeItem("iot_on_04 [HardwareDebug]").expand();
		bot.tree().getTreeItem("iot_on_04 [HardwareDebug]").getNode("iot_on_04.scfg").doubleClick();
		bot.sleep(10000);
		Utility.reFocus(robot);
		if (bot.activeShell().getText().equals(ProjectParameters.WINDOW_BOARD_DATA_FILE_UPDATE)) {
			bot.button(ButtonAction.BUTTON_YES).click();
		}
		bot.sleep(5000);
		if (bot.activeShell().getText().equals(ProjectParameters.WINDOW_OPEN_ASSOCIATED_PERSPECTIVE)) {
			bot.button(ButtonAction.BUTTON_NO).click();
		}
		SWTBotEditor scfgEditor = bot.editorByTitle("iot_on_04.scfg");
		scfgEditor.setFocus();
		bot.cTabItem(ProjectParameters.SCFG_COMPONENT_TAB).activate();

		bot.tree(1).getTreeItem(ProjectParameters.FolderAndFile.FOLDER_RTOS)
				.getNode(ProjectParameters.FolderAndFile.FOLDER_RTOS_SETTING)
				.getNode(ProjectParameters.RTOSComponent.FREERTOS_APPLICATION).select();

		String text = bot.link(0).getText();
		String value = "Software package is missing. The issue may be resolved by <a>downloading it</a> or <a>changing the RTOS location</a> to the downloaded package location.";
		
		boolean isTextContains = text.contains(value);
		Utility.copyLocationToClipBoard(ProjectParameters.FileLocation.AMAZON_OLD_RTOS_LOCATION);

		bot.link(0).click(1);
		
		bot.button("Browse...", 1).click();
		
		Utility.pressCtrlV(robot);
		
		Utility.pressEnter(robot);
		Utility.pressEnter(robot);
		bot.button(ButtonAction.APPLY_AND_CLOSE).click();
		if (bot.activeShell().getText().equals(ProjectParameters.WINDOW_PROJECT_SETTING)) {
			bot.button(ButtonAction.BUTTON_OK).click();
		}
		
		bot.tree(1).getTreeItem(ProjectParameters.FolderAndFile.FOLDER_RTOS)
		.getNode(ProjectParameters.FolderAndFile.FOLDER_RTOS_SETTING)
		.getNode(ProjectParameters.RTOSComponent.FREERTOS_APPLICATION).select();
		
		Utility.clickGenerateCode();
		
		if(!isTextContains) {
			assertFalse(true);
		}
	}
	
	@Test
	public void tc_07_DeleteAmazonProject() throws Exception {
		Utility.deleteProject("iot_on_04", true);
		if (bot.activeShell().getText().equals(ProjectParameters.WINDOW_SAVE_RESOURCES)) {
			bot.button(ButtonAction.BUTTON_DONT_SAVE).click();
		}
	}
}
