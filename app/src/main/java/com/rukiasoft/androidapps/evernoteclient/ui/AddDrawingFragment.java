package com.rukiasoft.androidapps.evernoteclient.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.evernote.client.android.EvernoteUtil;
import com.evernote.edam.type.Note;
import com.rukiasoft.androidapps.evernoteclient.R;
import com.rukiasoft.androidapps.evernoteclient.classes.DrawingView;
import com.rukiasoft.androidapps.evernoteclient.classes.OCRClass;
import com.rukiasoft.androidapps.evernoteclient.classes.OnSaveNoteListener;
import com.rukiasoft.androidapps.evernoteclient.utilities.Tools;

import butterknife.Bind;
import butterknife.ButterKnife;


public class AddDrawingFragment extends Fragment {


    @Bind(R.id.add_drawing_canvas)
    DrawingView canvas;

    private OnSaveNoteListener mCallback;

    public AddDrawingFragment() {
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
        inflater.inflate(R.menu.menu_draw_note, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch(id){
            case R.id.action_call_ocr:
                if(canvas != null){
                    OCRClass ocrClass = new OCRClass(getActivity());
                    String recognizedText = ocrClass.getOCRFromBitmap(canvas.getBitmapFromDrawing());
                    showDialogWithRecognizedText(recognizedText);
                }
                //callOCR();
                return true;
            case R.id.action_delete_draw:
                if(canvas != null){
                    canvas.clearDrawing();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void showDialogWithRecognizedText(final String recognizedText) {
        String sBody = getResources().getString(R.string.recognized);
        Tools tools = new Tools();
        String strColor = tools.getStringColorFromIntColor(getActivity(), R.color.colorAccent);
        sBody = sBody.replace("_RECOGNIZED_", "<b><font color=\"" + strColor + "\">" + recognizedText + "</font></b>");

        new AlertDialog.Builder(getActivity())
                .setIcon(android.R.drawable.ic_dialog_info)
                .setTitle(getResources().getString(R.string.OCR_title))
                .setMessage(Html.fromHtml(sBody))
                .setPositiveButton(R.string.accept, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        saveNote(recognizedText);
                    }
                })
                .setNegativeButton(R.string.cancel, null)
                .show();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add_drawing, container, false);
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

    private void saveNote(String recognizedText){
        Note note = new Note();
        note.setTitle(recognizedText.trim());
        String body = "";

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
