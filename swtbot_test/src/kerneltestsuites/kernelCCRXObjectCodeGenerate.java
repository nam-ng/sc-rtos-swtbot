package kerneltestsuites;

import static org.junit.Assert.assertFalse;

import java.io.File;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotEclipseEditor;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotEditor;
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
public class kernelCCRXObjectCodeGenerate {
	private static SWTWorkbenchBot bot;
	private static ProjectModel projectModelSpecific = new ProjectModel();
	private static final String PLATFORM_XML_FILE = "xml/platformdata.xml";
	private static final String RTOS_PG_XML_FILE = "xml/rtospg.xml";

	@BeforeClass
	public static void beforeClass() throws Exception {
		bot = new SWTWorkbenchBot();
		PlatformModel.loadPlatformModel(new File(Utility.getBundlePath(LogUtil.PLUGIN_ID, PLATFORM_XML_FILE)));
		RTOSManager.loadRTOSModel(new File(Utility.getBundlePath(LogUtil.PLUGIN_ID, RTOS_PG_XML_FILE)));
		projectModelSpecific = PGUtility.prepareProjectModel(RTOSType.FREERTOSKERNEL, RTOSVersion.Kernel_1_0_7,
				RTOSApplication.KERNEL_BARE, Constants.CCRX_TOOLCHAIN, TargetBoard.BOARD_RSK_RX65N_2MB);
	}

	@Test
	public void tc_01_CreateKernelProject() throws Exception {
		PGUtility.createProject(RTOSType.FREERTOSKERNEL, RTOSVersion.Kernel_1_0_7, RTOSApplication.KERNEL_BARE,
				Constants.CCRX_TOOLCHAIN, TargetBoard.BOARD_RSK_RX65N_2MB);

	}

	@Test
	public void tc_02_GenerateDefault() throws Exception {
		Utility.openSCFGEditor(projectModelSpecific);

		bot.tree(1).getTreeItem(ProjectParameters.FolderAndFile.FOLDER_RTOS)
				.getNode(ProjectParameters.FolderAndFile.FOLDER_RTOS_OBJECT)
				.getNode(ProjectParameters.RTOSComponent.FREERTOS_OBJECT).select();

		bot.tabItem("Tasks").activate();
		Utility.addOrRemoveKernelObject(true);

		bot.tabItem("Message Buffers").activate();
		Utility.addOrRemoveKernelObject(true);

		bot.tabItem("Event Groups").activate();
		Utility.addOrRemoveKernelObject(true);

		bot.tabItem("Queues").activate();
		Utility.addOrRemoveKernelObject(true);

		bot.tabItem("Software Timers").activate();
		Utility.addOrRemoveKernelObject(true);

		bot.tabItem("Semaphores").activate();
		Utility.addOrRemoveKernelObject(true);

		bot.tabItem("Stream Buffers").activate();
		Utility.addOrRemoveKernelObject(true);

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
				.getNode(ProjectParameters.FolderAndFile.FOLDER_SRC).expand();
		bot.tree().getTreeItem(projectModelSpecific.getProjectName() + " [" + buildType + "]")
				.getNode(ProjectParameters.FolderAndFile.FOLDER_SRC)
				.getNode(ProjectParameters.FolderAndFile.FOLDER_FRTOS_STARTUP).expand();
		bot.tree().getTreeItem(projectModelSpecific.getProjectName() + " [" + buildType + "]")
				.getNode(ProjectParameters.FolderAndFile.FOLDER_SRC)
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

		Utility.openSCFGEditor(projectModelSpecific);

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
				.getNode(ProjectParameters.FolderAndFile.FOLDER_SRC).expand();
		bot.tree().getTreeItem(projectModelSpecific.getProjectName() + " [" + buildType + "]")
				.getNode(ProjectParameters.FolderAndFile.FOLDER_SRC)
				.getNode(ProjectParameters.FolderAndFile.FOLDER_FRTOS_STARTUP).expand();
		bot.tree().getTreeItem(projectModelSpecific.getProjectName() + " [" + buildType + "]")
				.getNode(ProjectParameters.FolderAndFile.FOLDER_SRC)
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

		Utility.openSCFGEditor(projectModelSpecific);

		bot.tree(1).getTreeItem(ProjectParameters.FolderAndFile.FOLDER_RTOS)
				.getNode(ProjectParameters.FolderAndFile.FOLDER_RTOS_OBJECT)
				.getNode(ProjectParameters.RTOSComponent.FREERTOS_OBJECT).select();

		bot.tabItem("Tasks").activate();
		bot.ccomboBox(0).setSelection("manual");

		Utility.clickGenerateCode();

		String buildType = projectModelSpecific.getActiveBuildConfiguration();
		bot.tree().getTreeItem(projectModelSpecific.getProjectName()).select();
		bot.tree().getTreeItem(projectModelSpecific.getProjectName() + " [" + buildType + "]").expand();
		bot.tree().getTreeItem(projectModelSpecific.getProjectName() + " [" + buildType + "]")
				.getNode(ProjectParameters.FolderAndFile.FOLDER_SRC).expand();
		bot.tree().getTreeItem(projectModelSpecific.getProjectName() + " [" + buildType + "]")
				.getNode(ProjectParameters.FolderAndFile.FOLDER_SRC)
				.getNode(ProjectParameters.FolderAndFile.FOLDER_FRTOS_STARTUP).expand();
		bot.tree().getTreeItem(projectModelSpecific.getProjectName() + " [" + buildType + "]")
				.getNode(ProjectParameters.FolderAndFile.FOLDER_SRC)
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
	public void tc_05_generateWithUserCodeProtected() throws Exception{
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
		
		Utility.openSCFGEditor(projectModelSpecific);

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
				.getNode(ProjectParameters.FolderAndFile.FOLDER_SRC).expand();
		bot.tree().getTreeItem(projectModelSpecific.getProjectName() + " [" + buildType + "]")
				.getNode(ProjectParameters.FolderAndFile.FOLDER_SRC)
				.getNode(ProjectParameters.FolderAndFile.FOLDER_FRTOS_SKELETON).expand();
		bot.tree().getTreeItem(projectModelSpecific.getProjectName() + " [" + buildType + "]")
				.getNode(ProjectParameters.FolderAndFile.FOLDER_SRC)
				.getNode(ProjectParameters.FolderAndFile.FOLDER_FRTOS_SKELETON)
				.getNode(ProjectParameters.FolderAndFile.FILE_TASK_1_C).doubleClick();
		
		SWTBotEclipseEditor task1Editor = bot
				.editorByTitle(ProjectParameters.FolderAndFile.FILE_TASK_1_C).toTextEditor();
		String textOfCFile = task1Editor.getText();
		boolean isFileContainsText1 = false;

		if (textOfCFile.contains(task_skeleton) && 
				textOfCFile.contains("Copyright (C) 2019 Renesas Electronics Corporation. All rights reserved.")) {
			isFileContainsText1 = true;
		}
		
		task1Editor.setFocus();
		task1Editor.insertText(27,70,"""

    int i = 0;""", false);
		
		bot.menu(MenuName.MENU_FILE).menu(MenuName.MENU_SAVE).click();
		
		Utility.openSCFGEditor(projectModelSpecific);
		
		Utility.clickGenerateCode();
		bot.tree().getTreeItem(projectModelSpecific.getProjectName()).select();
		bot.tree().getTreeItem(projectModelSpecific.getProjectName() + " [" + buildType + "]")
		.getNode(ProjectParameters.FolderAndFile.FOLDER_SRC)
		.getNode(ProjectParameters.FolderAndFile.FOLDER_FRTOS_SKELETON)
		.getNode(ProjectParameters.FolderAndFile.FILE_TASK_1_C).doubleClick();
		
		SWTBotEclipseEditor task1Editor2 = bot
				.editorByTitle(ProjectParameters.FolderAndFile.FILE_TASK_1_C).toTextEditor();
		String textOfCFile2 = task1Editor2.getText();
		boolean isFileContainsText2 = false;
		
		if(textOfCFile2.contains("int i = 0;")) {
			isFileContainsText2 = true;
		}
		
		if (!isFileContainsText1 || !isFileContainsText2) {
			assertFalse(true);
		}
	}
	
	@Test
	public void tc_06_DeleteKernelProject() throws Exception {
		Utility.deleteProject(projectModelSpecific.getProjectName(), true);
		if (bot.activeShell().getText().equals(ProjectParameters.WINDOW_SAVE_RESOURCES)) {
			bot.button(ButtonAction.BUTTON_DONT_SAVE).click();
		}
	}
}
