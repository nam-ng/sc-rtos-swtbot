package utilities;

import static org.junit.Assert.assertFalse;

import java.awt.Dimension;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.bindings.keys.ParseException;
import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.eclipse.finder.waits.Conditions;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotEditor;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotView;
import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.matchers.WidgetMatcherFactory;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotCanvas;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.junit.Test;
import org.osgi.framework.Bundle;

import model.AbstractNode;
import model.Action;
import model.IncludeDirectory;
import model.LinkerSections;
import model.Project;
import model.ProjectModel;
import model.ProjectSettings;
import model.TC;
import parameters.ProjectParameters;
import parameters.ProjectParameters.ButtonAction;
import parameters.ProjectParameters.LabelName;
import parameters.ProjectParameters.MenuName;
import parameters.ProjectParameters.RTOSDisplay;
import parameters.ProjectParameters.RTOSType;
import parameters.ProjectParameters.RTOSVersion;
import parameters.ProjectParameters.TargetBoard;

public class Utility {
	protected static SWTWorkbenchBot bot = new SWTWorkbenchBot();
	protected static Collection<ProjectModel> allProjectModels = new ArrayList<>();

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
		} else if (rtosType.equalsIgnoreCase(RTOSType.RI600v4)) {
			return RTOSDisplay.RI600v4;
		} else if (rtosType.equalsIgnoreCase(RTOSType.FREERTOSIOTLTS)) {
			return RTOSDisplay.FREERTOSIOTLTS;
		}
		return "";
	}
	
	public static void openSCFGEditor(ProjectModel projectModel, String tabOpen) {
		Utility.getProjectExplorerView().setFocus();
		bot.tree().getTreeItem(projectModel.getProjectName() + " ["+ projectModel.getActiveBuildConfiguration() +"]").expand();
		bot.tree().getTreeItem(projectModel.getProjectName() + " ["+ projectModel.getActiveBuildConfiguration() +"]").getNode(projectModel.getProjectName()+".scfg").doubleClick();
		bot.sleep(3000);
		if (bot.activeShell().getText().equals(ProjectParameters.WINDOW_OPEN_ASSOCIATED_PERSPECTIVE)) {
			bot.button(ButtonAction.BUTTON_NO).click();
		}
		SWTBotEditor scfgEditor = bot.editorByTitle(projectModel.getProjectName()+".scfg");
		scfgEditor.setFocus();
		bot.cTabItem(tabOpen).activate();
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

	public static <T> Collection<T> filterXMLModelProjectSettings(Collection<T> model, String selectedToolchain, String selectedBoard, String selectedApplication) {
		Collection<T> filtered = new ArrayList<>();

		for (T node : model) {
			if (node instanceof ProjectSettings) {
				ProjectSettings nodeModel = (ProjectSettings) node;
				if (isSupport(nodeModel.getToolchains(), nodeModel.getBoards(), nodeModel.getApplications(), selectedToolchain, selectedBoard, selectedApplication)) {
					filtered.add(node);
				}
			}
		}
		return filtered;
	}

	private static boolean isSupport(Collection<String> toolchains, Collection<String> boards, Collection<String> application,  String selectedToolchain, String selectedBoard, String selectedApplication) {
		boolean toolchainCondition = (toolchains.isEmpty() || toolchains.contains(selectedToolchain));
		boolean boardCondition = (boards.isEmpty() || boards.contains(selectedBoard));
		boolean applicationCondition = (application.isEmpty() || application.contains(selectedApplication));
		return toolchainCondition && boardCondition && applicationCondition;
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

	public static void changeModuleDownloadLocation(Robot robot, String location, boolean isRTOSLocation) {
		reFocus(robot);
		copyLocationToClipBoard(location);
		
		bot.menu(MenuName.MENU_WINDOW).menu(MenuName.MENU_PREFERENCES).click();
		bot.tree().getTreeItem("Renesas").expand();
		bot.tree().getTreeItem("Renesas").getNode("Module Download").doubleClick();
		bot.shell("Preferences").activate();
		
		if(isRTOSLocation) {
			bot.button("Browse...", 1).click();
		} else {
			bot.button("Browse...", 0).click();

		}
		
		pressCtrlV(robot);
		
		pressEnter(robot);
		pressEnter(robot);
		
		bot.button(ButtonAction.APPLY_AND_CLOSE).click();
	}

	public static void pressEnter(Robot robot) {
		robot.keyPress(KeyEvent.VK_ENTER);
		robot.keyRelease(KeyEvent.VK_ENTER);		
	}

	public static void pressCtrlV(Robot robot) {
		robot.keyPress(KeyEvent.VK_CONTROL);
		robot.keyPress(KeyEvent.VK_V);
		robot.keyRelease(KeyEvent.VK_V);
		robot.keyRelease(KeyEvent.VK_CONTROL);		
	}

	public static void copyLocationToClipBoard(String location) {
		StringSelection selection = new StringSelection(location);
		Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		clipboard.setContents(selection, null);
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
		openSCFGEditor(model, ProjectParameters.SCFG_COMPONENT_TAB);
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

	public static String checkForIncludeDir(Collection<ProjectSettings> projectSettings) {
		StringBuilder stringBuilder2 = new StringBuilder("");
		for (ProjectModel model: allProjectModels) {
			Collection<ProjectSettings> filteredProjectSettings = filterXMLModelProjectSettings(projectSettings, model.getToolchain(), model.getBoard(), model.getApplication());
			Collection<IncludeDirectory> includeDirs= createIncDirList(filteredProjectSettings);
			SWTBotTreeItem projectItem=null;
			if(!isItemSelected(model.getProjectName())) {
				projectItem = bot.tree().getTreeItem(model.getProjectName()).select();
				projectItem.select();
			} else {
				projectItem = Utility.getProjectTreeItem(model);
				projectItem.select();
			}
			
			projectItem.contextMenu("C/C++ Project Settings").click();
			bot.cTabItem("Tool Settings").activate();
			
			bot.treeWithLabel("Settings").getTreeItem("Compiler").getNode("Source").click();
			String[] currentListOfIncDirs = bot.list(0).getItems();

			for (IncludeDirectory aIncDir: includeDirs) {
				for (String path: aIncDir.getPaths()) {
					boolean isFound = false;
					for (String currentPath: currentListOfIncDirs) {
						if (currentPath.contains(path)) {
							isFound = true;
						}
					}
					if(!isFound) {
						stringBuilder2.append("\nProject "+ model.getProjectName()+ " does not have path: "+ path + " in include directory");

					}
				}
			}
			
			bot.button("Cancel").click();
		}
		return stringBuilder2.toString();
	}
	//TCExecute.java will call this function
	public static String checkForLinkerSection(Collection<ProjectSettings> projectSettings) {
		StringBuilder stringBuilder2 = new StringBuilder("");
		for (ProjectModel model : allProjectModels) {
			if (!model.getToolchain().equals("CCRX")) {
				continue;
			}
			Collection<ProjectSettings> filteredProjectSettings = filterXMLModelProjectSettings(projectSettings, model.getToolchain(), model.getBoard(), model.getApplication());
			Collection<LinkerSections> linkerSections = createLinkerSectionList(filteredProjectSettings);
			SWTBotTreeItem projectItem=null;
			if(!isItemSelected(model.getProjectName())) {
				projectItem = bot.tree().getTreeItem(model.getProjectName()).select();
				projectItem.select();
			} else {
				projectItem = Utility.getProjectTreeItem(model);
				projectItem.select();
			}

			// open project setting dialog
			projectItem.contextMenu("C/C++ Project Settings").click();
			bot.cTabItem("Tool Settings").activate();

			// check Linker/Section/Symbol file option
			bot.treeWithLabel("Settings").getTreeItem("Linker").getNode("Section").click();
			bot.button("...").click();
			for (LinkerSections aSection: linkerSections) {
				boolean isSectionExist = checkForSectionExist(aSection);
				if (!isSectionExist) {
					StringBuilder stringBuilder = new StringBuilder("");
					for (String name: aSection.getName()) {
						stringBuilder.append(name);
						stringBuilder.append(" ");
					}
					stringBuilder2.append("\nProject "+ model.getProjectName()+ " does not satisfy sections: "+ stringBuilder + " in address "+ aSection.getAddress());
				}
			}
			
			bot.button("Cancel").click();
			bot.button("Cancel").click();
		}
		return stringBuilder2.toString();
	}
	
	
	
	private static boolean isItemSelected(String projectName) {
		SWTBotTreeItem[] allItems = bot.tree().getAllItems();
		boolean isItemSelected = true;
		for (SWTBotTreeItem treeItem : allItems) {
			if (projectName.equals(treeItem.getText())) {
				isItemSelected=false;
			}
		}
		// TODO Auto-generated method stub
		return isItemSelected;
	}

	private static boolean checkForSectionExist(LinkerSections aSection) {
		boolean isSatisfied = false;
		for (String name: aSection.getName()) {
			boolean startSearching = false;
			for (int i=0; i<bot.table().rowCount(); i++) {
				if(bot.table().getTableItem(i).getText(0).equals(aSection.getAddress())){
					startSearching = true;
				}
				if(startSearching) {
					if ((!bot.table().getTableItem(i).getText(0).equals(aSection.getAddress())&&(!bot.table().getTableItem(i).getText(0).equals("")))){
						isSatisfied =false;
						return isSatisfied;
					}
					if(name.equals(bot.table().getTableItem(i).getText(1))){
						isSatisfied = true;
						break;
					}
				}
			}
		}
		return isSatisfied;
	}

	private static Collection<LinkerSections> createLinkerSectionList(
			Collection<ProjectSettings> filteredProjectSettings) {
		Collection<LinkerSections> linkersections = new ArrayList<>();
		for (ProjectSettings projectsetting: filteredProjectSettings) {
			linkersections.addAll(projectsetting.getLinkerSections());
		}
		return linkersections;
	}
	
	private static Collection<IncludeDirectory> createIncDirList(Collection<ProjectSettings> filteredProjectSettings){
		Collection<IncludeDirectory> includeDir = new ArrayList<>();
		for (ProjectSettings projectsetting: filteredProjectSettings) {
			includeDir.add(projectsetting.getIncludeDirectory());
		}
		return includeDir;
	}

	public static void executeTCStep(TC tc, SWTBotShell shell) throws ParseException {
		// create project or import project or using current project
		Collection<ProjectModel> result = null;
		for (Project project : tc.getProjects()) {
			if (project.getProjectId().equalsIgnoreCase("pg")) {
				result = PGUtility.createProjectByTC(tc);
			} else if (project.getProjectId().equalsIgnoreCase("import")) {
				
			} else {
				// using current project
			}
		}
		// do action
		Collection<Action> rawActions = tc.getActions();
		Collections.sort((List<Action> ) rawActions, new Comparator<Action>() {

			@Override
			public int compare(Action o1, Action o2) {
				return o2.getActionOrder() - o1.getActionOrder();
			}
			
		});
		for (Action action : rawActions) {
			if (action.getActionId().equalsIgnoreCase("changeboard")) {
				if (result != null) {
					for (ProjectModel model : result) {
						Utility.changeBoard(model, null, null, true, false);
					}
				}
			}
		}
		allProjectModels.addAll(result);
	}
	
	public static void addOrRemoveKernelObject (boolean isAdd, int index) {
		SWTBotCanvas filterCanvas;
		if (isAdd) {
			filterCanvas = new SWTBotCanvas(bot.widget(WidgetMatcherFactory.withTooltip("Add new object"), index));
	        filterCanvas.click();
		} else {
			filterCanvas = new SWTBotCanvas(bot.widget(WidgetMatcherFactory.withTooltip("Remove object"), index));
	        filterCanvas.click();
		}
	}
	
	public static void clickClearConsole() {
		bot.toolbarButtonWithTooltip(ProjectParameters.ButtonAction.BUTTON_CLEAR_CONSOLE).click();
	}
	
	public static boolean isConsoleHasString(String text) {
		SWTBotView consoleView = bot.viewById("org.eclipse.ui.console.ConsoleView");
		return consoleView.bot().styledText().getText().contains(text);
			
	}
	
	public static void changeConfigOfCombobox(SWTBotTreeItem config, String configName, String configValue) {
		if (config.cell(0).contains(configName)) {
			bot.sleep(2000);
			config.click(1);
			bot.list(0).select(configValue);
		}
	}
	
	public static void changeConfigOfTextBox(SWTBotTreeItem config, String configName, String configValue, boolean isClearConsole) {
		if (config.cell(0).contains(configName)) {
			bot.sleep(2000);
			config.click(1);
			config.click(1);
			if (isClearConsole) {
				clickClearConsole();
			}
			bot.text(config.cell(1)).setText(configValue);
		}
	}
	
	public static void changeConfigOfTextBoxWithIndex(SWTBotTreeItem config, String configName, String configValue, boolean isClearConsole) {
		if (config.cell(0).contains(configName)) {
			bot.sleep(2000);
			config.click(1);
			config.click(1);
			if (isClearConsole) {
				clickClearConsole();
			}
			bot.text(1).setText(configValue);
		}
	}
	
	public static boolean changeConfigOfTextBoxVerifyError(SWTBotTreeItem config, String configName, String configValue,
			boolean isClearConsole) {
		boolean isVerifyError=false;
		if (config.cell(0).contains(configName)) {
			bot.sleep(2000);
			config.click(1);
			config.click(1);
			if(isClearConsole) {
				clickClearConsole();
			}
			try {
				bot.text(config.cell(1)).setText("");
			} catch (Exception e) {
				isVerifyError = true;
			}
			;

		}
		return isVerifyError;
	}
	
	
	public static void checkEventGroups() {
		
		bot.tabItem(ProjectParameters.KernelObjectTab.EVENT_GROUPS).activate();
		Utility.addOrRemoveKernelObject(true, 0);

		boolean isEventGroupsDisplayCorrectly = false;
		if (bot.text(1).getText().equals(ProjectParameters.KernelObject.EVENT_GRP_HANDLE_1)) {
			isEventGroupsDisplayCorrectly = true;
		}

		if (!isEventGroupsDisplayCorrectly) {
			assertFalse(true);
		}
	}
	
	
	public static void checkMsgBuffer() {
		bot.tabItem(ProjectParameters.KernelObjectTab.MESSAGE_BUFFERS).activate();
		Utility.addOrRemoveKernelObject(true, 0);

		boolean isMsgBufferDisplayCorrectly = false;
		if (bot.text(1).getText().equals(ProjectParameters.KernelObject.MSG_BFF_HANDLE_1)
				&& bot.text(2).getText().equals(ProjectParameters.KernelObject.NUMBER_100)) {
			isMsgBufferDisplayCorrectly = true;
		}

		if (!isMsgBufferDisplayCorrectly) {
			assertFalse(true);
		}
	}
	
	
	public static void checkQueueUI() {
		bot.tabItem(ProjectParameters.KernelObjectTab.QUEUES).activate();
		Utility.addOrRemoveKernelObject(true, 0);

		boolean isQueueDisplayCorrectly = false;
		if (bot.text(1).getText().equals(ProjectParameters.KernelObject.QUEUE_HANDLE_1)
				&& bot.text(2).getText().equals(ProjectParameters.KernelObject.NUMBER_100)
				&& bot.text(3).getText().equals(ProjectParameters.KernelObject.SIZEOF)) {
			isQueueDisplayCorrectly = true;
		}

		if (!isQueueDisplayCorrectly) {
			assertFalse(true);
		}
	}
	
	public static void checkSemaphoresUI() {
		bot.tabItem(ProjectParameters.KernelObjectTab.SEMAPHORES).activate();
		Utility.addOrRemoveKernelObject(true, 0);

		boolean isSemaphoreDisplayCorrectly = false;
		if (bot.ccomboBox(0).getText().equals(ProjectParameters.KernelObject.BINARY)
				&& bot.text(1).getText().equals(ProjectParameters.KernelObject.SEMAPHORE_HANDLE_1)) {
			isSemaphoreDisplayCorrectly = true;
		}

		if (!isSemaphoreDisplayCorrectly) {
			assertFalse(true);
		}
	}
	
	
	public static void checkSWTimerUI()  {
		bot.tabItem(ProjectParameters.KernelObjectTab.SOFTWARE_TIMERS).activate();
		Utility.addOrRemoveKernelObject(true, 0);

		boolean isSWTimerDisplayCorrectly = false;
		if (bot.text(1).getText().equals(ProjectParameters.KernelObject.SWT_HANDLE_1)
				&& bot.text(2).getText().equals(ProjectParameters.KernelObject.TIMER_1)
				&& bot.text(3).getText().equals(ProjectParameters.KernelObject.NUMBER_100)
				&& bot.ccomboBox(0).getText().equals(ProjectParameters.KernelObject.FALSE)
				&& bot.text(4).getText().equals(ProjectParameters.KernelObject.NUMBER_0)
				&& bot.text(5).getText().equals(ProjectParameters.KernelObject.NULL)) {
			isSWTimerDisplayCorrectly = true;
		}

		if (!isSWTimerDisplayCorrectly) {
			assertFalse(true);
		}
	}
	
	
	public static void checkStreamBufferUI() {
		bot.tabItem(ProjectParameters.KernelObjectTab.STREAM_BUFFERS).activate();
		Utility.addOrRemoveKernelObject(true, 0);

		boolean isStreamBufferDisplayCorrectly = false;
		if (bot.text(1).getText().equals(ProjectParameters.KernelObject.STREAM_BFF_HANDLE_1)
				&& bot.text(2).getText().equals(ProjectParameters.KernelObject.NUMBER_100)
				&& bot.text(3).getText().equals(ProjectParameters.KernelObject.NUMBER_10)) {
			isStreamBufferDisplayCorrectly = true;
		}

		if (!isStreamBufferDisplayCorrectly) {
			assertFalse(true);
		}
	}
	
	
	public static void CheckTaskUI() {
		bot.tabItem(ProjectParameters.KernelObjectTab.TASKS).activate();
		Utility.addOrRemoveKernelObject(true, 0);

		boolean isTasksObjectDisplayCorrectly = false;
		if (bot.ccomboBox(0).getText().equals(ProjectParameters.KernelObject.KERNEL_START)
				&& bot.text(1).getText().equals(ProjectParameters.KernelObject.TASK_1)
				&& bot.text(2).getText().equals(ProjectParameters.KernelObject.TASK_1)
				&& bot.text(3).getText().equals(ProjectParameters.KernelObject.NUMBER_512)
				&& bot.text(4).getText().equals(ProjectParameters.KernelObject.NULL)
				&& bot.text(5).getText().equals(ProjectParameters.KernelObject.NULL)
				&& bot.text(6).getText().equals(ProjectParameters.KernelObject.NUMBER_1)) {
			isTasksObjectDisplayCorrectly = true;
		}

		if (!isTasksObjectDisplayCorrectly) {
			assertFalse(true);
		}
	}
	
	public static void SaveConfigOfAWSLibs(ProjectModel projectModelSpecific) {
		Utility.openSCFGEditor(projectModelSpecific, ProjectParameters.SCFG_COMPONENT_TAB);

		bot.tree(1).getTreeItem(ProjectParameters.FolderAndFile.FOLDER_RTOS)
				.getNode(ProjectParameters.FolderAndFile.FOLDER_RTOS_LIBRARY)
				.getNode(ProjectParameters.RTOSComponent.AWS_MQTT).select();
		SWTBotTreeItem[] amazonConfigTree = bot.tree(2).getTreeItem(ProjectParameters.KernelConfig.CONFIGURATIONS)
				.getItems();
		for (SWTBotTreeItem config : amazonConfigTree) {
			Utility.changeConfigOfCombobox(config, ProjectParameters.AmazonConfig.REPORT_USAGE, "Disable");
		}

		bot.tree(1).getTreeItem(ProjectParameters.FolderAndFile.FOLDER_RTOS)
				.getNode(ProjectParameters.FolderAndFile.FOLDER_RTOS_LIBRARY)
				.getNode(ProjectParameters.RTOSComponent.AWS_DEVICE_SHADOW).select();
		amazonConfigTree = bot.tree(2).getTreeItem(ProjectParameters.KernelConfig.CONFIGURATIONS).getItems();
		for (SWTBotTreeItem config : amazonConfigTree) {
			Utility.changeConfigOfTextBox(config, ProjectParameters.AmazonConfig.JSMN_TOKENS, "128", false);
		}

		bot.tree(1).getTreeItem(ProjectParameters.FolderAndFile.FOLDER_RTOS)
				.getNode(ProjectParameters.FolderAndFile.FOLDER_RTOS_LIBRARY)
				.getNode(ProjectParameters.RTOSComponent.AWS_GGD).select();
		amazonConfigTree = bot.tree(2).getTreeItem(ProjectParameters.KernelConfig.CONFIGURATIONS).getItems();
		for (SWTBotTreeItem config : amazonConfigTree) {
			Utility.changeConfigOfTextBox(config, ProjectParameters.AmazonConfig.SIZE_ARRAY_FOR_TOKENS, "256", false);
		}

		bot.tree(1).getTreeItem(ProjectParameters.FolderAndFile.FOLDER_RTOS)
				.getNode(ProjectParameters.FolderAndFile.FOLDER_RTOS_LIBRARY)
				.getNode(ProjectParameters.RTOSComponent.AWS_SECURE_SOCKET).select();
		amazonConfigTree = bot.tree(2).getTreeItem(ProjectParameters.KernelConfig.CONFIGURATIONS).getItems();
		for (SWTBotTreeItem config : amazonConfigTree) {
			Utility.changeConfigOfTextBox(config, ProjectParameters.AmazonConfig.DEFAULT_SOCKET_RECEIVE_TIMEOUT,
					"20000", false);
		}

		bot.tree(1).getTreeItem(ProjectParameters.FolderAndFile.FOLDER_RTOS)
				.getNode(ProjectParameters.FolderAndFile.FOLDER_RTOS_LIBRARY)
				.getNode(ProjectParameters.RTOSComponent.AWS_TCP_IP).select();
		amazonConfigTree = bot.tree(2).getTreeItem(ProjectParameters.KernelConfig.CONFIGURATIONS).getItems();
		for (SWTBotTreeItem config : amazonConfigTree) {
			Utility.changeConfigOfCombobox(config, ProjectParameters.AmazonConfig.BYTE_ORDER, "pdFREERTOS_BIG_ENDIAN");
		}
		bot.menu(MenuName.MENU_FILE).menu(MenuName.MENU_SAVE).click();

		bot.closeAllEditors();
		Utility.openSCFGEditor(projectModelSpecific, ProjectParameters.SCFG_COMPONENT_TAB);

		bot.tree(1).getTreeItem(ProjectParameters.FolderAndFile.FOLDER_RTOS)
				.getNode(ProjectParameters.FolderAndFile.FOLDER_RTOS_LIBRARY)
				.getNode(ProjectParameters.RTOSComponent.AWS_MQTT).select();
		amazonConfigTree = bot.tree(2).getTreeItem(ProjectParameters.KernelConfig.CONFIGURATIONS).getItems();
		boolean isRightValue = false;
		for (SWTBotTreeItem config : amazonConfigTree) {
			if (config.cell(0).contains(ProjectParameters.AmazonConfig.REPORT_USAGE)) {
				isRightValue = config.cell(1).equals("Disable");
			}
		}

		bot.tree(1).getTreeItem(ProjectParameters.FolderAndFile.FOLDER_RTOS)
				.getNode(ProjectParameters.FolderAndFile.FOLDER_RTOS_LIBRARY)
				.getNode(ProjectParameters.RTOSComponent.AWS_DEVICE_SHADOW).select();
		amazonConfigTree = bot.tree(2).getTreeItem(ProjectParameters.KernelConfig.CONFIGURATIONS).getItems();
		boolean isRightValue2 = false;
		for (SWTBotTreeItem config : amazonConfigTree) {
			if (config.cell(0).contains(ProjectParameters.AmazonConfig.JSMN_TOKENS)) {
				isRightValue2 = config.cell(1).equals("128");
			}
		}

		bot.tree(1).getTreeItem(ProjectParameters.FolderAndFile.FOLDER_RTOS)
				.getNode(ProjectParameters.FolderAndFile.FOLDER_RTOS_LIBRARY)
				.getNode(ProjectParameters.RTOSComponent.AWS_GGD).select();
		amazonConfigTree = bot.tree(2).getTreeItem(ProjectParameters.KernelConfig.CONFIGURATIONS).getItems();
		boolean isRightValue3 = false;
		for (SWTBotTreeItem config : amazonConfigTree) {
			if (config.cell(0).contains(ProjectParameters.AmazonConfig.SIZE_ARRAY_FOR_TOKENS)) {
				isRightValue3 = config.cell(1).equals("256");
			}
		}

		bot.tree(1).getTreeItem(ProjectParameters.FolderAndFile.FOLDER_RTOS)
				.getNode(ProjectParameters.FolderAndFile.FOLDER_RTOS_LIBRARY)
				.getNode(ProjectParameters.RTOSComponent.AWS_SECURE_SOCKET).select();
		amazonConfigTree = bot.tree(2).getTreeItem(ProjectParameters.KernelConfig.CONFIGURATIONS).getItems();
		boolean isRightValue4 = false;
		for (SWTBotTreeItem config : amazonConfigTree) {
			if (config.cell(0).contains(ProjectParameters.AmazonConfig.DEFAULT_SOCKET_RECEIVE_TIMEOUT)) {
				isRightValue4 = config.cell(1).equals("20000");
			}
		}

		bot.tree(1).getTreeItem(ProjectParameters.FolderAndFile.FOLDER_RTOS)
				.getNode(ProjectParameters.FolderAndFile.FOLDER_RTOS_LIBRARY)
				.getNode(ProjectParameters.RTOSComponent.AWS_TCP_IP).select();
		amazonConfigTree = bot.tree(2).getTreeItem(ProjectParameters.KernelConfig.CONFIGURATIONS).getItems();
		boolean isRightValue5 = false;
		for (SWTBotTreeItem config : amazonConfigTree) {
			if (config.cell(0).contains(ProjectParameters.AmazonConfig.BYTE_ORDER)) {
				isRightValue5 = config.cell(1).equals("pdFREERTOS_BIG_ENDIAN");
			}
		}

		bot.tree(1).getTreeItem(ProjectParameters.FolderAndFile.FOLDER_RTOS)
				.getNode(ProjectParameters.FolderAndFile.FOLDER_RTOS_LIBRARY)
				.getNode(ProjectParameters.RTOSComponent.AWS_MQTT).select();
		amazonConfigTree = bot.tree(2).getTreeItem(ProjectParameters.KernelConfig.CONFIGURATIONS).getItems();
		for (SWTBotTreeItem config : amazonConfigTree) {
			Utility.changeConfigOfCombobox(config, ProjectParameters.AmazonConfig.REPORT_USAGE, "Enable ");
		}

		bot.tree(1).getTreeItem(ProjectParameters.FolderAndFile.FOLDER_RTOS)
				.getNode(ProjectParameters.FolderAndFile.FOLDER_RTOS_LIBRARY)
				.getNode(ProjectParameters.RTOSComponent.AWS_DEVICE_SHADOW).select();
		amazonConfigTree = bot.tree(2).getTreeItem(ProjectParameters.KernelConfig.CONFIGURATIONS).getItems();
		for (SWTBotTreeItem config : amazonConfigTree) {
			Utility.changeConfigOfTextBox(config, ProjectParameters.AmazonConfig.JSMN_TOKENS, "64", false);
		}

		bot.tree(1).getTreeItem(ProjectParameters.FolderAndFile.FOLDER_RTOS)
				.getNode(ProjectParameters.FolderAndFile.FOLDER_RTOS_LIBRARY)
				.getNode(ProjectParameters.RTOSComponent.AWS_GGD).select();
		amazonConfigTree = bot.tree(2).getTreeItem(ProjectParameters.KernelConfig.CONFIGURATIONS).getItems();
		for (SWTBotTreeItem config : amazonConfigTree) {
			Utility.changeConfigOfTextBox(config, ProjectParameters.AmazonConfig.SIZE_ARRAY_FOR_TOKENS, "128", false);
		}

		bot.tree(1).getTreeItem(ProjectParameters.FolderAndFile.FOLDER_RTOS)
				.getNode(ProjectParameters.FolderAndFile.FOLDER_RTOS_LIBRARY)
				.getNode(ProjectParameters.RTOSComponent.AWS_SECURE_SOCKET).select();
		amazonConfigTree = bot.tree(2).getTreeItem(ProjectParameters.KernelConfig.CONFIGURATIONS).getItems();
		for (SWTBotTreeItem config : amazonConfigTree) {
			Utility.changeConfigOfTextBox(config, ProjectParameters.AmazonConfig.DEFAULT_SOCKET_RECEIVE_TIMEOUT,
					"10000", false);
		}

		bot.tree(1).getTreeItem(ProjectParameters.FolderAndFile.FOLDER_RTOS)
				.getNode(ProjectParameters.FolderAndFile.FOLDER_RTOS_LIBRARY)
				.getNode(ProjectParameters.RTOSComponent.AWS_TCP_IP).select();
		amazonConfigTree = bot.tree(2).getTreeItem(ProjectParameters.KernelConfig.CONFIGURATIONS).getItems();
		for (SWTBotTreeItem config : amazonConfigTree) {
			Utility.changeConfigOfCombobox(config, ProjectParameters.AmazonConfig.BYTE_ORDER,
					"pdFREERTOS_LITTLE_ENDIAN ");
		}
		bot.menu(MenuName.MENU_FILE).menu(MenuName.MENU_SAVE).click();

		if (!isRightValue || !isRightValue2 || !isRightValue3 || !isRightValue4 || !isRightValue5) {
			assertFalse(true);
		}
	}
	
	public static void testRemoveAWSLibs() {
		bot.tree(1).getTreeItem(ProjectParameters.FolderAndFile.FOLDER_RTOS)
		.getNode(ProjectParameters.FolderAndFile.FOLDER_RTOS_LIBRARY)
		.getNode(ProjectParameters.RTOSComponent.AWS_MQTT).select();
		boolean canNotBeRemoved1 = !bot.toolbarButtonWithTooltip(ProjectParameters.ButtonAction.BUTTON_REMOVE_COMPONENT).isEnabled();
		
		bot.tree(1).getTreeItem(ProjectParameters.FolderAndFile.FOLDER_RTOS)
		.getNode(ProjectParameters.FolderAndFile.FOLDER_RTOS_LIBRARY)
		.getNode(ProjectParameters.RTOSComponent.AWS_DEVICE_SHADOW).select();
		boolean canNotBeRemoved2 = !bot.toolbarButtonWithTooltip(ProjectParameters.ButtonAction.BUTTON_REMOVE_COMPONENT).isEnabled();

		bot.tree(1).getTreeItem(ProjectParameters.FolderAndFile.FOLDER_RTOS)
		.getNode(ProjectParameters.FolderAndFile.FOLDER_RTOS_LIBRARY)
		.getNode(ProjectParameters.RTOSComponent.AWS_GGD).select();
		boolean canNotBeRemoved3 = !bot.toolbarButtonWithTooltip(ProjectParameters.ButtonAction.BUTTON_REMOVE_COMPONENT).isEnabled();

		bot.tree(1).getTreeItem(ProjectParameters.FolderAndFile.FOLDER_RTOS)
		.getNode(ProjectParameters.FolderAndFile.FOLDER_RTOS_LIBRARY)
		.getNode(ProjectParameters.RTOSComponent.AWS_SECURE_SOCKET).select();
		boolean canNotBeRemoved4 = !bot.toolbarButtonWithTooltip(ProjectParameters.ButtonAction.BUTTON_REMOVE_COMPONENT).isEnabled();

		bot.tree(1).getTreeItem(ProjectParameters.FolderAndFile.FOLDER_RTOS)
		.getNode(ProjectParameters.FolderAndFile.FOLDER_RTOS_LIBRARY)
		.getNode(ProjectParameters.RTOSComponent.AWS_TCP_IP).select();
		boolean canNotBeRemoved5 = !bot.toolbarButtonWithTooltip(ProjectParameters.ButtonAction.BUTTON_REMOVE_COMPONENT).isEnabled();
		
		if (!canNotBeRemoved1 || !canNotBeRemoved2 || !canNotBeRemoved3 || !canNotBeRemoved4 || !canNotBeRemoved5) {
			assertFalse(true);
		}
	}

	public static void reFocus(Robot robot) {
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		robot.mouseMove((int)screenSize.getWidth() / 2, (int) screenSize.getHeight() / 2);
		robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
		robot.mouseRelease(InputEvent.BUTTON2_DOWN_MASK);		
	}
	
	public static void checkDataAfterConfig(ProjectModel projectModelSpecific) {
		Utility.openSCFGEditor(projectModelSpecific, ProjectParameters.SCFG_COMPONENT_TAB);

		bot.tree(1).getTreeItem(ProjectParameters.FolderAndFile.FOLDER_RTOS)
				.getNode(ProjectParameters.FolderAndFile.FOLDER_RTOS_KERNEL)
				.getNode(ProjectParameters.RTOSComponent.FREERTOS_KERNEL).select();
		
		SWTBotTreeItem[] amazonConfigTree = bot.tree(2).getTreeItem(ProjectParameters.KernelConfig.CONFIGURATIONS)
				.getItems();
		for (SWTBotTreeItem config : amazonConfigTree) {
			Utility.changeConfigOfCombobox(config, ProjectParameters.AmazonConfig.RTOS_SCHEDULER, "Cooperative");
			Utility.changeConfigOfTextBox(config, ProjectParameters.AmazonConfig.MAXIMUM_PRIORITIES_APPLICATION_TASK, "6", false);
		}
		bot.closeAllEditors();
		if (bot.activeShell().getText().equals(ProjectParameters.WINDOW_SAVE_RESOURCES)) {
			bot.button(ButtonAction.BUTTON_DONT_SAVE).click();
		}
		
		Utility.openSCFGEditor(projectModelSpecific, ProjectParameters.SCFG_COMPONENT_TAB);

		bot.tree(1).getTreeItem(ProjectParameters.FolderAndFile.FOLDER_RTOS)
				.getNode(ProjectParameters.FolderAndFile.FOLDER_RTOS_KERNEL)
				.getNode(ProjectParameters.RTOSComponent.FREERTOS_KERNEL).select();
		
		amazonConfigTree = bot.tree(2).getTreeItem(ProjectParameters.KernelConfig.CONFIGURATIONS)
				.getItems();
		boolean isRightValue1 = false;
		boolean isRightValue2 = false;
		for (SWTBotTreeItem config : amazonConfigTree) {
			if (config.cell(0).contains(ProjectParameters.AmazonConfig.RTOS_SCHEDULER)) {
				isRightValue1 = config.cell(1).equals("Preemptive");
			}
			if (config.cell(0).contains(ProjectParameters.AmazonConfig.MAXIMUM_PRIORITIES_APPLICATION_TASK)) {
				isRightValue2 = config.cell(1).equals("7");
			}
		}
		
		if (!isRightValue1 || !isRightValue2) {
			assertFalse(true);
		}
	}
	
	public static void testRemoveKernelComponent () {
		bot.tree(1).getTreeItem(ProjectParameters.FolderAndFile.FOLDER_RTOS)
		.getNode(ProjectParameters.FolderAndFile.FOLDER_RTOS_KERNEL)
		.getNode(ProjectParameters.RTOSComponent.FREERTOS_KERNEL).select();
		
		boolean canNotBeRemoved1 = !bot.toolbarButtonWithTooltip(ProjectParameters.ButtonAction.BUTTON_REMOVE_COMPONENT).isEnabled();
		
		if(!canNotBeRemoved1) {
			assertFalse(true);
		}
	}
	
	public static void CheckEventGroupGUI(ProjectModel projectModelSpecific, boolean isLTSProject) {
		Utility.openSCFGEditor(projectModelSpecific, ProjectParameters.SCFG_COMPONENT_TAB);

		if (isLTSProject) {
			bot.tree(1).getTreeItem(ProjectParameters.FolderAndFile.FOLDER_RTOS)
			.getNode(ProjectParameters.FolderAndFile.FOLDER_RTOS_OBJECT)
			.getNode(ProjectParameters.RTOSComponent.IOT_LTS_FREERTOS_OBJECT).select();
		} else {
			bot.tree(1).getTreeItem(ProjectParameters.FolderAndFile.FOLDER_RTOS)
					.getNode(ProjectParameters.FolderAndFile.FOLDER_RTOS_OBJECT)
					.getNode(ProjectParameters.RTOSComponent.FREERTOS_OBJECT).select();
		}
		
		bot.sleep(2000);

		bot.tabItem(ProjectParameters.KernelObjectTab.EVENT_GROUPS).activate();
		boolean isEventGroupsDisplayCorrectly = false;
		List<String> columnsList = bot.table(0).columns();
		if (columnsList.get(0).equals(ProjectParameters.KernelObjectTableColumn.PLUS_MINUS)
				&& columnsList.get(1).equals(ProjectParameters.KernelObjectTableColumn.EVENT_GROUP_HANDLER)) {
			isEventGroupsDisplayCorrectly = true;
		}

		if (!isEventGroupsDisplayCorrectly) {
			assertFalse(true);
		}
	}
	
	public static void CheckMsgBufferGUI() {
		bot.sleep(2000);

		bot.tabItem(ProjectParameters.KernelObjectTab.MESSAGE_BUFFERS).activate();

		boolean isMsgBufferDisplayCorrectly = false;
		List<String> columnsList = bot.table(0).columns();
		if (columnsList.get(0).equals(ProjectParameters.KernelObjectTableColumn.PLUS_MINUS)
				&& columnsList.get(1).equals(ProjectParameters.KernelObjectTableColumn.MSG_BUFFER_HANDLER)
				&& columnsList.get(2).equals(ProjectParameters.KernelObjectTableColumn.MSG_BUFFER_SIZE)) {
			isMsgBufferDisplayCorrectly = true;
		}

		if (!isMsgBufferDisplayCorrectly) {
			assertFalse(true);
		}
	}
	
	public static void CheckQueueGUI() {
		bot.sleep(2000);

		bot.tabItem(ProjectParameters.KernelObjectTab.QUEUES).activate();

		boolean isQueueDisplayCorrectly = false;
		List<String> columnsList = bot.table(0).columns();
		if (columnsList.get(0).equals(ProjectParameters.KernelObjectTableColumn.PLUS_MINUS)
				&& columnsList.get(1).equals(ProjectParameters.KernelObjectTableColumn.QUEUE_HANDLER)
				&& columnsList.get(2).equals(ProjectParameters.KernelObjectTableColumn.QUEUE_LENGTH)
				&& columnsList.get(3).equals(ProjectParameters.KernelObjectTableColumn.ITEMS_SIZE)) {
			isQueueDisplayCorrectly = true;
		}

		if (!isQueueDisplayCorrectly) {
			assertFalse(true);
		}
	}
	

	public static void CheckSemaphoreGUI() {
		bot.sleep(2000);

		bot.tabItem(ProjectParameters.KernelObjectTab.SEMAPHORES).activate();

		boolean isSemaphoreDisplayCorrectly = false;
		List<String> columnsList = bot.table(0).columns();
		if (columnsList.get(0).equals(ProjectParameters.KernelObjectTableColumn.PLUS_MINUS)
				&& columnsList.get(1).equals(ProjectParameters.KernelObjectTableColumn.SEMAPHORE_TYPE)
				&& columnsList.get(2).equals(ProjectParameters.KernelObjectTableColumn.SEMAPHORE_HANDLER)) {
			isSemaphoreDisplayCorrectly = true;
		}

		if (!isSemaphoreDisplayCorrectly) {
			assertFalse(true);
		}
	}

	public static void CheckStreamBufferGUI() {
		bot.sleep(2000);

		bot.tabItem(ProjectParameters.KernelObjectTab.STREAM_BUFFERS).activate();

		boolean isStreamBufferDisplayCorrectly = false;
		List<String> columnsList = bot.table(0).columns();
		if (columnsList.get(0).equals(ProjectParameters.KernelObjectTableColumn.PLUS_MINUS)
				&& columnsList.get(1).equals(ProjectParameters.KernelObjectTableColumn.STREAM_BUFFER_HANDLER)
				&& columnsList.get(2).equals(ProjectParameters.KernelObjectTableColumn.STREAM_BUFFER_SIZE)
				&& columnsList.get(3).equals(ProjectParameters.KernelObjectTableColumn.TRIGGER_LEVEL)) {
			isStreamBufferDisplayCorrectly = true;
		}

		if (!isStreamBufferDisplayCorrectly) {
			assertFalse(true);
		}
	}
	
	public static void checkSWTimerGUI() {
		bot.sleep(2000);

		bot.tabItem(ProjectParameters.KernelObjectTab.SOFTWARE_TIMERS).activate();

		boolean isSWTimerDisplayCorrectly = false;
		List<String> columnsList = bot.table(0).columns();
		if (columnsList.get(0).equals(ProjectParameters.KernelObjectTableColumn.PLUS_MINUS)
				&& columnsList.get(1).equals(ProjectParameters.KernelObjectTableColumn.SWTIMER_HANDLER)
				&& columnsList.get(2).equals(ProjectParameters.KernelObjectTableColumn.SWTIMER_NAME)
				&& columnsList.get(3).equals(ProjectParameters.KernelObjectTableColumn.SWTIMER_PERIOD)
				&& columnsList.get(4).equals(ProjectParameters.KernelObjectTableColumn.AUTO_RELOAD)
				&& columnsList.get(5).equals(ProjectParameters.KernelObjectTableColumn.SWTIMER_ID)
				&& columnsList.get(6).equals(ProjectParameters.KernelObjectTableColumn.CALLBACK_FUNCTION)) {
			isSWTimerDisplayCorrectly = true;
		}

		if (!isSWTimerDisplayCorrectly) {
			assertFalse(true);
		}
	}
	
	public static void checkTaskGUI() {
		bot.sleep(2000);

		bot.tabItem(ProjectParameters.KernelObjectTab.TASKS).activate();

		boolean isTaskDisplayCorrectly = false;
		List<String> columnsList = bot.table(0).columns();
		if (columnsList.get(0).equals(ProjectParameters.KernelObjectTableColumn.PLUS_MINUS)
				&& columnsList.get(1).equals(ProjectParameters.KernelObjectTableColumn.INITIALIZE)
				&& columnsList.get(2).equals(ProjectParameters.KernelObjectTableColumn.TASK_CODE)
				&& columnsList.get(3).equals(ProjectParameters.KernelObjectTableColumn.TASK_NAME)
				&& columnsList.get(4).equals(ProjectParameters.KernelObjectTableColumn.STACK_SIZE)
				&& columnsList.get(5).equals(ProjectParameters.KernelObjectTableColumn.TASK_HANDLER)
				&& columnsList.get(6).equals(ProjectParameters.KernelObjectTableColumn.PARAMETER)
				&& columnsList.get(7).equals(ProjectParameters.KernelObjectTableColumn.PRIORITY)) {
			isTaskDisplayCorrectly = true;
		}

		if (!isTaskDisplayCorrectly) {
			assertFalse(true);
		}
	}
	
	public static void MustNotBeANumber(ProjectModel projectModelSpecific, boolean isLTSProject) {
		Utility.openSCFGEditor(projectModelSpecific, ProjectParameters.SCFG_COMPONENT_TAB);

		if (isLTSProject) {
			bot.tree(1).getTreeItem(ProjectParameters.FolderAndFile.FOLDER_RTOS)
			.getNode(ProjectParameters.FolderAndFile.FOLDER_RTOS_OBJECT)
			.getNode(ProjectParameters.RTOSComponent.IOT_LTS_FREERTOS_OBJECT).select();
		} else {
			bot.tree(1).getTreeItem(ProjectParameters.FolderAndFile.FOLDER_RTOS)
					.getNode(ProjectParameters.FolderAndFile.FOLDER_RTOS_OBJECT)
					.getNode(ProjectParameters.RTOSComponent.FREERTOS_OBJECT).select();
		}
		
		bot.tabItem(ProjectParameters.KernelObjectTab.TASKS).activate();
		Utility.clickClearConsole();
		Utility.addOrRemoveKernelObject(true, 0);

		bot.sleep(3000);
		bot.text(1).setText("1");

		boolean check1 = Utility.isConsoleHasString(ProjectParameters.MessageCode.E04050001);
		boolean check2 = Utility.isConsoleHasString(ProjectParameters.MessageCode.E04050003);

		bot.text(1).setText("task_1");
		Utility.clickClearConsole();

		bot.text(4).setText("1");

		boolean check3 = Utility.isConsoleHasString(ProjectParameters.MessageCode.E04050001);
		boolean check4 = Utility.isConsoleHasString(ProjectParameters.MessageCode.E04050003);

		bot.text(4).setText("NULL");
		Utility.clickClearConsole();

		if (!check1 || !check2 || !check3 || !check4) {
			assertFalse(true);
		}
	}
	
	public static void OutOfSizeQueues() {
		bot.tabItem(ProjectParameters.KernelObjectTab.QUEUES).activate();
		Utility.addOrRemoveKernelObject(true, 0);

		bot.text(2).setText("4294967296");

		boolean check1 = Utility.isConsoleHasString(ProjectParameters.MessageCode.E04050006);

		bot.text(2).setText("100");

		Utility.clickClearConsole();

		if (!check1) {
			assertFalse(true);
		}
	}

	public static void OutOfSizeSWTimer() {
		bot.tabItem(ProjectParameters.KernelObjectTab.SOFTWARE_TIMERS).activate();
		Utility.addOrRemoveKernelObject(true, 0);
		bot.text(3).setText("4294967296");
		boolean check1 = Utility.isConsoleHasString("E04050006: The value must be from 1 to 4294967295");

		bot.text(3).setText("100");

		Utility.clickClearConsole();

		bot.text(4).setText("4294967296");
		boolean check2 = Utility.isConsoleHasString("E04050006: The value must be from 0 to 4294967295");

		bot.text(4).setText("0");

		Utility.clickClearConsole();

		if (!check1 || !check2) {
			assertFalse(true);
		}
	}

	public static void ParameterMustNotBeADigit() {
		bot.tabItem(ProjectParameters.KernelObjectTab.TASKS).activate();
		
		bot.text(5).setText("1");
		
		boolean check1 = Utility.isConsoleHasString(ProjectParameters.MessageCode.E04050001);
		boolean check2 = Utility.isConsoleHasString(ProjectParameters.MessageCode.E04050003);

		bot.text(5).setText("NULL");
		Utility.clickClearConsole();
		
		if (!check1 || !check2) {
			assertFalse(true);
		}
	}
	
	public static void RemovedDuplicatedValues() {
		bot.tabItem(ProjectParameters.KernelObjectTab.TASKS).activate();
		
		Utility.addOrRemoveKernelObject(true, 0);
		Utility.addOrRemoveKernelObject(true, 0);
		
		bot.text(1+6*1).setText("task_1");
		boolean check1 = Utility.isConsoleHasString(ProjectParameters.MessageCode.E04050007);
		bot.text(1+6*1).setText("task_2");

		bot.sleep(2000);
		Utility.clickClearConsole();
		
		bot.text(1+6*2).setText("task_1");
		boolean check2 = Utility.isConsoleHasString(ProjectParameters.MessageCode.E04050007);
		bot.text(1+6*2).setText("task_3");

		bot.sleep(2000);
		Utility.clickClearConsole();
		
		bot.text(2+6*1).setText("task_1");
		boolean check3 = Utility.isConsoleHasString(ProjectParameters.MessageCode.E04050007);
		bot.text(2+6*1).setText("task_2");

		bot.sleep(2000);
		Utility.clickClearConsole();

		bot.text(2+6*2).setText("task_1");
		boolean check4 = Utility.isConsoleHasString(ProjectParameters.MessageCode.E04050007);
		bot.text(2+6*2).setText("task_3");

		bot.sleep(2000);
		Utility.clickClearConsole();
		
		bot.text(4+6*0).setText("test");
		bot.text(4+6*1).setText("test");
		boolean check5 = Utility.isConsoleHasString(ProjectParameters.MessageCode.E04050007);
		bot.text(4+6*1).setText("NULL");

		bot.sleep(2000);
		Utility.clickClearConsole();
		
		bot.text(4+6*2).setText("test");
		boolean check6 = Utility.isConsoleHasString(ProjectParameters.MessageCode.E04050007);
		bot.text(4+6*2).setText("NULL");

		bot.sleep(2000);
		Utility.clickClearConsole();
		
		if (!check1 || !check2 || !check3 || !check4 || !check5 || !check6) {
			assertFalse(true);
		}
	}
	
	public static void DownloadAFRPackage(boolean isLTSProject) {
		bot.sleep(3000);
		bot.menu(MenuName.MENU_FILE).menu(MenuName.MENU_NEW)
		.menu(MenuName.MENU_C_CPP_PROJECT).menu(MenuName.MENU_RENESAS_RX).click();
		bot.table().select(2);
		
		bot.button(ButtonAction.BUTTON_NEXT).click();
		bot.waitUntil(Conditions.waitForWidget(WidgetMatcherFactory.withText(LabelName.LABEL_PROJECT_NAME)), 10000);
		bot.textWithLabel(LabelName.LABEL_PROJECT_NAME).setText("DownloadAFR");
		bot.waitUntil(Conditions.widgetIsEnabled(bot.button(ButtonAction.BUTTON_NEXT)), 10000);
		
		bot.button(ButtonAction.BUTTON_NEXT).click();
		bot.radio(0).click();
		
		if(isLTSProject) {
			bot.comboBoxWithLabel(LabelName.LABEL_RTOS).setSelection(RTOSDisplay.FREERTOSIOTLTS);
		}else {
			bot.comboBoxWithLabel(LabelName.LABEL_RTOS).setSelection(RTOSDisplay.AMAZONFREERTOS);
		}
		
		
		bot.link("<a>Manage RTOS Versions...</a>").click();
		bot.sleep(40000);

		if(isLTSProject) {
			bot.table().getTableItem(bot.table().indexOf("v202210.01-LTS-rx-1.0.0-rc2", "Rev.")).check();
		}else {
			bot.table().getTableItem(bot.table().indexOf(RTOSVersion.Amazon_202107_1_0_1, "Rev.")).check();
		}
		bot.button("Download").click();
		bot.button("Accept").click();
		
		bot.comboBoxWithLabel(LabelName.LABEL_TARGET_BOARD).setSelection(TargetBoard.BOARD_CK_RX65N);
		bot.button(ButtonAction.BUTTON_NEXT).click();
		bot.button(ButtonAction.BUTTON_FINISH).click();
		
		
		if(isLTSProject) {
			PGUtility.loopForPGAzureAndLTS();
		}else {
			PGUtility.loopForPGOther();
		}
	}
}
