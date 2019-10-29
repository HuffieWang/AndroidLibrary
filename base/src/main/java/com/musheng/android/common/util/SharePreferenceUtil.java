package com.musheng.android.common.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.google.gson.Gson;

/**
 * Author      : MuSheng
 * CreateDate  : 2019/9/9 17:59
 * Description :
 */
public class SharePreferenceUtil {

    private static SharedPreferences sharedPreferences;
    private static Context mContext;

    public static SharedPreferences getSharePreference() {
        return sharedPreferences;
    }

    public static void init(Context context) {
        mContext = context;
        sharedPreferences = mContext.getSharedPreferences("BoChat_Preference", Context.MODE_PRIVATE);
    }

    public static void saveAccount(String username) {
        try {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("account", username);
            editor.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void saveString(String key, String value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public static void saveInt(String key, int value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(key, value);
        editor.apply();
    }

    public static String getString(String key) {
        return sharedPreferences.getString(key, null);
    }

    public static int getInt(String key) {
        return sharedPreferences.getInt(key, -1);
    }

    public static String getAccount() {
        return sharedPreferences.getString("account", null);
    }

    public static boolean getBooleanValue(String key) {
        return sharedPreferences.getBoolean(key, false);
    }

    public static void setBooleanValue(String key, boolean value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    public static <T> void saveEntity(String key, T entity) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String ENTITY_JSON = gson.toJson(entity);
        editor.putString(key, ENTITY_JSON);
        editor.apply();
    }

    public static <T> T getEntity(String key, Class<T> type) {
        return getEntity(key, type, new Gson());
    }

    public static <T> T getEntity(String key, Class<T> type, Gson gson) {
        String entityJson = sharedPreferences.getString(key, "");
        if (!TextUtils.isEmpty(entityJson)) {
            return gson.fromJson(entityJson, type);
        }
        return null;
    }
}
