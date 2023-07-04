package kerneltestsuites;

import static org.junit.Assert.assertFalse;

import java.io.File;

import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
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
public class kernelGCCAddRemoveObject {
	private static SWTWorkbenchBot bot;
	private static ProjectModel projectModelSpecific = new ProjectModel();
	private static final String PLATFORM_XML_FILE = "xml/platformdata.xml";
	private static final String RTOS_PG_XML_FILE = "xml/rtospg.xml";
	
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
	public void tc_02_AddAndRemoveRows() throws Exception{
		Utility.openSCFGEditor(projectModelSpecific);
		
		bot.tree(1).getTreeItem(ProjectParameters.FolderAndFile.FOLDER_RTOS)
		.getNode(ProjectParameters.FolderAndFile.FOLDER_RTOS_OBJECT)
		.getNode(ProjectParameters.RTOSComponent.FREERTOS_OBJECT).select();
		
		bot.tabItem("Tasks").activate();
		Utility.addOrRemoveKernelObject(true);
		Utility.addOrRemoveKernelObject(true);
		Utility.addOrRemoveKernelObject(true);

		Utility.addOrRemoveKernelObject(false);
		Utility.addOrRemoveKernelObject(false);

        
        
        bot.sleep(3000);
        boolean check0 = bot.ccomboBox(0).getText().equals("kernel start");
        boolean check1 = bot.text(1).getText().equals("task_3");
        boolean check2 = bot.text(2).getText().equals("task_3");
        boolean check3 = bot.text(3).getText().equals("512");
        boolean check4 = bot.text(4).getText().equals("NULL");
        boolean check5 = bot.text(5).getText().equals("NULL");
        boolean check6 = bot.text(6).getText().equals("1");
        
		Utility.addOrRemoveKernelObject(false);

        
        if(!check0||!check1||!check2||!check3||!check4||!check5||!check6) {
        	assertFalse(true);
        }
	}
	
	@Test
	public void tc_03_DeleteAndCreateKernelProject() throws Exception{
		Utility.deleteProject(projectModelSpecific.getProjectName(), true);
		if (bot.activeShell().getText().equals(ProjectParameters.WINDOW_SAVE_RESOURCES)) {
			bot.button(ButtonAction.BUTTON_DONT_SAVE).click();
		}
		PGUtility.createProject(RTOSType.FREERTOSKERNEL, RTOSVersion.Kernel_1_0_7, RTOSApplication.KERNEL_BARE, Constants.GCC_TOOLCHAIN, TargetBoard.BOARD_RSK_RX65N_2MB);
	}
	
	@Test
	public void tc_04_AddFirstRowAndCheckDefault() throws Exception{
		Utility.openSCFGEditor(projectModelSpecific);
		
		bot.tree(1).getTreeItem(ProjectParameters.FolderAndFile.FOLDER_RTOS)
		.getNode(ProjectParameters.FolderAndFile.FOLDER_RTOS_OBJECT)
		.getNode(ProjectParameters.RTOSComponent.FREERTOS_OBJECT).select();
		bot.tabItem("Tasks").activate();
		Utility.addOrRemoveKernelObject(true);
        
        bot.sleep(3000);
        boolean check0 = bot.ccomboBox(0).getText().equals("kernel start");
        boolean check1 = bot.text(1).getText().equals("task_1");
        boolean check2 = bot.text(2).getText().equals("task_1");
        boolean check3 = bot.text(3).getText().equals("512");
        boolean check4 = bot.text(4).getText().equals("NULL");
        boolean check5 = bot.text(5).getText().equals("NULL");
        boolean check6 = bot.text(6).getText().equals("1");
        
		Utility.addOrRemoveKernelObject(false);

        
        if(!check0||!check1||!check2||!check3||!check4||!check5||!check6) {
        	assertFalse(true);
        }
	}
	
	@Test
	public void tc_05_DeleteAndCreateKernelProject() throws Exception{
		Utility.deleteProject(projectModelSpecific.getProjectName(), true);
		if (bot.activeShell().getText().equals(ProjectParameters.WINDOW_SAVE_RESOURCES)) {
			bot.button(ButtonAction.BUTTON_DONT_SAVE).click();
		}
		PGUtility.createProject(RTOSType.FREERTOSKERNEL, RTOSVersion.Kernel_1_0_7, RTOSApplication.KERNEL_BARE, Constants.GCC_TOOLCHAIN, TargetBoard.BOARD_RSK_RX65N_2MB);
	}
	
	@Test
	public void tc_06_AddSecondRowAndCheckDefault() throws Exception{
		Utility.openSCFGEditor(projectModelSpecific);
		
		bot.tree(1).getTreeItem(ProjectParameters.FolderAndFile.FOLDER_RTOS)
		.getNode(ProjectParameters.FolderAndFile.FOLDER_RTOS_OBJECT)
		.getNode(ProjectParameters.RTOSComponent.FREERTOS_OBJECT).select();
		bot.tabItem("Tasks").activate();
		Utility.addOrRemoveKernelObject(true);
		Utility.addOrRemoveKernelObject(true);
        
        bot.sleep(3000);
        boolean check0 = bot.ccomboBox(1).getText().equals("kernel start");
        boolean check1 = bot.text(1+7*1).getText().equals("task_2");
        boolean check2 = bot.text(2+7*1).getText().equals("task_2");
        boolean check3 = bot.text(3+7*1).getText().equals("512");
        boolean check4 = bot.text(4+7*1).getText().equals("NULL");
        boolean check5 = bot.text(5+7*1).getText().equals("NULL");
        boolean check6 = bot.text(6+7*1).getText().equals("1");
        
        Utility.addOrRemoveKernelObject(false);
        Utility.addOrRemoveKernelObject(false);
        
        if(!check0||!check1||!check2||!check3||!check4||!check5||!check6) {
        	assertFalse(true);
        }
	}
	
	@Test
	public void tc_07_DeleteAndCreateKernelProject() throws Exception{
		Utility.deleteProject(projectModelSpecific.getProjectName(), true);
		if (bot.activeShell().getText().equals(ProjectParameters.WINDOW_SAVE_RESOURCES)) {
			bot.button(ButtonAction.BUTTON_DONT_SAVE).click();
		}
		PGUtility.createProject(RTOSType.FREERTOSKERNEL, RTOSVersion.Kernel_1_0_7, RTOSApplication.KERNEL_BARE, Constants.GCC_TOOLCHAIN, TargetBoard.BOARD_RSK_RX65N_2MB);
	}
	
	@Test
	public void tc_08_AddThirdRowAndCheckDefault() throws Exception{
		Utility.openSCFGEditor(projectModelSpecific);
		
		bot.tree(1).getTreeItem(ProjectParameters.FolderAndFile.FOLDER_RTOS)
		.getNode(ProjectParameters.FolderAndFile.FOLDER_RTOS_OBJECT)
		.getNode(ProjectParameters.RTOSComponent.FREERTOS_OBJECT).select();
		bot.tabItem("Tasks").activate();
		Utility.addOrRemoveKernelObject(true);
		Utility.addOrRemoveKernelObject(true);
		Utility.addOrRemoveKernelObject(true);
        
        bot.sleep(3000);
        boolean check0 = bot.ccomboBox(2).getText().equals("kernel start");
        boolean check1 = bot.text(1+7*2).getText().equals("task_3");
        boolean check2 = bot.text(2+7*2).getText().equals("task_3");
        boolean check3 = bot.text(3+7*2).getText().equals("512");
        boolean check4 = bot.text(4+7*2).getText().equals("NULL");
        boolean check5 = bot.text(5+7*2).getText().equals("NULL");
        boolean check6 = bot.text(6+7*2).getText().equals("1");
        
        Utility.addOrRemoveKernelObject(false);
        Utility.addOrRemoveKernelObject(false);
        Utility.addOrRemoveKernelObject(false);
        
        if(!check0||!check1||!check2||!check3||!check4||!check5||!check6) {
        	assertFalse(true);
        }
	}
	
	@Test
	public void tc_09_DeleteKernelProject() throws Exception{
		Utility.deleteProject(projectModelSpecific.getProjectName(), true);
		if (bot.activeShell().getText().equals(ProjectParameters.WINDOW_SAVE_RESOURCES)) {
			bot.button(ButtonAction.BUTTON_DONT_SAVE).click();
		}
	}
}
