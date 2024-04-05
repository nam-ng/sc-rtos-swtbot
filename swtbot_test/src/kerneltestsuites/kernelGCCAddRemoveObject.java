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
public class kernelGCCAddRemoveObject {
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
		projectModelSpecific = PGUtility.prepareProjectModel(RTOSType.FREERTOSKERNEL, RTOSVersion.Kernel_1_0_8,
				RTOSApplication.KERNEL_BARE, Constants.GCC_TOOLCHAIN, TargetBoard.BOARD_RSK_RX65N_2MB);
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
		PGUtility.createProject(RTOSType.FREERTOSKERNEL, RTOSVersion.Kernel_1_0_8, RTOSApplication.KERNEL_BARE,
				Constants.GCC_TOOLCHAIN, TargetBoard.BOARD_RSK_RX65N_2MB);

	}

	@Test
	public void tc_02_AddAndRemoveRows() throws Exception {
		Utility.openSCFGEditor(projectModelSpecific, ProjectParameters.SCFG_COMPONENT_TAB);

		bot.tree(1).getTreeItem(ProjectParameters.FolderAndFile.FOLDER_RTOS)
				.getNode(ProjectParameters.FolderAndFile.FOLDER_RTOS_OBJECT)
				.getNode(ProjectParameters.RTOSComponent.FREERTOS_OBJECT).select();

		bot.tabItem(ProjectParameters.KernelObjectTab.TASKS).activate();
		Utility.addOrRemoveKernelObject(true, 0);
		Utility.addOrRemoveKernelObject(true, 0);
		Utility.addOrRemoveKernelObject(true, 0);

		Utility.addOrRemoveKernelObject(false, 1);
		Utility.addOrRemoveKernelObject(false, 1);

		bot.sleep(3000);
		boolean check0 = bot.ccomboBox(0).getText().equals(ProjectParameters.KernelObject.KERNEL_START);
		boolean check1 = bot.text(1).getText().equals(ProjectParameters.KernelObject.TASK_1);
		boolean check2 = bot.text(2).getText().equals(ProjectParameters.KernelObject.TASK_1);
		boolean check3 = bot.text(3).getText().equals(ProjectParameters.KernelObject.NUMBER_512);
		boolean check4 = bot.text(4).getText().equals(ProjectParameters.KernelObject.NULL);
		boolean check5 = bot.text(5).getText().equals(ProjectParameters.KernelObject.NULL);
		boolean check6 = bot.text(6).getText().equals(ProjectParameters.KernelObject.NUMBER_1);

		Utility.addOrRemoveKernelObject(false, 0);

		if (!check0 || !check1 || !check2 || !check3 || !check4 || !check5 || !check6) {
			assertFalse(true);
		}
	}

	@Test
	public void tc_03_DeleteAndCreateKernelProject() throws Exception {
		Utility.deleteProject(projectModelSpecific.getProjectName(), true);
		if (bot.activeShell().getText().equals(ProjectParameters.WINDOW_SAVE_RESOURCES)) {
			bot.button(ButtonAction.BUTTON_DONT_SAVE).click();
		}
		PGUtility.createProject(RTOSType.FREERTOSKERNEL, RTOSVersion.Kernel_1_0_8, RTOSApplication.KERNEL_BARE,
				Constants.GCC_TOOLCHAIN, TargetBoard.BOARD_RSK_RX65N_2MB);
	}

	@Test
	public void tc_04_AddFirstRowAndCheckDefault() throws Exception {
		Utility.openSCFGEditor(projectModelSpecific, ProjectParameters.SCFG_COMPONENT_TAB);

		bot.tree(1).getTreeItem(ProjectParameters.FolderAndFile.FOLDER_RTOS)
				.getNode(ProjectParameters.FolderAndFile.FOLDER_RTOS_OBJECT)
				.getNode(ProjectParameters.RTOSComponent.FREERTOS_OBJECT).select();
		bot.tabItem(ProjectParameters.KernelObjectTab.TASKS).activate();
		Utility.addOrRemoveKernelObject(true, 0);

		bot.sleep(3000);
		boolean check0 = bot.ccomboBox(0).getText().equals(ProjectParameters.KernelObject.KERNEL_START);
		boolean check1 = bot.text(1).getText().equals(ProjectParameters.KernelObject.TASK_1);
		boolean check2 = bot.text(2).getText().equals(ProjectParameters.KernelObject.TASK_1);
		boolean check3 = bot.text(3).getText().equals(ProjectParameters.KernelObject.NUMBER_512);
		boolean check4 = bot.text(4).getText().equals(ProjectParameters.KernelObject.NULL);
		boolean check5 = bot.text(5).getText().equals(ProjectParameters.KernelObject.NULL);
		boolean check6 = bot.text(6).getText().equals(ProjectParameters.KernelObject.NUMBER_1);

		Utility.addOrRemoveKernelObject(false, 0);

		if (!check0 || !check1 || !check2 || !check3 || !check4 || !check5 || !check6) {
			assertFalse(true);
		}
	}

	@Test
	public void tc_05_DeleteAndCreateKernelProject() throws Exception {
		Utility.deleteProject(projectModelSpecific.getProjectName(), true);
		if (bot.activeShell().getText().equals(ProjectParameters.WINDOW_SAVE_RESOURCES)) {
			bot.button(ButtonAction.BUTTON_DONT_SAVE).click();
		}
		PGUtility.createProject(RTOSType.FREERTOSKERNEL, RTOSVersion.Kernel_1_0_8, RTOSApplication.KERNEL_BARE,
				Constants.GCC_TOOLCHAIN, TargetBoard.BOARD_RSK_RX65N_2MB);
	}

	@Test
	public void tc_06_AddSecondRowAndCheckDefault() throws Exception {
		Utility.openSCFGEditor(projectModelSpecific, ProjectParameters.SCFG_COMPONENT_TAB);

		bot.tree(1).getTreeItem(ProjectParameters.FolderAndFile.FOLDER_RTOS)
				.getNode(ProjectParameters.FolderAndFile.FOLDER_RTOS_OBJECT)
				.getNode(ProjectParameters.RTOSComponent.FREERTOS_OBJECT).select();
		bot.tabItem(ProjectParameters.KernelObjectTab.TASKS).activate();
		Utility.addOrRemoveKernelObject(true, 0);
		Utility.addOrRemoveKernelObject(true, 0);

		bot.sleep(3000);
		/*
		 * Each cell of the tasks object text box is access by formula: columnNumber + 7
		 * * rowNumber because there are 7 text box in a row columnNumber: count from 1
		 * rowNumber: count from 0
		 */
		boolean check0 = bot.ccomboBox(1).getText().equals(ProjectParameters.KernelObject.KERNEL_START);
		boolean check1 = bot.text(1 + 7 * 1).getText().equals(ProjectParameters.KernelObject.TASK_2);
		boolean check2 = bot.text(2 + 7 * 1).getText().equals(ProjectParameters.KernelObject.TASK_2);
		boolean check3 = bot.text(3 + 7 * 1).getText().equals(ProjectParameters.KernelObject.NUMBER_512);
		boolean check4 = bot.text(4 + 7 * 1).getText().equals(ProjectParameters.KernelObject.NULL);
		boolean check5 = bot.text(5 + 7 * 1).getText().equals(ProjectParameters.KernelObject.NULL);
		boolean check6 = bot.text(6 + 7 * 1).getText().equals(ProjectParameters.KernelObject.NUMBER_1);

		Utility.addOrRemoveKernelObject(false, 0);
		Utility.addOrRemoveKernelObject(false, 0);

		if (!check0 || !check1 || !check2 || !check3 || !check4 || !check5 || !check6) {
			assertFalse(true);
		}
	}

	@Test
	public void tc_07_DeleteAndCreateKernelProject() throws Exception {
		Utility.deleteProject(projectModelSpecific.getProjectName(), true);
		if (bot.activeShell().getText().equals(ProjectParameters.WINDOW_SAVE_RESOURCES)) {
			bot.button(ButtonAction.BUTTON_DONT_SAVE).click();
		}
		PGUtility.createProject(RTOSType.FREERTOSKERNEL, RTOSVersion.Kernel_1_0_8, RTOSApplication.KERNEL_BARE,
				Constants.GCC_TOOLCHAIN, TargetBoard.BOARD_RSK_RX65N_2MB);
	}

	@Test
	public void tc_08_AddThirdRowAndCheckDefault() throws Exception {
		Utility.openSCFGEditor(projectModelSpecific, ProjectParameters.SCFG_COMPONENT_TAB);

		bot.tree(1).getTreeItem(ProjectParameters.FolderAndFile.FOLDER_RTOS)
				.getNode(ProjectParameters.FolderAndFile.FOLDER_RTOS_OBJECT)
				.getNode(ProjectParameters.RTOSComponent.FREERTOS_OBJECT).select();
		bot.tabItem(ProjectParameters.KernelObjectTab.TASKS).activate();
		Utility.addOrRemoveKernelObject(true, 0);
		Utility.addOrRemoveKernelObject(true, 0);
		Utility.addOrRemoveKernelObject(true, 0);

		bot.sleep(3000);
		/*
		 * Each cell of the tasks object text box is access by formula: columnNumber + 7
		 * * rowNumber because there are 7 text box in a row columnNumber: count from 1
		 * rowNumber: count from 0
		 */
		boolean check0 = bot.ccomboBox(2).getText().equals(ProjectParameters.KernelObject.KERNEL_START);
		boolean check1 = bot.text(1 + 7 * 2).getText().equals(ProjectParameters.KernelObject.TASK_3);
		boolean check2 = bot.text(2 + 7 * 2).getText().equals(ProjectParameters.KernelObject.TASK_3);
		boolean check3 = bot.text(3 + 7 * 2).getText().equals(ProjectParameters.KernelObject.NUMBER_512);
		boolean check4 = bot.text(4 + 7 * 2).getText().equals(ProjectParameters.KernelObject.NULL);
		boolean check5 = bot.text(5 + 7 * 2).getText().equals(ProjectParameters.KernelObject.NULL);
		boolean check6 = bot.text(6 + 7 * 2).getText().equals(ProjectParameters.KernelObject.NUMBER_1);

		Utility.addOrRemoveKernelObject(false, 0);
		Utility.addOrRemoveKernelObject(false, 0);
		Utility.addOrRemoveKernelObject(false, 0);

		if (!check0 || !check1 || !check2 || !check3 || !check4 || !check5 || !check6) {
			assertFalse(true);
		}
	}

	@Test
	public void tc_09_DeleteKernelProject() throws Exception {
		Utility.deleteProject(projectModelSpecific.getProjectName(), true);
		if (bot.activeShell().getText().equals(ProjectParameters.WINDOW_SAVE_RESOURCES)) {
			bot.button(ButtonAction.BUTTON_DONT_SAVE).click();
		}
	}
}
