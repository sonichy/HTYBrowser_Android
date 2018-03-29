package com.hty.browser;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.PreferenceActivity;

public class SettingsActivity extends PreferenceActivity implements OnSharedPreferenceChangeListener{

    private EditTextPreference ETP_homepage;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preference);
        ETP_homepage = (EditTextPreference) findPreference("homepage");
        sharedPreferences = getPreferenceScreen().getSharedPreferences();
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    // Activity启动时，修改列表项目值
    protected void onResume() {
        super.onResume();
        ETP_homepage.setSummary(sharedPreferences.getString("homepage","http://www.baidu.com"));
    }

    @Override
    // 编辑后确定，修改列表项目值
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if(key.equals("homepage")){
            ETP_homepage.setSummary(sharedPreferences.getString(key,""));
        }
    }
}
