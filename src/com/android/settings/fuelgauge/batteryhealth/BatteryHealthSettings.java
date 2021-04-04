/*
 * Copyright (C) 2020 KangOS
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.settings.fuelgauge.batteryhealth;

import android.annotation.Nullable;
import android.content.Context;
import android.os.Bundle;
import android.os.UserHandle;
import android.provider.SearchIndexableResource;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import androidx.preference.Preference;
import androidx.preference.Preference.OnPreferenceChangeListener;

import com.android.internal.logging.nano.MetricsProto;
import com.android.settings.fuelgauge.PowerGaugePreference;
import com.android.settings.R;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settings.SettingsPreferenceFragment;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.lang.Integer;
import java.util.ArrayList;
import java.util.List;

/**
 * Settings screen for battery health
 */
public class BatteryHealthSettings extends SettingsPreferenceFragment implements OnPreferenceChangeListener {

    private static final String TAG = "BatteryHealthSettings";
    private static final String KEY_CURRENT_BATTERY_CAPACITY = "current_battery_capacity";
    private static final String KEY_DESIGNED_BATTERY_CAPACITY = "designed_battery_capacity";
    private static final String KEY_BATTERY_CHARGE_CYCLES = "battery_charge_cycles";

    private String mBatDesCap;
    private String mBatCurCap;
    private String mBatChgCyc;

    PowerGaugePreference mCurrentBatteryCapacity;
    PowerGaugePreference mDesignedBatteryCapacity;
    PowerGaugePreference mBatteryChargeCycles;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.battery_health);

         mCurrentBatteryCapacity = (PowerGaugePreference) findPreference(
                KEY_CURRENT_BATTERY_CAPACITY);
        mDesignedBatteryCapacity = (PowerGaugePreference) findPreference(
                KEY_DESIGNED_BATTERY_CAPACITY);
        mBatteryChargeCycles = (PowerGaugePreference) findPreference(
                KEY_BATTERY_CHARGE_CYCLES);
                
                // Check availability of Battery Health
        Preference mDesignedHealthPref = (Preference) findPreference(KEY_DESIGNED_BATTERY_CAPACITY);
        if (!getResources().getBoolean(R.bool.config_supportBatteryHealth)) {
            getPreferenceScreen().removePreference(mDesignedHealthPref);
        }

        Preference mCurrentHealthPref = (Preference) findPreference(KEY_CURRENT_BATTERY_CAPACITY);
        if (!getResources().getBoolean(R.bool.config_supportBatteryHealth)) {
            getPreferenceScreen().removePreference(mCurrentHealthPref);
        }

        Preference mCyclesHealthPref = (Preference) findPreference(KEY_BATTERY_CHARGE_CYCLES);
        if (!getResources().getBoolean(R.bool.config_supportBatteryHealth)) {
            getPreferenceScreen().removePreference(mCyclesHealthPref);
        }
        
        mBatDesCap = getResources().getString(R.string.config_batDesCap);
        mBatCurCap = getResources().getString(R.string.config_batCurCap);
        mBatChgCyc = getResources().getString(R.string.config_batChargeCycle);
        
        mCurrentBatteryCapacity.setSubtitle(parseBatterymAhText(mBatCurCap));
        mDesignedBatteryCapacity.setSubtitle(parseBatterymAhText(mBatDesCap));
        mBatteryChargeCycles.setSubtitle(parseBatteryCycle(mBatChgCyc));
    }

    @Override
    public int getMetricsCategory() {
        return MetricsProto.MetricsEvent.KANGOS;
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object objValue) {
        return false;
    }

    private String parseBatterymAhText(String file) {
        try {
            return Integer.parseInt(readLine(file)) / 1000 + " mAh";
        } catch (IOException ioe) {
            Log.e(TAG, "Cannot read battery capacity from "
                    + file, ioe);
        } catch (NumberFormatException nfe) {
            Log.e(TAG, "Read a badly formatted battery capacity from "
                    + file, nfe);
        }
        return getResources().getString(R.string.status_unavailable);
    }

    private String parseBatteryCycle(String file) {
        try {
            return Integer.parseInt(readLine(file)) + " Cycles";
        } catch (IOException ioe) {
            Log.e(TAG, "Cannot read battery cycle from "
                    + file, ioe);
        } catch (NumberFormatException nfe) {
            Log.e(TAG, "Read a badly formatted battery cycle from "
                    + file, nfe);
        }
        return getResources().getString(R.string.status_unavailable);
    }

    /**
    * Reads a line from the specified file.
    *
    * @param filename The file to read from.
    * @return The first line up to 256 characters, or <code>null</code> if file is empty.
    * @throws IOException If the file couldn't be read.
    */
    @Nullable
    private String readLine(String filename) throws IOException {
        final BufferedReader reader = new BufferedReader(new FileReader(filename), 256);
        try {
            return reader.readLine();
        } finally {
            reader.close();
        }
    }
}
