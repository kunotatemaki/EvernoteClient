package com.rukiasoft.androidapps.evernoteclient.ui;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.evernote.edam.type.Note;
import com.rukiasoft.androidapps.evernoteclient.R;
import com.rukiasoft.androidapps.evernoteclient.utilities.Constants;

import java.lang.reflect.ParameterizedType;

import butterknife.Bind;
import butterknife.ButterKnife;


public class NoteDetailsFragment extends Fragment {

    private static final String ARG_PARAM_NOTE = Constants.PACKAGE_NAME + ".note";
    private static final String KEY_NOTE = ARG_PARAM_NOTE;

    Note note;
    @Bind(R.id.body_note_details)
    TextView bodyDetails;
    @Bind(R.id.title_note_details)
    TextView titleDetails;

    public NoteDetailsFragment() {
        // Required empty public constructor
    }

    public void setNote(Note note) {
        this.note = note;
    }

    public static NoteDetailsFragment newInstance(Note note) {
        NoteDetailsFragment fragment = new NoteDetailsFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PARAM_NOTE, note);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            note = (Note) getArguments().getSerializable(ARG_PARAM_NOTE);
        }
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_note_details, container, false);
        ButterKnife.bind(this, view);
        if(savedInstanceState != null){
            if(savedInstanceState.containsKey(KEY_NOTE)){
                note = (Note) savedInstanceState.getSerializable(KEY_NOTE);
            }
        }

        if(note != null) {
            titleDetails.setText(note.getTitle());
            bodyDetails.setText(Html.fromHtml(note.getContent()));
        }

        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle saveInstance){
        if(note != null){
            saveInstance.putSerializable(KEY_NOTE, note);
        }
        super.onSaveInstanceState(saveInstance);
    }


    @Override
    public void onResume() {
        super.onResume();
        if(getActivity() instanceof EverNoteActivity){
            ((EverNoteActivity)getActivity()).showBackArrow();
            ((EverNoteActivity)getActivity()).disableRefreshLayoutSwipe();
            ((EverNoteActivity)getActivity()).hideFabButton();
            ((EverNoteActivity)getActivity()).setTitle(getResources().getString(R.string.app_name));
        }
    }


}
