package testsuitesfor202407;

import static org.junit.Assert.assertFalse;

import java.awt.Robot;
import java.io.File;
import java.util.Locale;
import java.util.regex.Pattern;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.eclipse.finder.matchers.WidgetMatcherFactory;
import org.eclipse.swtbot.eclipse.finder.waits.Conditions;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotView;
import org.eclipse.swtbot.swt.finder.utils.SWTBotPreferences;
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
public class RSC_17977_ShowLatestVersionDownload {
	private static final String PLATFORM_XML_FILE = "xml/platformdata.xml";
	private static final String RTOS_PG_XML_FILE = "xml/rtospg.xml";
	private static SWTWorkbenchBot bot;
	private static Robot robot;
	private static final Pattern REVISION_PATTERN_RC = Pattern.compile("[-_][rR][cC](\\d+)?$");
	private static final String RC_SUFFIX = "rc";
	private static ProjectModel projectModelSpecific = new ProjectModel();
	private static ProjectModel projectModelSpecific2 = new ProjectModel();
	private static ProjectModel projectModelSpecific3 = new ProjectModel();
	private static ProjectModel projectModelSpecific4 = new ProjectModel();



	@BeforeClass
	public static void beforeClass() throws Exception {
		bot = new SWTWorkbenchBot();
		PlatformModel.loadPlatformModel(new File(Utility.getBundlePath(LogUtil.PLUGIN_ID, PLATFORM_XML_FILE)));
		RTOSManager.loadRTOSModel(new File(Utility.getBundlePath(LogUtil.PLUGIN_ID, RTOS_PG_XML_FILE)));
		projectModelSpecific = PGUtility.prepareProjectModel(RTOSType.FREERTOSIOTLTS, RTOSVersion.IoTLTS_202210_1_1_3,
				RTOSApplication.IOT_LTS_ETHER_PUBSUB, Constants.CCRX_TOOLCHAIN, TargetBoard.BOARD_CK_RX65N);
		projectModelSpecific2 = PGUtility.prepareProjectModel(RTOSType.AMAZONFREERTOS, RTOSVersion.Amazon_202107_1_0_1,
				RTOSApplication.AMAZON_BARE, Constants.CCRX_TOOLCHAIN, TargetBoard.BOARD_CK_RX65N);
		projectModelSpecific3 = PGUtility.prepareProjectModel(RTOSType.AZURE, RTOSVersion.Azure_6_2_1,
				RTOSApplication.AZURE_PING, Constants.CCRX_TOOLCHAIN, TargetBoard.BOARD_RSK_RX65N_2MB);
		projectModelSpecific4 = PGUtility.prepareProjectModel(RTOSType.FREERTOSKERNEL, RTOSVersion.Kernel_1_0_8,
				RTOSApplication.KERNEL_BARE, Constants.GCC_TOOLCHAIN, TargetBoard.BOARD_RSK_RX65N_2MB);
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
	public void tc_01_PGDownloadDialogLatestVersionAzure() throws Exception {
		Utility.OpenDownloadDialogPG(RTOSDisplay.AZURE);
		boolean isCheckLatestVersionOK = Utility.CheckLatestVersionDownloadDialog(REVISION_PATTERN_RC, RC_SUFFIX, 2, false);
		if (!isCheckLatestVersionOK) {
			assertFalse(true);
		}
	}

	@Test
	public void tc_02_PGDownloadDialogLatestVersionKernel() throws Exception {
		Utility.OpenDownloadDialogPG(RTOSDisplay.FREERTOSKERNEL);
		boolean isCheckLatestVersionOK = Utility.CheckLatestVersionDownloadDialog(REVISION_PATTERN_RC, RC_SUFFIX, 2, false);
		if (!isCheckLatestVersionOK) {
			assertFalse(true);
		}
	}
	
	@Test
	public void tc_03_PGDownloadDialogLatestVersionIoTLTS() throws Exception {
		Utility.OpenDownloadDialogPG(RTOSDisplay.FREERTOSIOTLTS);
		boolean isCheckLatestVersionOK = Utility.CheckLatestVersionDownloadDialog(REVISION_PATTERN_RC, RC_SUFFIX, 2, false);
		if (!isCheckLatestVersionOK) {
			assertFalse(true);
		}
	}
	
	@Test
	public void tc_04_PGDownloadDialogLatestVersionIoTLegacy() throws Exception {
		Utility.OpenDownloadDialogPG(RTOSDisplay.AMAZONFREERTOS);
		boolean isCheckLatestVersionOK = Utility.CheckLatestVersionDownloadDialog(REVISION_PATTERN_RC, RC_SUFFIX, 2, false);
		if (!isCheckLatestVersionOK) {
			assertFalse(true);
		}
	}
	
	@Test
	public void tc_05_ImportDownloadDialogLatestVersionRL78() throws Exception {
		Utility.OpenDownloadDialogImport(RTOSDisplayImport.RL78);
		boolean isCheckLatestVersionOK = Utility.CheckLatestVersionDownloadDialog(REVISION_PATTERN_RC, RC_SUFFIX, 2, true);
		if (!isCheckLatestVersionOK) {
			assertFalse(true);
		}
	}
	
	@Test
	public void tc_06_ImportDownloadDialogLatestVersionRX() throws Exception {
		Utility.OpenDownloadDialogImport(RTOSDisplayImport.RX);
		boolean isCheckLatestVersionOK = Utility.CheckLatestVersionDownloadDialog(REVISION_PATTERN_RC, RC_SUFFIX, 2, false);
		if (!isCheckLatestVersionOK) {
			assertFalse(true);
		}
	}
	
	@Test
	public void tc_06_ImportDownloadDialogLatestVersionLegacy() throws Exception {
		Utility.OpenDownloadDialogImport(RTOSDisplayImport.LEGACY);
		boolean isCheckLatestVersionOK = Utility.CheckLatestVersionDownloadDialog(REVISION_PATTERN_RC, RC_SUFFIX, 2, false);
		if (!isCheckLatestVersionOK) {
			assertFalse(true);
		}
	}
	
	@Test
	public void tc_07_DownloadMissingDialogForLTS() throws Exception {
		Utility.changeModuleDownloadLocation(robot, ProjectParameters.FileLocation.IOTLTS_RTOS_LOCATION, true);
		Utility.changeModuleDownloadLocation(robot, ProjectParameters.FileLocation.NEWEST_FIT_MODULES_LOCATION, false);
		Utility.reFocus(robot);
		
		PGUtility.createProject(RTOSType.FREERTOSIOTLTS, RTOSVersion.IoTLTS_202210_1_1_3, RTOSApplication.IOT_LTS_ETHER_PUBSUB,
				Constants.CCRX_TOOLCHAIN, TargetBoard.BOARD_CK_RX65N);
		
		Utility.openSCFGEditor(projectModelSpecific, ProjectParameters.SCFG_COMPONENT_TAB);

		bot.tree(1).getTreeItem(ProjectParameters.FolderAndFile.FOLDER_RTOS)
				.getNode(ProjectParameters.FolderAndFile.FOLDER_RTOS_KERNEL)
				.getNode(ProjectParameters.RTOSComponent.FREERTOS_KERNEL).select();
				
		bot.tree().getTreeItem(projectModelSpecific.getProjectName()).select();
		bot.tree().getTreeItem(projectModelSpecific.getProjectName() + " ["+ projectModelSpecific.getActiveBuildConfiguration() +"]").contextMenu(ProjectParameters.ProjectSettings.C_CPLUSPLUS_PROJECT_SETTINGS).click();
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
		.getNode(ProjectParameters.RTOSComponent.FREERTOS_KERNEL).select();
		
		bot.link(0).click(0);
		
		bot.sleep(20000);
		
		boolean isCheckLatestVersionOK = Utility.CheckLatestVersionDownloadDialog(REVISION_PATTERN_RC, RC_SUFFIX, 1, false);
		Utility.deleteProject(projectModelSpecific.getProjectName(), true);
		if (!isCheckLatestVersionOK) {
			assertFalse(true);
		}
	}
	
	@Test
	public void tc_08_DownloadMissingDialogForLegacy() throws Exception {
		Utility.changeModuleDownloadLocation(robot, ProjectParameters.FileLocation.AMAZON_RTOS_LOCATION, true);
		Utility.changeModuleDownloadLocation(robot, ProjectParameters.FileLocation.FIT_MODULES_LOCATION, false);
		Utility.reFocus(robot);
		bot.defaultPerspective().activate();
		PGUtility.createProject(RTOSType.AMAZONFREERTOS, RTOSVersion.Amazon_202107_1_0_1, RTOSApplication.AMAZON_BARE,
				Constants.CCRX_TOOLCHAIN, TargetBoard.BOARD_CK_RX65N);
		
		Utility.openSCFGEditor(projectModelSpecific2, ProjectParameters.SCFG_COMPONENT_TAB);
				
		bot.tree().getTreeItem(projectModelSpecific2.getProjectName()).select();
		bot.tree().getTreeItem(projectModelSpecific2.getProjectName() + " ["+ projectModelSpecific2.getActiveBuildConfiguration() +"]").contextMenu(ProjectParameters.ProjectSettings.C_CPLUSPLUS_PROJECT_SETTINGS).click();
		bot.tree().getTreeItem("Resource").expand();
		bot.tree().getTreeItem("Resource").getNode("Linked Resources").click();
		bot.table().select(0);
		bot.button("Edit...").click();
		bot.text(0).setText("AFR_HOME_2");
		bot.button(ButtonAction.BUTTON_OK).click();
		bot.button(ButtonAction.APPLY_AND_CLOSE).click();
		
		Utility.changeModuleDownloadLocation(robot, ProjectParameters.FileLocation.EMPTY_RTOS_LOCATION, true);
		
		bot.tree(1).getTreeItem(ProjectParameters.FolderAndFile.FOLDER_RTOS)
		.getNode(ProjectParameters.FolderAndFile.FOLDER_RTOS_KERNEL)
		.getNode(ProjectParameters.RTOSComponent.FREERTOS_KERNEL).doubleClick();
		
		bot.link(0).click(0);
		
		bot.sleep(20000);
		
		boolean isCheckLatestVersionOK = Utility.CheckLatestVersionDownloadDialog(REVISION_PATTERN_RC, RC_SUFFIX, 1, false);
		Utility.deleteProject(projectModelSpecific2.getProjectName(), true);

		if (!isCheckLatestVersionOK) {
			assertFalse(true);
		}
	}
	
	@Test
	public void tc_09_ChangeRTOSVersionDownloadDialogAzure() throws Exception {
		Utility.changeModuleDownloadLocation(robot, ProjectParameters.FileLocation.AZURE_RTOS_LOCATION, true);
		Utility.changeModuleDownloadLocation(robot, ProjectParameters.FileLocation.NEWEST_FIT_MODULES_LOCATION, false);
		Utility.reFocus(robot);

		PGUtility.createProject(RTOSType.AZURE, RTOSVersion.Azure_6_2_1, RTOSApplication.AZURE_PING,
				Constants.CCRX_TOOLCHAIN, TargetBoard.BOARD_RSK_RX65N_2MB);

		Utility.openSCFGEditor(projectModelSpecific3, ProjectParameters.SCFG_COMPONENT_TAB);

		bot.tree(1).getTreeItem(ProjectParameters.FolderAndFile.FOLDER_RTOS)
				.getNode(ProjectParameters.FolderAndFile.FOLDER_RTOS_KERNEL)
				.getNode(ProjectParameters.RTOSComponent.THREADX)
				.contextMenu(ProjectParameters.MenuName.MENU_CHANGE_VERSION).click();
		
		bot.link("<a>Manage RTOS Versions...</a>").click();
		
		boolean isCheckLatestVersionOK = Utility.CheckLatestVersionDownloadDialog(REVISION_PATTERN_RC, RC_SUFFIX, 2, false);
		Utility.deleteProject(projectModelSpecific3.getProjectName(), true);

		if (!isCheckLatestVersionOK) {
			assertFalse(true);
		}
	}
	
	@Test
	public void tc_10_ChangeRTOSVersionDownloadDialogKernel() throws Exception {
		Utility.changeModuleDownloadLocation(robot, ProjectParameters.FileLocation.KERNEL_RTOS_LOCATION, true);
		Utility.changeModuleDownloadLocation(robot, ProjectParameters.FileLocation.FIT_MODULES_LOCATION, false);
		Utility.reFocus(robot);
		bot.defaultPerspective().activate();
		PGUtility.createProject(RTOSType.FREERTOSKERNEL, RTOSVersion.Kernel_1_0_8, RTOSApplication.KERNEL_BARE,
				Constants.GCC_TOOLCHAIN, TargetBoard.BOARD_RSK_RX65N_2MB);
		
		Utility.openSCFGEditor(projectModelSpecific4, ProjectParameters.SCFG_COMPONENT_TAB);


		bot.tree(1).getTreeItem(ProjectParameters.FolderAndFile.FOLDER_RTOS)
				.getNode(ProjectParameters.FolderAndFile.FOLDER_RTOS_KERNEL)
				.getNode(ProjectParameters.RTOSComponent.FREERTOS_KERNEL)
				.contextMenu(ProjectParameters.MenuName.MENU_CHANGE_VERSION).click();
		
		bot.link("<a>Manage RTOS Versions...</a>").click();
		
		boolean isCheckLatestVersionOK = Utility.CheckLatestVersionDownloadDialog(REVISION_PATTERN_RC, RC_SUFFIX, 2, false);
		Utility.deleteProject(projectModelSpecific4.getProjectName(), true);

		if (!isCheckLatestVersionOK) {
			assertFalse(true);
		}
	}
}
