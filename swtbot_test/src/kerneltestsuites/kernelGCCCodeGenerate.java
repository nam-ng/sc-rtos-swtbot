package kerneltestsuites;

import static org.junit.Assert.assertFalse;

import java.io.File;
import java.util.List;

import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
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
public class kernelGCCCodeGenerate {
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
				RTOSApplication.KERNEL_BARE, Constants.GCC_TOOLCHAIN, TargetBoard.BOARD_RSK_RX65N_2MB);
	}

	@Test
	public void tc_01_CreateKernelProject() throws Exception {
		PGUtility.createProject(RTOSType.FREERTOSKERNEL, RTOSVersion.Kernel_1_0_7, RTOSApplication.KERNEL_BARE,
				Constants.GCC_TOOLCHAIN, TargetBoard.BOARD_RSK_RX65N_2MB);

	}

	@Test
	public void tc_02_CheckCodeGenerate() throws Exception {
		Utility.openSCFGEditor(projectModelSpecific);

		bot.tree(1).getTreeItem(ProjectParameters.FolderAndFile.FOLDER_RTOS)
				.getNode(ProjectParameters.FolderAndFile.FOLDER_RTOS_KERNEL)
				.getNode(ProjectParameters.RTOSComponent.FREERTOS_KERNEL).select();

		SWTBotTreeItem[] kernelConfigTree = bot.tree(2).getTreeItem("Configurations ").getItems();
		for (SWTBotTreeItem config : kernelConfigTree) {
			if (config.cell(0).contains("RTOS scheduler ")) {
				bot.sleep(2000);
				config.click(1);
				bot.list(0).select("Cooperative");
			}
			if (config.cell(0).contains("The frequency of the RTOS tick interrupt ")) {
				bot.sleep(2000);
				config.click(1);
				bot.text(config.cell(1)).setText("test1");
			}
			if (config.cell(0).contains("The size of the stack used by the idle task ")) {
				bot.sleep(2000);
				config.click(1);
				bot.text(config.cell(1)).setText("test2");
			}
		}

		Utility.clickGenerateCode();

		String buildType = projectModelSpecific.getActiveBuildConfiguration();
		bot.tree().getTreeItem(projectModelSpecific.getProjectName()).select();
		bot.tree().getTreeItem(projectModelSpecific.getProjectName() + " [" + buildType + "]").expand();
		bot.tree().getTreeItem(projectModelSpecific.getProjectName() + " [" + buildType + "]")
				.getNode(ProjectParameters.FolderAndFile.FOLDER_SRC).expand();
		bot.tree().getTreeItem(projectModelSpecific.getProjectName() + " [" + buildType + "]")
				.getNode(ProjectParameters.FolderAndFile.FOLDER_SRC)
				.getNode(ProjectParameters.FolderAndFile.FOLDER_FRTOS_CONFIG).expand();
		List<String> frtosConfigFolder = bot.tree()
				.getTreeItem(projectModelSpecific.getProjectName() + " [" + buildType + "]")
				.getNode(ProjectParameters.FolderAndFile.FOLDER_SRC)
				.getNode(ProjectParameters.FolderAndFile.FOLDER_FRTOS_CONFIG).getNodes();
		boolean isRightFileName = false;
		boolean isRightFilePath = false;
		for (String item : frtosConfigFolder) {
			if (item.equals(ProjectParameters.FolderAndFile.FILE_FREERTOSCONFIG_H)) {
				isRightFileName = true;
				bot.tree().getTreeItem(projectModelSpecific.getProjectName() + " [" + buildType + "]")
						.contextMenu(ProjectParameters.MenuName.CONTEXT_MENU_PROPERTIES).click();
				SWTBotTreeItem[] propertiesList1 = bot.tree(3).getTreeItem("Info").getItems();
				String projectLocation = "";
				for (SWTBotTreeItem properties1 : propertiesList1) {
					if (properties1.cell(0).contains("location")) {
						projectLocation = properties1.cell(1);
					}
				}
				bot.tree().getTreeItem(projectModelSpecific.getProjectName() + " [" + buildType + "]")
						.getNode(ProjectParameters.FolderAndFile.FOLDER_SRC)
						.getNode(ProjectParameters.FolderAndFile.FOLDER_FRTOS_CONFIG)
						.getNode(ProjectParameters.FolderAndFile.FILE_FREERTOSCONFIG_H)
						.contextMenu(ProjectParameters.MenuName.CONTEXT_MENU_PROPERTIES).click();
				SWTBotTreeItem[] propertiesList2 = bot.tree(3).getTreeItem("Info").getItems();
				String fileConfigLocation = "";
				for (SWTBotTreeItem properties2 : propertiesList2) {
					if (properties2.cell(0).contains("location")) {
						fileConfigLocation = properties2.cell(1);
					}
				}
				if (fileConfigLocation.equals(projectLocation.concat("\\src\\frtos_config\\FreeRTOSConfig.h"))) {
					isRightFilePath = true;
				}
			}
		}
		if (!isRightFileName || !isRightFilePath) {
			assertFalse(true);
		}
	}

	@Test
	public void tc_03_DeleteFileAndGenerate() throws Exception {
		String buildType = projectModelSpecific.getActiveBuildConfiguration();

		bot.tree().getTreeItem(projectModelSpecific.getProjectName() + " [" + buildType + "]")
				.getNode(ProjectParameters.FolderAndFile.FOLDER_SRC)
				.getNode(ProjectParameters.FolderAndFile.FOLDER_FRTOS_CONFIG)
				.getNode(ProjectParameters.FolderAndFile.FILE_FREERTOSCONFIG_H)
				.contextMenu(ProjectParameters.MenuName.CONTEXT_MENU_DELETE).click();
		bot.checkBox("Move to Recycle Bin").select();
		bot.button(ProjectParameters.ButtonAction.BUTTON_OK).click();

		Utility.clickGenerateCode();
		bot.tree().getTreeItem(projectModelSpecific.getProjectName()).select();
		bot.tree().getTreeItem(projectModelSpecific.getProjectName() + " [" + buildType + "]")
				.getNode(ProjectParameters.FolderAndFile.FOLDER_SRC)
				.getNode(ProjectParameters.FolderAndFile.FOLDER_FRTOS_CONFIG).expand();
		List<String> frtosConfigFolder = bot.tree()
				.getTreeItem(projectModelSpecific.getProjectName() + " [" + buildType + "]")
				.getNode(ProjectParameters.FolderAndFile.FOLDER_SRC)
				.getNode(ProjectParameters.FolderAndFile.FOLDER_FRTOS_CONFIG).getNodes();
		boolean isFileRecreate = false;
		for (String item : frtosConfigFolder) {
			if (item.equals(ProjectParameters.FolderAndFile.FILE_FREERTOSCONFIG_H)) {
				isFileRecreate = true;
			}
		}
		if (isFileRecreate) {
			assertFalse(true);
		}
	}

	@Test
	public void tc_04_DeleteKernelProject() throws Exception {
		Utility.deleteProject(projectModelSpecific.getProjectName(), true);
		if (bot.activeShell().getText().equals(ProjectParameters.WINDOW_SAVE_RESOURCES)) {
			bot.button(ButtonAction.BUTTON_DONT_SAVE).click();
		}
	}
}
