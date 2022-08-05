package com.demidovich;

import static com.demidovich.pageObject.ListPasswordsActivityPageObject.checkListPasswordsActivityElementsAreDisplayed;
import static com.demidovich.pageObject.ListPasswordsActivityPageObject.checkRecyclerViewElementIsDeletedOnPressingDeleteButton;

import android.content.Intent;

import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;

import org.junit.After;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

@RunWith(AndroidJUnit4.class)
@LargeTest
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class DemidovichListPasswordsAppInstrumentationTest {

    @Rule
    public final ActivityTestRule<ListPasswordsActivity> rule =
            new ActivityTestRule<>(ListPasswordsActivity.class, true, false);

    @Before
    public void setup(){
        Intents.init();
        rule.launchActivity(new Intent());
    }

    @After
    public void tearDown(){
        Intents.release();
    }

    @Test
    public void passwordsElementsAreDisplayedTest(){
        checkListPasswordsActivityElementsAreDisplayed();
    }

    @Test
    public void savedPasswordIsDeletedTest(){
        checkRecyclerViewElementIsDeletedOnPressingDeleteButton(0);
    }
}
