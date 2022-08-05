package com.demidovich.pageObject;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isEnabled;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.not;
import static com.demidovich.helpers.CommonActions.checkToast;
import static com.demidovich.helpers.CommonActions.checkElementIsDisplayed;
import static com.demidovich.helpers.CommonActions.clickButton;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.demidovich.MainActivity;
import com.demidovich.R;
import com.demidovich.helpers.CommonActions;
import com.demidovich.helpers.TextViewGetter;

import org.hamcrest.Matcher;


public class MainActivityPageObject {

    public static void clickGenerateButton(){
        clickButton(R.id.btn_generate);
    }

    public static void clickSaveButton(){
        clickButton(R.id.btn_save);
    }

    public static void clickGoToListPasswordsButton(){
        clickButton(R.id.btn_saved_passwords);
    }

    public static void texViewMatches(Matcher<View> input){
        onView(withId(R.id.tv_pass)).check(matches(input));
    }

    public static void textSaveButtonMatches(String text){
        onView(withId(R.id.btn_save)).check(matches(withText(text)));
    }

    public static void textGenerateButtonMatches(String text){
        onView(withId(R.id.btn_generate)).check(matches(withText(text)));
    }

    public static void checkToastTextMatches(String toastText){
        checkToast(toastText);
    }

    public static void textViewMatchesRegex(MainActivity activity, String regex) {
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

    public static void checkGenerateButtonIsDisplayed(){
        checkElementIsDisplayed(R.id.btn_generate);
    }

    public static void checkSaveButtonIsDisplayed(){
        checkElementIsDisplayed(R.id.btn_save);
    }

    public static void checkGoToSavedPasswordsListButtonIsDisplayed(){
        checkElementIsDisplayed(R.id.btn_saved_passwords);
    }

    public static void checkTextViewPasswordIsDisplayed(){
        checkElementIsDisplayed(R.id.tv_pass);
    }

    public static void checkButtonSaveIsInactive() {
        onView(withId(R.id.btn_save)).check(matches(not(isEnabled())));
    }


    public static void checkButtonSaveIsActive() {
        onView(withId(R.id.btn_save)).check(matches(isEnabled()));
    }
}
