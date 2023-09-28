package kerneltestsuites;

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
import parameters.ProjectParameters.MenuName;
import parameters.ProjectParameters.RTOSApplication;
import parameters.ProjectParameters.RTOSType;
import parameters.ProjectParameters.RTOSVersion;
import parameters.ProjectParameters.TargetBoard;
import platform.PlatformModel;
import utilities.PGUtility;
import utilities.Utility;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class kernelCCRXKernelGUI {
	private static SWTWorkbenchBot bot;
	private static Robot robot;
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
		changeView();
	}

	private static void closeWelcomePage() {
		for (SWTBotView view : bot.views()) {
			if (view.getTitle().equals("Welcome")) {
				view.close();
			}
		}
	}
	
	private static void changeView() {
		bot.defaultPerspective().activate();
	}
	
	@Test
	public void tc_00_ChangeRTOSLocation() throws Exception{
		Utility.changeModuleDownloadLocation(robot, ProjectParameters.FileLocation.KERNEL_RTOS_LOCATION, true);
		Utility.changeModuleDownloadLocation(robot, ProjectParameters.FileLocation.FIT_MODULES_LOCATION, false);
		Utility.reFocus(robot);
	}

	@Test
	public void tc_01_CreateKernelProject() throws Exception {
		PGUtility.createProject(RTOSType.FREERTOSKERNEL, RTOSVersion.Kernel_1_0_7, RTOSApplication.KERNEL_BARE,
				Constants.CCRX_TOOLCHAIN, TargetBoard.BOARD_RSK_RX65N_2MB);

	}

	@Test
	public void tc_02_CheckComponentView() throws Exception {
		Utility.openSCFGEditor(projectModelSpecific, ProjectParameters.SCFG_COMPONENT_TAB);

		bot.tree(1).getTreeItem("Startup").getNode("Generic").getNode("r_bsp").select();

		bot.tree(1).getTreeItem(ProjectParameters.FolderAndFile.FOLDER_RTOS)
				.getNode(ProjectParameters.FolderAndFile.FOLDER_RTOS_KERNEL)
				.getNode(ProjectParameters.RTOSComponent.FREERTOS_KERNEL).select();

		boolean checkConfigureView1 = bot.tree(2).getTreeItem(ProjectParameters.KernelConfig.CONFIGURATIONS)
				.isVisible();

		Utility.getProjectItemOnProjectExplorer(projectModelSpecific.getProjectName())
				.contextMenu(MenuName.CONTEXT_MENU_CLOSE_PROJECT).click();
		if (bot.activeShell().getText().equals(ProjectParameters.WINDOW_SAVE_RESOURCES)) {
			bot.button(ButtonAction.BUTTON_DONT_SAVE).click();
		}

		Utility.getProjectItemOnProjectExplorer(projectModelSpecific.getProjectName())
				.contextMenu(MenuName.CONTEXT_MENU_OPEN_PROJECT).click();
		Utility.getProjectItemOnProjectExplorer(projectModelSpecific.getProjectName()).click();

		Utility.openSCFGEditor(projectModelSpecific, ProjectParameters.SCFG_COMPONENT_TAB);
		bot.tree(1).getTreeItem(ProjectParameters.FolderAndFile.FOLDER_RTOS)
				.getNode(ProjectParameters.FolderAndFile.FOLDER_RTOS_KERNEL)
				.getNode(ProjectParameters.RTOSComponent.FREERTOS_KERNEL).select();

		boolean checkConfigureView2 = bot.tree(2).getTreeItem(ProjectParameters.KernelConfig.CONFIGURATIONS)
				.isVisible();

		if (!checkConfigureView1 || !checkConfigureView2) {
			assertFalse(true);
		}
	}

	@Test
	public void tc_03_CheckDatalinkBetweenProperty() throws Exception {
		SWTBotTreeItem[] kernelConfigTree = bot.tree(2).getTreeItem(ProjectParameters.KernelConfig.CONFIGURATIONS)
				.getItems();
		for (SWTBotTreeItem config : kernelConfigTree) {
			Utility.changeConfigOfTextBoxWithIndex(config, ProjectParameters.KernelConfig.MAX_SYSCALL, "5", false);
		}
		boolean isConsoleContain1 = false;
		boolean isConsoleContain2 = false;
		boolean isConsoleContain3 = false;
		boolean isConsoleContain4 = false;
		boolean isConsoleContain5 = false;
		boolean isConsoleContain6 = false;
		for (SWTBotTreeItem config : kernelConfigTree) {
			Utility.changeConfigOfTextBoxWithIndex(config, ProjectParameters.KernelConfig.KERNEL_INTERRUPT_PRIORITY, "-1", true);
		}
		isConsoleContain1 = Utility.isConsoleHasString(ProjectParameters.MessageCode.E04020001);
		for (SWTBotTreeItem config : kernelConfigTree) {
			Utility.changeConfigOfTextBoxWithIndex(config, ProjectParameters.KernelConfig.KERNEL_INTERRUPT_PRIORITY, "0", true);

		}
		isConsoleContain2 = !Utility.isConsoleHasString(ProjectParameters.MessageCode.E04020001);

		for (SWTBotTreeItem config : kernelConfigTree) {
			Utility.changeConfigOfTextBoxWithIndex(config, ProjectParameters.KernelConfig.KERNEL_INTERRUPT_PRIORITY, "1", false);

		}
		isConsoleContain3 = !Utility.isConsoleHasString(ProjectParameters.MessageCode.E04020001);

		for (SWTBotTreeItem config : kernelConfigTree) {
			Utility.changeConfigOfTextBoxWithIndex(config, ProjectParameters.KernelConfig.KERNEL_INTERRUPT_PRIORITY, "3", false);

		}
		isConsoleContain4 = !Utility.isConsoleHasString(ProjectParameters.MessageCode.E04020001);

		for (SWTBotTreeItem config : kernelConfigTree) {
			Utility.changeConfigOfTextBoxWithIndex(config, ProjectParameters.KernelConfig.KERNEL_INTERRUPT_PRIORITY, "4", false);

		}
		isConsoleContain5 = !Utility.isConsoleHasString(ProjectParameters.MessageCode.E04020001);

		for (SWTBotTreeItem config : kernelConfigTree) {
			Utility.changeConfigOfTextBoxWithIndex(config, ProjectParameters.KernelConfig.KERNEL_INTERRUPT_PRIORITY, "5", false);

		}
		isConsoleContain6 = Utility.isConsoleHasString(ProjectParameters.MessageCode.E04020001);

		if (!isConsoleContain1 || !isConsoleContain2 || !isConsoleContain3 || !isConsoleContain4 || !isConsoleContain5
				|| !isConsoleContain6) {
			assertFalse(true);
		}
	}

	@Test
	public void tc_03_CheckDatalinkBetweenProperty2() throws Exception {
		SWTBotTreeItem[] kernelConfigTree = bot.tree(2).getTreeItem(ProjectParameters.KernelConfig.CONFIGURATIONS)
				.getItems();
		for (SWTBotTreeItem config : kernelConfigTree) {
			Utility.changeConfigOfTextBoxWithIndex(config, ProjectParameters.KernelConfig.KERNEL_INTERRUPT_PRIORITY, "2", false);
			Utility.changeConfigOfTextBoxWithIndex(config, ProjectParameters.KernelConfig.MAXIMUM_PRIORITIES_APPLICATION_TASK, "8", false);
		}
		boolean isConsoleContain1 = false;
		boolean isConsoleContain2 = false;
		boolean isConsoleContain3 = false;
		boolean isConsoleContain4 = false;
		boolean isConsoleContain5 = false;
		boolean isConsoleContain6 = false;
		for (SWTBotTreeItem config : kernelConfigTree) {
			Utility.changeConfigOfTextBoxWithIndex(config, ProjectParameters.KernelConfig.MAX_SYSCALL, "2", true);
		}
		isConsoleContain1 = Utility.isConsoleHasString(ProjectParameters.MessageCode.E04020001_2);

		for (SWTBotTreeItem config : kernelConfigTree) {
			Utility.changeConfigOfTextBoxWithIndex(config, ProjectParameters.KernelConfig.MAX_SYSCALL, "3", true);
		}
		isConsoleContain2 = !Utility.isConsoleHasString(ProjectParameters.MessageCode.E04020001_2);

		for (SWTBotTreeItem config : kernelConfigTree) {
			Utility.changeConfigOfTextBoxWithIndex(config, ProjectParameters.KernelConfig.MAX_SYSCALL, "4", false);
		}
		isConsoleContain3 = !Utility.isConsoleHasString(ProjectParameters.MessageCode.E04020001_2);

		for (SWTBotTreeItem config : kernelConfigTree) {
			Utility.changeConfigOfTextBoxWithIndex(config, ProjectParameters.KernelConfig.MAX_SYSCALL, "6", false);

		}
		isConsoleContain4 = !Utility.isConsoleHasString(ProjectParameters.MessageCode.E04020001_2);

		for (SWTBotTreeItem config : kernelConfigTree) {
			Utility.changeConfigOfTextBoxWithIndex(config, ProjectParameters.KernelConfig.MAX_SYSCALL, "7", false);

		}
		isConsoleContain5 = !Utility.isConsoleHasString(ProjectParameters.MessageCode.E04020001_2);

		for (SWTBotTreeItem config : kernelConfigTree) {
			Utility.changeConfigOfTextBoxWithIndex(config, ProjectParameters.KernelConfig.MAX_SYSCALL, "8", false);

		}
		isConsoleContain6 = Utility.isConsoleHasString(ProjectParameters.MessageCode.E04020001_2);

		if (!isConsoleContain1 || !isConsoleContain2 || !isConsoleContain3 || !isConsoleContain4 || !isConsoleContain5
				|| !isConsoleContain6) {
			assertFalse(true);
		}
	}

	@Test
	public void tc_04_CheckDisableProperty() throws Exception {
		SWTBotTreeItem[] kernelConfigTree = bot.tree(2).getTreeItem(ProjectParameters.KernelConfig.CONFIGURATIONS)
				.getItems();
		boolean isNotEditable1 = false;
		boolean isNotEditable2 = false;
		boolean isNotEditable3 = false;
		boolean isNotEditable4 = false;
		boolean isNotEditable5 = false;
		boolean isNotEditable6 = false;

		for (SWTBotTreeItem config : kernelConfigTree) {
			if (Utility.changeConfigOfTextBoxVerifyError(config, ProjectParameters.KernelConfig.THE_FREQUENCY_OF_THE_CPU_CLOCK, "", false)) {
				isNotEditable1 = true;
			}
			if(Utility.changeConfigOfTextBoxVerifyError(config, ProjectParameters.KernelConfig.THE_FREQUENCY_OF_PHERIPHERAL_CLOCK, "", false)) {
				isNotEditable2 = true;
			}
			if(Utility.changeConfigOfTextBoxVerifyError(config, ProjectParameters.KernelConfig.THE_DEPTH_ALLOCATE_SW_TIMER_TASK, "", false)) {
				isNotEditable3 = true;
			}
			if(Utility.changeConfigOfTextBoxVerifyError(config, ProjectParameters.KernelConfig.BKT_PRIMARY_PRIORITY, "", false)) {
				isNotEditable4 = true;
			}
			if(Utility.changeConfigOfTextBoxVerifyError(config, ProjectParameters.KernelConfig.BKT_SECONDARY_PRIORITY, "", false)) {
				isNotEditable5 = true;
			}
			if(Utility.changeConfigOfTextBoxVerifyError(config, ProjectParameters.KernelConfig.INTQ_HIGHER_PRIORITY, "", false)) {
				isNotEditable6 = true;
			}
		}

		if (!isNotEditable1 || !isNotEditable2 || !isNotEditable3 || !isNotEditable4 || !isNotEditable5
				|| !isNotEditable6) {
			assertFalse(true);
		}
	}

	@Test
	public void tc_05_CheckRemoveKernelComponent() throws Exception {
		bot.tree(1).getTreeItem(ProjectParameters.FolderAndFile.FOLDER_RTOS)
				.getNode(ProjectParameters.FolderAndFile.FOLDER_RTOS_KERNEL)
				.getNode(ProjectParameters.RTOSComponent.FREERTOS_KERNEL).select();

		if (bot.toolbarButtonWithTooltip(ProjectParameters.ButtonAction.BUTTON_REMOVE_COMPONENT).isEnabled()) {
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
