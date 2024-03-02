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
public class MainActivityUkranianLocaleTest {
    public static final ForceLocaleRule localeTestRule = new ForceLocaleRule();
    private static Context context;
    private static Helper helper;
    @Rule
    public final ActivityScenarioRule<MainActivity> activityScenarioRule
            = new ActivityScenarioRule<>(MainActivity.class);

    @Before
    public void setUp() {
        context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        localeTestRule.setLocale(new Locale("ua"));
        helper = new Helper(context);
    }


    @Test
    public void applicationUkranianNameTest() {
        helper.checkLocaleTextExists("Генератор паролів Демидович", R.string.app_name);
    }

    @Test
    public void textViewDefaultUkranianTextTest() {
        helper.checkLocaleTextExists("Пароль", R.string.pass_default);
    }

    @Test
    public void checkSaveButtonUkranianTextTest() {
        helper.checkLocaleTextExists("Зберегти", R.string.btn_save);
    }

    @Test
    public void checkGenerateButtonUkranianTextTest() {
        helper.checkLocaleTextExists("Згенерувати", R.string.btn_generate);
    }

    @Test
    public void checkPasswordIsSavedToastUkranianTextTest() {
        helper.checkLocaleTextExists("Пароль збережено", R.string.toast_text);
    }
}
