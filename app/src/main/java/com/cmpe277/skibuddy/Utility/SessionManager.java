package com.cmpe277.skibuddy.Utility;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

import com.cmpe277.skibuddy.LoginActivity;
import com.cmpe277.skibuddy.MainActivity;
import com.cmpe277.skibuddy.Models.User;
import com.google.android.gms.auth.GoogleAuthUtil;

public class SessionManager {
    SharedPreferences pref;
    Editor editor;
    Context _context;

    int PRIVATE_MODE = 0;

    private static final String PREF_NAME = "SKI_BUDDY_PREF";

    private static final String IS_LOGGED_IN = "IS_LOGGED_IN";

    public static final String KEY_NAME = "NAME";

    public static final String KEY_EMAIL = "EMAIL";

    public static final String KEY_TAG_LINE = "TAG_LINE";

    public static final String KEY_PROFILE_URL = "PROFILE_URL";

    public static final String KEY_IMAGE_URL = "IMAGE_URL";

    public static final String KEY_LATITUDE = "LATITUDE";
    public static final String KEY_LONGITUDE = "LONGITUDE";
    public static final String KEY_LOCATION_UPDATE_TIME = "LOCATION_UPDATE_TIME";

    private static final String IS_DISCONNECTED = "IS_DISCONNECTED";

    private static final String TOKEN = "TOKEN";

    public SessionManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void createLoginSession(User user) {
        editor.putBoolean(IS_LOGGED_IN, true);
        editor.putString(KEY_NAME, user.getUserName());
        editor.putString(KEY_EMAIL, user.getUserId());
        editor.putString(KEY_TAG_LINE, user.getTagLine());
        editor.putString(KEY_PROFILE_URL, user.getUrl());
        editor.putString(KEY_IMAGE_URL, user.getImage());
        editor.putString(KEY_LATITUDE, user.getLatitude());
        editor.putString(KEY_LONGITUDE, user.getLongitude());
        editor.commit();

        Intent i = new Intent(_context, MainActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        _context.startActivity(i);
    }

    public boolean checkLogin() {
        if (!this.isLoggedIn()) {
            Intent i = new Intent(_context, LoginActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            _context.startActivity(i);
            return false;
        }
        return true;
    }

    public void setToken(String token) {
        Log.d(Constatnts.TAG, "Token Set:" + token);
        editor.putString(TOKEN, token);
        editor.commit();
    }

    public String getToken() {
        String token = pref.getString(TOKEN, "");
        if (token.equalsIgnoreCase("")) {
            token = requestAccessToken();
            setToken(token);
        }
        return token;
    }

    public String getLoggedInMail() {
        return pref.getString(KEY_EMAIL, "");
    }

    public void logoutUser() {
        clearUserDetails();

        Intent i = new Intent(_context, LoginActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        _context.startActivity(i);
    }

    public void clearUserDetails() {
        editor.remove(IS_LOGGED_IN);
        editor.remove(KEY_NAME);
        editor.remove(KEY_EMAIL);
        editor.remove(KEY_TAG_LINE);
        editor.remove(KEY_PROFILE_URL);
        editor.remove(KEY_IMAGE_URL);
        editor.remove(TOKEN);
        editor.commit();
    }

    public User getLoggedInUserDetails(){
        User user = new User();
        user.setUserId(pref.getString(KEY_EMAIL, ""));
        user.setUserName(pref.getString(KEY_NAME, ""));
        user.setImage(pref.getString(KEY_IMAGE_URL, ""));
        user.setTagLine(pref.getString(KEY_TAG_LINE, ""));
        user.setUrl(pref.getString(KEY_PROFILE_URL, ""));
        user.setLatitude(pref.getString(KEY_LATITUDE, ""));
        user.setLongitude(pref.getString(KEY_LONGITUDE, ""));
        return user;
    }

    public boolean isLoggedIn() {
        return pref.getBoolean(IS_LOGGED_IN, false);
    }

    public boolean isDisconnected() {
        return pref.getBoolean(IS_DISCONNECTED, true);
    }

    public void setIsDisconnected(boolean value) {
        editor.putBoolean(IS_DISCONNECTED, value);
        editor.commit();
    }

    public String requestAccessToken() {
        String accessToken = "";
        try {
            accessToken = GoogleAuthUtil.getToken(_context, getLoggedInMail(), Constatnts.SCOPE_STRING);
        } catch (Exception e) {
            Log.e(Constatnts.TAG, "Exception in Access Token Request:", e);
        }
        return accessToken;
    }
}
