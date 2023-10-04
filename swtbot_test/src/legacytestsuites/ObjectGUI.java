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
		Utility.CheckEventGroupGUI(projectModelSpecific, false);
	}
	
	@Test
	public void tc_03_CheckMsgBufferUI() throws Exception {
		Utility.CheckMsgBufferGUI();
	}
	
	@Test
	public void tc_04_CheckQueueUI() throws Exception {
		Utility.CheckQueueGUI();
	}
	
	@Test
	public void tc_05_CheckSemaphoreUI() throws Exception {
		Utility.CheckSemaphoreGUI();
	}
	
	@Test
	public void tc_06_CheckStreamBufferUI() throws Exception {
		Utility.CheckStreamBufferGUI();
	}
	
	@Test
	public void tc_07_CheckSWTimerUI() throws Exception {
		Utility.checkSWTimerGUI();
	}
	
	@Test
	public void tc_08_CheckTaskUI() throws Exception {
		Utility.checkTaskGUI();
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
