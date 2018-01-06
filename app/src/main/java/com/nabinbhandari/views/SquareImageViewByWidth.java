package com.nabinbhandari.views;

import android.content.Context;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;

public class SquareImageViewByWidth extends AppCompatImageView {

    public SquareImageViewByWidth(Context context) {
        super(context);
    }

    public SquareImageViewByWidth(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SquareImageViewByWidth(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(getMeasuredWidth(), getMeasuredWidth());
    }

}
