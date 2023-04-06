package utilities;

import org.eclipse.jface.bindings.keys.KeyStroke;
import org.eclipse.jface.bindings.keys.ParseException;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotView;
import org.eclipse.swtbot.swt.finder.keyboard.Keystrokes;

import model.ProjectModel;
import common.Constants;
import parameters.CommonParameters;
import parameters.ProjectParameters;
import parameters.ProjectParameters.BuildType;
import parameters.ProjectParameters.ButtonAction;
import parameters.ProjectParameters.MenuName;

public class BuildUtility extends Utility {

	public static void setBuildConfiguration(ProjectModel model) {
		bot.sleep(5000);
		Utility.projectExplorerSelectProject(model);
		String buildType = model.getActiveBuildConfiguration();
		if (Constants.GCC_TOOLCHAIN.equalsIgnoreCase(model.getToolchain())) {
			if (buildType.equalsIgnoreCase(BuildType.RELEASE)) {
				bot.tree().getTreeItem(model.getProjectName() + " [" + buildType + "]")
						.contextMenu(CommonParameters.ContextMenuBuildConfigurations)
						.menu(CommonParameters.MenuSetActive).menu(CommonParameters.GCCMenuActiveRelease).click();
			} else if (buildType.equalsIgnoreCase(BuildType.HARDWARE)) {
				bot.tree().getTreeItem(model.getProjectName() + " [" + buildType + "]")
						.contextMenu(CommonParameters.ContextMenuBuildConfigurations)
						.menu(CommonParameters.MenuSetActive).menu(CommonParameters.GCCMenuActiveHardwareDebug).click();
			} else {
				bot.tree().getTreeItem(model.getProjectName() + " [" + buildType + "]")
						.contextMenu(CommonParameters.ContextMenuBuildConfigurations)
						.menu(CommonParameters.MenuSetActive).menu(CommonParameters.GCCMenuActiveDebug).click();
			}
		} else if (Constants.CCRX_TOOLCHAIN.equalsIgnoreCase(model.getToolchain())) {
			if (buildType.equalsIgnoreCase(BuildType.RELEASE)) {
				bot.tree().getTreeItem(model.getProjectName() + " [" + buildType + "]")
						.contextMenu(CommonParameters.ContextMenuBuildConfigurations)
						.menu(CommonParameters.MenuSetActive).menu(CommonParameters.CCRXMenuActiveRelease).click();
			} else if (buildType.equalsIgnoreCase(BuildType.HARDWARE)) {
				bot.tree().getTreeItem(model.getProjectName() + " [" + buildType + "]")
						.contextMenu(CommonParameters.ContextMenuBuildConfigurations)
						.menu(CommonParameters.MenuSetActive).menu(CommonParameters.CCRXMenuActiveHardwareDebug)
						.click();
			} else {
				bot.tree().getTreeItem(model.getProjectName() + " [" + buildType + "]")
						.contextMenu(CommonParameters.ContextMenuBuildConfigurations)
						.menu(CommonParameters.MenuSetActive).menu(CommonParameters.CCRXMenuActiveDebug).click();
			}
		}
		bot.sleep(5000);
	}

	public static void buildProject(ProjectModel model) {
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
			if (bot.activeShell().getText().contains(ProjectParameters.WINDOW_INSTALL)) {
				bot.button(ButtonAction.BUTTON_CANCEL).click();
			}
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
	}

	public static void deleteProject(ProjectModel model) {
		
	}

	public static void buildAll() throws ParseException {
		bot.sleep(3000);
		bot.shell().pressShortcut(Keystrokes.CTRL, Keystrokes.ALT, KeyStroke.getInstance("B"));
		bot.sleep(3000);
	}
}
