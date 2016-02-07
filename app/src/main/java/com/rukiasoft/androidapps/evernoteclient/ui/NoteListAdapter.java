package com.rukiasoft.androidapps.evernoteclient.ui;

/**
 * Created by Raúl Feliz on 7/2/16.
 *
 */

import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.rukiasoft.androidapps.evernoteclient.R;
import com.rukiasoft.androidapps.evernoteclient.classes.NoteView;
import com.rukiasoft.androidapps.evernoteclient.utilities.Tools;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * RecyclerView adapter enabling undo on a swiped away item.
 */
public class NoteListAdapter extends RecyclerView.Adapter<NoteListAdapter.NoteViewHolder>
{


    private List<NoteView> adapterNotes;
    //List<NoteView> notesShowingButton;
    Context mContext;

    private OnActionListener onActionListener;

    public NoteListAdapter(Context context, List<NoteView> notes) {
        adapterNotes = notes;
        //notesShowingButton = new ArrayList<>();
        mContext = context;
    }

    @Override
    public NoteViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.note_list_item, parent, false);
        return new NoteViewHolder(view);
    }

    public void setOnActionListener(OnActionListener onActionListener) {
        this.onActionListener = onActionListener;
    }

    @Override
    public void onBindViewHolder(final NoteViewHolder viewHolder, int position) {
        final NoteView item = adapterNotes.get(position);
        viewHolder.itemView.setTag(item);

        switch (item.getStatus()) {
            case NoteView.STATUS_NORMAL:
                viewHolder.row.setBackgroundColor(Color.TRANSPARENT);
                viewHolder.titleNote.setVisibility(View.VISIBLE);
                viewHolder.titleNote.setText(item.getNote().getTitle());
                viewHolder.dateNote.setVisibility(View.VISIBLE);
                Tools mTools = new Tools();
                String date = mTools.getDateFromLong(mContext, item.getNote().getCreated());
                viewHolder.dateNote.setText(date);
                viewHolder.actionButton.setVisibility(View.GONE);
                viewHolder.actionButton.setOnClickListener(null);
                viewHolder.cancelButton.setVisibility(View.GONE);
                viewHolder.cancelButton.setOnClickListener(null);

                break;
            case NoteView.STATUS_EDITING:
                viewHolder.row.setBackgroundColor(ContextCompat.getColor(mContext, R.color.color_editing));
                showActionButtons(viewHolder, mContext.getResources().getString(R.string.edit));
                break;
            case NoteView.STATUS_DELETING:
                viewHolder.row.setBackgroundColor(ContextCompat.getColor(mContext, R.color.color_deleting));
                showActionButtons(viewHolder, mContext.getResources().getString(R.string.delete));
                break;
        }

    }

    private void showActionButtons(final NoteViewHolder viewHolder, String name){
        final NoteView item = (NoteView) viewHolder.itemView.getTag();
        viewHolder.actionButton.setText(name);
        viewHolder.actionButton.setVisibility(View.VISIBLE);
        viewHolder.actionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onActionListener != null) {
                    onActionListener.onActionClick((NoteView) viewHolder.itemView.getTag());
                }
                item.setStatus(NoteView.STATUS_NORMAL);
                notifyItemChanged(adapterNotes.indexOf(item));
            }
        });

        viewHolder.cancelButton.setVisibility(View.VISIBLE);
        viewHolder.cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                item.setStatus(NoteView.STATUS_NORMAL);
                notifyItemChanged(adapterNotes.indexOf(item));
            }
        });
        viewHolder.titleNote.setVisibility(View.INVISIBLE);
        viewHolder.dateNote.setVisibility(View.INVISIBLE);
    }

    @Override
    public int getItemCount() {
        return adapterNotes.size();
    }


    public void showingButton(int position, int swipeDir) {
        final NoteView item = adapterNotes.get(position);
        int status = 0;
        if(swipeDir == ItemTouchHelper.LEFT){
            status = NoteView.STATUS_DELETING;
        }else if(swipeDir == ItemTouchHelper.RIGHT){
            status = NoteView.STATUS_EDITING;
        }
        if (item.getStatus().equals(NoteView.STATUS_NORMAL)) {
            item.setStatus(status);
            notifyItemChanged(position);

        }
    }


    public boolean isPendingSelectOption(int position) {
        NoteView item = adapterNotes.get(position);
        return !item.getStatus().equals(NoteView.STATUS_NORMAL);
    }


    public void refresh(NoteView noteView) {
        if(adapterNotes != null && !adapterNotes.isEmpty()) {
            notifyItemChanged(adapterNotes.indexOf(noteView));
        }
    }


    /**
     * ViewHolder capable of presenting two states: "normal" and "undo" state.
     */
    protected static class NoteViewHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.note_list_title_item)
        TextView titleNote;
        @Bind(R.id.action_under_swipe_button)Button actionButton;
        @Bind(R.id.cancel_under_swipe_button)Button cancelButton;
        @Bind(R.id.swipe_to_show_layout)
        RelativeLayout row;
        @Bind(R.id.note_list_date_item) TextView dateNote;

        public NoteViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public interface OnActionListener {
        void onActionClick(NoteView noteView);
    }


}



