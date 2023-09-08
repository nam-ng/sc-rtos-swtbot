package kerneltestsuites;

import static org.junit.Assert.assertFalse;

import java.io.File;

import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
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
public class kernelCCRXObjectGUI {
	private static SWTWorkbenchBot bot;
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
	}

	@Test
	public void tc_01_CreateKernelProject() throws Exception {
		PGUtility.createProject(RTOSType.FREERTOSKERNEL, RTOSVersion.Kernel_1_0_7, RTOSApplication.KERNEL_BARE,
				Constants.CCRX_TOOLCHAIN, TargetBoard.BOARD_RSK_RX65N_2MB);

	}

	@Test
	public void tc_02_Check8TabsDisplay() throws Exception {
		Utility.openSCFGEditor(projectModelSpecific, ProjectParameters.SCFG_COMPONENT_TAB);

		bot.tree(1).getTreeItem(ProjectParameters.FolderAndFile.FOLDER_RTOS)
				.getNode(ProjectParameters.FolderAndFile.FOLDER_RTOS_OBJECT)
				.getNode(ProjectParameters.RTOSComponent.FREERTOS_OBJECT).select();

		boolean isEightTabsDisplay = false;
		if (bot.tabItem(ProjectParameters.KernelObjectTab.HEAP_ESTIMATION).isVisible()
				&& bot.tabItem(ProjectParameters.KernelObjectTab.TASKS).isVisible()
				&& bot.tabItem(ProjectParameters.KernelObjectTab.SEMAPHORES).isVisible()
				&& bot.tabItem(ProjectParameters.KernelObjectTab.QUEUES).isVisible()
				&& bot.tabItem(ProjectParameters.KernelObjectTab.SOFTWARE_TIMERS).isVisible()
				&& bot.tabItem(ProjectParameters.KernelObjectTab.EVENT_GROUPS).isVisible()
				&& bot.tabItem(ProjectParameters.KernelObjectTab.STREAM_BUFFERS).isVisible()
				&& bot.tabItem(ProjectParameters.KernelObjectTab.MESSAGE_BUFFERS).isVisible()) {
			isEightTabsDisplay = true;
		}

		if (!isEightTabsDisplay) {
			assertFalse(true);
		}
	}

	@Test
	public void tc_03_SwitchComponentAndCheckUI() throws Exception {
		bot.tree(1).getTreeItem(ProjectParameters.FolderAndFile.FOLDER_RTOS)
				.getNode(ProjectParameters.FolderAndFile.FOLDER_RTOS_OBJECT)
				.getNode(ProjectParameters.RTOSComponent.FREERTOS_OBJECT).select();

		bot.tree(1).getTreeItem(ProjectParameters.FolderAndFile.FOLDER_RTOS)
				.getNode(ProjectParameters.FolderAndFile.FOLDER_RTOS_KERNEL)
				.getNode(ProjectParameters.RTOSComponent.FREERTOS_KERNEL).select();

		bot.tree(1).getTreeItem(ProjectParameters.FolderAndFile.FOLDER_RTOS)
				.getNode(ProjectParameters.FolderAndFile.FOLDER_RTOS_OBJECT)
				.getNode(ProjectParameters.RTOSComponent.FREERTOS_OBJECT).select();

		boolean isEightTabsDisplay = false;
		if (bot.tabItem(ProjectParameters.KernelObjectTab.HEAP_ESTIMATION).isVisible()
				&& bot.tabItem(ProjectParameters.KernelObjectTab.TASKS).isVisible()
				&& bot.tabItem(ProjectParameters.KernelObjectTab.SEMAPHORES).isVisible()
				&& bot.tabItem(ProjectParameters.KernelObjectTab.QUEUES).isVisible()
				&& bot.tabItem(ProjectParameters.KernelObjectTab.SOFTWARE_TIMERS).isVisible()
				&& bot.tabItem(ProjectParameters.KernelObjectTab.EVENT_GROUPS).isVisible()
				&& bot.tabItem(ProjectParameters.KernelObjectTab.STREAM_BUFFERS).isVisible()
				&& bot.tabItem(ProjectParameters.KernelObjectTab.MESSAGE_BUFFERS).isVisible()) {
			isEightTabsDisplay = true;
		}

		if (!isEightTabsDisplay) {
			assertFalse(true);
		}
	}

	@Test
	public void tc_04_CheckHeapGUI() throws Exception {
		bot.tabItem(ProjectParameters.KernelObjectTab.HEAP_ESTIMATION).activate();
		boolean isHeapGUIDisplayCorrectly = false;
		if (bot.label("Total For Heap Usage").isVisible() && bot.label("Total For Task(s)").isVisible()
				&& bot.label("Total For Queue(s)").isVisible() && bot.label("Total For Semaphore(s)").isVisible()
				&& bot.label("Total For Software Timer(s)").isVisible()
				&& bot.label("Total For Event Group(s)").isVisible()
				&& bot.label("Total For Message Buffer(s)").isVisible()
				&& bot.label("Total For Stream Buffer(s)").isVisible() && bot.label("Main Task").isVisible()
				&& bot.label("IDLE Task").isVisible() && bot.label("Timer Service Task").isVisible()
				&& bot.label("Timer Queue").isVisible()) {
			isHeapGUIDisplayCorrectly = true;
		}

		if (!isHeapGUIDisplayCorrectly) {
			assertFalse(true);
		}
	}

	@Test
	public void tc_05_CheckTaskUI() throws Exception {
		Utility.CheckTaskUI();
	}

	@Test
	public void tc_06_CheckEventGroups() throws Exception {
		Utility.checkEventGroups();
	}

	@Test
	public void tc_07_checkMsgBuffer() throws Exception {
		Utility.checkMsgBuffer();
	}

	@Test
	public void tc_08_checkQueueUI() throws Exception {
		Utility.checkQueueUI();
	}

	@Test
	public void tc_09_checkSWTimerUI() throws Exception {
		Utility.checkSWTimerUI();
	}

	@Test
	public void tc_10_checkSemaphoresUI() throws Exception {
		Utility.checkSemaphoresUI();
	}

	@Test
	public void tc_11_checkStreamBufferUI() throws Exception {
		Utility.checkStreamBufferUI();
	}

	@Test
	public void tc_12_DeleteKernelProject() throws Exception {
		Utility.deleteProject(projectModelSpecific.getProjectName(), true);
		if (bot.activeShell().getText().equals(ProjectParameters.WINDOW_SAVE_RESOURCES)) {
			bot.button(ButtonAction.BUTTON_DONT_SAVE).click();
		}
	}
}
