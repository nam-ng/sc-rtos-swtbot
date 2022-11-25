package swtbot_test;
 
import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotView;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.FixMethodOrder;
import org.junit.runners.MethodSorters;

import model.AbstractApplication;
import model.Bare;
import model.FileX;
import model.GuiX16;
import model.GuiX8;
import model.GuiXDraw2d;
import model.IoTSdk;
import model.IotPlugAndPlay;
import model.IotSdkPnp;
import model.Iperf;
import model.LowPower;
import model.Ping;
import model.UsbX;
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class SampleTest {
    private static SWTWorkbenchBot  bot;
    private static ProjectModel projectModelSpecific = new ProjectModel();
    private ProjectModel projectModel = new ProjectModel();
    AbstractApplication application;
    SWTBotView consoleView = bot.viewById("org.eclipse.ui.console.ConsoleView");
 
    @BeforeClass
    public static void beforeClass() throws Exception {
        bot = new SWTWorkbenchBot();
        projectModelSpecific.setRtosType(ProjectParameters.RTOSType.AZURE);
		projectModelSpecific.setRtosVersion(ProjectParameters.RTOSVersion.NEWEST);
		projectModelSpecific.setTargetBoard("RSKRX65N-2MB");
		projectModelSpecific.setApplication(0);
		projectModelSpecific.setToolchain("GCC");
		projectModelSpecific.setProjectName("project"+projectModelSpecific.getApplication()+projectModelSpecific.getToolchain() +"100");
    }
    @Test
    public void tc_01_closeWelcome() throws Exception {
    	bot.viewByTitle("Welcome").close();
    }
//    @Test
//    public void tc_021_createAndBuildProjectsAzureBareGCC() throws Exception {
//    	application= new Bare();
//    	if(Bare.isGccexecuted()) {
//    		application.gccExecuted(projectModel);
//    		bot.sleep(60000);
//    	}
//    }
//    @Test
//    public void tc_022_createAndBuildProjectsAzureBareCCRX() throws Exception {
//    	application= new Bare();
//    	if(Bare.isCcrxexecuted()) {
//    		application.ccrxExecuted(projectModel);
//    		bot.sleep(60000);
//    	}
//    }
//    @Test
//    public void tc_031_createAndBuildProjectsAzureFileXGCC() throws Exception {
//    	application= new FileX();
//    	if(FileX.isGccexecuted()) {
//    		application.gccExecuted(projectModel);
//    		bot.sleep(60000);
//    	}
//    }
//    @Test
//    public void tc_032_createAndBuildProjectsAzureFileXCCRX() throws Exception {
//    	application= new FileX();
//    	if(FileX.isCcrxexecuted()) {
//    		application.ccrxExecuted(projectModel);
//    		bot.sleep(60000);
//    	}
//    }
    @Test
    public void tc_041_createAndBuildProjectsAzurePingGCC() throws Exception {
    	application= new Ping();
    	if(Ping.isGccexecuted()) {
    		application.gccExecuted(projectModel);
    		bot.sleep(60000);
    	}
    }
    @Test
    public void tc_042_createAndBuildProjectsAzurePingCCRX() throws Exception {
    	application= new Ping();
    	if(Ping.isCcrxexecuted()) {
    		application.ccrxExecuted(projectModel);
    		bot.sleep(60000);
    	}
    }
    @Test
    public void tc_051_createAndBuildProjectsAzureIperfGCC() throws Exception {
    	application= new Iperf();
    	if(Iperf.isGccexecuted()) {
    		application.gccExecuted(projectModel);
    		bot.sleep(60000);
    	}
    }
    @Test
    public void tc_052_createAndBuildProjectsAzureIperfCCRX() throws Exception {
    	application= new Iperf();
    	if(Iperf.isCcrxexecuted()) {
    		application.ccrxExecuted(projectModel);
    		bot.sleep(60000);
    	}
    }
    @Test
    public void tc_061_createAndBuildProjectsAzureIotSDKGCC() throws Exception {
    	application= new IoTSdk();
    	if(IoTSdk.isGccexecuted()) {
    		application.gccExecuted(projectModel);
    		bot.sleep(60000);
    	}
    }
    @Test
    public void tc_062_createAndBuildProjectsAzureIotSDKCCRX() throws Exception {
    	application= new IoTSdk();
    	if(IoTSdk.isCcrxexecuted()) {
    		application.ccrxExecuted(projectModel);
    		bot.sleep(60000);
    	}
    }
    @Test
    public void tc_071_createAndBuildProjectsAzureIotSDKPNPGCC() throws Exception {
    	application= new IotSdkPnp();
    	if(IotSdkPnp.isGccexecuted()) {
    		application.gccExecuted(projectModel);
    		bot.sleep(60000);
    	}
    }
    @Test
    public void tc_072_createAndBuildProjectsAzureIotSDKPNPCCRX() throws Exception {
    	application= new IotSdkPnp();
    	if(IotSdkPnp.isCcrxexecuted()) {
    		application.ccrxExecuted(projectModel);
    		bot.sleep(60000);
    	}
    }
    @Test
    public void tc_081_createAndBuildProjectsAzureIotPlugAndPlayGCC() throws Exception {
    	application= new IotPlugAndPlay();
    	if(IotPlugAndPlay.isGccexecuted()) {
    		application.gccExecuted(projectModel);
    		bot.sleep(60000);
    	}
    }
    @Test
    public void tc_082_createAndBuildProjectsAzureIotPlugAndPlayCCRX() throws Exception {
    	application= new IotPlugAndPlay();
    	if(IotPlugAndPlay.isCcrxexecuted()) {
    		application.ccrxExecuted(projectModel);
    		bot.sleep(60000);
    	}
    }
    @Test
    public void tc_091_createAndBuildProjectsAzureGuix8bppGCC() throws Exception {
    	application= new GuiX8();
    	if(GuiX8.isGccexecuted()) {
    		application.gccExecuted(projectModel);
    		bot.sleep(60000);
    	}
    }
    @Test
    public void tc_092_createAndBuildProjectsAzureGuix8bppCCRX() throws Exception {
    	application= new GuiX8();
    	if(GuiX8.isCcrxexecuted()) {
    		application.ccrxExecuted(projectModel);
    		bot.sleep(60000);
    	}
    }
    @Test
    public void tc_101_createAndBuildProjectsAzureGuix16bppGCC() throws Exception {
    	application= new GuiX16();
    	if(GuiX16.isGccexecuted()) {
    		application.gccExecuted(projectModel);
    		bot.sleep(60000);
    	}
    }
    @Test
    public void tc_102_createAndBuildProjectsAzureGuix16bppCCRX() throws Exception {
    	application= new GuiX16();
    	if(GuiX16.isCcrxexecuted()) {
    		application.ccrxExecuted(projectModel);
    		bot.sleep(60000);
    	}
    }
    @Test
    public void tc_111_createAndBuildProjectsAzureGuixDraw2dGCC() throws Exception {
    	application= new GuiXDraw2d();
    	if(GuiXDraw2d.isGccexecuted()) {
    		application.gccExecuted(projectModel);
    		bot.sleep(60000);
    	}
    }
    @Test
    public void tc_112_createAndBuildProjectsAzureGuixDraw2dCCRX() throws Exception {
    	application= new GuiXDraw2d();
    	if(GuiXDraw2d.isCcrxexecuted()) {
    		application.ccrxExecuted(projectModel);
    		bot.sleep(60000);
    	}
    }
    @Test
    public void tc_121_createAndBuildProjectsUsbxGCC() throws Exception {
    	application= new UsbX();
    	if(UsbX.isGccexecuted()) {
    		application.gccExecuted(projectModel);
    		bot.sleep(60000);
    	}
    }
    @Test
    public void tc_122_createAndBuildProjectsUsbxCCRX() throws Exception {
    	application= new UsbX();
    	if(UsbX.isCcrxexecuted()) {
    		application.ccrxExecuted(projectModel);
    		bot.sleep(60000);
    	}
    }
    @Test
    public void tc_131_createAndBuildProjectsAzureLowPowerGCC() throws Exception {
    	application= new LowPower();
    	if(LowPower.isGccexecuted()) {
    		application.gccExecuted(projectModel);
    		bot.sleep(60000);
    	}
    }
    @Test
    public void tc_132_createAndBuildProjectsAzureLowPowerCCRX() throws Exception {
    	application= new LowPower();
    	if(LowPower.isCcrxexecuted()) {
    		application.ccrxExecuted(projectModel);
    		bot.sleep(60000);
    	}
    }
    @Test
    public void tc_141_createAndBuildSpecificProjectAzure() throws Exception {
    	TestUtils.createAndBuildSpecificProjectAzure(projectModelSpecific);
    	bot.sleep(5000);
    }
}