package creative.endless.growing.allergychecker;

import android.content.Context;
import android.preference.PreferenceManager;
import android.util.Log;

import java.sql.SQLTransactionRollbackException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.TreeMap;

import static android.content.ContentValues.TAG;

/**
 * @author Jonathan Alexander Norberg
 * @version 2018-05-18
 */

public class CollectAllAllergies {
    public static HashSet<String> getAllergies(Context context){
        HashSet<String> integers = new HashSet<>();
        HashMap<Integer, Integer> hashMap = ValidateAllergiesPreferences.setupAllergy();
        for (int key : hashMap.keySet()) {
            if(PreferenceManager.getDefaultSharedPreferences(context).getBoolean(String.valueOf(hashMap.get(key)),true)){
                integers.add(context.getString(key));
            }
        }
        Log.d(TAG, "getAllergies: "+ integers);
        return integers;
    }
}
