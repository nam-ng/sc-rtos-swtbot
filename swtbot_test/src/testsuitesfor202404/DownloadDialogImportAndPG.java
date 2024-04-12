package testsuitesfor202404;

import static org.junit.Assert.assertFalse;

import java.awt.Robot;
import java.io.File;

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
public class DownloadDialogImportAndPG {
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
	public void tc_01_DownloadDialogForPG() throws Exception{
		bot.sleep(3000);
		bot.menu(MenuName.MENU_FILE).menu(MenuName.MENU_NEW)
		.menu(MenuName.MENU_C_CPP_PROJECT).menu(MenuName.MENU_RENESAS_RX).click();
		bot.table().select(2);
		
		bot.button(ButtonAction.BUTTON_NEXT).click();
		bot.waitUntil(Conditions.waitForWidget(WidgetMatcherFactory.withText(LabelName.LABEL_PROJECT_NAME)), 10000);
		bot.textWithLabel(LabelName.LABEL_PROJECT_NAME).setText("Testing");
		bot.waitUntil(Conditions.widgetIsEnabled(bot.button(ButtonAction.BUTTON_NEXT)), 10000);
		
		bot.button(ButtonAction.BUTTON_NEXT).click();
		bot.radio(0).click();
		
		bot.comboBoxWithLabel(LabelName.LABEL_RTOS).setSelection(RTOSDisplay.FREERTOSIOTLTS);
		
		bot.link("<a>Manage RTOS Versions...</a>").click();
		bot.sleep(20000);

		boolean isDownloadDialogHasImportPackage = bot.table().indexOf(RTOSVersion.IoTLTS_202210_1_2_0_with_v, "Rev.") != -1;
		boolean isDownloadDialogHasPGPackage = bot.table().indexOf(RTOSVersion.IoTLTS_202210_1_1_3_with_v, "Rev.") != -1;
		bot.button(ButtonAction.BUTTON_CANCEL).click();
		bot.button(ButtonAction.BUTTON_CANCEL).click();
		if (isDownloadDialogHasImportPackage || !isDownloadDialogHasPGPackage) {
			assertFalse(true);
		}
	}
	
	@Test
	public void tc_02_DownloadDialogForImport() throws Exception{
		bot.sleep(1000);
		bot.menu(MenuName.MENU_FILE).menu(MenuName.MEMU_IMPORT).click();
		bot.tree().getTreeItem(ProjectParameters.FolderAndFile.FOLDER_GENERAL).expand();
		bot.tree().getTreeItem(ProjectParameters.FolderAndFile.FOLDER_GENERAL)
		.getNode(ProjectParameters.FolderAndFile.RENESAS_GITHUB).select();
		bot.button(ButtonAction.BUTTON_NEXT).click();
		bot.link("<a>Manage RTOS Versions...</a>").click();
		bot.comboBoxWithLabel(LabelName.LABEL_RTOS).setSelection("FreeRTOS (with IoT libraries) for RX");
		bot.button(ButtonAction.BUTTON_OK).click();
		bot.sleep(20000);
		boolean isDownloadDialogHasImportPackage = bot.table().indexOf(RTOSVersion.IoTLTS_202210_1_2_0_with_v, "Rev.") != -1;
		boolean isDownloadDialogHasPGPackage = bot.table().indexOf(RTOSVersion.IoTLTS_202210_1_1_3_with_v, "Rev.") != -1;
		bot.button(ButtonAction.BUTTON_CANCEL).click();
		bot.button(ButtonAction.BUTTON_CANCEL).click();
		if(!isDownloadDialogHasImportPackage || !isDownloadDialogHasPGPackage) {
			assertFalse(true);
		}
	}
}
