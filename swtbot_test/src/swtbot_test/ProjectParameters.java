package swtbot_test;

public class ProjectParameters {
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
	}
}
