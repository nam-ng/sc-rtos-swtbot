package kerneltestsuites;

import static org.junit.Assert.assertFalse;

import java.io.File;

import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotView;
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
public class kernelCCRXCheckContrains {
	private static SWTWorkbenchBot bot;
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
				RTOSApplication.KERNEL_BARE, Constants.CCRX_TOOLCHAIN, TargetBoard.BOARD_RSK_RX65N_2MB);
	}

	@Test
	public void tc_01_CreateKernelProject() throws Exception {
		PGUtility.createProject(RTOSType.FREERTOSKERNEL, RTOSVersion.Kernel_1_0_7, RTOSApplication.KERNEL_BARE,
				Constants.CCRX_TOOLCHAIN, TargetBoard.BOARD_RSK_RX65N_2MB);

	}

	@Test
	public void tc_02_TwoRowsDuplicate() throws Exception {
		Utility.openSCFGEditor(projectModelSpecific, ProjectParameters.SCFG_COMPONENT_TAB);

		bot.tree(1).getTreeItem(ProjectParameters.FolderAndFile.FOLDER_RTOS)
				.getNode(ProjectParameters.FolderAndFile.FOLDER_RTOS_OBJECT)
				.getNode(ProjectParameters.RTOSComponent.FREERTOS_OBJECT).select();

		bot.tabItem(ProjectParameters.KernelObjectTab.TASKS).activate();
		Utility.addOrRemoveKernelObject(true, 0);
		Utility.addOrRemoveKernelObject(true, 0);

		Utility.clickClearConsole();

		bot.sleep(3000);
		bot.text(1 + 7 * 0).setText("task_1");
		bot.text(1 + 7 * 1).setText("task_1");

		boolean check1 = Utility.isConsoleHasString(ProjectParameters.MessageCode.E04050007);

		Utility.clickClearConsole();

		bot.text(1 + 7 * 1).setText("task_2");
		bot.text(2 + 7 * 0).setText("task_1");
		bot.text(2 + 7 * 1).setText("task_1");

		boolean check2 = Utility.isConsoleHasString(ProjectParameters.MessageCode.E04050007);

		Utility.clickClearConsole();

		Utility.addOrRemoveKernelObject(false, 0);
		Utility.addOrRemoveKernelObject(false, 0);

		if (!check1 || !check2) {
			assertFalse(true);
		}

	}

	@Test
	public void tc_03_EmptyError() throws Exception {

		Utility.addOrRemoveKernelObject(true, 0);

		bot.sleep(3000);
		bot.text(1 + 7 * 0).setText("");

		boolean check1 = Utility.isConsoleHasString(ProjectParameters.MessageCode.E04050004);

		Utility.addOrRemoveKernelObject(false, 0);

		Utility.clickClearConsole();

		if (!check1) {
			assertFalse(true);
		}
	}

	@Test
	public void tc_04_MustBeANumber() throws Exception {
		Utility.addOrRemoveKernelObject(true, 0);

		bot.sleep(3000);
		bot.text(3 + 7 * 0).setText("a");

		boolean check1 = Utility.isConsoleHasString(ProjectParameters.MessageCode.E04050002);

		bot.text(3 + 7 * 0).setText("512");

		Utility.clickClearConsole();

		bot.text(6 + 7 * 0).setText("a");

		boolean check2 = Utility.isConsoleHasString(ProjectParameters.MessageCode.E04050002);

		bot.text(3 + 7 * 0).setText("1");

		Utility.clickClearConsole();

		if (!check1 || !check2) {
			assertFalse(true);
		}
	}

	@Test
	public void tc_05_MustBeANumberQueues() throws Exception {
		bot.tabItem(ProjectParameters.KernelObjectTab.QUEUES).activate();

		Utility.addOrRemoveKernelObject(true, 0);

		bot.text(2).setText("a");

		boolean check1 = Utility.isConsoleHasString(ProjectParameters.MessageCode.E04050002);

		bot.text(2).setText("100");

		Utility.clickClearConsole();

		if (!check1) {
			assertFalse(true);
		}
	}

	@Test
	public void tc_06_MustBeANumberSWTimer() throws Exception {
		bot.tabItem(ProjectParameters.KernelObjectTab.SOFTWARE_TIMERS).activate();

		Utility.addOrRemoveKernelObject(true, 0);

		bot.text(3).setText("a");

		boolean check1 = Utility.isConsoleHasString(ProjectParameters.MessageCode.E04050002);

		bot.text(3).setText("100");

		Utility.clickClearConsole();

		bot.text(4).setText("a");

		boolean check2 = Utility.isConsoleHasString(ProjectParameters.MessageCode.E04050002);

		bot.text(4).setText("0");

		Utility.clickClearConsole();

		if (!check1 || !check2) {
			assertFalse(true);
		}
	}

	@Test
	public void tc_07_MustBeANumberStreamBuffer() throws Exception {
		bot.tabItem(ProjectParameters.KernelObjectTab.STREAM_BUFFERS).activate();

		Utility.addOrRemoveKernelObject(true, 0);

		bot.text(2).setText("a");

		boolean check1 = Utility.isConsoleHasString(ProjectParameters.MessageCode.E04050002);

		bot.text(2).setText("100");

		Utility.clickClearConsole();

		bot.text(3).setText("a");

		boolean check2 = Utility.isConsoleHasString(ProjectParameters.MessageCode.E04050002);

		bot.text(3).setText("10");

		Utility.clickClearConsole();

		if (!check1 || !check2) {
			assertFalse(true);
		}
	}

	@Test
	public void tc_08_MustBeANumberMsgBuffer() throws Exception {
		bot.tabItem(ProjectParameters.KernelObjectTab.MESSAGE_BUFFERS).activate();

		Utility.addOrRemoveKernelObject(true, 0);

		bot.text(2).setText("a");

		boolean check1 = Utility.isConsoleHasString(ProjectParameters.MessageCode.E04050002);

		bot.text(2).setText("100");

		Utility.clickClearConsole();

		if (!check1) {
			assertFalse(true);
		}
	}

	@Test
	public void tc_09_MustNotBeANumber() throws Exception {
		bot.tabItem(ProjectParameters.KernelObjectTab.TASKS).activate();

		bot.sleep(3000);
		bot.text(1).setText("1");

		boolean check1 = Utility.isConsoleHasString(ProjectParameters.MessageCode.E04050001);
		boolean check2 = Utility.isConsoleHasString(ProjectParameters.MessageCode.E04050003);

		bot.text(1).setText("task_4");
		Utility.clickClearConsole();

		bot.text(4).setText("1");

		boolean check3 = Utility.isConsoleHasString(ProjectParameters.MessageCode.E04050001);
		boolean check4 = Utility.isConsoleHasString(ProjectParameters.MessageCode.E04050003);

		bot.text(4).setText("NULL");
		Utility.clickClearConsole();

		if (!check1 || !check2 || !check3 || !check4) {
			assertFalse(true);
		}
	}

	@Test
	public void tc_10_OutOfSizeQueues() throws Exception {
		bot.tabItem(ProjectParameters.KernelObjectTab.QUEUES).activate();

		bot.text(2).setText("4294967296");

		boolean check1 = Utility.isConsoleHasString(ProjectParameters.MessageCode.E04050006);

		bot.text(2).setText("100");

		Utility.clickClearConsole();

		if (!check1) {
			assertFalse(true);
		}
	}

	@Test
	public void tc_11_OutOfSizeSWTimer() throws Exception {
		bot.tabItem(ProjectParameters.KernelObjectTab.SOFTWARE_TIMERS).activate();
		bot.text(3).setText("4294967296");
		boolean check1 = Utility.isConsoleHasString("E04050006: The value must be from 1 to 4294967295");

		bot.text(3).setText("100");

		Utility.clickClearConsole();

		bot.text(4).setText("4294967296");
		boolean check2 = Utility.isConsoleHasString("E04050006: The value must be from 0 to 4294967295");

		bot.text(4).setText("0");

		Utility.clickClearConsole();

		if (!check1 || !check2) {
			assertFalse(true);
		}
	}

	@Test
	public void tc_12_OutOfSizeStreamBuffer() throws Exception {
		bot.tabItem(ProjectParameters.KernelObjectTab.STREAM_BUFFERS).activate();

		bot.text(2).setText("4294967296");

		boolean check1 = Utility.isConsoleHasString(ProjectParameters.MessageCode.E04050006);

		bot.text(2).setText("100");

		Utility.clickClearConsole();

		bot.text(3).setText("4294967296");

		boolean check2 = Utility.isConsoleHasString(ProjectParameters.MessageCode.E04050006);

		bot.text(3).setText("10");

		Utility.clickClearConsole();

		if (!check1 || !check2) {
			assertFalse(true);
		}
	}

	@Test
	public void tc_13_OutOfSizeMsgBuffer() throws Exception {
		bot.tabItem(ProjectParameters.KernelObjectTab.MESSAGE_BUFFERS).activate();

		bot.text(2).setText("4294967296");

		boolean check1 = Utility.isConsoleHasString(ProjectParameters.MessageCode.E04050006);

		bot.text(2).setText("100");

		Utility.clickClearConsole();

		if (!check1) {
			assertFalse(true);
		}
	}

	@Test
	public void tc_14_DeleteKernelProject() throws Exception {
		Utility.deleteProject(projectModelSpecific.getProjectName(), true);
		if (bot.activeShell().getText().equals(ProjectParameters.WINDOW_SAVE_RESOURCES)) {
			bot.button(ButtonAction.BUTTON_DONT_SAVE).click();
		}
	}
}
