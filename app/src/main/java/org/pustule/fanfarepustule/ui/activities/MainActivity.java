package org.pustule.fanfarepustule.ui.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;

import org.pustule.fanfarepustule.R;
import org.pustule.fanfarepustule.ui.bases.BaseActivity;
import org.pustule.fanfarepustule.utils.DeviceUtils;

public class MainActivity extends BaseActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected int getLayoutRes() {
        return R.layout.activity_main;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!DeviceUtils.isGooglePlayServicesAvailable(this)) {
            DeviceUtils.acquireGooglePlayServices(this);
        }
    }
}
