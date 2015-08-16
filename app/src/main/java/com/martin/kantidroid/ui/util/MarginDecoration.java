package com.martin.kantidroid.ui.util;

import android.content.Context;
import android.graphics.Rect;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;

import com.martin.kantidroid.R;

public class MarginDecoration extends RecyclerView.ItemDecoration {
    private final int margin;

    public MarginDecoration(Context context) {
        margin = context.getResources().getDimensionPixelSize(R.dimen.item_margin);
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        int spacing = margin;
        int halfSpacing = spacing / 2;

        int childIndex = parent.getChildAdapterPosition(view);
        int spanCount = getTotalSpan(parent);
        int spanIndex = childIndex % spanCount;

        /* INVALID SPAN */
        if (spanCount < 1) return;

        outRect.top = spacing;
        outRect.bottom = 0;
        outRect.left = halfSpacing;
        outRect.right = halfSpacing;

        if (isTopEdge(childIndex, spanCount)) {
            outRect.top = 0;
            outRect.bottom = 0;
        }

        if (isLeftEdge(spanIndex)) {
            outRect.left = 0;
        }

        if (isRightEdge(spanIndex, spanCount)) {
            outRect.right = 0;
        }
    }

    protected int getTotalSpan(RecyclerView parent) {
        RecyclerView.LayoutManager mgr = parent.getLayoutManager();
        if (mgr instanceof GridLayoutManager) {
            return ((GridLayoutManager) mgr).getSpanCount();
        } else if (mgr instanceof StaggeredGridLayoutManager) {
            return ((StaggeredGridLayoutManager) mgr).getSpanCount();
        }
        return -1;
    }

    protected boolean isLeftEdge(int spanIndex) {
        return spanIndex == 0;
    }

    protected boolean isRightEdge(int spanIndex, int spanCount) {
        return spanIndex == spanCount - 1;
    }

    protected boolean isTopEdge(int childIndex, int spanCount) {
        return childIndex < spanCount;
    }
}