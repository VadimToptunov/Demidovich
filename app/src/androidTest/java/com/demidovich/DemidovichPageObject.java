package com.demidovich;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isCompletelyDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import android.view.View;

import org.hamcrest.Matcher;

public class DemidovichPageObject {
    public void clickGenerateButton(){
        onView(withId(R.id.btn_generate)).perform(click());
    }

    public void texViewMatches(Matcher<View> input){
        onView(withId(R.id.tv_pass)).check(matches(input));
    }

    public void checkButtonIsDisplayed(){
        onView(withId(R.id.btn_generate)).check(matches(isCompletelyDisplayed()));
    }
}
