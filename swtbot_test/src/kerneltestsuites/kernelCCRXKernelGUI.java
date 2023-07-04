package kerneltestsuites;

import static org.junit.Assert.assertFalse;

import java.io.File;

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
	public void tc_02_CheckComponentView() throws Exception {
		Utility.openSCFGEditor(projectModelSpecific);

		bot.tree(1).getTreeItem("Startup").getNode("Generic").getNode("r_bsp").select();

		bot.tree(1).getTreeItem(ProjectParameters.FolderAndFile.FOLDER_RTOS)
				.getNode(ProjectParameters.FolderAndFile.FOLDER_RTOS_KERNEL)
				.getNode(ProjectParameters.RTOSComponent.FREERTOS_KERNEL).select();

		boolean checkConfigureView1 = bot.tree(2).getTreeItem("Configurations ").isVisible();

		Utility.getProjectItemOnProjectExplorer(projectModelSpecific.getProjectName())
				.contextMenu(MenuName.CONTEXT_MENU_CLOSE_PROJECT).click();
		if (bot.activeShell().getText().equals(ProjectParameters.WINDOW_SAVE_RESOURCES)) {
			bot.button(ButtonAction.BUTTON_DONT_SAVE).click();
		}

		Utility.getProjectItemOnProjectExplorer(projectModelSpecific.getProjectName())
				.contextMenu(MenuName.CONTEXT_MENU_OPEN_PROJECT).click();
		Utility.getProjectItemOnProjectExplorer(projectModelSpecific.getProjectName()).click();

		Utility.openSCFGEditor(projectModelSpecific);
		bot.tree(1).getTreeItem(ProjectParameters.FolderAndFile.FOLDER_RTOS)
				.getNode(ProjectParameters.FolderAndFile.FOLDER_RTOS_KERNEL)
				.getNode(ProjectParameters.RTOSComponent.FREERTOS_KERNEL).select();

		boolean checkConfigureView2 = bot.tree(2).getTreeItem("Configurations ").isVisible();

		if (!checkConfigureView1 || !checkConfigureView2) {
			assertFalse(true);
		}
	}

	@Test
	public void tc_03_CheckDatalinkBetweenProperty() throws Exception {
		SWTBotTreeItem[] kernelConfigTree = bot.tree(2).getTreeItem("Configurations ").getItems();
		for (SWTBotTreeItem config : kernelConfigTree) {
			if (config.cell(0).contains("Maximum syscall interrupt priority")) {
				bot.sleep(2000);
				config.click(1);
				config.click(1);
				bot.text(1).setText("5");

			}
		}
		boolean isConsoleContain1 = false;
		boolean isConsoleContain2 = false;
		boolean isConsoleContain3 = false;
		boolean isConsoleContain4 = false;
		boolean isConsoleContain5 = false;
		boolean isConsoleContain6 = false;
		for (SWTBotTreeItem config : kernelConfigTree) {
			if (config.cell(0).contains("Kernel interrupt priority")) {
				bot.sleep(2000);
				config.click(1);
				config.click(1);
				Utility.clickClearConsole();
				bot.text(1).setText("-1");
				isConsoleContain1 = Utility.isConsoleHasString(
						"E04020001: Value must be in range 0 ~ configMAX_SYSCALL_INTERRUPT_PRIORITY - 1");
			}
		}

		for (SWTBotTreeItem config : kernelConfigTree) {
			if (config.cell(0).contains("Kernel interrupt priority")) {
				bot.sleep(2000);
				config.click(1);
				config.click(1);
				Utility.clickClearConsole();
				bot.text(1).setText("0");
				isConsoleContain2 = !Utility.isConsoleHasString(
						"E04020001: Value must be in range 0 ~ configMAX_SYSCALL_INTERRUPT_PRIORITY - 1");
			}
		}

		for (SWTBotTreeItem config : kernelConfigTree) {
			if (config.cell(0).contains("Kernel interrupt priority")) {
				bot.sleep(2000);
				config.click(1);
				config.click(1);
				bot.text(1).setText("1");
				isConsoleContain3 = !Utility.isConsoleHasString(
						"E04020001: Value must be in range 0 ~ configMAX_SYSCALL_INTERRUPT_PRIORITY - 1");

			}
		}

		for (SWTBotTreeItem config : kernelConfigTree) {
			if (config.cell(0).contains("Kernel interrupt priority")) {
				bot.sleep(2000);
				config.click(1);
				config.click(1);
				bot.text(1).setText("3");
				isConsoleContain4 = !Utility.isConsoleHasString(
						"E04020001: Value must be in range 0 ~ configMAX_SYSCALL_INTERRUPT_PRIORITY - 1");

			}
		}

		for (SWTBotTreeItem config : kernelConfigTree) {
			if (config.cell(0).contains("Kernel interrupt priority")) {
				bot.sleep(2000);
				config.click(1);
				config.click(1);
				bot.text(1).setText("4");
				isConsoleContain5 = !Utility.isConsoleHasString(
						"E04020001: Value must be in range 0 ~ configMAX_SYSCALL_INTERRUPT_PRIORITY - 1");

			}
		}

		for (SWTBotTreeItem config : kernelConfigTree) {
			if (config.cell(0).contains("Kernel interrupt priority")) {
				bot.sleep(2000);
				config.click(1);
				config.click(1);
				bot.text(1).setText("5");
				isConsoleContain6 = Utility.isConsoleHasString(
						"E04020001: Value must be in range 0 ~ configMAX_SYSCALL_INTERRUPT_PRIORITY - 1");

			}
		}

		if (!isConsoleContain1 || !isConsoleContain2 || !isConsoleContain3 || !isConsoleContain4 || !isConsoleContain5
				|| !isConsoleContain6) {
			assertFalse(true);
		}
	}

	@Test
	public void tc_03_CheckDatalinkBetweenProperty2() throws Exception {
		SWTBotTreeItem[] kernelConfigTree = bot.tree(2).getTreeItem("Configurations ").getItems();
		for (SWTBotTreeItem config : kernelConfigTree) {
			if (config.cell(0).contains("Kernel interrupt priority")) {
				bot.sleep(2000);
				config.click(1);
				config.click(1);
				bot.text(1).setText("2");

			}
			if (config.cell(0).contains("Maximum number of priorities to the application task")) {
				bot.sleep(2000);
				config.click(1);
				config.click(1);
				bot.text(1).setText("8");

			}
		}
		boolean isConsoleContain1 = false;
		boolean isConsoleContain2 = false;
		boolean isConsoleContain3 = false;
		boolean isConsoleContain4 = false;
		boolean isConsoleContain5 = false;
		boolean isConsoleContain6 = false;
		for (SWTBotTreeItem config : kernelConfigTree) {
			if (config.cell(0).contains("Maximum syscall interrupt priority")) {
				bot.sleep(2000);
				config.click(1);
				config.click(1);
				Utility.clickClearConsole();
				bot.text(1).setText("2");
				isConsoleContain1 = Utility.isConsoleHasString(
						"E04020001: Value must be in range configKERNEL_INTERRUPT_PRIORITY + 1 ~ configMAX_PRIORITIES - 1");
			}
		}
		
		for (SWTBotTreeItem config : kernelConfigTree) {
			if (config.cell(0).contains("Maximum syscall interrupt priority")) {
				bot.sleep(2000);
				config.click(1);
				config.click(1);
				Utility.clickClearConsole();
				bot.text(1).setText("3");
				isConsoleContain2 = !Utility.isConsoleHasString(
						"E04020001: Value must be in range configKERNEL_INTERRUPT_PRIORITY + 1 ~ configMAX_PRIORITIES - 1");
			}
		}
		
		for (SWTBotTreeItem config : kernelConfigTree) {
			if (config.cell(0).contains("Maximum syscall interrupt priority")) {
				bot.sleep(2000);
				config.click(1);
				config.click(1);
				bot.text(1).setText("4");
				isConsoleContain3 = !Utility.isConsoleHasString(
						"E04020001: Value must be in range configKERNEL_INTERRUPT_PRIORITY + 1 ~ configMAX_PRIORITIES - 1");
			}
		}
		
		for (SWTBotTreeItem config : kernelConfigTree) {
			if (config.cell(0).contains("Maximum syscall interrupt priority")) {
				bot.sleep(2000);
				config.click(1);
				config.click(1);
				bot.text(1).setText("6");
				isConsoleContain4 = !Utility.isConsoleHasString(
						"E04020001: Value must be in range configKERNEL_INTERRUPT_PRIORITY + 1 ~ configMAX_PRIORITIES - 1");
			}
		}
		
		for (SWTBotTreeItem config : kernelConfigTree) {
			if (config.cell(0).contains("Maximum syscall interrupt priority")) {
				bot.sleep(2000);
				config.click(1);
				config.click(1);
				bot.text(1).setText("7");
				isConsoleContain5 = !Utility.isConsoleHasString(
						"E04020001: Value must be in range configKERNEL_INTERRUPT_PRIORITY + 1 ~ configMAX_PRIORITIES - 1");
			}
		}
		
		for (SWTBotTreeItem config : kernelConfigTree) {
			if (config.cell(0).contains("Maximum syscall interrupt priority")) {
				bot.sleep(2000);
				config.click(1);
				config.click(1);
				bot.text(1).setText("8");
				isConsoleContain6 = Utility.isConsoleHasString(
						"E04020001: Value must be in range configKERNEL_INTERRUPT_PRIORITY + 1 ~ configMAX_PRIORITIES - 1");
			}
		}
		
		if (!isConsoleContain1 || !isConsoleContain2 || !isConsoleContain3 || !isConsoleContain4 || !isConsoleContain5
				|| !isConsoleContain6) {
			assertFalse(true);
		}
	}
	
	@Test
	public void tc_04_CheckDisableProperty() throws Exception {
		SWTBotTreeItem[] kernelConfigTree = bot.tree(2).getTreeItem("Configurations ").getItems();
		boolean isNotEditable1 = false;
		boolean isNotEditable2 = false;
		boolean isNotEditable3 = false;
		boolean isNotEditable4 = false;
		boolean isNotEditable5 = false;
		boolean isNotEditable6 = false;

		for (SWTBotTreeItem config : kernelConfigTree) {
			if (config.cell(0).contains("The frequency of the CPU clock")) {
				bot.sleep(2000);
				config.click(1);
				config.click(1);
				try {
					bot.text(config.cell(1)).setText("");
				} catch (Exception e) {
					isNotEditable1 = true;
				};

			}
			if (config.cell(0).contains("The frequency of the PERIPHERAL clock")) {
				bot.sleep(2000);
				config.click(1);
				config.click(1);
				try {
					bot.text(config.cell(1)).setText("");
				} catch (Exception e) {
					isNotEditable2 = true;
				};
			}
			if (config.cell(0).contains("The stack depth allocated to the software timer task")) {
				bot.sleep(2000);
				config.click(1);
				config.click(1);
				try {
					bot.text(config.cell(1)).setText("");
				} catch (Exception e) {
					isNotEditable3 = true;
				};
			}
			if (config.cell(0).contains("bktPRIMARY_PRIORITY")) {
				bot.sleep(2000);
				config.click(1);
				config.click(1);
				try {
					bot.text(config.cell(1)).setText("");
				} catch (Exception e) {
					isNotEditable4 = true;
				};
			}
			if (config.cell(0).contains("bktSECONDARY_PRIORITY")) {
				bot.sleep(2000);
				config.click(1);
				config.click(1);
				try {
					bot.text(config.cell(1)).setText("");
				} catch (Exception e) {
					isNotEditable5 = true;
				};
			}
			if (config.cell(0).contains("intqHIGHER_PRIORITY")) {
				bot.sleep(2000);
				config.click(1);
				config.click(1);
				try {
					bot.text(config.cell(1)).setText("");
				} catch (Exception e) {
					isNotEditable6 = true;
				};
			}
		}
		
		if (!isNotEditable1 ||!isNotEditable2 ||!isNotEditable3 ||!isNotEditable4 ||!isNotEditable5 ||!isNotEditable6) {
			assertFalse(true);
		}
	}
	
	@Test
	public void tc_05_CheckRemoveKernelComponent() throws Exception{
		bot.tree(1).getTreeItem(ProjectParameters.FolderAndFile.FOLDER_RTOS)
		.getNode(ProjectParameters.FolderAndFile.FOLDER_RTOS_KERNEL)
		.getNode(ProjectParameters.RTOSComponent.FREERTOS_KERNEL).select();
		
		if(bot.toolbarButtonWithTooltip(ProjectParameters.ButtonAction.BUTTON_REMOVE_COMPONENT).isEnabled()) {
			assertFalse(true);
		};
	}
	
	@Test
	public void tc_06_DeleteKernelProject() throws Exception {
		Utility.deleteProject(projectModelSpecific.getProjectName(), true);
		if (bot.activeShell().getText().equals(ProjectParameters.WINDOW_SAVE_RESOURCES)) {
			bot.button(ButtonAction.BUTTON_DONT_SAVE).click();
		}
	}
}
