package com.notfaqs.network_switch;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.TwoStatePreference;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class SettingsActivity extends PreferenceActivity {

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        initialize();
    }

    // setMobileDataEnabled is not available on Android 5.0
    private void enableMobile(boolean enabled) {
        Object conMgr = getSystemService(CONNECTIVITY_SERVICE);
        try {
            Class c = Class.forName(conMgr.getClass().getName());
            Method m = c.getDeclaredMethod("setMobileDataEnabled", Boolean.TYPE);
            m.setAccessible(true);
            m.invoke(conMgr, enabled);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    private void enableWifi(boolean enabled) {
        WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        wifiManager.setWifiEnabled(enabled);
    }

    private void initialize() {
        addPreferencesFromResource(R.xml.pref_network);

        TwoStatePreference wifiPref = (TwoStatePreference) findPreference("network_wifi");
        wifiPref.setChecked(isWifiEnabled());
        wifiPref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object value) {
                boolean enabled = Boolean.parseBoolean(value.toString());
                enableWifi(enabled);
                return true;
            }
        });

        TwoStatePreference mobilePref = (TwoStatePreference) findPreference("network_mobile");
        mobilePref.setChecked(isMobileEnabled());
        mobilePref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object value) {
                boolean enabled = Boolean.parseBoolean(value.toString());
                enableMobile(enabled);
                return true;
            }
        });
    }

    private boolean isMobileEnabled() {
        Object conMgr = getSystemService(CONNECTIVITY_SERVICE);
        try {
            final Class c = Class.forName(conMgr.getClass().getName());
            final Method m = c.getDeclaredMethod("getMobileDataEnabled");
            m.setAccessible(true);
            return (Boolean) m.invoke(conMgr);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return false;
    }

    private boolean isWifiEnabled() {
        ConnectivityManager conMgr = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        return conMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isAvailable();
    }
}
