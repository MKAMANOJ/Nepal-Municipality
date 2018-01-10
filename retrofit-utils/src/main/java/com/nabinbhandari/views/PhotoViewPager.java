package com.nabinbhandari.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * https://github.com/chrisbanes/PhotoView/issues/31
 * Created by azi on 2013-6-21.
 * <p>
 * Custom your own ViewPager to extends support ViewPager. java source:
 */
public class PhotoViewPager extends android.support.v4.view.ViewPager {

    public PhotoViewPager(Context context) {
        super(context);
    }

    public PhotoViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        try {
            return super.onTouchEvent(ev);
        } catch (IllegalArgumentException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        try {
            return super.onInterceptTouchEvent(ev);
        } catch (IllegalArgumentException ex) {
            ex.printStackTrace();
        }
        return false;
    }

}