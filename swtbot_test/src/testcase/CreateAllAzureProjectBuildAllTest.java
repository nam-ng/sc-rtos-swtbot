package testcase;

import java.io.File;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotView;
import org.eclipse.swtbot.swt.finder.utils.SWTBotPreferences;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import common.Constants;
import common.LogUtil;
import model.RTOSManager;
import parameters.ProjectParameters.RTOSApplication;
import parameters.ProjectParameters.RTOSType;
import parameters.ProjectParameters.RTOSVersion;
import platform.PlatformModel;
import utilities.BuildUtility;
import utilities.PGUtility;
import utilities.Utility;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class CreateAllAzureProjectBuildAllTest {
	private static SWTWorkbenchBot bot;
	private static SWTBotShell workbenchShell;
	

	@BeforeClass
	public static void beforeClass() throws Exception {
		bot = new SWTWorkbenchBot();
		PlatformModel.loadPlatformModel(new File(Utility.getBundlePath(LogUtil.PLUGIN_ID, Constants.PLATFORM_XML_FILE)));
		RTOSManager.loadRTOSModel(new File(Utility.getBundlePath(LogUtil.PLUGIN_ID, Constants.RTOS_PG_XML_FILE)));

		// initialize the SWTBot
		bot = new SWTWorkbenchBot();
		Display.getDefault().syncExec(new Runnable() {

			@Override
			public void run() {
				bot.getDisplay().getActiveShell().setMaximized(true);
			}
		});
		SWTBotPreferences.TIMEOUT = 20000;
		SWTBotPreferences.PLAYBACK_DELAY = 30;
		closeWelcomePage();
		workbenchShell = bot.activeShell();
	}

	private static void closeWelcomePage() {
		for (SWTBotView view : bot.views()) {
			if (view.getTitle().equals("Welcome")) {
				view.close();
			}
		}
	}

	@Test
	public void TC_021_PGAzure_Bare() throws Exception {
		PGUtility.createProject(RTOSType.AZURE, RTOSVersion.Azure_6_2_1, RTOSApplication.AZURE_BARE);
	}

	@Test
	public void TC_031_PGAzure_Ram() throws Exception {
		PGUtility.createProject(RTOSType.AZURE, RTOSVersion.Azure_6_2_1, RTOSApplication.AZURE_RAM);
	}

	@Test
	public void TC_041_PGAzure_Ping() throws Exception {
		PGUtility.createProject(RTOSType.AZURE, RTOSVersion.Azure_6_2_1, RTOSApplication.AZURE_PING);
	}

	@Test
	public void TC_051_PGAzure_Iperf() throws Exception {
		PGUtility.createProject(RTOSType.AZURE, RTOSVersion.Azure_6_2_1, RTOSApplication.AZURE_IPERF);
	}

	@Test
	public void TC_061_PGAzure_Iot_Sdk() throws Exception {
		PGUtility.createProject(RTOSType.AZURE, RTOSVersion.Azure_6_2_1, RTOSApplication.AZURE_IOT_SDK);
	}

	@Test
	public void TC_071_PGAzure_Iot_Sdk_Ewf() throws Exception {
		PGUtility.createProject(RTOSType.AZURE, RTOSVersion.Azure_6_2_1, RTOSApplication.AZURE_IOT_SDK_EWF);
	}

	@Test
	public void TC_081_PGAzure_Iot_Pnp() throws Exception {
		PGUtility.createProject(RTOSType.AZURE, RTOSVersion.Azure_6_2_1, RTOSApplication.AZURE_IOT_PNP);
	}

	@Test
	public void TC_091_PGAzure_Iot_Pnp_Ewf() throws Exception {
		PGUtility.createProject(RTOSType.AZURE, RTOSVersion.Azure_6_2_1, RTOSApplication.AZURE_IOT_PNP_EWF);
	}

	@Test
	public void TC_122_PGAzure_Guix_8bpp() throws Exception {
		PGUtility.createProject(RTOSType.AZURE, RTOSVersion.Azure_6_2_1, RTOSApplication.AZURE_GUIX_8BPP);
	}

	@Test
	public void TC_131_PGAzure_Guix_16bpp() throws Exception {
		PGUtility.createProject(RTOSType.AZURE, RTOSVersion.Azure_6_2_1, RTOSApplication.AZURE_GUIX_16BPP);
	}

	@Test
	public void TC_141_PGAzure_Guix_16bpp_Draw() throws Exception {
		PGUtility.createProject(RTOSType.AZURE, RTOSVersion.Azure_6_2_1, RTOSApplication.AZURE_GUIX_16BPP_DRAW);
	}

	@Test
	public void TC_152_PGAzure_Usbx_CDC() throws Exception {
		PGUtility.createProject(RTOSType.AZURE, RTOSVersion.Azure_6_2_1, RTOSApplication.AZURE_USBX_CDC);
	}
	
	@Test
	public void TC_161_PGAzure_Usbx_HMSC() throws Exception {
		PGUtility.createProject(RTOSType.AZURE, RTOSVersion.Azure_6_2_1, RTOSApplication.AZURE_USBX_HMSC);
	}

	@Test
	public void TC_171_PGAzure_Low_Power() throws Exception {
		PGUtility.createProject(RTOSType.AZURE, RTOSVersion.Azure_6_2_1, RTOSApplication.AZURE_LOW_POWER);
	}

	@Test
	public void TC_181_PG_Bootloader() throws Exception {
		PGUtility.createProject(RTOSType.AZURE, RTOSVersion.Azure_6_2_1, RTOSApplication.AZURE_BOOTLOADER);
	}

	@Test
	public void TC_191_BuildAll() throws Exception {
		BuildUtility.buildAll(workbenchShell);
	}

}