package parameters;

import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;

public class ProjectParameters {
	public static final String OPEN_ASSOCIATED_PERSPECTIVE = "Open Associated Perspective?";
	public static final String OPEN_PERSPECTIVE = "Open Perspective";
	public static final String TOOL_CHAIN_VERSION_CCRX = "v3.01.00";
	public static final String PROJECT_NAME = "ProjectTesting";
	public static final String EDITOR_PROJECT_NAME = "ProjectTesting.scfg";
	public static final String PROJECT_NAME_HARDWARE_DEBUG = "ProjectTesting [HardwareDebug]";
	public static final String VIEW_NAME_PROJECT_EXPLORER = "Project Explorer";
	public static final String VIEW_TITLE_WELCOME = "Welcome";
	public static final String WINDOW_OPEN_ASSOCIATED_PERSPECTIVE = "Open Associated Perspective?";
	public static final String WINDOW_MARKETPLACE = "Editors available on the Marketplace";
	public static final String WINDOW_PROJECT_EXPLORER = "Project Explorer";
	public static final String WINDOW_DELETE_RESOURCES = "Delete Resources";
	public static final String WINDOW_INSTALL = "Install";
	public static final String WINDOW_BOARD_DATA = "Board Data File Update";
	public static final String WINDOW_FIT = "RX Driver Package Download";
	public static final String WINDOW_NEW_COMPONENT = "New Component";
	public static final String BUILD_SUCCESSFULLY = "Build Finished. 0 errors";
	public static final String BUILD_FINISH = "Build Finished";
	public static final String BUILD_FAILED = "Build Failed";
	public static final String CHECKBOX_DELETE_PROJECT_CONTENTS_ON_DISK = "Delete project contents on disk (cannot be undone)";
	public static final String CODE_GENERATING = "Code Generating";
	public static final String WINDOW_CHANGE_VERSION = "Change Version";
	public static final String SCFG_COMPONENT_TAB = "Components";
	public static final String GENERAL = "General";
	public static final CharSequence WINDOW_QUESTION = "Question";
	public static final Object WINDOW_SAVE_RESOURCES = "Save Resource";
	

	public class RTOSType {
		public static final String AZURE = "AzureRTOS";
		public static final String AMAZONFREERTOS = "AmazonFreeRTOS";
		public static final String FREERTOSKERNEL = "FreeRTOSKernel";
		public static final String RI600v4 = "RI600V4";
	}

	public class RTOSDisplay {
		public static final String AZURE = "Azure RTOS";
		public static final String AMAZONFREERTOS = "FreeRTOS (with IoT libraries)";
		public static final String FREERTOSKERNEL = "FreeRTOS (kernel only)";
		public static final String RI600v4 = "RI600V4";
	}

	public class RTOSVersion {
		public static final String Azure_6_2_1 = "6.2.1_rel-rx-1.1.0";
		public static final String Azure_6_2_0 = "6.2.0_rel-rx-1.0.0";
		public static final String Azure_6_1_6 = "6.1.6_rel-rx-1.0.6";
		public static final String RI600_1_06_01 = "1.06.01";
		public static final String Kernel_1_0_7 = "10.4.3-rx-1.0.7";
	}

	public class RTOSApplication {
		public static final String AZURE_BARE = "bare";
		public static final String AZURE_RAM = "ramdisk";
		public static final String AZURE_PING = "ping";
		public static final String AZURE_IPERF = "iperf";
		public static final String AZURE_IOT_SDK = "iot_sdk";
		public static final String AZURE_IOT_SDK_EWF = "iot_sdk_ewf";
		public static final String AZURE_IOT_PNP = "iot_sdk_pnp";
		public static final String AZURE_IOT_PNP_EWF = "iot_sdk_pnp_ewf";
		public static final String AZURE_GUIX_8BPP = "guix_8bpp";
		public static final String AZURE_GUIX_16BPP = "guix_16bpp";
		public static final String AZURE_GUIX_16BPP_DRAW = "guix_16bpp_draw";
		public static final String AZURE_USBX_CDC = "usbx_cdc";
		public static final String AZURE_USBX_HMSC = "usbx_hmsc";
		public static final String AZURE_LOW_POWER = "low_power";
		public static final String AZURE_IOT_ADU = "iot_adu";
		public static final String AZURE_BOOTLOADER = "bootloader";
		
		public static final String RI600V4 = "RI600v4";
		public static final String AZURE_BARE_CPLUSPLUS = "bare_Cplusplus";
		public static final String KERNEL_BARE = "bare";
		public static final String KERNEL_BARE_CPLUSPLUS = "bare_Cplusplus";
	}

	public class TargetBoard {
		public static final String CUSTOM = "Custom";
		public static final String BOARD_RSK_RX65N_2MB = "RSKRX65N-2MB";
		public static final String BOARD_CK_RX65N = "CK-RX65N";
		public static final String DEVICE_R5F565NCDxBG = "R5F565NCDxBG";
	}

	public class ToolchainType {
		public static final String CCRX = "Renesas CCRX";
		public static final String GCCFORRENESASRX = "GCC For Renesas RX";
		public static final String GCC_TOOLCHAIN = "GCC";
		public static final String CCRX_TOOLCHAIN = "CCRX";
	}

	public class ProjectKind {
		public static final String EXECUTABLE = "Executable";
	}

	public class BuildType {
		public static final String HARDWARE = "HardwareDebug";
		public static final String DEBUG = "Debug";
		public static final String RELEASE = "Release";
	}

	public class ButtonAction {
		public static final String BUTTON_OPEN = "Open";
		public static final String BUTTON_OPEN_PERSPECTIVE = "Open Perspective";
		public static final String BUTTON_NO = "No";
		public static final String BUTTON_YES = "Yes";
		public static final String BUTTON_DOWNLOAD = "Download";
		public static final String BUTTON_ACCEPT = "Accept";
		public static final String BUTTON_NEXT = "Next >";
		public static final String BUTTON_FINISH = "Finish";
		public static final String BUTTON_OK = "OK";
		public static final String BUTTON_CANCEL = "Cancel";
		public static final String BUTTON_SELECT_ALL = "Select All";
		public static final String BUTTON_GENERATE_CODE = "Generate Code";
		public static final String BUTTON_PROCEED = "Proceed";
		public static final String BUTTON_ADD_COMPONENT = "Add component";
		public static final String BUTTON_REMOVE_COMPONENT = "Remove component";
		public static final String BUTTON_DONT_SAVE = "Don't Save";
		public static final String BUTTON_CLEAR_CONSOLE = "Clear Console";
	}

	public class LabelName {
		public static final String LABEL_PROJECT_NAME = "&Project name:";
		public static final String LABEL_TOOLCHAIN_VERSION = "&Toolchain Version:";
		public static final String LABEL_RTOS = "&RTOS:";
		public static final String LABEL_RTOS_VERSION = "&RTOS Version:";
		public static final String LABEL_TARGET_BOARD = "&Target Board:";
		public static final String LABEL_TARGET_DEVICE = "&Target Device:";
		public static final String LABEL_FILTER = "Filter";
		public static final String LABEL_AVAILABLE_VERSION = "&Available versions:";
		public static final String LABEL_CURRENT_VERSION = "&Current version:";
	}

	public class MenuName {
		public static final String MENU_WINDOW = "Window";
		public static final String MENU_SHOW_VIEW = "Show View";
		public static final String MENU_OTHER = "Other...";
		public static final String MENU_PROJECT = "Project";
		public static final String MENU_BUILD_PROJECT = "Build Project";
		public static final String MENU_BUILD_ALL = "Build All";
		public static final String CONTEXT_MENU_DELETE = "Delete";
		public static final String CONTEXT_MENU_PROPERTIES = "Properties";
		public static final String CONTEXT_MENU_CLOSE_PROJECT = "Close Project";
		public static final String MENU_FILE = "File";
		public static final String MENU_NEW = "New";
		public static final String MENU_C_CPP_PROJECT = "Renesas C/C++ Project";
		public static final String MENU_RENESAS_RX = "Renesas RX";
		public static final String MENU_CHANGE_VERSION= "Change version...";
		public static final String CONTEXT_MENU_OPEN_PROJECT = "Open Project";
		public static final String MENU_SAVE = "Save";
	}
	
	public class FolderAndFile {
		public static final String FOLDER_RTOS = "RTOS";
		public static final String FOLDER_RTOS_LIBRARY = "RTOS Library";
		public static final String FOLDER_DHCP = "dhcp";
		public static final String FOLDER_LIBS = "libs";
		public static final String FOLDER_ADDONS = "addons";
		public static final String FILE_FX_USER_H = "fx_user.h";
		public static final String FILE_NX_USER_H = "nx_user.h";
		public static final String FILE_NXD_DHCP_CLIENT_H = "nxd_dhcp_client.h";
		public static final String FOLDER_RTOS_KERNEL = "RTOS Kernel";
		public static final String FOLDER_RTOS_OBJECT = "RTOS Object";
		public static final String FILE_FREERTOSCONFIG_H = "FreeRTOSConfig.h";
		public static final String FOLDER_SRC = "src";
		public static final String FOLDER_FRTOS_CONFIG = "frtos_config";
		public static final String FILE_FREERTOS_OBJECT_INIT_C = "freertos_object_init.c";
		public static final String FOLDER_FRTOS_STARTUP = "frtos_startup";
		public static final String FOLDER_FRTOS_SKELETON = "frtos_skeleton";
		public static final String FILE_TASK_1_C = "task_1.c";
	}
	
	public class RTOSComponent {
		public static final String FILEX = "filex";
		public static final String NETXDUO = "netxduo";
		public static final String NETXDUO_ADDONS = "netxduo_addons";
		public static final String USBX = "usbx";
		public static final String THREADX = "threadx";
		public static final String AZURERTOS_OBJECT = "azurertos_object";
		public static final String FREERTOS_OBJECT = "FreeRTOS_Object";
		public static final String FREERTOS_KERNEL = "FreeRTOS_Kernel";
	}
	
	public class ProjectSettings {
		public static final String ASSEMBLER = "Assembler";
		public static final String SOURCE = "Source";
		public static final String C_CPLUSPLUS_PROJECT_SETTINGS = "C/C++ Project Settings";
	}
}
