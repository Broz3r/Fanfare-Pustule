package org.pustule.fanfarepustule.utils;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;

import org.pustule.fanfarepustule.activities.HomeActivity;

/**
 * Created by Paul Mougin on 16/05/2017.
 */

public final class NavigationUtils {

    /**
     * Start {@link HomeActivity}
     * @param activity the activity from where {@link HomeActivity} will be launch
     */
    public static void launchHomeActivity(@NonNull Activity activity) {
        final Intent intent = new Intent(activity, HomeActivity.class);
        activity.startActivity(intent);
    }
}
