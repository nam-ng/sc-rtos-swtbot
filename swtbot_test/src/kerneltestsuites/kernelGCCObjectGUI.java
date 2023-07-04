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
public class kernelGCCObjectGUI {
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
	public void tc_02_Check8TabsDisplay() throws Exception{
		Utility.openSCFGEditor(projectModelSpecific);
		
		bot.tree(1).getTreeItem(ProjectParameters.FolderAndFile.FOLDER_RTOS)
		.getNode(ProjectParameters.FolderAndFile.FOLDER_RTOS_OBJECT)
		.getNode(ProjectParameters.RTOSComponent.FREERTOS_OBJECT).select();
		
		
		boolean isEightTabsDisplay = false;
		if(bot.tabItem("Heap Estimation").isVisible() && bot.tabItem("Tasks").isVisible() &&
				bot.tabItem("Semaphores").isVisible() && bot.tabItem("Queues").isVisible() &&
				bot.tabItem("Software Timers").isVisible() && bot.tabItem("Event Groups").isVisible() &&
				bot.tabItem("Stream Buffers").isVisible() && bot.tabItem("Message Buffers").isVisible()) {
			isEightTabsDisplay = true;
		}
		
		if (!isEightTabsDisplay) {
			assertFalse(true);
		}
	}
	
	@Test
	public void tc_03_SwitchComponentAndCheckUI() throws Exception{
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
		if(bot.tabItem("Heap Estimation").isVisible() && bot.tabItem("Tasks").isVisible() &&
				bot.tabItem("Semaphores").isVisible() && bot.tabItem("Queues").isVisible() &&
				bot.tabItem("Software Timers").isVisible() && bot.tabItem("Event Groups").isVisible() &&
				bot.tabItem("Stream Buffers").isVisible() && bot.tabItem("Message Buffers").isVisible()) {
			isEightTabsDisplay = true;
		}
		
		if (!isEightTabsDisplay) {
			assertFalse(true);
		}
	}
	
	@Test
	public void tc_04_CheckHeapGUI() throws Exception{
		bot.tabItem("Heap Estimation").activate();
		boolean isHeapGUIDisplayCorrectly = false;
		if (bot.label("Total For Heap Usage").isVisible() && bot.label("Total For Task(s)").isVisible() && 
				bot.label("Total For Queue(s)").isVisible() && bot.label("Total For Semaphore(s)").isVisible() && 
				bot.label("Total For Software Timer(s)").isVisible() && bot.label("Total For Event Group(s)").isVisible() && 
				bot.label("Total For Message Buffer(s)").isVisible() && bot.label("Total For Stream Buffer(s)").isVisible() && 
				bot.label("Main Task").isVisible() && bot.label("IDLE Task").isVisible() && 
				bot.label("Timer Service Task").isVisible() && bot.label("Timer Queue").isVisible()) {
			isHeapGUIDisplayCorrectly = true;
		}
		
		if(!isHeapGUIDisplayCorrectly) {
			assertFalse(true);
		}
	}
	
	@Test
	public void tc_05_CheckTaskUI() throws Exception{
		bot.tabItem("Tasks").activate();
		Utility.addOrRemoveKernelObject(true);
		
		boolean isTasksObjectDisplayCorrectly = false;
		if (bot.ccomboBox(0).getText().equals("kernel start") && bot.text(1).getText().equals("task_1")
				&& bot.text(2).getText().equals("task_1") && bot.text(3).getText().equals("512")
				&& bot.text(4).getText().equals("NULL") && bot.text(5).getText().equals("NULL")
				&& bot.text(6).getText().equals("1")) {
			isTasksObjectDisplayCorrectly = true;
		}
		
		if(!isTasksObjectDisplayCorrectly) {
			assertFalse(true);
		}
	}
	
	@Test
	public void tc_06_CheckEventGroups() throws Exception{
		bot.tabItem("Event Groups").activate();
		Utility.addOrRemoveKernelObject(true);
		
		boolean isEventGroupsDisplayCorrectly = false;
		if (bot.text(1).getText().equals("event_grp_handle_1")) {
			isEventGroupsDisplayCorrectly = true;
		}
		
		if(!isEventGroupsDisplayCorrectly) {
			assertFalse(true);
		}
	}
	
	@Test
	public void tc_07_checkMsgBuffer() throws Exception{
		bot.tabItem("Message Buffers").activate();
		Utility.addOrRemoveKernelObject(true);

		boolean isMsgBufferDisplayCorrectly = false;
		if(bot.text(1).getText().equals("msg_bff_handle_1") && bot.text(2).getText().equals("100")) {
			isMsgBufferDisplayCorrectly = true;
		}
		
		if(!isMsgBufferDisplayCorrectly) {
			assertFalse(true);
		}
	}
	
	@Test
	public void tc_08_checkQueueUI() throws Exception{
		bot.tabItem("Queues").activate();
		Utility.addOrRemoveKernelObject(true);
		
		boolean isQueueDisplayCorrectly = false;
		if(bot.text(1).getText().equals("queue_handle_1") && bot.text(2).getText().equals("100") && 
				bot.text(3).getText().equals("sizeof(uint32_t)")) {
			isQueueDisplayCorrectly = true;
		}
		
		if(!isQueueDisplayCorrectly) {
			assertFalse(true);
		}
	}
	
	@Test
	public void tc_09_checkSWTimerUI() throws Exception{
		bot.tabItem("Software Timers").activate();
		Utility.addOrRemoveKernelObject(true);
		
		boolean isSWTimerDisplayCorrectly = false;
		if(bot.text(1).getText().equals("swt_handle_1") && bot.text(2).getText().equals("Timer_1") &&
				bot.text(3).getText().equals("100") && bot.text(4).getText().equals("0") &&
				bot.text(5).getText().equals("NULL")) {
			isSWTimerDisplayCorrectly = true;
		}
		
		if(!isSWTimerDisplayCorrectly) {
			assertFalse(true);
		}
	}
	
	@Test
	public void tc_10_checkSemaphoresUI() throws Exception{
		bot.tabItem("Semaphores").activate();
		Utility.addOrRemoveKernelObject(true);
		
		boolean isSemaphoreDisplayCorrectly = false;
		if(bot.ccomboBox(0).getText().equals("binary") && bot.text(1).getText().equals("semaphore_handle_1")) {
			isSemaphoreDisplayCorrectly = true;
		}
		
		if(!isSemaphoreDisplayCorrectly) {
			assertFalse(true);
		}
	}
	
	@Test
	public void tc_11_checkStreamBufferUI() throws Exception{
		bot.tabItem("Stream Buffers").activate();
		Utility.addOrRemoveKernelObject(true);
		
		boolean isStreamBufferDisplayCorrectly = false;
		if(bot.text(1).getText().equals("stream_bff_handle_1") && bot.text(2).getText().equals("100") && 
				bot.text(3).getText().equals("10")) {
			isStreamBufferDisplayCorrectly = true;
		}
		
		if(!isStreamBufferDisplayCorrectly) {
			assertFalse(true);
		}
	}
	
	@Test
	public void tc_12_DeleteKernelProject() throws Exception {
		Utility.deleteProject(projectModelSpecific.getProjectName(), true);
		if (bot.activeShell().getText().equals(ProjectParameters.WINDOW_SAVE_RESOURCES)) {
			bot.button(ButtonAction.BUTTON_DONT_SAVE).click();
		}
	}
}
