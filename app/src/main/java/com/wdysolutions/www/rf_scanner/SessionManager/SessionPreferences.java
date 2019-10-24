package com.wdysolutions.www.rf_scanner.SessionManager;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashMap;


public class SessionPreferences {

    SharedPreferences pref;
    public SharedPreferences.Editor editor;
    Context _context;
    int PRIVATE_MODE = 0;
    private static final String PREF_NAME = "userPref";
    private static final String IS_LOGIN = "isFontSaved";
    public String KEY_COMPANY_ID = "company_id";
    public String KEY_USER_ID = "user_id";
    public String KEY_COMPANY_CODE = "company_code";
    public String KEY_USER_CODE = "user_code";
    public String KEY_CATEGORY_ID = "categor_id";

    public SessionPreferences(Context context){
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void saveUserInfo(String company_id, String user_id, String company_code,String user_code,String category_id){
        editor.putBoolean(IS_LOGIN, true);
        editor.putString(KEY_COMPANY_ID, company_id);
        editor.putString(KEY_USER_ID, user_id);
        editor.putString(KEY_COMPANY_CODE, company_code);
        editor.putString(KEY_USER_CODE, user_code);
        editor.putString(KEY_CATEGORY_ID, category_id);
        editor.commit();
    }

    public HashMap<String, String> getUserDetails(){
        HashMap<String, String> user = new HashMap<String, String>();
        user.put(KEY_COMPANY_ID, pref.getString(KEY_COMPANY_ID, null));
        user.put(KEY_USER_ID, pref.getString(KEY_USER_ID, null));
        user.put(KEY_COMPANY_CODE, pref.getString(KEY_COMPANY_CODE, null));
        user.put(KEY_USER_CODE, pref.getString(KEY_USER_CODE, null));
        user.put(KEY_CATEGORY_ID, pref.getString(KEY_CATEGORY_ID, null));
        return user;
    }

    public boolean isLogin(){
        return pref.getBoolean(IS_LOGIN, false);
    }
}
