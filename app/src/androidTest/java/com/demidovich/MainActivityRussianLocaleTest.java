package com.demidovich;

import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static com.demidovich.pageObject.MainActivityPageObject.checkToastTextMatches;
import static com.demidovich.pageObject.MainActivityPageObject.clickGenerateButton;
import static com.demidovich.pageObject.MainActivityPageObject.clickSaveButton;
import static com.demidovich.pageObject.MainActivityPageObject.texViewMatches;
import static com.demidovich.pageObject.MainActivityPageObject.textGenerateButtonMatches;
import static com.demidovich.pageObject.MainActivityPageObject.textSaveButtonMatches;
import static org.junit.Assert.assertEquals;

import android.content.Context;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.demidovich.helpers.ForceLocaleRule;

import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Locale;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class MainActivityRussianLocaleTest {
    private static Context context;

    @ClassRule
    public static final ForceLocaleRule localeTestRule = new ForceLocaleRule(context, "ru");

    @Rule
    public final ActivityScenarioRule<MainActivity> activityScenarioRule
            = new ActivityScenarioRule<>(MainActivity.class);

    @Before
    public void setUp(){
        context = InstrumentationRegistry.getInstrumentation().getTargetContext();
    }


    @Test
    public void applicationRussianNameTest(){
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        assertEquals("Генератор паролей Демидович", appContext.getText(R.string.app_name));
    }

    @Test
    public void textViewDefaultRussianTextTest(){
        texViewMatches(withText("Пароль"));
    }

    @Test
    public void checkSaveButtonRussianTextTest(){
        textSaveButtonMatches("Сохранить");
    }

    @Test
    public void checkGenerateButtonRussianTextTest(){
        textGenerateButtonMatches("Сгенерировать");
    }

    @Test
    public void checkPasswordIsSavedToastRussianTextTest(){
        clickGenerateButton();
        clickSaveButton();
        checkToastTextMatches("Пароль сохранён");
    }
}
