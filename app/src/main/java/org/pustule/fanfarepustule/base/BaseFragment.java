package org.pustule.fanfarepustule.base;

import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by Paul Mougin on 27/02/2017.
 */

public abstract class BaseFragment extends Fragment {

    protected boolean isViewInflated = false;
    private Unbinder unbinder;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View root = inflater.inflate(getLayoutRes(), container, false);

        unbinder = ButterKnife.bind(this, root);
        bindView(root);

        return root;
    }

    @Override public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @LayoutRes
    protected abstract int getLayoutRes();

    protected void bindView(View root) {
        isViewInflated = true;
    }
}
