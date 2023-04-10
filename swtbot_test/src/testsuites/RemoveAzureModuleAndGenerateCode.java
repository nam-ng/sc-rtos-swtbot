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
public class RemoveAzureModuleAndGenerateCode{
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
		Utility.openSCFGEditor(projectModelSpecific);
		Utility.addComponentAndGenerate("filex");
		Utility.getProjectExplorerView().setFocus();
		SWTBotTreeItem project= bot.tree().getTreeItem(projectModelSpecific.getProjectName() + " ["+ projectModelSpecific.getActiveBuildConfiguration() +"]");
		project.getNode(ProjectParameters.FolderAndFile.FOLDER_LIBS).expand();
		project.getNode(ProjectParameters.FolderAndFile.FOLDER_LIBS).getNode(ProjectParameters.RTOSComponent.FILEX).expand();
		SWTBotTreeItem[] items = project.getNode(ProjectParameters.FolderAndFile.FOLDER_LIBS).getNode(ProjectParameters.RTOSComponent.FILEX).getItems();
		boolean isFileExist = false;
		for(SWTBotTreeItem item: items) {
			if(item.getText().contains(ProjectParameters.FolderAndFile.FILE_FX_USER_H)) {
				isFileExist = true;
			}
		}
		if (!isFileExist) {
			assertFalse(true);
		}
	}
	@Test
	public void tc_03_AddComponentNetxduo() throws Exception{
		bot.editorByTitle(projectModelSpecific.getProjectName() + ".scfg").setFocus();
		Utility.addComponentAndGenerate("netx");
		Utility.getProjectExplorerView().setFocus();
		SWTBotTreeItem project = bot.tree().getTreeItem(projectModelSpecific.getProjectName() + " ["+ projectModelSpecific.getActiveBuildConfiguration() +"]");
		project.getNode(ProjectParameters.FolderAndFile.FOLDER_LIBS).expand();
		project.getNode(ProjectParameters.FolderAndFile.FOLDER_LIBS).getNode(ProjectParameters.RTOSComponent.NETXDUO)
				.expand();
		SWTBotTreeItem[] items = project.getNode(ProjectParameters.FolderAndFile.FOLDER_LIBS)
				.getNode(ProjectParameters.RTOSComponent.NETXDUO).getItems();
		boolean isFileExist = false;
		for (SWTBotTreeItem item : items) {
			if (item.getText().contains(ProjectParameters.FolderAndFile.FILE_NX_USER_H)) {
				isFileExist = true;
			}
		}
		if (!isFileExist) {
			assertFalse(true);
		}
	}
	@Test
	public void tc_04_AddComponentNetxduoAddons() throws Exception{
		bot.editorByTitle(projectModelSpecific.getProjectName() + ".scfg").setFocus();
		Utility.addComponentAndGenerate("netx duo addons");
		Utility.getProjectExplorerView().setFocus();
		SWTBotTreeItem project = bot.tree().getTreeItem(projectModelSpecific.getProjectName() + " ["+ projectModelSpecific.getActiveBuildConfiguration() +"]");
		project.getNode(ProjectParameters.FolderAndFile.FOLDER_LIBS).expand();
		project.getNode(ProjectParameters.FolderAndFile.FOLDER_LIBS)
				.getNode(ProjectParameters.RTOSComponent.NETXDUO_ADDONS).expand();
		project.getNode(ProjectParameters.FolderAndFile.FOLDER_LIBS)
				.getNode(ProjectParameters.RTOSComponent.NETXDUO_ADDONS)
				.getNode(ProjectParameters.FolderAndFile.FOLDER_ADDONS).expand();
		project.getNode(ProjectParameters.FolderAndFile.FOLDER_LIBS)
				.getNode(ProjectParameters.RTOSComponent.NETXDUO_ADDONS)
				.getNode(ProjectParameters.FolderAndFile.FOLDER_ADDONS)
				.getNode(ProjectParameters.FolderAndFile.FOLDER_DHCP).expand();
		SWTBotTreeItem[] items = project.getNode(ProjectParameters.FolderAndFile.FOLDER_LIBS)
				.getNode(ProjectParameters.RTOSComponent.NETXDUO_ADDONS)
				.getNode(ProjectParameters.FolderAndFile.FOLDER_ADDONS)
				.getNode(ProjectParameters.FolderAndFile.FOLDER_DHCP).getItems();
		boolean isFileExist = false;
		for (SWTBotTreeItem item : items) {
			if (item.getText().contains(ProjectParameters.FolderAndFile.FILE_NXD_DHCP_CLIENT_H)) {
				isFileExist = true;
			}
		}
		if (!isFileExist) {
			assertFalse(true);
		}
	}
	@Test
	public void tc_05_RemoveComponentFilex() throws Exception{
		bot.editorByTitle(projectModelSpecific.getProjectName() + ".scfg").setFocus();
		Utility.removeComponentAndGenerate(ProjectParameters.RTOSComponent.FILEX);
		Utility.openProjectExplorer();
		bot.tree().getTreeItem(projectModelSpecific.getProjectName()).select();
		SWTBotTreeItem project= bot.tree().getTreeItem(projectModelSpecific.getProjectName() + " ["+ projectModelSpecific.getActiveBuildConfiguration() +"]");
		project.getNode(ProjectParameters.FolderAndFile.FOLDER_LIBS).expand();
		List<String> folderLibsItem = project.getNode(ProjectParameters.FolderAndFile.FOLDER_LIBS).getNodes();
		boolean isFileExist = false;
		for (String libsItem : folderLibsItem) {
			if (libsItem.equals(ProjectParameters.RTOSComponent.FILEX)) {
				project.getNode(ProjectParameters.FolderAndFile.FOLDER_LIBS)
						.getNode(ProjectParameters.RTOSComponent.FILEX).expand();
				SWTBotTreeItem[] items = project.getNode(ProjectParameters.FolderAndFile.FOLDER_LIBS)
						.getNode(ProjectParameters.RTOSComponent.FILEX).getItems();
				for (SWTBotTreeItem item : items) {
					if (item.getText().contains(ProjectParameters.FolderAndFile.FILE_FX_USER_H)) {
						isFileExist = true;
					}
				}
			}
		}
		if (isFileExist) {
			assertFalse(true);
		}
	}
	
	@Test
	public void tc_06_RemoveComponentNetxduoAddons() throws Exception{
		bot.editorByTitle(projectModelSpecific.getProjectName() + ".scfg").setFocus();
		Utility.removeComponentAndGenerate(ProjectParameters.RTOSComponent.NETXDUO_ADDONS);
		Utility.openProjectExplorer();
		bot.tree().getTreeItem(projectModelSpecific.getProjectName()).select();
		SWTBotTreeItem project= bot.tree().getTreeItem(projectModelSpecific.getProjectName() + " ["+ projectModelSpecific.getActiveBuildConfiguration() +"]");
		project.getNode(ProjectParameters.FolderAndFile.FOLDER_LIBS).expand();
		List<String> folderLibsItem = project.getNode(ProjectParameters.FolderAndFile.FOLDER_LIBS).getNodes();
		boolean isFileExist = false;
		for (String libsItem : folderLibsItem) {
			if (libsItem.equals(ProjectParameters.RTOSComponent.NETXDUO_ADDONS)) {
				project.getNode(ProjectParameters.FolderAndFile.FOLDER_LIBS)
						.getNode(ProjectParameters.RTOSComponent.NETXDUO_ADDONS).expand();
				SWTBotTreeItem[] folderAddonitems = project.getNode(ProjectParameters.FolderAndFile.FOLDER_LIBS)
						.getNode(ProjectParameters.RTOSComponent.NETXDUO_ADDONS).getItems();
				for (SWTBotTreeItem addonItem : folderAddonitems) {
					if (addonItem.getText().contains(ProjectParameters.FolderAndFile.FOLDER_ADDONS)) {
						project.getNode(ProjectParameters.FolderAndFile.FOLDER_LIBS)
								.getNode(ProjectParameters.RTOSComponent.NETXDUO_ADDONS)
								.getNode(ProjectParameters.FolderAndFile.FOLDER_ADDONS).expand();
						SWTBotTreeItem[] folderDhcpItems = project.getNode(ProjectParameters.FolderAndFile.FOLDER_LIBS)
								.getNode(ProjectParameters.RTOSComponent.NETXDUO_ADDONS)
								.getNode(ProjectParameters.FolderAndFile.FOLDER_ADDONS).getItems();
						for (SWTBotTreeItem dhcpItem : folderDhcpItems) {
							if (addonItem.getText().contains(ProjectParameters.FolderAndFile.FOLDER_DHCP)) {
								project.getNode(ProjectParameters.FolderAndFile.FOLDER_LIBS)
										.getNode(ProjectParameters.RTOSComponent.NETXDUO_ADDONS)
										.getNode(ProjectParameters.FolderAndFile.FOLDER_ADDONS)
										.getNode(ProjectParameters.FolderAndFile.FOLDER_DHCP).expand();
								SWTBotTreeItem[] items = project.getNode(ProjectParameters.FolderAndFile.FOLDER_LIBS)
										.getNode(ProjectParameters.RTOSComponent.NETXDUO_ADDONS)
										.getNode(ProjectParameters.FolderAndFile.FOLDER_ADDONS)
										.getNode(ProjectParameters.FolderAndFile.FOLDER_DHCP).getItems();
								for (SWTBotTreeItem item : items) {
									if (item.getText().contains(ProjectParameters.FolderAndFile.FILE_NXD_DHCP_CLIENT_H)) {
										isFileExist = true;
									}
								}
							}
						}
					}
				}
			}
		}
		if (isFileExist) {
			assertFalse(true);
		}
	}
	
	@Test
	public void tc_07_RemoveComponentNetxduo() throws Exception{
		bot.editorByTitle(projectModelSpecific.getProjectName() + ".scfg").setFocus();
		Utility.removeComponentAndGenerate(ProjectParameters.RTOSComponent.NETXDUO);
		Utility.openProjectExplorer();
		bot.tree().getTreeItem(projectModelSpecific.getProjectName()).select();
		SWTBotTreeItem project= bot.tree().getTreeItem(projectModelSpecific.getProjectName() + " ["+ projectModelSpecific.getActiveBuildConfiguration() +"]");
		project.getNode(ProjectParameters.FolderAndFile.FOLDER_LIBS).expand();
		List<String> folderLibsItem = project.getNode(ProjectParameters.FolderAndFile.FOLDER_LIBS).getNodes();
		boolean isFileExist = false;
		for (String libsItem : folderLibsItem) {
			if (libsItem.equals(ProjectParameters.RTOSComponent.NETXDUO)) {
				project.getNode(ProjectParameters.FolderAndFile.FOLDER_LIBS)
						.getNode(ProjectParameters.RTOSComponent.NETXDUO).expand();
				SWTBotTreeItem[] items = project.getNode(ProjectParameters.FolderAndFile.FOLDER_LIBS)
						.getNode(ProjectParameters.RTOSComponent.NETXDUO).getItems();
				for (SWTBotTreeItem item : items) {
					if (item.getText().contains(ProjectParameters.FolderAndFile.FILE_NX_USER_H)) {
						isFileExist = true;
					}
				}
			}
		}
		bot.text().setText("");
		if (isFileExist) {
			assertFalse(true);
		}
	}
}
