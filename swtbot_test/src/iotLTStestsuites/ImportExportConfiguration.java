package iotLTStestsuites;

import static org.junit.Assert.assertFalse;

import java.awt.Robot;
import java.io.File;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
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
import parameters.ProjectParameters.RTOSApplication;
import parameters.ProjectParameters.RTOSType;
import parameters.ProjectParameters.RTOSVersion;
import parameters.ProjectParameters.TargetBoard;
import platform.PlatformModel;
import utilities.PGUtility;
import utilities.Utility;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ImportExportConfiguration {
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
		projectModelSpecific = PGUtility.prepareProjectModel(RTOSType.FREERTOSIOTLTS, RTOSVersion.IoTLTS_202210_1_0_0,
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
		PGUtility.createProject(RTOSType.FREERTOSIOTLTS, RTOSVersion.IoTLTS_202210_1_0_0, RTOSApplication.IOT_LTS_ETHER_PUBSUB,
				Constants.CCRX_TOOLCHAIN, TargetBoard.BOARD_CK_RX65N);
	}
	
	@Test
	public void tc_02_ExportConfig() throws Exception{
		Utility.openSCFGEditor(projectModelSpecific, ProjectParameters.SCFG_COMPONENT_TAB);

		bot.tree(1).getTreeItem(ProjectParameters.FolderAndFile.FOLDER_RTOS)
				.getNode(ProjectParameters.FolderAndFile.FOLDER_RTOS_KERNEL)
				.getNode(ProjectParameters.RTOSComponent.FREERTOS_KERNEL).select();
		
		SWTBotTreeItem[] kernelConfigTree = bot.tree(2).getTreeItem(ProjectParameters.KernelConfig.CONFIGURATIONS)
				.getItems();
		for (SWTBotTreeItem config : kernelConfigTree) {
			Utility.changeConfigOfTextBoxWithIndex(config, ProjectParameters.KernelConfig.MAXIMUM_PRIORITIES_APPLICATION_TASK, "8", false);
			Utility.changeConfigOfTextBoxWithIndex(config, ProjectParameters.KernelConfig.MAXIMUM_PRIORITIES_APPLICATION_CO_ROUTINE, "3", false);
		}
		
		bot.tree(1).getTreeItem(ProjectParameters.FolderAndFile.FOLDER_RTOS)
		.getNode(ProjectParameters.FolderAndFile.FOLDER_RTOS_OBJECT)
		.getNode(ProjectParameters.RTOSComponent.IOT_LTS_FREERTOS_OBJECT).select();
		
		bot.tabItem(ProjectParameters.KernelObjectTab.TASKS).activate();
		Utility.addOrRemoveKernelObject(true, 0);
		
		bot.ccomboBox(0).setSelection("kernel start");
		
		Utility.reFocus(robot);
		Utility.copyLocationToClipBoard(ProjectParameters.FileLocation.IOTLTS_EXPORT);
		bot.toolbarButtonWithTooltip(ProjectParameters.ButtonAction.BUTTON_EXPORT_CONFIG).click();
		bot.button(ProjectParameters.ButtonAction.BUTTON_SELECT_ALL).click();
		bot.button(ProjectParameters.ButtonAction.BUTTON_BROWSE).click();

		Utility.pressCtrlV(robot);
		Utility.pressEnter(robot);
		Utility.pressEnter(robot);
		bot.button(ProjectParameters.ButtonAction.BUTTON_FINISH).click();
		if (bot.activeShell().getText().equals(ProjectParameters.WINDOW_CONFIRM_NEW_FILE)) {
			bot.button(ButtonAction.BUTTON_YES).click();
		}
		Utility.reFocus(robot);
	}
	
	@Test
	public void tc_03_DeleteAndReCreateProject() throws Exception{
		Utility.deleteProject(projectModelSpecific.getProjectName(), true);
		if (bot.activeShell().getText().equals(ProjectParameters.WINDOW_SAVE_RESOURCES)) {
			bot.button(ButtonAction.BUTTON_DONT_SAVE).click();
		}
		
		PGUtility.createProject(RTOSType.FREERTOSIOTLTS, RTOSVersion.IoTLTS_202210_1_0_0, RTOSApplication.IOT_LTS_ETHER_PUBSUB,
				Constants.CCRX_TOOLCHAIN, TargetBoard.BOARD_CK_RX65N);
	}
	
	@Test
	public void tc_04_ImportConfig() throws Exception {
		Utility.openSCFGEditor(projectModelSpecific, ProjectParameters.SCFG_COMPONENT_TAB);

		bot.tree(1).getTreeItem(ProjectParameters.FolderAndFile.FOLDER_RTOS)
				.getNode(ProjectParameters.FolderAndFile.FOLDER_RTOS_KERNEL)
				.getNode(ProjectParameters.RTOSComponent.FREERTOS_KERNEL).select();

		SWTBotTreeItem[] configTree = bot.tree(2).getTreeItem(ProjectParameters.KernelConfig.CONFIGURATIONS).getItems();
		boolean isRightValue = false;
		boolean isRightValue2 = false;

		for (SWTBotTreeItem config : configTree) {
			if (config.cell(0).contains(ProjectParameters.KernelConfig.MAXIMUM_PRIORITIES_APPLICATION_TASK)) {
				isRightValue = config.cell(1).equals("7");
			}
			if (config.cell(0).contains(ProjectParameters.KernelConfig.MAXIMUM_PRIORITIES_APPLICATION_CO_ROUTINE)) {
				isRightValue2 = config.cell(1).equals("2");
			}
		}

		Utility.copyLocationToClipBoard(ProjectParameters.FileLocation.IOTLTS_EXPORT);
		bot.toolbarButtonWithTooltip(ProjectParameters.ButtonAction.BUTTON_IMPORT_CONFIG).click();
		bot.button(ProjectParameters.ButtonAction.BUTTON_BROWSE).click();

		Utility.pressCtrlV(robot);
		Utility.pressEnter(robot);
		Utility.pressEnter(robot);

		bot.button(ProjectParameters.ButtonAction.BUTTON_SELECT_ALL).click();
		bot.button(ProjectParameters.ButtonAction.BUTTON_NEXT).click();
		bot.button(ProjectParameters.ButtonAction.BUTTON_FINISH).click();

		bot.tree(1).getTreeItem(ProjectParameters.FolderAndFile.FOLDER_RTOS)
				.getNode(ProjectParameters.FolderAndFile.FOLDER_RTOS_KERNEL)
				.getNode(ProjectParameters.RTOSComponent.FREERTOS_KERNEL).select();

		configTree = bot.tree(2).getTreeItem(ProjectParameters.KernelConfig.CONFIGURATIONS).getItems();
		boolean isRightValue3 = false;
		boolean isRightValue4 = false;

		for (SWTBotTreeItem config : configTree) {
			if (config.cell(0).contains(ProjectParameters.KernelConfig.MAXIMUM_PRIORITIES_APPLICATION_TASK)) {
				isRightValue3 = config.cell(1).equals("8");
			}
			if (config.cell(0).contains(ProjectParameters.KernelConfig.MAXIMUM_PRIORITIES_APPLICATION_CO_ROUTINE)) {
				isRightValue4 = config.cell(1).equals("3");
			}
		}
		
		if (!isRightValue || !isRightValue2 || !isRightValue3 || !isRightValue4) {
			assertFalse(true);
		}
	}
	
	@Test
	public void tc_05_DeleteIoTLTSProject() throws Exception {
		Utility.deleteProject(projectModelSpecific.getProjectName(), true);
		if (bot.activeShell().getText().equals(ProjectParameters.WINDOW_SAVE_RESOURCES)) {
			bot.button(ButtonAction.BUTTON_DONT_SAVE).click();
		}
	}
}
