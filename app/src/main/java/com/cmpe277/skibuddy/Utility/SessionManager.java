package com.cmpe277.skibuddy.Utility;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

import com.cmpe277.skibuddy.LoginActivity;
import com.cmpe277.skibuddy.MainActivity;
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

    private static final String IS_DISCONNECTED = "IS_DISCONNECTED";

    private static final String TOKEN = "TOKEN";

    public SessionManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void createLoginSession(String name, String email) {
        editor.putBoolean(IS_LOGGED_IN, true);
        editor.putString(KEY_NAME, name);
        editor.putString(KEY_EMAIL, email);
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
        editor.remove(TOKEN);
        editor.commit();

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
