package org.pustule.fanfarepustule.ui.activities;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.view.MenuItem;

import org.pustule.fanfarepustule.R;
import org.pustule.fanfarepustule.ui.adapters.pagers.MainAdapter;
import org.pustule.fanfarepustule.ui.bases.BaseActivity;
import org.pustule.fanfarepustule.ui.widgets.LockableViewPager;
import org.pustule.fanfarepustule.utils.DeviceUtils;

import butterknife.BindView;

public class MainActivity extends BaseActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = MainActivity.class.getSimpleName();

    @BindView(R.id.pager) protected LockableViewPager pager;
    @BindView(R.id.navigation) protected BottomNavigationView bottomView;

    private MainAdapter adapter;

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

        pager.setPagingEnabled(false);
        adapter = new MainAdapter(getSupportFragmentManager(), pager);
        bottomView.setOnNavigationItemSelectedListener(this);
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        adapter.changePage(item);
        return true;
    }
}
