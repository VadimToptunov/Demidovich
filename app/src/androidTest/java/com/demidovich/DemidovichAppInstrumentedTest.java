package com.demidovich;
import static androidx.test.espresso.matcher.ViewMatchers.isCompletelyDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.not;

import android.content.Context;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.filters.LargeTest;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.junit.Assert.*;

import com.demidovich.pageObject.DemidovichPageObject;

/**
 * Instrumented test, which will execute on Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class DemidovichAppInstrumentedTest {
    DemidovichPageObject demidovich = new DemidovichPageObject();

    @Rule
    public ActivityScenarioRule<MainActivity> activityScenarioRule
            = new ActivityScenarioRule<>(MainActivity.class);

    @Test
    public void useAppContext() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        assertEquals("com.demidovich", appContext.getPackageName());
    }

    @Test
    public void applicationNameTest(){
       Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        assertEquals("Demidovich", appContext.getText(R.string.app_name));
    }

    @Test
    public void textViewDefaultTextTest(){
        demidovich.texViewMatches(withText("Пароль"));
    }

    @Test
    public void passwordIsCompletelyDisplayedTest(){
        demidovich.clickGenerateButton();
        demidovich.texViewMatches(isCompletelyDisplayed());
    }

    @Test
    public void generateButtonIsDisplayedTest(){
        demidovich.checkButtonIsDisplayed();
    }

    @Test
    public void passwordIsNotEmptyTest(){
        demidovich.clickGenerateButton();
        demidovich.texViewMatches(not(withText("")));
    }

    @Test
    public void passwordMatchesToRegex(){
        String regex = "[a-zA-Z0-9<>/\\!@$%^&*()_+=\\-{}\\\"|]{8,16}";
        activityScenarioRule.getScenario()
                .onActivity(activity -> demidovich.textViewMatchesRegex(activity, regex));

    }
}