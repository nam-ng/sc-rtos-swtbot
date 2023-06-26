package parameters;

import org.eclipse.osgi.util.NLS;

public class CommonParameters extends NLS {
	private static final String BUNDLE_NAME = "parameters.parameter"; //$NON-NLS-1$
	public static String ContextMenuBuildConfigurations;
	public static String ContextMenuBuildProject;
	public static String DefaultProjectName;
	public static String GCCMenuActiveDebug;
	public static String CCRXMenuActiveDebug;
	public static String GCCMenuActiveHardwareDebug;
	public static String CCRXMenuActiveHardwareDebug;
	public static String GCCMenuActiveRelease;
	public static String CCRXMenuActiveRelease;
	public static String MenuSetActive;
	public static String Message_Build_Successfully;
	public static String ToolchainVersionCCRX;
	public static String ViewEventPoint;
	public static String ViewTypeDebug;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, CommonParameters.class);
	}

	public class DeviceFamily {
		public static final String RX = "Renesas RX";
		public static final String RZ = "Renesas RZ";
	}

	public class TargetBoard {
		public static final String CUSTOM = "Custom";
	}

	public class ProjectKind {
		public static final String EXECUTABLE = "Executable";
		public static final String LIBRARY = "Library";
	}

	public class BuildType {
		public static final String HARDWARE = "Hardware";
		public static final String DEBUG = "Debug";
		public static final String RELEASE = "Release";
	}

	public class CalculateExecuteTime {
		public static final String CREATETIME = "Create Time";
		public static final String BUILDTIME = "Build Time";
		public static final String OVERALLTIME = "Overall Time";
	}

	public enum Language {
		C, CPP
	}
}
