package utilities;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.eclipse.finder.waits.Conditions;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotEditor;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotView;
import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
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

	public static SWTBotTreeItem getProjectTreeItem(ProjectModel projectModel) {
		SWTBotView projectExplorerBot = bot.viewByTitle("Project Explorer");
		projectExplorerBot.show();
		projectExplorerBot.bot().waitUntil(Conditions.widgetIsEnabled(projectExplorerBot.bot().tree()));
		String buildType = projectModel.getActiveBuildConfiguration();
		bot.tree().getTreeItem(projectModel.getProjectName() + " [" + buildType + "]").select();
		return bot.tree().getTreeItem(projectModel.getProjectName() + " [" + buildType + "]");
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

	public static void changeRTOSLocation() {
		bot.menu("Window").menu("Preferences").click();
		bot.waitUntil(Conditions.shellIsActive("Preferences"));
		SWTBotShell preferenceShell = bot.shell("Preferences");
		SWTBot preferBot = preferenceShell.bot();
		preferBot.text().setText("Module Download");
		preferBot.tree().getTreeItem("Renesas").select();
		preferBot.tree().getTreeItem("Renesas").expand();
		preferBot.tree().getTreeItem("Renesas").getNode("Module Download").select();

		// reset to default
		preferBot.button("Restore &Defaults").click();
		preferBot.sleep(1000);
		preferBot.button("Apply and Close").click();
		preferBot.sleep(5000);
		bot.waitUntil(Conditions.shellCloses(preferenceShell));
	}

	/**
	 * change board API with use-cases
	 * 
	 * case 1: change current board/device --> new board --> put new board to parameter 2, otherwise, put empty string
	 * 
	 * case 2: change current board/device --> new device --> put new device to parameter 3, otherwise, put empty string
	 * 
	 * case 3: change current board from linear mode --> dual mode --> put boolean true to parameter 4, otherwise, put false
	 * 
	 * case 4, change current board from dual model --> linear model --> put boolean true to parameter 5, otherwise, put false
	 * 
	 * @param model
	 * @param board
	 * @param device
	 * @param isLinearDualConverted
	 * @param isDualLinearConverted
	 */
	public static void changeBoard(ProjectModel model, String board, String device, boolean isLinearDualConverted, boolean isDualLinearConverted) {
		openSCFGEditor(model);
		SWTBotEditor scfgEditor = bot.editorByTitle(model.getProjectName() + ".scfg");
		SWTBot editorBot = scfgEditor.bot();
		editorBot.cTabItem("Board").activate();
		editorBot.button("...").click();

		// handle change board dialog
		editorBot.waitUntil(Conditions.shellIsActive("Refactoring"));
		SWTBotShell refactorShell = editorBot.shell("Refactoring");
		SWTBot refactorBot = refactorShell.bot();
		// handle Linear -- Dual device
		if (isLinearDualConverted) {
			String dualDevice = refactorBot.styledText().getText() + "_DUAL";
			refactorBot.styledText().setText(dualDevice);
		} else if (isDualLinearConverted) {
			String linearDevice = refactorBot.styledText().getText().replace("_DUAL", "");
			refactorBot.styledText().setText(linearDevice);
		}
		// handle not Linear -- Dual device
		if (!board.isEmpty()) {
			refactorBot.comboBox().setText(board);
		} else if (!device.isEmpty()) {
			refactorBot.styledText().setText(device);
		}

		refactorBot.styledText().setText(board);
		refactorBot.button(ButtonAction.BUTTON_NEXT).click();
		refactorBot.waitUntil(Conditions.widgetIsEnabled(refactorBot.button(ButtonAction.BUTTON_NEXT)));
		refactorBot.button(ButtonAction.BUTTON_NEXT).click();
		refactorBot.waitUntil(Conditions.widgetIsEnabled(refactorBot.button(ButtonAction.BUTTON_FINISH)));
		refactorBot.button(ButtonAction.BUTTON_FINISH).click();
		if (isLinearDualConverted) {
			// handle confirm dialog
			refactorBot.waitUntil(Conditions.shellIsActive("Question"));
			SWTBotShell confirmShell = refactorBot.shell("Question");
			SWTBot confirmBot = confirmShell.bot();
			confirmBot.button(ButtonAction.BUTTON_YES).click();
			refactorBot.waitUntil(Conditions.shellCloses(refactorShell));
		}
	}

	public static void updateGCCLinkerScriptFile(ProjectModel model) {
		bot.sleep(2000);
		SWTBotTreeItem projectItem = getProjectTreeItem(model);
		projectItem.select();
		projectItem.expand();
		projectItem.getNode("src").select();
		projectItem.getNode("src").expand();
		SWTBotTreeItem linkerItem = projectItem.getNode("src").getNode("linker_script.ld").select();
		linkerItem.contextMenu().menu("Rename...").click();

		// handle rename resource dialog
		bot.waitUntil(Conditions.shellIsActive("Rename Resource"));
		SWTBotShell renameShell = bot.shell("Rename Resource");
		SWTBot renameBot = renameShell.bot();
		renameBot.text().setText("linker_script_1.ld");
		renameBot.waitUntil(Conditions.widgetIsEnabled(renameBot.button(ButtonAction.BUTTON_OK)));
		renameBot.button(ButtonAction.BUTTON_OK).click();
		renameBot.waitUntil(Conditions.shellCloses(renameShell));

		// handle rename for linker_script_sample.ld
		projectItem.select();
		SWTBotTreeItem linkerSampleItem = projectItem.getNode("src").getNode("linker_script_sample.ld").select();
		linkerSampleItem.contextMenu().menu("Rename...").click();

		bot.waitUntil(Conditions.shellIsActive("Rename Resource"));
		SWTBotShell reShell = bot.shell("Rename Resource");
		SWTBot reBot = reShell.bot();
		reBot.text().setText("linker_script.ld");
		reBot.waitUntil(Conditions.widgetIsEnabled(reBot.button(ButtonAction.BUTTON_OK)));
		reBot.button(ButtonAction.BUTTON_OK).click();
		reBot.waitUntil(Conditions.shellCloses(reShell));
	}

	public static void updateRXCLinkerSection(ProjectModel model, String rxcLinkerFile) {
		SWTBotTreeItem projectItem = Utility.getProjectTreeItem(model);
		projectItem.select();

		// open project setting dialog
		projectItem.contextMenu("C/C++ Project Settings").click();
		String settingDiaTitle = "Properties for " + model.getProjectName();
		bot.waitUntil(Conditions.shellIsActive(settingDiaTitle));
		SWTBotShell dialog = bot.shell(settingDiaTitle);
		dialog.setFocus();
		SWTBot dialogBot = dialog.bot();
		dialogBot.cTabItem("Tool Settings").activate();

		// check Linker/Section/Symbol file option
		dialogBot.treeWithLabel("Settings").getTreeItem("Linker").getNode("Section").getNode("Symbol file").click();
		dialogBot.toolbarButtonWithTooltip("Add...", 2).click();
		// TODO: handle dialog
		dialogBot.waitUntil(Conditions.shellIsActive("Enter Value"));
		SWTBotShell mapDialog = dialogBot.shell("Enter Value");
		SWTBot mapBot = mapDialog.bot();
		mapBot.text().setText("PFRAM2=RPFRAM2");
		mapBot.button(ButtonAction.BUTTON_OK).click();
		dialogBot.waitUntil(Conditions.shellCloses(mapDialog));
		dialogBot.toolbarButtonWithTooltip("Move Down", 2).click();
		dialogBot.toolbarButtonWithTooltip("Move Down", 2).click();

		// handle Rebuild index dialog
		dialogBot.button("Apply").click();
		dialogBot.waitUntil(Conditions.shellIsActive("Settings"));
		SWTBotShell rebuildDialog = dialogBot.shell("Settings");
		SWTBot rebuildBot = rebuildDialog.bot();
		rebuildBot.button("Rebuild Index").click();
		dialogBot.waitUntil(Conditions.shellCloses(rebuildDialog));

		// handle Section viewer dialog
		dialogBot.treeWithLabel("Settings").getTreeItem("Linker").getNode("Section").click();
		dialogBot.button("...").click();
		dialogBot.sleep(2000);// wait for dialog section edition
		SWTBotShell sectionShell = dialogBot.activeShell();
		sectionShell.setFocus();
		SWTBot sectionBot = sectionShell.bot();

		// checkbox
		Path linkerPath = new Path(rxcLinkerFile);
		sectionBot.checkBox("Override Linker Script").click();
		sectionBot.text().setText(linkerPath.toOSString());
		sectionBot.button("Re-Apply").click();
		// Re-Apply dialog
		sectionBot.waitUntil(Conditions.shellIsActive("Re-Apply"));
		SWTBotShell reApplyShell = sectionBot.shell("Re-Apply");
		SWTBot reApplyBot = reApplyShell.bot();
		reApplyBot.button("OK").click();
		sectionBot.waitUntil(Conditions.shellCloses(reApplyShell));

		sectionBot.button("OK").click();
		dialogBot.sleep(2000);

		// handle Rebuild index dialog
		dialogBot.button("Apply and Close").click();
		dialogBot.waitUntil(Conditions.shellIsActive("Settings"));
		SWTBotShell reDialog = dialogBot.shell("Settings");
		SWTBot reBot = reDialog.bot();
		reBot.button("Rebuild Index").click();
		dialogBot.waitUntil(Conditions.shellCloses(reDialog));
	}
}
