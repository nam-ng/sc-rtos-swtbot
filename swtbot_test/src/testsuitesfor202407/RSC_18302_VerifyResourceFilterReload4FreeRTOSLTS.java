package testsuitesfor202407;

import static org.junit.Assert.assertTrue;

import java.awt.Robot;
import java.io.File;
import java.util.Arrays;
import java.util.Optional;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotView;
import org.eclipse.swtbot.swt.finder.utils.SWTBotPreferences;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
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
import utilities.BuildUtility;
import utilities.PGUtility;
import utilities.Utility;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class RSC_18302_VerifyResourceFilterReload4FreeRTOSLTS {
	private static final String PLATFORM_XML_FILE = "xml/platformdata.xml";
	private static final String RTOS_PG_XML_FILE = "xml/rtospg.xml";
	private static SWTWorkbenchBot bot;
	private static Robot robot;

	@BeforeClass
	public static void beforeClass() throws Exception {
		PlatformModel.loadPlatformModel(new File(Utility.getBundlePath(LogUtil.PLUGIN_ID, PLATFORM_XML_FILE)));
		RTOSManager.loadRTOSModel(new File(Utility.getBundlePath(LogUtil.PLUGIN_ID, RTOS_PG_XML_FILE)));
		bot = new SWTWorkbenchBot();
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
	public void tc_00_setToolchainLocation() throws Exception {
		Utility.setToolchainManagement(robot, "Renesas CC-RX", ProjectParameters.FileLocation.TOOLCHAIN_RENESAS_CCRX_V3_05);
		Utility.reFocus(robot);
	}

	@Test
	public void tc_01_ChangeRTOSLocation() throws Exception {
		Utility.changeModuleDownloadLocation(robot, "D:\\rtos_package\\IoT_LTS\\2024-07\\new", 3);
		Utility.changeModuleDownloadLocation(robot, ProjectParameters.FileLocation.NEWEST_FIT_MODULES_LOCATION, 2);
		Utility.reFocus(robot);
	}

	@Test
	public void tc_02_VerifyResourceFilterReloadOnPG() throws Exception {
		ProjectModel model = PGUtility.prepareProjectModel(Constants.CCRX_TOOLCHAIN,
				ProjectParameters.LanguageType.C_LANGUAGE, RTOSType.FREERTOSIOTLTS, RTOSVersion.IoTLTS_202210_1_2_1,
				TargetBoard.BOARD_CK_RX65N, RTOSApplication.IOT_LTS_ETHER_PUBSUB);
		String projectName = model.getProjectName();
		PGUtility.createProject(Constants.CCRX_TOOLCHAIN, ProjectParameters.LanguageType.C_LANGUAGE,
				RTOSType.FREERTOSIOTLTS, RTOSVersion.IoTLTS_202210_1_2_1, TargetBoard.BOARD_CK_RX65N,
				RTOSApplication.IOT_LTS_ETHER_PUBSUB);
		// set symbol
		setSymbolLinkedResource(projectName, "AWS_IOT_MCU_ROOT", "AWS_IOT_MCU_ROOT1");
		// reset symbol
		setSymbolLinkedResource(projectName, "AWS_IOT_MCU_ROOT1", "AWS_IOT_MCU_ROOT");
		bot.sleep(2000);
		// build project
		boolean isBuildPassed = BuildUtility.buildProject(projectName, true);
		assertTrue(isBuildPassed);
	}

	@Test
	public void tc_03_VerifyExceptionCheckWhenCreatingSameProjectName() throws Exception {
		bot.sleep(5000);
		ProjectModel model = PGUtility.prepareProjectModel(Constants.CCRX_TOOLCHAIN,
				ProjectParameters.LanguageType.C_LANGUAGE, RTOSType.FREERTOSIOTLTS, RTOSVersion.IoTLTS_202210_1_2_1,
				TargetBoard.BOARD_CK_RX65N, RTOSApplication.IOT_LTS_ETHER_PUBSUB);
		String projectName = model.getProjectName();
		PGUtility.createProject(Constants.CCRX_TOOLCHAIN, ProjectParameters.LanguageType.C_LANGUAGE,
				RTOSType.FREERTOSIOTLTS, RTOSVersion.IoTLTS_202210_1_2_1, TargetBoard.BOARD_CK_RX65N,
				RTOSApplication.IOT_LTS_ETHER_PUBSUB);
		// delete project
		Utility.deleteProject(projectName, true);
		bot.sleep(5000);
		PGUtility.createProject(Constants.CCRX_TOOLCHAIN, ProjectParameters.LanguageType.C_LANGUAGE,
				RTOSType.FREERTOSIOTLTS, RTOSVersion.IoTLTS_202210_1_2_1, TargetBoard.BOARD_CK_RX65N,
				RTOSApplication.IOT_LTS_ETHER_PUBSUB);
		// check error log
		openErrorLogView();
		// check Exception
		assertTrue(Arrays.asList(bot.tree(1).getAllItems()).stream()
				.noneMatch(i -> i.getText().contains("rootMemento is null")));
		// delete Project
		Utility.deleteProject(projectName, true);
	}

//	@Test
	public void tc_04_VerifyResourceFilterReloadOnImport() throws Exception {
		Utility.reFocus(robot);
		// import the FreeRTOS LTS project from package
		bot.menu(MenuName.MENU_FILE).menu(MenuName.MEMU_IMPORT).click();
		bot.tree().getTreeItem(ProjectParameters.FolderAndFile.FOLDER_GENERAL).expand();
		bot.tree().getTreeItem(ProjectParameters.FolderAndFile.FOLDER_GENERAL)
				.getNode(ProjectParameters.FolderAndFile.EXISTING_PROJECTS).select();
		bot.button(ButtonAction.BUTTON_NEXT).click();

		// Import Project information
		String projectName = "aws_ether_ck_rx65n";
		String projectPath = "D:\\rtos_package\\IoT_LTS\\2024-07\\new\\afr-v202210.01-LTS-rx-1.2.1\\Projects\\aws_ether_ck_rx65n\\e2studio_ccrx";

		// Import wizard
		SWTBotShell importWizard = bot.shell("Import").activate();
		importWizard.bot().comboBox().setText(projectPath);
		importWizard.bot().button("Refresh").click();
		importWizard.bot().button(ButtonAction.BUTTON_FINISH).click();
		bot.sleep(2000);

		Utility.reFocus(robot);
		// open SCFG editor
		openSCFGEditor(projectName);
		bot.sleep(2000);
		// set symbol
		setSymbolLinkedResource(projectName, "AWS_IOT_MCU_ROOT", "AWS_IOT_MCU_ROOT1");
		// reset symbol
		setSymbolLinkedResource(projectName, "AWS_IOT_MCU_ROOT1", "AWS_IOT_MCU_ROOT");
		// close SCFG editor
		Utility.closeEditorView(projectName + ".scfg"); 
		bot.sleep(2000);

		// build project
		boolean isBuildPassed = BuildUtility.buildProject(projectName, false);
		assertTrue(isBuildPassed);
	}
	

	private void setSymbolLinkedResource(String projectName, String currentSymbol, String newSymbol) {
		// open resource filter dialog
		SWTBotTreeItem projectItem = Utility.getProjectItemOnProjectExplorer(projectName);
		Optional.ofNullable(projectItem).ifPresent(SWTBotTreeItem::select);
		Optional.ofNullable(projectItem).ifPresent(prj -> prj.contextMenu("C/C++ Project Settings").click());
		// linked resources
		bot.text(0).setText("Linked Resources");
		bot.tree().getTreeItem("Resource").expand();
		bot.tree().getTreeItem("Resource").getNode("Linked Resources").click();

		// linked resources tab
		bot.table().getTableItem(currentSymbol).doubleClick();
		// symbol dialog
		bot.textWithLabel("Name:").setText(newSymbol);
		bot.button(ButtonAction.BUTTON_OK).click();
		bot.button(ButtonAction.APPLY_AND_CLOSE).click();
	}

	private void openErrorLogView() {
		Utility.reFocus(robot);
		bot.menu(ProjectParameters.MenuName.MENU_WINDOW).menu(ProjectParameters.MenuName.MENU_SHOW_VIEW)
				.menu(ProjectParameters.MenuName.MENU_OTHER).click();
		bot.text().setText("Error Log");
		SWTBotTreeItem treeItem = bot.tree().getTreeItem(ProjectParameters.GENERAL);
		bot.waitUntil(org.eclipse.swtbot.swt.finder.waits.Conditions.treeItemHasNode(treeItem, "Error Log"));
		treeItem.getNode("Error Log").select();
		bot.button(ProjectParameters.ButtonAction.BUTTON_OPEN).click();
	}

	private void openSCFGEditor(String projectName) {
		Utility.openProjectExplorer();
		SWTBotTreeItem projectItem = Utility.getProjectItemOnProjectExplorer(projectName);
		Optional.ofNullable(projectItem).ifPresent(SWTBotTreeItem::select);
		Optional.ofNullable(projectItem).ifPresent(SWTBotTreeItem::expand);
		bot.sleep(3000);
		Optional.ofNullable(projectItem).ifPresent(prj -> prj.getNode(projectName + ".scfg").doubleClick());
		bot.sleep(3000);
		if (bot.activeShell().getText().equals(ProjectParameters.WINDOW_OPEN_ASSOCIATED_PERSPECTIVE)) {
			bot.button(ButtonAction.BUTTON_NO).click();
		}
	}
}
