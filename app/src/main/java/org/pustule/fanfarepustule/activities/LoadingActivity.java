package org.pustule.fanfarepustule.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.widget.ImageView;

import org.pustule.fanfarepustule.R;
import org.pustule.fanfarepustule.base.BaseActivity;

import butterknife.BindView;

/**
 * Created by Paul Mougin on 16/05/2017.
 */

public class LoadingActivity extends BaseActivity {

    private static final int DELAY = 2000;

    @BindView(R.id.image) ImageView imageView;

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
                launchHomeActivity();
                finish();
            }
        }, DELAY);
    }

    private void launchHomeActivity() {
        final Intent intent = new Intent(this, MainActivity.class);

        final String transitionName = getString(R.string.transition_string_loading_home);
        ActivityOptionsCompat options =

                ActivityOptionsCompat.makeSceneTransitionAnimation(this,
                        imageView,   // Starting view
                        transitionName    // The String
                );

        ActivityCompat.startActivity(this, intent, options.toBundle());
    }
}
