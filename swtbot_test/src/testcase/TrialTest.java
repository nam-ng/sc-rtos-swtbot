package testcase;

import java.io.File;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotView;
import org.eclipse.swtbot.swt.finder.utils.SWTBotPreferences;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import common.Constants;
import common.LogUtil;
import model.ProjectModel;
import model.RTOSManager;
import parameters.ProjectParameters.RTOSApplication;
import parameters.ProjectParameters.RTOSType;
import parameters.ProjectParameters.RTOSVersion;
import parameters.ProjectParameters.TargetBoard;
import platform.PlatformModel;
import utilities.PGUtility;
import utilities.Utility;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TrialTest {
	private static SWTWorkbenchBot bot;
	private static SWTBotShell workbenchShell;
	private static ProjectModel model;

	@BeforeClass
	public static void beforeClass() throws Exception {
		// process the model xml
		PlatformModel
				.loadPlatformModel(new File(Utility.getBundlePath(LogUtil.PLUGIN_ID, Constants.PLATFORM_XML_FILE)));
		RTOSManager.loadRTOSModel(new File(Utility.getBundlePath(LogUtil.PLUGIN_ID, Constants.RTOS_PG_XML_FILE)));
		model = PGUtility.prepareProjectModel(RTOSType.AZURE, RTOSVersion.Azure_6_2_1, RTOSApplication.AZURE_IOT_ADU,
				Constants.GCC_TOOLCHAIN, TargetBoard.BOARD_CK_RX65N);

		// initialize the SWTBot
		bot = new SWTWorkbenchBot();
		Display.getDefault().syncExec(new Runnable() {

			@Override
			public void run() {
				bot.getDisplay().getActiveShell().setMaximized(true);
			}
		});
		SWTBotPreferences.TIMEOUT = 20000;
		SWTBotPreferences.PLAYBACK_DELAY = 30;
		closeWelcomePage();
		workbenchShell = bot.activeShell();
	}

	private static void closeWelcomePage() {
		for (SWTBotView view : bot.views()) {
			if (view.getTitle().equals("Welcome")) {
				view.close();
			}
		}
	}

	@Test
	public void TC_02_createProject() {
		workbenchShell.setFocus();
		Utility.changeRTOSLocation();
		PGUtility.createProject(RTOSType.AZURE, RTOSVersion.Azure_6_2_1, RTOSApplication.AZURE_IOT_ADU);
	}
}
