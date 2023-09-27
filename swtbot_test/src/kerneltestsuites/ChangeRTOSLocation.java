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
public class ChangeRTOSLocation {
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
		projectModelSpecific = PGUtility.prepareProjectModel(RTOSType.FREERTOSKERNEL, RTOSVersion.Kernel_1_0_7,
				RTOSApplication.KERNEL_BARE, Constants.CCRX_TOOLCHAIN, TargetBoard.BOARD_RSK_RX72N);
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
				Constants.CCRX_TOOLCHAIN, TargetBoard.BOARD_RSK_RX72N);
	}
	
	@Test
	public void tc_02_ChangeRTOSLocation() throws Exception{
		Utility.changeModuleDownloadLocation(robot, ProjectParameters.FileLocation.KERNEL_RTOS_LOCATION, true);
		Utility.reFocus(robot);
		
		Utility.clickClearConsole();
		
		Utility.changeModuleDownloadLocation(robot, ProjectParameters.FileLocation.KERNEL_OLD_RTOS_LOCATION, true);
		Utility.reFocus(robot);
		
		Utility.openSCFGEditor(projectModelSpecific, ProjectParameters.SCFG_COMPONENT_TAB);

		bot.tree(1).getTreeItem(ProjectParameters.FolderAndFile.FOLDER_RTOS)
				.getNode(ProjectParameters.FolderAndFile.FOLDER_RTOS_KERNEL)
				.getNode(ProjectParameters.RTOSComponent.FREERTOS_KERNEL).select();
		
		boolean isConsoleContains = Utility
				.isConsoleHasString("E04050008: RTOS Package version " + RTOSVersion.Kernel_1_0_7 +" is not available");
		if (isConsoleContains) {
			assertFalse(true);
		}
	}
	
	@Test
	public void tc_03_ChangeNoneRTOSLocation() throws Exception {
		Utility.changeModuleDownloadLocation(robot, ProjectParameters.FileLocation.KERNEL_RTOS_LOCATION, true);
		Utility.reFocus(robot);
				
		Utility.changeModuleDownloadLocation(robot, ProjectParameters.FileLocation.EMPTY_RTOS_LOCATION, true);
		Utility.reFocus(robot);
		
		Utility.openSCFGEditor(projectModelSpecific, ProjectParameters.SCFG_COMPONENT_TAB);

		bot.tree(1).getTreeItem(ProjectParameters.FolderAndFile.FOLDER_RTOS)
				.getNode(ProjectParameters.FolderAndFile.FOLDER_RTOS_KERNEL)
				.getNode(ProjectParameters.RTOSComponent.FREERTOS_KERNEL).select();
		
		boolean isConsoleContains = Utility
				.isConsoleHasString("E04050008: RTOS Package version " + RTOSVersion.Kernel_1_0_7 +" is not available");
		if (isConsoleContains) {
			assertFalse(true);
		}
	}
	
	@Test
	public void tc_04_RTOSReloadAfterChangeLocation() throws Exception {
		Utility.changeModuleDownloadLocation(robot, ProjectParameters.FileLocation.KERNEL_RTOS_LOCATION, true);
		Utility.reFocus(robot);
				
		Utility.changeModuleDownloadLocation(robot, ProjectParameters.FileLocation.KERNEL_OLD_RTOS_LOCATION, true);
		Utility.reFocus(robot);
		
		Utility.openSCFGEditor(projectModelSpecific, ProjectParameters.SCFG_COMPONENT_TAB);

		bot.tree(1).getTreeItem(ProjectParameters.FolderAndFile.FOLDER_RTOS)
				.getNode(ProjectParameters.FolderAndFile.FOLDER_RTOS_KERNEL)
				.getNode(ProjectParameters.RTOSComponent.FREERTOS_KERNEL).select();
		boolean isConsoleContains = Utility
				.isConsoleHasString("E04050008: RTOS Package version " + RTOSVersion.Kernel_1_0_7 +" is not available");
		
		Utility.changeModuleDownloadLocation(robot, ProjectParameters.FileLocation.KERNEL_RTOS_LOCATION, true);
		Utility.reFocus(robot);
		bot.closeAllEditors();
		Utility.openSCFGEditor(projectModelSpecific, ProjectParameters.SCFG_COMPONENT_TAB);

		bot.tree(1).getTreeItem(ProjectParameters.FolderAndFile.FOLDER_RTOS)
				.getNode(ProjectParameters.FolderAndFile.FOLDER_RTOS_KERNEL)
				.getNode(ProjectParameters.RTOSComponent.FREERTOS_KERNEL).select();
		boolean isConsoleContains2 = Utility
				.isConsoleHasString("E04050008: RTOS Package version " + RTOSVersion.Kernel_1_0_7 +" is not available");
		
		if (isConsoleContains || isConsoleContains2) {
			assertFalse(true);
		}
	}
	

	@Test
	public void tc_05_DeleteKernelProject() throws Exception {
		Utility.deleteProject(projectModelSpecific.getProjectName(), true);
		if (bot.activeShell().getText().equals(ProjectParameters.WINDOW_SAVE_RESOURCES)) {
			bot.button(ButtonAction.BUTTON_DONT_SAVE).click();
		}
	}
}
