package com.chipview.android;

import android.content.Context;
import android.util.AttributeSet;

import java.util.Collections;
import java.util.List;

public abstract class ChipListAdapter<T> extends ChipViewAdapter {

    private List<T> content = Collections.emptyList();

    public ChipListAdapter(Context context) {
        super(context);
    }

    public ChipListAdapter(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public List<T> getContent() {
        return content;
    }

    public void setContent(List<T> content) {
        this.content = content;
        notifyUpdate();
    }

    public void clear() {
        getChipList().clear();
        notifyUpdate();
    }
}