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
		projectModelSpecific = PGUtility.prepareProjectModel(RTOSType.AZURE, RTOSVersion.Azure_6_2_1, RTOSApplication.AZURE_USBX_CDC, Constants.CCRX_TOOLCHAIN, TargetBoard.BOARD_CK_RX65N);
	}
	
	@Test
	public void tc_01_CreateUsbxProject() throws Exception{
		PGUtility.createProject(RTOSType.AZURE, RTOSVersion.Azure_6_2_1, RTOSApplication.AZURE_USBX_CDC, Constants.CCRX_TOOLCHAIN, TargetBoard.BOARD_CK_RX65N);
	}
	
	@Test
	public void tc_02_ChangeRTOSVersion() throws Exception{
		Utility.openSCFGEditor(projectModelSpecific);
		bot.text().setText(ProjectParameters.RTOSComponent.USBX);
		bot.tree(1).getTreeItem(ProjectParameters.FolderAndFile.FOLDER_RTOS).getNode(ProjectParameters.FolderAndFile.FOLDER_RTOS_LIBRARY).getNode(ProjectParameters.RTOSComponent.USBX).contextMenu(ProjectParameters.MenuName.MENU_CHANGE_VERSION).click();
		bot.comboBoxWithLabel(ProjectParameters.LabelName.LABEL_AVAILABLE_VERSION).setSelection(ProjectParameters.RTOSVersion.Azure_6_1_6);
		bot.button(ProjectParameters.ButtonAction.BUTTON_NEXT).click();
		SWTBotTreeItem[] allItems= bot.tree().getAllItems();
		boolean isUsbXShowInTable = false;
		for (SWTBotTreeItem item : allItems) {
			if (item.cell(0).contains("usbx configurations") && item.cell(1).contains("Removed")) {
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
		bot.sleep(25000);
		boolean isUsbXInComponentTree = Utility.checkIfComponentExistOrNot(ProjectParameters.RTOSComponent.USBX);
		Utility.addComponent(ProjectParameters.RTOSComponent.FILEX);
		bot.text().setText(ProjectParameters.RTOSComponent.FILEX);
		bot.tree(1).getTreeItem(ProjectParameters.FolderAndFile.FOLDER_RTOS)
				.getNode(ProjectParameters.FolderAndFile.FOLDER_RTOS_LIBRARY).getNode(ProjectParameters.RTOSComponent.FILEX).contextMenu(ProjectParameters.MenuName.MENU_CHANGE_VERSION).click();
		String currentVersion = bot.textWithLabel(ProjectParameters.LabelName.LABEL_CURRENT_VERSION).getText();
		bot.button(ProjectParameters.ButtonAction.BUTTON_CANCEL).click();
		boolean isVersionChange = currentVersion.contains(ProjectParameters.RTOSVersion.Azure_6_1_6);
		Utility.openProjectExplorer();
		if (!isUsbXShowInTable || isUsbXInComponentTree || !isVersionChange) {
			assertFalse(true);
		}
	}
}
