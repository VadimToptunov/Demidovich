package com.demidovich.helpers;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import androidx.test.InstrumentationRegistry;

import org.jetbrains.annotations.NotNull;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

import java.util.Locale;


public class ForceLocaleRule implements TestRule {

    private final String testLocale;
    private Locale deviceLocale;
    private Context context;

    public ForceLocaleRule(Context context, String testLocale) {
        this.testLocale = testLocale;
        this.context = context;
    }

    @Override
    public Statement apply(final Statement base, Description description) {
        return new Statement() {
            public void evaluate() throws Throwable {
                try {
                    if (testLocale != null) {
                        switch (testLocale) {
                            case "ru":
                                Locale locale = new Locale("ru", "RU", "RU");
                                setLocale(locale);
                            case "ua":
                                setLocale(new Locale("uk", "UA", "UA"));
                            case "us":
                                setLocale(Locale.US);
                        }
                    }
                    base.evaluate();
                } finally {
                    if (deviceLocale != null) {
                        setLocale(deviceLocale);
                    }
                }
            }
        };
    }

    public void setLanguage(String lang, String country){
        Locale locale = new Locale(lang, country);
        setLocale(locale);
    }

    public void setLocale(Locale locale) {
        context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        Resources resources = InstrumentationRegistry.getTargetContext().getResources();
        Locale.setDefault(locale);
        Configuration config = resources.getConfiguration();
        config.setLocale(locale);
        resources.updateConfiguration(config, resources.getDisplayMetrics());
    }
}
