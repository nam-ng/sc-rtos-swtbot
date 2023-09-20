package legacytestsuites;

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
public class ObjectGUICheckConstraints {
	private static SWTWorkbenchBot bot;
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
	}

	@Test
	public void tc_01_CreateAmazonProject() throws Exception {
		PGUtility.createProject(RTOSType.AMAZONFREERTOS, RTOSVersion.Amazon_202107_1_0_1, RTOSApplication.AMAZON_BARE,
				Constants.CCRX_TOOLCHAIN, TargetBoard.BOARD_CK_RX65N);

	}
	@Test
	public void tc_02_MustNotBeANumber() throws Exception {
		Utility.openSCFGEditor(projectModelSpecific, ProjectParameters.SCFG_COMPONENT_TAB);

		bot.tree(1).getTreeItem(ProjectParameters.FolderAndFile.FOLDER_RTOS)
				.getNode(ProjectParameters.FolderAndFile.FOLDER_RTOS_OBJECT)
				.getNode(ProjectParameters.RTOSComponent.FREERTOS_OBJECT).select();
		
		bot.tabItem(ProjectParameters.KernelObjectTab.TASKS).activate();
		Utility.clickClearConsole();
		Utility.addOrRemoveKernelObject(true, 0);

		bot.sleep(3000);
		bot.text(1).setText("1");

		boolean check1 = Utility.isConsoleHasString(ProjectParameters.MessageCode.E04050001);
		boolean check2 = Utility.isConsoleHasString(ProjectParameters.MessageCode.E04050003);

		bot.text(1).setText("task_1");
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
	public void tc_03_OutOfSizeQueues() throws Exception {
		bot.tabItem(ProjectParameters.KernelObjectTab.QUEUES).activate();
		Utility.addOrRemoveKernelObject(true, 0);

		bot.text(2).setText("4294967296");

		boolean check1 = Utility.isConsoleHasString(ProjectParameters.MessageCode.E04050006);

		bot.text(2).setText("100");

		Utility.clickClearConsole();

		if (!check1) {
			assertFalse(true);
		}
	}

	@Test
	public void tc_04_OutOfSizeSWTimer() throws Exception {
		bot.tabItem(ProjectParameters.KernelObjectTab.SOFTWARE_TIMERS).activate();
		Utility.addOrRemoveKernelObject(true, 0);
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
	public void tc_05_ParameterMustNotBeADigit() throws Exception{
		bot.tabItem(ProjectParameters.KernelObjectTab.TASKS).activate();
		
		bot.text(5).setText("1");
		
		boolean check1 = Utility.isConsoleHasString(ProjectParameters.MessageCode.E04050001);
		boolean check2 = Utility.isConsoleHasString(ProjectParameters.MessageCode.E04050003);

		bot.text(5).setText("NULL");
		Utility.clickClearConsole();
		
		if (!check1 || !check2) {
			assertFalse(true);
		}
	}
	
	@Test
	public void tc_06_RemovedDuplicatedValues() throws Exception {
		bot.tabItem(ProjectParameters.KernelObjectTab.TASKS).activate();
		
		Utility.addOrRemoveKernelObject(true, 0);
		Utility.addOrRemoveKernelObject(true, 0);
		
		bot.text(1+6*1).setText("task_1");
		boolean check1 = Utility.isConsoleHasString(ProjectParameters.MessageCode.E04050007);
		bot.text(1+6*1).setText("task_2");

		bot.sleep(2000);
		Utility.clickClearConsole();
		
		bot.text(1+6*2).setText("task_1");
		boolean check2 = Utility.isConsoleHasString(ProjectParameters.MessageCode.E04050007);
		bot.text(1+6*2).setText("task_3");

		bot.sleep(2000);
		Utility.clickClearConsole();
		
		bot.text(2+6*1).setText("task_1");
		boolean check3 = Utility.isConsoleHasString(ProjectParameters.MessageCode.E04050007);
		bot.text(2+6*1).setText("task_2");

		bot.sleep(2000);
		Utility.clickClearConsole();

		bot.text(2+6*2).setText("task_1");
		boolean check4 = Utility.isConsoleHasString(ProjectParameters.MessageCode.E04050007);
		bot.text(2+6*2).setText("task_3");

		bot.sleep(2000);
		Utility.clickClearConsole();
		
		bot.text(4+6*0).setText("test");
		bot.text(4+6*1).setText("test");
		boolean check5 = Utility.isConsoleHasString(ProjectParameters.MessageCode.E04050007);
		bot.text(4+6*1).setText("NULL");

		bot.sleep(2000);
		Utility.clickClearConsole();
		
		bot.text(4+6*2).setText("test");
		boolean check6 = Utility.isConsoleHasString(ProjectParameters.MessageCode.E04050007);
		bot.text(4+6*2).setText("NULL");

		bot.sleep(2000);
		Utility.clickClearConsole();
		
		if (!check1 || !check2 || !check3 || !check4 || !check5 || !check6) {
			assertFalse(true);
		}
	}
	
	@Test
	public void tc_07_DeleteAmazonProject() throws Exception {
		Utility.deleteProject(projectModelSpecific.getProjectName(), true);
		if (bot.activeShell().getText().equals(ProjectParameters.WINDOW_SAVE_RESOURCES)) {
			bot.button(ButtonAction.BUTTON_DONT_SAVE).click();
		}
	}
}
