package com.demidovich;

import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.junit.Assert.assertEquals;

import android.content.Context;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.demidovich.helpers.ForceLocaleRule;

import static com.demidovich.pageObject.MainActivityPageObject.checkToastTextMatches;
import static com.demidovich.pageObject.MainActivityPageObject.clickGenerateButton;
import static com.demidovich.pageObject.MainActivityPageObject.clickSaveButton;
import static com.demidovich.pageObject.MainActivityPageObject.texViewMatches;
import static com.demidovich.pageObject.MainActivityPageObject.textGenerateButtonMatches;
import static com.demidovich.pageObject.MainActivityPageObject.textSaveButtonMatches;

import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Locale;


@RunWith(AndroidJUnit4.class)
@LargeTest
public class MainActivityEnglishLocaleTest {

    private static Context context;

    @ClassRule
    public static final ForceLocaleRule localeTestRule = new ForceLocaleRule(context, "us");

    @Rule
    public final ActivityTestRule<MainActivity> myActivityRule = new ActivityTestRule<>(MainActivity.class);

    @Before
    public void setUp() {
        context = InstrumentationRegistry.getInstrumentation().getTargetContext();
    }

    @Test
    public void applicationEnglishNameTest(){
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        assertEquals("Demidovich Password Generator", appContext.getText(R.string.app_name));
    }

    @Test
    public void textViewDefaultEnglishTextTest(){
        texViewMatches(withText("Password"));
    }

    @Test
    public void checkSaveButtonEnglishTextTest(){
        textSaveButtonMatches("Save");
    }

    @Test
    public void checkGenerateButtonEnglishTextTest(){
        textGenerateButtonMatches("Generate");
    }

    @Test
    public void checkPasswordIsSavedToastEnglishTextTest(){
        clickGenerateButton();
        clickSaveButton();
        checkToastTextMatches("Password has been saved");
    }
}
