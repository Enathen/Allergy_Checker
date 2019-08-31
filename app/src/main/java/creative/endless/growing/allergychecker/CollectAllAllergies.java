package creative.endless.growing.allergychecker;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.preference.PreferenceManager;

import java.util.HashMap;
import java.util.Locale;

import androidx.annotation.NonNull;

/**
 * @author Jonathan Alexander Norberg
 * @version 2018-05-18
 */

public class CollectAllAllergies {
    public static HashMap<String, AllergiesClass> getAllergies(Context context, Locale locale) {
        HashMap<String, AllergiesClass> integers = new HashMap<>();
        HashMap<Integer, Integer> hashMap = ValidateAllergiesPreferences.setupAllergy();
        for (int key : hashMap.keySet()) {
            if (PreferenceManager.getDefaultSharedPreferences(context).getBoolean(String.valueOf(hashMap.get(key)), false)) {
                integers.put(getLocalizedResources(context, locale).getString(key), new AllergiesClass(context.getString(key)));
            }
        }

        return integers;
    }

    @NonNull
    static Resources getLocalizedResources(Context context, Locale desiredLocale) {
        Configuration conf = context.getResources().getConfiguration();
        conf = new Configuration(conf);
        conf.setLocale(desiredLocale);
        Context localizedContext = context.createConfigurationContext(conf);
        return localizedContext.getResources();
    }
}
