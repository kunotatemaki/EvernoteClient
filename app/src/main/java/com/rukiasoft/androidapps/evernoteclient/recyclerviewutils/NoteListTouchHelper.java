package com.rukiasoft.androidapps.evernoteclient.recyclerviewutils;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.View;

import com.rukiasoft.androidapps.evernoteclient.R;
import com.rukiasoft.androidapps.evernoteclient.classes.NoteView;
import com.rukiasoft.androidapps.evernoteclient.ui.NoteListAdapter;


/**
 * Created by iRuler on 25/1/16.
 */
public class NoteListTouchHelper extends ItemTouchHelper.SimpleCallback {


    // we want to cache these and not allocate anything repeatedly in the onChildDraw method
    Drawable background;
    Drawable xMark;
    Drawable shownDrawable;
    Drawable deleteDrawable;
    Drawable editDrawable;
    int xMarkMargin;
    boolean initiated;
    Context mContext;
    NoteListAdapter mAdapter;

    public NoteListTouchHelper(int dragDirs, int swipeDirs) {
        super(dragDirs, swipeDirs);
    }


    public void init(Context context, NoteListAdapter adapter) {
        mContext = context;
        mAdapter = adapter;
        background = new ColorDrawable(ContextCompat.getColor(mContext, R.color.colorAccent));
        xMark = ContextCompat.getDrawable(mContext, R.drawable.ic_clear_24dp);
        xMark.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
        deleteDrawable = ContextCompat.getDrawable(mContext, R.drawable.ic_delete_white_24dp);
        editDrawable = ContextCompat.getDrawable(mContext, R.drawable.ic_delete_white_24dp);
        xMarkMargin = (int) mContext.getResources().getDimension(R.dimen.ic_clear_margin);
        initiated = true;
    }

    // not important, we don't want drag & drop
    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public int getSwipeDirs(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        int position = viewHolder.getAdapterPosition();
        if (mAdapter.isPendingSelectOption(position)) {
            return 0;
        }
        return super.getSwipeDirs(recyclerView, viewHolder);
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
        int swipedPosition = viewHolder.getAdapterPosition();
        mAdapter.showingButton(swipedPosition, swipeDir);

    }

    @Override
    public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        View itemView = viewHolder.itemView;

        // not sure why, but this method get's called for viewholder that are already swiped away
        if (viewHolder.getAdapterPosition() == -1) {
            // not interested in those
            return;
        }



        // draw x mark
        int itemHeight = itemView.getBottom() - itemView.getTop();

        // draw background
        Log.d("OK", " d1: " + dX);
        int xMarkLeft;
        int xMarkRight;
        int intrinsicWidth;
        int intrinsicHeight;
        if(dX<0) {
            background = new ColorDrawable(ContextCompat.getColor(mContext, R.color.color_deleting));
            shownDrawable = deleteDrawable;
            intrinsicWidth = shownDrawable.getIntrinsicWidth();
            intrinsicHeight = shownDrawable.getIntrinsicWidth();
            background.setBounds(itemView.getRight() + (int) dX, itemView.getTop(), itemView.getRight(), itemView.getBottom());
            xMarkLeft = itemView.getRight() - xMarkMargin - intrinsicWidth;
            xMarkRight = itemView.getRight() - xMarkMargin;
        } else{
            background = new ColorDrawable(ContextCompat.getColor(mContext, R.color.color_editing));
            shownDrawable = editDrawable;
            intrinsicWidth = shownDrawable.getIntrinsicWidth();
            intrinsicHeight = shownDrawable.getIntrinsicWidth();
            background.setBounds(itemView.getLeft(), itemView.getTop(), itemView.getLeft() + (int) dX, itemView.getBottom());
            xMarkLeft = itemView.getLeft() + xMarkMargin;
            xMarkRight = itemView.getLeft() + xMarkMargin + intrinsicWidth;
        }
        background.draw(c);



        int xMarkTop = itemView.getTop() + (itemHeight - intrinsicHeight)/2;
        int xMarkBottom = xMarkTop + intrinsicHeight;
        shownDrawable.setBounds(xMarkLeft, xMarkTop, xMarkRight, xMarkBottom);

        shownDrawable.draw(c);

        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
    }
}
