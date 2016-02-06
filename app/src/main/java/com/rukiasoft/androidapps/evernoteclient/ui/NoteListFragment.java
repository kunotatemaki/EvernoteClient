package com.rukiasoft.androidapps.evernoteclient.ui;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import com.rukiasoft.androidapps.evernoteclient.R;

/**
 * A placeholder fragment containing a simple view.
 */
public class NoteListFragment extends Fragment {

    public NoteListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        setHasOptionsMenu(true);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_note_list, container, false);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_note_list, menu);
    }

    @Override
    public void onResume(){
        super.onResume();
        if(getActivity() instanceof EverNoteActivity){
            ((EverNoteActivity)getActivity()).hideBackArrow();
            ((EverNoteActivity)getActivity()).enableRefreshLayoutSwipe();
            ((EverNoteActivity)getActivity()).showFabButton();
            ((EverNoteActivity)getActivity()).setTitle(getResources().getString(R.string.app_name) + " - " +
                    getResources().getString(R.string.note_list_fragment));
        }

    }

}
