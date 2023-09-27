package kerneltestsuites;

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
public class kernelCCRXCheckMessageConsole {
	private static SWTWorkbenchBot bot;
	private static Robot robot;
	private static ProjectModel projectModelSpecific = new ProjectModel();
	private static final String PLATFORM_XML_FILE = "xml/platformdata.xml";
	private static final String RTOS_PG_XML_FILE = "xml/rtospg.xml";

	@BeforeClass
	public static void beforeClass() throws Exception {
		bot = new SWTWorkbenchBot();
		PlatformModel.loadPlatformModel(new File(Utility.getBundlePath(LogUtil.PLUGIN_ID, PLATFORM_XML_FILE)));
		RTOSManager.loadRTOSModel(new File(Utility.getBundlePath(LogUtil.PLUGIN_ID, RTOS_PG_XML_FILE)));
		projectModelSpecific = PGUtility.prepareProjectModel(RTOSType.FREERTOSKERNEL, RTOSVersion.Kernel_1_0_7,
				RTOSApplication.KERNEL_BARE, Constants.CCRX_TOOLCHAIN, TargetBoard.BOARD_RSK_RX65N_2MB);
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
		Utility.changeModuleDownloadLocation(robot, ProjectParameters.FileLocation.KERNEL_RTOS_LOCATION, true);
		Utility.changeModuleDownloadLocation(robot, ProjectParameters.FileLocation.FIT_MODULES_LOCATION, false);
		Utility.reFocus(robot);
	}

	@Test
	public void tc_01_CreateKernelProject() throws Exception {
		PGUtility.createProject(RTOSType.FREERTOSKERNEL, RTOSVersion.Kernel_1_0_7, RTOSApplication.KERNEL_BARE,
				Constants.CCRX_TOOLCHAIN, TargetBoard.BOARD_RSK_RX65N_2MB);

	}

	@Test
	public void tc_02_CheckConsoleAfterCreate() throws Exception {
		Utility.openSCFGEditor(projectModelSpecific, ProjectParameters.SCFG_COMPONENT_TAB);

		SWTBotView consoleView = bot.viewById("org.eclipse.ui.console.ConsoleView");
		boolean isConsoleContains1 = false;
		isConsoleContains1 = Utility.isConsoleHasString("M04050011: File modified:src\\frtos_config\\FreeRTOSConfig.h");

		bot.tree(1).getTreeItem(ProjectParameters.FolderAndFile.FOLDER_RTOS)
				.getNode(ProjectParameters.FolderAndFile.FOLDER_RTOS_OBJECT)
				.getNode(ProjectParameters.RTOSComponent.FREERTOS_OBJECT).select();
		bot.tabItem(ProjectParameters.KernelObjectTab.TASKS).activate();
		Utility.addOrRemoveKernelObject(true, 0);

		Utility.clickClearConsole();
		Utility.clickGenerateCode();
		boolean isConsoleContains2 = false;
		boolean isConsoleContains3 = false;
		boolean isConsoleContains4 = false;
		isConsoleContains2 = Utility
				.isConsoleHasString("M04050001: File generated:src\\frtos_startup\\freertos_object_init.c");
		isConsoleContains3 = Utility
				.isConsoleHasString("M04050001: File generated:src\\frtos_skeleton\\task_function.h");
		isConsoleContains4 = Utility.isConsoleHasString("M04050001: File generated:src\\frtos_skeleton\\task_1.c");

		if (!isConsoleContains1 || !isConsoleContains2 || !isConsoleContains3 || !isConsoleContains4) {
			assertFalse(true);
		}
	}

	@Test
	public void tc_03_DeleteKernelProject() throws Exception {
		Utility.deleteProject(projectModelSpecific.getProjectName(), true);
		if (bot.activeShell().getText().equals(ProjectParameters.WINDOW_SAVE_RESOURCES)) {
			bot.button(ButtonAction.BUTTON_DONT_SAVE).click();
		}
	}
}
