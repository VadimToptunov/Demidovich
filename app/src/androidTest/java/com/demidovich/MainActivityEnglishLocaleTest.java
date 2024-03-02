package com.demidovich;

import android.content.Context;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.platform.app.InstrumentationRegistry;

import com.demidovich.helpers.ForceLocaleRule;
import com.demidovich.helpers.Helper;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Locale;


@RunWith(AndroidJUnit4.class)
@LargeTest
public class MainActivityEnglishLocaleTest {

    private static Context context;
    private static Helper helper;
    @Rule
    public final ActivityScenarioRule<MainActivity> activityScenarioRule
            = new ActivityScenarioRule<>(MainActivity.class);

    @Before
    public void setUp() {
        context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        ForceLocaleRule localeRule = new ForceLocaleRule();
        localeRule.setLocale(Locale.US);
        helper = new Helper(context);
    }

    @Test
    public void applicationEnglishNameTest() {
        helper.checkLocaleTextExists("Demidovich Password Generator", R.string.app_name);
    }

    @Test
    public void textViewDefaultEnglishTextTest() {
        helper.checkLocaleTextExists("Password", R.string.pass_default);
    }

    @Test
    public void checkSaveButtonEnglishTextTest() {
        helper.checkLocaleTextExists("Save", R.string.btn_save);
    }

    @Test
    public void checkGenerateButtonEnglishTextTest() {
        helper.checkLocaleTextExists("Generate", R.string.btn_generate);
    }

    @Test
    public void checkPasswordIsSavedToastEnglishTextTest() {
        helper.checkLocaleTextExists("Password has been saved", R.string.toast_text);
    }
}
