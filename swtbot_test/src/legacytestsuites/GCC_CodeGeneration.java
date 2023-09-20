package legacytestsuites;

import static org.junit.Assert.assertFalse;

import java.io.File;

import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotEclipseEditor;
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
import parameters.ProjectParameters.MenuName;
import parameters.ProjectParameters.RTOSApplication;
import parameters.ProjectParameters.RTOSType;
import parameters.ProjectParameters.RTOSVersion;
import parameters.ProjectParameters.TargetBoard;
import platform.PlatformModel;
import utilities.PGUtility;
import utilities.Utility;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class GCC_CodeGeneration {
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
				RTOSApplication.AMAZON_BARE, Constants.GCC_TOOLCHAIN, TargetBoard.BOARD_CK_RX65N);
	}

	@Test
	public void tc_01_CreateAmazonProject() throws Exception {
		PGUtility.createProject(RTOSType.AMAZONFREERTOS, RTOSVersion.Amazon_202107_1_0_1, RTOSApplication.AMAZON_BARE,
				Constants.GCC_TOOLCHAIN, TargetBoard.BOARD_CK_RX65N);

	}
	
	@Test
	public void tc_02_GenerateDefault() throws Exception {
		Utility.openSCFGEditor(projectModelSpecific, ProjectParameters.SCFG_COMPONENT_TAB);

		bot.tree(1).getTreeItem(ProjectParameters.FolderAndFile.FOLDER_RTOS)
				.getNode(ProjectParameters.FolderAndFile.FOLDER_RTOS_OBJECT)
				.getNode(ProjectParameters.RTOSComponent.FREERTOS_OBJECT).select();

		bot.tabItem(ProjectParameters.KernelObjectTab.TASKS).activate();
		Utility.addOrRemoveKernelObject(true, 0);

		bot.tabItem(ProjectParameters.KernelObjectTab.MESSAGE_BUFFERS).activate();
		Utility.addOrRemoveKernelObject(true, 0);

		bot.tabItem(ProjectParameters.KernelObjectTab.EVENT_GROUPS).activate();
		Utility.addOrRemoveKernelObject(true, 0);

		bot.tabItem(ProjectParameters.KernelObjectTab.QUEUES).activate();
		Utility.addOrRemoveKernelObject(true, 0);

		bot.tabItem(ProjectParameters.KernelObjectTab.SOFTWARE_TIMERS).activate();
		Utility.addOrRemoveKernelObject(true, 0);

		bot.tabItem(ProjectParameters.KernelObjectTab.SEMAPHORES).activate();
		Utility.addOrRemoveKernelObject(true, 0);

		bot.tabItem(ProjectParameters.KernelObjectTab.STREAM_BUFFERS).activate();
		Utility.addOrRemoveKernelObject(true, 0);

		Utility.clickGenerateCode();

		String task = "ret = xTaskCreate(task_1, \"task_1\", 512, NULL, 1, NULL)";
		String semaphore = "semaphore_handle_1 = xSemaphoreCreateBinary()";
		String queue = "queue_handle_1 = xQueueCreate(100, sizeof(uint32_t))";
		String swtimer = "swt_handle_1 = xTimerCreate(\"Timer_1\", 100, pdFALSE, 0, NULL)";
		String evtgrp = "event_grp_handle_1 = xEventGroupCreate()";
		String strbuffer = "stream_bff_handle_1 = xStreamBufferCreate(100, 10)";
		String megbuffer = "msg_bff_handle_1 = xMessageBufferCreate(100)";

		String buildType = projectModelSpecific.getActiveBuildConfiguration();
		bot.tree().getTreeItem(projectModelSpecific.getProjectName()).select();
		bot.tree().getTreeItem(projectModelSpecific.getProjectName() + " [" + buildType + "]").expand();
		bot.tree().getTreeItem(projectModelSpecific.getProjectName() + " [" + buildType + "]")
				.getNode(ProjectParameters.FolderAndFile.FOLDER_APPLICATION_CODE).expand();
		bot.tree().getTreeItem(projectModelSpecific.getProjectName() + " [" + buildType + "]")
				.getNode(ProjectParameters.FolderAndFile.FOLDER_APPLICATION_CODE)
				.getNode(ProjectParameters.FolderAndFile.FOLDER_RENESAS_CODE).expand();
		bot.tree().getTreeItem(projectModelSpecific.getProjectName() + " [" + buildType + "]")
				.getNode(ProjectParameters.FolderAndFile.FOLDER_APPLICATION_CODE)
				.getNode(ProjectParameters.FolderAndFile.FOLDER_RENESAS_CODE)
				.getNode(ProjectParameters.FolderAndFile.FOLDER_FRTOS_STARTUP).expand();
		bot.tree().getTreeItem(projectModelSpecific.getProjectName() + " [" + buildType + "]")
				.getNode(ProjectParameters.FolderAndFile.FOLDER_APPLICATION_CODE)
				.getNode(ProjectParameters.FolderAndFile.FOLDER_RENESAS_CODE)
				.getNode(ProjectParameters.FolderAndFile.FOLDER_FRTOS_STARTUP)
				.getNode(ProjectParameters.FolderAndFile.FILE_FREERTOS_OBJECT_INIT_C).doubleClick();

		SWTBotEclipseEditor freeRTOSConfigEditor = bot
				.editorByTitle(ProjectParameters.FolderAndFile.FILE_FREERTOS_OBJECT_INIT_C).toTextEditor();
		String textOfCFile = freeRTOSConfigEditor.getText();
		boolean isFileContainsText = false;
		if (textOfCFile.contains(task) && textOfCFile.contains(semaphore) && textOfCFile.contains(queue)
				&& textOfCFile.contains(swtimer) && textOfCFile.contains(evtgrp) && textOfCFile.contains(strbuffer)
				&& textOfCFile.contains(megbuffer)) {
			isFileContainsText = true;
		}

		if (!isFileContainsText) {
			assertFalse(true);
		}
	}
	
	@Test
	public void tc_03_generateTaskKernelStart() throws Exception {
		String task = """
				void Kernel_Object_init (void)
				{
				    /************** task creation ****************************/

				    ret = xTaskCreate(task_1, "task_1", 512, NULL, 1, NULL);
				    if (pdPASS != ret)
				    {
				        while (1)
				        {
				            /* Failed! Task can not be created. */
				        }
				    }
								""";

		Utility.openSCFGEditor(projectModelSpecific, ProjectParameters.SCFG_COMPONENT_TAB);

		bot.tree(1).getTreeItem(ProjectParameters.FolderAndFile.FOLDER_RTOS)
				.getNode(ProjectParameters.FolderAndFile.FOLDER_RTOS_OBJECT)
				.getNode(ProjectParameters.RTOSComponent.FREERTOS_OBJECT).select();

		bot.tabItem(ProjectParameters.KernelObjectTab.TASKS).activate();
		bot.ccomboBox(0).setSelection("kernel start");

		Utility.clickGenerateCode();

		String buildType = projectModelSpecific.getActiveBuildConfiguration();
		bot.tree().getTreeItem(projectModelSpecific.getProjectName()).select();
		bot.tree().getTreeItem(projectModelSpecific.getProjectName() + " [" + buildType + "]").expand();
		bot.tree().getTreeItem(projectModelSpecific.getProjectName() + " [" + buildType + "]")
				.getNode(ProjectParameters.FolderAndFile.FOLDER_APPLICATION_CODE).expand();
		bot.tree().getTreeItem(projectModelSpecific.getProjectName() + " [" + buildType + "]")
				.getNode(ProjectParameters.FolderAndFile.FOLDER_APPLICATION_CODE)
				.getNode(ProjectParameters.FolderAndFile.FOLDER_RENESAS_CODE).expand();
		bot.tree().getTreeItem(projectModelSpecific.getProjectName() + " [" + buildType + "]")
				.getNode(ProjectParameters.FolderAndFile.FOLDER_APPLICATION_CODE)
				.getNode(ProjectParameters.FolderAndFile.FOLDER_RENESAS_CODE)
				.getNode(ProjectParameters.FolderAndFile.FOLDER_FRTOS_STARTUP).expand();
		bot.tree().getTreeItem(projectModelSpecific.getProjectName() + " [" + buildType + "]")
				.getNode(ProjectParameters.FolderAndFile.FOLDER_APPLICATION_CODE)
				.getNode(ProjectParameters.FolderAndFile.FOLDER_RENESAS_CODE)
				.getNode(ProjectParameters.FolderAndFile.FOLDER_FRTOS_STARTUP)
				.getNode(ProjectParameters.FolderAndFile.FILE_FREERTOS_OBJECT_INIT_C).doubleClick();

		SWTBotEclipseEditor freeRTOSConfigEditor = bot
				.editorByTitle(ProjectParameters.FolderAndFile.FILE_FREERTOS_OBJECT_INIT_C).toTextEditor();
		String textOfCFile = freeRTOSConfigEditor.getText();
		boolean isFileContainsText = false;

		if (textOfCFile.contains(task)) {
			isFileContainsText = true;
		}

		if (!isFileContainsText) {
			assertFalse(true);
		}
	}
	
	@Test
	public void tc_04_generateTaskManual() throws Exception {
		String task = """
				void Object_init_manual (void)
				{
				    /************** task creation ****************************/
				    ret = xTaskCreate(task_1, "task_1", 512, NULL, 1, NULL);
				    if (pdPASS != ret)
				    {
				        while (1)
				        {
				            /* Failed! Task can not be created. */
				        }
				    }
				} /* End of function Object_init_manual()*/
								""";

		Utility.openSCFGEditor(projectModelSpecific, ProjectParameters.SCFG_COMPONENT_TAB);

		bot.tree(1).getTreeItem(ProjectParameters.FolderAndFile.FOLDER_RTOS)
				.getNode(ProjectParameters.FolderAndFile.FOLDER_RTOS_OBJECT)
				.getNode(ProjectParameters.RTOSComponent.FREERTOS_OBJECT).select();

		bot.tabItem(ProjectParameters.KernelObjectTab.TASKS).activate();
		bot.ccomboBox(0).setSelection(ProjectParameters.KernelObject.MANUAL);

		Utility.clickGenerateCode();

		String buildType = projectModelSpecific.getActiveBuildConfiguration();
		bot.tree().getTreeItem(projectModelSpecific.getProjectName()).select();
		bot.tree().getTreeItem(projectModelSpecific.getProjectName() + " [" + buildType + "]").expand();
		bot.tree().getTreeItem(projectModelSpecific.getProjectName() + " [" + buildType + "]")
				.getNode(ProjectParameters.FolderAndFile.FOLDER_APPLICATION_CODE).expand();
		bot.tree().getTreeItem(projectModelSpecific.getProjectName() + " [" + buildType + "]")
				.getNode(ProjectParameters.FolderAndFile.FOLDER_APPLICATION_CODE)
				.getNode(ProjectParameters.FolderAndFile.FOLDER_RENESAS_CODE).expand();
		bot.tree().getTreeItem(projectModelSpecific.getProjectName() + " [" + buildType + "]")
				.getNode(ProjectParameters.FolderAndFile.FOLDER_APPLICATION_CODE)
				.getNode(ProjectParameters.FolderAndFile.FOLDER_RENESAS_CODE)
				.getNode(ProjectParameters.FolderAndFile.FOLDER_FRTOS_STARTUP).expand();
		bot.tree().getTreeItem(projectModelSpecific.getProjectName() + " [" + buildType + "]")
				.getNode(ProjectParameters.FolderAndFile.FOLDER_APPLICATION_CODE)
				.getNode(ProjectParameters.FolderAndFile.FOLDER_RENESAS_CODE)
				.getNode(ProjectParameters.FolderAndFile.FOLDER_FRTOS_STARTUP)
				.getNode(ProjectParameters.FolderAndFile.FILE_FREERTOS_OBJECT_INIT_C).doubleClick();

		SWTBotEclipseEditor freeRTOSConfigEditor = bot
				.editorByTitle(ProjectParameters.FolderAndFile.FILE_FREERTOS_OBJECT_INIT_C).toTextEditor();
		String textOfCFile = freeRTOSConfigEditor.getText();
		boolean isFileContainsText = false;

		if (textOfCFile.contains(task)) {
			isFileContainsText = true;
		}

		if (!isFileContainsText) {
			assertFalse(true);
		}
	}
	
	@Test
	public void tc_05_generateWithUserCodeProtected() throws Exception {
		String task_skeleton = """
				#include "task_function.h"
				/* Start user code for import. Do not edit comment generated here */
				/* End user code. Do not edit comment generated here */

				void task_1(void * pvParameters)
				{
				/* Start user code for function. Do not edit comment generated here */
				/* End user code. Do not edit comment generated here */
				}
				/* Start user code for other. Do not edit comment generated here */
				/* End user code. Do not edit comment generated here */
								""";

		Utility.openSCFGEditor(projectModelSpecific, ProjectParameters.SCFG_COMPONENT_TAB);

		bot.tree(1).getTreeItem(ProjectParameters.FolderAndFile.FOLDER_RTOS)
				.getNode(ProjectParameters.FolderAndFile.FOLDER_RTOS_OBJECT)
				.getNode(ProjectParameters.RTOSComponent.FREERTOS_OBJECT).select();

		bot.tabItem("Tasks").activate();
		bot.ccomboBox(0).setSelection("kernel start");

		Utility.clickGenerateCode();

		String buildType = projectModelSpecific.getActiveBuildConfiguration();
		bot.tree().getTreeItem(projectModelSpecific.getProjectName()).select();
		bot.tree().getTreeItem(projectModelSpecific.getProjectName() + " [" + buildType + "]").expand();
		bot.tree().getTreeItem(projectModelSpecific.getProjectName() + " [" + buildType + "]")
				.getNode(ProjectParameters.FolderAndFile.FOLDER_APPLICATION_CODE).expand();
		bot.tree().getTreeItem(projectModelSpecific.getProjectName() + " [" + buildType + "]")
				.getNode(ProjectParameters.FolderAndFile.FOLDER_APPLICATION_CODE)
				.getNode(ProjectParameters.FolderAndFile.FOLDER_RENESAS_CODE).expand();
		bot.tree().getTreeItem(projectModelSpecific.getProjectName() + " [" + buildType + "]")
				.getNode(ProjectParameters.FolderAndFile.FOLDER_APPLICATION_CODE)
				.getNode(ProjectParameters.FolderAndFile.FOLDER_RENESAS_CODE)
				.getNode(ProjectParameters.FolderAndFile.FOLDER_FRTOS_SKELETON).expand();
		bot.tree().getTreeItem(projectModelSpecific.getProjectName() + " [" + buildType + "]")
				.getNode(ProjectParameters.FolderAndFile.FOLDER_APPLICATION_CODE)
				.getNode(ProjectParameters.FolderAndFile.FOLDER_RENESAS_CODE)
				.getNode(ProjectParameters.FolderAndFile.FOLDER_FRTOS_SKELETON)
				.getNode(ProjectParameters.FolderAndFile.FILE_TASK_1_C).doubleClick();

		SWTBotEclipseEditor task1Editor = bot.editorByTitle(ProjectParameters.FolderAndFile.FILE_TASK_1_C)
				.toTextEditor();
		String textOfCFile1 = task1Editor.getText();
		boolean isFileContainsText1 = false;

		if (textOfCFile1.contains(task_skeleton)) {
			isFileContainsText1 = true;
		}

		task1Editor.setFocus();
		task1Editor.insertText(27, 70, """

				int i = 0;""", false);

		bot.sleep(5000);
		bot.menu(MenuName.MENU_FILE).menu(MenuName.MENU_SAVE).click();

		Utility.openSCFGEditor(projectModelSpecific, ProjectParameters.SCFG_COMPONENT_TAB);

		Utility.clickGenerateCode();
		bot.tree().getTreeItem(projectModelSpecific.getProjectName()).select();
		bot.tree().getTreeItem(projectModelSpecific.getProjectName() + " [" + buildType + "]")
				.getNode(ProjectParameters.FolderAndFile.FOLDER_APPLICATION_CODE)
				.getNode(ProjectParameters.FolderAndFile.FOLDER_RENESAS_CODE)
				.getNode(ProjectParameters.FolderAndFile.FOLDER_FRTOS_SKELETON)
				.getNode(ProjectParameters.FolderAndFile.FILE_TASK_1_C).doubleClick();

		SWTBotEclipseEditor task1Editor2 = bot.editorByTitle(ProjectParameters.FolderAndFile.FILE_TASK_1_C)
				.toTextEditor();
		String textOfCFile2 = task1Editor2.getText();
		boolean isFileContainsText2 = false;

		if (textOfCFile2.contains("int i = 0;")) {
			isFileContainsText2 = true;
		}
		bot.sleep(5000);

		Utility.openSCFGEditor(projectModelSpecific, ProjectParameters.SCFG_COMPONENT_TAB);

		bot.tree(1).getTreeItem(ProjectParameters.FolderAndFile.FOLDER_RTOS)
				.getNode(ProjectParameters.FolderAndFile.FOLDER_RTOS_OBJECT)
				.getNode(ProjectParameters.RTOSComponent.FREERTOS_OBJECT).select();
		bot.tabItem("Tasks").activate();
		bot.text(1 + 7 * 0).setText("task_2");
		
		Utility.clickGenerateCode();
		bot.tree().getTreeItem(projectModelSpecific.getProjectName()).select();
		bot.tree().getTreeItem(projectModelSpecific.getProjectName() + " [" + buildType + "]")
				.getNode(ProjectParameters.FolderAndFile.FOLDER_APPLICATION_CODE)
				.getNode(ProjectParameters.FolderAndFile.FOLDER_RENESAS_CODE)
				.getNode(ProjectParameters.FolderAndFile.FOLDER_FRTOS_SKELETON)
				.getNode(ProjectParameters.FolderAndFile.FILE_TASK_2_C).doubleClick();
		
		SWTBotEclipseEditor task2Editor = bot.editorByTitle(ProjectParameters.FolderAndFile.FILE_TASK_2_C)
				.toTextEditor();
		String textOfCFile = task2Editor.getText();
		boolean isFileContainsText = false;

		if (textOfCFile.contains("int i = 0;")) {
			isFileContainsText = true;
		}
		bot.sleep(5000);

		if (!isFileContainsText2 || !isFileContainsText || !isFileContainsText1) {
			assertFalse(true);
		}
	}
	
	@Test
	public void tc_06_generateAgainAfterDelete() throws Exception {
		Utility.openSCFGEditor(projectModelSpecific, ProjectParameters.SCFG_COMPONENT_TAB);

		bot.tree(1).getTreeItem(ProjectParameters.FolderAndFile.FOLDER_RTOS)
				.getNode(ProjectParameters.FolderAndFile.FOLDER_RTOS_OBJECT)
				.getNode(ProjectParameters.RTOSComponent.FREERTOS_OBJECT).select();

		bot.tabItem(ProjectParameters.KernelObjectTab.TASKS).activate();
		bot.sleep(1000);
		Utility.addOrRemoveKernelObject(false, 0);

		bot.tabItem(ProjectParameters.KernelObjectTab.MESSAGE_BUFFERS).activate();
		bot.sleep(1000);
		Utility.addOrRemoveKernelObject(false, 0);

		bot.tabItem(ProjectParameters.KernelObjectTab.EVENT_GROUPS).activate();
		bot.sleep(1000);
		Utility.addOrRemoveKernelObject(false, 0);

		bot.tabItem(ProjectParameters.KernelObjectTab.QUEUES).activate();
		bot.sleep(1000);
		Utility.addOrRemoveKernelObject(false, 0);

		bot.tabItem(ProjectParameters.KernelObjectTab.SOFTWARE_TIMERS).activate();
		bot.sleep(1000);
		Utility.addOrRemoveKernelObject(false, 0);

		bot.tabItem(ProjectParameters.KernelObjectTab.SEMAPHORES).activate();
		bot.sleep(1000);
		Utility.addOrRemoveKernelObject(false, 0);

		bot.tabItem(ProjectParameters.KernelObjectTab.STREAM_BUFFERS).activate();
		bot.sleep(1000);
		Utility.addOrRemoveKernelObject(false, 0);

		Utility.clickGenerateCode();
	}
	
	@Test
	public void tc_07_DeleteAmazonProject() throws Exception {
		Utility.deleteProject(projectModelSpecific.getProjectName(), true);
		if (bot.activeShell().getText().equals(ProjectParameters.WINDOW_SAVE_RESOURCES)) {
			bot.button(ButtonAction.BUTTON_DONT_SAVE).click();
		}
	}
}
