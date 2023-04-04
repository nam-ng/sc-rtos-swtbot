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

import common.LogUtil;
import utilities.PGUtility;
import utilities.Utility;

import model.ProjectModel;
import model.RTOSManager;
import parameters.ProjectParameters;
import parameters.ProjectParameters.RTOSType;
import parameters.ProjectParameters.RTOSVersion;
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
		Collection<ProjectModel> list = PGUtility.prepareProjectModel(RTOSType.AZURE, RTOSVersion.Azure_6_2_1,"addAzureModuleAndGenCode");
		if(list.size()==1) {
			for(ProjectModel model:list) {
				projectModelSpecific=model;
			}
		}
	}
	
	@Test
	public void tc_01_CreateThreadxProject() throws Exception{
		PGUtility.createProject(RTOSType.AZURE, RTOSVersion.Azure_6_2_1,"addAzureModuleAndGenCode");
		
	}
	
	@Test
	public void tc_02_AddComponentFilex() throws Exception{
		Utility.getProjectExplorerView().setFocus();
		bot.tree().getTreeItem(projectModelSpecific.getProjectName() + " [HardwareDebug]").expand();
		bot.tree().getTreeItem(projectModelSpecific.getProjectName() + " [HardwareDebug]").getNode(projectModelSpecific.getProjectName()+".scfg").doubleClick();
		SWTBotEditor scfgEditor = bot.editorByTitle(projectModelSpecific.getProjectName()+".scfg");
		scfgEditor.setFocus();
		bot.cTabItem("Components").activate();
		bot.toolbarButtonWithTooltip("Add component").click();
		bot.shell("New Component").setFocus();
		bot.textWithLabel("Filter").setText("filex");
		bot.table().select(0);
		bot.button(ProjectParameters.ButtonAction.BUTTON_FINISH).click();
		bot.toolbarButton(ProjectParameters.ButtonAction.BUTTON_GENERATE_CODE).click();
		if (bot.activeShell().getText().contains(ProjectParameters.CODE_GENERATING)) {
			bot.button(ProjectParameters.ButtonAction.BUTTON_PROCEED).click();
		}
		bot.sleep(15000);
		Utility.getProjectExplorerView().setFocus();
		SWTBotTreeItem project= bot.tree().getTreeItem(projectModelSpecific.getProjectName() + " [HardwareDebug]");
		project.getNode("libs").expand();
		project.getNode("libs").getNode("filex").expand();
		SWTBotTreeItem[] items = project.getNode("libs").getNode("filex").getItems();
		boolean isFileExist = false;
		for(SWTBotTreeItem item: items) {
			if(item.getText().contains("fx_user.h")) {
				isFileExist = true;
			}
		}
		if (!isFileExist) {
			assertFalse(true);
		}
	}
	@Test
	public void tc_03_AddComponentNetxduo() throws Exception{
		SWTBotEditor scfgEditor = bot.editorByTitle(projectModelSpecific.getProjectName()+".scfg");
		scfgEditor.setFocus();
		bot.cTabItem("Components").activate();
		bot.toolbarButtonWithTooltip("Add component").click();
		bot.shell("New Component").setFocus();
		bot.textWithLabel("Filter").setText("netx");
		bot.table().select(0);
		bot.button(ProjectParameters.ButtonAction.BUTTON_FINISH).click();
		bot.toolbarButton(ProjectParameters.ButtonAction.BUTTON_GENERATE_CODE).click();
		if (bot.activeShell().getText().contains(ProjectParameters.CODE_GENERATING)) {
			bot.button(ProjectParameters.ButtonAction.BUTTON_PROCEED).click();
		}
		bot.sleep(15000);
		Utility.getProjectExplorerView().setFocus();
		SWTBotTreeItem project= bot.tree().getTreeItem(projectModelSpecific.getProjectName() + " [HardwareDebug]");
		project.getNode("libs").expand();
		project.getNode("libs").getNode("netxduo").expand();
		SWTBotTreeItem[] items = project.getNode("libs").getNode("netxduo").getItems();
		boolean isFileExist = false;
		for(SWTBotTreeItem item: items) {
			if(item.getText().contains("nx_user.h")) {
				isFileExist = true;
			}
		}
		if (!isFileExist) {
			assertFalse(true);
		}
	}
	@Test
	public void tc_04_AddComponentNetxduoAddons() throws Exception{
		SWTBotEditor scfgEditor = bot.editorByTitle(projectModelSpecific.getProjectName()+".scfg");
		scfgEditor.setFocus();
		bot.cTabItem("Components").activate();
		bot.toolbarButtonWithTooltip("Add component").click();
		bot.shell("New Component").setFocus();
		bot.textWithLabel("Filter").setText("netx");
		bot.table().select(1);
		bot.button(ProjectParameters.ButtonAction.BUTTON_FINISH).click();
		bot.toolbarButton(ProjectParameters.ButtonAction.BUTTON_GENERATE_CODE).click();
		if (bot.activeShell().getText().contains(ProjectParameters.CODE_GENERATING)) {
			bot.button(ProjectParameters.ButtonAction.BUTTON_PROCEED).click();
		}
		bot.sleep(15000);
		Utility.getProjectExplorerView().setFocus();
		SWTBotTreeItem project= bot.tree().getTreeItem(projectModelSpecific.getProjectName() + " [HardwareDebug]");
		project.getNode("libs").expand();
		project.getNode("libs").getNode("netxduo_addons").expand();
		project.getNode("libs").getNode("netxduo_addons").getNode("addons").expand();
		project.getNode("libs").getNode("netxduo_addons").getNode("addons").getNode("dhcp").expand();
		SWTBotTreeItem[] items = project.getNode("libs").getNode("netxduo_addons").getNode("addons").getNode("dhcp").getItems();
		boolean isFileExist = false;
		for(SWTBotTreeItem item: items) {
			if(item.getText().contains("nxd_dhcp_client.h")) {
				isFileExist = true;
			}
		}
		if (!isFileExist) {
			assertFalse(true);
		}
	}
}
