package testsuites;

import static org.junit.Assert.assertFalse;

import java.io.File;
import java.util.Collection;
import java.util.List;

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
public class ChangeRTOSVersion {
	private static SWTWorkbenchBot bot;
	private static ProjectModel projectModelSpecific = new ProjectModel();
	private static final String PLATFORM_XML_FILE = "xml/platformdata.xml";
	private static final String RTOS_PG_XML_FILE = "xml/rtospg.xml";
	
	@BeforeClass
	public static void beforeClass() throws Exception {
		bot = new SWTWorkbenchBot();
		PlatformModel.loadPlatformModel(new File(Utility.getBundlePath(LogUtil.PLUGIN_ID, PLATFORM_XML_FILE)));
		RTOSManager.loadRTOSModel(new File(Utility.getBundlePath(LogUtil.PLUGIN_ID, RTOS_PG_XML_FILE)));
		Collection<ProjectModel> list = PGUtility.prepareProjectModel(RTOSType.AZURE, RTOSVersion.Azure_6_2_1,"changeRTOSVersion");
		if(list.size()==1) {
			for(ProjectModel model:list) {
				projectModelSpecific=model;
			}
		}
	}
	
	@Test
	public void tc_01_CreateUsbxProject() throws Exception{
		PGUtility.createProject(RTOSType.AZURE, RTOSVersion.Azure_6_2_1,"changeRTOSVersion");
		
	}
	
	@Test
	public void tc_02_ChangeRTOSVersion() throws Exception{
		Utility.getProjectExplorerView().setFocus();
		bot.tree().getTreeItem(projectModelSpecific.getProjectName() + " [HardwareDebug]").expand();
		bot.tree().getTreeItem(projectModelSpecific.getProjectName() + " [HardwareDebug]").getNode(projectModelSpecific.getProjectName()+".scfg").doubleClick();
		SWTBotEditor scfgEditor = bot.editorByTitle(projectModelSpecific.getProjectName()+".scfg");
		scfgEditor.setFocus();
		bot.cTabItem("Components").activate();
		scfgEditor.setFocus();
		Utility.getProjectExplorerView().close();
		bot.text().setText("usbx");
		bot.tree().getTreeItem("RTOS").getNode("RTOS Library").getNode("usbx").contextMenu("Change version...").click();
		bot.comboBoxWithLabel("&Available versions:").setSelection(ProjectParameters.RTOSVersion.Azure_6_1_6);
		bot.button(ProjectParameters.ButtonAction.BUTTON_NEXT).click();
		SWTBotTreeItem[] allItems= bot.tree().getAllItems();
		boolean isUsbXShowInTable = false;
		for(SWTBotTreeItem item:allItems) {
			if (item.getText().contains("usbx configurations")) {
				isUsbXShowInTable = true;
			}
		}
		bot.button(ProjectParameters.ButtonAction.BUTTON_FINISH).click();
		if (bot.activeShell().getText().contains(ProjectParameters.WINDOW_CHANGE_VERSION)) {
			bot.button(ProjectParameters.ButtonAction.BUTTON_YES).click();
		}
		if (bot.activeShell().getText().contains(ProjectParameters.CODE_GENERATING)) {
			bot.button(ProjectParameters.ButtonAction.BUTTON_PROCEED).click();
		}
		bot.sleep(15000);
		boolean isUsbXInComponentTree = false;
		bot.text().setText("");
		bot.tree().getTreeItem("RTOS").expand();
		List<String> RTOSFolder = bot.tree().getTreeItem("RTOS").getNodes();
		for(String folder:RTOSFolder) {
			if (folder.contains("RTOS Library")) {
				SWTBotTreeItem[] componentItems = bot.tree().getTreeItem("RTOS").getNode("RTOS Library").getItems();
				for (SWTBotTreeItem currentItem: componentItems) {
					if (currentItem.getText().contains("usbx")) {
						isUsbXInComponentTree = true;
					}
				}
			}
		}
		bot.menu(ProjectParameters.MenuName.MENU_WINDOW).menu(ProjectParameters.MenuName.MENU_SHOW_VIEW)
				.menu(ProjectParameters.MenuName.MENU_OTHER).click();
		bot.text().setText(ProjectParameters.WINDOW_PROJECT_EXPLORER);
		SWTBotTreeItem treeItem = bot.tree().getTreeItem("General");
		bot.waitUntil(org.eclipse.swtbot.swt.finder.waits.Conditions.treeItemHasNode(treeItem,
				ProjectParameters.WINDOW_PROJECT_EXPLORER));
		treeItem.getNode(ProjectParameters.WINDOW_PROJECT_EXPLORER).select();
		bot.button(ProjectParameters.ButtonAction.BUTTON_OPEN).click();
		if (!isUsbXShowInTable || isUsbXInComponentTree) {
			assertFalse(true);
		}
	}
}
