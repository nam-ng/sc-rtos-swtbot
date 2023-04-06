package common;

import java.net.URL;

import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.osgi.framework.Bundle;

public class LogUtil {
	public static final String PLUGIN_ID = "swtbot_test"; 

	/**
	 * bundle and bundle context for managing plugin life cycle
	 */
	private static Bundle bundle;

	private LogUtil() {
		// do nothing
	}

	public static Bundle getBundle() {
		if (LogUtil.bundle == null) {
			LogUtil.bundle = Platform.getBundle(LogUtil.PLUGIN_ID);
		}
		return LogUtil.bundle;
	}

	public static URL getEntry(String pluginId, String path) {
		Bundle bundle = Platform.getBundle(pluginId);
		return bundle.getEntry(path);
	}

	private static ILog getLog() {
		Bundle bundle = getBundle();
		if (bundle != null) {
			return Platform.getLog(bundle);
		}
		return null;
	}

	/**
	 * Log with input message
	 * 
	 * @param status
	 *            message to log
	 */
	public static void log(Status status) {
		ILog log = getLog();
		if (log != null) {
			log.log(status);
		}
	}

	/**
	 * Log with input message
	 * 
	 * @param msg
	 *            message to log
	 */
	public static void log(String msg) {
		log(new Status(Status.INFO, PLUGIN_ID, msg));
	}

	/**
	 * Exception logging
	 * 
	 * @param e
	 *            Exception
	 */
	public static void logException(Exception e) {
		log(new Status(Status.ERROR, PLUGIN_ID, e.getMessage(), e));
	}
	
	public static void logException(Exception e, String message) {
		log((Status) Status.error(message, e));
	}
	
	public static void logError(String message) {
		log((Status) Status.error(message));
	}
}
