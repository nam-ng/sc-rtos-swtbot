package iotLTStestsuites;

import static org.junit.Assert.assertFalse;

import java.awt.Robot;
import java.io.File;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotEclipseEditor;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotView;
import org.eclipse.swtbot.swt.finder.utils.SWTBotPreferences;
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
import parameters.ProjectParameters.MenuName;
import parameters.ProjectParameters.RTOSApplication;
import parameters.ProjectParameters.RTOSType;
import parameters.ProjectParameters.RTOSVersion;
import parameters.ProjectParameters.TargetBoard;
import platform.PlatformModel;
import utilities.PGUtility;
import utilities.Utility;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class KernelConfigure {
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
	public void tc_02_CheckDataAfterConfig() throws Exception {
		Utility.checkDataAfterConfig(projectModelSpecific);
	}
	
	@Test
	public void tc_03_TestRemoveKernelComponent() throws Exception{
		Utility.testRemoveKernelComponent();
	}
	
	@Test
	public void tc_04_CheckSwitchOtherViewAndGenCode() throws Exception{
		bot.tree(1).getTreeItem(ProjectParameters.FolderAndFile.FOLDER_RTOS)
		.getNode(ProjectParameters.FolderAndFile.FOLDER_RTOS_KERNEL)
		.getNode(ProjectParameters.RTOSComponent.FREERTOS_KERNEL).select();
		
		SWTBotTreeItem[] amazonConfigTree = bot.tree(2).getTreeItem(ProjectParameters.KernelConfig.CONFIGURATIONS)
				.getItems();
		for (SWTBotTreeItem config : amazonConfigTree) {
			Utility.changeConfigOfTextBox(config, ProjectParameters.AmazonConfig.THE_FREQUENCY_OF_RTOS_TICK_INTERRUPT, "test1", false);
			Utility.changeConfigOfTextBox(config, ProjectParameters.AmazonConfig.MAXIMUM_PRIORITIES_APPLICATION_TASK, "5", false);
		}
		
		bot.menu(MenuName.MENU_FILE).menu(MenuName.MENU_SAVE).click();
		bot.tree(1).getTreeItem(ProjectParameters.FolderAndFile.FOLDER_RTOS)
				.getNode(ProjectParameters.FolderAndFile.FOLDER_RTOS_OBJECT)
				.getNode(ProjectParameters.RTOSComponent.IOT_LTS_FREERTOS_OBJECT).select();

		bot.tree(1).getTreeItem(ProjectParameters.FolderAndFile.FOLDER_RTOS)
				.getNode(ProjectParameters.FolderAndFile.FOLDER_RTOS_KERNEL)
				.getNode(ProjectParameters.RTOSComponent.FREERTOS_KERNEL).select();
		
		Utility.clickGenerateCode();
		
		amazonConfigTree = bot.tree(2).getTreeItem(ProjectParameters.KernelConfig.CONFIGURATIONS)
				.getItems();
		boolean isRightValue1 = false;
		boolean isRightValue2 = false;
		for (SWTBotTreeItem config : amazonConfigTree) {
			if (config.cell(0).contains(ProjectParameters.AmazonConfig.THE_FREQUENCY_OF_RTOS_TICK_INTERRUPT)) {
				isRightValue1 = config.cell(1).equals("test1");
			}
			if (config.cell(0).contains(ProjectParameters.AmazonConfig.MAXIMUM_PRIORITIES_APPLICATION_TASK)) {
				isRightValue2 = config.cell(1).equals("5");
			}
		}
		
		if (!isRightValue1 || !isRightValue2) {
			assertFalse(true);
		}
	}
	
	@Test
	public void tc_05_OpenAndCheckComponent() throws Exception {
		Utility.openSCFGEditor(projectModelSpecific, ProjectParameters.SCFG_COMPONENT_TAB);
		
		bot.tree(1).getTreeItem(ProjectParameters.FolderAndFile.FOLDER_RTOS)
				.getNode(ProjectParameters.FolderAndFile.FOLDER_RTOS_OBJECT)
				.getNode(ProjectParameters.RTOSComponent.IOT_LTS_FREERTOS_OBJECT).select();

		bot.tree(1).getTreeItem(ProjectParameters.FolderAndFile.FOLDER_RTOS)
				.getNode(ProjectParameters.FolderAndFile.FOLDER_RTOS_KERNEL)
				.getNode(ProjectParameters.RTOSComponent.FREERTOS_KERNEL).select();
		
		boolean isConfigViewHasItem = bot.tree(2).getTreeItem(ProjectParameters.KernelConfig.CONFIGURATIONS).isEnabled();
		
		if(!isConfigViewHasItem) {
			assertFalse(true);
		}
	}
	
	@Test
	public void tc_06_CheckOutputHeaderFile() throws Exception {
		Utility.openSCFGEditor(projectModelSpecific, ProjectParameters.SCFG_COMPONENT_TAB);

		bot.tree(1).getTreeItem(ProjectParameters.FolderAndFile.FOLDER_RTOS)
				.getNode(ProjectParameters.FolderAndFile.FOLDER_RTOS_KERNEL)
				.getNode(ProjectParameters.RTOSComponent.FREERTOS_KERNEL).select();
		
		SWTBotTreeItem[] amazonConfigTree = bot.tree(2).getTreeItem(ProjectParameters.KernelConfig.CONFIGURATIONS)
				.getItems();
		for (SWTBotTreeItem config : amazonConfigTree) {
			Utility.changeConfigOfTextBox(config, ProjectParameters.AmazonConfig.MAXIMUM_PRIORITIES_APPLICATION_TASK, "8", false);
		}
		
		bot.menu(MenuName.MENU_FILE).menu(MenuName.MENU_SAVE).click();
		Utility.clickGenerateCode();
		
		String buildType = projectModelSpecific.getActiveBuildConfiguration();
		bot.tree().getTreeItem(projectModelSpecific.getProjectName()).select();
		bot.tree().getTreeItem(projectModelSpecific.getProjectName() + " [" + buildType + "]").expand();
		bot.tree().getTreeItem(projectModelSpecific.getProjectName() + " [" + buildType + "]")
				.getNode(ProjectParameters.FolderAndFile.FOLDER_SRC).expand();
		bot.tree().getTreeItem(projectModelSpecific.getProjectName() + " [" + buildType + "]")
				.getNode(ProjectParameters.FolderAndFile.FOLDER_SRC)
				.getNode(ProjectParameters.FolderAndFile.FOLDER_FRTOS_CONFIG).expand();
		bot.tree().getTreeItem(projectModelSpecific.getProjectName() + " [" + buildType + "]")
				.getNode(ProjectParameters.FolderAndFile.FOLDER_SRC)
				.getNode(ProjectParameters.FolderAndFile.FOLDER_FRTOS_CONFIG)
				.getNode(ProjectParameters.FolderAndFile.FILE_FREERTOSCONFIG_H).doubleClick();
		
		String value = """
#define configMAX_PRIORITIES                       (8)
				""";
		
		SWTBotEclipseEditor Editor = bot.editorByTitle(ProjectParameters.FolderAndFile.FILE_FREERTOSCONFIG_H)
				.toTextEditor();
		String textOfHFile = Editor.getText();
		boolean isFileContainsText = textOfHFile.contains(value);
		if (!isFileContainsText) {
			assertFalse(true);
		}
	}
	
	@Test
	public void tc_07_CheckDataIsNotLost() throws Exception{
		Utility.openSCFGEditor(projectModelSpecific, ProjectParameters.SCFG_COMPONENT_TAB);
		
		bot.tree(1).getTreeItem(ProjectParameters.FolderAndFile.FOLDER_RTOS)
		.getNode(ProjectParameters.FolderAndFile.FOLDER_RTOS_KERNEL)
		.getNode(ProjectParameters.RTOSComponent.FREERTOS_KERNEL).select();
		
		SWTBotTreeItem[] amazonConfigTree = bot.tree(2).getTreeItem(ProjectParameters.KernelConfig.CONFIGURATIONS)
				.getItems();
		for (SWTBotTreeItem config : amazonConfigTree) {
			Utility.changeConfigOfCombobox(config, ProjectParameters.AmazonConfig.RTOS_SCHEDULER, "Cooperative");
			Utility.changeConfigOfTextBox(config, ProjectParameters.AmazonConfig.MAXIMUM_PRIORITIES_APPLICATION_TASK, "6", false);
		}
		
		bot.tree(1).getTreeItem(ProjectParameters.FolderAndFile.FOLDER_RTOS)
				.getNode(ProjectParameters.FolderAndFile.FOLDER_RTOS_OBJECT)
				.getNode(ProjectParameters.RTOSComponent.IOT_LTS_FREERTOS_OBJECT).select();

		bot.tree(1).getTreeItem(ProjectParameters.FolderAndFile.FOLDER_RTOS)
				.getNode(ProjectParameters.FolderAndFile.FOLDER_RTOS_KERNEL)
				.getNode(ProjectParameters.RTOSComponent.FREERTOS_KERNEL).select();
		
		
		amazonConfigTree = bot.tree(2).getTreeItem(ProjectParameters.KernelConfig.CONFIGURATIONS)
				.getItems();
		boolean isRightValue1 = false;
		boolean isRightValue2 = false;
		for (SWTBotTreeItem config : amazonConfigTree) {
			if (config.cell(0).contains(ProjectParameters.AmazonConfig.RTOS_SCHEDULER)) {
				isRightValue1 = config.cell(1).equals("Cooperative");
			}
			if (config.cell(0).contains(ProjectParameters.AmazonConfig.MAXIMUM_PRIORITIES_APPLICATION_TASK)) {
				isRightValue2 = config.cell(1).equals("6");
			}
		}
		
		if (!isRightValue1 || !isRightValue2) {
			assertFalse(true);
		}
	}
	
	@Test
	public void tc_08_HeaderFileGenToCorrectPath() throws Exception{
		bot.tree(1).getTreeItem(ProjectParameters.FolderAndFile.FOLDER_RTOS)
		.getNode(ProjectParameters.FolderAndFile.FOLDER_RTOS_KERNEL)
		.getNode(ProjectParameters.RTOSComponent.FREERTOS_KERNEL).select();
		
		SWTBotTreeItem[] amazonConfigTree = bot.tree(2).getTreeItem(ProjectParameters.KernelConfig.CONFIGURATIONS)
				.getItems();
		for (SWTBotTreeItem config : amazonConfigTree) {
			Utility.changeConfigOfTextBox(config, ProjectParameters.AmazonConfig.THE_FREQUENCY_OF_RTOS_TICK_INTERRUPT, "test", false);
			Utility.changeConfigOfTextBox(config, ProjectParameters.AmazonConfig.MAXIMUM_PRIORITIES_APPLICATION_TASK, "5", false);
		}
		bot.menu(MenuName.MENU_FILE).menu(MenuName.MENU_SAVE).click();
		Utility.clickGenerateCode();

		String buildType = projectModelSpecific.getActiveBuildConfiguration();
		bot.tree().getTreeItem(projectModelSpecific.getProjectName()).select();
		bot.tree().getTreeItem(projectModelSpecific.getProjectName() + " [" + buildType + "]").expand();
		bot.tree().getTreeItem(projectModelSpecific.getProjectName() + " [" + buildType + "]")
				.getNode(ProjectParameters.FolderAndFile.FOLDER_SRC).expand();
		bot.tree().getTreeItem(projectModelSpecific.getProjectName() + " [" + buildType + "]")
				.getNode(ProjectParameters.FolderAndFile.FOLDER_SRC)
				.getNode(ProjectParameters.FolderAndFile.FOLDER_FRTOS_CONFIG).expand();
		bot.tree().getTreeItem(projectModelSpecific.getProjectName() + " [" + buildType + "]")
				.getNode(ProjectParameters.FolderAndFile.FOLDER_SRC)
				.getNode(ProjectParameters.FolderAndFile.FOLDER_FRTOS_CONFIG)
				.getNode(ProjectParameters.FolderAndFile.FILE_FREERTOSCONFIG_H).doubleClick();
		
		String value1 = """
#define configMAX_PRIORITIES                       (5)
				""";
		String value2 = """
#define configTICK_RATE_HZ                         (test)
				""";
		
		SWTBotEclipseEditor Editor = bot.editorByTitle(ProjectParameters.FolderAndFile.FILE_FREERTOSCONFIG_H)
				.toTextEditor();
		String textOfHFile = Editor.getText();
		boolean isFileContainsText1 = textOfHFile.contains(value1);
		boolean isFileContainsText2 = textOfHFile.contains(value2);

		if (!isFileContainsText1 || !isFileContainsText2) {
			assertFalse(true);
		}
	}
	
	@Test
	public void tc_09_DeleteIoTLTSProject() throws Exception {
		Utility.deleteProject(projectModelSpecific.getProjectName(), true);
		if (bot.activeShell().getText().equals(ProjectParameters.WINDOW_SAVE_RESOURCES)) {
			bot.button(ButtonAction.BUTTON_DONT_SAVE).click();
		}
	}
}
