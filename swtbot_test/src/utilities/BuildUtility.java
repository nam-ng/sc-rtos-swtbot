package utilities;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotView;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;

import common.Constants;
import model.ProjectModel;
import parameters.CommonParameters;
import parameters.ProjectParameters;
import parameters.ProjectParameters.BuildType;
import parameters.ProjectParameters.ButtonAction;
import parameters.ProjectParameters.MenuName;

public class BuildUtility extends Utility {

	public static void setBuildConfiguration(ProjectModel model) {
		Utility.projectExplorerSelectProject(model);
		String buildType = model.getActiveBuildConfiguration();
		SWTBotTreeItem projectItem = Utility.getProjectItemOnProjectExplorer(model.getProjectName()); 
		List<String> menus = new ArrayList<>();
		menus = bot.tree().getTreeItem(projectItem.getText()).contextMenu(CommonParameters.ContextMenuBuildConfigurations)
				.menu(CommonParameters.MenuSetActive).menuItems();
		if (Constants.GCC_TOOLCHAIN.equalsIgnoreCase(model.getToolchain())) {
			if (buildType.equalsIgnoreCase(BuildType.RELEASE)) {
				for (String item : menus) {
					if (item.matches("^\\d+\s*Release$")) {
						bot.tree().getTreeItem(projectItem.getText())
								.contextMenu(CommonParameters.ContextMenuBuildConfigurations)
								.menu(CommonParameters.MenuSetActive).menu(item).click();
					}
				}
			} else if (buildType.equalsIgnoreCase(BuildType.HARDWARE)) {
				for (String item : menus) {
					if (item.matches("^\\d+\s*HardwareDebug$")) {
						bot.tree().getTreeItem(projectItem.getText())
								.contextMenu(CommonParameters.ContextMenuBuildConfigurations)
								.menu(CommonParameters.MenuSetActive).menu(item).click();
					}
				}
			} else {
				for (String item : menus) {
					if (item.matches("^\\d+\s*Debug$")) {
						bot.tree().getTreeItem(projectItem.getText())
								.contextMenu(CommonParameters.ContextMenuBuildConfigurations)
								.menu(CommonParameters.MenuSetActive).menu(item).click();
					}
				}
			}
		} else if (Constants.CCRX_TOOLCHAIN.equalsIgnoreCase(model.getToolchain())) {
			if (buildType.equalsIgnoreCase(BuildType.RELEASE)) {
				for (String item : menus) {
					if (item.matches("^\\d+\s*Release[^\n]+$")) {
						bot.tree().setFocus();
						bot.tree().getTreeItem(projectItem.getText()).contextMenu(CommonParameters.ContextMenuBuildConfigurations)
								.menu(CommonParameters.MenuSetActive).menu(item).click();
					}
				}
			} else if (buildType.equalsIgnoreCase(BuildType.HARDWARE)) {
				for (String item : menus) {
					if (item.matches("^\\d+\s*HardwareDebug[^\n]+$")) {
						bot.tree().getTreeItem(projectItem.getText()).contextMenu(CommonParameters.ContextMenuBuildConfigurations)
								.menu(CommonParameters.MenuSetActive).menu(item).click();
					}
				}
			} else {
				for (String item : menus) {
					if (item.matches("^\\d+\s*Debug[^\n]+$")) {
						bot.tree().getTreeItem(projectItem.getText()).contextMenu(CommonParameters.ContextMenuBuildConfigurations)
								.menu(CommonParameters.MenuSetActive).menu(item).click();
					}
				}
			}
		}
	}

	public static boolean buildProject(ProjectModel model) {
		bot.sleep(5000);
		setBuildConfiguration(model);
		bot.sleep(5000);
		String buildType = model.getActiveBuildConfiguration();
		bot.tree().getTreeItem(model.getProjectName() + " [" + buildType + "]")
				.contextMenu(MenuName.MENU_BUILD_PROJECT).click();
		bot.sleep(15000);
		SWTBotView consoleView = bot.viewById("org.eclipse.ui.console.ConsoleView");
		boolean isBuildSuccessful = false;
		while (true) {
			if (consoleView.bot().styledText().getText().contains(ProjectParameters.BUILD_SUCCESSFULLY)) {
				isBuildSuccessful = true;
				break;
			}
			if (consoleView.bot().styledText().getText().contains(ProjectParameters.BUILD_FAILED)) {
				break;
			}
		}
		if (isBuildSuccessful) {
			deleteProject(model);
		}
		return isBuildSuccessful;
	}

	public static void deleteProject(ProjectModel model) {
		Utility.deleteProject(model.getProjectName(), true);
	}

	public static void buildAll(SWTBotShell shell){
		bot.menu(shell).menu("Project").menu("Build All").click();
	}
}
