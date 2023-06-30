package kerneltestsuites;

import static org.junit.Assert.assertFalse;

import java.io.File;

import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotView;
import org.eclipse.swtbot.swt.finder.matchers.WidgetMatcherFactory;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotCanvas;
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
public class kernelCCRXCheckMessageConsole {
	private static SWTWorkbenchBot bot;
	private static ProjectModel projectModelSpecific = new ProjectModel();
	private static final String PLATFORM_XML_FILE = "xml/platformdata.xml";
	private static final String RTOS_PG_XML_FILE = "xml/rtospg.xml";
	
	@BeforeClass
	public static void beforeClass() throws Exception {
		bot = new SWTWorkbenchBot();
		PlatformModel.loadPlatformModel(new File(Utility.getBundlePath(LogUtil.PLUGIN_ID, PLATFORM_XML_FILE)));
		RTOSManager.loadRTOSModel(new File(Utility.getBundlePath(LogUtil.PLUGIN_ID, RTOS_PG_XML_FILE)));
		projectModelSpecific = PGUtility.prepareProjectModel(RTOSType.FREERTOSKERNEL, RTOSVersion.Kernel_1_0_7, RTOSApplication.KERNEL_BARE, Constants.CCRX_TOOLCHAIN, TargetBoard.BOARD_RSK_RX65N_2MB);
	}
	
	@Test
	public void tc_01_CreateKernelProject() throws Exception{
		PGUtility.createProject(RTOSType.FREERTOSKERNEL, RTOSVersion.Kernel_1_0_7, RTOSApplication.KERNEL_BARE, Constants.CCRX_TOOLCHAIN, TargetBoard.BOARD_RSK_RX65N_2MB);
		
	}
	
	@Test
	public void tc_02_CheckConsoleAfterCreate() throws Exception{
		Utility.openSCFGEditor(projectModelSpecific);
		
		SWTBotView consoleView = bot.viewById("org.eclipse.ui.console.ConsoleView");
		boolean isConsoleContains1 = false;
		isConsoleContains1 = Utility.isConsoleHasString("M04050011: File modified:src\\frtos_config\\FreeRTOSConfig.h");
		
		bot.tree(1).getTreeItem(ProjectParameters.FolderAndFile.FOLDER_RTOS)
		.getNode(ProjectParameters.FolderAndFile.FOLDER_RTOS_OBJECT)
		.getNode(ProjectParameters.RTOSComponent.FREERTOS_OBJECT).select();
		bot.tabItem("Tasks").activate();
		Utility.addOrRemoveKernelObject(true);
        
		Utility.clickClearConsole();        
        Utility.clickGenerateCode();
        boolean isConsoleContains2 = false;
        boolean isConsoleContains3 = false;
        boolean isConsoleContains4 = false;
		isConsoleContains2 = Utility.isConsoleHasString("M04050001: File generated:src\\frtos_startup\\freertos_object_init.c");
		isConsoleContains3 = Utility.isConsoleHasString("M04050001: File generated:src\\frtos_skeleton\\task_function.h");
		isConsoleContains4 = Utility.isConsoleHasString("M04050001: File generated:src\\frtos_skeleton\\task_1.c");
		
        
        if (!isConsoleContains1 || !isConsoleContains2 || !isConsoleContains3 || !isConsoleContains4) {
        	assertFalse(true);
        }
	}
	@Test
	public void tc_03_DeleteKernelProject() throws Exception{
		Utility.deleteProject(projectModelSpecific.getProjectName(), true);
		if (bot.activeShell().getText().equals(ProjectParameters.WINDOW_SAVE_RESOURCES)) {
			bot.button(ButtonAction.BUTTON_DONT_SAVE).click();
		}
	}
}
