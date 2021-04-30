/*
 * Copyright (C) 2018 The LineageOS Project
 * Copyright (C) 2019 KangOS
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

package com.kangos.settings.preference.deviceinfo;

import java.io.IOException;
import android.content.Context;
import androidx.annotation.VisibleForTesting;
import android.os.SystemProperties;

import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceScreen;
import com.android.settings.utils.KangOSSpecUtils;

import com.android.settings.core.BasePreferenceController;
import com.android.settingslib.widget.LayoutPreference;
import com.android.settingslib.Utils;
import com.android.settings.core.PreferenceControllerMixin;

import android.widget.TextView;
import com.android.settings.R;

public class KangOSVersionPreferenceController extends BasePreferenceController {

    @VisibleForTesting
    private static final String KEY_KANGOS_VERSION_PROP = "ro.kangos.version";

    @VisibleForTesting
    TextView mKangOSVersionText;
    @VisibleForTesting
    TextView mKangOSVersionFlavourText;
    @VisibleForTesting
    TextView mDeviceNameText;
    @VisibleForTesting
    TextView mCpuText;
    @VisibleForTesting
    TextView mScreenResText;
    @VisibleForTesting
    TextView mBatteryText;
    @VisibleForTesting
    TextView mRamText;
    @VisibleForTesting
    TextView mMaintainerText;

    private PreferenceFragmentCompat mHost;
    private LayoutPreference mKangOSVersionLayoutPref;
    private Context mContext;

    public KangOSVersionPreferenceController(Context context, String preferenceKey) {
        super(context, preferenceKey);
        mContext = context;
    }

    @Override
    public int getAvailabilityStatus() {
        return AVAILABLE;
    }

    public void setFragment(PreferenceFragmentCompat fragment) {
        mHost = fragment;
    }

    @Override
    public void displayPreference(PreferenceScreen screen) {
        super.displayPreference(screen);
        mKangOSVersionLayoutPref = screen.findPreference(getPreferenceKey());
        mDeviceNameText = mKangOSVersionLayoutPref.findViewById(R.id.device_name_text);
        mCpuText = mKangOSVersionLayoutPref.findViewById(R.id.device_cpu_text);
        mScreenResText = mKangOSVersionLayoutPref.findViewById(R.id.device_screen_res_text);
        mBatteryText = mKangOSVersionLayoutPref.findViewById(R.id.device_battery_text);
        mRamText = mKangOSVersionLayoutPref.findViewById(R.id.device_ram_text);
        mMaintainerText = mKangOSVersionLayoutPref.findViewById(R.id.device_maintainer_text);

        UpdateKangOSVersionPreference();
    }

    private void UpdateKangOSVersionPreference() {
        // We split the different specs into different voids to make the code more organized.
        updateDeviceNameText();
        updateCpuText();
        updateScreenResText();
        updateBatteryText();
        updateRamText();
        updateMaintainerText();

    }
    private void updateDeviceNameText() {
        mDeviceNameText.setText(KangOSSpecUtils.getDeviceName());
    }

    private void updateBatteryText() {
        mBatteryText.setText(KangOSSpecUtils.getBatteryCapacity(mContext) + " mAh");
    }

    private void updateCpuText() {
        mCpuText.setText(KangOSSpecUtils.getProcessorModel());
    }

    private void updateScreenResText() {
        mScreenResText.setText(KangOSSpecUtils.getScreenRes(mContext));
    }

    private void updateRamText() {
        mRamText.setText(String.valueOf(KangOSSpecUtils.getTotalRAM())+ " GB");
    }
    private void updateMaintainerText() {
        mMaintainerText.setText(String.valueOf(KangOSSpecUtils.getMaintainerName()));
    }

}

