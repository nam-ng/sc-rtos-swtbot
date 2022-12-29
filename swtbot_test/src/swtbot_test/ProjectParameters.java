package swtbot_test;

public class ProjectParameters {
<<<<<<< HEAD
	protected static final String OPEN_ASSOCIATED_PERSPECTIVE = "Open Associated Perspective?";
	protected static final String OPEN_PERSPECTIVE = "Open Perspective";
	protected static final String TOOL_CHAIN_VERSION_CCRX = "v3.01.00";
	protected static final String PROJECT_NAME = "ProjectTesting";
	protected static final String LABEL_PROJECT_NAME = "&Project name:";
	protected static final String LABEL_RTOS = "&RTOS:";
	protected static final String LABEL_RTOS_VERSION = "&RTOS Version:";
	protected static final String LABEL_TARGET_BOARD = "&Target Board:";
	protected static final String LABEL_TARGET_DEVICE = "&Target Device:";
	protected static final String EDITOR_PROJECT_NAME = "ProjectTesting.scfg";
	protected static final String PROJECT_NAME_HARDWARE_DEBUG = "ProjectTesting [HardwareDebug]";
	protected static final String VIEW_NAME_PROJECT_EXPLORER = "Project Explorer";
	protected static final String VIEW_TITLE_WELCOME = "Welcome";
	protected static final String MENU_WINDOW = "Window";
	protected static final String MENU_SHOW_VIEW = "Show View";
	protected static final String MENU_OTHER = "Other...";
	protected static final String MENU_BUILD_PROJECT = "Build Project";
	protected static final String CONTEXT_MENU_DELETE = "Delete";
	protected static final String BUTTON_OPEN = "Open";
	protected static final String BUTTON_OPEN_PERSPECTIVE = "Open Perspective";
	protected static final String BUTTON_NO = "No";
	protected static final String BUTTON_YES = "Yes";
	protected static final String BUTTON_DOWNLOAD = "Download";
	protected static final String BUTTON_ACCEPT = "Accept";
	protected static final String BUTTON_NEXT = "Next >";
	protected static final String BUTTON_FINISH = "Finish";
	protected static final String BUTTON_OK = "OK";
	protected static final String BUTTON_CANCEL = "Cancel";
	protected static final String BUTTON_SELECT_ALL = "Select All";
	protected static final String BUTTON_GENERATE_CODE = "Generate Code";
	protected static final String MENU_FILE = "File";
	protected static final String MENU_NEW = "New";
	protected static final String MENU_C_CPP_PROJECT = "Renesas C/C++ Project";
	protected static final String MENU_RENESAS_RX = "Renesas RX";
	protected static final String WINDOW_OPEN_ASSOCIATED_PERSPECTIVE = "Open Associated Perspective?";
	protected static final String WINDOW_MARKETPLACE = "Editors available on the Marketplace";
	protected static final String WINDOW_PROJECT_EXPLORER = "Project Explorer";
	protected static final String WINDOW_DELETE_RESOURCES = "Delete Resources";
	protected static final String WINDOW_INSTALL = "Install";
	protected static final String WINDOW_BOARD_DATA = "Board Data File Update";
	protected static final String WINDOW_FIT = "RX Driver Package Download";
	protected static final String BUILD_SUCCESSFULLY = "Build Finished. 0 errors";
	protected static final String BUILD_FINISH = "Build Finished";
	protected static final String BUILD_FAILED = "Build Failed";
	protected static final String CHECKBOX_DELETE_PROJECT_CONTENTS_ON_DISK = "Delete project contents on disk (cannot be undone)";
	protected static final String CODE_GENERATING = "Code Generating";
	protected static final String BUTTON_PROCEED = "Proceed";

	protected class DeviceFamily {
		protected static final String RX = "Renesas RX";
	}

	protected class DeviceName {
		/**
		 * Devices RX
		 */
		protected static final String RX651 = "R5F5651EHxLC_DUAL";
	}

	protected class RTOSType {
		protected static final String AZURE = "Azure RTOS";
	}

	protected class RTOSVersion {
		protected static final String NEWEST = "6.2.0_rel-rx-1.0.0";
	}

	protected class TargetBoard {
		protected static final String CUSTOM = "Custom";
	}

	protected class ToolchainType {
		protected static final String CCRX = "Renesas CCRX";
		protected static final String GCCFORRENESASRX = "GCC For Renesas RX";
	}

	protected class ProjectKind {
		protected static final String EXECUTABLE = "Executable";
	}

	protected class BuildType {
		protected static final String HARDWARE = "Hardware";
		protected static final String DEBUG = "Debug";
		protected static final String RELEASE = "Release";
=======
	public static final String OPEN_ASSOCIATED_PERSPECTIVE = "Open Associated Perspective?";
	public static final String OPEN_PERSPECTIVE = "Open Perspective";
	public static final String TOOL_CHAIN_VERSION_CCRX = "v3.01.00";
	public static final String PROJECT_NAME = "ProjectTesting";
	public static final String LABEL_PROJECT_NAME = "&Project name:";
	public static final String LABEL_RTOS = "&RTOS:";
	public static final String LABEL_RTOS_VERSION = "&RTOS Version:";
	public static final String LABEL_TARGET_BOARD = "&Target Board:";
	public static final String LABEL_TARGET_DEVICE = "&Target Device:";
	public static final String EDITOR_PROJECT_NAME = "ProjectTesting.scfg";
	public static final String PROJECT_NAME_HARDWARE_DEBUG = "ProjectTesting [HardwareDebug]";
	public static final String VIEW_NAME_PROJECT_EXPLORER = "Project Explorer";
	public static final String VIEW_TITLE_WELCOME = "Welcome";
	public static final String MENU_WINDOW = "Window";
	public static final String MENU_SHOW_VIEW = "Show View";
	public static final String MENU_OTHER = "Other...";
	public static final String MENU_BUILD_PROJECT = "Build Project";
	public static final String CONTEXT_MENU_DELETE = "Delete";
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
	public static final String MENU_FILE = "File";
	public static final String MENU_NEW = "New";
	public static final String MENU_C_CPP_PROJECT = "Renesas C/C++ Project";
	public static final String MENU_RENESAS_RX = "Renesas RX";
	public static final String WINDOW_OPEN_ASSOCIATED_PERSPECTIVE = "Open Associated Perspective?";
	public static final String WINDOW_MARKETPLACE = "Editors available on the Marketplace";
	public static final String WINDOW_PROJECT_EXPLORER = "Project Explorer";
	public static final String WINDOW_DELETE_RESOURCES = "Delete Resources";
	public static final String WINDOW_INSTALL = "Install";
	public static final String WINDOW_BOARD_DATA = "Board Data File Update";
	public static final String WINDOW_FIT = "RX Driver Package Download";
	public static final String BUILD_SUCCESSFULLY = "Build Finished. 0 errors";
	public static final String BUILD_FINISH = "Build Finished";
	public static final String BUILD_FAILED = "Build Failed";
	public static final String CHECKBOX_DELETE_PROJECT_CONTENTS_ON_DISK = "Delete project contents on disk (cannot be undone)";
	public static final String CODE_GENERATING = "Code Generating";
	public static final String BUTTON_PROCEED = "Proceed";

	public class DeviceFamily {
		public static final String RX = "Renesas RX";
	}

	public class DeviceName {
		/**
		 * Devices RX
		 */
		public static final String RX651 = "R5F5651EHxLC_DUAL";
	}

	public class RTOSType {
		public static final String AZURE = "Azure RTOS";
	}

	public class RTOSVersion {
		public static final String NEWEST = "6.2.0_rel-rx-1.0.0";
	}

	public class TargetBoard {
		public static final String CUSTOM = "Custom";
	}

	public class ToolchainType {
		public static final String CCRX = "Renesas CCRX";
		public static final String GCCFORRENESASRX = "GCC For Renesas RX";
	}

	public class ProjectKind {
		public static final String EXECUTABLE = "Executable";
	}

	public class BuildType {
		public static final String HARDWARE = "Hardware";
		public static final String DEBUG = "Debug";
		public static final String RELEASE = "Release";
>>>>>>> dbe78b6994fcf864c2352d408ec9096ce2c1f3b3
	}
}
