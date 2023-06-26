package testsuites;

import static org.junit.Assert.assertFalse;

import java.io.File;

import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotView;
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
import parameters.ProjectParameters.RTOSApplication;
import parameters.ProjectParameters.RTOSComponent;
import parameters.ProjectParameters.RTOSType;
import parameters.ProjectParameters.RTOSVersion;
import parameters.ProjectParameters.TargetBoard;
import platform.PlatformModel;
import utilities.PGUtility;
import utilities.Utility;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ThreadxLowPowerConfiguration {
	private static SWTWorkbenchBot bot;
	private static ProjectModel projectModelSpecific = new ProjectModel();
	private static final String PLATFORM_XML_FILE = "xml/platformdata.xml";
	private static final String RTOS_PG_XML_FILE = "xml/rtospg.xml";
	
	@BeforeClass
	public static void beforeClass() throws Exception {
		bot = new SWTWorkbenchBot();
		PlatformModel.loadPlatformModel(new File(Utility.getBundlePath(LogUtil.PLUGIN_ID, PLATFORM_XML_FILE)));
		RTOSManager.loadRTOSModel(new File(Utility.getBundlePath(LogUtil.PLUGIN_ID, RTOS_PG_XML_FILE)));
		projectModelSpecific = PGUtility.prepareProjectModel(RTOSType.AZURE, RTOSVersion.Azure_6_2_1, RTOSApplication.AZURE_LOW_POWER, Constants.CCRX_TOOLCHAIN, TargetBoard.BOARD_RSK_RX65N_2MB);
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
	public void tc_01_CreateLowPowerProject() throws Exception{
		PGUtility.createProject(RTOSType.AZURE, RTOSVersion.Azure_6_2_1, RTOSApplication.AZURE_LOW_POWER, Constants.CCRX_TOOLCHAIN, TargetBoard.BOARD_RSK_RX65N_2MB);
	}
	
	@Test
	public void tc_02_LowPowerConfiguration() throws Exception{
		Utility.openSCFGEditor(projectModelSpecific);
		bot.tree(1).getTreeItem(ProjectParameters.FolderAndFile.FOLDER_RTOS)
		.getNode(ProjectParameters.FolderAndFile.FOLDER_RTOS_KERNEL)
		.getNode(RTOSComponent.THREADX).select();

		SWTBotTreeItem[] lowPowerConfigTree= bot.tree(2).getTreeItem("Configurations ").getItems();
		for (SWTBotTreeItem config: lowPowerConfigTree) {
			if (config.cell(0).contains("Enable low power mode ") || config.cell(0).contains("Enable threadx wait ")) {
				bot.sleep(2000);
				config.click(1);
				config.click(1);
			}
		}
		Utility.clickGenerateCode();
		Utility.getProjectExplorerView().setFocus();
		bot.tree().getTreeItem(projectModelSpecific.getProjectName() + " ["+ projectModelSpecific.getActiveBuildConfiguration() +"]").contextMenu(ProjectParameters.ProjectSettings.C_CPLUSPLUS_PROJECT_SETTINGS).click();
		bot.tree(1).getTreeItem(ProjectParameters.ProjectSettings.ASSEMBLER).getNode(ProjectParameters.ProjectSettings.SOURCE).select();
		String[] macroDefinition = bot.list(1).getItems();
		boolean txLowPower = false;
		boolean txEnableWait = false;
		for (String macroDef: macroDefinition) {
			if(macroDef.contains("TX_LOW_POWER=0")) {
				txLowPower = true;
			} else if (macroDef.contains("TX_ENABLE_WAIT=1")) {
				txEnableWait = true;
			}
		}
		if (!txLowPower||!txEnableWait) {
			assertFalse(true);
		}
	}
	
	@Test
	public void tc_03_deleteProject() throws Exception{
		bot.button(ProjectParameters.ButtonAction.BUTTON_CANCEL).click();
		Utility.deleteProject(projectModelSpecific.getProjectName(), true);
	}
}
