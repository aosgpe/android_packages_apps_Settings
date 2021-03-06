/*
 * Copyright (C) 2017 The Android Open Source Project
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
package com.android.settings.deviceinfo;

import android.app.Fragment;
import android.content.Context;
import android.os.Build;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceScreen;
import android.text.TextUtils;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.BufferedReader;


import com.android.settings.R;
import com.android.settings.core.PreferenceControllerMixin;
import com.android.settingslib.DeviceInfoUtils;
import com.android.settingslib.core.AbstractPreferenceController;

public class DeviceModelPreferenceController extends AbstractPreferenceController implements
        PreferenceControllerMixin {

    private static final String KEY_DEVICE_MODEL = "device_model";

    private final Fragment mHost;

    public DeviceModelPreferenceController(Context context, Fragment host) {
        super(context);
        mHost = host;
    }

    @Override
    public boolean isAvailable() {
        return mContext.getResources().getBoolean(R.bool.config_show_device_model);
    }

    @Override
    public void displayPreference(PreferenceScreen screen) {
        super.displayPreference(screen);
        final Preference pref = screen.findPreference(KEY_DEVICE_MODEL);
        if (pref != null) {
            pref.setSummary(mContext.getResources().getString(R.string.model_summary,
                    getDeviceModel()));
        }
    }

    @Override
    public String getPreferenceKey() {
        return KEY_DEVICE_MODEL;
    }

    @Override
    public boolean handlePreferenceTreeClick(Preference preference) {
        if (!TextUtils.equals(preference.getKey(), KEY_DEVICE_MODEL)) {
            return false;
        }
        final HardwareInfoDialogFragment fragment = HardwareInfoDialogFragment.newInstance();
        fragment.show(mHost.getFragmentManager(), HardwareInfoDialogFragment.TAG);
        return true;
    }

	public static String getProp(String propName) {
		Process p = null;
		String result = "";
		try {
			p = new ProcessBuilder("/system/bin/getprop", propName).redirectErrorStream(true).start();
			BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
			String line = "";
			while ((line=br.readLine()) != null) {
				result = line;
			}
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}

    public static String getDeviceModel() {
	if (getProp("ro.vendor.product.device") != null){
	  return getProp("ro.vendor.product.device");
	} else {
          return Build.MODEL + DeviceInfoUtils.getMsvSuffix();
	}
    }
}
