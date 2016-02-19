/*
 * Copyright 2012 GreenMile LLC. All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * GreenMile LLC ("Confidential Information"). You shall not disclose such
 * Confidential Information and shall use it only in accordance with the
 * terms of the license agreement you entered into with GreenMile LLC.
 */
package com.chipview.android;

import android.content.Context;
import android.support.annotation.ColorInt;
import android.support.annotation.LayoutRes;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

public class ChipView extends ViewGroup implements Observer {

    private ChipViewAdapter adapter;
    private OnChipClickListener listener;

    private List<Integer> lineHeightList;

    public ChipView(Context context) {
        super(context);
        init(context, null);
    }

    public ChipView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public ChipView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        lineHeightList = new ArrayList<>();

        setAdapter(new ChipViewAdapter(context, attrs) {

            @Override
            public int getLayoutRes(int position) {
                return 0;
            }

            @Override
            public void newView(View view) {
            }

            @Override
            public void bindView(View view, int position) {
            }
        });
    }

    @Override
    protected void onMeasure(final int widthMeasureSpec, final int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        measureChildren(widthMeasureSpec, heightMeasureSpec);

        lineHeightList.clear();
        int width = getMeasuredWidth();
        int height = getPaddingTop() + getPaddingBottom();
        int lineHeight = 0;
        int lineWidth = getPaddingLeft();
        int childCount = getChildCount();

        for (int i = 0; i < childCount; i++) {
            View childView = getChildAt(i);
            MarginLayoutParams layoutParams = (MarginLayoutParams) childView.getLayoutParams();
            boolean lastChild = (i == childCount - 1);

            if (childView.getVisibility() == GONE) {
                if (lastChild)
                    lineHeightList.add(lineHeight);

                continue;
            }

            int childWidth = (childView.getMeasuredWidth() +
                    layoutParams.leftMargin +
                    layoutParams.rightMargin);
            int childHeight = (childView.getMeasuredHeight() +
                    layoutParams.topMargin +
                    layoutParams.bottomMargin);
            lineHeight = Math.max(lineHeight, childHeight);

            if (childWidth > width)
                width = childWidth;

            if (lineWidth + childWidth + getPaddingRight() > width) {
                lineHeightList.add(lineHeight);
                lineWidth = getPaddingLeft() + childWidth;
            } else
                lineWidth += childWidth;

            if (lastChild)
                lineHeightList.add(lineHeight);
        }

        for (Integer h : lineHeightList)
            height += h;

        setMeasuredDimension(width, height);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (adapter != null) {

            int width = getMeasuredWidth();
            int lineWidth = getPaddingLeft();
            int childCount = getChildCount();
            int j = 0;
            int lineHeight = (lineHeightList.size() > 0 ? lineHeightList.get(j) : 0);
            int childY = getPaddingTop();

            for (int i = 0; i < childCount; i++) {
                final Chip chip = adapter.getChipList().get(i);
                View childView = getChildAt(i);
                ViewGroup.MarginLayoutParams layoutParams =
                        (ViewGroup.MarginLayoutParams) childView.getLayoutParams();

                if (childView.getVisibility() == View.GONE)
                    continue;

                int childWidth = (childView.getMeasuredWidth() +
                        layoutParams.leftMargin +
                        layoutParams.rightMargin);
                int childHeight = (childView.getMeasuredHeight() +
                        layoutParams.topMargin +
                        layoutParams.bottomMargin);

                if (childWidth > width)
                    width = childWidth;

                if (lineWidth + childWidth + getPaddingRight() > width) {
                    childY += lineHeight;
                    j++;
                    lineHeight = lineHeightList.get(j);
                    lineWidth = getPaddingLeft() + childWidth;
                } else
                    lineWidth += childWidth;

                int childX = lineWidth - childWidth;

                childView.layout((childX + layoutParams.leftMargin),
                        (childY + layoutParams.topMargin),
                        (lineWidth - layoutParams.rightMargin),
                        (childY + childHeight - layoutParams.bottomMargin)
                );

                if (listener != null) {
                    childView.setOnClickListener(new OnClickListener() {

                        @Override
                        public void onClick(View v) {

                            listener.onChipClick(chip);
                        }
                    });
                }
            }
        }
    }

    @Override
    protected LayoutParams generateDefaultLayoutParams() {
        return new MarginLayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
    }

    @Override
    protected LayoutParams generateLayoutParams(LayoutParams p) {
        return new MarginLayoutParams(p);
    }

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new MarginLayoutParams(getContext(), attrs);
    }

    public void refresh() {
        if (adapter != null) {
            removeAllViews();

            for (int i = 0; i < adapter.count(); i++) {
                View view = adapter.getView(this, i);

                if (view != null) {

                    if (listener != null) {

                        view.setClickable(true);
                        view.setFocusable(true);
                    }

                    addView(view);
                }
            }

            invalidate();
        }
    }

    public void add(Chip chip) {
        adapter.add(chip);
    }

    public void remove(Chip chip) {
        adapter.remove(chip);
    }

    public int count() {
        return adapter.count();
    }

    public List<Chip> getChipList() {
        return adapter.getChipList();
    }

    public void setChipList(List<Chip> chipList) {
        adapter.setChipList(chipList);
    }

    public ChipViewAdapter getAdapter() {
        return adapter;
    }

    public void setAdapter(ChipViewAdapter adapter) {
        this.adapter = adapter;
        this.adapter.deleteObservers();
        this.adapter.addObserver(this);
        refresh();
    }

    public int getChipLayoutRes() {
        return adapter.getChipLayoutRes();
    }

    public void setChipLayoutRes(@LayoutRes int chipRes) {
        adapter.setChipLayoutRes(chipRes);
    }

    public void setOnChipClickListener(OnChipClickListener listener) {
        this.listener = listener;
    }

    public boolean isToleratingDuplicate() {
        return adapter.isToleratingDuplicate();
    }

    public void setToleratingDuplicate(boolean toleratingDuplicate) {
        adapter.setToleratingDuplicate(toleratingDuplicate);
    }

    public int getChipSpacing() {
        return adapter.getChipSpacing();
    }

    public void setChipSpacing(int chipSpacing) {
        adapter.setChipSpacing(chipSpacing);
    }

    public int getLineSpacing() {
        return adapter.getLineSpacing();
    }

    public void setLineSpacing(int lineSpacing) {
        adapter.setLineSpacing(lineSpacing);
    }

    public int getChipPadding() {
        return adapter.getChipPadding();
    }

    public void setChipPadding(int chipPadding) {
        adapter.setChipPadding(chipPadding);
    }

    public int getChipSidePadding() {
        return adapter.getChipSidePadding();
    }

    public void setChipSidePadding(int chipSidePadding) {
        adapter.setChipSidePadding(chipSidePadding);
    }

    public int getChipCornerRadius() {
        return adapter.getChipCornerRadius();
    }

    public void setChipCornerRadius(int chipCornerRadius) {
        adapter.setChipCornerRadius(chipCornerRadius);
    }

    public int getChipBackgroundColor() {
        return adapter.getChipBackgroundColor();
    }

    public void setChipBackgroundColor(@ColorInt int chipBackgroundColor) {
        adapter.setChipBackgroundColor(chipBackgroundColor);
    }

    public int getChipBackgroundColorSelected() {
        return adapter.getChipBackgroundColorSelected();
    }

    public void setChipBackgroundColorSelected(@ColorInt int chipBackgroundColorSelected) {
        adapter.setChipBackgroundColorSelected(chipBackgroundColorSelected);
    }

    public int getChipTextSize() {
        return adapter.getChipTextSize();
    }

    public void setChipTextSize(int chipTextSize) {
        adapter.setChipTextSize(chipTextSize);
    }

    @Override
    public void update(Observable observable, Object data) {
        refresh();
    }
}