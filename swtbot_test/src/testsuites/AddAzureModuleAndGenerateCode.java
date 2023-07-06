package testsuites;

import static org.junit.Assert.assertFalse;

import java.io.File;
import java.util.Collection;

import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotEditor;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.junit.Test;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.runners.MethodSorters;

import common.Constants;
import common.LogUtil;
import utilities.PGUtility;
import utilities.Utility;

import model.ProjectModel;
import model.RTOSManager;
import parameters.ProjectParameters;
import parameters.ProjectParameters.RTOSApplication;
import parameters.ProjectParameters.RTOSType;
import parameters.ProjectParameters.RTOSVersion;
import parameters.ProjectParameters.TargetBoard;
import platform.PlatformModel;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class AddAzureModuleAndGenerateCode{
	private static SWTWorkbenchBot bot;
	private static ProjectModel projectModelSpecific = new ProjectModel();
	private static final String PLATFORM_XML_FILE = "xml/platformdata.xml";
	private static final String RTOS_PG_XML_FILE = "xml/rtospg.xml";
	
	@BeforeClass
	public static void beforeClass() throws Exception {
		bot = new SWTWorkbenchBot();
		PlatformModel.loadPlatformModel(new File(Utility.getBundlePath(LogUtil.PLUGIN_ID, PLATFORM_XML_FILE)));
		RTOSManager.loadRTOSModel(new File(Utility.getBundlePath(LogUtil.PLUGIN_ID, RTOS_PG_XML_FILE)));
		projectModelSpecific = PGUtility.prepareProjectModel(RTOSType.AZURE, RTOSVersion.Azure_6_2_1, RTOSApplication.AZURE_BARE, Constants.CCRX_TOOLCHAIN, TargetBoard.BOARD_RSK_RX65N_2MB);
	}
	
	@Test
	public void tc_01_CreateThreadxProject() throws Exception{
		PGUtility.createProject(RTOSType.AZURE, RTOSVersion.Azure_6_2_1, RTOSApplication.AZURE_BARE, Constants.CCRX_TOOLCHAIN, TargetBoard.BOARD_RSK_RX65N_2MB);
		
	}
	
	@Test
	public void tc_02_AddComponentFilex() throws Exception{
		Utility.openSCFGEditor(projectModelSpecific, ProjectParameters.SCFG_COMPONENT_TAB);
		Utility.addComponent("filex");
		Utility.clickGenerateCode();
		boolean isFileXInComponentTree = Utility.checkIfComponentExistOrNot(ProjectParameters.RTOSComponent.FILEX);
		if (!isFileXInComponentTree) {
			assertFalse(true);
		}
	}
	@Test
	public void tc_03_AddComponentNetxduo() throws Exception{
		bot.editorByTitle(projectModelSpecific.getProjectName() + ".scfg").setFocus();
		Utility.addComponent("netx");
		Utility.clickGenerateCode();
		boolean isNetXInComponentTree = Utility.checkIfComponentExistOrNot(ProjectParameters.RTOSComponent.NETXDUO);
		if (!isNetXInComponentTree) {
			assertFalse(true);
		}
	}
	@Test
	public void tc_04_AddComponentNetxduoAddons() throws Exception{
		bot.editorByTitle(projectModelSpecific.getProjectName() + ".scfg").setFocus();
		Utility.addComponent("netx duo addons");
		Utility.clickGenerateCode();
		boolean isAddonsInComponentTree = Utility.checkIfComponentExistOrNot(ProjectParameters.RTOSComponent.NETXDUO_ADDONS);
		if (!isAddonsInComponentTree) {
			assertFalse(true);
		}
	}
}
