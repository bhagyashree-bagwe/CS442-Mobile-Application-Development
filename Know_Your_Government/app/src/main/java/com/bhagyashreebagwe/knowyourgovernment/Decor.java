package com.bhagyashreebagwe.knowyourgovernment;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by bhagyashree on 4/3/18.
 */

public class Decor extends RecyclerView.ItemDecoration {

    Drawable drawable;

    public Decor(Drawable d){
    this.drawable=d;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);

        if (parent.getChildAdapterPosition(view) == 0) {
            return;
        }

        outRect.top = drawable.getIntrinsicHeight();
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        //super.onDraw(c, parent, state);
        int leftDivider = parent.getPaddingLeft();
        int rightDivider = parent.getWidth() - parent.getPaddingRight();

        int childCount = parent.getChildCount();
        for (int i = 0; i < childCount - 1; i++) {
            View child = parent.getChildAt(i);

            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();

            int topDivider = child.getBottom() + params.bottomMargin;
            int bottomDivider = topDivider + drawable.getIntrinsicHeight();

            drawable.setBounds(leftDivider, topDivider, rightDivider, bottomDivider);
            drawable.draw(c);
        }
    }


    }

