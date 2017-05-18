package org.pustule.fanfarepustule.activities;

import android.content.Intent;

import org.pustule.fanfarepustule.R;
import org.pustule.fanfarepustule.base.BaseActivity;

import butterknife.OnClick;

public class HomeActivity extends BaseActivity {

    @Override
    protected int getLayoutRes() {
        return R.layout.activity_home;
    }

    @OnClick(R.id.sheets_button)
    public void onSheetsButtonCLicked() {
        final Intent intent = new Intent(this, GoogleCalendarExempleActivity.class);
        startActivity(intent);
    }
}
