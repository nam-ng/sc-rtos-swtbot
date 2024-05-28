package testsuitesfor202407;

import static org.junit.Assert.assertTrue;

import java.awt.Robot;
import java.io.File;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Widget;
import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotView;
import org.eclipse.swtbot.swt.finder.exceptions.WidgetNotFoundException;
import org.eclipse.swtbot.swt.finder.matchers.WidgetMatcherFactory;
import org.eclipse.swtbot.swt.finder.utils.SWTBotPreferences;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTree;
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
import parameters.ProjectParameters.RTOSType;
import parameters.ProjectParameters.SCApplication;
import parameters.ProjectParameters.TargetBoard;
import platform.PlatformModel;
import utilities.BuildUtility;
import utilities.PGUtility;
import utilities.Utility;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class RSC_17552_VerifyImproveBlinkySample {
	private static final String PLATFORM_XML_FILE = "xml/platformdata.xml";
	private static final String RTOS_PG_XML_FILE = "xml/rtospg.xml";
	private static SWTWorkbenchBot bot;
	private static Robot robot;
	private Map<String, String> interruptChannelMap = new HashMap<>();

	@BeforeClass
	public static void beforeClass() throws Exception {
		bot = new SWTWorkbenchBot();
		PlatformModel.loadPlatformModel(new File(Utility.getBundlePath(LogUtil.PLUGIN_ID, PLATFORM_XML_FILE)));
		RTOSManager.loadRTOSModel(new File(Utility.getBundlePath(LogUtil.PLUGIN_ID, RTOS_PG_XML_FILE)));
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
	public void tc_02_SetToolchainManagement() throws Exception {
		Utility.setToolchainManagement(robot, "LLVM for RISC-V", ProjectParameters.FileLocation.TOOLCHAIN_RENESAS_LLVM_RISCV_V17_0_2);
		Utility.reFocus(robot);
		Utility.setToolchainManagement(robot, "Renesas CC-RL", ProjectParameters.FileLocation.TOOLCHAIN_RENESAS_CCRL_RL78_V1_13);
		Utility.reFocus(robot);
	}

	@Test
	public void tc_03_ChangeFITModuleLocation() throws Exception {
		Utility.changeModuleDownloadLocation(robot, ProjectParameters.FileLocation.NEWEST_FIT_MODULES_LOCATION, 2);
		Utility.reFocus(robot);
	}

	@Test
	public void tc_04_CreateNoneCCRX_CKRX65NProject() throws Exception {
		ProjectModel model = PGUtility.prepareProjectModel(Constants.CCRX_TOOLCHAIN,
				ProjectParameters.LanguageType.C_LANGUAGE, RTOSType.NONE, "", TargetBoard.BOARD_CK_RX65N,
				SCApplication.SC_BLINKY);
		String projectName = model.getProjectName();
		PGUtility.createProject(Constants.CCRX_TOOLCHAIN, ProjectParameters.LanguageType.C_LANGUAGE, RTOSType.NONE, "",
				TargetBoard.BOARD_CK_RX65N, SCApplication.SC_BLINKY);
		// open SCFG editor and check components
		Utility.openSCFGEditorByProjectName(projectName, "Components");
		// select FreeRTOS_Object component
		String[] rxConfigICUComponent = { "Drivers", "Interrupt", "Config_ICU" };
		SWTBotTreeItem configICUNode = getNodeChild(bot.tree(1), rxConfigICUComponent);
		if (configICUNode != null) {
			configICUNode.select();
		}
		assertTrue(configICUNode != null);
		Display.getDefault().syncExec(() -> {
			List<? extends Widget> widgets = bot.getFinder()
					.findControls(WidgetMatcherFactory.widgetOfType(org.eclipse.swt.widgets.Button.class));
			for (Widget widget : widgets) {
				if (widget instanceof Button button && button.getSelection()) {
					interruptChannelMap.put(projectName, button.getText());
				}
			}
		});
		// check settings of interrupt
		boolean isFallingEdge = false;
		boolean isPriorityLevelRight = false;
		String interuptChannel = interruptChannelMap.get(projectName);
		if (interuptChannel != null) {
			isFallingEdge = "Falling edge".equals(bot.comboBoxInGroup(interuptChannel + " setting", 0).selection());
			isPriorityLevelRight = "Level 15 (highest)"
					.equals(bot.comboBoxInGroup(interuptChannel + " setting", 2).selection());
		}
		assertTrue(interuptChannel != null && isFallingEdge && isPriorityLevelRight);

		// check template file
		boolean isMainContentDefined = false;
		boolean isIcuUserContentDefined = false;
		String workspaceLocation = Utility.getWorkspaceLocation();
		if (!workspaceLocation.isEmpty()) {
			// main.c file
			String mainCPath = workspaceLocation + "/" + projectName + "/src/"
					+ projectName + ".c";
			String configUserPath = workspaceLocation + "/" + projectName
					+ "/src/smc_gen/Config_ICU/Config_ICU_user.c";
			List<String> mainFileKeys = List.of("volatile uint32_t blinkDelay = 1000;",
					"R_Config_ICU_" + interuptChannel + "_Start()",
					"R_BSP_SoftwareDelay(blinkDelay, BSP_DELAY_MILLISECS);");
			isMainContentDefined = Utility.checkContentOfFile(Paths.get(mainCPath), mainFileKeys, false);
			// Config_ICU_user.c file
			List<String> icuHeaderKeys = List.of("extern volatile uint32_t blinkDelay;");
			boolean isIcuHeaderDefined = Utility.checkContentOfFile(Paths.get(configUserPath), icuHeaderKeys, false);
			List<String> switchCaseKey = List.of("switch (blinkDelay) {", "case 1000:",
					"blinkDelay = 200; // Change blink delay to 200 milliseconds (5 Hz)", "break;", "case 200:",
					"blinkDelay = 100; // Change blink delay to 100 milliseconds (10 Hz)", "break;", "default:",
					"blinkDelay = 1000; // Change blink delay back to 1000 milliseconds (1 Hz)", "break;", "}");
			boolean isSwitchCaseDefined = Utility.checkContentOfFile(Paths.get(configUserPath), switchCaseKey, true);
			isIcuUserContentDefined = isIcuHeaderDefined && isSwitchCaseDefined;
		}
		assertTrue(isMainContentDefined && isIcuUserContentDefined);
		// build project
		assertTrue(BuildUtility.buildProject(model));

	}

	@Test
	public void tc_05_CreateNoneCCRX_CKRX65NCPPProject() throws Exception {
		ProjectModel model = PGUtility.prepareProjectModel(Constants.CCRX_TOOLCHAIN,
				ProjectParameters.LanguageType.CPP_LANGUAGE, RTOSType.NONE, "", TargetBoard.BOARD_CK_RX65N,
				SCApplication.SC_BLINKY);
		String projectName = model.getProjectName();
		PGUtility.createProject(Constants.CCRX_TOOLCHAIN, ProjectParameters.LanguageType.CPP_LANGUAGE, RTOSType.NONE,
				"", TargetBoard.BOARD_CK_RX65N, SCApplication.SC_BLINKY);
		// open SCFG editor and check components
		Utility.openSCFGEditorByProjectName(projectName, "Components");
		// select FreeRTOS_Object component
		String[] rxConfigICUComponent = { "Drivers", "Interrupt", "Config_ICU" };
		SWTBotTreeItem configICUNode = getNodeChild(bot.tree(1), rxConfigICUComponent);
		if (configICUNode != null) {
			configICUNode.select();
		}
		assertTrue(configICUNode != null);
		Display.getDefault().syncExec(() -> {
			List<? extends Widget> widgets = bot.getFinder()
					.findControls(WidgetMatcherFactory.widgetOfType(org.eclipse.swt.widgets.Button.class));
			for (Widget widget : widgets) {
				if (widget instanceof Button button && button.getSelection()) {
					interruptChannelMap.put(projectName, button.getText());
				}
			}
		});
		// check settings of interrupt
		boolean isFallingEdge = false;
		boolean isPriorityLevelRight = false;
		String interuptChannel = interruptChannelMap.get(projectName);
		if (interuptChannel != null) {
			isFallingEdge = "Falling edge".equals(bot.comboBoxInGroup(interuptChannel + " setting", 0).selection());
			isPriorityLevelRight = "Level 15 (highest)"
					.equals(bot.comboBoxInGroup(interuptChannel + " setting", 2).selection());
		}
		assertTrue(interuptChannel != null && isFallingEdge && isPriorityLevelRight);

		// check template file
		boolean isMainContentDefined = false;
		boolean isIcuUserContentDefined = false;
		String workspaceLocation = Utility.getWorkspaceLocation();
		if (!workspaceLocation.isEmpty()) {
			// main.c file
			String mainCPath = workspaceLocation + "/" + projectName + "/src/"
					+ projectName + ".cpp";
			String configUserPath = workspaceLocation + "/" + projectName
					+ "/src/smc_gen/Config_ICU/Config_ICU_user.c";
			List<String> mainFileKeys = List.of("volatile uint32_t blinkDelay = 1000;",
					"R_Config_ICU_" + interuptChannel + "_Start()",
					"R_BSP_SoftwareDelay(blinkDelay, BSP_DELAY_MILLISECS);");
			isMainContentDefined = Utility.checkContentOfFile(Paths.get(mainCPath), mainFileKeys, false);
			// Config_ICU_user.c file
			List<String> icuHeaderKeys = List.of("extern volatile uint32_t blinkDelay;");
			boolean isIcuHeaderDefined = Utility.checkContentOfFile(Paths.get(configUserPath), icuHeaderKeys, false);
			List<String> switchCaseKey = List.of("switch (blinkDelay) {", "case 1000:",
					"blinkDelay = 200; // Change blink delay to 200 milliseconds (5 Hz)", "break;", "case 200:",
					"blinkDelay = 100; // Change blink delay to 100 milliseconds (10 Hz)", "break;", "default:",
					"blinkDelay = 1000; // Change blink delay back to 1000 milliseconds (1 Hz)", "break;", "}");
			boolean isSwitchCaseDefined = Utility.checkContentOfFile(Paths.get(configUserPath), switchCaseKey, true);
			isIcuUserContentDefined = isIcuHeaderDefined && isSwitchCaseDefined;
		}
		assertTrue(isMainContentDefined && isIcuUserContentDefined);
		// build project
		assertTrue(BuildUtility.buildProject(model));

	}

	@Test
	public void tc_06_CreateNoneCCRX_RSKRX65N2MBProject() throws Exception {
		ProjectModel model = PGUtility.prepareProjectModel(Constants.CCRX_TOOLCHAIN,
				ProjectParameters.LanguageType.C_LANGUAGE, RTOSType.NONE, "", TargetBoard.BOARD_RSK_RX65N_2MB,
				SCApplication.SC_BLINKY);
		String projectName = model.getProjectName();
		PGUtility.createProject(Constants.CCRX_TOOLCHAIN,
				ProjectParameters.LanguageType.C_LANGUAGE, RTOSType.NONE, "", TargetBoard.BOARD_RSK_RX65N_2MB,
				SCApplication.SC_BLINKY);
		// open SCFG editor and check components
		Utility.openSCFGEditorByProjectName(projectName, "Components");
		// select FreeRTOS_Object component
		String[] rxConfigICUComponent = { "Drivers", "Interrupt", "Config_ICU" };
		SWTBotTreeItem configICUNode = getNodeChild(bot.tree(1), rxConfigICUComponent);
		if (configICUNode != null) {
			configICUNode.select();
		}
		assertTrue(configICUNode != null);
		Display.getDefault().syncExec(() -> {
			List<? extends Widget> widgets = bot.getFinder()
					.findControls(WidgetMatcherFactory.widgetOfType(org.eclipse.swt.widgets.Button.class));
			for (Widget widget : widgets) {
				if (widget instanceof Button button && button.getSelection()) {
					interruptChannelMap.put(projectName, button.getText());
				}
			}
		});
		// check settings of interrupt
		boolean isFallingEdge = false;
		boolean isPriorityLevelRight = false;
		String interuptChannel = interruptChannelMap.get(projectName);
		if (interuptChannel != null) {
			isFallingEdge = "Falling edge".equals(bot.comboBoxInGroup(interuptChannel + " setting", 0).selection());
			isPriorityLevelRight = "Level 15 (highest)"
					.equals(bot.comboBoxInGroup(interuptChannel + " setting", 2).selection());
		}
		assertTrue(interuptChannel != null && isFallingEdge && isPriorityLevelRight);

		// check template file
		boolean isMainContentDefined = false;
		boolean isIcuUserContentDefined = false;
		String workspaceLocation = Utility.getWorkspaceLocation();
		if (!workspaceLocation.isEmpty()) {
			// main.c file
			String mainCPath = workspaceLocation + "/" + projectName + "/src/"
					+ projectName + ".c";
			String configUserPath = workspaceLocation + "/" + projectName
					+ "/src/smc_gen/Config_ICU/Config_ICU_user.c";
			List<String> mainFileKeys = List.of("volatile uint32_t blinkDelay = 1000;",
					"R_Config_ICU_" + interuptChannel + "_Start()",
					"R_BSP_SoftwareDelay(blinkDelay, BSP_DELAY_MILLISECS);");
			isMainContentDefined = Utility.checkContentOfFile(Paths.get(mainCPath), mainFileKeys, false);
			// Config_ICU_user.c file
			List<String> icuHeaderKeys = List.of("extern volatile uint32_t blinkDelay;");
			boolean isIcuHeaderDefined = Utility.checkContentOfFile(Paths.get(configUserPath), icuHeaderKeys, false);
			List<String> switchCaseKey = List.of("switch (blinkDelay) {", "case 1000:",
					"blinkDelay = 200; // Change blink delay to 200 milliseconds (5 Hz)", "break;", "case 200:",
					"blinkDelay = 100; // Change blink delay to 100 milliseconds (10 Hz)", "break;", "default:",
					"blinkDelay = 1000; // Change blink delay back to 1000 milliseconds (1 Hz)", "break;", "}");
			boolean isSwitchCaseDefined = Utility.checkContentOfFile(Paths.get(configUserPath), switchCaseKey, true);
			isIcuUserContentDefined = isIcuHeaderDefined && isSwitchCaseDefined;
		}
		assertTrue(isMainContentDefined && isIcuUserContentDefined);
		// build project
		assertTrue(BuildUtility.buildProject(model));

	}

	@Test
	public void tc_07_CreateNoneCCRX_RSKRX65N2MBCPPProject() throws Exception {
		ProjectModel model = PGUtility.prepareProjectModel(Constants.GCC_TOOLCHAIN,
				ProjectParameters.LanguageType.CPP_LANGUAGE, RTOSType.NONE, "", TargetBoard.BOARD_RSK_RX65N_2MB,
				SCApplication.SC_BLINKY);
		String projectName = model.getProjectName();
		PGUtility.createProject(Constants.GCC_TOOLCHAIN, ProjectParameters.LanguageType.CPP_LANGUAGE, RTOSType.NONE,
				"", TargetBoard.BOARD_RSK_RX65N_2MB, SCApplication.SC_BLINKY);
		// open SCFG editor and check components
		Utility.openSCFGEditorByProjectName(projectName, "Components");
		// select FreeRTOS_Object component
		String[] rxConfigICUComponent = { "Drivers", "Interrupt", "Config_ICU" };
		SWTBotTreeItem configICUNode = getNodeChild(bot.tree(1), rxConfigICUComponent);
		if (configICUNode != null) {
			configICUNode.select();
		}
		assertTrue(configICUNode != null);
		Display.getDefault().syncExec(() -> {
			List<? extends Widget> widgets = bot.getFinder()
					.findControls(WidgetMatcherFactory.widgetOfType(org.eclipse.swt.widgets.Button.class));
			for (Widget widget : widgets) {
				if (widget instanceof Button button && button.getSelection()) {
					interruptChannelMap.put(projectName, button.getText());
				}
			}
		});
		// check settings of interrupt
		boolean isFallingEdge = false;
		boolean isPriorityLevelRight = false;
		String interuptChannel = interruptChannelMap.get(projectName);
		if (interuptChannel != null) {
			isFallingEdge = "Falling edge".equals(bot.comboBoxInGroup(interuptChannel + " setting", 0).selection());
			isPriorityLevelRight = "Level 15 (highest)"
					.equals(bot.comboBoxInGroup(interuptChannel + " setting", 2).selection());
		}
		assertTrue(interuptChannel != null && isFallingEdge && isPriorityLevelRight);

		// check template file
		boolean isMainContentDefined = false;
		boolean isIcuUserContentDefined = false;
		String workspaceLocation = Utility.getWorkspaceLocation();
		if (!workspaceLocation.isEmpty()) {
			// main.c file
			String mainCPath = workspaceLocation + "/" + projectName + "/src/"
					+ projectName + ".cpp";
			String configUserPath = workspaceLocation + "/" + projectName
					+ "/src/smc_gen/Config_ICU/Config_ICU_user.c";
			List<String> mainFileKeys = List.of("volatile uint32_t blinkDelay = 1000;",
					"R_Config_ICU_" + interuptChannel + "_Start()",
					"R_BSP_SoftwareDelay(blinkDelay, BSP_DELAY_MILLISECS);");
			isMainContentDefined = Utility.checkContentOfFile(Paths.get(mainCPath), mainFileKeys, false);
			// Config_ICU_user.c file
			List<String> icuHeaderKeys = List.of("extern volatile uint32_t blinkDelay;");
			boolean isIcuHeaderDefined = Utility.checkContentOfFile(Paths.get(configUserPath), icuHeaderKeys, false);
			List<String> switchCaseKey = List.of("switch (blinkDelay) {", "case 1000:",
					"blinkDelay = 200; // Change blink delay to 200 milliseconds (5 Hz)", "break;", "case 200:",
					"blinkDelay = 100; // Change blink delay to 100 milliseconds (10 Hz)", "break;", "default:",
					"blinkDelay = 1000; // Change blink delay back to 1000 milliseconds (1 Hz)", "break;", "}");
			boolean isSwitchCaseDefined = Utility.checkContentOfFile(Paths.get(configUserPath), switchCaseKey, true);
			isIcuUserContentDefined = isIcuHeaderDefined && isSwitchCaseDefined;
		}
		assertTrue(isMainContentDefined && isIcuUserContentDefined);
		// build project
		assertTrue(BuildUtility.buildProject(model));

	}

	@Test
	public void tc_08_CreateNoneGCCRX_RSKRX65N2MBProject() throws Exception {
		ProjectModel model = PGUtility.prepareProjectModel(Constants.GCC_TOOLCHAIN,
				ProjectParameters.LanguageType.C_LANGUAGE, RTOSType.NONE, "", TargetBoard.BOARD_RSK_RX65N_2MB,
				SCApplication.SC_BLINKY);
		String projectName = model.getProjectName();
		PGUtility.createProject(Constants.GCC_TOOLCHAIN, ProjectParameters.LanguageType.C_LANGUAGE, RTOSType.NONE,
				"", TargetBoard.BOARD_RSK_RX65N_2MB, SCApplication.SC_BLINKY);
		// open SCFG editor and check components
		Utility.openSCFGEditorByProjectName(projectName, "Components");
		// select FreeRTOS_Object component
		String[] rxConfigICUComponent = { "Drivers", "Interrupt", "Config_ICU" };
		SWTBotTreeItem configICUNode = getNodeChild(bot.tree(1), rxConfigICUComponent);
		if (configICUNode != null) {
			configICUNode.select();
		}
		assertTrue(configICUNode != null);
		Display.getDefault().syncExec(() -> {
			List<? extends Widget> widgets = bot.getFinder()
					.findControls(WidgetMatcherFactory.widgetOfType(org.eclipse.swt.widgets.Button.class));
			for (Widget widget : widgets) {
				if (widget instanceof Button button && button.getSelection()) {
					interruptChannelMap.put(projectName, button.getText());
				}
			}
		});
		// check settings of interrupt
		boolean isFallingEdge = false;
		boolean isPriorityLevelRight = false;
		String interuptChannel = interruptChannelMap.get(projectName);
		if (interuptChannel != null) {
			isFallingEdge = "Falling edge".equals(bot.comboBoxInGroup(interuptChannel + " setting", 0).selection());
			isPriorityLevelRight = "Level 15 (highest)"
					.equals(bot.comboBoxInGroup(interuptChannel + " setting", 2).selection());
		}
		assertTrue(interuptChannel != null && isFallingEdge && isPriorityLevelRight);

		// check template file
		boolean isMainContentDefined = false;
		boolean isIcuUserContentDefined = false;
		String workspaceLocation = Utility.getWorkspaceLocation();
		if (!workspaceLocation.isEmpty()) {
			// main.c file
			String mainCPath = workspaceLocation + "/" + projectName + "/src/"
					+ projectName + ".c";
			String configUserPath = workspaceLocation + "/" + projectName
					+ "/src/smc_gen/Config_ICU/Config_ICU_user.c";
			List<String> mainFileKeys = List.of("volatile uint32_t blinkDelay = 1000;",
					"R_Config_ICU_" + interuptChannel + "_Start()",
					"R_BSP_SoftwareDelay(blinkDelay, BSP_DELAY_MILLISECS);");
			isMainContentDefined = Utility.checkContentOfFile(Paths.get(mainCPath), mainFileKeys, false);
			// Config_ICU_user.c file
			List<String> icuHeaderKeys = List.of("extern volatile uint32_t blinkDelay;");
			boolean isIcuHeaderDefined = Utility.checkContentOfFile(Paths.get(configUserPath), icuHeaderKeys, false);
			List<String> switchCaseKey = List.of("switch (blinkDelay) {", "case 1000:",
					"blinkDelay = 200; // Change blink delay to 200 milliseconds (5 Hz)", "break;", "case 200:",
					"blinkDelay = 100; // Change blink delay to 100 milliseconds (10 Hz)", "break;", "default:",
					"blinkDelay = 1000; // Change blink delay back to 1000 milliseconds (1 Hz)", "break;", "}");
			boolean isSwitchCaseDefined = Utility.checkContentOfFile(Paths.get(configUserPath), switchCaseKey, true);
			isIcuUserContentDefined = isIcuHeaderDefined && isSwitchCaseDefined;
		}
		assertTrue(isMainContentDefined && isIcuUserContentDefined);
		// check Linker optimization option
		
		// build project
		assertTrue(BuildUtility.buildProject(model));

	}

	@Test
	public void tc_09_CreateNoneGCCRX_RSKRX65N2MBCPPProject() throws Exception {
		ProjectModel model = PGUtility.prepareProjectModel(Constants.CCRX_TOOLCHAIN,
				ProjectParameters.LanguageType.CPP_LANGUAGE, RTOSType.NONE, "", TargetBoard.BOARD_RSK_RX65N_2MB,
				SCApplication.SC_BLINKY);
		String projectName = model.getProjectName();
		PGUtility.createProject(Constants.CCRX_TOOLCHAIN, ProjectParameters.LanguageType.CPP_LANGUAGE, RTOSType.NONE,
				"", TargetBoard.BOARD_RSK_RX65N_2MB, SCApplication.SC_BLINKY);
		// open SCFG editor and check components
		Utility.openSCFGEditorByProjectName(projectName, "Components");
		// select FreeRTOS_Object component
		String[] rxConfigICUComponent = { "Drivers", "Interrupt", "Config_ICU" };
		SWTBotTreeItem configICUNode = getNodeChild(bot.tree(1), rxConfigICUComponent);
		if (configICUNode != null) {
			configICUNode.select();
		}
		assertTrue(configICUNode != null);
		Display.getDefault().syncExec(() -> {
			List<? extends Widget> widgets = bot.getFinder()
					.findControls(WidgetMatcherFactory.widgetOfType(org.eclipse.swt.widgets.Button.class));
			for (Widget widget : widgets) {
				if (widget instanceof Button button && button.getSelection()) {
					interruptChannelMap.put(projectName, button.getText());
				}
			}
		});
		// check settings of interrupt
		boolean isFallingEdge = false;
		boolean isPriorityLevelRight = false;
		String interuptChannel = interruptChannelMap.get(projectName);
		if (interuptChannel != null) {
			isFallingEdge = "Falling edge".equals(bot.comboBoxInGroup(interuptChannel + " setting", 0).selection());
			isPriorityLevelRight = "Level 15 (highest)"
					.equals(bot.comboBoxInGroup(interuptChannel + " setting", 2).selection());
		}
		assertTrue(interuptChannel != null && isFallingEdge && isPriorityLevelRight);

		// check template file
		boolean isMainContentDefined = false;
		boolean isIcuUserContentDefined = false;
		String workspaceLocation = Utility.getWorkspaceLocation();
		if (!workspaceLocation.isEmpty()) {
			// main.c file
			String mainCPath = workspaceLocation + "/" + projectName + "/src/"
					+ projectName + ".cpp";
			String configUserPath = workspaceLocation + "/" + projectName
					+ "/src/smc_gen/Config_ICU/Config_ICU_user.c";
			List<String> mainFileKeys = List.of("volatile uint32_t blinkDelay = 1000;",
					"R_Config_ICU_" + interuptChannel + "_Start()",
					"R_BSP_SoftwareDelay(blinkDelay, BSP_DELAY_MILLISECS);");
			isMainContentDefined = Utility.checkContentOfFile(Paths.get(mainCPath), mainFileKeys, false);
			// Config_ICU_user.c file
			List<String> icuHeaderKeys = List.of("extern volatile uint32_t blinkDelay;");
			boolean isIcuHeaderDefined = Utility.checkContentOfFile(Paths.get(configUserPath), icuHeaderKeys, false);
			List<String> switchCaseKey = List.of("switch (blinkDelay) {", "case 1000:",
					"blinkDelay = 200; // Change blink delay to 200 milliseconds (5 Hz)", "break;", "case 200:",
					"blinkDelay = 100; // Change blink delay to 100 milliseconds (10 Hz)", "break;", "default:",
					"blinkDelay = 1000; // Change blink delay back to 1000 milliseconds (1 Hz)", "break;", "}");
			boolean isSwitchCaseDefined = Utility.checkContentOfFile(Paths.get(configUserPath), switchCaseKey, true);
			isIcuUserContentDefined = isIcuHeaderDefined && isSwitchCaseDefined;
		}
		assertTrue(isMainContentDefined && isIcuUserContentDefined);
		// build project
		assertTrue(BuildUtility.buildProject(model));

	}

	@Test
	public void tc_10_CreateNoneLLVM_RL78G2364Project() throws Exception {
		ProjectModel model = PGUtility.prepareProjectModel(Constants.LLVM_RL78_TOOLCHAIN,
				ProjectParameters.LanguageType.C_LANGUAGE, RTOSType.NONE, "", TargetBoard.RL78_BOARD_G23_64P_FPB,
				SCApplication.SC_BLINKY);
		String projectName = model.getProjectName();
		PGUtility.createProject(Constants.LLVM_RL78_TOOLCHAIN, ProjectParameters.LanguageType.C_LANGUAGE, RTOSType.NONE,
				"", TargetBoard.RL78_BOARD_G23_64P_FPB, SCApplication.SC_BLINKY);
		// open SCFG editor and check components
		Utility.openSCFGEditorByProjectName(projectName, "Components");
		// select FreeRTOS_Object component
		String[] rxConfigICUComponent = { "Drivers", "Interrupt", "Config_INTC" };
		SWTBotTreeItem configICUNode = getNodeChild(bot.tree(1), rxConfigICUComponent);
		if (configICUNode != null) {
			configICUNode.select();
		}
		assertTrue(configICUNode != null);
		Display.getDefault().syncExec(() -> {
			List<? extends Widget> widgets = bot.getFinder()
					.findControls(WidgetMatcherFactory.widgetOfType(org.eclipse.swt.widgets.Button.class));
			for (Widget widget : widgets) {
				if (widget instanceof Button button && button.getSelection()) {
					interruptChannelMap.put(projectName, button.getText());
				}
			}
		});
		// check settings of interrupt
		boolean isFallingEdge = false;
		boolean isPriorityLevelRight = false;
		String interuptChannel = interruptChannelMap.get(projectName);
		if (interuptChannel != null) {
			isFallingEdge = "Falling edge".equals(bot.comboBoxInGroup(interuptChannel + " setting", 0).selection());
			isPriorityLevelRight = "Level 0 (high)"
					.equals(bot.comboBoxInGroup(interuptChannel + " setting", 1).selection());
		}
		assertTrue(interuptChannel != null && isFallingEdge && isPriorityLevelRight);

		// check template file
		boolean isMainContentDefined = false;
		boolean isIcuUserContentDefined = false;
		String workspaceLocation = Utility.getWorkspaceLocation();
		if (!workspaceLocation.isEmpty()) {
			// main.c file
			String mainCPath = workspaceLocation + "/" + projectName + "/src/"
					+ projectName + ".c";
			String configUserPath = workspaceLocation + "/" + projectName
					+ "/src/smc_gen/Config_INTC/Config_INTC_user.c";
			List<String> mainFileKeys = List.of("volatile uint32_t blinkDelay = 1000;",
					"R_Config_INTC_" + interuptChannel + "_Start()",
					"R_BSP_SoftwareDelay(blinkDelay, BSP_DELAY_MILLISECS);");
			isMainContentDefined = Utility.checkContentOfFile(Paths.get(mainCPath), mainFileKeys, false);
			// Config_ICU_user.c file
			List<String> icuHeaderKeys = List.of("extern volatile uint32_t blinkDelay;");
			boolean isIcuHeaderDefined = Utility.checkContentOfFile(Paths.get(configUserPath), icuHeaderKeys, false);
			List<String> switchCaseKey = List.of("switch (blinkDelay) {", "case 1000:",
					"blinkDelay = 200; // Change blink delay to 200 milliseconds (5 Hz)", "break;", "case 200:",
					"blinkDelay = 100; // Change blink delay to 100 milliseconds (10 Hz)", "break;", "default:",
					"blinkDelay = 1000; // Change blink delay back to 1000 milliseconds (1 Hz)", "break;", "}");
			boolean isSwitchCaseDefined = Utility.checkContentOfFile(Paths.get(configUserPath), switchCaseKey, true);
			isIcuUserContentDefined = isIcuHeaderDefined && isSwitchCaseDefined;
		}
		assertTrue(isMainContentDefined && isIcuUserContentDefined);
		// build project
		assertTrue(BuildUtility.buildProject(model));
	}

	@Test
	public void tc_11_CreateNoneLLVM_RL78G23128Project() throws Exception {
		ProjectModel model = PGUtility.prepareProjectModel(Constants.LLVM_RL78_TOOLCHAIN,
				ProjectParameters.LanguageType.C_LANGUAGE, RTOSType.NONE, "", TargetBoard.RL78_BOARD_G23_128P_FPB,
				SCApplication.SC_BLINKY);
		String projectName = model.getProjectName();
		PGUtility.createProject(Constants.LLVM_RL78_TOOLCHAIN, ProjectParameters.LanguageType.C_LANGUAGE, RTOSType.NONE,
				"", TargetBoard.RL78_BOARD_G23_128P_FPB, SCApplication.SC_BLINKY);
		// open SCFG editor and check components
		Utility.openSCFGEditorByProjectName(projectName, "Components");
		// select FreeRTOS_Object component
		String[] rxConfigICUComponent = { "Drivers", "Interrupt", "Config_INTC" };
		SWTBotTreeItem configICUNode = getNodeChild(bot.tree(1), rxConfigICUComponent);
		if (configICUNode != null) {
			configICUNode.select();
		}
		assertTrue(configICUNode != null);
		Display.getDefault().syncExec(() -> {
			List<? extends Widget> widgets = bot.getFinder()
					.findControls(WidgetMatcherFactory.widgetOfType(org.eclipse.swt.widgets.Button.class));
			for (Widget widget : widgets) {
				if (widget instanceof Button button && button.getSelection()) {
					interruptChannelMap.put(projectName, button.getText());
				}
			}
		});
		// check settings of interrupt
		boolean isFallingEdge = false;
		boolean isPriorityLevelRight = false;
		String interuptChannel = interruptChannelMap.get(projectName);
		if (interuptChannel != null) {
			isFallingEdge = "Falling edge".equals(bot.comboBoxInGroup(interuptChannel + " setting", 0).selection());
			isPriorityLevelRight = "Level 0 (high)"
					.equals(bot.comboBoxInGroup(interuptChannel + " setting", 1).selection());
		}
		assertTrue(interuptChannel != null && isFallingEdge && isPriorityLevelRight);

		// check template file
		boolean isMainContentDefined = false;
		boolean isIcuUserContentDefined = false;
		String workspaceLocation = Utility.getWorkspaceLocation();
		if (!workspaceLocation.isEmpty()) {
			// main.c file
			String mainCPath = workspaceLocation + "/" + projectName + "/src/"
					+ projectName + ".c";
			String configUserPath = workspaceLocation + "/" + projectName
					+ "/src/smc_gen/Config_INTC/Config_INTC_user.c";
			List<String> mainFileKeys = List.of("volatile uint32_t blinkDelay = 1000;",
					"R_Config_INTC_" + interuptChannel + "_Start()",
					"R_BSP_SoftwareDelay(blinkDelay, BSP_DELAY_MILLISECS);");
			isMainContentDefined = Utility.checkContentOfFile(Paths.get(mainCPath), mainFileKeys, false);
			// Config_ICU_user.c file
			List<String> icuHeaderKeys = List.of("extern volatile uint32_t blinkDelay;");
			boolean isIcuHeaderDefined = Utility.checkContentOfFile(Paths.get(configUserPath), icuHeaderKeys, false);
			List<String> switchCaseKey = List.of("switch (blinkDelay) {", "case 1000:",
					"blinkDelay = 200; // Change blink delay to 200 milliseconds (5 Hz)", "break;", "case 200:",
					"blinkDelay = 100; // Change blink delay to 100 milliseconds (10 Hz)", "break;", "default:",
					"blinkDelay = 1000; // Change blink delay back to 1000 milliseconds (1 Hz)", "break;", "}");
			boolean isSwitchCaseDefined = Utility.checkContentOfFile(Paths.get(configUserPath), switchCaseKey, true);
			isIcuUserContentDefined = isIcuHeaderDefined && isSwitchCaseDefined;
		}
		assertTrue(isMainContentDefined && isIcuUserContentDefined);
		// build project
		assertTrue(BuildUtility.buildProject(model));
	}

	@Test
	public void tc_12_CreateNoneLLVM_RL78G2364CPPProject() throws Exception {
		ProjectModel model = PGUtility.prepareProjectModel(Constants.LLVM_RL78_TOOLCHAIN,
				ProjectParameters.LanguageType.CPP_LANGUAGE, RTOSType.NONE, "", TargetBoard.RL78_BOARD_G23_64P_FPB,
				SCApplication.SC_BLINKY);
		String projectName = model.getProjectName();
		PGUtility.createProject(Constants.LLVM_RL78_TOOLCHAIN, ProjectParameters.LanguageType.CPP_LANGUAGE, RTOSType.NONE,
				"", TargetBoard.RL78_BOARD_G23_64P_FPB, SCApplication.SC_BLINKY);
		// open SCFG editor and check components
		Utility.openSCFGEditorByProjectName(projectName, "Components");
		// select FreeRTOS_Object component
		String[] rxConfigICUComponent = { "Drivers", "Interrupt", "Config_INTC" };
		SWTBotTreeItem configICUNode = getNodeChild(bot.tree(1), rxConfigICUComponent);
		if (configICUNode != null) {
			configICUNode.select();
		}
		assertTrue(configICUNode != null);
		Display.getDefault().syncExec(() -> {
			List<? extends Widget> widgets = bot.getFinder()
					.findControls(WidgetMatcherFactory.widgetOfType(org.eclipse.swt.widgets.Button.class));
			for (Widget widget : widgets) {
				if (widget instanceof Button button && button.getSelection()) {
					interruptChannelMap.put(projectName, button.getText());
				}
			}
		});
		// check settings of interrupt
		boolean isFallingEdge = false;
		boolean isPriorityLevelRight = false;
		String interuptChannel = interruptChannelMap.get(projectName);
		if (interuptChannel != null) {
			isFallingEdge = "Falling edge".equals(bot.comboBoxInGroup(interuptChannel + " setting", 0).selection());
			isPriorityLevelRight = "Level 0 (high)"
					.equals(bot.comboBoxInGroup(interuptChannel + " setting", 1).selection());
		}
		assertTrue(interuptChannel != null && isFallingEdge && isPriorityLevelRight);

		// check template file
		boolean isMainContentDefined = false;
		boolean isIcuUserContentDefined = false;
		String workspaceLocation = Utility.getWorkspaceLocation();
		if (!workspaceLocation.isEmpty()) {
			// main.c file
			String mainCPath = workspaceLocation + "/" + projectName + "/src/"
					+ projectName + ".cpp";
			String configUserPath = workspaceLocation + "/" + projectName
					+ "/src/smc_gen/Config_INTC/Config_INTC_user.c";
			List<String> mainFileKeys = List.of("volatile uint32_t blinkDelay = 1000;",
					"R_Config_INTC_" + interuptChannel + "_Start()",
					"R_BSP_SoftwareDelay(blinkDelay, BSP_DELAY_MILLISECS);");
			isMainContentDefined = Utility.checkContentOfFile(Paths.get(mainCPath), mainFileKeys, false);
			// Config_ICU_user.c file
			List<String> icuHeaderKeys = List.of("extern volatile uint32_t blinkDelay;");
			boolean isIcuHeaderDefined = Utility.checkContentOfFile(Paths.get(configUserPath), icuHeaderKeys, false);
			List<String> switchCaseKey = List.of("switch (blinkDelay) {", "case 1000:",
					"blinkDelay = 200; // Change blink delay to 200 milliseconds (5 Hz)", "break;", "case 200:",
					"blinkDelay = 100; // Change blink delay to 100 milliseconds (10 Hz)", "break;", "default:",
					"blinkDelay = 1000; // Change blink delay back to 1000 milliseconds (1 Hz)", "break;", "}");
			boolean isSwitchCaseDefined = Utility.checkContentOfFile(Paths.get(configUserPath), switchCaseKey, true);
			isIcuUserContentDefined = isIcuHeaderDefined && isSwitchCaseDefined;
		}
		assertTrue(isMainContentDefined && isIcuUserContentDefined);
		// build project
		assertTrue(BuildUtility.buildProject(model));
	}

	@Test
	public void tc_13_CreateNoneLLVM_RL78G23128CPPProject() throws Exception {
		ProjectModel model = PGUtility.prepareProjectModel(Constants.LLVM_RL78_TOOLCHAIN,
				ProjectParameters.LanguageType.CPP_LANGUAGE, RTOSType.NONE, "", TargetBoard.RL78_BOARD_G23_128P_FPB,
				SCApplication.SC_BLINKY);
		String projectName = model.getProjectName();
		PGUtility.createProject(Constants.LLVM_RL78_TOOLCHAIN, ProjectParameters.LanguageType.CPP_LANGUAGE, RTOSType.NONE,
				"", TargetBoard.RL78_BOARD_G23_128P_FPB, SCApplication.SC_BLINKY);
		// open SCFG editor and check components
		Utility.openSCFGEditorByProjectName(projectName, "Components");
		// select FreeRTOS_Object component
		String[] rxConfigICUComponent = { "Drivers", "Interrupt", "Config_INTC" };
		SWTBotTreeItem configICUNode = getNodeChild(bot.tree(1), rxConfigICUComponent);
		if (configICUNode != null) {
			configICUNode.select();
		}
		assertTrue(configICUNode != null);
		Display.getDefault().syncExec(() -> {
			List<? extends Widget> widgets = bot.getFinder()
					.findControls(WidgetMatcherFactory.widgetOfType(org.eclipse.swt.widgets.Button.class));
			for (Widget widget : widgets) {
				if (widget instanceof Button button && button.getSelection()) {
					interruptChannelMap.put(projectName, button.getText());
				}
			}
		});
		// check settings of interrupt
		boolean isFallingEdge = false;
		boolean isPriorityLevelRight = false;
		String interuptChannel = interruptChannelMap.get(projectName);
		if (interuptChannel != null) {
			isFallingEdge = "Falling edge".equals(bot.comboBoxInGroup(interuptChannel + " setting", 0).selection());
			isPriorityLevelRight = "Level 0 (high)"
					.equals(bot.comboBoxInGroup(interuptChannel + " setting", 1).selection());
		}
		assertTrue(interuptChannel != null && isFallingEdge && isPriorityLevelRight);

		// check template file
		boolean isMainContentDefined = false;
		boolean isIcuUserContentDefined = false;
		String workspaceLocation = Utility.getWorkspaceLocation();
		if (!workspaceLocation.isEmpty()) {
			// main.c file
			String mainCPath = workspaceLocation + "/" + projectName + "/src/"
					+ projectName + ".cpp";
			String configUserPath = workspaceLocation + "/" + projectName
					+ "/src/smc_gen/Config_INTC/Config_INTC_user.c";
			List<String> mainFileKeys = List.of("volatile uint32_t blinkDelay = 1000;",
					"R_Config_INTC_" + interuptChannel + "_Start()",
					"R_BSP_SoftwareDelay(blinkDelay, BSP_DELAY_MILLISECS);");
			isMainContentDefined = Utility.checkContentOfFile(Paths.get(mainCPath), mainFileKeys, false);
			// Config_ICU_user.c file
			List<String> icuHeaderKeys = List.of("extern volatile uint32_t blinkDelay;");
			boolean isIcuHeaderDefined = Utility.checkContentOfFile(Paths.get(configUserPath), icuHeaderKeys, false);
			List<String> switchCaseKey = List.of("switch (blinkDelay) {", "case 1000:",
					"blinkDelay = 200; // Change blink delay to 200 milliseconds (5 Hz)", "break;", "case 200:",
					"blinkDelay = 100; // Change blink delay to 100 milliseconds (10 Hz)", "break;", "default:",
					"blinkDelay = 1000; // Change blink delay back to 1000 milliseconds (1 Hz)", "break;", "}");
			boolean isSwitchCaseDefined = Utility.checkContentOfFile(Paths.get(configUserPath), switchCaseKey, true);
			isIcuUserContentDefined = isIcuHeaderDefined && isSwitchCaseDefined;
		}
		assertTrue(isMainContentDefined && isIcuUserContentDefined);
		// build project
		assertTrue(BuildUtility.buildProject(model));
	}

	@Test
	public void tc_14_CreateNoneCCRL_RL78G2364Project() throws Exception {
		ProjectModel model = PGUtility.prepareProjectModel(Constants.CCRL_RL78_TOOLCHAIN,
				ProjectParameters.LanguageType.C_LANGUAGE, RTOSType.NONE, "", TargetBoard.RL78_BOARD_G23_64P_FPB,
				SCApplication.SC_BLINKY);
		String projectName = model.getProjectName();
		PGUtility.createProject(Constants.CCRL_RL78_TOOLCHAIN, ProjectParameters.LanguageType.C_LANGUAGE, RTOSType.NONE,
				"", TargetBoard.RL78_BOARD_G23_64P_FPB, SCApplication.SC_BLINKY);
		// open SCFG editor and check components
		Utility.openSCFGEditorByProjectName(projectName, "Components");
		// select FreeRTOS_Object component
		String[] rxConfigICUComponent = { "Drivers", "Interrupt", "Config_INTC" };
		SWTBotTreeItem configICUNode = getNodeChild(bot.tree(1), rxConfigICUComponent);
		if (configICUNode != null) {
			configICUNode.select();
		}
		assertTrue(configICUNode != null);
		Display.getDefault().syncExec(() -> {
			List<? extends Widget> widgets = bot.getFinder()
					.findControls(WidgetMatcherFactory.widgetOfType(org.eclipse.swt.widgets.Button.class));
			for (Widget widget : widgets) {
				if (widget instanceof Button button && button.getSelection()) {
					interruptChannelMap.put(projectName, button.getText());
				}
			}
		});
		// check settings of interrupt
		boolean isFallingEdge = false;
		boolean isPriorityLevelRight = false;
		String interuptChannel = interruptChannelMap.get(projectName);
		if (interuptChannel != null) {
			isFallingEdge = "Falling edge".equals(bot.comboBoxInGroup(interuptChannel + " setting", 0).selection());
			isPriorityLevelRight = "Level 0 (high)"
					.equals(bot.comboBoxInGroup(interuptChannel + " setting", 1).selection());
		}
		assertTrue(interuptChannel != null && isFallingEdge && isPriorityLevelRight);

		// check template file
		boolean isMainContentDefined = false;
		boolean isIcuUserContentDefined = false;
		String workspaceLocation = Utility.getWorkspaceLocation();
		if (!workspaceLocation.isEmpty()) {
			// main.c file
			String mainCPath = workspaceLocation + "/" + projectName + "/src/"
					+ projectName + ".c";
			String configUserPath = workspaceLocation + "/" + projectName
					+ "/src/smc_gen/Config_INTC/Config_INTC_user.c";
			List<String> mainFileKeys = List.of("volatile uint32_t blinkDelay = 1000;",
					"R_Config_INTC_" + interuptChannel + "_Start()",
					"R_BSP_SoftwareDelay(blinkDelay, BSP_DELAY_MILLISECS);");
			isMainContentDefined = Utility.checkContentOfFile(Paths.get(mainCPath), mainFileKeys, false);
			// Config_ICU_user.c file
			List<String> icuHeaderKeys = List.of("extern volatile uint32_t blinkDelay;");
			boolean isIcuHeaderDefined = Utility.checkContentOfFile(Paths.get(configUserPath), icuHeaderKeys, false);
			List<String> switchCaseKey = List.of("switch (blinkDelay) {", "case 1000:",
					"blinkDelay = 200; // Change blink delay to 200 milliseconds (5 Hz)", "break;", "case 200:",
					"blinkDelay = 100; // Change blink delay to 100 milliseconds (10 Hz)", "break;", "default:",
					"blinkDelay = 1000; // Change blink delay back to 1000 milliseconds (1 Hz)", "break;", "}");
			boolean isSwitchCaseDefined = Utility.checkContentOfFile(Paths.get(configUserPath), switchCaseKey, true);
			isIcuUserContentDefined = isIcuHeaderDefined && isSwitchCaseDefined;
		}
		assertTrue(isMainContentDefined && isIcuUserContentDefined);
		// build project
		assertTrue(BuildUtility.buildProject(model));
	}

	@Test
	public void tc_15_CreateNoneCCRL_RL78G23128Project() throws Exception {
		ProjectModel model = PGUtility.prepareProjectModel(Constants.CCRL_RL78_TOOLCHAIN,
				ProjectParameters.LanguageType.C_LANGUAGE, RTOSType.NONE, "", TargetBoard.RL78_BOARD_G23_128P_FPB,
				SCApplication.SC_BLINKY);
		String projectName = model.getProjectName();
		PGUtility.createProject(Constants.CCRL_RL78_TOOLCHAIN, ProjectParameters.LanguageType.C_LANGUAGE, RTOSType.NONE,
				"", TargetBoard.RL78_BOARD_G23_128P_FPB, SCApplication.SC_BLINKY);
		// open SCFG editor and check components
		Utility.openSCFGEditorByProjectName(projectName, "Components");
		// select FreeRTOS_Object component
		String[] rxConfigICUComponent = { "Drivers", "Interrupt", "Config_INTC" };
		SWTBotTreeItem configICUNode = getNodeChild(bot.tree(1), rxConfigICUComponent);
		if (configICUNode != null) {
			configICUNode.select();
		}
		assertTrue(configICUNode != null);
		Display.getDefault().syncExec(() -> {
			List<? extends Widget> widgets = bot.getFinder()
					.findControls(WidgetMatcherFactory.widgetOfType(org.eclipse.swt.widgets.Button.class));
			for (Widget widget : widgets) {
				if (widget instanceof Button button && button.getSelection()) {
					interruptChannelMap.put(projectName, button.getText());
				}
			}
		});
		// check settings of interrupt
		boolean isFallingEdge = false;
		boolean isPriorityLevelRight = false;
		String interuptChannel = interruptChannelMap.get(projectName);
		if (interuptChannel != null) {
			isFallingEdge = "Falling edge".equals(bot.comboBoxInGroup(interuptChannel + " setting", 0).selection());
			isPriorityLevelRight = "Level 0 (high)"
					.equals(bot.comboBoxInGroup(interuptChannel + " setting", 1).selection());
		}
		assertTrue(interuptChannel != null && isFallingEdge && isPriorityLevelRight);

		// check template file
		boolean isMainContentDefined = false;
		boolean isIcuUserContentDefined = false;
		String workspaceLocation = Utility.getWorkspaceLocation();
		if (!workspaceLocation.isEmpty()) {
			// main.c file
			String mainCPath = workspaceLocation + "/" + projectName + "/src/"
					+ projectName + ".c";
			String configUserPath = workspaceLocation + "/" + projectName
					+ "/src/smc_gen/Config_INTC/Config_INTC_user.c";
			List<String> mainFileKeys = List.of("volatile uint32_t blinkDelay = 1000;",
					"R_Config_INTC_" + interuptChannel + "_Start()",
					"R_BSP_SoftwareDelay(blinkDelay, BSP_DELAY_MILLISECS);");
			isMainContentDefined = Utility.checkContentOfFile(Paths.get(mainCPath), mainFileKeys, false);
			// Config_ICU_user.c file
			List<String> icuHeaderKeys = List.of("extern volatile uint32_t blinkDelay;");
			boolean isIcuHeaderDefined = Utility.checkContentOfFile(Paths.get(configUserPath), icuHeaderKeys, false);
			List<String> switchCaseKey = List.of("switch (blinkDelay) {", "case 1000:",
					"blinkDelay = 200; // Change blink delay to 200 milliseconds (5 Hz)", "break;", "case 200:",
					"blinkDelay = 100; // Change blink delay to 100 milliseconds (10 Hz)", "break;", "default:",
					"blinkDelay = 1000; // Change blink delay back to 1000 milliseconds (1 Hz)", "break;", "}");
			boolean isSwitchCaseDefined = Utility.checkContentOfFile(Paths.get(configUserPath), switchCaseKey, true);
			isIcuUserContentDefined = isIcuHeaderDefined && isSwitchCaseDefined;
		}
		assertTrue(isMainContentDefined && isIcuUserContentDefined);
		// build project
		assertTrue(BuildUtility.buildProject(model));
	}

	@Test
	public void tc_16_CreateNoneCCRL_RL78G2364CPPProject() throws Exception {
		ProjectModel model = PGUtility.prepareProjectModel(Constants.CCRL_RL78_TOOLCHAIN,
				ProjectParameters.LanguageType.CPP_LANGUAGE, RTOSType.NONE, "", TargetBoard.RL78_BOARD_G23_64P_FPB,
				SCApplication.SC_BLINKY);
		String projectName = model.getProjectName();
		PGUtility.createProject(Constants.CCRL_RL78_TOOLCHAIN, ProjectParameters.LanguageType.CPP_LANGUAGE, RTOSType.NONE,
				"", TargetBoard.RL78_BOARD_G23_64P_FPB, SCApplication.SC_BLINKY);
		// open SCFG editor and check components
		Utility.openSCFGEditorByProjectName(projectName, "Components");
		// select FreeRTOS_Object component
		String[] rxConfigICUComponent = { "Drivers", "Interrupt", "Config_INTC" };
		SWTBotTreeItem configICUNode = getNodeChild(bot.tree(1), rxConfigICUComponent);
		if (configICUNode != null) {
			configICUNode.select();
		}
		assertTrue(configICUNode != null);
		Display.getDefault().syncExec(() -> {
			List<? extends Widget> widgets = bot.getFinder()
					.findControls(WidgetMatcherFactory.widgetOfType(org.eclipse.swt.widgets.Button.class));
			for (Widget widget : widgets) {
				if (widget instanceof Button button && button.getSelection()) {
					interruptChannelMap.put(projectName, button.getText());
				}
			}
		});
		// check settings of interrupt
		boolean isFallingEdge = false;
		boolean isPriorityLevelRight = false;
		String interuptChannel = interruptChannelMap.get(projectName);
		if (interuptChannel != null) {
			isFallingEdge = "Falling edge".equals(bot.comboBoxInGroup(interuptChannel + " setting", 0).selection());
			isPriorityLevelRight = "Level 0 (high)"
					.equals(bot.comboBoxInGroup(interuptChannel + " setting", 1).selection());
		}
		assertTrue(interuptChannel != null && isFallingEdge && isPriorityLevelRight);

		// check template file
		boolean isMainContentDefined = false;
		boolean isIcuUserContentDefined = false;
		String workspaceLocation = Utility.getWorkspaceLocation();
		if (!workspaceLocation.isEmpty()) {
			// main.c file
			String mainCPath = workspaceLocation + "/" + projectName + "/src/"
					+ projectName + ".cpp";
			String configUserPath = workspaceLocation + "/" + projectName
					+ "/src/smc_gen/Config_INTC/Config_INTC_user.c";
			List<String> mainFileKeys = List.of("volatile uint32_t blinkDelay = 1000;",
					"R_Config_INTC_" + interuptChannel + "_Start()",
					"R_BSP_SoftwareDelay(blinkDelay, BSP_DELAY_MILLISECS);");
			isMainContentDefined = Utility.checkContentOfFile(Paths.get(mainCPath), mainFileKeys, false);
			// Config_ICU_user.c file
			List<String> icuHeaderKeys = List.of("extern volatile uint32_t blinkDelay;");
			boolean isIcuHeaderDefined = Utility.checkContentOfFile(Paths.get(configUserPath), icuHeaderKeys, false);
			List<String> switchCaseKey = List.of("switch (blinkDelay) {", "case 1000:",
					"blinkDelay = 200; // Change blink delay to 200 milliseconds (5 Hz)", "break;", "case 200:",
					"blinkDelay = 100; // Change blink delay to 100 milliseconds (10 Hz)", "break;", "default:",
					"blinkDelay = 1000; // Change blink delay back to 1000 milliseconds (1 Hz)", "break;", "}");
			boolean isSwitchCaseDefined = Utility.checkContentOfFile(Paths.get(configUserPath), switchCaseKey, true);
			isIcuUserContentDefined = isIcuHeaderDefined && isSwitchCaseDefined;
		}
		assertTrue(isMainContentDefined && isIcuUserContentDefined);
		// build project
		assertTrue(BuildUtility.buildProject(model));
	}

	@Test
	public void tc_17_CreateNoneCCRL_RL78G23128CPPProject() throws Exception {
		ProjectModel model = PGUtility.prepareProjectModel(Constants.CCRL_RL78_TOOLCHAIN,
				ProjectParameters.LanguageType.CPP_LANGUAGE, RTOSType.NONE, "", TargetBoard.RL78_BOARD_G23_128P_FPB,
				SCApplication.SC_BLINKY);
		String projectName = model.getProjectName();
		PGUtility.createProject(Constants.CCRL_RL78_TOOLCHAIN, ProjectParameters.LanguageType.CPP_LANGUAGE, RTOSType.NONE,
				"", TargetBoard.RL78_BOARD_G23_128P_FPB, SCApplication.SC_BLINKY);
		// open SCFG editor and check components
		Utility.openSCFGEditorByProjectName(projectName, "Components");
		// select FreeRTOS_Object component
		String[] rxConfigICUComponent = { "Drivers", "Interrupt", "Config_INTC" };
		SWTBotTreeItem configICUNode = getNodeChild(bot.tree(1), rxConfigICUComponent);
		if (configICUNode != null) {
			configICUNode.select();
		}
		assertTrue(configICUNode != null);
		Display.getDefault().syncExec(() -> {
			List<? extends Widget> widgets = bot.getFinder()
					.findControls(WidgetMatcherFactory.widgetOfType(org.eclipse.swt.widgets.Button.class));
			for (Widget widget : widgets) {
				if (widget instanceof Button button && button.getSelection()) {
					interruptChannelMap.put(projectName, button.getText());
				}
			}
		});
		// check settings of interrupt
		boolean isFallingEdge = false;
		boolean isPriorityLevelRight = false;
		String interuptChannel = interruptChannelMap.get(projectName);
		if (interuptChannel != null) {
			isFallingEdge = "Falling edge".equals(bot.comboBoxInGroup(interuptChannel + " setting", 0).selection());
			isPriorityLevelRight = "Level 0 (high)"
					.equals(bot.comboBoxInGroup(interuptChannel + " setting", 2).selection());
		}
		assertTrue(interuptChannel != null && isFallingEdge && isPriorityLevelRight);

		// check template file
		boolean isMainContentDefined = false;
		boolean isIcuUserContentDefined = false;
		String workspaceLocation = Utility.getWorkspaceLocation();
		if (!workspaceLocation.isEmpty()) {
			// main.c file
			String mainCPath = workspaceLocation + "/" + projectName + "/src/"
					+ projectName + ".cpp";
			String configUserPath = workspaceLocation + "/" + projectName
					+ "/src/smc_gen/Config_INTC/Config_INTC_user.c";
			List<String> mainFileKeys = List.of("volatile uint32_t blinkDelay = 1000;",
					"R_Config_INTC_" + interuptChannel + "_Start()",
					"R_BSP_SoftwareDelay(blinkDelay, BSP_DELAY_MILLISECS);");
			isMainContentDefined = Utility.checkContentOfFile(Paths.get(mainCPath), mainFileKeys, false);
			// Config_ICU_user.c file
			List<String> icuHeaderKeys = List.of("extern volatile uint32_t blinkDelay;");
			boolean isIcuHeaderDefined = Utility.checkContentOfFile(Paths.get(configUserPath), icuHeaderKeys, false);
			List<String> switchCaseKey = List.of("switch (blinkDelay) {", "case 1000:",
					"blinkDelay = 200; // Change blink delay to 200 milliseconds (5 Hz)", "break;", "case 200:",
					"blinkDelay = 100; // Change blink delay to 100 milliseconds (10 Hz)", "break;", "default:",
					"blinkDelay = 1000; // Change blink delay back to 1000 milliseconds (1 Hz)", "break;", "}");
			boolean isSwitchCaseDefined = Utility.checkContentOfFile(Paths.get(configUserPath), switchCaseKey, true);
			isIcuUserContentDefined = isIcuHeaderDefined && isSwitchCaseDefined;
		}
		assertTrue(isMainContentDefined && isIcuUserContentDefined);
		// build project
		assertTrue(BuildUtility.buildProject(model));
	}

	@Test
	public void tc_18_CreateNoneLLVM_RISCVG021Project() throws Exception {
		ProjectModel model = PGUtility.prepareProjectModel(Constants.LLVM_RISCV_TOOLCHAIN,
				ProjectParameters.LanguageType.C_LANGUAGE, RTOSType.NONE, "", TargetBoard.RISCV_BOARD_G021_FPB,
				SCApplication.SC_BLINKY);
		String projectName = model.getProjectName();
		PGUtility.createProject(Constants.LLVM_RISCV_TOOLCHAIN, ProjectParameters.LanguageType.C_LANGUAGE, RTOSType.NONE,
				"", TargetBoard.RISCV_BOARD_G021_FPB, SCApplication.SC_BLINKY);
		// open SCFG editor and check components
		Utility.openSCFGEditorByProjectName(projectName, "Components");
		// select FreeRTOS_Object component
		String[] rxConfigICUComponent = { "Drivers", "Interrupt", "Config_ICU" };
		SWTBotTreeItem configICUNode = getNodeChild(bot.tree(1), rxConfigICUComponent);
		if (configICUNode != null) {
			configICUNode.select();
		}
		assertTrue(configICUNode != null);
		Display.getDefault().syncExec(() -> {
			List<? extends Widget> widgets = bot.getFinder()
					.findControls(WidgetMatcherFactory.widgetOfType(org.eclipse.swt.widgets.Button.class));
			for (Widget widget : widgets) {
				if (widget instanceof Button button && button.getSelection()) {
					interruptChannelMap.put(projectName, button.getText());
				}
			}
		});
		// check settings of interrupt
		boolean isFallingEdge = false;
		boolean isPriorityLevelRight = false;
		String interuptChannel = interruptChannelMap.get(projectName);
		if (interuptChannel != null) {
			isFallingEdge = "Falling edge".equals(bot.comboBoxInGroup(interuptChannel + " setting", 0).selection());
			isPriorityLevelRight = "Level 0 (high)"
					.equals(bot.comboBoxInGroup(interuptChannel + " setting", 2).selection());
		}
		assertTrue(interuptChannel != null && isFallingEdge && isPriorityLevelRight);

		// check template file
		boolean isMainContentDefined = false;
		boolean isIcuUserContentDefined = false;
		String workspaceLocation = Utility.getWorkspaceLocation();
		if (!workspaceLocation.isEmpty()) {
			// main.c file
			String mainCPath = workspaceLocation + "/" + projectName + "/src/"
					+ projectName + ".c";
			String configUserPath = workspaceLocation + "/" + projectName
					+ "/src/smc_gen/Config_ICU/Config_ICU_user.c";
			List<String> mainFileKeys = List.of("volatile uint32_t blinkDelay = 1000;",
					"R_Config_ICU_" + interuptChannel + "_Start()",
					"R_BSP_SoftwareDelay(blinkDelay, BSP_DELAY_MILLISECS);");
			isMainContentDefined = Utility.checkContentOfFile(Paths.get(mainCPath), mainFileKeys, false);
			// Config_ICU_user.c file
			List<String> icuHeaderKeys = List.of("extern volatile uint32_t blinkDelay;");
			boolean isIcuHeaderDefined = Utility.checkContentOfFile(Paths.get(configUserPath), icuHeaderKeys, false);
			List<String> switchCaseKey = List.of("switch (blinkDelay) {", "case 1000:",
					"blinkDelay = 200; // Change blink delay to 200 milliseconds (5 Hz)", "break;", "case 200:",
					"blinkDelay = 100; // Change blink delay to 100 milliseconds (10 Hz)", "break;", "default:",
					"blinkDelay = 1000; // Change blink delay back to 1000 milliseconds (1 Hz)", "break;", "}");
			boolean isSwitchCaseDefined = Utility.checkContentOfFile(Paths.get(configUserPath), switchCaseKey, true);
			isIcuUserContentDefined = isIcuHeaderDefined && isSwitchCaseDefined;
		}
		assertTrue(isMainContentDefined && isIcuUserContentDefined);
		// build project
		assertTrue(BuildUtility.buildProject(model));
	}

	@Test
	public void tc_19_CreateNoneLLVM_RISCVG021CPPProject() throws Exception {
		ProjectModel model = PGUtility.prepareProjectModel(Constants.LLVM_RISCV_TOOLCHAIN,
				ProjectParameters.LanguageType.CPP_LANGUAGE, RTOSType.NONE, "", TargetBoard.RISCV_BOARD_G021_FPB,
				SCApplication.SC_BLINKY);
		String projectName = model.getProjectName();
		PGUtility.createProject(Constants.LLVM_RISCV_TOOLCHAIN, ProjectParameters.LanguageType.CPP_LANGUAGE, RTOSType.NONE,
				"", TargetBoard.RISCV_BOARD_G021_FPB, SCApplication.SC_BLINKY);
		// open SCFG editor and check components
		Utility.openSCFGEditorByProjectName(projectName, "Components");
		// select FreeRTOS_Object component
		String[] rxConfigICUComponent = { "Drivers", "Interrupt", "Config_ICU" };
		SWTBotTreeItem configICUNode = getNodeChild(bot.tree(1), rxConfigICUComponent);
		if (configICUNode != null) {
			configICUNode.select();
		}
		assertTrue(configICUNode != null);
		Display.getDefault().syncExec(() -> {
			List<? extends Widget> widgets = bot.getFinder()
					.findControls(WidgetMatcherFactory.widgetOfType(org.eclipse.swt.widgets.Button.class));
			for (Widget widget : widgets) {
				if (widget instanceof Button button && button.getSelection()) {
					interruptChannelMap.put(projectName, button.getText());
				}
			}
		});
		// check settings of interrupt
		boolean isFallingEdge = false;
		boolean isPriorityLevelRight = false;
		String interuptChannel = interruptChannelMap.get(projectName);
		if (interuptChannel != null) {
			isFallingEdge = "Falling edge".equals(bot.comboBoxInGroup(interuptChannel + " setting", 0).selection());
			isPriorityLevelRight = "Level 0 (high)"
					.equals(bot.comboBoxInGroup(interuptChannel + " setting", 2).selection());
		}
		assertTrue(interuptChannel != null && isFallingEdge && isPriorityLevelRight);

		// check template file
		boolean isMainContentDefined = false;
		boolean isIcuUserContentDefined = false;
		String workspaceLocation = Utility.getWorkspaceLocation();
		if (!workspaceLocation.isEmpty()) {
			// main.c file
			String mainCPath = workspaceLocation + "/" + projectName + "/src/"
					+ projectName + ".cpp";
			String configUserPath = workspaceLocation + "/" + projectName
					+ "/src/smc_gen/Config_ICU/Config_ICU_user.c";
			List<String> mainFileKeys = List.of("volatile uint32_t blinkDelay = 1000;",
					"R_Config_ICU_" + interuptChannel + "_Start()",
					"R_BSP_SoftwareDelay(blinkDelay, BSP_DELAY_MILLISECS);");
			isMainContentDefined = Utility.checkContentOfFile(Paths.get(mainCPath), mainFileKeys, false);
			// Config_ICU_user.c file
			List<String> icuHeaderKeys = List.of("extern volatile uint32_t blinkDelay;");
			boolean isIcuHeaderDefined = Utility.checkContentOfFile(Paths.get(configUserPath), icuHeaderKeys, false);
			List<String> switchCaseKey = List.of("switch (blinkDelay) {", "case 1000:",
					"blinkDelay = 200; // Change blink delay to 200 milliseconds (5 Hz)", "break;", "case 200:",
					"blinkDelay = 100; // Change blink delay to 100 milliseconds (10 Hz)", "break;", "default:",
					"blinkDelay = 1000; // Change blink delay back to 1000 milliseconds (1 Hz)", "break;", "}");
			boolean isSwitchCaseDefined = Utility.checkContentOfFile(Paths.get(configUserPath), switchCaseKey, true);
			isIcuUserContentDefined = isIcuHeaderDefined && isSwitchCaseDefined;
		}
		assertTrue(isMainContentDefined && isIcuUserContentDefined);
		// build project
		assertTrue(BuildUtility.buildProject(model));
		
	}
	
	private SWTBotTreeItem getNodeChild(SWTBotTree botTree, String... path) {
		try {
            SWTBotTreeItem treeItem = botTree.expandNode(path);
            return treeItem;
        } catch (WidgetNotFoundException e) {
            // Handle the case where the tree node is not found
        	 LogUtil.logException(e);
            return null;
        } catch (Exception e) {
            // Handle any other exceptions that might occur
            LogUtil.logException(e);
            return null;
        }
	}
}
