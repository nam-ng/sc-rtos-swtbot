package swtbot_test;

import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotView;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;

import model.IApplication;

import java.util.Arrays;
import java.util.List;

public class TestUtils {
	public static SWTWorkbenchBot bot = new SWTWorkbenchBot();

	public static void waitForProcess(int sleepMili) {
		bot.sleep(sleepMili);
		if (bot.activeShell().getText().contains(ProjectParameters.WINDOW_INSTALL)) {
			bot.button(ProjectParameters.BUTTON_CANCEL).click();
		}
		if (bot.activeShell().getText().contains(ProjectParameters.WINDOW_INSTALL)) {
			bot.button(ProjectParameters.BUTTON_CANCEL).click();
		}
	}

	public static void deleteProject(String projectName, boolean deleteProjectContentOnDisk) {
		waitForProcess(5000);
		getProjectItemOnProjectExplorer(projectName).contextMenu(ProjectParameters.CONTEXT_MENU_DELETE).click();
		if (deleteProjectContentOnDisk) {
			bot.waitUntil(org.eclipse.swtbot.swt.finder.waits.Conditions
					.waitForShell(org.eclipse.swtbot.swt.finder.matchers.WidgetMatcherFactory
							.withText(ProjectParameters.WINDOW_DELETE_RESOURCES)),
					600000);
			bot.shell(ProjectParameters.WINDOW_DELETE_RESOURCES).bot()
					.checkBox(ProjectParameters.CHECKBOX_DELETE_PROJECT_CONTENTS_ON_DISK).click();
		}
		bot.shell(ProjectParameters.WINDOW_DELETE_RESOURCES).bot().button(ProjectParameters.BUTTON_OK).click();
		waitForProcess(5000);
	}

	public static SWTBotTreeItem getProjectItemOnProjectExplorer(String projectName) {
		getProjectExplorerView().setFocus();
		String projectItem = projectName;
		SWTBotTreeItem[] allItems = bot.tree().getAllItems();
		for (SWTBotTreeItem item : allItems) {
			if (item.getText().contentEquals(projectName)
					|| item.getText().contentEquals(projectName + " [" + "HardwareDebug" + "]")) {
				projectItem = item.getText();
			}
		}
		return bot.tree().getTreeItem(projectItem);
	}

	public static SWTBotView getProjectExplorerView() {
		return bot.viewByTitle(ProjectParameters.VIEW_NAME_PROJECT_EXPLORER);
	}

	public static void createProject(ProjectModel projectModel) {
		waitForProcess(5000);
		bot.menu(ProjectParameters.MENU_FILE).menu(ProjectParameters.MENU_NEW)
				.menu(ProjectParameters.MENU_C_CPP_PROJECT).menu(ProjectParameters.MENU_RENESAS_RX).click();
		if (projectModel.getToolchain().equals("GCC")) {
			bot.table().select(0);
		} else if (projectModel.getToolchain().equals("CCRX")) {
			bot.table().select(2);
		}
		bot.button(ProjectParameters.BUTTON_NEXT).click();
		bot.textWithLabel(ProjectParameters.LABEL_PROJECT_NAME).setText(projectModel.getProjectName());
		bot.button(ProjectParameters.BUTTON_NEXT).click();
		bot.comboBoxWithLabel(ProjectParameters.LABEL_RTOS).setSelection(projectModel.getRtosType());
		bot.comboBoxWithLabel(ProjectParameters.LABEL_RTOS_VERSION).setSelection(projectModel.getRtosVersion());
		bot.comboBoxWithLabel(ProjectParameters.LABEL_TARGET_BOARD).setSelection(projectModel.getTargetBoard());
		if (projectModel.getTargetBoard().equals("Custom")) {
			bot.styledText().setText(ProjectParameters.DeviceName.RX651);
		}
		bot.button(ProjectParameters.BUTTON_NEXT).click();
		bot.button(ProjectParameters.BUTTON_NEXT).click();
		bot.radio(projectModel.getApplication()).click();
		bot.button(ProjectParameters.BUTTON_FINISH).click();
		while (true) {
			if (bot.activeShell().getText().contains(ProjectParameters.WINDOW_INSTALL)) {
				bot.button(ProjectParameters.BUTTON_CANCEL).click();
			}
			if (bot.activeShell().getText().contains(ProjectParameters.WINDOW_OPEN_ASSOCIATED_PERSPECTIVE)) {
				bot.button(ProjectParameters.BUTTON_OPEN_PERSPECTIVE).click();
			}
			if (bot.activeShell().getText().contains(ProjectParameters.WINDOW_FIT)) {
				bot.button(ProjectParameters.BUTTON_YES).click();
				bot.comboBox(0).setSelection("Singapore/South &Southeast Asia/Oceania");
				bot.button(ProjectParameters.BUTTON_OK).click();
				bot.sleep(5000);
				bot.button(ProjectParameters.BUTTON_SELECT_ALL).click();
				bot.button(ProjectParameters.BUTTON_DOWNLOAD).click();
				bot.button(ProjectParameters.BUTTON_ACCEPT).click();
				bot.shell(ProjectParameters.WINDOW_OPEN_ASSOCIATED_PERSPECTIVE).activate();
			}
			if (bot.activeShell().getText().contains(ProjectParameters.CODE_GENERATING)) {
				bot.button(ProjectParameters.BUTTON_PROCEED).click();
			}
			if (bot.activeShell().getText().contains(ProjectParameters.WINDOW_MARKETPLACE)) {
				bot.button(ProjectParameters.BUTTON_CANCEL).click();
				break;
			}

		}

	}

	public static void buildProject(ProjectModel projectModel) {
		waitForProcess(5000);
		bot.menu(ProjectParameters.MENU_WINDOW).menu(ProjectParameters.MENU_SHOW_VIEW)
				.menu(ProjectParameters.MENU_OTHER).click();
		bot.text().setText(ProjectParameters.WINDOW_PROJECT_EXPLORER);
		SWTBotTreeItem treeItem = bot.tree().getTreeItem("General");
		bot.waitUntil(org.eclipse.swtbot.swt.finder.waits.Conditions.treeItemHasNode(treeItem,
				ProjectParameters.WINDOW_PROJECT_EXPLORER));
		treeItem.getNode(ProjectParameters.WINDOW_PROJECT_EXPLORER).select();
		bot.button(ProjectParameters.BUTTON_OPEN).click();
		waitForProcess(5000);
		bot.tree().getTreeItem(projectModel.getProjectName()).select();
		waitForProcess(5000);
		bot.tree().getTreeItem(projectModel.getProjectName() + " [HardwareDebug]")
				.contextMenu(ProjectParameters.MENU_BUILD_PROJECT).click();
		SWTBotView consoleView = bot.viewById("org.eclipse.ui.console.ConsoleView");
		boolean isSuccessful = false;
		while (true) {
			if (bot.activeShell().getText().contains(ProjectParameters.WINDOW_INSTALL)) {
				bot.button(ProjectParameters.BUTTON_CANCEL).click();
			}
			if (consoleView.bot().styledText().getText().contains(ProjectParameters.BUILD_SUCCESSFULLY)) {
				isSuccessful = true;
				break;
			} else if (consoleView.bot().styledText().getText().contains(ProjectParameters.BUILD_FAILED)) {
				break;
			}
		}
		if (isSuccessful) {
			deleteProject(projectModel.getProjectName(), true);
		}
	}

	public static void createAndBuildSpecificProjectAzure(ProjectModel projectModel) {
		createProject(projectModel);
		buildProject(projectModel);
	}

	public static void gccExecuted(ProjectModel projectModel, IApplication application) {
		// TODO Auto-generated method stub
		for (int i = 0; i < application.getBoard().size(); i++) {
			if (application.getGccExecuted().get(i) == 1) {
				projectModel.setRtosType(ProjectParameters.RTOSType.AZURE);
				projectModel.setRtosVersion(ProjectParameters.RTOSVersion.NEWEST);
				projectModel.setTargetBoard(application.getBoard().get(i));
				projectModel.setApplication(application.getApplicationNumber());
				projectModel.setToolchain("GCC");
				projectModel.setProjectName(application.getApplication() + "GCC" + i);
				TestUtils.createProject(projectModel);
				TestUtils.buildProject(projectModel);
			}
		}
	}

	public static void ccrxExecuted(ProjectModel projectModel, IApplication application) {
		// TODO Auto-generated method stub
		for (int j = 0; j < application.getBoard().size(); j++) {
			if (application.getCcrxExecuted().get(j) == 1) {
				projectModel.setRtosType(ProjectParameters.RTOSType.AZURE);
				projectModel.setRtosVersion(ProjectParameters.RTOSVersion.NEWEST);
				projectModel.setTargetBoard(application.getBoard().get(j));
				projectModel.setApplication(application.getApplicationNumber());
				projectModel.setToolchain("CCRX");
				projectModel.setProjectName(application.getApplication() + "CCRX" + j);
				TestUtils.createProject(projectModel);
				TestUtils.buildProject(projectModel);
			}
		}
	}

}