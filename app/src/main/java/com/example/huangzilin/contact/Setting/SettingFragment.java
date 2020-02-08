package com.example.huangzilin.contact.Setting;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.PreferenceFragment;
import android.util.Log;

import com.example.huangzilin.contact.R;

public class SettingFragment extends PreferenceFragment implements OnSharedPreferenceChangeListener {
	
    private EditTextPreference mEtPreference;
    private EditTextPreference mEtPreference1;
    private EditTextPreference mEtPreference2;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.preferences);
        initPreferences();
        Bundle bundle=this.getArguments();
        String content = bundle.getString("data");
        Log.e("测试setting",content);
       // txt_content.setText(content);
    }
    
    private void initPreferences() {
    	mEtPreference = (EditTextPreference)findPreference("edittext_key");
        mEtPreference1 = (EditTextPreference)findPreference("edittext_key1");
        mEtPreference2 = (EditTextPreference)findPreference("edittext_key2");
    }
    
    @Override
    public void onResume() {
        super.onResume();

        // Setup the initial values
        SharedPreferences sharedPreferences = getPreferenceScreen().getSharedPreferences();
        mEtPreference.setSummary(sharedPreferences.getString("edittext_key", "linc"));
        mEtPreference1.setSummary(sharedPreferences.getString("edittext_key1", "linc"));
        mEtPreference2.setSummary(sharedPreferences.getString("edittext_key2", "linc"));
        
        // Set up a listener whenever a key changes
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        // Unregister the listener whenever a key changes
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }    
    
	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals("edittext_key")) {
        	mEtPreference.setSummary(
                    sharedPreferences.getString(key, "https://www.baidu.com"));
        }else if (key.equals("edittext_key1")){
            mEtPreference1.setSummary(
                    sharedPreferences.getString(key, "https://www.baidu.com"));
        }else if (key.equals("edittext_key2")){
            mEtPreference2.setSummary(
                    sharedPreferences.getString(key, "https://www.baidu.com"));
        }
	}
}
