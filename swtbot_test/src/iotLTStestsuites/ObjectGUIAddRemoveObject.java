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
public class ObjectGUIAddRemoveObject {
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
		projectModelSpecific = PGUtility.prepareProjectModel(RTOSType.FREERTOSIOTLTS, RTOSVersion.IoTLTS_202210_1_1_3,
				RTOSApplication.IOT_LTS_ETHER_PUBSUB, Constants.CCRX_TOOLCHAIN, TargetBoard.BOARD_CK_RX65N);
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
		PGUtility.createProject(RTOSType.FREERTOSIOTLTS, RTOSVersion.IoTLTS_202210_1_1_3, RTOSApplication.IOT_LTS_ETHER_PUBSUB,
				Constants.CCRX_TOOLCHAIN, TargetBoard.BOARD_CK_RX65N);
	}
	
	@Test
	public void tc_02_CheckEventGroups() throws Exception {
		Utility.openSCFGEditor(projectModelSpecific, ProjectParameters.SCFG_COMPONENT_TAB);

		bot.tree(1).getTreeItem(ProjectParameters.FolderAndFile.FOLDER_RTOS)
				.getNode(ProjectParameters.FolderAndFile.FOLDER_RTOS_OBJECT)
				.getNode(ProjectParameters.RTOSComponent.IOT_LTS_FREERTOS_OBJECT).select();
		
		Utility.checkEventGroups();
	}
	
	@Test
	public void tc_03_checkMsgBuffer() throws Exception {
		Utility.checkMsgBuffer();
	}
	
	@Test
	public void tc_04_checkQueueUI() throws Exception {
		Utility.checkQueueUI();
	}

	@Test
	public void tc_05_checkSemaphoreUI() throws Exception {
		Utility.checkSemaphoresUI();
	}
	
	@Test
	public void tc_06_checkSWTimerUI() throws Exception {
		Utility.checkSWTimerUI();
	}
	
	@Test
	public void tc_07_checkStreamBufferUI() throws Exception {
		Utility.checkStreamBufferUI();
	}
	
	@Test
	public void tc_08_checkTaskUI() throws Exception {
		bot.tabItem(ProjectParameters.KernelObjectTab.TASKS).activate();
		Utility.addOrRemoveKernelObject(true, 0);

		boolean isTasksObjectDisplayCorrectly = false;
		if (bot.ccomboBox(1).getText().equals(ProjectParameters.KernelObject.KERNEL_START)
				&& bot.text(7).getText().equals(ProjectParameters.KernelObject.TASK_2)
				&& bot.text(8).getText().equals(ProjectParameters.KernelObject.TASK_2)
				&& bot.text(9).getText().equals(ProjectParameters.KernelObject.NUMBER_512)
				&& bot.text(10).getText().equals(ProjectParameters.KernelObject.NULL)
				&& bot.text(11).getText().equals(ProjectParameters.KernelObject.NULL)
				&& bot.text(12).getText().equals(ProjectParameters.KernelObject.NUMBER_1)) {
			isTasksObjectDisplayCorrectly = true;
		}

		if (!isTasksObjectDisplayCorrectly) {
			assertFalse(true);
		}
	}
	
	@Test
	public void tc_09_checkSecondRowTaskUI() throws Exception {
		Utility.addOrRemoveKernelObject(true, 0);
		boolean isTasksObjectDisplayCorrectly = false;
		if (bot.ccomboBox(1).getText().equals(ProjectParameters.KernelObject.KERNEL_START)
				&& bot.text(1 + 6 * 1).getText().equals(ProjectParameters.KernelObject.TASK_2)
				&& bot.text(2 + 6 * 1).getText().equals(ProjectParameters.KernelObject.TASK_2)
				&& bot.text(3 + 6 * 1).getText().equals(ProjectParameters.KernelObject.NUMBER_512)
				&& bot.text(4 + 6 * 1).getText().equals(ProjectParameters.KernelObject.NULL)
				&& bot.text(5 + 6 * 1).getText().equals(ProjectParameters.KernelObject.NULL)
				&& bot.text(6 + 6 * 1).getText().equals(ProjectParameters.KernelObject.NUMBER_1)) {
			isTasksObjectDisplayCorrectly = true;
		}

		if (!isTasksObjectDisplayCorrectly) {
			assertFalse(true);
		}
	}
	
	@Test
	public void tc_10_checkThirdRowTaskUI() throws Exception {
		Utility.addOrRemoveKernelObject(true, 0);
		boolean isTasksObjectDisplayCorrectly = false;
		if (bot.ccomboBox(2).getText().equals(ProjectParameters.KernelObject.KERNEL_START)
				&& bot.text(1 + 6 * 2).getText().equals(ProjectParameters.KernelObject.TASK_3)
				&& bot.text(2 + 6 * 2).getText().equals(ProjectParameters.KernelObject.TASK_3)
				&& bot.text(3 + 6 * 2).getText().equals(ProjectParameters.KernelObject.NUMBER_512)
				&& bot.text(4 + 6 * 2).getText().equals(ProjectParameters.KernelObject.NULL)
				&& bot.text(5 + 6 * 2).getText().equals(ProjectParameters.KernelObject.NULL)
				&& bot.text(6 + 6 * 2).getText().equals(ProjectParameters.KernelObject.NUMBER_1)) {
			isTasksObjectDisplayCorrectly = true;
		}

		if (!isTasksObjectDisplayCorrectly) {
			assertFalse(true);
		}
	}
	
	@Test
	public void tc_11_DeleteIoTLTSProject() throws Exception {
		Utility.deleteProject(projectModelSpecific.getProjectName(), true);
		if (bot.activeShell().getText().equals(ProjectParameters.WINDOW_SAVE_RESOURCES)) {
			bot.button(ButtonAction.BUTTON_DONT_SAVE).click();
		}
	}
}
