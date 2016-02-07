package com.rukiasoft.androidapps.evernoteclient.ui;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.rukiasoft.androidapps.evernoteclient.R;
import com.rukiasoft.androidapps.evernoteclient.classes.NoteView;
import com.rukiasoft.androidapps.evernoteclient.recyclerviewutils.Decorator;
import com.rukiasoft.androidapps.evernoteclient.recyclerviewutils.DividerItemDecoration;
import com.rukiasoft.androidapps.evernoteclient.recyclerviewutils.NoteListTouchHelper;
import com.rukiasoft.androidapps.evernoteclient.utilities.Constants;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;


public class NoteListFragment extends Fragment {

    private static final String KEY_NEED_TO_SET_DATA = Constants.PACKAGE_NAME + "key_need_to_set_data";
    private static final String KEY_DONT_GET_NOTES = Constants.PACKAGE_NAME + "key_dont_get_notes";
    @Bind(R.id.recycler_view_note_list)
    RecyclerView mRecyclerView;
    @Bind(R.id.swipe_refresh_layout)
    SwipeRefreshLayout refreshLayout;

    private List<NoteView> mNotes;
    Context mContext;
    NoteListAdapter mAdapter;
    private boolean needToSetData = false;
    private boolean avoidGettingNotes = false;

    public void setAvoidGettingNotes(boolean avoidGettingNotes) {
        this.avoidGettingNotes = avoidGettingNotes;
    }

    public NoteListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_note_list, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch(id){
            case R.id.action_order_by_date:
                if(mAdapter != null) {
                    mAdapter.orderByDate();
                }
                return true;
            case R.id.action_order_by_name:
                if(mAdapter != null) {
                    mAdapter.orderByName();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_note_list, container, false);

        ButterKnife.bind(this, view);
        mContext = getActivity();

        if(getActivity() instanceof ToolbarAndRefreshActivity) {
            ((ToolbarAndRefreshActivity)getActivity()).setRefreshLayout(refreshLayout);
            ((ToolbarAndRefreshActivity)getActivity()).getRefreshLayout().setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    getNotesFromEverNote();
                }
            });
        }

        if(savedInstanceState != null){
            if(savedInstanceState.containsKey(KEY_NEED_TO_SET_DATA)){
                needToSetData = savedInstanceState.getBoolean(KEY_NEED_TO_SET_DATA);
            }
            if(!savedInstanceState.containsKey(KEY_DONT_GET_NOTES)){
                avoidGettingNotes = savedInstanceState.getBoolean(KEY_DONT_GET_NOTES);
            }
        }


        setmRecyclerView();
        if(!avoidGettingNotes){
            getNotesFromEverNote();
        }else{
            needToSetData = true;
        }

        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle savedState){
        savedState.putBoolean(KEY_NEED_TO_SET_DATA, needToSetData);
        savedState.putBoolean(KEY_DONT_GET_NOTES, avoidGettingNotes);
        super.onSaveInstanceState(savedState);
    }

    public void getNotesFromEverNote(){
        if(getActivity() instanceof EverNoteActivity){
            ((EverNoteActivity) getActivity()).showRefreshLayoutSwipeProgress();
            ((EverNoteActivity) getActivity()).getNotesFromEvernote();
        }
    }


    public void setNotes(List<NoteView> notes){
        mNotes = notes;
        if(this.isResumed()) {
            setDataInRecycler();
        }else{
            needToSetData = true;
        }
    }

    private void setmRecyclerView(){
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity()));
    }

    private void setDataInRecycler(){

        mAdapter = new NoteListAdapter(getActivity(), mNotes);
        mAdapter.setOnNoteListener((EverNoteActivity) getActivity());
        mRecyclerView.setAdapter(mAdapter);
        setUpItemTouchHelper(mAdapter);
        setUpAnimationDecoratorHelper();
        avoidGettingNotes = true;
    }

    /**
     * This is the standard support library way of implementing "swipe to delete" feature. You can do custom drawing in onChildDraw method
     * but whatever you draw will disappear once the swipe is over, and while the items are animating to their new position the recycler view
     * background will be visible. That is rarely an desired effect.
     */
    private void setUpItemTouchHelper(NoteListAdapter adapter) {

        NoteListTouchHelper simpleItemTouchCallback = new NoteListTouchHelper(0,
                ItemTouchHelper.RIGHT|ItemTouchHelper.LEFT);
        simpleItemTouchCallback.init(getActivity(), adapter);
        ItemTouchHelper mItemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        mItemTouchHelper.attachToRecyclerView(mRecyclerView);
    }

    /**
     * We're gonna setup another ItemDecorator that will draw the red background in the empty space while the items are animating to thier new positions
     * after an item is removed.
     */
    private void setUpAnimationDecoratorHelper() {
        mRecyclerView.addItemDecoration(new Decorator());
    }


    public void onResume(){
        super.onResume();
        if(getActivity() instanceof EverNoteActivity){
            ((EverNoteActivity)getActivity()).hideBackArrow();
            ((EverNoteActivity)getActivity()).enableRefreshLayoutSwipe();
            ((EverNoteActivity)getActivity()).showFabButton();
            ((EverNoteActivity)getActivity()).setTitle(getResources().getString(R.string.app_name) + " " +
                    getResources().getString(R.string.note_list_fragment));
        }
        if(needToSetData){
            setDataInRecycler();
        }
    }

    public void refresh(NoteView noteView) {
        if(mAdapter != null){
            mAdapter.refresh(noteView);
        }
    }
}
