package com.nu.art.cyborg.demo;

import android.support.test.runner.AndroidJUnit4;

import com.nu.art.automation.core.AutomationScenario;
import com.nu.art.automation.models.Action_Delay;
import com.nu.art.belog.Logger;
import com.nu.art.core.tools.DateTimeTools;
import com.nu.art.cyborg.automation.core.AutomationManager;

import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest
	extends Logger {

	@Test
	public void useAppContext()
		throws Exception {
		AutomationManager automationManager = new AutomationManager();
		automationManager.init();
		AutomationScenario scenario = new AutomationScenario();
		//		scenario.addStep_viaCode(new Action_StartActivity().setActivityName(ApplicationLauncher.class.getName()));
		scenario.addStep_viaCode(new Action_Delay((100 * DateTimeTools.Second)));
		automationManager.executeScenario(scenario);
		logInfo("Started!");
	}
}
