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
import android.content.res.TypedArray;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.annotation.LayoutRes;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

public abstract class ChipViewAdapter extends Observable {

    private Context context;
    private AttributeSet attributeSet;
    private LayoutInflater layoutInflater;
    private List<Chip> content;

    private int chipSpacing;
    private int lineSpacing;
    private int chipPadding;
    private int chipCornerRadius;
    private int chipSidePadding;
    private int chipTextSize;
    private int chipRes;
    private int chipBackgroundColor;
    private int chipBackgroundColorSelected;
    private boolean toleratingDuplicate = false;

    public abstract int getLayoutRes(int position);

    public abstract void bindView(View view, int position);

    public abstract void newView(View view);

    public ChipViewAdapter(Context context) {
        this(context, null);
    }

    public ChipViewAdapter(Context context, AttributeSet attributeSet) {
        this.context = context;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        content = new ArrayList<>();

        setAttributeSet(attributeSet);
    }

    private void init() {
        chipSpacing = context.getResources().getDimensionPixelSize(R.dimen.chip_spacing);
        lineSpacing = context.getResources().getDimensionPixelSize(R.dimen.chip_line_spacing);
        chipPadding = context.getResources().getDimensionPixelSize(R.dimen.chip_padding);
        chipSidePadding = context.getResources().getDimensionPixelSize(R.dimen.chip_side_padding);
        chipCornerRadius = context.getResources().getDimensionPixelSize(R.dimen.chip_corner_radius);
        chipBackgroundColor = getColor(R.color.chip_background);
        chipBackgroundColorSelected = getColor(R.color.chip_background_selected);

        if (attributeSet != null) {
            TypedArray typedArray = context.getTheme()
                    .obtainStyledAttributes(attributeSet, R.styleable.ChipView, 0, 0);

            try {

                chipSpacing = (int) typedArray
                        .getDimension(R.styleable.ChipView_chip_spacing, chipSpacing);
                lineSpacing = (int) typedArray
                        .getDimension(R.styleable.ChipView_chip_line_spacing, lineSpacing);
                chipPadding = (int) typedArray
                        .getDimension(R.styleable.ChipView_chip_padding, chipPadding);
                chipSidePadding = (int) typedArray
                        .getDimension(R.styleable.ChipView_chip_side_padding, chipSidePadding);
                chipCornerRadius = (int) typedArray
                        .getDimension(R.styleable.ChipView_chip_corner_radius, chipCornerRadius);
                chipBackgroundColor = typedArray
                        .getColor(R.styleable.ChipView_chip_background, chipBackgroundColor);
                chipBackgroundColorSelected = typedArray
                        .getColor(
                                R.styleable.ChipView_chip_background_selected,
                                chipBackgroundColorSelected);
            } finally {

                typedArray.recycle();
            }
        }
    }

    public View getView(ViewGroup parent, int position) {
        View view = null;
        Chip chip = getChip(position);

        if (chip != null) {
            int chipLayoutRes = (getLayoutRes(position) != 0
                    ? getLayoutRes(position) : getChipLayoutRes());

            if (chipLayoutRes == 0) {
                LinearLayout.LayoutParams layoutParams =
                        new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.WRAP_CONTENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT
                        );
                layoutParams.setMargins(0, 0, chipSpacing, lineSpacing);

                view = new LinearLayout(context);
                view.setLayoutParams(layoutParams);

                ((LinearLayout) view).setOrientation(LinearLayout.HORIZONTAL);
                ((LinearLayout) view).setGravity(Gravity.CENTER_VERTICAL);

                view.setPadding(chipSidePadding, chipPadding, chipSidePadding, chipPadding);

                TextView text = new TextView(context);
                text.setId(android.R.id.text1);

                ((LinearLayout) view).addView(text);
            } else {

                view = layoutInflater.inflate(chipLayoutRes, parent, false);
                ViewGroup.MarginLayoutParams layoutParams =
                        (ViewGroup.MarginLayoutParams) view.getLayoutParams();
                layoutParams.setMargins(layoutParams.leftMargin,
                        layoutParams.topMargin,
                        (layoutParams.rightMargin > 0 ? layoutParams.rightMargin : chipSpacing),
                        (layoutParams.bottomMargin > 0 ? layoutParams.bottomMargin : lineSpacing));
            }

            newView(view);
            bindView(view, position);
        }

        return view;
    }

    protected void notifyUpdate() {
        setChanged();
        notifyObservers();
    }

    public Chip getChip(int position) {
        return (position < count() ? content.get(position) : null);
    }

    public void add(Chip chip) {
        if (!content.contains(chip) || toleratingDuplicate) {
            content.add(chip);
            notifyUpdate();
        }
    }

    public void remove(Chip chip) {
        content.remove(chip);
        notifyUpdate();
    }

    public int count() {
        return content.size();
    }

    protected int getColor(@ColorRes int colorRes) {
        return context.getResources().getColor(colorRes);
    }

    public Context getContext() {
        return context;
    }

    public AttributeSet getAttributeSet() {
        return attributeSet;
    }

    public void setAttributeSet(AttributeSet attributeSet) {
        this.attributeSet = attributeSet;
        init();
    }

    public List<Chip> getChipList() {
        return content;
    }

    public void setChipList(List<Chip> chipList) {
        content = chipList;
        notifyUpdate();
    }

    public boolean isToleratingDuplicate() {
        return toleratingDuplicate;
    }

    public void setToleratingDuplicate(boolean toleratingDuplicate) {
        this.toleratingDuplicate = toleratingDuplicate;
    }

    public int getChipSpacing() {
        return chipSpacing;
    }

    public void setChipSpacing(int chipSpacing) {
        this.chipSpacing = chipSpacing;
    }

    public int getLineSpacing() {
        return lineSpacing;
    }

    public void setLineSpacing(int lineSpacing) {
        this.lineSpacing = lineSpacing;
    }

    public int getChipPadding() {
        return chipPadding;
    }

    public void setChipPadding(int chipPadding) {
        this.chipPadding = chipPadding;
    }

    public int getChipSidePadding() {
        return chipSidePadding;
    }

    public void setChipSidePadding(int chipSidePadding) {
        this.chipSidePadding = chipSidePadding;
    }

    public int getChipCornerRadius() {
        return chipCornerRadius;
    }

    public void setChipCornerRadius(int chipCornerRadius) {
        this.chipCornerRadius = chipCornerRadius;
    }

    public int getChipBackgroundColor() {
        return chipBackgroundColor;
    }

    public void setChipBackgroundColor(@ColorInt int chipBackgroundColor) {
        this.chipBackgroundColor = chipBackgroundColor;
    }

    public int getChipBackgroundColorSelected() {
        return chipBackgroundColorSelected;
    }

    public void setChipBackgroundColorSelected(@ColorInt int chipBackgroundColorSelected) {
        this.chipBackgroundColorSelected = chipBackgroundColorSelected;
    }

    public int getChipTextSize() {
        return chipTextSize;
    }

    public void setChipTextSize(int chipTextSize) {
        this.chipTextSize = chipTextSize;
    }

    public int getChipLayoutRes() {
        return chipRes;
    }

    public void setChipLayoutRes(@LayoutRes int chipRes) {
        this.chipRes = chipRes;
    }
}