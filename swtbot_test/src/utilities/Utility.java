package utilities;

import static org.junit.Assert.assertFalse;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotEditor;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotView;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.osgi.framework.Bundle;

import model.AbstractNode;
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
			if (item.getText().contains(projectName)) {
				projectItem = item.getText();
			}
		}
		return bot.tree().getTreeItem(projectItem);
	}

	public static void projectExplorerSelectProject(ProjectModel model) {
		// open project explorer
		openProjectExplorer();
		bot.sleep(5000);
		// select project
		SWTBotTreeItem[] allItems = bot.tree().getAllItems();
		String projectName = model.getProjectName();
		for (SWTBotTreeItem item : allItems) {
			String itemName = item.getText();
			if (itemName.contains(projectName)) {
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
	
	public static void openSCFGEditor(ProjectModel projectModel) {
		Utility.getProjectExplorerView().setFocus();
		bot.tree().getTreeItem(projectModel.getProjectName() + " ["+ projectModel.getActiveBuildConfiguration() +"]").expand();
		bot.tree().getTreeItem(projectModel.getProjectName() + " ["+ projectModel.getActiveBuildConfiguration() +"]").getNode(projectModel.getProjectName()+".scfg").doubleClick();
		SWTBotEditor scfgEditor = bot.editorByTitle(projectModel.getProjectName()+".scfg");
		scfgEditor.setFocus();
		bot.cTabItem(ProjectParameters.SCFG_COMPONENT_TAB).activate();
	}
	
	public static void addComponent(String componentName) {
		bot.toolbarButtonWithTooltip(ProjectParameters.ButtonAction.BUTTON_ADD_COMPONENT).click();
		bot.shell(ProjectParameters.WINDOW_NEW_COMPONENT).setFocus();
		bot.textWithLabel(ProjectParameters.LabelName.LABEL_FILTER).setText(componentName);
		bot.table().select(0);
		bot.button(ProjectParameters.ButtonAction.BUTTON_FINISH).click();
	}
	public static void clickGenerateCode() {
		bot.toolbarButton(ProjectParameters.ButtonAction.BUTTON_GENERATE_CODE).click();
		if (bot.activeShell().getText().contains(ProjectParameters.CODE_GENERATING)) {
			bot.button(ProjectParameters.ButtonAction.BUTTON_PROCEED).click();
		}
		if (bot.activeShell().getText().contains(ProjectParameters.CODE_GENERATING)) {
			bot.button(ProjectParameters.ButtonAction.BUTTON_PROCEED).click();
		}
		bot.sleep(15000);
	}
	public static void removeComponent(String componentName) {
		bot.tree(1).getTreeItem(ProjectParameters.FolderAndFile.FOLDER_RTOS)
				.getNode(ProjectParameters.FolderAndFile.FOLDER_RTOS_LIBRARY)
				.getNode(componentName).select();
		bot.toolbarButtonWithTooltip(ProjectParameters.ButtonAction.BUTTON_REMOVE_COMPONENT).click();
		if (bot.activeShell().getText().contains(ProjectParameters.WINDOW_QUESTION)) {
			bot.button(ProjectParameters.ButtonAction.BUTTON_YES).click();
		}
	}
	
	public static void openProjectExplorer() {
		bot.menu(ProjectParameters.MenuName.MENU_WINDOW).menu(ProjectParameters.MenuName.MENU_SHOW_VIEW)
				.menu(ProjectParameters.MenuName.MENU_OTHER).click();
		bot.text().setText(ProjectParameters.WINDOW_PROJECT_EXPLORER);
		SWTBotTreeItem treeItem = bot.tree().getTreeItem(ProjectParameters.GENERAL);
		bot.waitUntil(org.eclipse.swtbot.swt.finder.waits.Conditions.treeItemHasNode(treeItem,
				ProjectParameters.WINDOW_PROJECT_EXPLORER));
		treeItem.getNode(ProjectParameters.WINDOW_PROJECT_EXPLORER).select();
		bot.button(ProjectParameters.ButtonAction.BUTTON_OPEN).click();
	}
	
	public static boolean checkIfComponentExistOrNot (String componentName) {
		boolean isComponentInComponentTree = false;
		bot.text().setText("");
		bot.tree(1).getTreeItem(ProjectParameters.FolderAndFile.FOLDER_RTOS).expand();
		List<String> RTOSFolder = bot.tree(1).getTreeItem(ProjectParameters.FolderAndFile.FOLDER_RTOS).getNodes();
		for(String folder:RTOSFolder) {
			if (folder.contains(ProjectParameters.FolderAndFile.FOLDER_RTOS_LIBRARY)) {
				SWTBotTreeItem[] componentItems = bot.tree(1).getTreeItem(ProjectParameters.FolderAndFile.FOLDER_RTOS).getNode(ProjectParameters.FolderAndFile.FOLDER_RTOS_LIBRARY).getItems();
				for (SWTBotTreeItem currentItem: componentItems) {
					if (currentItem.getText().contains(componentName)) {
						isComponentInComponentTree = true;
					}
				}
			}
		}
		return isComponentInComponentTree;
	}

	public static <T> Collection<T> filterXMLData(Collection<T> model, String selectedToolchain, String selectedBoard) {
		Collection<T> filtered = new ArrayList<>();

		for (T node : model) {
			if (node instanceof AbstractNode) {
				AbstractNode nodeModel = (AbstractNode) node;
				if (isSupport(nodeModel.getToolchains(), nodeModel.getBoards(), selectedToolchain, selectedBoard)) {
					filtered.add(node);
				}
			}
		}
		return filtered;
	}

	private static boolean isSupport(Collection<String> toolchains, Collection<String> boards, String selectedToolchain, String selectedBoard) {
		boolean toolchainCondition = (toolchains.isEmpty() || toolchains.contains(selectedToolchain));
		boolean boardCondition = (boards.isEmpty() || boards.contains(selectedBoard));
		return toolchainCondition && boardCondition;
	}
}
