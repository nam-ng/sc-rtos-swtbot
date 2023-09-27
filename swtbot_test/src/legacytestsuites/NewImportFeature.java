package legacytestsuites;

import static org.junit.Assert.assertFalse;

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

import common.LogUtil;
import model.RTOSManager;
import parameters.ProjectParameters;
import parameters.ProjectParameters.ButtonAction;
import parameters.ProjectParameters.MenuName;
import platform.PlatformModel;
import utilities.Utility;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class NewImportFeature {
	private static SWTWorkbenchBot bot;
	private static Robot robot;
	private static final String PLATFORM_XML_FILE = "xml/platformdata.xml";
	private static final String RTOS_PG_XML_FILE = "xml/rtospg.xml";

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
	public void tc_00_ChangeRTOSLocation() throws Exception{
		Utility.changeModuleDownloadLocation(robot, ProjectParameters.FileLocation.AMAZON_RTOS_LOCATION, true);
		Utility.changeModuleDownloadLocation(robot, ProjectParameters.FileLocation.FIT_MODULES_LOCATION, false);
		Utility.reFocus(robot);
	}
	
	@Test
	public void tc_01_ImportAWSDemos() throws Exception {
		bot.sleep(1000);
		bot.menu(MenuName.MENU_FILE).menu(MenuName.MEMU_IMPORT).click();
		bot.tree().getTreeItem(ProjectParameters.FolderAndFile.FOLDER_GENERAL).expand();
		bot.tree().getTreeItem(ProjectParameters.FolderAndFile.FOLDER_GENERAL)
		.getNode(ProjectParameters.FolderAndFile.RENESAS_GITHUB).select();
		bot.button(ButtonAction.BUTTON_NEXT).click();
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
		
	}

	@Test
	public void tc_02_CheckWarningDialog() throws Exception {
		bot.sleep(5000);
		bot.menu(MenuName.MENU_FILE).menu(MenuName.MEMU_IMPORT).click();
		bot.tree().getTreeItem(ProjectParameters.FolderAndFile.FOLDER_GENERAL).expand();
		bot.tree().getTreeItem(ProjectParameters.FolderAndFile.FOLDER_GENERAL)
		.getNode(ProjectParameters.FolderAndFile.RENESAS_GITHUB).select();
		bot.button(ButtonAction.BUTTON_NEXT).click();
		bot.activeShell().setFocus();
		String text = bot.textWithLabel("", 1).getText();
		String value = " Specified folder is not empty.";
		boolean isWarningMessageCorrect = text.contains(value);
		
		bot.button(ButtonAction.BUTTON_NEXT).click();
		boolean isTextCorrect = false;
		if (bot.activeShell().getText().equals(ProjectParameters.WINDOW_COPY_RESOURCES)) {
			String value2 = "Specified folder is not empty. Duplicated content will be replaced.\nDo you want to replace?";
			String label = bot.label().getText();
			isTextCorrect = label.contains(value2);
			bot.button(ButtonAction.BUTTON_NO).click();
			bot.sleep(5000);
			bot.button(ButtonAction.BUTTON_CANCEL).click();
		}
		
		if (!isWarningMessageCorrect || !isTextCorrect) {
			assertFalse(true);
		}
	}
	
	@Test
	public void tc_03_BuildProject() throws Exception {
		bot.tree().getTreeItem("aws_demos").select();
		bot.tree().getTreeItem("aws_demos [HardwareDebug]").contextMenu(ProjectParameters.ProjectSettings.C_CPLUSPLUS_PROJECT_SETTINGS).click();
		bot.cTabItem("Toolchain").activate();
		bot.comboBoxWithLabel("Toolchain:").setSelection(0);
		bot.button(ButtonAction.APPLY_AND_CLOSE).click();
		bot.tree().getTreeItem("aws_demos [HardwareDebug]").contextMenu(MenuName.MENU_BUILD_PROJECT)
				.click();
		SWTBotView consoleView = bot.viewById("org.eclipse.ui.console.ConsoleView");
		boolean isBuildSuccessful = false;
		while (true) {
			if (consoleView.bot().styledText().getText().contains(ProjectParameters.BUILD_SUCCESSFULLY)) {
				isBuildSuccessful = true;
				break;
			}
			if (consoleView.bot().styledText().getText().contains(ProjectParameters.BUILD_FAILED)) {
				break;
			}
		}
		if (isBuildSuccessful) {
			Utility.deleteProject("aws_demos", true);
		} else {
			assertFalse(true);
		}
	}
}
