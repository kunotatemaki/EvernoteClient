package com.rukiasoft.androidapps.evernoteclient.ui;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_note_list, container, false);
    }
}
