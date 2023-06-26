package testcase;

import java.io.File;
import java.util.Collection;

import org.eclipse.jface.bindings.keys.ParseException;
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
import model.RTOSManager;
import model.TC;
import model.TCManager;
import parameters.ProjectParameters;
import parameters.ProjectParameters.ButtonAction;
import platform.PlatformModel;
import utilities.Utility;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TCExecute {
	private static SWTWorkbenchBot bot;
	private static SWTBotShell workbenchShell;
	private static Collection<TC> tces;
	public static int numberOfProject = 0;
	private static double createTime = 0.0;
	private static double buildTime = 0.0;

	@BeforeClass
	public static void beforeClass() throws Exception {
		// process the model xml
		PlatformModel
				.loadPlatformModel(new File(Utility.getBundlePath(LogUtil.PLUGIN_ID, Constants.PLATFORM_XML_FILE)));
		RTOSManager.loadRTOSModel(new File(Utility.getBundlePath(LogUtil.PLUGIN_ID, Constants.RTOS_PG_XML_FILE)));
		TCManager.loadRTOSModel(new File(Utility.getBundlePath(LogUtil.PLUGIN_ID, Constants.TC_XML_FILE)));
		tces = TCManager.getAllTCes();

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
	public void TC_00_execute () throws ParseException {
		workbenchShell.setFocus();
		//Utility.changeRTOSLocation();
		long start = System.nanoTime();
		for (TC tc : tces) {
			Utility.executeTCStep(tc, workbenchShell);
		}
		long end = System.nanoTime();
		long timeExecute = end-start;
		createTime = (double) timeExecute/1_000_000_000.0;
		System.out.println("Create time: " + createTime + " seconds");
	}
	
//	@Test
//	public void TC_01_checkBuild () {
//		SWTBotView consoleView = bot.viewById("org.eclipse.ui.console.ConsoleView");
//		long start = System.nanoTime();
//		while (true) {
//			bot.sleep(1);
//			if (consoleView.bot().styledText().getText().contains(ProjectParameters.BUILD_SUCCESSFULLY)) {
//				numberOfProject--;
//				bot.sleep(10000);
//			}
//			if (consoleView.bot().styledText().getText().contains(ProjectParameters.BUILD_FAILED)) {
//				numberOfProject--;
//				bot.sleep(10000);
//			}
//			if(numberOfProject<=0) {
//				break;
//			}
//		}
//		long end = System.nanoTime();
//		long timeExecute = end-start;
//		buildTime = (double) timeExecute/1_000_000_000.0;
//		System.out.println("PG time: " + createTime + " seconds");
//		System.out.println("Build time: " + buildTime + " seconds");
//		double overallTime = createTime + buildTime;
//		System.out.println("Overall time: " + overallTime + " seconds");
//	}
}
