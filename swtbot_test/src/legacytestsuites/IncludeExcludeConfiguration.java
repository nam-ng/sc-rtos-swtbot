package legacytestsuites;

import static org.junit.Assert.assertFalse;

import java.io.File;

import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
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
import parameters.ProjectParameters.TargetBoard;
import platform.PlatformModel;
import utilities.PGUtility;
import utilities.Utility;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class IncludeExcludeConfiguration {
	private static SWTWorkbenchBot bot;
	private static ProjectModel projectModelSpecific = new ProjectModel();
	private static final String PLATFORM_XML_FILE = "xml/platformdata.xml";
	private static final String RTOS_PG_XML_FILE = "xml/rtospg.xml";

	@BeforeClass
	public static void beforeClass() throws Exception {
		bot = new SWTWorkbenchBot();
		PlatformModel.loadPlatformModel(new File(Utility.getBundlePath(LogUtil.PLUGIN_ID, PLATFORM_XML_FILE)));
		RTOSManager.loadRTOSModel(new File(Utility.getBundlePath(LogUtil.PLUGIN_ID, RTOS_PG_XML_FILE)));
		projectModelSpecific = PGUtility.prepareProjectModel(RTOSType.AMAZONFREERTOS, RTOSVersion.Amazon_202107_1_0_1,
				RTOSApplication.AMAZON_BARE, Constants.CCRX_TOOLCHAIN, TargetBoard.BOARD_RSK_RX65N_2MB);
	}

	@Test
	public void tc_01_CreateAmazonProject() throws Exception {
		PGUtility.createProject(RTOSType.AMAZONFREERTOS, RTOSVersion.Amazon_202107_1_0_1, RTOSApplication.AMAZON_BARE,
				Constants.CCRX_TOOLCHAIN, TargetBoard.BOARD_RSK_RX65N_2MB);

	}
	
	@Test
	public void tc_02_ExcludeConfigAndGenCode() throws Exception {
		Utility.openSCFGEditor(projectModelSpecific, ProjectParameters.SCFG_COMPONENT_TAB);
		bot.tree(1).getTreeItem(ProjectParameters.FolderAndFile.FOLDER_RTOS)
		.getNode(ProjectParameters.FolderAndFile.FOLDER_RTOS_LIBRARY)
		.getNode(ProjectParameters.RTOSComponent.AWS_DEVICE_SHADOW)
		.contextMenu().menu(ProjectParameters.MenuName.CONTEXT_MENU_EXCLUDE).click();
		
		boolean isConfigExclude = !bot.tree(2).getTreeItem(ProjectParameters.KernelConfig.CONFIGURATIONS).isEnabled();

		bot.menu(MenuName.MENU_FILE).menu(MenuName.MENU_SAVE_ALL).click();
		Utility.clickGenerateCode();
		
		if (!isConfigExclude) {
			assertFalse(true);
		}
	}
	
	@Test
	public void tc_03_IncludeConfigAndGenCode() throws Exception {
		Utility.openSCFGEditor(projectModelSpecific, ProjectParameters.SCFG_COMPONENT_TAB);
		bot.tree(1).getTreeItem(ProjectParameters.FolderAndFile.FOLDER_RTOS)
		.getNode(ProjectParameters.FolderAndFile.FOLDER_RTOS_LIBRARY)
		.getNode(ProjectParameters.RTOSComponent.AWS_DEVICE_SHADOW)
		.contextMenu().menu(ProjectParameters.MenuName.CONTEXT_MENU_EXCLUDE).click();
		
		boolean isConfigInclude = bot.tree(2).getTreeItem(ProjectParameters.KernelConfig.CONFIGURATIONS).isEnabled();
		
		bot.menu(MenuName.MENU_FILE).menu(MenuName.MENU_SAVE_ALL).click();
		Utility.clickGenerateCode();
		
		if (!isConfigInclude) {
			assertFalse(true);
		}
	}
	
	@Test
	public void tc_04_DeleteAmazonProject() throws Exception {
		Utility.deleteProject(projectModelSpecific.getProjectName(), true);
		if (bot.activeShell().getText().equals(ProjectParameters.WINDOW_SAVE_RESOURCES)) {
			bot.button(ButtonAction.BUTTON_DONT_SAVE).click();
		}
	}
}
