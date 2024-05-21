package testsuitesfor202407;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.awt.Robot;
import java.io.File;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.eclipse.finder.waits.Conditions;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotView;
import org.eclipse.swtbot.swt.finder.matchers.WidgetMatcherFactory;
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
import parameters.ProjectParameters.ButtonAction;
import parameters.ProjectParameters.LabelName;
import parameters.ProjectParameters.MenuName;
import parameters.ProjectParameters.RTOSApplication;
import parameters.ProjectParameters.RTOSDisplay;
import parameters.ProjectParameters.RTOSType;
import parameters.ProjectParameters.RTOSVersion;
import parameters.ProjectParameters.TargetBoard;
import platform.PlatformModel;
import utilities.PGUtility;
import utilities.Utility;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class RSC_18103_UseLatestVersionInTemplate {
	private static final String PLATFORM_XML_FILE = "xml/platformdata.xml";
	private static final String RTOS_PG_XML_FILE = "xml/rtospg.xml";
	private static SWTWorkbenchBot bot;
	private static Robot robot;
	private static ProjectModel projectModelKernelCKRX65N109 = new ProjectModel();
	private static ProjectModel projectModelKernelEKRX72N109 = new ProjectModel();
	private static ProjectModel projectModelKernelRSKRX652MBN108 = new ProjectModel();
	private static ProjectModel projectModelKAzureRSKRX72N = new ProjectModel();

	@BeforeClass
	public static void beforeClass() throws Exception {
		bot = new SWTWorkbenchBot();
		PlatformModel.loadPlatformModel(new File(Utility.getBundlePath(LogUtil.PLUGIN_ID, PLATFORM_XML_FILE)));
		RTOSManager.loadRTOSModel(new File(Utility.getBundlePath(LogUtil.PLUGIN_ID, RTOS_PG_XML_FILE)));
		projectModelKernelCKRX65N109 = PGUtility.prepareProjectModel(RTOSType.FREERTOSKERNEL, RTOSVersion.Kernel_1_0_9,
				RTOSApplication.KERNEL_BARE, Constants.GCC_TOOLCHAIN, TargetBoard.BOARD_CK_RX65N);
		projectModelKernelEKRX72N109 = PGUtility.prepareProjectModel(RTOSType.FREERTOSKERNEL, RTOSVersion.Kernel_1_0_9,
				RTOSApplication.KERNEL_BARE, Constants.GCC_TOOLCHAIN, TargetBoard.BOARD_EK_RX72N);
		projectModelKernelRSKRX652MBN108 = PGUtility.prepareProjectModel(RTOSType.FREERTOSKERNEL, RTOSVersion.Kernel_1_0_8,
				RTOSApplication.KERNEL_BARE, Constants.GCC_TOOLCHAIN, TargetBoard.BOARD_RSK_RX65N_2MB);
		projectModelKAzureRSKRX72N = PGUtility.prepareProjectModel(RTOSType.AZURE, RTOSVersion.Azure_6_2_1,
				RTOSApplication.AZURE_BARE, Constants.GCC_TOOLCHAIN, TargetBoard.BOARD_RSK_RX72N);
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
		Utility.changeModuleDownloadLocation(robot, ProjectParameters.FileLocation.EMPTY_RTOS_LOCATION, 3);
		Utility.changeModuleDownloadLocation(robot, ProjectParameters.FileLocation.NEWEST_FIT_MODULES_LOCATION, 2);
		Utility.reFocus(robot);
	}

	@Test
	public void tc_01_DownloadFreeRTOSKernelPackages() throws Exception {
		bot.sleep(3000);
		bot.menu(MenuName.MENU_FILE).menu(MenuName.MENU_NEW).menu(MenuName.MENU_C_CPP_PROJECT)
				.menu(MenuName.MENU_RENESAS_RX).click();
		bot.table().select(2);

		bot.button(ButtonAction.BUTTON_NEXT).click();
		bot.waitUntil(Conditions.waitForWidget(WidgetMatcherFactory.withText(LabelName.LABEL_PROJECT_NAME)), 10000);
		bot.textWithLabel(LabelName.LABEL_PROJECT_NAME).setText("Testing");
		bot.waitUntil(Conditions.widgetIsEnabled(bot.button(ButtonAction.BUTTON_NEXT)), 10000);

		bot.button(ButtonAction.BUTTON_NEXT).click();
		bot.radio(0).click();

		bot.comboBoxWithLabel(LabelName.LABEL_RTOS).setSelection(RTOSDisplay.FREERTOSKERNEL);

		bot.link("<a>Manage RTOS Versions...</a>").click();
		bot.sleep(10000);

		// change RTOS locaiton
		Utility.copyLocationToClipBoard(ProjectParameters.FileLocation.EMPTY_RTOS_LOCATION);
		bot.button("Browse...", 0).click();
		Utility.pressCtrlV(robot);

		Utility.pressEnter(robot);
		Utility.pressEnter(robot);
		bot.sleep(5000);
		
		// uncheck latest version checkbox
		bot.checkBox("Show only latest version").click();
		bot.sleep(1000);

		bot.table().getTableItem(bot.table().indexOf(RTOSVersion.Kernel_1_0_9, "Rev.")).check();
		bot.table().getTableItem(bot.table().indexOf(RTOSVersion.Kernel_1_0_8, "Rev.")).check();
		bot.button(ButtonAction.BUTTON_DOWNLOAD).click();
		bot.button(ButtonAction.BUTTON_ACCEPT).click();
		while (bot.activeShell().getText().equals(ProjectParameters.WINDOW_PROGRESS_INFORMATION)) {
			bot.sleep(10000);
		}
		bot.button(ButtonAction.BUTTON_CANCEL).click();
		// check downloaded packages on RTOS location
		File rtosLocFile = new File(ProjectParameters.FileLocation.EMPTY_RTOS_LOCATION);
		List<String> packageNames = List.of("freertos-" + RTOSVersion.Kernel_1_0_9,
				"freertos-" + RTOSVersion.Kernel_1_0_8);
		boolean isDownloadSuccesful = false;
		if (rtosLocFile.isDirectory()) {
			isDownloadSuccesful = Arrays.stream(rtosLocFile.listFiles()).map(File::getName).collect(Collectors.toList()).containsAll(packageNames);
		}
		assertTrue(isDownloadSuccesful);

		// modify the package: 10.4.3-rx-1.0.9 template file
		String uselatestVersion = "uselatestversion=\"true\"";
		String notUselatestVersion = "uselatestversion=\"false\"";
		String anchorKey = "display=\"r_bsp\"";
		String commonTemplatePath = ProjectParameters.FileLocation.EMPTY_RTOS_LOCATION + "/freertos-"
				+ RTOSVersion.Kernel_1_0_9 + "/configuration/patch_files/common_scfg.ftl";
		String rxv3TemplatePath = ProjectParameters.FileLocation.EMPTY_RTOS_LOCATION + "/freertos-"
				+ RTOSVersion.Kernel_1_0_9 + "/configuration/patch_files/RXv3/rxv3_scfg.ftl";
		boolean isInsertedCommonFile = Utility.insertContentIntoFile(Paths.get(commonTemplatePath), anchorKey,
				uselatestVersion);
		boolean isInsertedRxv3File = Utility.insertContentIntoFile(Paths.get(rxv3TemplatePath), anchorKey,
				notUselatestVersion);
		assertTrue(isInsertedCommonFile && isInsertedRxv3File);

	}

	@Test
	public void tc_02_CreateFreeRTOSKernelProjectWithCKRX65NBoard() throws Exception {
		PGUtility.createProject(RTOSType.FREERTOSKERNEL, RTOSVersion.Kernel_1_0_9, RTOSApplication.KERNEL_BARE,
				Constants.GCC_TOOLCHAIN, TargetBoard.BOARD_CK_RX65N);
	}

	@Test
	public void tc_03_CheckLatestVersionBSPOfFreeRTOSKernelProject() throws Exception {
		// check the current version of r_bsp
		Utility.openSCFGEditor(projectModelKernelCKRX65N109, "Overview");
		String rBspVersion = bot.table(0).getTableItem("Board Support Packages. (r_bsp)").getText(1);

		// check the latest version of r_bsp
		Utility.openSCFGEditor(projectModelKernelCKRX65N109, "Components");
		// check latest r_bsp version on add component wizard
		bot.toolbarButtonWithTooltip(ProjectParameters.ButtonAction.BUTTON_ADD_COMPONENT).click();
		bot.shell(ProjectParameters.WINDOW_NEW_COMPONENT).setFocus();
		bot.textWithLabel(ProjectParameters.LabelName.LABEL_FILTER).setText("r_bsp");
		bot.table().select(0);
		String latestBspVersion = bot.table().getTableItem("Board Support Packages.").getText(3);
		bot.button(ProjectParameters.ButtonAction.BUTTON_CANCEL).click();
		assertTrue(rBspVersion.equals(latestBspVersion));
		// remove project out from workspace
		Utility.deleteProject(projectModelKernelCKRX65N109.getProjectName(), false);
	}

	@Test
	public void tc_04_CreateFreeRTOSKernelProjectWithEKRX72NBoard() throws Exception {
		PGUtility.createProject(RTOSType.FREERTOSKERNEL, RTOSVersion.Kernel_1_0_9, RTOSApplication.KERNEL_BARE,
				Constants.GCC_TOOLCHAIN, TargetBoard.BOARD_EK_RX72N);
	}

	@Test
	public void tc_05_CheckLatestVersionBSPNotUseOfFreeRTOSKernelProject() throws Exception {
		// check the current version of r_bsp
		Utility.openSCFGEditor(projectModelKernelEKRX72N109, "Overview");
		String rBspVersion = bot.table(0).getTableItem("Board Support Packages. (r_bsp)").getText(1);

		// check the latest version of r_bsp
		Utility.openSCFGEditor(projectModelKernelEKRX72N109, "Components");
		// check latest r_bsp version on add component wizard
		bot.toolbarButtonWithTooltip(ProjectParameters.ButtonAction.BUTTON_ADD_COMPONENT).click();
		bot.shell(ProjectParameters.WINDOW_NEW_COMPONENT).setFocus();
		bot.textWithLabel(ProjectParameters.LabelName.LABEL_FILTER).setText("r_bsp");
		bot.table().select(0);
		String latestBspVersion = bot.table().getTableItem("Board Support Packages.").getText(3);
		bot.button(ProjectParameters.ButtonAction.BUTTON_CANCEL).click();
		assertFalse(rBspVersion.equals(latestBspVersion));
		// remove project out from workspace
		Utility.deleteProject(projectModelKernelEKRX72N109.getProjectName(), false);
	}

	@Test
	public void tc_06_CreateFreeRTOSKernelProjectWithCKRX65NBoardAndPackage108() throws Exception {
		PGUtility.createProject(RTOSType.FREERTOSKERNEL, RTOSVersion.Kernel_1_0_8, RTOSApplication.KERNEL_BARE,
				Constants.GCC_TOOLCHAIN, TargetBoard.BOARD_RSK_RX65N_2MB);
	}

	@Test
	public void tc_07_CheckLatestVersionBSPInCaseNotUseTemplateOfFreeRTOSKernelProject() throws Exception {
		// check the current version of r_bsp
		Utility.openSCFGEditorByProjectName(projectModelKernelRSKRX652MBN108.getProjectName(), "Overview");
		String rBspVersion = bot.table(0).getTableItem("Board Support Packages. (r_bsp)").getText(1);

		// check the latest version of r_bsp
		Utility.openSCFGEditorByProjectName(projectModelKernelRSKRX652MBN108.getProjectName(), "Components");
		// check latest r_bsp version on add component wizard
		bot.toolbarButtonWithTooltip(ProjectParameters.ButtonAction.BUTTON_ADD_COMPONENT).click();
		bot.shell(ProjectParameters.WINDOW_NEW_COMPONENT).setFocus();
		bot.textWithLabel(ProjectParameters.LabelName.LABEL_FILTER).setText("r_bsp");
		bot.table().select(0);
		String latestBspVersion = bot.table().getTableItem("Board Support Packages.").getText(3);
		bot.button(ProjectParameters.ButtonAction.BUTTON_CANCEL).click();
		assertTrue(rBspVersion.equals(latestBspVersion));
		// remove project out from workspace
		Utility.deleteProject(projectModelKernelRSKRX652MBN108.getProjectName(), false);
	}

	@Test
	public void tc_08_DownloadAzureRTOSPackages() throws Exception {
		bot.sleep(3000);
		bot.menu(MenuName.MENU_FILE).menu(MenuName.MENU_NEW).menu(MenuName.MENU_C_CPP_PROJECT)
				.menu(MenuName.MENU_RENESAS_RX).click();
		bot.table().select(2);

		bot.button(ButtonAction.BUTTON_NEXT).click();
		bot.waitUntil(Conditions.waitForWidget(WidgetMatcherFactory.withText(LabelName.LABEL_PROJECT_NAME)), 10000);
		bot.textWithLabel(LabelName.LABEL_PROJECT_NAME).setText("Testing");
		bot.waitUntil(Conditions.widgetIsEnabled(bot.button(ButtonAction.BUTTON_NEXT)), 10000);

		bot.button(ButtonAction.BUTTON_NEXT).click();
		bot.radio(0).click();

		bot.comboBoxWithLabel(LabelName.LABEL_RTOS).setSelection(RTOSDisplay.AZURE);

		bot.link("<a>Manage RTOS Versions...</a>").click();
		bot.sleep(10000);

		// change RTOS locaiton
		Utility.copyLocationToClipBoard(ProjectParameters.FileLocation.EMPTY_RTOS_LOCATION);
		bot.button("Browse...", 0).click();
		Utility.pressCtrlV(robot);

		Utility.pressEnter(robot);
		Utility.pressEnter(robot);
		bot.sleep(5000);
		
		// uncheck latest version checkbox
		bot.checkBox("Show only latest version").click();
		bot.sleep(1000);

		bot.table().getTableItem(bot.table().indexOf("v" + RTOSVersion.Azure_6_2_1, "Rev.")).check();
		bot.button(ButtonAction.BUTTON_DOWNLOAD).click();
		bot.button(ButtonAction.BUTTON_ACCEPT).click();
		while (bot.activeShell().getText().equals(ProjectParameters.WINDOW_PROGRESS_INFORMATION)) {
			bot.sleep(10000);
		}
		bot.button(ButtonAction.BUTTON_CANCEL).click();
		// check downloaded packages on RTOS location
		String downloadedPackage = "azurertos-v" + RTOSVersion.Azure_6_2_1;
		File packageFolder = new File(ProjectParameters.FileLocation.EMPTY_RTOS_LOCATION, downloadedPackage);
		assertTrue(packageFolder.isDirectory());

		// modify the package: 6.2.1_rel-rx-2.0.0 template file
		String uselatestVersion = "uselatestversion=\"true\"";
		String anchorKey = "display=\"r_bsp\"";
		String commonTemplatePath = ProjectParameters.FileLocation.EMPTY_RTOS_LOCATION + "/" + downloadedPackage
				+ "/configuration/samples/bare/bare_scfg.ftl";
		boolean isInsertedCommonFile = Utility.insertContentIntoFile(Paths.get(commonTemplatePath), anchorKey,
				uselatestVersion);
		assertTrue(isInsertedCommonFile);

	}

	@Test
	public void tc_09_CreateFAzureRTOSProjectWithCKRX65NBoard() throws Exception {
		PGUtility.createProject(RTOSType.AZURE, RTOSVersion.Azure_6_2_1, RTOSApplication.AZURE_BARE,
				Constants.GCC_TOOLCHAIN, TargetBoard.BOARD_RSK_RX72N);
	}

	/**
	 * this Azure bare project using {@code uselatestversion="true"} attribute in template file
	 * 
	 * @throws Exception
	 */
	@Test
	public void tc_10_CheckLatestVersionBSPOfAzureProject() throws Exception {
		// check the current version of r_bsp
		Utility.openSCFGEditorByProjectName(projectModelKAzureRSKRX72N.getProjectName(), "Overview");
		String rBspVersion = bot.table(0).getTableItem("Board Support Packages. (r_bsp)").getText(1);

		// check the latest version of r_bsp
		Utility.openSCFGEditorByProjectName(projectModelKAzureRSKRX72N.getProjectName(), "Components");
		// check latest r_bsp version on add component wizard
		bot.toolbarButtonWithTooltip(ProjectParameters.ButtonAction.BUTTON_ADD_COMPONENT).click();
		bot.shell(ProjectParameters.WINDOW_NEW_COMPONENT).setFocus();
		bot.textWithLabel(ProjectParameters.LabelName.LABEL_FILTER).setText("r_bsp");
		bot.table().select(0);
		String latestBspVersion = bot.table().getTableItem("Board Support Packages.").getText(3);
		bot.button(ProjectParameters.ButtonAction.BUTTON_CANCEL).click();
		assertTrue(rBspVersion.equals(latestBspVersion));
		// remove project out from workspace
		Utility.deleteProject(projectModelKAzureRSKRX72N.getProjectName(), false);
	}
}
