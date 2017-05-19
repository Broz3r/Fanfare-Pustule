package org.pustule.fanfarepustule.ui.adapters.pagers;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.MenuItem;
import android.view.View;

import org.pustule.fanfarepustule.R;
import org.pustule.fanfarepustule.ui.bases.BaseFragment;
import org.pustule.fanfarepustule.ui.fragments.HomeFragment;
import org.pustule.fanfarepustule.ui.fragments.OtherFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Paul Mougin on 06/03/2017.
 */

public class MainAdapter extends FragmentPagerAdapter {

    private enum MainPages {
        HOME(0),
        EVENTS(1),
        SHEETS(2),
        MUSICS(3);

        private int position;

        MainPages(int position) {
            this.position = position;
        }

        static MainPages getByActionId(int actionId) {
            switch (actionId) {
                case R.id.action_home:
                    return HOME;
                case R.id.action_events:
                    return EVENTS;
                case R.id.action_sheets:
                    return SHEETS;
                case R.id.action_musics:
                    return MUSICS;
            }
            return HOME;
        }
    }

    private List<BaseFragment> pages = new ArrayList<>();
    private ViewPager pager;

    private HomeFragment homeFragment = new HomeFragment();

    public MainAdapter(FragmentManager fm, ViewPager pager) {
        super(fm);
        initFragments();

        this.pager = pager;
        pager.setOffscreenPageLimit(3);
        pager.setAdapter(this);

        pager.setPageTransformer(true, new ViewPager.PageTransformer() {
            @Override
            public void transformPage(View page, float position) {
                page.setTranslationX(page.getWidth() * -position);

                // Ensures the views overlap each other.
                page.setTranslationX(page.getWidth() * -position);

                // Alpha property is based on the view position.
                if(position <= -1.0F || position >= 1.0F) {
                    page.setAlpha(0.0F);
                } else if( position == 0.0F ) {
                    page.setAlpha(1.0F);
                } else { // position is between -1.0F & 0.0F OR 0.0F & 1.0F
                    page.setAlpha(1.0F - Math.abs(position));
                }
            }
        });
    }

    private void initFragments() {
        pages.add(homeFragment);
        pages.add(new OtherFragment());
        pages.add(new OtherFragment());
        pages.add(new OtherFragment());
    }

    @Override
    public Fragment getItem(int position) {
        return pages.get(position);
    }

    @Override
    public int getCount() {
        return pages.size();
    }

    public void handleActivityResponse(int resultCode, Intent data) {
        pages.get(pager.getCurrentItem()).handleActivityResponse(resultCode, data);
    }

    public void changePage(MenuItem item) {
        pager.setCurrentItem(MainPages.getByActionId(item.getItemId()).position);
    }
}
