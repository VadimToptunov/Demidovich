package com.demidovich.pageObject;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isEnabled;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.Matchers.not;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.demidovich.MainActivity;
import com.demidovich.R;
import com.demidovich.helpers.CommonActions;
import com.demidovich.helpers.TextViewGetter;

import org.hamcrest.Matcher;


public class MainActivityPageObject {

    CommonActions commonActions = new CommonActions();

    public void clickGenerateButton(){
        commonActions.clickButton(R.id.btn_generate);
    }

    public void clickSaveButton(){
        commonActions.clickButton(R.id.btn_save);
    }

    public void clickGoToListPasswordsButton(){
        commonActions.clickButton(R.id.btn_saved_passwords);
    }

    public void texViewMatches(Matcher<View> input){
        onView(withId(R.id.tv_pass)).check(matches(input));
    }

    public void checkToastTextMatches(String toastText){
        commonActions.checkToast(toastText);
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

    public void checkGenerateButtonIsDisplayed(){
        commonActions.checkElementIsDisplayed(R.id.btn_generate);
    }

    public void checkSaveButtonIsDisplayed(){
        commonActions.checkElementIsDisplayed(R.id.btn_save);
    }

    public void checkGoToSavedPasswordsListButtonIsDisplayed(){
        commonActions.checkElementIsDisplayed(R.id.btn_saved_passwords);
    }

    public void checkTextViewPasswordIsDisplayed(){
        commonActions.checkElementIsDisplayed(R.id.tv_pass);
    }

    public void checkButtonSaveIsInactive() {
        onView(withId(R.id.btn_save)).check(matches(not(isEnabled())));
    }


    public void checkButtonSaveIsActive() {
        onView(withId(R.id.btn_save)).check(matches(isEnabled()));
    }
}
