package com.demidovich.pageObject;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isCompletelyDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.test.espresso.matcher.ViewMatchers;

import com.demidovich.MainActivity;
import com.demidovich.R;
import com.demidovich.helpers.TextViewGetter;

import org.hamcrest.Matcher;


public class DemidovichPageObject {
    public void clickGenerateButton(){
        onView(ViewMatchers.withId(R.id.btn_generate)).perform(click());
    }

    public void texViewMatches(Matcher<View> input){
        onView(withId(R.id.tv_pass)).check(matches(input));
    }

    public void textViewMatchesRegex(MainActivity activity, String regex) {
        TextView textView;
        Button button;
        button = activity.findViewById(R.id.btn_generate);
        button.performClick();
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        textView = activity.findViewById(R.id.tv_pass);
        TextViewGetter textViewGetter = new TextViewGetter();
        textViewGetter.matchesToRegex(textView, regex);
    }

    public void checkButtonIsDisplayed(){
        onView(withId(R.id.btn_generate)).check(matches(isCompletelyDisplayed()));
    }
}
