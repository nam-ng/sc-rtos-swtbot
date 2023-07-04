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
public class kernelGCCCheckContrains {
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
		projectModelSpecific = PGUtility.prepareProjectModel(RTOSType.FREERTOSKERNEL, RTOSVersion.Kernel_1_0_7, RTOSApplication.KERNEL_BARE, Constants.GCC_TOOLCHAIN, TargetBoard.BOARD_RSK_RX65N_2MB);
	}
	
	@Test
	public void tc_01_CreateKernelProject() throws Exception{
		PGUtility.createProject(RTOSType.FREERTOSKERNEL, RTOSVersion.Kernel_1_0_7, RTOSApplication.KERNEL_BARE, Constants.GCC_TOOLCHAIN, TargetBoard.BOARD_RSK_RX65N_2MB);
		
	}
	
	@Test
	public void tc_02_TwoRowsDuplicate() throws Exception{
		Utility.openSCFGEditor(projectModelSpecific);
		
		bot.tree(1).getTreeItem(ProjectParameters.FolderAndFile.FOLDER_RTOS)
		.getNode(ProjectParameters.FolderAndFile.FOLDER_RTOS_OBJECT)
		.getNode(ProjectParameters.RTOSComponent.FREERTOS_OBJECT).select();
		
		bot.tabItem("Tasks").activate();
		Utility.addOrRemoveKernelObject(true);
		Utility.addOrRemoveKernelObject(true);
        
        Utility.clickClearConsole();
        
        bot.sleep(3000);
        bot.text(1+7*0).setText("task_1");
        bot.text(1+7*1).setText("task_1");
        
		boolean check1 = Utility.isConsoleHasString("E04050007: This name exists. Please use another name");
		
        Utility.clickClearConsole();

		
        bot.text(1+7*1).setText("task_2");
        bot.text(2+7*0).setText("task_1");
        bot.text(2+7*1).setText("task_1");

		
		boolean check2 = Utility.isConsoleHasString("E04050007: This name exists. Please use another name");
		
        Utility.clickClearConsole();

        Utility.addOrRemoveKernelObject(false);
        Utility.addOrRemoveKernelObject(false);
        
        if (!check1||!check2) {
        	assertFalse(true);
        }

	}
	
	@Test
	public void tc_03_EmptyError() throws Exception{
		
		Utility.addOrRemoveKernelObject(true);
        
        bot.sleep(3000);
        bot.text(1+7*0).setText("");
        
		boolean check1 = Utility.isConsoleHasString("E04050004: The value must not be empty");
		
		Utility.addOrRemoveKernelObject(false);
        
        Utility.clickClearConsole();
        
        if (!check1) {
        	assertFalse(true);
        }
	}
	
	@Test
	public void tc_04_MustBeANumber() throws Exception{
		Utility.addOrRemoveKernelObject(true);
        
        bot.sleep(3000);
        bot.text(3+7*0).setText("a");
        
		boolean check1 = Utility.isConsoleHasString("E04050002: The value must be a number");
		
        bot.text(3+7*0).setText("512");
        
        Utility.clickClearConsole();
        
        bot.text(6+7*0).setText("a");
        
		boolean check2 = Utility.isConsoleHasString("E04050002: The value must be a number");
		
        bot.text(3+7*0).setText("1");
        
        Utility.clickClearConsole();
        
        if (!check1||!check2) {
        	assertFalse(true);
        }
	}
	
	@Test
	public void tc_05_MustBeANumber2() throws Exception{
		bot.tabItem("Queues").activate();
		
		Utility.addOrRemoveKernelObject(true);
		
        bot.text(2).setText("a");

		boolean check1 = Utility.isConsoleHasString("E04050002: The value must be a number");
		
        bot.text(2).setText("100");
        
        Utility.clickClearConsole();
        
        if (!check1) {
        	assertFalse(true);
        }
	}
	
	@Test
	public void tc_06_MustBeANumber3() throws Exception{
		bot.tabItem("Software Timers").activate();
		
		Utility.addOrRemoveKernelObject(true);
        
        bot.text(3).setText("a");
        
		boolean check1 = Utility.isConsoleHasString("E04050002: The value must be a number");
		
        bot.text(3).setText("100");
        
        Utility.clickClearConsole();
        
        
        bot.text(4).setText("a");
        
		boolean check2 = Utility.isConsoleHasString("E04050002: The value must be a number");
		
        bot.text(4).setText("0");
        
        Utility.clickClearConsole();
        
        if (!check1||!check2) {
        	assertFalse(true);
        }
	}
	
	@Test
	public void tc_07_MustBeANumber4() throws Exception{
		bot.tabItem("Stream Buffers").activate();
		
		Utility.addOrRemoveKernelObject(true);
        
        bot.text(2).setText("a");
        
		boolean check1 = Utility.isConsoleHasString("E04050002: The value must be a number");
		
        bot.text(2).setText("100");
        
        Utility.clickClearConsole();
        
        
        bot.text(3).setText("a");
        
		boolean check2 = Utility.isConsoleHasString("E04050002: The value must be a number");
		
        bot.text(3).setText("10");
        
        Utility.clickClearConsole();
        
        if (!check1||!check2) {
        	assertFalse(true);
        }
	}
	
	@Test
	public void tc_08_MustBeANumber5() throws Exception{
		bot.tabItem("Message Buffers").activate();
		
		Utility.addOrRemoveKernelObject(true);
		
        bot.text(2).setText("a");

		boolean check1 = Utility.isConsoleHasString("E04050002: The value must be a number");
		
        bot.text(2).setText("100");
        
        Utility.clickClearConsole();
        
        if (!check1) {
        	assertFalse(true);
        }
	}
	
	@Test
	public void tc_09_MustNotBeANumber() throws Exception {
		bot.tabItem("Tasks").activate();

		bot.sleep(3000);
		bot.text(1).setText("1");
		
		boolean check1 = Utility.isConsoleHasString("E04050001: The first character must not be a digit");
		boolean check2 = Utility.isConsoleHasString("E04050003: The value must not be a number");

		bot.text(1).setText("task_4");
        Utility.clickClearConsole();

		bot.text(4).setText("1");

		boolean check3 = Utility.isConsoleHasString("E04050001: The first character must not be a digit");
		boolean check4 = Utility.isConsoleHasString("E04050003: The value must not be a number");

		bot.text(4).setText("NULL");
        Utility.clickClearConsole();

		if (!check1 || !check2 || !check3 || !check4) {
			assertFalse(true);
		}
	}
	
	@Test
	public void tc_10_OutOfSize2() throws Exception{
		bot.tabItem("Queues").activate();
		
        bot.text(2).setText("4294967296");

		boolean check1 = Utility.isConsoleHasString("E04050006: The value must be from 1 to 4294967295");
		
        bot.text(2).setText("100");
        
        Utility.clickClearConsole();
        
        if (!check1) {
        	assertFalse(true);
        }
	}
	
	@Test
	public void tc_11_OutOfSize3() throws Exception{
		bot.tabItem("Software Timers").activate();
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
	public void tc_12_OutOfSize4() throws Exception{
		bot.tabItem("Stream Buffers").activate();
        
        bot.text(2).setText("4294967296");
        
		boolean check1 = Utility.isConsoleHasString("E04050006: The value must be from 1 to 4294967295");
		
        bot.text(2).setText("100");
        
        Utility.clickClearConsole();
        
        
        bot.text(3).setText("4294967296");
        
		boolean check2 = Utility.isConsoleHasString("E04050006: The value must be from 1 to 4294967295");

		
        bot.text(3).setText("10");
        
        Utility.clickClearConsole();
        
        if (!check1||!check2) {
        	assertFalse(true);
        }
	}
	
	@Test
	public void tc_13_OutOfSize5() throws Exception{
		bot.tabItem("Message Buffers").activate();
		
        bot.text(2).setText("4294967296");

		boolean check1 = Utility.isConsoleHasString("E04050006: The value must be from 1 to 4294967295");

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
