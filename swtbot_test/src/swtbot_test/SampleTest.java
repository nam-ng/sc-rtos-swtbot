package swtbot_test;

import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotView;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.FixMethodOrder;
import org.junit.runners.MethodSorters;

import model.Bare;
import model.FileX;
import model.GuiX16;
import model.GuiX8;
import model.GuiXDraw2d;
import model.IApplication;
import model.IoTSdk;
import model.IoTSdkEwf;
import model.IotPlugAndPlay;
import model.IotPlugAndPlayEwf;
import model.IotSdkPnp;
import model.IotSdkPnpEwf;
import model.Iperf;
import model.LowPower;
import model.Ping;
import model.UsbX;
import model.UsbXMass;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class SampleTest {
	private static SWTWorkbenchBot bot;
	private static ProjectModel projectModelSpecific = new ProjectModel();
	private ProjectModel projectModel = new ProjectModel();
	IApplication application;
	SWTBotView consoleView = bot.viewById("org.eclipse.ui.console.ConsoleView");

	@BeforeClass
	public static void beforeClass() throws Exception {
		bot = new SWTWorkbenchBot();
		projectModelSpecific.setRtosType(ProjectParameters.RTOSType.AZURE);
		projectModelSpecific.setRtosVersion(ProjectParameters.RTOSVersion.NEWEST);
		projectModelSpecific.setTargetBoard("RSKRX65N-2MB");
		projectModelSpecific.setApplication(0);
		projectModelSpecific.setToolchain("GCC");
		projectModelSpecific.setProjectName(
				"project" + projectModelSpecific.getApplication() + projectModelSpecific.getToolchain() + "100");
	}

	public void gccTestExecuted() {
		if (application.isGccexecuted()) {
			TestUtils.gccExecuted(projectModel, application);
			bot.sleep(60000);
		}
	}

	public void ccrxTestExecuted() {
		if (application.isCcrxexecuted()) {
			TestUtils.ccrxExecuted(projectModel, application);
			bot.sleep(60000);
		}
	}

	@Test
	public void tc_01_closeWelcome() throws Exception {
		bot.viewByTitle("Welcome").close();
	}

//	@Test
//	public void tc_021_createAndBuildProjectsAzureBareGCC() throws Exception {
//		application = new Bare();
//		gccTestExecuted();
//	}
//
//	@Test
//	public void tc_022_createAndBuildProjectsAzureBareCCRX() throws Exception {
//		application = new Bare();
//		ccrxTestExecuted();
//	}
//
//	@Test
//	public void tc_031_createAndBuildProjectsAzureFileXGCC() throws Exception {
//		application = new FileX();
//		gccTestExecuted();
//	}
//
//	@Test
//	public void tc_032_createAndBuildProjectsAzureFileXCCRX() throws Exception {
//		application = new FileX();
//		ccrxTestExecuted();
//	}
//
//	@Test
//	public void tc_041_createAndBuildProjectsAzurePingGCC() throws Exception {
//		application = new Ping();
//		gccTestExecuted();
//	}
//
//	@Test
//	public void tc_042_createAndBuildProjectsAzurePingCCRX() throws Exception {
//		application = new Ping();
//		ccrxTestExecuted();
//	}
//
//	@Test
//	public void tc_051_createAndBuildProjectsAzureIperfGCC() throws Exception {
//		application = new Iperf();
//		gccTestExecuted();
//	}
//
//	@Test
//	public void tc_052_createAndBuildProjectsAzureIperfCCRX() throws Exception {
//		application = new Iperf();
//		ccrxTestExecuted();
//	}
//
//	@Test
//	public void tc_061_createAndBuildProjectsAzureIotSDKGCC() throws Exception {
//		application = new IoTSdk();
//		gccTestExecuted();
//	}
//
//	@Test
//	public void tc_062_createAndBuildProjectsAzureIotSDKCCRX() throws Exception {
//		application = new IoTSdk();
//		ccrxTestExecuted();
//	}

	@Test
	public void tc_071_createAndBuildProjectsAzureIotSDKEwfGCC() throws Exception {
		application = new IoTSdkEwf();
		gccTestExecuted();
	}

	@Test
	public void tc_072_createAndBuildProjectsAzureIotSDKEwfCCRX() throws Exception {
		application = new IoTSdkEwf();
		ccrxTestExecuted();
	}
	@Test
	public void tc_081_createAndBuildProjectsAzureIotSDKPNPGCC() throws Exception {
		application = new IotSdkPnp();
		gccTestExecuted();
	}

	@Test
	public void tc_082_createAndBuildProjectsAzureIotSDKPNPCCRX() throws Exception {
		application = new IotSdkPnp();
		ccrxTestExecuted();
	}
	
	@Test
	public void tc_091_createAndBuildProjectsAzureIotSDKPNPEwfGCC() throws Exception {
		application = new IotSdkPnpEwf();
		gccTestExecuted();
	}

	@Test
	public void tc_092_createAndBuildProjectsAzureIotSDKPNPEwfCCRX() throws Exception {
		application = new IotSdkPnpEwf();
		ccrxTestExecuted();
	}

	@Test
	public void tc_101_createAndBuildProjectsAzureIotPlugAndPlayGCC() throws Exception {
		application = new IotPlugAndPlay();
		gccTestExecuted();
	}

	@Test
	public void tc_102_createAndBuildProjectsAzureIotPlugAndPlayCCRX() throws Exception {
		application = new IotPlugAndPlay();
		ccrxTestExecuted();
	}
	
	@Test
	public void tc_111_createAndBuildProjectsAzureIotPlugAndPlayEwfGCC() throws Exception {
		application = new IotPlugAndPlayEwf();
		gccTestExecuted();
	}

	@Test
	public void tc_112_createAndBuildProjectsAzureIotPlugAndPlayEwfCCRX() throws Exception {
		application = new IotPlugAndPlayEwf();
		ccrxTestExecuted();
	}

	@Test
	public void tc_121_createAndBuildProjectsAzureGuix8bppGCC() throws Exception {
		application = new GuiX8();
		gccTestExecuted();
	}

	@Test
	public void tc_122_createAndBuildProjectsAzureGuix8bppCCRX() throws Exception {
		application = new GuiX8();
		ccrxTestExecuted();
	}

	@Test
	public void tc_131_createAndBuildProjectsAzureGuix16bppGCC() throws Exception {
		application = new GuiX16();
		gccTestExecuted();
	}

	@Test
	public void tc_132_createAndBuildProjectsAzureGuix16bppCCRX() throws Exception {
		application = new GuiX16();
		ccrxTestExecuted();
	}

	@Test
	public void tc_141_createAndBuildProjectsAzureGuixDraw2dGCC() throws Exception {
		application = new GuiXDraw2d();
		gccTestExecuted();
	}

	@Test
	public void tc_142_createAndBuildProjectsAzureGuixDraw2dCCRX() throws Exception {
		application = new GuiXDraw2d();
		ccrxTestExecuted();
	}

	@Test
	public void tc_151_createAndBuildProjectsUsbxGCC() throws Exception {
		application = new UsbX();
		gccTestExecuted();
	}

	@Test
	public void tc_152_createAndBuildProjectsUsbxCCRX() throws Exception {
		application = new UsbX();
		ccrxTestExecuted();
	}
	
	@Test
	public void tc_161_createAndBuildProjectsUsbxMassGCC() throws Exception {
		application = new UsbXMass();
		gccTestExecuted();
	}

	@Test
	public void tc_162_createAndBuildProjectsUsbxMassCCRX() throws Exception {
		application = new UsbXMass();
		ccrxTestExecuted();
	}

	@Test
	public void tc_171_createAndBuildProjectsAzureLowPowerGCC() throws Exception {
		application = new LowPower();
		gccTestExecuted();
	}

	@Test
	public void tc_172_createAndBuildProjectsAzureLowPowerCCRX() throws Exception {
		application = new LowPower();
		ccrxTestExecuted();
	}

	@Test
	public void tc_181_createAndBuildSpecificProjectAzure() throws Exception {
		TestUtils.createAndBuildSpecificProjectAzure(projectModelSpecific);
		bot.sleep(5000);
	}
}