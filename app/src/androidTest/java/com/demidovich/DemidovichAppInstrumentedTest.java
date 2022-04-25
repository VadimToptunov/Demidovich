package com.demidovich;

import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.isCompletelyDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
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

import com.demidovich.pageObject.MainActivityPageObject;

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

    MainActivityPageObject mainActivityPageObject = new MainActivityPageObject();


//    @Rule
//    ActivityScenarioRule<ListPasswordsActivity> listPasswordsActivityActivityScenarioRule =
//            new ActivityScenarioRule<>(ListPasswordsActivity.class);
//

    @Rule
    public final ActivityTestRule<ListPasswordsActivity> rule =
            new ActivityTestRule<>(ListPasswordsActivity.class, true, false);

    @Test
    public void useAppContext() {
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        assertEquals("com.demidovich", appContext.getPackageName());
    }

    @Test
    public void applicationNameTest(){
       Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        assertEquals("Demidovich Password Generator", appContext.getText(R.string.app_name));
    }

    @Test
    public void textViewDefaultTextTest(){
        mainActivityPageObject.texViewMatches(withText(R.string.tv_pass));
    }

    @Test
    public void passwordIsCompletelyDisplayedTest(){
        mainActivityPageObject.clickGenerateButton();
        mainActivityPageObject.texViewMatches(isCompletelyDisplayed());
    }

    @Test
    public void checkAllElementsAreDisplayedTest(){
        mainActivityPageObject.checkGoToSavedPasswordsListButtonIsDisplayed();
        mainActivityPageObject.checkTextViewPasswordIsDisplayed();
        mainActivityPageObject.checkSaveButtonIsDisplayed();
        mainActivityPageObject.checkGenerateButtonIsDisplayed();
    }

    @Test
    public void passwordIsNotEmptyTest(){
        mainActivityPageObject.clickGenerateButton();
        mainActivityPageObject.texViewMatches(not(withText("")));
    }

    @Test
    public void passwordMatchesToRegex(){
        String regex = "[a-zA-Z0-9<>/\\\\!@$%^&*()_+=\\-{}\\\"|]{8,21}";
        activityScenarioRule.getScenario()
                .onActivity(activity -> mainActivityPageObject.textViewMatchesRegex(activity, regex));

    }

    @Test
    public void saveButtonIsInitiallyInactiveTest(){
        mainActivityPageObject.checkButtonSaveIsInactive();
    }

    @Test
    public void saveButtonIsActiveAfterGeneratingPasswordTest(){
        mainActivityPageObject.clickGenerateButton();
        mainActivityPageObject.checkButtonSaveIsActive();
    }

    @Test
    public void saveButtonIsInactiveAfterSavingPasswordTest(){
        mainActivityPageObject.clickGenerateButton();
        mainActivityPageObject.clickSaveButton();
        mainActivityPageObject.checkButtonSaveIsInactive();
    }

    @Test
    public void toastTextTest(){
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        String toastText = appContext.getString(R.string.toast_text);
        mainActivityPageObject.clickGenerateButton();
        mainActivityPageObject.clickSaveButton();
        mainActivityPageObject.checkToastTextMatches(toastText);
    }

    @Test
    public void goToListPasswordsActivityAfterClickingButtonTest(){
        mainActivityPageObject.clickGoToListPasswordsButton();
        Intents.init();
        rule.launchActivity(new Intent());
        intended(hasComponent(ListPasswordsActivity.class.getName()));
        Intents.release();
    }
}