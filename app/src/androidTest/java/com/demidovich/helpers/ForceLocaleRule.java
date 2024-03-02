package com.demidovich.helpers;

import android.content.res.Configuration;
import android.content.res.Resources;

import androidx.test.platform.app.InstrumentationRegistry;

import java.util.Locale;


public class ForceLocaleRule {


    public void setLanguage(String lang, String country) {
        Locale locale = new Locale(lang, country);
        setLocale(locale);
    }

    public void setLocale(Locale locale) {
        Resources resources = InstrumentationRegistry.getInstrumentation().getTargetContext().getResources();
        Locale.setDefault(locale);
        Configuration config = resources.getConfiguration();
        config.setLocale(locale);
        resources.updateConfiguration(config, resources.getDisplayMetrics());
    }
}
