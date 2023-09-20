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
public class MessageConsole {
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
	public void tc_02_CheckConsoleOutput() throws Exception {
		Utility.openSCFGEditor(projectModelSpecific, ProjectParameters.SCFG_COMPONENT_TAB);
		bot.tree(1).getTreeItem(ProjectParameters.FolderAndFile.FOLDER_RTOS)
		.getNode(ProjectParameters.FolderAndFile.FOLDER_RTOS_OBJECT)
		.getNode(ProjectParameters.RTOSComponent.FREERTOS_OBJECT).select();
		bot.tabItem(ProjectParameters.KernelObjectTab.TASKS).activate();
		Utility.addOrRemoveKernelObject(true, 0);
		Utility.clickClearConsole();
		Utility.clickGenerateCode();
		bot.sleep(5000);
		boolean check1 = Utility.isConsoleHasString("M04050011: File modified:config_files\\FreeRTOSConfig.h");
		boolean check2 = Utility.isConsoleHasString("M04050011: File modified:config_files\\FreeRTOSIPConfig.h");
		boolean check3 = Utility.isConsoleHasString("M04050001: File generated:application_code\\renesas_code\\frtos_startup\\freertos_object_init.c");
		boolean check4 = Utility.isConsoleHasString("M04050001: File generated:application_code\\renesas_code\\frtos_skeleton\\task_function.h");
		boolean check5 = Utility.isConsoleHasString("M04050001: File generated:application_code\\renesas_code\\frtos_skeleton\\task_1.c");
		
		if (!check1 || !check2 || !check3 || !check4 || !check5) {
			assertFalse(true);
		}

	}
	
	@Test
	public void tc_03_CheckConsoleOutput() throws Exception {
		bot.tabItem(ProjectParameters.KernelObjectTab.TASKS).activate();
		Utility.addOrRemoveKernelObject(false, 0);
		Utility.clickClearConsole();
		Utility.clickGenerateCode();
		bot.sleep(5000);
		boolean check1 = Utility.isConsoleHasString("M04050011: File modified:config_files\\FreeRTOSIPConfig.h");
		boolean check2 = Utility.isConsoleHasString("M04050001: File generated:application_code\\renesas_code\\frtos_startup\\freertos_object_init.c");
		boolean check3 = Utility.isConsoleHasString("M04050001: File generated:application_code\\renesas_code\\frtos_skeleton\\task_function.h");
		
		if (!check1 || !check2 || !check3) {
			assertFalse(true);
		}

	}
	
	@Test
	public void tc_04_DeleteAmazonProject() throws Exception {
		Utility.deleteProject(projectModelSpecific.getProjectName(), true);
		if (bot.activeShell().getText().equals(ProjectParameters.WINDOW_SAVE_RESOURCES)) {
			bot.button(ButtonAction.BUTTON_DONT_SAVE).click();
		}
	}
}
