package com.jinsen.multifinder;

import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.RingtonePreference;
import android.util.Log;

import com.jinsen.multifinder.Events.AlarmMessage;
import com.jinsen.multifinder.Events.TimeMessage;

import de.greenrobot.event.EventBus;

/**
 * Created by Jinsen on 15/6/3.
 */
public class SettingFragment extends PreferenceFragment {
    private static final String TAG = SettingFragment.class.getSimpleName();

    //Preference Keys
    public static final String KEY_ALARM = "pref_key_alarm";
    public static final String KEY_TIME = "pref_key_time";

    private RingtonePreference pRingtone;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.setting_pref);
        pRingtone = ((RingtonePreference) findPreference(KEY_ALARM));

        String cacheRingtone = PreferenceUtil.getString(getActivity(), KEY_ALARM);
        pRingtone.setSummary(cacheRingtone);
        pRingtone.setOnPreferenceChangeListener(new SetupChangeListener());
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    public class SetupChangeListener implements Preference.OnPreferenceChangeListener {
        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {
            if (preference instanceof RingtonePreference) {
                RingtonePreference temp = ((RingtonePreference) preference);
                Uri uri = Uri.parse(newValue.toString());
                PreferenceUtil.putString(getActivity(), KEY_ALARM, newValue.toString());
                Log.d(TAG, "url= " + uri.toString());

                EventBus.getDefault().post(new AlarmMessage(newValue.toString()));
                return true;
            }else if (preference instanceof ListPreference) {
                ListPreference temp = ((ListPreference) preference);

            // preferences may be changed auto-ly after modified, these code dont work
                PreferenceUtil.putString(getActivity(), KEY_TIME, newValue.toString());
                int realtime = ((Integer.valueOf(newValue.toString())) + 1 ) * 5;
                temp.setSummary(realtime + "");
                Log.d(TAG, "time= " + newValue.toString());

                EventBus.getDefault().post(new TimeMessage(realtime));
                return true;
        }
            return false;
        }
    }

    private String getRingtoneName(Uri uri) {
        Ringtone r = RingtoneManager.getRingtone(this.getActivity(), uri);
        return r.getTitle(this.getActivity());
    }
}
