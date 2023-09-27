package legacytestsuites;

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
import org.junit.Test;

import common.LogUtil;
import model.RTOSManager;
import parameters.ProjectParameters;
import parameters.ProjectParameters.ButtonAction;
import parameters.ProjectParameters.LabelName;
import parameters.ProjectParameters.MenuName;
import parameters.ProjectParameters.RTOSDisplay;
import platform.PlatformModel;
import utilities.Utility;

public class ValidateMessage {
	private static SWTWorkbenchBot bot;
	private static Robot robot;
	private static final String PLATFORM_XML_FILE = "xml/platformdata.xml";
	private static final String RTOS_PG_XML_FILE = "xml/rtospg.xml";

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
		changeView();
	}

	private static void closeWelcomePage() {
		for (SWTBotView view : bot.views()) {
			if (view.getTitle().equals("Welcome")) {
				view.close();
			}
		}
	}
	
	private static void changeView() {
		bot.defaultPerspective().activate();
	}
	
	@Test
	public void tc_00_ChangeRTOSLocation() throws Exception{
		Utility.changeModuleDownloadLocation(robot, ProjectParameters.FileLocation.AMAZON_RTOS_LOCATION, true);
		Utility.changeModuleDownloadLocation(robot, ProjectParameters.FileLocation.FIT_MODULES_LOCATION, false);
		Utility.reFocus(robot);
	}
	
	@Test
	public void tc_01_CheckCustomBoard() throws Exception {
		bot.sleep(3000);
		bot.menu(MenuName.MENU_FILE).menu(MenuName.MENU_NEW)
		.menu(MenuName.MENU_C_CPP_PROJECT).menu(MenuName.MENU_RENESAS_RX).click();
		bot.table().select(2);
		
		bot.button(ButtonAction.BUTTON_NEXT).click();
		bot.waitUntil(Conditions.waitForWidget(WidgetMatcherFactory.withText(LabelName.LABEL_PROJECT_NAME)), 10000);
		bot.textWithLabel(LabelName.LABEL_PROJECT_NAME).setText("message_custom_board");
		bot.waitUntil(Conditions.widgetIsEnabled(bot.button(ButtonAction.BUTTON_NEXT)), 10000);
		
		bot.button(ButtonAction.BUTTON_NEXT).click();
		bot.radio(0).click();
		bot.comboBoxWithLabel(LabelName.LABEL_RTOS).setSelection(RTOSDisplay.AMAZONFREERTOS);
		
		bot.styledText().setText("R5F565NCDxBG");
		
		String text = bot.textWithLabel("").getText();

		bot.sleep(3000);
		String warningText = " Please configure your board related settings and put them into your code. (pins, clock settings, etc)";
		boolean isTextContains = text.contains(warningText);
		boolean isButtonNextEnable = bot.button(ButtonAction.BUTTON_NEXT).isEnabled();
		if (!isTextContains || !isButtonNextEnable) {
			assertFalse(true);
		}

	}
	
	@Test
	public void tc_02_CheckBoardNotSupport() throws Exception {
		bot.comboBoxWithLabel(LabelName.LABEL_TARGET_BOARD).setSelection("RSKRX111");
		
		String text = bot.textWithLabel("").getText();

		bot.sleep(3000);
		String errorText = " FreeRTOSv202107.00-rx-1.0.1 does not support selected device \n(Support: [RX65N, RX671, RX72N])";
		boolean isTextContains = text.contains(errorText);
		boolean isButtonNextNotEnable = !bot.button(ButtonAction.BUTTON_NEXT).isEnabled();
		bot.button(ButtonAction.BUTTON_CANCEL).click();
		if (!isTextContains || !isButtonNextNotEnable) {
			assertFalse(true);
		}
	}
}
