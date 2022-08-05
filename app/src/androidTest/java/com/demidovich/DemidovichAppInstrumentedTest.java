package com.demidovich;

import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.isCompletelyDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static com.demidovich.pageObject.MainActivityPageObject.checkButtonSaveIsActive;
import static com.demidovich.pageObject.MainActivityPageObject.checkButtonSaveIsInactive;
import static com.demidovich.pageObject.MainActivityPageObject.checkGenerateButtonIsDisplayed;
import static com.demidovich.pageObject.MainActivityPageObject.checkGoToSavedPasswordsListButtonIsDisplayed;
import static com.demidovich.pageObject.MainActivityPageObject.checkSaveButtonIsDisplayed;
import static com.demidovich.pageObject.MainActivityPageObject.checkTextViewPasswordIsDisplayed;
import static com.demidovich.pageObject.MainActivityPageObject.checkToastTextMatches;
import static com.demidovich.pageObject.MainActivityPageObject.clickGenerateButton;
import static com.demidovich.pageObject.MainActivityPageObject.clickGoToListPasswordsButton;
import static com.demidovich.pageObject.MainActivityPageObject.clickSaveButton;
import static com.demidovich.pageObject.MainActivityPageObject.texViewMatches;
import static com.demidovich.pageObject.MainActivityPageObject.textViewMatchesRegex;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertEquals;

import android.content.Context;
import android.content.Intent;

import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

@RunWith(AndroidJUnit4.class)
@LargeTest
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class DemidovichAppInstrumentedTest {

    @Rule
    public final ActivityScenarioRule<MainActivity> activityScenarioRule
            = new ActivityScenarioRule<>(MainActivity.class);

    @Rule
    public final ActivityTestRule<ListPasswordsActivity> rule =
            new ActivityTestRule<>(ListPasswordsActivity.class, true, false);

    @Test
    public void useAppContext() {
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        assertEquals("com.demidovich", appContext.getPackageName());
    }

    @Test
    public void textViewDefaultTextTest(){
        texViewMatches(withText(R.string.tv_pass));
    }

    @Test
    public void passwordIsCompletelyDisplayedTest(){
        clickGenerateButton();
        texViewMatches(isCompletelyDisplayed());
    }

    @Test
    public void checkAllElementsAreDisplayedTest(){
        checkGoToSavedPasswordsListButtonIsDisplayed();
        checkTextViewPasswordIsDisplayed();
        checkSaveButtonIsDisplayed();
        checkGenerateButtonIsDisplayed();
    }

    @Test
    public void passwordIsNotEmptyTest(){
        clickGenerateButton();
        texViewMatches(not(withText("")));
    }

    @Test
    public void passwordMatchesToRegex(){
        String regex = "[a-zA-Z0-9<>/\\\\!@$%^&*()_+=\\-{}\\\"|]{8,21}";
        activityScenarioRule.getScenario()
                .onActivity(activity -> textViewMatchesRegex(activity, regex));

    }

    @Test
    public void saveButtonIsInitiallyInactiveTest(){
        checkButtonSaveIsInactive();
    }

    @Test
    public void saveButtonIsActiveAfterGeneratingPasswordTest(){
        clickGenerateButton();
        checkButtonSaveIsActive();
    }

    @Test
    public void saveButtonIsInactiveAfterSavingPasswordTest(){
        clickGenerateButton();
        clickSaveButton();
        checkButtonSaveIsInactive();
    }

    @Test
    public void goToListPasswordsActivityAfterClickingButtonTest(){
        clickGoToListPasswordsButton();
        Intents.init();
        rule.launchActivity(new Intent());
        intended(hasComponent(ListPasswordsActivity.class.getName()));
        Intents.release();
    }
}