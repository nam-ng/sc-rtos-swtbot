package testcase;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.bindings.keys.ParseException;
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
import model.RTOSManager;
import model.TC;
import model.TCManager;
import parameters.CommonParameters;
import parameters.ProjectParameters;
import parameters.ProjectParameters.ButtonAction;
import platform.PlatformModel;
import utilities.Utility;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TCExecute {
	private static SWTWorkbenchBot bot;
	private static SWTBotShell workbenchShell;
	private static Collection<TC> tces;
	private static Map<String, String> timeRecord = new HashMap<>();

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
		long start = System.currentTimeMillis();
		for (TC tc : tces) {
			Utility.executeTCStep(tc, workbenchShell);
		}
		long end = System.currentTimeMillis();
		long timeExecute = end-start;
		double createTime = (double) timeExecute/1000.0;
		timeRecord.put(CommonParameters.CalculateExecuteTime.CREATETIME, Double.toString(createTime));
	}
	
	
	@Test
	public void TC_01_checkBuild () {
		SWTBotView consoleView = bot.viewById("org.eclipse.ui.console.ConsoleView");
		long start = System.currentTimeMillis();
		String lastProject = "";
		while (true) {
			bot.sleep(10000);
			lastProject = getLastProject();
			bot.tree().getTreeItem(lastProject).select();
			if (consoleView.bot().styledText().getText().contains(ProjectParameters.BUILD_SUCCESSFULLY)) {
				break;
			}
			if (consoleView.bot().styledText().getText().contains(ProjectParameters.BUILD_FAILED)) {
				break;
			}
		}
		long end = System.currentTimeMillis();
		long timeExecute = end-start;
		double buildTime = (double) timeExecute/1000.0;
		timeRecord.put(CommonParameters.CalculateExecuteTime.BUILDTIME, Double.toString(buildTime));
		System.out.println("PG time: " + timeRecord.get(CommonParameters.CalculateExecuteTime.CREATETIME) + " seconds");
		System.out.println("Build time: " + timeRecord.get(CommonParameters.CalculateExecuteTime.BUILDTIME) + " seconds");
		double overallTime = Double.parseDouble(timeRecord.get(CommonParameters.CalculateExecuteTime.CREATETIME)) + Double.parseDouble(timeRecord.get(CommonParameters.CalculateExecuteTime.BUILDTIME));
		timeRecord.put(CommonParameters.CalculateExecuteTime.OVERALLTIME, Double.toString(overallTime));
		System.out.println("Overall time: " + timeRecord.get(CommonParameters.CalculateExecuteTime.OVERALLTIME) + " seconds");
	}
	
	public String getLastProject() {
		int i=0;
		int length = bot.tree().visibleRowCount();
		SWTBotTreeItem[] allItems = bot.tree().getAllItems();
		for (SWTBotTreeItem treeItem: allItems) {
			if(i==length-1) {
				return treeItem.getText();
			}
			i++;
		}
		return "";
	}
	
}
