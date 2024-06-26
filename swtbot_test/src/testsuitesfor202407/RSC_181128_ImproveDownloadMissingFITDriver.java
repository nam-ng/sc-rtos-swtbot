package testsuitesfor202407;

import static org.junit.Assert.assertTrue;

import java.awt.Robot;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotEditor;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotView;
import org.eclipse.swtbot.swt.finder.utils.SWTBotPreferences;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotButton;
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
import parameters.ProjectParameters.SCApplication;
import parameters.ProjectParameters.TargetBoard;
import platform.PlatformModel;
import utilities.PGUtility;
import utilities.Utility;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class RSC_181128_ImproveDownloadMissingFITDriver {
	private static final String PLATFORM_XML_FILE = "xml/platformdata.xml";
	private static final String RTOS_PG_XML_FILE = "xml/rtospg.xml";
	private static final String EMPTY_FIT_LOCATION = "D:\\e2_package\\none";
	private static SWTWorkbenchBot bot;
	private static Robot robot;

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
	public void tc_00_ChangeRTOSLocation() throws Exception {
		Utility.changeModuleDownloadLocation(robot, "D:\\rtos_package\\IoT_LTS\\2024-07\\new", 3);
		Utility.changeModuleDownloadLocation(robot, ProjectParameters.FileLocation.NEWEST_FIT_MODULES_LOCATION, 2);
		setRegionSetting();
		Utility.reFocus(robot);
	}

	private void setRegionSetting() {
		Utility.reFocus(robot);
		bot.menu(MenuName.MENU_WINDOW).menu(MenuName.MENU_PREFERENCES).click();
		bot.tree().getTreeItem("Renesas").expand();
		bot.tree().getTreeItem("Renesas").getNode("Smart Browser").doubleClick();
		bot.shell("Preferences").activate();
		// maximize dialog
		Display.getDefault().syncExec(new Runnable() {

			@Override
			public void run() {
				bot.getDisplay().getActiveShell().setMaximized(true);
			}
		});
		bot.comboBoxInGroup("Region setting").setSelection("Singapore/South &Southeast Asia/Oceania");
		bot.button(ButtonAction.APPLY_AND_CLOSE).click();
	}

	@Test
	public void tc_01_VerifyDownloadMissingFitOnAzureRTOS() throws Exception {
		ProjectModel model = PGUtility.prepareProjectModel(Constants.GCC_TOOLCHAIN,
				ProjectParameters.LanguageType.C_LANGUAGE, RTOSType.AZURE, RTOSVersion.Azure_6_4_0,
				TargetBoard.BOARD_CK_RX65N, RTOSApplication.AZURE_MINIMAL);
		String projectName = model.getProjectName();
		PGUtility.createProject(Constants.GCC_TOOLCHAIN, ProjectParameters.LanguageType.C_LANGUAGE, RTOSType.AZURE,
				RTOSVersion.Azure_6_4_0, TargetBoard.BOARD_CK_RX65N, RTOSApplication.AZURE_MINIMAL);
		// change FIT modules location to empty
		Utility.changeModuleDownloadLocation(robot, EMPTY_FIT_LOCATION, 2);
		Utility.reFocus(robot);
		// open SCFG editor and check components
		Utility.openSCFGEditorByProjectName(projectName);
		// close RX Driver Package dialog
		SWTBotShell rdpShell = Arrays.asList(bot.shells()).stream()
				.filter(s -> s.getText().equals("RX Driver Package Download")).findFirst().orElse(null);
		closeDialog(rdpShell, ButtonAction.BUTTON_NO);
		// close download missing dialog
		SWTBotShell fitMissingShell = Arrays.asList(bot.shells()).stream()
				.filter(s -> s.getText().equals("Missing FIT Module Download")).findFirst().orElse(null);
		closeDialog(fitMissingShell, ButtonAction.BUTTON_NO);
		// switch to Components tab
		SWTBotEditor scfgEditor = bot.editorByTitle(projectName + ".scfg");
		scfgEditor.setFocus();
		scfgEditor.bot().cTabItem("Components").activate();
		// collect missing FIT drivers
		SWTBotTreeItem[] componentTree = bot.tree(1).getAllItems();
		List<SWTBotTreeItem> roots = Arrays.asList(componentTree).stream().filter(e -> !e.getText().contains("RTOS"))
				.collect(Collectors.toList());
		List<SWTBotTreeItem> componentNodes = roots.stream().map(r -> getComponentNode(r)).flatMap(List::stream)
				.collect(Collectors.toList());
		List<String> missingItems = componentNodes.stream()
				.filter(e -> !e.contextMenu("Reset to default").isEnabled()).map(SWTBotTreeItem::getText)
				.collect(Collectors.toList());
		// close SCFG editor
		Utility.closeEditorView(projectName + ".scfg");
		// open again
		Utility.openSCFGEditorByProjectName(projectName);
		// close RX Driver Package dialog
		closeDialog(Arrays.asList(bot.shells()).stream()
				.filter(s -> s.getText().equals("RX Driver Package Download")).findFirst().orElse(null), ButtonAction.BUTTON_NO);
		// proceed Download Missing dialog
		proceedDialog(Arrays.asList(bot.shells()).stream()
				.filter(s -> s.getText().equals("Missing FIT Module Download")).findFirst().orElse(null));
		// check progress info
		while (true) {
			if (Arrays.asList(bot.shells()).stream().anyMatch(s -> s.getText().equals("Download missing components"))) {
				break;
			}
		}
		// check component missing
		Display.getDefault().syncExec(new Runnable() {

			@Override
			public void run() {
				bot.getDisplay().getActiveShell().setMaximized(true);
				Utility.reFocus(robot);
			}
		});
		List<String> componentMissingList = Arrays.asList(bot.tree().getAllItems()).stream()
				.map(SWTBotTreeItem::getText).collect(Collectors.toList());
		assertTrue(componentMissingList.containsAll(missingItems));
		closeDialog(Arrays.asList(bot.shells()).stream().filter(s -> s.getText().equals("Download missing components"))
				.findFirst().orElse(null), ButtonAction.BUTTON_CANCEL);
		List<SWTBotShell> perspectives = Arrays.asList(bot.shells()).stream()
				.filter(s -> s.getText().equals(ProjectParameters.WINDOW_OPEN_ASSOCIATED_PERSPECTIVE)).collect(Collectors.toList());
		perspectives.stream().forEach(s -> closeDialog(s, ButtonAction.BUTTON_NO));
		// close SCFG editor
		Utility.closeEditorView(projectName + ".scfg");
	}

	@Test
	public void tc_02_VerifyDownloadMissingFitOnSCProject() throws Exception {
		Utility.changeModuleDownloadLocation(robot, ProjectParameters.FileLocation.NEWEST_FIT_MODULES_LOCATION, 2);
		Utility.reFocus(robot);
		bot.sleep(2000);
		ProjectModel model = PGUtility.prepareProjectModel(Constants.GCC_TOOLCHAIN,
				ProjectParameters.LanguageType.C_LANGUAGE, RTOSType.NONE, "", TargetBoard.BOARD_CK_RX65N,
				SCApplication.SC_BLINKY);
		String projectName = model.getProjectName();
		PGUtility.createProject(Constants.GCC_TOOLCHAIN, ProjectParameters.LanguageType.C_LANGUAGE, RTOSType.NONE, "",
				TargetBoard.BOARD_CK_RX65N, SCApplication.SC_BLINKY);
		// open SCFG editor and check components
		Utility.openSCFGEditorByProjectName(projectName, "Components");
		// add component r_sci_rx
		Utility.addComponent("r_sci_rx");
		Utility.clickGenerateCode();
		Utility.closeEditorView(projectName + ".scfg");
		// change FIT modules location to empty
		Utility.changeModuleDownloadLocation(robot, EMPTY_FIT_LOCATION, 2);
		Utility.reFocus(robot);
		// open SCFG editor and check components
		Utility.openSCFGEditorByProjectName(projectName);
		// close RX Driver Package dialog
		SWTBotShell rdpShell = Arrays.asList(bot.shells()).stream()
				.filter(s -> s.getText().equals("RX Driver Package Download")).findFirst().orElse(null);
		closeDialog(rdpShell, ButtonAction.BUTTON_NO);
		// close download missing dialog
		SWTBotShell fitMissingShell = Arrays.asList(bot.shells()).stream()
				.filter(s -> s.getText().equals("Missing FIT Module Download")).findFirst().orElse(null);
		closeDialog(fitMissingShell, ButtonAction.BUTTON_NO);
		// switch to Components tab
		SWTBotEditor scfgEditor = bot.editorByTitle(projectName + ".scfg");
		scfgEditor.setFocus();
		scfgEditor.bot().cTabItem("Components").activate();
		// collect missing FIT drivers
		SWTBotTreeItem[] componentTree = bot.tree(1).getAllItems();
		List<SWTBotTreeItem> roots = Arrays.asList(componentTree).stream().filter(e -> !e.getText().contains("RTOS"))
				.collect(Collectors.toList());
		List<SWTBotTreeItem> componentNodes = roots.stream().map(r -> getComponentNode(r)).flatMap(List::stream)
				.collect(Collectors.toList());
		List<String> missingItems = componentNodes.stream()
				.filter(e -> !e.contextMenu("Reset to default").isEnabled()).map(SWTBotTreeItem::getText)
				.collect(Collectors.toList());
		// close SCFG editor
		Utility.closeEditorView(projectName + ".scfg");
		// open again
		Utility.openSCFGEditorByProjectName(projectName);
		// close RX Driver Package dialog
		closeDialog(Arrays.asList(bot.shells()).stream()
				.filter(s -> s.getText().equals("RX Driver Package Download")).findFirst().orElse(null), ButtonAction.BUTTON_NO);
		// proceed Download Missing dialog
		proceedDialog(Arrays.asList(bot.shells()).stream()
				.filter(s -> s.getText().equals("Missing FIT Module Download")).findFirst().orElse(null));
		// check progress info
		while (true) {
			if (Arrays.asList(bot.shells()).stream().anyMatch(s -> s.getText().equals("Download missing components"))) {
				break;
			}
		}
		// check component missing
		Display.getDefault().syncExec(new Runnable() {

			@Override
			public void run() {
				bot.getDisplay().getActiveShell().setMaximized(true);
				Utility.reFocus(robot);
			}
		});
		List<String> componentMissingList = Arrays.asList(bot.tree().getAllItems()).stream()
				.map(SWTBotTreeItem::getText).collect(Collectors.toList());
		assertTrue(componentMissingList.containsAll(missingItems));
		closeDialog(Arrays.asList(bot.shells()).stream().filter(s -> s.getText().equals("Download missing components"))
				.findFirst().orElse(null), ButtonAction.BUTTON_CANCEL);
		List<SWTBotShell> perspectives = Arrays.asList(bot.shells()).stream()
				.filter(s -> s.getText().equals(ProjectParameters.WINDOW_OPEN_ASSOCIATED_PERSPECTIVE)).collect(Collectors.toList());
		perspectives.stream().forEach(s -> closeDialog(s, ButtonAction.BUTTON_NO));
		// close SCFG editor
		Utility.closeEditorView(projectName + ".scfg");

	}

	@Test
	public void tc_03_VerifyNoPreferenceOfDownloadMissingFit() throws Exception {
		ProjectModel model = PGUtility.prepareProjectModel(Constants.GCC_TOOLCHAIN,
				ProjectParameters.LanguageType.C_LANGUAGE, RTOSType.AZURE, RTOSVersion.Azure_6_4_0,
				TargetBoard.BOARD_CK_RX65N, RTOSApplication.AZURE_MINIMAL);
		String projectName = model.getProjectName();
		// check preference
		Utility.openSCFGEditorByProjectName(projectName);
		// close RDP dialog
		closeDialog(Arrays.asList(bot.shells()).stream().filter(s -> s.getText().equals("RX Driver Package Download"))
				.findFirst().orElse(null), ButtonAction.BUTTON_NO);
		// check preference
		SWTBotShell downloadMissingShell = Arrays.asList(bot.shells()).stream().filter(s -> s.getText().equals("Missing FIT Module Download"))
				.findFirst().orElse(null);
		Optional.ofNullable(downloadMissingShell).ifPresent(SWTBotShell::activate);
		Optional.ofNullable(downloadMissingShell).map(SWTBotShell::bot).map(b -> b.checkBox("Remember my decision")).ifPresent(c -> c.click());
		closeDialog(downloadMissingShell, ButtonAction.BUTTON_NO);
		// close SCFG editor
		Utility.closeEditorView(projectName + ".scfg");
		// open SCFG editor again
		Utility.openSCFGEditorByProjectName(projectName);
		// close RDP dialog
		closeDialog(Arrays.asList(bot.shells()).stream().filter(s -> s.getText().equals("RX Driver Package Download"))
				.findFirst().orElse(null), ButtonAction.BUTTON_NO);
		// assert download missing dialog
		assertTrue(Arrays.asList(bot.shells()).stream().filter(s -> s.getText().equals("Missing FIT Module Download"))
				.findFirst().orElse(null) == null);
		List<SWTBotShell> perspectives = Arrays.asList(bot.shells()).stream()
				.filter(s -> s.getText().equals(ProjectParameters.WINDOW_OPEN_ASSOCIATED_PERSPECTIVE)).collect(Collectors.toList());
		perspectives.stream().forEach(s -> closeDialog(s, ButtonAction.BUTTON_NO));
		// close SCFG editor
		Utility.closeEditorView(projectName + ".scfg");
		
	}

	private void closeDialog(SWTBotShell shell, String buttonText) {
		Optional.ofNullable(shell).ifPresent(SWTBotShell::activate);
		SWTBotButton noClick = Optional.ofNullable(shell).map(SWTBotShell::bot)
				.flatMap(b -> Optional.ofNullable(b.button(buttonText))).orElse(null);
		Optional.ofNullable(noClick).ifPresent(SWTBotButton::click);
	}

	private void proceedDialog(SWTBotShell shell) {
		Optional.ofNullable(shell).ifPresent(SWTBotShell::activate);
		SWTBotButton yesClick = Optional.ofNullable(shell).map(SWTBotShell::bot)
				.flatMap(b -> Optional.ofNullable(b.button(ButtonAction.BUTTON_YES))).orElse(null);
		Optional.ofNullable(yesClick).ifPresent(SWTBotButton::click);
	}

	private List<SWTBotTreeItem> getComponentNode(SWTBotTreeItem root) {
		List<SWTBotTreeItem> result = new ArrayList<>();
		if (Arrays.asList(root.getItems()).isEmpty()) {
			result.add(root);
		} else {
			for (SWTBotTreeItem item : root.getItems()) {
				result.addAll(getComponentNode(item));
			}
		}
		return result;
	}
}
