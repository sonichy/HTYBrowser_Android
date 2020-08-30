package com.hty.browser;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.PreferenceActivity;

public class SettingsActivity extends PreferenceActivity implements OnSharedPreferenceChangeListener{

    private EditTextPreference ETP_homepage, ETP_filter, ETP_highlight;
    SharedPreferences SP;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preference);
        ETP_homepage = (EditTextPreference) findPreference("homepage");
        ETP_filter = (EditTextPreference) findPreference("filter");
        ETP_highlight = (EditTextPreference) findPreference("highlight");
        SP = getPreferenceScreen().getSharedPreferences();
        SP.registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    // 启动时显示
    protected void onResume() {
        super.onResume();
        ETP_homepage.setSummary(SP.getString("homepage", "http://www.baidu.com"));
        ETP_filter.setSummary(SP.getString("filter", ""));
        ETP_highlight.setSummary(SP.getString("highlight", ""));
    }

    @Override
    // 修改后显示
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if(key.equals("homepage")){
            ETP_homepage.setSummary(sharedPreferences.getString(key, ""));
        }else if(key.equals("filter")){
            ETP_filter.setSummary(sharedPreferences.getString(key, ""));
        }else if(key.equals("highlight")){
            ETP_highlight.setSummary(sharedPreferences.getString(key, ""));
        }
    }

}