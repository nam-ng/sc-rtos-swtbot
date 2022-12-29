package swtbot_test;

import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.FixMethodOrder;
import org.junit.runners.MethodSorters;

import model.Bare;
import model.FileX;
import model.GuiX16;
import model.GuiX8;
import model.GuiXDraw2d;
import model.AbstractApplication;
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
	AbstractApplication application;

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

	private void executeTest() {
		TestUtils.executeProject(projectModel, application, 60000);
	}

	@Test
	public void tc_01_closeWelcome() throws Exception {
		bot.viewByTitle("Welcome").close();
	}

	@Test
	public void tc_02_createAndBuildProjectsAzureBare() throws Exception {
		application = new Bare();
		executeTest();
	}

	@Test
	public void tc_03_createAndBuildProjectsAzureFileX() throws Exception {
		application = new FileX();
		executeTest();
	}

	@Test
	public void tc_04_createAndBuildProjectsAzurePing() throws Exception {
		application = new Ping();
		executeTest();
	}

	@Test
	public void tc_05_createAndBuildProjectsAzureIperf() throws Exception {
		application = new Iperf();
		executeTest();
	}

	@Test
	public void tc_06_createAndBuildProjectsAzureIotSDK() throws Exception {
		application = new IoTSdk();
		executeTest();
	}

	@Test
	public void tc_07_createAndBuildProjectsAzureIotSDKEwf() throws Exception {
		application = new IoTSdkEwf();
		executeTest();
	}

	@Test
	public void tc_08_createAndBuildProjectsAzureIotSDKPNP() throws Exception {
		application = new IotSdkPnp();
		executeTest();
	}

	@Test
	public void tc_09_createAndBuildProjectsAzureIotSDKPNPEwf() throws Exception {
		application = new IotSdkPnpEwf();
		executeTest();
	}

	@Test
	public void tc_10_createAndBuildProjectsAzureIotPlugAndPlay() throws Exception {
		application = new IotPlugAndPlay();
		executeTest();
	}

	@Test
	public void tc_11_createAndBuildProjectsAzureIotPlugAndPlayEwf() throws Exception {
		application = new IotPlugAndPlayEwf();
		executeTest();
	}

	@Test
	public void tc_12_createAndBuildProjectsAzureGuix8bpp() throws Exception {
		application = new GuiX8();
		executeTest();
	}

	@Test
	public void tc_13_createAndBuildProjectsAzureGuix16bpp() throws Exception {
		application = new GuiX16();
		executeTest();
	}

	@Test
	public void tc_14_createAndBuildProjectsAzureGuixDraw2d() throws Exception {
		application = new GuiXDraw2d();
		executeTest();
	}

	@Test
	public void tc_15_createAndBuildProjectsUsbx() throws Exception {
		application = new UsbX();
		executeTest();
	}

	@Test
	public void tc_16_createAndBuildProjectsUsbxMass() throws Exception {
		application = new UsbXMass();
		executeTest();
	}

	@Test
	public void tc_17_createAndBuildProjectsAzureLowPower() throws Exception {
		application = new LowPower();
		executeTest();
	}

	@Test
	public void tc_18_createAndBuildSpecificProjectAzure() throws Exception {
		TestUtils.createAndBuildSpecificProjectAzure(projectModelSpecific);
		bot.sleep(5000);
	}
}