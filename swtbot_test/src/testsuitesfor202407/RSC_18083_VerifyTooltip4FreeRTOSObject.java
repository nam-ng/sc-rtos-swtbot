package testsuitesfor202407;

import static org.junit.Assert.assertTrue;

import java.awt.Robot;
import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotView;
import org.eclipse.swtbot.swt.finder.utils.SWTBotPreferences;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTable;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import common.Constants;
import common.LogUtil;
import model.ProjectModel;
import model.RTOSManager;
import parameters.ProjectParameters;
import parameters.ProjectParameters.RTOSApplication;
import parameters.ProjectParameters.RTOSType;
import parameters.ProjectParameters.RTOSVersion;
import parameters.ProjectParameters.TargetBoard;
import platform.PlatformModel;
import utilities.PGUtility;
import utilities.Utility;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class RSC_18083_VerifyTooltip4FreeRTOSObject {
	// Object common column
	private static final String ADD_REMOVE_OBJECT = "+/-";
	private static final String HEAP_USAGE = "Heap Usage";
	
	// Task column
	private static final String TASK_INITIALIZE = "Initialize";
	private static final String TASK_CODE = "Task Code";
	private static final String TASK_NAME = "Task Name";
	private static final String TASK_STACK = "Stack Size (words)";
	private static final String TASK_HANDLER = "Task Handler";
	private static final String TASK_PARAMETER = "Parameter";
	private static final String TASK_PRIORITY = "Priority";
	// Task tool tip
	private static final String TASK_INITIALIZE_TOOLTIP = "Initialize the task automatically (kernel start) or manually.";
	private static final String TASK_CODE_TOOLTIP = "Pointer to the task entry function.";
	private static final String TASK_NAME_TOOLTIP = "A descriptive name for the task.";
	private static final String TASK_STACK_SIZE_TOOLTIP = "The number of words to allocate for use as the task's stack.";
	private static final String TASK_HANDLER_TOOLTIP = "Used to pass a handle to the created task. Task Handler is optional and can be set to NULL.";
	private static final String TASK_PARAMETER_TOOLTIP = "A value that is passed as the parameter to the created task.";
	private static final String TASK_PRIORITY_TOOLTIP = "Task priority from 0 to configMAX_PRIORITIES-1.";

	// Semaphores column
	private static final String SEMAPHORE_TYPE = "Semaphore Type";
	private static final String SEMAPHORE_HANDLER = "Semaphore Handler";

	// Semaphores tool tip
	private static final String SEMAPHORE_TYPE_TOOLTIP = "The type of semaphore, binary or mutex.";
	private static final String SEMAPHORE_HANDLER_TOOLTIP = "The handler name of semaphore.";

	// Queue column
	private static final String QUEUE_HANDLER = "Queue Handler";
	private static final String QUEUE_LENGTH = "Queue Length";
	private static final String QUEUE_ITEM_SIZE = "Items Size (bytes)";

	// Queue tool tip
	private static final String QUEUE_HANDLER_TOOLTIP = "The handler name of each queue.";
	private static final String QUEUE_LENGTH_TOOLTIP = "The maximum number of items the queue can hold any one time.";
	private static final String QUEUE_ITEM_SIZE_TOOLTIP = "The size, in bytes, required to hold each item in the queue.";

	// Software Timer column
	private static final String SW_TIMER_HANDLER = "swTimer Handler";
	private static final String SW_TIMER_NAME = "swTimer Name";
	private static final String SW_TIMER_PERIOD = "swTimer Period (ticks)";
	private static final String SW_TIMER_AUTO_LOAD = "Auto Reload";
	private static final String SW_TIMER_ID = "swTimer ID";
	private static final String SW_TIMER_CALLBACK_FUNCTION = "Callback Function";

	// Software Timer tool tip
	private static final String SW_TIMER_HANDLER_TOOLTIP = "The handler name of Software Timer.";
	private static final String SW_TIMER_NAME_TOOLTIP = "A descriptive name for the software timer.";
	private static final String SW_TIMER_PERIOD_TOOLTIP = "The period of the timer and specified in ticks.";
	private static final String SW_TIMER_AUTO_LOAD_TOOLTIP = "True: the timer is expired repeatedly, False: the timer is a one-shot after it expires.";
	private static final String SW_TIMER_ID_TOOLTIP = "An identifier that is assigned to the timer being created.";
	private static final String SW_TIMER_CALLBACK_FUNCTION_TOOLTIP = "The function to call when the timer expires.";

	// Event Groups column
	private static final String EVENT_GROUP_HANDLER = "Event Group Handler";
	
	// Event Groups tool tip
	private static final String EVENT_GROUP_HANDLER_TOOLTIP = "The handler name of Event Group.";

	// Stream Buffer column
	private static final String STREAM_BUFFER_HANDLER = "Stream Buffer Handler";
	private static final String STREAM_BUFFER_SIZE = "Stream Buffer Size (bytes)";
	private static final String STREAM_BUFFER_TRIGGER_LEVEL = "Trigger Level (bytes)";
	
	// Stream Buffer tool tip
	private static final String STREAM_BUFFER_HANDLER_TOOLTIP = "The handler name of Stream Buffer.";
	private static final String STREAM_BUFFER_SIZE_TOOLTIP = "The total number of bytes the stream buffer will be able to hold at any one time.";
	private static final String STREAM_BUFFER_TRIGGER_LEVEL_TOOLTIP = "The number of bytes that must be in the stream buffer to move the task out of the blocked state.";

	// Message Buffer column
	private static final String MESSAGE_BUFFER_HANDLER = "MsgBuffer Handler";
	private static final String MESSAGE_BUFFER_SIZE = "MsgBuffer Size (bytes)";

	// Message Buffer tool tip
	private static final String MESSAGE_BUFFER_HANDLER_TOOLTIP = "The handler name of Message Buffer.";
	private static final String MESSAGE_BUFFER_SIZE_TOOLTIP = "The total number of bytes (not messages) the message buffer will be able to hold at any one time.";
	
	private static final String PLATFORM_XML_FILE = "xml/platformdata.xml";
	private static final String RTOS_PG_XML_FILE = "xml/rtospg.xml";
	private static SWTWorkbenchBot bot;
	private static Robot robot;
	private static ProjectModel projectModelKernelRSKRX65N109 = new ProjectModel();
	private static ProjectModel projectModelIOTLTSCKRX65N113 = new ProjectModel();
	private static ProjectModel projectModelAmazonCKRX65107 = new ProjectModel();

	@BeforeClass
	public static void beforeClass() throws Exception {
		bot = new SWTWorkbenchBot();
		PlatformModel.loadPlatformModel(new File(Utility.getBundlePath(LogUtil.PLUGIN_ID, PLATFORM_XML_FILE)));
		RTOSManager.loadRTOSModel(new File(Utility.getBundlePath(LogUtil.PLUGIN_ID, RTOS_PG_XML_FILE)));
		projectModelKernelRSKRX65N109 = PGUtility.prepareProjectModel(RTOSType.FREERTOSKERNEL, RTOSVersion.Kernel_1_0_9,
				RTOSApplication.KERNEL_BARE, Constants.GCC_TOOLCHAIN, TargetBoard.BOARD_RSK_RX65N_2MB);
		projectModelIOTLTSCKRX65N113 = PGUtility.prepareProjectModel(RTOSType.FREERTOSIOTLTS, RTOSVersion.IoTLTS_202210_1_1_3,
				RTOSApplication.IOT_LTS_ETHER_PUBSUB, Constants.CCRX_TOOLCHAIN, TargetBoard.BOARD_CK_RX65N);
		projectModelAmazonCKRX65107 = PGUtility.prepareProjectModel(RTOSType.AMAZONFREERTOS, RTOSVersion.Amazon_202107_1_0_1,
				RTOSApplication.AMAZON_BARE, Constants.GCC_TOOLCHAIN, TargetBoard.BOARD_CK_RX65N);
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
	public void tc_00_setToolchainLocation() throws Exception {
		Utility.setToolchainManagement(robot, "Renesas CC-RX", ProjectParameters.FileLocation.TOOLCHAIN_RENESAS_CCRX_V3_05);
		Utility.reFocus(robot);
	}

	@Test
	public void tc_01_ChangeRTOSLocation() throws Exception{
		Utility.changeModuleDownloadLocation(robot, ProjectParameters.FileLocation.EMPTY_RTOS_LOCATION, 3);
		Utility.changeModuleDownloadLocation(robot, ProjectParameters.FileLocation.NEWEST_FIT_MODULES_LOCATION, 2);
		Utility.reFocus(robot);
	}

	@Test
	public void tc_02_CreateFreeRTOSKernelProjectWithCKRX65NBoard() throws Exception {
		PGUtility.createProject(RTOSType.FREERTOSKERNEL, RTOSVersion.Kernel_1_0_9, RTOSApplication.KERNEL_BARE,
				Constants.GCC_TOOLCHAIN, TargetBoard.BOARD_RSK_RX65N_2MB);
		// verify tooltip
		Utility.openSCFGEditorByProjectName(projectModelKernelRSKRX65N109.getProjectName(), "Components");

		// select FreeRTOS_Object component
		bot.tree(1).getTreeItem(ProjectParameters.FolderAndFile.FOLDER_RTOS)
				.getNode(ProjectParameters.FolderAndFile.FOLDER_RTOS_OBJECT)
				.getNode(ProjectParameters.RTOSComponent.FREERTOS_OBJECT_SPACE).select();

		// check tooltip
		List<Boolean> result = new ArrayList<>();
		for (IVerifyToolTipTask toolTipTask : getVerifyFreeRTOSObjectToolTipTasks()) {
			toolTipTask.initializeObjectTab();
			result.add(Boolean.valueOf(toolTipTask.verifyTooltip(bot.table())));
		}
		assertTrue(!result.contains(Boolean.FALSE));
		Utility.deleteProject(projectModelKernelRSKRX65N109.getProjectName(), false);
	}

	@Test
	public void tc_03_VerifyTooltipOfAmazonFreeRTOSObjectComponent() throws Exception {
		PGUtility.createProject(RTOSType.AMAZONFREERTOS, RTOSVersion.Amazon_202107_1_0_1, RTOSApplication.AMAZON_BARE,
				Constants.GCC_TOOLCHAIN, TargetBoard.BOARD_CK_RX65N);
		Utility.openSCFGEditor(projectModelAmazonCKRX65107, "Components");

		// select FreeRTOS_Object component
		bot.tree(1).getTreeItem(ProjectParameters.FolderAndFile.FOLDER_RTOS)
				.getNode(ProjectParameters.FolderAndFile.FOLDER_RTOS_OBJECT)
				.getNode(ProjectParameters.RTOSComponent.FREERTOS_OBJECT).select();

		// check tooltip
		List<Boolean> result = new ArrayList<>();
		for (IVerifyToolTipTask toolTipTask : getVerifyFreeRTOSObjectToolTipTasks()) {
			toolTipTask.initializeObjectTab();
			result.add(Boolean.valueOf(toolTipTask.verifyTooltip(bot.table())));
		}
		assertTrue(!result.contains(Boolean.FALSE));
		Utility.deleteProject(projectModelAmazonCKRX65107.getProjectName(), false);
	}

	@Test
	public void tc_04_VerifyTooltipOfLTSFreeRTOSObjectComponent() throws Exception {
		PGUtility.createProject(RTOSType.FREERTOSIOTLTS, RTOSVersion.IoTLTS_202210_1_1_3,
				RTOSApplication.IOT_LTS_ETHER_PUBSUB, Constants.CCRX_TOOLCHAIN, TargetBoard.BOARD_CK_RX65N);
		Utility.openSCFGEditorByProjectName(projectModelIOTLTSCKRX65N113.getProjectName(), "Components");

		// select FreeRTOS_Object component
		bot.tree(1).getTreeItem(ProjectParameters.FolderAndFile.FOLDER_RTOS)
				.getNode(ProjectParameters.FolderAndFile.FOLDER_RTOS_OBJECT)
				.getNode(ProjectParameters.RTOSComponent.FREERTOS_OBJECT_SPACE).select();

		// check tooltip
		List<Boolean> result = new ArrayList<>();
		for (IVerifyToolTipTask toolTipTask : getVerifyFreeRTOSObjectToolTipTasks()) {
			toolTipTask.initializeObjectTab();
			result.add(Boolean.valueOf(toolTipTask.verifyTooltip(bot.table())));
		}
		assertTrue(!result.contains(Boolean.FALSE));
		Utility.deleteProject(projectModelIOTLTSCKRX65N113.getProjectName(), false);
	}

	public interface IVerifyToolTipTask {
		void initializeObjectTab();
		boolean verifyTooltip(SWTBotTable tableBot);
	}

	private List<IVerifyToolTipTask> getVerifyFreeRTOSObjectToolTipTasks() {
		List<IVerifyToolTipTask> tasks = new LinkedList<>();
		tasks.add(new VerifyObjectTaskHeaderTooltip());
		tasks.add(new VerifyObjectSemaphoreHeaderTooltip());
		tasks.add(new VerifyObjectQueueHeaderTooltip());
		tasks.add(new VerifyObjectSoftwareTimerHeaderTooltip());
		tasks.add(new VerifyObjectEventGroupHeaderTooltip());
		tasks.add(new VerifyObjectStreamBufferHeaderTooltip());
		tasks.add(new VerifyObjectMessageBufferHeaderTooltip());
		return tasks;
	}

	private class VerifyObjectTaskHeaderTooltip implements IVerifyToolTipTask {
	
		@Override
		public void initializeObjectTab() {
			bot.tabItem(ProjectParameters.KernelObjectTab.TASKS).activate();
		}

		@Override
		public boolean verifyTooltip(SWTBotTable tableBot) {
			List<Boolean> result = new ArrayList<>();
			// retrieve all columns of Object Table
			List<String> columns = tableBot.columns();
			for (String column : columns) {
				if (ADD_REMOVE_OBJECT.equals(column) || HEAP_USAGE.equals(column)) {
					continue;
				}
				if (TASK_INITIALIZE.equals(column)
						&& TASK_INITIALIZE_TOOLTIP.equals(tableBot.header(column).getToolTipText())) {
					result.add(Boolean.TRUE);
				} else if (TASK_CODE.equals(column)
						&& TASK_CODE_TOOLTIP.equals(tableBot.header(column).getToolTipText())) {
					result.add(Boolean.TRUE);
				} else if (TASK_NAME.equals(column)
						&& TASK_NAME_TOOLTIP.equals(tableBot.header(column).getToolTipText())) {
					result.add(Boolean.TRUE);
				} else if (TASK_STACK.equals(column)
						&& TASK_STACK_SIZE_TOOLTIP.equals(tableBot.header(column).getToolTipText())) {
					result.add(Boolean.TRUE);
				} else if (TASK_HANDLER.equals(column)
						&& TASK_HANDLER_TOOLTIP.equals(tableBot.header(column).getToolTipText())) {
					result.add(Boolean.TRUE);
				} else if (TASK_PARAMETER.equals(column)
						&& TASK_PARAMETER_TOOLTIP.equals(tableBot.header(column).getToolTipText())) {
					result.add(Boolean.TRUE);
				} else if (TASK_PRIORITY.equals(column)
						&& TASK_PRIORITY_TOOLTIP.equals(tableBot.header(column).getToolTipText())) {
					result.add(Boolean.TRUE);
				} else {
					result.add(Boolean.FALSE);
				}
			}
			return !result.contains(Boolean.FALSE);
		}
	}

	private class VerifyObjectSemaphoreHeaderTooltip implements IVerifyToolTipTask {

		@Override
		public void initializeObjectTab() {
			bot.tabItem(ProjectParameters.KernelObjectTab.SEMAPHORES).activate();
		}

		@Override
		public boolean verifyTooltip(SWTBotTable tableBot) {
			List<Boolean> result = new ArrayList<>();
			// retrieve all columns of Object Table
			List<String> columns = tableBot.columns();
			for (String column : columns) {
				if (ADD_REMOVE_OBJECT.equals(column) || HEAP_USAGE.equals(column)) {
					continue;
				}
				if (SEMAPHORE_TYPE.equals(column)
						&& SEMAPHORE_TYPE_TOOLTIP.equals(tableBot.header(column).getToolTipText())) {
					result.add(Boolean.TRUE);
				} else if (SEMAPHORE_HANDLER.equals(column)
						&& SEMAPHORE_HANDLER_TOOLTIP.equals(tableBot.header(column).getToolTipText())) {
					result.add(Boolean.TRUE);
				} else {
					result.add(Boolean.FALSE);
				}
			}
			return !result.contains(Boolean.FALSE);
		}
	}

	private class VerifyObjectQueueHeaderTooltip implements IVerifyToolTipTask {

		@Override
		public void initializeObjectTab() {
			bot.tabItem(ProjectParameters.KernelObjectTab.QUEUES).activate();
		}

		@Override
		public boolean verifyTooltip(SWTBotTable tableBot) {
			List<Boolean> result = new ArrayList<>();
			// retrieve all columns of Object Table
			List<String> columns = tableBot.columns();
			for (String column : columns) {
				if (ADD_REMOVE_OBJECT.equals(column) || HEAP_USAGE.equals(column)) {
					continue;
				}
				if (QUEUE_HANDLER.equals(column)
						&& QUEUE_HANDLER_TOOLTIP.equals(tableBot.header(column).getToolTipText())) {
					result.add(Boolean.TRUE);
				} else if (QUEUE_LENGTH.equals(column)
						&& QUEUE_LENGTH_TOOLTIP.equals(tableBot.header(column).getToolTipText())) {
					result.add(Boolean.TRUE);
				} else if (QUEUE_ITEM_SIZE.equals(column)
						&& QUEUE_ITEM_SIZE_TOOLTIP.equals(tableBot.header(column).getToolTipText())) {
					result.add(Boolean.TRUE);
				} else {
					result.add(Boolean.FALSE);
				}
			}
			return !result.contains(Boolean.FALSE);
		}

	}

	private class VerifyObjectSoftwareTimerHeaderTooltip implements IVerifyToolTipTask {

		@Override
		public void initializeObjectTab() {
			bot.tabItem(ProjectParameters.KernelObjectTab.SOFTWARE_TIMERS).activate();
		}

		@Override
		public boolean verifyTooltip(SWTBotTable tableBot) {
			List<Boolean> result = new ArrayList<>();
			// retrieve all columns of Object Table
			List<String> columns = tableBot.columns();
			for (String column : columns) {
				if (ADD_REMOVE_OBJECT.equals(column) || HEAP_USAGE.equals(column)) {
					continue;
				}
				if (SW_TIMER_HANDLER.equals(column)
						&& SW_TIMER_HANDLER_TOOLTIP.equals(tableBot.header(column).getToolTipText())) {
					result.add(Boolean.TRUE);
				} else if (SW_TIMER_NAME.equals(column)
						&& SW_TIMER_NAME_TOOLTIP.equals(tableBot.header(column).getToolTipText())) {
					result.add(Boolean.TRUE);
				} else if (SW_TIMER_PERIOD.equals(column)
						&& SW_TIMER_PERIOD_TOOLTIP.equals(tableBot.header(column).getToolTipText())) {
					result.add(Boolean.TRUE);
				} else if (SW_TIMER_AUTO_LOAD.equals(column)
						&& SW_TIMER_AUTO_LOAD_TOOLTIP.equals(tableBot.header(column).getToolTipText())) {
					result.add(Boolean.TRUE);
				} else if (SW_TIMER_ID.equals(column)
						&& SW_TIMER_ID_TOOLTIP.equals(tableBot.header(column).getToolTipText())) {
					result.add(Boolean.TRUE);
				} else if (SW_TIMER_CALLBACK_FUNCTION.equals(column)
						&& SW_TIMER_CALLBACK_FUNCTION_TOOLTIP.equals(tableBot.header(column).getToolTipText())) {
					result.add(Boolean.TRUE);
				} else {
					result.add(Boolean.FALSE);
				}
			}
			return !result.contains(Boolean.FALSE);
		}

	}

	private class VerifyObjectEventGroupHeaderTooltip implements IVerifyToolTipTask {

		@Override
		public void initializeObjectTab() {
			bot.tabItem(ProjectParameters.KernelObjectTab.EVENT_GROUPS).activate();
		}

		@Override
		public boolean verifyTooltip(SWTBotTable tableBot) {
			List<Boolean> result = new ArrayList<>();
			// retrieve all columns of Object Table
			List<String> columns = tableBot.columns();
			for (String column : columns) {
				if (ADD_REMOVE_OBJECT.equals(column) || HEAP_USAGE.equals(column)) {
					continue;
				}
				if (EVENT_GROUP_HANDLER.equals(column)
						&& EVENT_GROUP_HANDLER_TOOLTIP.equals(tableBot.header(column).getToolTipText())) {
					result.add(Boolean.TRUE);
				} else {
					result.add(Boolean.FALSE);
				}
			}
			return !result.contains(Boolean.FALSE);
		}

	}

	private class VerifyObjectStreamBufferHeaderTooltip implements IVerifyToolTipTask {

		@Override
		public void initializeObjectTab() {
			bot.tabItem(ProjectParameters.KernelObjectTab.STREAM_BUFFERS).activate();
		}

		@Override
		public boolean verifyTooltip(SWTBotTable tableBot) {
			List<Boolean> result = new ArrayList<>();
			// retrieve all columns of Object Table
			List<String> columns = tableBot.columns();
			for (String column : columns) {
				if (ADD_REMOVE_OBJECT.equals(column) || HEAP_USAGE.equals(column)) {
					continue;
				}
				if (STREAM_BUFFER_HANDLER.equals(column)
						&& STREAM_BUFFER_HANDLER_TOOLTIP.equals(tableBot.header(column).getToolTipText())) {
					result.add(Boolean.TRUE);
				} else if (STREAM_BUFFER_SIZE.equals(column)
						&& STREAM_BUFFER_SIZE_TOOLTIP.equals(tableBot.header(column).getToolTipText())) {
					result.add(Boolean.TRUE);
				} else if (STREAM_BUFFER_TRIGGER_LEVEL.equals(column)
						&& STREAM_BUFFER_TRIGGER_LEVEL_TOOLTIP.equals(tableBot.header(column).getToolTipText())) {
					result.add(Boolean.TRUE);
				} else {
					result.add(Boolean.FALSE);
				}
			}
			return !result.contains(Boolean.FALSE);
		}

	}

	private class VerifyObjectMessageBufferHeaderTooltip implements IVerifyToolTipTask {

		@Override
		public void initializeObjectTab() {
			bot.tabItem(ProjectParameters.KernelObjectTab.MESSAGE_BUFFERS).activate();
		}

		@Override
		public boolean verifyTooltip(SWTBotTable tableBot) {
			List<Boolean> result = new ArrayList<>();
			// retrieve all columns of Object Table
			List<String> columns = tableBot.columns();
			for (String column : columns) {
				if (ADD_REMOVE_OBJECT.equals(column) || HEAP_USAGE.equals(column)) {
					continue;
				}
				if (MESSAGE_BUFFER_HANDLER.equals(column)
						&& MESSAGE_BUFFER_HANDLER_TOOLTIP.equals(tableBot.header(column).getToolTipText())) {
					result.add(Boolean.TRUE);
				} else if (MESSAGE_BUFFER_SIZE.equals(column)
						&& MESSAGE_BUFFER_SIZE_TOOLTIP.equals(tableBot.header(column).getToolTipText())) {
					result.add(Boolean.TRUE);
				} else {
					result.add(Boolean.FALSE);
				}
			}
			return !result.contains(Boolean.FALSE);
		}

	}
}
