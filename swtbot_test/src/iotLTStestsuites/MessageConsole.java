package iotLTStestsuites;

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

import common.Constants;
import common.LogUtil;
import model.ProjectModel;
import model.RTOSManager;
import parameters.ProjectParameters;
import parameters.ProjectParameters.ButtonAction;
import parameters.ProjectParameters.RTOSApplication;
import parameters.ProjectParameters.RTOSType;
import parameters.ProjectParameters.RTOSVersion;
import parameters.ProjectParameters.TargetBoard;
import platform.PlatformModel;
import utilities.PGUtility;
import utilities.Utility;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class MessageConsole {
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
		projectModelSpecific = PGUtility.prepareProjectModel(RTOSType.FREERTOSIOTLTS, RTOSVersion.IoTLTS_202210_1_0_0,
				RTOSApplication.KERNEL_BARE, Constants.CCRX_TOOLCHAIN, TargetBoard.BOARD_CK_RX65N);
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
		Utility.changeModuleDownloadLocation(robot, ProjectParameters.FileLocation.IOTLTS_RTOS_LOCATION, true);
		Utility.changeModuleDownloadLocation(robot, ProjectParameters.FileLocation.NEWEST_FIT_MODULES_LOCATION, false);
		Utility.reFocus(robot);
	}
	
	@Test
	public void tc_01_CreateIoTLTSProject() throws Exception {
		PGUtility.createProject(RTOSType.FREERTOSIOTLTS, RTOSVersion.IoTLTS_202210_1_0_0, RTOSApplication.KERNEL_BARE,
				Constants.CCRX_TOOLCHAIN, TargetBoard.BOARD_CK_RX65N);
	}
	
	@Test
	public void tc_02_CheckConsoleOutput() throws Exception {
		boolean check2 = Utility.isConsoleHasString("M04050011: File modified:src\\frtos_config\\FreeRTOSConfig.h");
		boolean check3 = Utility.isConsoleHasString("M04050001: File generated:src\\frtos_startup\\freertos_object_init.c");
		boolean check4 = Utility.isConsoleHasString("M04050001: File generated:src\\frtos_skeleton\\task_function.h");
		
		if (!check2 || !check3 || !check4) {
			assertFalse(true);
		}
	}
	
	
	@Test
	public void tc_03_CheckConsoleOutput() throws Exception {
		Utility.openSCFGEditor(projectModelSpecific, ProjectParameters.SCFG_COMPONENT_TAB);
		bot.tree(1).getTreeItem(ProjectParameters.FolderAndFile.FOLDER_RTOS)
		.getNode(ProjectParameters.FolderAndFile.FOLDER_RTOS_OBJECT)
		.getNode(ProjectParameters.RTOSComponent.IOT_LTS_FREERTOS_OBJECT).select();
		bot.tabItem(ProjectParameters.KernelObjectTab.TASKS).activate();
		Utility.addOrRemoveKernelObject(true, 0);
		Utility.clickClearConsole();
		Utility.clickGenerateCode();
		bot.sleep(5000);
		boolean check2 = Utility.isConsoleHasString("M04050001: File generated:src\\frtos_startup\\freertos_object_init.c");
		boolean check3 = Utility.isConsoleHasString("M04050001: File generated:src\\frtos_skeleton\\task_function.h");
		boolean check4 = Utility.isConsoleHasString("M04050001: File generated:src\\frtos_skeleton\\task_1.c");
		
		if (!check2 || !check3 || !check4) {
			assertFalse(true);
		}

	}
	
	@Test
	public void tc_04_CheckConsoleOutput() throws Exception {
		bot.tabItem(ProjectParameters.KernelObjectTab.TASKS).activate();
		Utility.addOrRemoveKernelObject(false, 0);
		Utility.clickClearConsole();
		Utility.clickGenerateCode();
		bot.sleep(5000);
		boolean check1 = Utility.isConsoleHasString("M04050001: File generated:src\\frtos_startup\\freertos_object_init.c");
		boolean check2 = Utility.isConsoleHasString("M04050001: File generated:src\\frtos_skeleton\\task_function.h");
		
		if (!check1 || !check2) {
			assertFalse(true);
		}

	}
	
	@Test
	public void tc_05_DeleteIoTLTSProject() throws Exception {
		Utility.deleteProject(projectModelSpecific.getProjectName(), true);
		if (bot.activeShell().getText().equals(ProjectParameters.WINDOW_SAVE_RESOURCES)) {
			bot.button(ButtonAction.BUTTON_DONT_SAVE).click();
		}
	}
}
