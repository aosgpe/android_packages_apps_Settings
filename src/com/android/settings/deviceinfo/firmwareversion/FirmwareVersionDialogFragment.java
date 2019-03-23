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

package com.android.settings.deviceinfo.firmwareversion;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.util.Log;
import android.widget.Toast;
import android.widget.TextView;
import android.os.SystemClock;
import android.os.SystemProperties;


import com.android.internal.logging.nano.MetricsProto;
import com.android.settings.R;
import com.android.settings.core.instrumentation.InstrumentedDialogFragment;

public class FirmwareVersionDialogFragment extends InstrumentedDialogFragment {

    private static final String TAG = "firmwareVersionDialog";
    private long[] mHits = new long[3];

    private View mRootView;

    public static void show(Fragment host) {
        final FragmentManager manager = host.getChildFragmentManager();
        if (manager.findFragmentByTag(TAG) == null) {
            final FirmwareVersionDialogFragment dialog = new FirmwareVersionDialogFragment();
            dialog.show(manager, TAG);
        }
    }

    @Override
    public int getMetricsCategory() {
        return MetricsProto.MetricsEvent.DIALOG_FIRMWARE_VERSION;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                .setTitle(R.string.firmware_title)
                .setPositiveButton(android.R.string.ok, null /* listener */);

        mRootView = LayoutInflater.from(getActivity()).inflate(
                R.layout.dialog_firmware_version, null /* parent */);

	TextView aosgp_version = mRootView.findViewById(R.id.info_aosgp_version);
        aosgp_version.setText(SystemProperties.get("ro.aosgp.version"));
        aosgp_version.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            System.arraycopy(mHits, 1, mHits, 0, mHits.length - 1);
            mHits[mHits.length - 1] = SystemClock.uptimeMillis();
            if (mHits[0] >= (SystemClock.uptimeMillis() - 500)) {
            /*final Intent intent = new Intent(Intent.ACTION_MAIN)
                    .setClassName(
                            "android", com.android.internal.app.PlatLogoActivity.class.getName());
			*/
                try {
                    //getContext().startActivity(intent);
		    Toast.makeText(getActivity(), "Congratulations! You find AOSGP E secret message, don't share with anyone 			    please", Toast.LENGTH_LONG).show();
                } catch (Exception e) {
                    //Log.e(TAG, "Unable to start activity " + intent.toString());
		    Log.e(TAG, "Unable to start activity ");
                }
            }
            }
        });


        initializeControllers();

        return builder.setView(mRootView).create();
    }

    public void setText(int viewId, CharSequence text) {
        final TextView view = mRootView.findViewById(viewId);
        if (view != null) {
            view.setText(text);
        }
    }

    public void removeSettingFromScreen(int viewId) {
        final View view = mRootView.findViewById(viewId);
        if (view != null) {
            view.setVisibility(View.GONE);
        }
    }

    public void registerClickListener(int viewId, View.OnClickListener listener) {
        final View view = mRootView.findViewById(viewId);
        if (view != null) {
            view.setOnClickListener(listener);
        }
    }

    private void initializeControllers() {
        new FirmwareVersionDialogController(this).initialize();
        new SecurityPatchLevelDialogController(this).initialize();
        new BasebandVersionDialogController(this).initialize();
        new KernelVersionDialogController(this).initialize();
        new BuildNumberDialogController(this).initialize();
    }
}
