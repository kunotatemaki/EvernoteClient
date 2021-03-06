package com.rukiasoft.androidapps.evernoteclient.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.evernote.client.android.EvernoteUtil;
import com.evernote.edam.type.Note;
import com.rukiasoft.androidapps.evernoteclient.R;
import com.rukiasoft.androidapps.evernoteclient.classes.OnSaveNoteListener;

import butterknife.Bind;
import butterknife.ButterKnife;


public class AddNoteFragment extends Fragment {


    @Bind(R.id.body_add_note)
    TextView bodyAdd;
    @Bind(R.id.title_add_note)
    TextView titleAdd;
    @Bind(R.id.title_add_note_wrapper)
    TextInputLayout titleWraper;

    private OnSaveNoteListener mCallback;

    public AddNoteFragment() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_add_note, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch(id){
            case R.id.action_save:
                showDialog();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void showDialog() {

        new AlertDialog.Builder(getActivity())
                .setIcon(android.R.drawable.ic_dialog_info)
                .setTitle(getResources().getString(R.string.save_note))
                .setMessage(getResources().getString(R.string.save_note_body))
                .setPositiveButton(R.string.accept, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        saveNote();
                    }
                })
                .setNegativeButton(R.string.cancel, null)
                .show();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add_note, container, false);
        ButterKnife.bind(this, view);

        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle saveInstance){
        super.onSaveInstanceState(saveInstance);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {

            if(context instanceof OnSaveNoteListener) {
                mCallback = (OnSaveNoteListener) context;
            }
        } catch (ClassCastException e) {
            throw new ClassCastException(getActivity().toString()
                    + " must implement OnHeadlineSelectedListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallback = null;
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

    private void saveNote(){
        if(titleAdd.getText().toString().isEmpty()){
            titleWraper.setError(getResources().getString(R.string.title_needed));
            return;
        }else{
            titleWraper.setError(null);
        }
        Note note = new Note();
        note.setTitle(titleAdd.getText().toString().trim());
        String body = "";
        if(bodyAdd.getText() != null && !bodyAdd.getText().toString().isEmpty()){
            //note.setTitle("nota añadida por la app");

            body = bodyAdd.getText().toString();
        }
        note.setContent(EvernoteUtil.NOTE_PREFIX + body + EvernoteUtil.NOTE_SUFFIX);

        mCallback.onSaveNote(note);

    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }



}
