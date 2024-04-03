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
	public static final String WINDOW_QUESTION = "Question";
	public static final String WINDOW_SAVE_RESOURCES = "Save Resource";
	public static final String WINDOW_COPY_RESOURCES = "Copy Resources";
	public static final String WINDOW_PROGRESS_INFORMATION = "Progress Information";
	public static final String WINDOW_CONFIRM_NEW_FILE = "Confirm New File";
	public static final String WINDOW_BOARD_DATA_FILE_UPDATE = "Board Data File Update";
	public static final String WINDOW_PROJECT_SETTING = "Project Setting";
	public static final String WINDOW_CONFIGURATION_PROBLEMS = "Configuration Problems";
	public static final String SMART_CONFIGURATOR = "Smart Configurator";
	

	public class RTOSType {
		public static final String AZURE = "AzureRTOS";
		public static final String AMAZONFREERTOS = "AmazonFreeRTOS";
		public static final String FREERTOSKERNEL = "FreeRTOSKernel";
		public static final String RI600v4 = "RI600V4";
		public static final String FREERTOSIOTLTS = "IoTLTS";
	}

	public class RTOSDisplay {
		public static final String AZURE = "Azure RTOS";
		public static final String AMAZONFREERTOS = "FreeRTOS (with IoT libraries)(deprecated structure)";
		public static final String FREERTOSKERNEL = "FreeRTOS (kernel only)";
		public static final String RI600v4 = "RI600V4";
		public static final String FREERTOSIOTLTS = "FreeRTOS (with IoT libraries)";
	}

	public class RTOSVersion {
		public static final String Azure_6_2_1 = "6.2.1_rel-rx-2.0.0";
		public static final String Azure_6_2_0 = "6.2.0_rel-rx-1.0.0";
		public static final String Azure_6_1_6 = "6.1.6_rel-rx-1.0.6";
		public static final String RI600_1_06_01 = "1.06.01";
		public static final String Kernel_1_0_7 = "10.4.3-rx-1.0.8";
		public static final String Amazon_202107_1_0_1 = "v202107.00-rx-1.0.1";
		public static final String IoTLTS_202210_1_0_0 = "202210.01-LTS-rx-1.1.3";
	}

	public class RTOSApplication {
		public static final String AZURE_BARE = "bare";
		public static final String AZURE_MINIMAL = "minimal";
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
		public static final String AZURE_USBX_HCDC = "usbx_hcdc";
		public static final String AZURE_LOW_POWER = "low_power";
		public static final String AZURE_IOT_ADU = "iot_adu";
		public static final String AZURE_BOOTLOADER = "bootloader";
		
		public static final String RI600V4 = "RI600v4";
		public static final String AZURE_BARE_CPLUSPLUS = "bare_Cplusplus";
		public static final String KERNEL_BARE = "bare";
		public static final String KERNEL_BARE_CPLUSPLUS = "bare_Cplusplus";
		
		public static final String AMAZON_BARE = "bare";
		public static final String AMAZON_BARE_CPLUSPLUS = "bare_Cplusplus";

		public static final String IOT_LTS_ETHER_PUBSUB = "pubsub_ether";
		public static final String IOT_LTS_ETHER_PUBSUB_CPLUSPLUS = "pubsub_ether_Cplusplus";

		public static final String IOT_LTS_CELL_PUBSUB = "pubsub_cell";
		public static final String IOT_LTS_FLEETPS_ETHER = "fleetps_ether";
		public static final String IOT_LTS_FLEETPS_CELL = "fleetps_cell";
	}

	public class TargetBoard {
		public static final String CUSTOM = "Custom";
		public static final String BOARD_RSK_RX65N_2MB = "RSKRX65N-2MB";
		public static final String BOARD_CK_RX65N = "CK-RX65N";
		public static final String DEVICE_R5F565NCDxBG = "R5F565NCDxBG";
		public static final String BOARD_RSK_RX130 = "RSKRX130";
		public static final String BOARD_RSK_RX231 = "RSKRX231";
		public static final String BOARD_RSK_RX72N = "RSKRX72N";
		public static final String DEVICE_R5F565NEHxLJ_DUAL = "R5F565NEHxLJ_DUAL";
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
		public static final String BUTTON_EXPORT_CONFIG = "Export Configuration";
		public static final String APPLY_AND_CLOSE = "Apply and Close";
		public static final String BUTTON_BROWSE = "Browse...";
		public static final String BUTTON_IMPORT_CONFIG = "Import Configuration";
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
		public static final String CONTEXT_MENU_EXCLUDE = "Exclude";
		public static final String MENU_SAVE_ALL = "Save All";
		public static final String CONTEXT_MENU_INCLUDE = "Include";
		public static final String MEMU_IMPORT = "Import...";
		public static final String MENU_PREFERENCES = "Preferences";
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
		public static final String FILE_TASK_2_C = "task_2.c";
		public static final String FOLDER_APPLICATION_CODE = "application_code";
		public static final String FOLDER_RENESAS_CODE = "renesas_code";
		public static final String FOLDER_CONFIG_FILES = "config_files";
		public static final String FOLDER_GENERAL = "General";
		public static final String RENESAS_GITHUB = "Renesas GitHub FreeRTOS (with IoT libraries) Project";
		public static final String EXISTING_PROJECTS = "Existing Projects into Workspace";
		public static final String FOLDER_RTOS_SETTING = "RTOS Setting";
		public static final String FOLDER_RTOS_GENERIC = "RTOS Generic";
		public static final String FOLDER_DRIVERS = "Drivers";
		public static final String FOLDER_COMMUNICATIONS = "Communications";
		
	}
	
	public class RTOSComponent {
		public static final String FILEX = "filex";
		public static final String NETXDUO = "netxduo";
		public static final String NETXDUO_ADDONS = "netxduo_addons";
		public static final String USBX = "usbx";
		public static final String THREADX = "threadx";
		public static final String AZURERTOS_OBJECT = "azurertos_object";
		public static final String FREERTOS_OBJECT = "FreeRTOS_Object";
		public static final String IOT_LTS_FREERTOS_OBJECT = "FreeRTOS Object";
		public static final String FREERTOS_KERNEL = "FreeRTOS_Kernel";
		public static final String AWS_DEVICE_SHADOW = "AWS_device_shadow";
		public static final String AWS_MQTT = "AWS_mqtt";
		public static final String AWS_GGD = "AWS_ggd";
		public static final String AWS_SECURE_SOCKET = "AWS_secure_socket";
		public static final String AWS_TCP_IP = "AWS_tcp_ip";
		public static final String FREERTOS_APPLICATION = "FreeRTOS_Application";
		public static final String R_ETHER_RX = "r_ether_rx";
	}
	
	public class ProjectSettings {
		public static final String ASSEMBLER = "Assembler";
		public static final String SOURCE = "Source";
		public static final String C_CPLUSPLUS_PROJECT_SETTINGS = "C/C++ Project Settings";
	}

	public class KernelObject {
		public static final String KERNEL_START = "kernel start";
		public static final String TASK_1 = "task_1";
		public static final String NUMBER_512 = "512";
		public static final String NULL = "NULL";
		public static final String NUMBER_1 = "1";
		public static final String TASK_2 = "task_2";
		public static final String TASK_3 = "task_3";
		public static final String TASK_4 = "task_4";
		public static final String EVENT_GRP_HANDLE_1 = "event_grp_handle_1";
		public static final String MSG_BFF_HANDLE_1 = "msg_bff_handle_1";
		public static final String NUMBER_100 = "100";
		public static final String QUEUE_HANDLE_1 = "queue_handle_1";
		public static final String SIZEOF = "sizeof(uint32_t)";
		public static final String SWT_HANDLE_1 = "swt_handle_1";
		public static final String TIMER_1 = "Timer_1";
		public static final String NUMBER_0 = "0";
		public static final String SEMAPHORE_HANDLE_1 = "semaphore_handle_1";
		public static final String BINARY = "binary";
		public static final String STREAM_BFF_HANDLE_1 = "stream_bff_handle_1";
		public static final String NUMBER_10 = "10";
		public static final String MANUAL = "manual";
		public static final Object FALSE = "False";
	}
	
	public class KernelObjectTableColumn {
		public static final String PLUS_MINUS = "+/-";
		public static final String EVENT_GROUP_HANDLER = "Event Group Handler";
		public static final String MSG_BUFFER_HANDLER = "MsgBuffer Handler";
		public static final String MSG_BUFFER_SIZE = "MsgBuffer Size";
		public static final String QUEUE_HANDLER = "Queue Handler";
		public static final String QUEUE_LENGTH = "Queue Length";
		public static final String ITEMS_SIZE = "Items Size";
		public static final String SEMAPHORE_TYPE = "Semaphore Type";
		public static final String SEMAPHORE_HANDLER = "Semaphore Handler";
		public static final String STREAM_BUFFER_HANDLER = "Stream Buffer Handler";
		public static final String STREAM_BUFFER_SIZE = "Stream Buffer Size";
		public static final String TRIGGER_LEVEL = "Trigger Level";
		public static final String SWTIMER_HANDLER = "swTimer Handler";
		public static final String SWTIMER_NAME = "swTimer Name";
		public static final String SWTIMER_PERIOD = "swTimer Period";
		public static final String AUTO_RELOAD = "Auto Reload";
		public static final String SWTIMER_ID = "swTimer ID";
		public static final String CALLBACK_FUNCTION = "Callback Function";
		public static final String INITIALIZE = "Initialize";
		public static final String TASK_CODE = "Task Code";
		public static final String TASK_NAME = "Task Name";
		public static final String STACK_SIZE = "Stack Size";
		public static final String TASK_HANDLER = "Task Handler";
		public static final String PARAMETER = "Parameter";
		public static final String PRIORITY = "Priority";
	}
	
	public class MessageCode {
		public static final String E04050007 = "E04050007: This name exists. Please use another name";
		public static final String E04050004 = "E04050004: The value must not be empty";
		public static final String E04050002 = "E04050002: The value must be a number";
		public static final String E04050001 = "E04050001: The first character must not be a digit";
		public static final String E04050003 = "E04050003: The value must not be a number";
		public static final String E04050006 = "E04050006: The value must be from 1 to 4294967295";
		public static final String E04020001 = "E04020001: Value must be in range 0 ~ configMAX_SYSCALL_INTERRUPT_PRIORITY - 1";
		public static final String E04020001_2 = "E04020001: Value must be in range configKERNEL_INTERRUPT_PRIORITY + 1 ~ configMAX_PRIORITIES - 1";
	}
	
	public class KernelObjectTab{
		public static final String TASKS = "Tasks";
		public static final String MESSAGE_BUFFERS = "Message Buffers";
		public static final String EVENT_GROUPS = "Event Groups";
		public static final String QUEUES = "Queues";
		public static final String SOFTWARE_TIMERS = "Software Timers";
		public static final String SEMAPHORES = "Semaphores";
		public static final String STREAM_BUFFERS = "Stream Buffers";
		public static final String HEAP_ESTIMATION = "Heap Estimation";
	}
	
	public class KernelConfig{
		public static final String CONFIGURATIONS = "Configurations ";
		public static final String MAX_SYSCALL = "Maximum syscall interrupt priority";
		public static final String KERNEL_INTERRUPT_PRIORITY = "Kernel interrupt priority";
		public static final String THE_FREQUENCY_OF_THE_CPU_CLOCK = "The frequency of the CPU clock";
		public static final String THE_FREQUENCY_OF_PHERIPHERAL_CLOCK = "The frequency of the PERIPHERAL clock";
		public static final String THE_DEPTH_ALLOCATE_SW_TIMER_TASK = "The stack depth allocated to the software timer task";
		public static final String BKT_PRIMARY_PRIORITY = "bktPRIMARY_PRIORITY";
		public static final String BKT_SECONDARY_PRIORITY = "bktSECONDARY_PRIORITY";
		public static final String INTQ_HIGHER_PRIORITY = "intqHIGHER_PRIORITY";
		public static final String RTOS_SCHEDULER = "RTOS scheduler ";
		public static final String THE_FREQUENCY_OF_RTOS_TICK_INTERRUPT = "The frequency of the RTOS tick interrupt ";
		public static final String SIZE_OF_STACK_IDLE_TASK = "The size of the stack used by the idle task ";
		public static final String MAXIMUM_PRIORITIES_APPLICATION_TASK = "Maximum number of priorities to the application task";
		public static final String MAXIMUM_PRIORITIES_APPLICATION_CO_ROUTINE = "Maximum number of priorities to the application co-routines";
		public static final String GROUP_AL1_INTERRUPT = "Group AL1 interrupt priority level";
	}

	public class AmazonConfig{

		public static final String REPORT_USAGE = "Report usage metrics to the AWS IoT broker";
		public static final String JSMN_TOKENS = "Number of jsmn tokens";
		public static final String SIZE_ARRAY_FOR_TOKENS = "Size of the array for tokens";
		public static final String DEFAULT_SOCKET_RECEIVE_TIMEOUT = "Default socket receive timeout";
		public static final String BYTE_ORDER = "Byte order of the target MCU";
		public static final String RTOS_SCHEDULER = "RTOS scheduler";
		public static final String MAXIMUM_PRIORITIES_APPLICATION_TASK = "Maximum number of priorities to the application task";
		public static final String THE_FREQUENCY_OF_RTOS_TICK_INTERRUPT = "The frequency of the RTOS tick interrupt ";
		public static final String CONFIGURATIONS = "Configurations ";

	}
	
	public class FileLocation{
		public static final String AZURE_RTOS_LOCATION = "D:\\rtos_package\\Azure_Final_NoChange";
		public static final String FIT_MODULES_LOCATION = "D:\\RDP1.39";
		public static final String NEWEST_FIT_MODULES_LOCATION = "D:\\RDP1.41";
		public static final String KERNEL_RTOS_LOCATION = "D:\\rtos_package\\Kernel";
		public static final String AMAZON_RTOS_LOCATION = "D:\\rtos_package\\IoTLegacyNoChange";
		public static final String EMPTY_RTOS_LOCATION = "D:\\rtos_package\\EmptyFolder";
		public static final String KERNEL_OLD_RTOS_LOCATION = "D:\\rtos_package\\Kernel_For_Regression_Test";
		public static final String KERNEL_EXPORT = "D:\\Export_Regression\\kernel_export.xml";
		public static final String AMAZON_EXPORT = "D:\\Export_Regression\\amazon_export.xml";
		public static final String IOT_EXPORT = "D:\\iot_export\\iot_export.zip";
		public static final String AMAZON_OLD_RTOS_LOCATION = "D:\\rtos_package\\IoT";
		public static final String IOTLTS_EXPORT = "D:\\Export_Regression\\iotlts_export.xml";
		public static final String IOTLTS_RTOS_LOCATION = "D:\\rtos_package\\IoT_LTS_Official_NoChange";
	}
}
