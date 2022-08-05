package com.demidovich.helpers;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isCompletelyDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;

import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.espresso.matcher.ViewMatchers;

import com.demidovich.R;

public class CommonActions {
    public static void checkElementIsDisplayed(int elementId){
        onView(withId(elementId)).check(matches(isCompletelyDisplayed()));
    }

    public static void checkElementsAreDisplayed(int elementId){
        onView(allOf(withId(elementId), isDisplayed()));
    }

    public static void clickButton(int buttonId){
        onView(ViewMatchers.withId(buttonId)).perform(click());
    }

    public static void checkToast(String toastText){
        onView(withText(toastText)).inRoot(new ToastMatcher())
                .check(matches(isDisplayed()));
    }

    public static void checkElementDeletedOnPosition(int position){
        onView(withId(R.id.recyclerViewListPasswords)).perform(
                RecyclerViewActions.actionOnItemAtPosition(position,
                        RecycleViewClickAction.clickChildViewWithId(R.id.list_item_btn_delete)));
    }
}
