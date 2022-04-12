package com.demidovich.helpers;

import static org.hamcrest.MatcherAssert.assertThat;

import android.widget.TextView;
import org.hamcrest.Matcher;
import org.hamcrest.text.MatchesPattern;

import java.util.regex.Pattern;

public class TextViewGetter {

    public void matchesToRegex(TextView textView, String regex){
        Matcher<String> matcher = new MatchesPattern(Pattern.compile(regex));
        String text = getTextViewText(textView);
        assertThat(text, matcher);
    }

    private String getTextViewText(TextView textView) {
        return textView.getText().toString();
    }


}
