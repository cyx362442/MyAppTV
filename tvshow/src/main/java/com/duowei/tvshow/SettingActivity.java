package com.duowei.tvshow;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.PreferenceActivity;
import android.os.Bundle;

import com.duowei.tvshow.contact.Consts;

public class SettingActivity extends PreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener {
    private EditTextPreference mEtPreference1;
    private EditTextPreference mEtPreference2;
    private EditTextPreference mEtPreference3;
    private ListPreference mListPreference;
    private CheckBoxPreference mCheckPreference;
    private SharedPreferences.Editor mEdit;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_setting);
        SharedPreferences preferences = getSharedPreferences("Users", Context.MODE_PRIVATE);
        mEdit = preferences.edit();
        addPreferencesFromResource(R.xml.preferenc);
        initPreferences();
    }
    private void initPreferences() {
        mEtPreference1 = (EditTextPreference)findPreference("edittext_key1");
        mEtPreference2 = (EditTextPreference)findPreference("edittext_key2");
        mEtPreference3 = (EditTextPreference)findPreference("edittext_key3");
        mListPreference = (ListPreference)findPreference(Consts.LIST_KEY);
        mCheckPreference = (CheckBoxPreference)findPreference(Consts.CHECKOUT_KEY);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Setup the initial values
        SharedPreferences sharedPreferences = getPreferenceScreen().getSharedPreferences();
        mListPreference.setSummary(sharedPreferences.getString(Consts.LIST_KEY, ""));
        mEtPreference1.setSummary(sharedPreferences.getString("edittext_key1", ""));
        mEtPreference2.setSummary(sharedPreferences.getString("edittext_key2", ""));
        mEtPreference3.setSummary(sharedPreferences.getString("edittext_key3", ""));
        // Set up a listener whenever a key changes
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Unregister the listener whenever a key changes
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals("edittext_key1")) { //服务器地址
            mEtPreference1.setSummary(sharedPreferences.getString(key, "20"));
            mEdit.putString("wurl",sharedPreferences.getString(key, "20"));
        }else if(key.equals("edittext_key2")){//微信ID
            mEtPreference2.setSummary(sharedPreferences.getString(key, "20"));
            mEdit.putString("weid",sharedPreferences.getString(key, "20"));
        }else if(key.equals("edittext_key3")){//门店ID
            mEtPreference3.setSummary(sharedPreferences.getString(key, "20"));
            mEdit.putString("storeid",sharedPreferences.getString(key, "20"));
        } else if(key.equals(Consts.LIST_KEY)) {
            mListPreference.setSummary(sharedPreferences.getString(key, ""));
            mEdit.putString("zoneNum",sharedPreferences.getString(key, ""));
        }
        mEdit.commit();
    }
}
