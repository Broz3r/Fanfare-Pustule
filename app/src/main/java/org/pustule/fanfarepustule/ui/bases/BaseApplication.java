package org.pustule.fanfarepustule.ui.bases;

import android.app.Application;

import org.pustule.fanfarepustule.R;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

/**
 * Created by Paul Mougin on 11/05/2017.
 */

public class BaseApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setFontAttrId(R.attr.fontPath)
                .build()
        );
    }
}
