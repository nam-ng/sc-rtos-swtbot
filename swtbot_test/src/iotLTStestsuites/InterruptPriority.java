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
public class InterruptPriority {
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
	public void tc_02_CheckInterruptPriority() throws Exception {
		Utility.openSCFGEditor(projectModelSpecific, ProjectParameters.SCFG_COMPONENT_TAB);
		Utility.addComponent("r_ether_rx");
		Utility.clickClearConsole();

		bot.tree(1).getTreeItem(ProjectParameters.FolderAndFile.FOLDER_DRIVERS)
		.getNode(ProjectParameters.FolderAndFile.FOLDER_COMMUNICATIONS)
		.getNode(ProjectParameters.RTOSComponent.R_ETHER_RX).select();
		
		SWTBotTreeItem[] amazonConfigTree = bot.tree(2).getTreeItem(ProjectParameters.KernelConfig.CONFIGURATIONS)
				.getItems();
		for (SWTBotTreeItem config : amazonConfigTree) {
			Utility.changeConfigOfCombobox(config, ProjectParameters.KernelConfig.GROUP_AL1_INTERRUPT, "Level 6");
		}
		
		Utility.reFocus(robot);
		bot.sleep(5000);
		boolean checkConsole = Utility.isConsoleHasString("W04050002: EDMAC0_EINT0 interrupt priority of r_ether_rx should be set lower than configMAX_SYSCALL_INTERRUPT_PRIORITY(4) if ISR calls RTOS API functions.");
		Utility.clickClearConsole();
		
		SWTBotTreeItem[] items = bot.tree(3).getAllItems();
		boolean checkConfigurationProblem = false;
		for (SWTBotTreeItem item: items) {
			if (item.cell(0).contains("Interrupt")) {
				item.expand();
				SWTBotTreeItem[] messages = item.getItems();
				for (SWTBotTreeItem message: messages) {
					if (message.cell(0).contains("W04050002: EDMAC0_EINT0 interrupt priority of r_ether_rx should be set lower than configMAX_SYSCALL_INTERRUPT_PRIORITY(4) if ISR calls RTOS API functions.")) {
						checkConfigurationProblem = true;
					}
				}
			}
		}
		
		for (SWTBotTreeItem config : amazonConfigTree) {
			Utility.changeConfigOfCombobox(config, ProjectParameters.KernelConfig.GROUP_AL1_INTERRUPT, "Level 3");
		}
		Utility.reFocus(robot);
		bot.sleep(5000);
		boolean checkConsole2 = Utility.isConsoleHasString("W04050002: EDMAC0_EINT0 interrupt priority of r_ether_rx should be set lower than configMAX_SYSCALL_INTERRUPT_PRIORITY(4) if ISR calls RTOS API functions.");
		
		boolean checkConfigurationProblem2 = false;
		for (SWTBotTreeItem item: items) {
			if (item.cell(0).contains("Interrupt")) {
				item.expand();
				SWTBotTreeItem[] messages = item.getItems();
				for (SWTBotTreeItem message: messages) {
					if (message.cell(0).contains("W04050002: EDMAC0_EINT0 interrupt priority of r_ether_rx should be set lower than configMAX_SYSCALL_INTERRUPT_PRIORITY(4) if ISR calls RTOS API functions.")) {
						checkConfigurationProblem2 = true;
					}
				}
			}
		}
		
		if (!checkConfigurationProblem || !checkConsole || checkConfigurationProblem2 || checkConsole2) {
			assertFalse(true);
		}
	}
	
	@Test
	public void tc_03_DeleteIoTLTSProject() throws Exception {
		Utility.deleteProject(projectModelSpecific.getProjectName(), true);
		if (bot.activeShell().getText().equals(ProjectParameters.WINDOW_SAVE_RESOURCES)) {
			bot.button(ButtonAction.BUTTON_DONT_SAVE).click();
		}
	}
}
