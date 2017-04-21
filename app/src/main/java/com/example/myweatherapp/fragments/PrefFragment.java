package com.example.myweatherapp.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.SwitchPreference;
import android.widget.Toast;

import com.example.myweatherapp.R;

public class PrefFragment extends PreferenceFragment {

    boolean update = true;
    private static SharedPreferences mUpdate;
    public static SharedPreferences.Editor mEditor;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);

        mUpdate = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
        update = mUpdate.getBoolean("update", false);

        String s = mUpdate.getString("summary", "123");

        final SwitchPreference sp1 = (SwitchPreference)findPreference("sp1");
        sp1.setSummary(update == false ? "Disabled" : "Enabled");

        final ListPreference list = (ListPreference)findPreference("list");
        list.setSummary(s);

            sp1.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {

                @Override
                public boolean onPreferenceChange(Preference preference,
                                                  Object newValue) {
                    boolean switched = ((SwitchPreference) preference)
                            .isChecked();
                    update = !switched;
                    mEditor = mUpdate.edit();
                    mEditor.putBoolean("update", update);
                    mEditor.commit();
                    sp1.setSummary(update == false ? "Disabled" : "Enabled");
                    return true;
                }

            });

        list.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            public boolean onPreferenceChange(Preference preference, Object newValue) {

                int index = list.findIndexOfValue(newValue.toString());
                if (index != -1)
                {
                    String s = String.valueOf(list.getEntryValues()[index]);
                    Toast.makeText(getActivity().getApplicationContext(), s, Toast.LENGTH_LONG).show();
                    mEditor = mUpdate.edit();
                    mEditor.putString("updateTime", s);
                    mEditor.putString("summary", String.valueOf(list.getEntries()[index]));
                    mEditor.commit();
                    list.setSummary(list.getEntries()[index]);
                }
                return true;
            }
        });
    }
}