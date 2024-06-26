package testsuitesfor202407;

import static org.junit.Assert.assertFalse;

import java.awt.Robot;
import java.io.File;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.eclipse.finder.matchers.WidgetMatcherFactory;
import org.eclipse.swtbot.eclipse.finder.waits.Conditions;
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
import parameters.ProjectParameters.BankMode;
import parameters.ProjectParameters.ButtonAction;
import parameters.ProjectParameters.LabelName;
import parameters.ProjectParameters.MenuName;
import parameters.ProjectParameters.RTOSApplication;
import parameters.ProjectParameters.RTOSDisplay;
import parameters.ProjectParameters.RTOSDisplayImport;
import parameters.ProjectParameters.RTOSType;
import parameters.ProjectParameters.RTOSVersion;
import parameters.ProjectParameters.TargetBoard;
import platform.PlatformModel;
import utilities.PGUtility;
import utilities.Utility;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class RSC_17365_AddRemoveComponentLTS {
	private static final String PLATFORM_XML_FILE = "xml/platformdata.xml";
	private static final String RTOS_PG_XML_FILE = "xml/rtospg.xml";
	private static SWTWorkbenchBot bot;
	private static Robot robot;
	private static final Pattern REVISION_PATTERN_RC = Pattern.compile("[-_][rR][cC](\\d+)?$");
	private static final String RC_SUFFIX = "rc";
	private static ProjectModel projectModelSpecific = new ProjectModel();

	@BeforeClass
	public static void beforeClass() throws Exception {
		bot = new SWTWorkbenchBot();
		PlatformModel.loadPlatformModel(new File(Utility.getBundlePath(LogUtil.PLUGIN_ID, PLATFORM_XML_FILE)));
		RTOSManager.loadRTOSModel(new File(Utility.getBundlePath(LogUtil.PLUGIN_ID, RTOS_PG_XML_FILE)));
		projectModelSpecific = PGUtility.prepareProjectModel(RTOSType.FREERTOSIOTLTS, RTOSVersion.IoTLTS_202210_1_2_1,
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
	public void tc_00_ChangeRTOSLocation() throws Exception {
		Utility.changeModuleDownloadLocation(robot, ProjectParameters.FileLocation.TEST_ADD_REMOVE_LTS_COMPONENT, true);
		Utility.changeModuleDownloadLocation(robot, ProjectParameters.FileLocation.NEWEST_FIT_MODULES_LOCATION, false);
		Utility.reFocus(robot);
	}
	
	@Test
	public void tc_01_TestAddRemoveComponent() throws Exception {
		PGUtility.createProject(RTOSType.FREERTOSIOTLTS, RTOSVersion.IoTLTS_202210_1_2_1, RTOSApplication.IOT_LTS_ETHER_PUBSUB,
				Constants.CCRX_TOOLCHAIN, TargetBoard.BOARD_CK_RX65N);
		
		Utility.openSCFGEditor(projectModelSpecific, ProjectParameters.SCFG_COMPONENT_TAB);
		Utility.addComponent("FreeRTOS TCP/IP component");
		Utility.clickGenerateCode();
		
		String buildType = projectModelSpecific.getActiveBuildConfiguration();
		boolean isAddedOK = Utility.CheckAddRemoveLTSResourceAndSetting(projectModelSpecific, buildType, true);
		
		Utility.removeComponent(ProjectParameters.RTOSComponent.FREERTOS_IP);
		Utility.clickGenerateCode();
		
		boolean isRemoveOK = Utility.CheckAddRemoveLTSResourceAndSetting(projectModelSpecific, buildType, false);
		
		if (!isAddedOK || !isRemoveOK) {
			assertFalse(true);
		}
	}
	
	
	
	@Test
	public void tc_02_TestRemoveComponentRemoveButtonReload() throws Exception {
		Utility.addComponent("FreeRTOS TCP/IP component");
		Utility.clickGenerateCode();
		Utility.removeComponent(ProjectParameters.RTOSComponent.FREERTOS_IP);
		boolean cannotBeRemoved = !bot.toolbarButtonWithTooltip(ProjectParameters.ButtonAction.BUTTON_REMOVE_COMPONENT)
				.isEnabled();

		bot.tree(1).getTreeItem(ProjectParameters.FolderAndFile.FOLDER_DRIVERS)
				.getNode(ProjectParameters.FolderAndFile.FOLDER_COMMUNICATIONS)
				.getNode(ProjectParameters.RTOSComponent.R_ETHER_RX).select();
		bot.toolbarButtonWithTooltip(ProjectParameters.ButtonAction.BUTTON_REMOVE_COMPONENT).click();
		if (bot.activeShell().getText().contains(ProjectParameters.WINDOW_QUESTION)) {
			bot.button(ProjectParameters.ButtonAction.BUTTON_YES).click();
		}
		boolean canBeRemoved = bot.toolbarButtonWithTooltip(ProjectParameters.ButtonAction.BUTTON_REMOVE_COMPONENT)
				.isEnabled();
		
		if (!cannotBeRemoved || !canBeRemoved) {
			assertFalse(true);
		}
	}
	
	@Test
	public void tc_03_TestComponentSettingAddRemoveLTSComponent() throws Exception {
		bot.tree(1).getTreeItem(ProjectParameters.FolderAndFile.FOLDER_RTOS)
				.getNode(ProjectParameters.FolderAndFile.FOLDER_RTOS_KERNEL)
				.getNode(ProjectParameters.RTOSComponent.FREERTOS_KERNEL).select();
		bot.toolbarButtonWithTooltip(ProjectParameters.ButtonAction.BUTTON_REMOVE_COMPONENT).click();
		if (bot.activeShell().getText().contains(ProjectParameters.WINDOW_QUESTION)) {
			bot.button(ProjectParameters.ButtonAction.BUTTON_YES).click();
		}
		Utility.addComponent("FreeRTOS TCP/IP component");
		Utility.clickGenerateCode();

		String buildType = projectModelSpecific.getActiveBuildConfiguration();
		boolean isAddedOK = Utility.checkAddRemoveLTSComponentSetting(projectModelSpecific, buildType, true);
		boolean isDependencyAdded = bot.tree(1).getTreeItem(ProjectParameters.FolderAndFile.FOLDER_RTOS)
				.getNode(ProjectParameters.FolderAndFile.FOLDER_RTOS_KERNEL)
				.getNode(ProjectParameters.RTOSComponent.FREERTOS_KERNEL).isVisible();

		bot.tree(1).getTreeItem(ProjectParameters.FolderAndFile.FOLDER_RTOS)
				.getNode(ProjectParameters.FolderAndFile.FOLDER_RTOS_LIBRARY)
				.getNode(ProjectParameters.RTOSComponent.FREERTOS_IP).select();
		SWTBotTreeItem[] configTree = bot.tree(2).getTreeItem(ProjectParameters.KernelConfig.CONFIGURATIONS)
				.getItems();
		for (SWTBotTreeItem config : configTree) {
			Utility.changeConfigOfCombobox(config, ProjectParameters.AmazonConfig.REPORT_USAGE, "Disable");
			if (config.cell(0).contains("Enable application hook event when the network goes up and when it goes down")) {
				bot.sleep(2000);
				config.click(1);
				bot.button(ButtonAction.BUTTON_YES).click();
			}
		}
		Utility.clickGenerateCode();

		boolean isConfigDisableOK = Utility.checkAddRemoveLTSComponentSetting(projectModelSpecific, buildType, false);
		boolean isDependencyRemoved = true;
		List<String> items= bot.tree(1).getTreeItem(ProjectParameters.FolderAndFile.FOLDER_RTOS).getNodes();
		for (String item:items) {
			if (item.equals(ProjectParameters.FolderAndFile.FOLDER_RTOS_KERNEL)) {
				isDependencyRemoved = false;
			}
		}

		bot.tree(1).getTreeItem(ProjectParameters.FolderAndFile.FOLDER_RTOS)
				.getNode(ProjectParameters.FolderAndFile.FOLDER_RTOS_LIBRARY)
				.getNode(ProjectParameters.RTOSComponent.FREERTOS_IP).select();
		SWTBotTreeItem[] configTree1 = bot.tree(2).getTreeItem(ProjectParameters.KernelConfig.CONFIGURATIONS)
				.getItems();
		for (SWTBotTreeItem config : configTree1) {
			Utility.changeConfigOfCombobox(config, ProjectParameters.AmazonConfig.REPORT_USAGE, "Disable");
			if (config.cell(0)
					.contains("Enable application hook event when the network goes up and when it goes down")) {
				bot.sleep(2000);
				config.click(1);
				bot.button(ButtonAction.BUTTON_YES).click();
			}
		}
		Utility.clickGenerateCode();
		
		boolean isConfigEnableOK = Utility.checkAddRemoveLTSComponentSetting(projectModelSpecific, buildType, true);
		boolean isDependencyAdded2 = bot.tree(1).getTreeItem(ProjectParameters.FolderAndFile.FOLDER_RTOS)
				.getNode(ProjectParameters.FolderAndFile.FOLDER_RTOS_KERNEL)
				.getNode(ProjectParameters.RTOSComponent.FREERTOS_KERNEL).isVisible();
		
		Utility.removeComponent(ProjectParameters.RTOSComponent.FREERTOS_IP);
		Utility.clickGenerateCode();
		
		boolean isRemoveOK = Utility.checkAddRemoveLTSComponentSetting(projectModelSpecific, buildType, false);
		
		if (!isAddedOK || !isDependencyAdded || !isConfigDisableOK || !isDependencyRemoved || !isConfigEnableOK || !isDependencyAdded2 || !isRemoveOK) {
			assertFalse(true);
		}

	}
	
	@Test
	public void tc_04_TestGreyOffComponent() throws Exception {
		Utility.addComponent("FreeRTOS TCP/IP component");
		Utility.clickGenerateCode();
		
		bot.tree()
				.getTreeItem(projectModelSpecific.getProjectName() + " ["
						+ projectModelSpecific.getActiveBuildConfiguration() + "]")
				.contextMenu(ProjectParameters.ProjectSettings.C_CPLUSPLUS_PROJECT_SETTINGS).click();
		bot.tree().getTreeItem("Resource").expand();
		bot.tree().getTreeItem("Resource").getNode("Linked Resources").click();
		bot.table().select(0);
		bot.button("Edit...").click();
		bot.text(0).setText("AWS_IOT_MCU_ROOT_2");
		bot.button(ButtonAction.BUTTON_OK).click();
		bot.button(ButtonAction.APPLY_AND_CLOSE).click();
		
		Utility.changeModuleDownloadLocation(robot, ProjectParameters.FileLocation.EMPTY_RTOS_LOCATION, true);
		bot.tree(1).getTreeItem(ProjectParameters.FolderAndFile.FOLDER_RTOS)
		.getNode(ProjectParameters.FolderAndFile.FOLDER_RTOS_GENERIC).expand();
		
		bot.tree(1).getTreeItem(ProjectParameters.FolderAndFile.FOLDER_RTOS)
		.getNode(ProjectParameters.FolderAndFile.FOLDER_RTOS_GENERIC)
		.getNode(ProjectParameters.RTOSComponent.FREERTOS_IP).select();

		boolean cannotBeRemoved = !bot.toolbarButtonWithTooltip(ProjectParameters.ButtonAction.BUTTON_REMOVE_COMPONENT)
				.isEnabled();

		Utility.clickGenerateCode();

		String buildType = projectModelSpecific.getActiveBuildConfiguration();
		bot.tree().getTreeItem(projectModelSpecific.getProjectName()).select();
		bot.tree().getTreeItem(projectModelSpecific.getProjectName() + " [" + buildType + "]").expand();
		bot.tree().getTreeItem(projectModelSpecific.getProjectName() + " [" + buildType + "]")
				.getNode(ProjectParameters.FolderAndFile.FOLDER_SRC).expand();
		bot.tree().getTreeItem(projectModelSpecific.getProjectName() + " [" + buildType + "]")
				.getNode(ProjectParameters.FolderAndFile.FOLDER_SRC)
				.getNode(ProjectParameters.FolderAndFile.FOLDER_FRTOS_CONFIG).expand();
		boolean isThereConfigFile = bot.tree()
				.getTreeItem(projectModelSpecific.getProjectName() + " [" + buildType + "]")
				.getNode(ProjectParameters.FolderAndFile.FOLDER_SRC)
				.getNode(ProjectParameters.FolderAndFile.FOLDER_FRTOS_CONFIG)
				.getNode(ProjectParameters.FolderAndFile.FILE_FREERTOS_IP_CONFIG_H).isVisible();

		bot.tree()
				.getTreeItem(projectModelSpecific.getProjectName() + " ["
						+ projectModelSpecific.getActiveBuildConfiguration() + "]")
				.contextMenu(ProjectParameters.ProjectSettings.C_CPLUSPLUS_PROJECT_SETTINGS).click();

		bot.tree(1).getTreeItem(ProjectParameters.ProjectSettings.COMPILER)
				.getNode(ProjectParameters.ProjectSettings.SOURCE).select();
		String[] includeDirs = bot.list(0).getItems();

		boolean isGenProjectSetting = false;

		for (String incdir : includeDirs) {
			if (incdir.contains("Middleware/FreeRTOS/FreeRTOS-Plus-TCP/source/include")) {
				isGenProjectSetting = true;
			}
		}
		bot.button(ButtonAction.BUTTON_CANCEL).click();
		Utility.deleteProject(projectModelSpecific.getProjectName(), true);

		if (!isGenProjectSetting || !isThereConfigFile || !cannotBeRemoved) {
			assertFalse(true);
		}
		
	}

}
