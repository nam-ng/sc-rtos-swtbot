package legacytestsuites;

import static org.junit.Assert.assertFalse;

import java.awt.Robot;
import java.io.File;
import java.util.List;

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
public class ObjectGUI {
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
		projectModelSpecific = PGUtility.prepareProjectModel(RTOSType.AMAZONFREERTOS, RTOSVersion.Amazon_202107_1_0_1,
				RTOSApplication.AMAZON_BARE, Constants.CCRX_TOOLCHAIN, TargetBoard.BOARD_CK_RX65N);
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
	public void tc_01_CreateAmazonProject() throws Exception {
		PGUtility.createProject(RTOSType.AMAZONFREERTOS, RTOSVersion.Amazon_202107_1_0_1, RTOSApplication.AMAZON_BARE,
				Constants.CCRX_TOOLCHAIN, TargetBoard.BOARD_CK_RX65N);

	}
	
	@Test
	public void tc_02_CheckEventGroupUI() throws Exception {
		Utility.openSCFGEditor(projectModelSpecific, ProjectParameters.SCFG_COMPONENT_TAB);

		bot.tree(1).getTreeItem(ProjectParameters.FolderAndFile.FOLDER_RTOS)
				.getNode(ProjectParameters.FolderAndFile.FOLDER_RTOS_OBJECT)
				.getNode(ProjectParameters.RTOSComponent.FREERTOS_OBJECT).select();
		bot.sleep(2000);

		bot.tabItem(ProjectParameters.KernelObjectTab.EVENT_GROUPS).activate();
		boolean isEventGroupsDisplayCorrectly = false;
		List<String> columnsList = bot.table(0).columns();
		if (columnsList.get(0).equals(ProjectParameters.KernelObjectTableColumn.PLUS_MINUS)
				&& columnsList.get(1).equals(ProjectParameters.KernelObjectTableColumn.EVENT_GROUP_HANDLER)) {
			isEventGroupsDisplayCorrectly = true;
		}

		if (!isEventGroupsDisplayCorrectly) {
			assertFalse(true);
		}
	}
	
	@Test
	public void tc_03_CheckMsgBufferUI() throws Exception {
		bot.sleep(2000);

		bot.tabItem(ProjectParameters.KernelObjectTab.MESSAGE_BUFFERS).activate();

		boolean isMsgBufferDisplayCorrectly = false;
		List<String> columnsList = bot.table(0).columns();
		if (columnsList.get(0).equals(ProjectParameters.KernelObjectTableColumn.PLUS_MINUS)
				&& columnsList.get(1).equals(ProjectParameters.KernelObjectTableColumn.MSG_BUFFER_HANDLER)
				&& columnsList.get(2).equals(ProjectParameters.KernelObjectTableColumn.MSG_BUFFER_SIZE)) {
			isMsgBufferDisplayCorrectly = true;
		}

		if (!isMsgBufferDisplayCorrectly) {
			assertFalse(true);
		}
	}
	
	@Test
	public void tc_04_CheckQueueUI() throws Exception {
		bot.sleep(2000);

		bot.tabItem(ProjectParameters.KernelObjectTab.QUEUES).activate();

		boolean isQueueDisplayCorrectly = false;
		List<String> columnsList = bot.table(0).columns();
		if (columnsList.get(0).equals(ProjectParameters.KernelObjectTableColumn.PLUS_MINUS)
				&& columnsList.get(1).equals(ProjectParameters.KernelObjectTableColumn.QUEUE_HANDLER)
				&& columnsList.get(2).equals(ProjectParameters.KernelObjectTableColumn.QUEUE_LENGTH)
				&& columnsList.get(3).equals(ProjectParameters.KernelObjectTableColumn.ITEMS_SIZE)) {
			isQueueDisplayCorrectly = true;
		}

		if (!isQueueDisplayCorrectly) {
			assertFalse(true);
		}
	}
	
	@Test
	public void tc_05_CheckSemaphoreUI() throws Exception {
		bot.sleep(2000);

		bot.tabItem(ProjectParameters.KernelObjectTab.SEMAPHORES).activate();

		boolean isSemaphoreDisplayCorrectly = false;
		List<String> columnsList = bot.table(0).columns();
		if (columnsList.get(0).equals(ProjectParameters.KernelObjectTableColumn.PLUS_MINUS)
				&& columnsList.get(1).equals(ProjectParameters.KernelObjectTableColumn.SEMAPHORE_TYPE)
				&& columnsList.get(2).equals(ProjectParameters.KernelObjectTableColumn.SEMAPHORE_HANDLER)) {
			isSemaphoreDisplayCorrectly = true;
		}

		if (!isSemaphoreDisplayCorrectly) {
			assertFalse(true);
		}
	}
	
	@Test
	public void tc_06_CheckStreamBufferUI() throws Exception {
		bot.sleep(2000);

		bot.tabItem(ProjectParameters.KernelObjectTab.STREAM_BUFFERS).activate();

		boolean isStreamBufferDisplayCorrectly = false;
		List<String> columnsList = bot.table(0).columns();
		if (columnsList.get(0).equals(ProjectParameters.KernelObjectTableColumn.PLUS_MINUS)
				&& columnsList.get(1).equals(ProjectParameters.KernelObjectTableColumn.STREAM_BUFFER_HANDLER)
				&& columnsList.get(2).equals(ProjectParameters.KernelObjectTableColumn.STREAM_BUFFER_SIZE)
				&& columnsList.get(3).equals(ProjectParameters.KernelObjectTableColumn.TRIGGER_LEVEL)) {
			isStreamBufferDisplayCorrectly = true;
		}

		if (!isStreamBufferDisplayCorrectly) {
			assertFalse(true);
		}
	}
	
	@Test
	public void tc_07_CheckSWTimerUI() throws Exception {
		bot.sleep(2000);

		bot.tabItem(ProjectParameters.KernelObjectTab.SOFTWARE_TIMERS).activate();

		boolean isSWTimerDisplayCorrectly = false;
		List<String> columnsList = bot.table(0).columns();
		if (columnsList.get(0).equals(ProjectParameters.KernelObjectTableColumn.PLUS_MINUS)
				&& columnsList.get(1).equals(ProjectParameters.KernelObjectTableColumn.SWTIMER_HANDLER)
				&& columnsList.get(2).equals(ProjectParameters.KernelObjectTableColumn.SWTIMER_NAME)
				&& columnsList.get(3).equals(ProjectParameters.KernelObjectTableColumn.SWTIMER_PERIOD)
				&& columnsList.get(4).equals(ProjectParameters.KernelObjectTableColumn.AUTO_RELOAD)
				&& columnsList.get(5).equals(ProjectParameters.KernelObjectTableColumn.SWTIMER_ID)
				&& columnsList.get(6).equals(ProjectParameters.KernelObjectTableColumn.CALLBACK_FUNCTION)) {
			isSWTimerDisplayCorrectly = true;
		}

		if (!isSWTimerDisplayCorrectly) {
			assertFalse(true);
		}
	}
	
	@Test
	public void tc_08_CheckTaskUI() throws Exception {
		bot.sleep(2000);

		bot.tabItem(ProjectParameters.KernelObjectTab.TASKS).activate();

		boolean isTaskDisplayCorrectly = false;
		List<String> columnsList = bot.table(0).columns();
		if (columnsList.get(0).equals(ProjectParameters.KernelObjectTableColumn.PLUS_MINUS)
				&& columnsList.get(1).equals(ProjectParameters.KernelObjectTableColumn.INITIALIZE)
				&& columnsList.get(2).equals(ProjectParameters.KernelObjectTableColumn.TASK_CODE)
				&& columnsList.get(3).equals(ProjectParameters.KernelObjectTableColumn.TASK_NAME)
				&& columnsList.get(4).equals(ProjectParameters.KernelObjectTableColumn.STACK_SIZE)
				&& columnsList.get(5).equals(ProjectParameters.KernelObjectTableColumn.TASK_HANDLER)
				&& columnsList.get(6).equals(ProjectParameters.KernelObjectTableColumn.PARAMETER)
				&& columnsList.get(7).equals(ProjectParameters.KernelObjectTableColumn.PRIORITY)) {
			isTaskDisplayCorrectly = true;
		}

		if (!isTaskDisplayCorrectly) {
			assertFalse(true);
		}
	}
	
	@Test
	public void tc_09_SwitchComponentAndCheckUI() throws Exception {
		bot.sleep(2000);

		bot.tree(1).getTreeItem(ProjectParameters.FolderAndFile.FOLDER_RTOS)
				.getNode(ProjectParameters.FolderAndFile.FOLDER_RTOS_OBJECT)
				.getNode(ProjectParameters.RTOSComponent.FREERTOS_OBJECT).select();

		bot.tree(1).getTreeItem(ProjectParameters.FolderAndFile.FOLDER_RTOS)
				.getNode(ProjectParameters.FolderAndFile.FOLDER_RTOS_KERNEL)
				.getNode(ProjectParameters.RTOSComponent.FREERTOS_KERNEL).select();
		bot.sleep(2000);

		bot.tree(1).getTreeItem(ProjectParameters.FolderAndFile.FOLDER_RTOS)
				.getNode(ProjectParameters.FolderAndFile.FOLDER_RTOS_OBJECT)
				.getNode(ProjectParameters.RTOSComponent.FREERTOS_OBJECT).select();

		boolean isSevenTabsDisplay = false;
		if (bot.tabItem(ProjectParameters.KernelObjectTab.TASKS).isVisible()
				&& bot.tabItem(ProjectParameters.KernelObjectTab.SEMAPHORES).isVisible()
				&& bot.tabItem(ProjectParameters.KernelObjectTab.QUEUES).isVisible()
				&& bot.tabItem(ProjectParameters.KernelObjectTab.SOFTWARE_TIMERS).isVisible()
				&& bot.tabItem(ProjectParameters.KernelObjectTab.EVENT_GROUPS).isVisible()
				&& bot.tabItem(ProjectParameters.KernelObjectTab.STREAM_BUFFERS).isVisible()
				&& bot.tabItem(ProjectParameters.KernelObjectTab.MESSAGE_BUFFERS).isVisible()) {
			isSevenTabsDisplay = true;
		}

		if (!isSevenTabsDisplay) {
			assertFalse(true);
		}
	}
	
	@Test
	public void tc_10_DeleteAmazonProject() throws Exception {
		Utility.deleteProject(projectModelSpecific.getProjectName(), true);
		if (bot.activeShell().getText().equals(ProjectParameters.WINDOW_SAVE_RESOURCES)) {
			bot.button(ButtonAction.BUTTON_DONT_SAVE).click();
		}
	}
}
