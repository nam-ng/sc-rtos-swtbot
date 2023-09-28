package testsuites;

import static org.junit.Assert.assertFalse;

import java.awt.Robot;
import java.io.File;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
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
import parameters.ProjectParameters.ButtonAction;
import parameters.ProjectParameters.RTOSApplication;
import parameters.ProjectParameters.RTOSType;
import parameters.ProjectParameters.RTOSVersion;
import parameters.ProjectParameters.TargetBoard;
import platform.PlatformModel;
import utilities.PGUtility;
import utilities.Utility;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class AddAzureModuleAndGenerateCode{
	private static SWTWorkbenchBot bot;
	private static Robot robot;
	private static ProjectModel projectModelSpecific = new ProjectModel();
	private static final String PLATFORM_XML_FILE = "xml/platformdata.xml";
	private static final String RTOS_PG_XML_FILE = "xml/rtospg.xml";
	
	@BeforeClass
	public static void beforeClass() throws Exception {
		bot = new SWTWorkbenchBot();
		robot = new Robot();
		PlatformModel.loadPlatformModel(new File(Utility.getBundlePath(LogUtil.PLUGIN_ID, PLATFORM_XML_FILE)));
		RTOSManager.loadRTOSModel(new File(Utility.getBundlePath(LogUtil.PLUGIN_ID, RTOS_PG_XML_FILE)));
		projectModelSpecific = PGUtility.prepareProjectModel(RTOSType.AZURE, RTOSVersion.Azure_6_2_1, RTOSApplication.AZURE_BARE, Constants.CCRX_TOOLCHAIN, TargetBoard.BOARD_RSK_RX65N_2MB);
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
		Utility.changeModuleDownloadLocation(robot, ProjectParameters.FileLocation.AZURE_RTOS_LOCATION, true);
		Utility.changeModuleDownloadLocation(robot, ProjectParameters.FileLocation.NEWEST_FIT_MODULES_LOCATION, false);
		Utility.reFocus(robot);
	}
	
	@Test
	public void tc_01_CreateThreadxProject() throws Exception{
		PGUtility.createProject(RTOSType.AZURE, RTOSVersion.Azure_6_2_1, RTOSApplication.AZURE_BARE, Constants.CCRX_TOOLCHAIN, TargetBoard.BOARD_RSK_RX65N_2MB);
		
	}
	
	@Test
	public void tc_02_AddComponentFilex() throws Exception{
		Utility.openSCFGEditor(projectModelSpecific, ProjectParameters.SCFG_COMPONENT_TAB);
		Utility.addComponent("filex");
		Utility.clickGenerateCode();
		bot.sleep(10000);
		boolean isFileXInComponentTree = Utility.checkIfComponentExistOrNot(ProjectParameters.RTOSComponent.FILEX);
		if (!isFileXInComponentTree) {
			assertFalse(true);
		}
	}
	@Test
	public void tc_03_AddComponentNetxduo() throws Exception{
		bot.editorByTitle(projectModelSpecific.getProjectName() + ".scfg").setFocus();
		Utility.addComponent("netx");
		Utility.clickGenerateCode();
		bot.sleep(10000);
		boolean isNetXInComponentTree = Utility.checkIfComponentExistOrNot(ProjectParameters.RTOSComponent.NETXDUO);
		if (!isNetXInComponentTree) {
			assertFalse(true);
		}
	}
	@Test
	public void tc_04_AddComponentNetxduoAddons() throws Exception{
		bot.editorByTitle(projectModelSpecific.getProjectName() + ".scfg").setFocus();
		Utility.addComponent("netx duo addons");
		Utility.clickGenerateCode();
		bot.sleep(10000);
		boolean isAddonsInComponentTree = Utility.checkIfComponentExistOrNot(ProjectParameters.RTOSComponent.NETXDUO_ADDONS);
		if (!isAddonsInComponentTree) {
			assertFalse(true);
		}
	}

	@Test
	public void tc_05_DeleteAzureProject() throws Exception {
		Utility.deleteProject(projectModelSpecific.getProjectName(), true);
		if (bot.activeShell().getText().equals(ProjectParameters.WINDOW_SAVE_RESOURCES)) {
			bot.button(ButtonAction.BUTTON_DONT_SAVE).click();
		}
	}
}
