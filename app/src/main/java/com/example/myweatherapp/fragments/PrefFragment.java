package com.example.myweatherapp.fragments;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.SwitchPreference;
import android.widget.Toast;

import com.example.myweatherapp.R;
import com.example.myweatherapp.services.MyService;

import static android.content.Context.ALARM_SERVICE;

public class PrefFragment extends PreferenceFragment {

    Intent alarmIntent;
    PendingIntent pendingIntent;
    AlarmManager alarmManager;

    boolean update = true;
    private static SharedPreferences mUpdate;
    public static SharedPreferences.Editor mEditor;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);

        alarmIntent = new Intent(getActivity(), MyService.class);
        pendingIntent = PendingIntent.getService(getActivity(), 0, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager = (AlarmManager) getActivity().getSystemService(ALARM_SERVICE);

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
                    if(update) {
                        long period = (Long.valueOf(mUpdate.getString("updateTime", "1"))) * 1000;
                        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), period, pendingIntent);
                    }
                    else {
                        alarmManager.cancel(pendingIntent);
                    }
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