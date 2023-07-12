package testcase;

import static org.junit.Assert.assertFalse;

import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

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
import model.Project;
import model.ProjectModel;
import model.RTOSManager;
import model.TC;
import model.TCManager;
import parameters.CommonParameters;
import parameters.ProjectParameters;
import platform.PlatformModel;
import utilities.BuildUtility;
import utilities.PGUtility;
import utilities.Utility;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TCExecute {
	private static SWTWorkbenchBot bot;
	private static SWTBotShell workbenchShell;
	private static Collection<TC> tces;
	private static String pathToConfigurationErrorFile = "";
	private static Map<String, String> timeRecordOverall = new HashMap<>();
	public static Map<String, Map<String, String>> PGTimeForCCRX = new HashMap<>();
	public static Map<String, Map<String, String>> PGTimeForGCC = new HashMap<>();
	public static Map<String, Map<String, String>> BuildTimeforCCRX = new HashMap<>();
	public static Map<String, Map<String, String>> BuildTimeforGCC = new HashMap<>();
	public static Collection<ProjectModel> projectModelList = new ArrayList<>();

	@BeforeClass
	public static void beforeClass() throws Exception {
		// process the model xml
		PlatformModel
				.loadPlatformModel(new File(Utility.getBundlePath(LogUtil.PLUGIN_ID, Constants.PLATFORM_XML_FILE)));
		RTOSManager.loadRTOSModel(new File(Utility.getBundlePath(LogUtil.PLUGIN_ID, Constants.RTOS_PG_XML_FILE)));
		TCManager.loadRTOSModel(new File(Utility.getBundlePath(LogUtil.PLUGIN_ID, Constants.TC_XML_FILE)));
		pathToConfigurationErrorFile = Utility.getBundlePath(LogUtil.PLUGIN_ID, Constants.CONFIGURATION_ERROR_CHECK_FILE);
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
		for (TC tc : tces) {
			for (Project project : tc.getProjects()) {
				if (project.getProjectId().equalsIgnoreCase("pg")) {
					Collection<ProjectModel> list = PGUtility.prepareProjectModel(tc);
					projectModelList.addAll(list);
				}
			}
		}
	}

	private static void closeWelcomePage() {
		for (SWTBotView view : bot.views()) {
			if (view.getTitle().equals("Welcome")) {
				view.close();
			}
		}
	}

	@Test
	public void TC_00_execute() throws Exception {
		workbenchShell.setFocus();
		// Utility.changeRTOSLocation();
		long start = System.currentTimeMillis();
		for (TC tc : tces) {
			Utility.executeTCStep(tc, workbenchShell);
		}
		long end = System.currentTimeMillis();
		long timeExecute = end - start;
		double createTime = (double) timeExecute / 1000.0;
		timeRecordOverall.put(CommonParameters.CalculateExecuteTime.CREATETIME, Double.toString(createTime));
	}

	@Test
	public void TC_01_checkConfigurationError() throws Exception {
		int index = 0;
		int length = bot.tree().visibleRowCount();
		String currentProjectName = "";
		ProjectModel projectModel = null;
		boolean isThereConfigurationError = false;
		PrintWriter writer = new PrintWriter(pathToConfigurationErrorFile, "UTF-8");
		while (true) {
			currentProjectName = getCurrentProject(index);
			projectModel = findProjectModelWithProjectName(currentProjectName);
			if (projectModel != null) {
				bot.tree().getTreeItem(currentProjectName).select();
				Utility.openSCFGEditor(projectModel, "Overview");
				for (int i = 0; i < bot.table().rowCount(); i++) {
					if (bot.table(0).getTableItem(i).getText(2).contains("configuration error")) {
						writer.println("Project "+ currentProjectName + " has configuration error on component: "+ bot.table(0).getTableItem(i).getText(2));
						isThereConfigurationError = true;
					}
				}
				
				bot.closeAllEditors();
				bot.tree()
						.getTreeItem(
								projectModel.getProjectName() + " [" + projectModel.getActiveBuildConfiguration() + "]")
						.collapse();
			}
			index++;
			if (index == length) {
				break;
			}
		}
		writer.close();
		if (isThereConfigurationError) {
			assertFalse(true);
		}
	}

	private ProjectModel findProjectModelWithProjectName(String projectName) {
		for (ProjectModel projectModel : projectModelList) {
			if (projectName.contains(projectModel.getProjectName())) {
				return projectModel;
			}
		}
		return null;
	}

	@Test
	public void TC_02_checkBuild() throws Exception {
		BuildUtility.buildAll(workbenchShell);
		SWTBotView consoleView = bot.viewById("org.eclipse.ui.console.ConsoleView");
		long start = System.currentTimeMillis();
		long checkPointStart = System.currentTimeMillis();
		long checkPointEnd;
		long timeExecutedCheckPoint;
		double buildTimeCheckPoint;
		String currentProject = "";
		String[] stringSplit;
		int index = 0;
		Map<String, String> boardAndTime;
		int length = bot.tree().visibleRowCount();
		while (true) {
			bot.sleep(1000);

			currentProject = getCurrentProject(index);
			stringSplit = currentProject.split("_");
			bot.tree().getTreeItem(currentProject).select();
			if (consoleView.bot().styledText().getText().contains(ProjectParameters.BUILD_SUCCESSFULLY)
					|| consoleView.bot().styledText().getText().contains(ProjectParameters.BUILD_FAILED)) {
				index++;
				checkPointEnd = System.currentTimeMillis();
				timeExecutedCheckPoint = checkPointEnd - checkPointStart;
				buildTimeCheckPoint = (double) timeExecutedCheckPoint / 1000.0;
				if (stringSplit[1].equals("CCRX")) {
					boardAndTime = BuildTimeforCCRX.get(stringSplit[0]);
					if (boardAndTime == null) {
						boardAndTime = new HashMap<>();
					}
					boardAndTime.put(stringSplit[2], Double.toString(buildTimeCheckPoint));
					TCExecute.BuildTimeforCCRX.put(stringSplit[0], boardAndTime);
				} else if (stringSplit[1].equals("GCC")) {
					boardAndTime = BuildTimeforGCC.get(stringSplit[0]);
					if (boardAndTime == null) {
						boardAndTime = new HashMap<>();
					}
					boardAndTime.put(stringSplit[2], Double.toString(buildTimeCheckPoint));
					TCExecute.BuildTimeforGCC.put(stringSplit[0], boardAndTime);
				}
				checkPointStart = System.currentTimeMillis();

			}
			if (index == length) {
				break;
			}
		}
		long end = System.currentTimeMillis();
		long timeExecute = end - start;
		double buildTime = (double) timeExecute / 1000.0;
		calculateSumAndAverage("PG", "CCRX", PGTimeForCCRX);
		calculateSumAndAverage("PG", "GCC", PGTimeForGCC);
		calculateSumAndAverage("Build", "CCRX", BuildTimeforCCRX);
		calculateSumAndAverage("Build", "GCC", BuildTimeforGCC);

		timeRecordOverall.put(CommonParameters.CalculateExecuteTime.BUILDTIME, Double.toString(buildTime));
		System.out.println(
				"PG time: " + timeRecordOverall.get(CommonParameters.CalculateExecuteTime.CREATETIME) + " seconds");
		System.out.println(
				"Build time: " + timeRecordOverall.get(CommonParameters.CalculateExecuteTime.BUILDTIME) + " seconds");
		double overallTime = Double.parseDouble(timeRecordOverall.get(CommonParameters.CalculateExecuteTime.CREATETIME))
				+ Double.parseDouble(timeRecordOverall.get(CommonParameters.CalculateExecuteTime.BUILDTIME));
		timeRecordOverall.put(CommonParameters.CalculateExecuteTime.OVERALLTIME, Double.toString(overallTime));
		System.out.println("Overall time: " + timeRecordOverall.get(CommonParameters.CalculateExecuteTime.OVERALLTIME)
				+ " seconds");
	}

	public String getCurrentProject(int index) {
		int i = 0;
		SWTBotTreeItem[] allItems = bot.tree().getAllItems();
		for (SWTBotTreeItem treeItem : allItems) {
			if (i == index) {
				return treeItem.getText();
			}
			i++;
		}
		return "";
	}

	public void calculateSumAndAverage(String pGOrBuild, String toolchain, Map<String, Map<String, String>> timeMap) {
		int i = 0;
		int j = 0;
		double sum = 0.0;
		double average = 0.0;
		for (Map.Entry<String, Map<String, String>> application : timeMap.entrySet()) {

			Map<String, String> boardAndTime = application.getValue();
			for (Map.Entry<String, String> entry : boardAndTime.entrySet()) {
				j++;
				sum = sum + Double.parseDouble(entry.getValue());
				System.out.println(pGOrBuild + " time for " + application.getKey() + "_" + toolchain + "_"
						+ entry.getKey() + ": " + entry.getValue() + "s");
			}
			i++;
			average = sum / j;
			System.out.println("Overall " + pGOrBuild + " time for " + application.getKey() + ", " + toolchain + " is "
					+ sum + "s");
			System.out.println("Average " + pGOrBuild + " time for " + application.getKey() + ", " + toolchain + " is "
					+ average + "s");

			j = 0;
			sum = 0.0;
		}
	}

}
