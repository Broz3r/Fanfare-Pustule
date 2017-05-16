package org.pustule.fanfarepustule.activities;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;

import org.pustule.fanfarepustule.R;
import org.pustule.fanfarepustule.base.BaseActivity;
import org.pustule.fanfarepustule.utils.NavigationUtils;

/**
 * Created by Paul Mougin on 16/05/2017.
 */

public class LoadingActivity extends BaseActivity {

    private static final int DELAY = 2000;

    @Override
    protected int getLayoutRes() {
        return R.layout.activity_loading;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                NavigationUtils.launchHomeActivity(LoadingActivity.this);
                finish();
            }
        }, DELAY);
    }
}
