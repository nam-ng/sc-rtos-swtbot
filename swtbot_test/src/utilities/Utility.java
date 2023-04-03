package utilities;

import java.io.IOException;
import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotEditor;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotView;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.osgi.framework.Bundle;

import model.ProjectModel;
import parameters.ProjectParameters;
import parameters.ProjectParameters.ButtonAction;
import parameters.ProjectParameters.MenuName;
import parameters.ProjectParameters.RTOSDisplay;
import parameters.ProjectParameters.RTOSType;

public class Utility {
	protected static SWTWorkbenchBot bot = new SWTWorkbenchBot();

	public static void waitForProcess(int sleepMili) {
		bot.sleep(sleepMili);
		if (bot.activeShell().getText().contains(ProjectParameters.WINDOW_INSTALL)) {
			bot.button(ButtonAction.BUTTON_CANCEL).click();
		}
		if (bot.activeShell().getText().contains(ProjectParameters.WINDOW_INSTALL)) {
			bot.button(ButtonAction.BUTTON_CANCEL).click();
		}
	}

	public static void deleteProject(String projectName, boolean deleteProjectContentOnDisk) {
		waitForProcess(5000);
		getProjectItemOnProjectExplorer(projectName).contextMenu(MenuName.CONTEXT_MENU_DELETE).click();
		if (deleteProjectContentOnDisk) {
			bot.waitUntil(org.eclipse.swtbot.swt.finder.waits.Conditions
					.waitForShell(org.eclipse.swtbot.swt.finder.matchers.WidgetMatcherFactory
							.withText(ProjectParameters.WINDOW_DELETE_RESOURCES)),
					600000);
			bot.shell(ProjectParameters.WINDOW_DELETE_RESOURCES).bot()
					.checkBox(ProjectParameters.CHECKBOX_DELETE_PROJECT_CONTENTS_ON_DISK).click();
		}
		bot.shell(ProjectParameters.WINDOW_DELETE_RESOURCES).bot().button(ButtonAction.BUTTON_OK).click();
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

	public static void projectExplorerSelectProject(ProjectModel model) {
		// open project explorer
		bot.menu(MenuName.MENU_WINDOW).menu(MenuName.MENU_SHOW_VIEW).menu(MenuName.MENU_OTHER).click();
		bot.text().setText(ProjectParameters.WINDOW_PROJECT_EXPLORER);
		SWTBotTreeItem treeItem = bot.tree().getTreeItem("General");
		bot.waitUntil(org.eclipse.swtbot.swt.finder.waits.Conditions.treeItemHasNode(treeItem,
				ProjectParameters.WINDOW_PROJECT_EXPLORER));
		treeItem.getNode(ProjectParameters.WINDOW_PROJECT_EXPLORER).select();
		bot.button(ButtonAction.BUTTON_OPEN).click();
		bot.sleep(5000);
		// select project
		SWTBotTreeItem[] allItems = bot.tree().getAllItems();
		String buildType = model.getActiveBuildConfiguration();
		String projectName = model.getProjectName();
		for (SWTBotTreeItem item : allItems) {
			String itemName = item.getText();
			if (itemName.contains(projectName + " [" + buildType + "]")) {
				projectName = itemName;
				break;
			}
		}
		bot.tree().getTreeItem(projectName).select();
	}

	public static SWTBotView getProjectExplorerView() {
		return bot.viewByTitle(ProjectParameters.VIEW_NAME_PROJECT_EXPLORER);
	}

	public static String getBundlePath(String pluginID, String subpath) {

		String fullPath = null;
		Bundle bundle = Platform.getBundle(pluginID);

		try {
			if (bundle != null) {
				URL url = bundle.getEntry(subpath);
				if (url != null) {
					URL locatedURL = null;
					locatedURL = FileLocator.toFileURL(url);
					fullPath = new Path(locatedURL.getPath()).toOSString();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return fullPath;
	}

	public static void closeEditorView(String title) {
		SWTBotEditor editorView = bot.editorByTitle(title);
				editorView.close();
	}

	public static String convertRTOSTypeToDisplay(String rtosType) {
		if (rtosType.equalsIgnoreCase(RTOSType.AZURE)) {
			return RTOSDisplay.AZURE;
		} else if (rtosType.equalsIgnoreCase(RTOSType.AMAZONFREERTOS)) {
			return RTOSDisplay.AMAZONFREERTOS;
		} else if (rtosType.equalsIgnoreCase(RTOSType.FREERTOSKERNEL)) {
			return RTOSDisplay.FREERTOSKERNEL;
		}
		return "";
	}
}
