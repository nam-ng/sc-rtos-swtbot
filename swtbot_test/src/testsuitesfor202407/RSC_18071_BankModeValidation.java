package testsuitesfor202407;

import static org.junit.Assert.assertFalse;

import java.awt.Robot;
import java.io.File;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.eclipse.finder.matchers.WidgetMatcherFactory;
import org.eclipse.swtbot.eclipse.finder.waits.Conditions;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotView;
import org.eclipse.swtbot.swt.finder.utils.SWTBotPreferences;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import common.LogUtil;
import model.RTOSManager;
import parameters.ProjectParameters;
import parameters.ProjectParameters.BankMode;
import parameters.ProjectParameters.ButtonAction;
import parameters.ProjectParameters.LabelName;
import parameters.ProjectParameters.MenuName;
import parameters.ProjectParameters.RTOSDisplay;
import parameters.ProjectParameters.RTOSVersion;
import parameters.ProjectParameters.TargetBoard;
import platform.PlatformModel;
import utilities.Utility;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class RSC_18071_BankModeValidation {
	private static final String PLATFORM_XML_FILE = "xml/platformdata.xml";
	private static final String RTOS_PG_XML_FILE = "xml/rtospg.xml";
	private static SWTWorkbenchBot bot;
	private static Robot robot;

	@BeforeClass
	public static void beforeClass() throws Exception {
		bot = new SWTWorkbenchBot();
		PlatformModel.loadPlatformModel(new File(Utility.getBundlePath(LogUtil.PLUGIN_ID, PLATFORM_XML_FILE)));
		RTOSManager.loadRTOSModel(new File(Utility.getBundlePath(LogUtil.PLUGIN_ID, RTOS_PG_XML_FILE)));
		robot = new Robot();
		Display.getDefault().syncExec(new Runnable() {

			@Override
			public void run() {
				bot.getDisplay().getActiveShell().setMaximized(true);
			}
		});
		SWTBotPreferences.TIMEOUT = 20000;
		SWTBotPreferences.PLAYBACK_DELAY = 30;
		closeWelcomePage();
	}

	private static void closeWelcomePage() {
		for (SWTBotView view : bot.views()) {
			if (view.getTitle().equals("Welcome")) {
				view.close();
			}
		}
	}

	@Test
	public void tc_00_setToolchainLocation() throws Exception {
		Utility.setToolchainManagement(robot, "Renesas CC-RX",
				ProjectParameters.FileLocation.TOOLCHAIN_RENESAS_CCRX_V3_05);
		Utility.reFocus(robot);
	}

	@Test
	public void tc_01_ChangeRTOSLocation() throws Exception {
		Utility.changeModuleDownloadLocation(robot, ProjectParameters.FileLocation.FreeRTOSIOT_RTOS_LOCATION_BANKMODE, true);
		Utility.changeModuleDownloadLocation(robot, ProjectParameters.FileLocation.NEWEST_FIT_MODULES_LOCATION, false);
		Utility.reFocus(robot);
	}

	@Test
	public void tc_02_TestValidateBankmodeWithBoard() throws Exception {
		bot.sleep(3000);
		bot.menu(MenuName.MENU_FILE).menu(MenuName.MENU_NEW).menu(MenuName.MENU_C_CPP_PROJECT)
				.menu(MenuName.MENU_RENESAS_RX).click();
		bot.table().select(2);
		bot.button(ButtonAction.BUTTON_NEXT).click();
		bot.waitUntil(Conditions.waitForWidget(WidgetMatcherFactory.withText(LabelName.LABEL_PROJECT_NAME)), 10000);
		bot.textWithLabel(LabelName.LABEL_PROJECT_NAME).setText("testValidateBankMode");
		bot.waitUntil(Conditions.widgetIsEnabled(bot.button(ButtonAction.BUTTON_NEXT)), 10000);

		bot.button(ButtonAction.BUTTON_NEXT).click();
		bot.comboBoxWithLabel(LabelName.LABEL_TOOLCHAIN_VERSION).setSelection(0);

		bot.comboBoxWithLabel(LabelName.LABEL_RTOS).setSelection(RTOSDisplay.FREERTOSIOTLTS);
		bot.comboBoxWithLabel(LabelName.LABEL_RTOS_VERSION).setSelection(RTOSVersion.IoTLTS_202210_1_2_1);

		bot.styledText().setText(TargetBoard.BOARD_CK_RX65N);

		bot.comboBoxWithLabel(LabelName.LABEL_BANK_MODE).setSelection(BankMode.SINGLE_MODE);

		bot.sleep(3000);
		Utility.reFocus(robot);
		boolean isValidateMessageError = bot.textWithLabel("").getText().contains(
				"FreeRTOS202210.01-LTS-rx-1.2.1 does not support selected device with Single Bank setting.\r\n(Please change Bank Mode setting to Dual Bank)");
		boolean isButtonEnable1 = bot.button(ButtonAction.BUTTON_NEXT).isEnabled();

		bot.comboBoxWithLabel(LabelName.LABEL_BANK_MODE).setSelection(BankMode.DUAL_MODE);
		bot.sleep(3000);
		Utility.reFocus(robot);
		boolean isValidateMessageEmpty = bot.textWithLabel("").getText()
				.contains("Select toolchain, device & debug settings");
		boolean isButtonEnable2 = bot.button(ButtonAction.BUTTON_NEXT).isEnabled();

		bot.button(ButtonAction.BUTTON_CANCEL).click();
		if (!isValidateMessageError || isButtonEnable1 || !isButtonEnable2 || !isValidateMessageEmpty) {
			assertFalse(true);
		}

	}

	@Test
	public void tc_03_TestValidateBankmodeWithDevice() throws Exception {
		bot.sleep(3000);
		bot.menu(MenuName.MENU_FILE).menu(MenuName.MENU_NEW).menu(MenuName.MENU_C_CPP_PROJECT)
				.menu(MenuName.MENU_RENESAS_RX).click();
		bot.table().select(2);
		bot.button(ButtonAction.BUTTON_NEXT).click();
		bot.waitUntil(Conditions.waitForWidget(WidgetMatcherFactory.withText(LabelName.LABEL_PROJECT_NAME)), 10000);
		bot.textWithLabel(LabelName.LABEL_PROJECT_NAME).setText("testValidateBankMode");
		bot.waitUntil(Conditions.widgetIsEnabled(bot.button(ButtonAction.BUTTON_NEXT)), 10000);

		bot.button(ButtonAction.BUTTON_NEXT).click();
		bot.comboBoxWithLabel(LabelName.LABEL_TOOLCHAIN_VERSION).setSelection(0);

		bot.comboBoxWithLabel(LabelName.LABEL_RTOS).setSelection(RTOSDisplay.FREERTOSIOTLTS);
		bot.comboBoxWithLabel(LabelName.LABEL_RTOS_VERSION).setSelection(RTOSVersion.IoTLTS_202210_1_2_1);

		bot.styledText().setText(TargetBoard.DEVICE_R5F565NEHxFC);

		bot.comboBoxWithLabel(LabelName.LABEL_BANK_MODE).setSelection(BankMode.DUAL_MODE);

		bot.sleep(3000);
		Utility.reFocus(robot);
		boolean isValidateMessageError = bot.textWithLabel("").getText().contains(
				"FreeRTOS202210.01-LTS-rx-1.2.1 does not support selected device \r\n(Support: Devices satisfied expression [R5F572M[DN][DH]xBD_DUAL, R5F565N[ED][EH]xFC$])");
		boolean isButtonEnable1 = bot.button(ButtonAction.BUTTON_NEXT).isEnabled();

		bot.comboBoxWithLabel(LabelName.LABEL_BANK_MODE).setSelection(BankMode.SINGLE_MODE);
		bot.sleep(3000);
		Utility.reFocus(robot);
		boolean isValidateMessageEmpty = bot.textWithLabel("").getText()
				.contains("Please configure your board related settings and put them into your code. (pins, clock settings, etc)");
		boolean isButtonEnable2 = bot.button(ButtonAction.BUTTON_NEXT).isEnabled();

		bot.button(ButtonAction.BUTTON_CANCEL).click();
		if (!isValidateMessageError || isButtonEnable1 || !isButtonEnable2 || !isValidateMessageEmpty) {
			assertFalse(true);
		}

	}
	
	@Test
	public void tc_04_TestValidateBankmodeWithBoardOnApp() throws Exception {
		bot.sleep(3000);
		bot.menu(MenuName.MENU_FILE).menu(MenuName.MENU_NEW)
				.menu(MenuName.MENU_C_CPP_PROJECT).menu(MenuName.MENU_RENESAS_RX).click();
		bot.table().select(2);
		bot.button(ButtonAction.BUTTON_NEXT).click();
		bot.waitUntil(Conditions.waitForWidget(WidgetMatcherFactory.withText(LabelName.LABEL_PROJECT_NAME)), 10000);
		bot.textWithLabel(LabelName.LABEL_PROJECT_NAME).setText("testValidateBankMode");
		bot.waitUntil(Conditions.widgetIsEnabled(bot.button(ButtonAction.BUTTON_NEXT)), 10000);
		
		bot.button(ButtonAction.BUTTON_NEXT).click();
		bot.comboBoxWithLabel(LabelName.LABEL_TOOLCHAIN_VERSION).setSelection(0);
		
		bot.comboBoxWithLabel(LabelName.LABEL_RTOS).setSelection(RTOSDisplay.FREERTOSIOTLTS);
		bot.comboBoxWithLabel(LabelName.LABEL_RTOS_VERSION).setSelection(RTOSVersion.IoTLTS_202210_1_2_1);
		
		bot.styledText().setText(TargetBoard.BOARD_RSK_RX65N_2MB);
		bot.comboBoxWithLabel(LabelName.LABEL_BANK_MODE).setSelection(BankMode.SINGLE_MODE);
		bot.button(ButtonAction.BUTTON_NEXT).click();
		bot.button(ButtonAction.BUTTON_NEXT).click();
		bot.radio(0).click();
		
		boolean isValidateMessageError = bot.textWithLabel("").getText().contains("Selected Application does not support target RSKRX65N-2MB with Single Bank setting.\r\n(Please \"<Back\" to the device selection page and change Bank Mode setting to Dual Bank)");
		boolean isButtonEnable1 = bot.button(ButtonAction.BUTTON_FINISH).isEnabled();
		
		bot.button(ButtonAction.BUTTON_BACK).click();
		bot.button(ButtonAction.BUTTON_BACK).click();
		bot.comboBoxWithLabel(LabelName.LABEL_BANK_MODE).setSelection(BankMode.DUAL_MODE);

		bot.button(ButtonAction.BUTTON_NEXT).click();
		bot.button(ButtonAction.BUTTON_NEXT).click();
		bot.radio(0).click();
		boolean isValidateMessageEmpty = bot.textWithLabel("").getText().contains("Select RTOS Project Settings");
		boolean isButtonEnable2 = bot.button(ButtonAction.BUTTON_FINISH).isEnabled();
		
		
		bot.button(ButtonAction.BUTTON_CANCEL).click();
		if (!isValidateMessageError || isButtonEnable1 || !isValidateMessageEmpty || !isButtonEnable2) {
			assertFalse(true);
		}
	}
	
	@Test
	public void tc_05_TestValidateBankmodeWithDeviceOnApp() throws Exception {
		bot.sleep(3000);
		bot.menu(MenuName.MENU_FILE).menu(MenuName.MENU_NEW)
				.menu(MenuName.MENU_C_CPP_PROJECT).menu(MenuName.MENU_RENESAS_RX).click();
		bot.table().select(2);
		bot.button(ButtonAction.BUTTON_NEXT).click();
		bot.waitUntil(Conditions.waitForWidget(WidgetMatcherFactory.withText(LabelName.LABEL_PROJECT_NAME)), 10000);
		bot.textWithLabel(LabelName.LABEL_PROJECT_NAME).setText("testValidateBankMode");
		bot.waitUntil(Conditions.widgetIsEnabled(bot.button(ButtonAction.BUTTON_NEXT)), 10000);
		
		bot.button(ButtonAction.BUTTON_NEXT).click();
		bot.comboBoxWithLabel(LabelName.LABEL_TOOLCHAIN_VERSION).setSelection(0);
		
		bot.comboBoxWithLabel(LabelName.LABEL_RTOS).setSelection(RTOSDisplay.FREERTOSIOTLTS);
		bot.comboBoxWithLabel(LabelName.LABEL_RTOS_VERSION).setSelection(RTOSVersion.IoTLTS_202210_1_2_1);
		
		bot.styledText().setText(TargetBoard.DEVICE_R5F565NEHxFC);
		bot.comboBoxWithLabel(LabelName.LABEL_BANK_MODE).setSelection(BankMode.SINGLE_MODE);
		bot.button(ButtonAction.BUTTON_NEXT).click();
		bot.button(ButtonAction.BUTTON_NEXT).click();
		bot.radio(0).click();
		
		boolean isValidateMessageError = bot.textWithLabel("").getText().contains("Selected Application does not support target device: R5F565NEHxFC \r\n(Support: Devices satisfied expression [R5F565N[ED][EH]xFC_DUAL])");
		boolean isButtonEnable1 = bot.button(ButtonAction.BUTTON_FINISH).isEnabled();
		
		
		bot.button(ButtonAction.BUTTON_CANCEL).click();
		if (!isValidateMessageError || !isButtonEnable1) {
			assertFalse(true);
		}
	}
}
