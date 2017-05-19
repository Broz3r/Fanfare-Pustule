package org.pustule.fanfarepustule.ui.widgets;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Class : Lockable View Pager.
 * Information : Classic ViewPager that can be locked.
 *
 * Created by Paul Mougin on 10/12/2015.
 */
public class LockableViewPager extends ViewPager
{
    private boolean isPagingEnabled = true;

    /**
     * Constructor.
     * @param context
     */
    public LockableViewPager(Context context) {
        super(context);
    }

    /**
     * Constructor.
     * @param context
     * @param attrs
     */
    public LockableViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * Override onTouchEvent to lock if needed.
     * @param ev
     * @return
     */
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        return isPagingEnabled && super.onTouchEvent(ev);
    }

    /**
     * Override onInterceptTouchEvent to lock if needed.
     * @param ev
     * @return
     */
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return isPagingEnabled && super.onInterceptTouchEvent(ev);
    }

    /**
     * Enable or lock paging.
     * @param pagingEnabled
     */
    public void setPagingEnabled(boolean pagingEnabled) {
        isPagingEnabled = pagingEnabled;
    }
}
