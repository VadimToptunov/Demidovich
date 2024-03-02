package com.demidovich.helpers;

import static org.junit.Assert.assertEquals;

import android.content.Context;

public class Helper {
    private final Context context;

    public Helper(Context context) {
        this.context = context;
    }

    public void checkLocaleTextExists(String expectedText, int stringRes) {
        assertEquals(expectedText, context.getText(stringRes));
    }
}
