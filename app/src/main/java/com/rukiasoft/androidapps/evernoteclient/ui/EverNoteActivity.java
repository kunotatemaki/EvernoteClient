package com.rukiasoft.androidapps.evernoteclient.ui;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.evernote.client.android.EvernoteSession;
import com.evernote.client.android.EvernoteUtil;
import com.evernote.client.android.asyncclient.EvernoteCallback;
import com.evernote.client.android.asyncclient.EvernoteNoteStoreClient;
import com.evernote.client.android.login.EvernoteLoginFragment;
import com.evernote.edam.notestore.NoteFilter;
import com.evernote.edam.notestore.NoteList;
import com.evernote.edam.type.Note;
import com.rukiasoft.androidapps.evernoteclient.BuildConfig;
import com.rukiasoft.androidapps.evernoteclient.R;
import com.rukiasoft.androidapps.evernoteclient.classes.NoteView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class EverNoteActivity extends ToolbarAndRefreshActivity implements LoginFragment.OnLoginListener,
        EvernoteLoginFragment.ResultCallback, NoteListAdapter.OnNoteListener, AddNoteFragment.OnSaveNoteListener {

    private static final EvernoteSession.EvernoteService EVER_NOTE_SERVICE = EvernoteSession.EvernoteService.SANDBOX;
    private EvernoteSession mEverNoteSession;

    @Bind(R.id.fragment_container_evernote)
    FrameLayout containerEvernote;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_list);
        ButterKnife.bind(this);

        setToolbar();

        FloatingActionButton fabEverNote = (FloatingActionButton) findViewById(R.id.evernote_fab);
        fabEverNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectAddNoteMethod();

            }
        });
        mEverNoteSession = new EvernoteSession.Builder(this)
                .setEvernoteService(EVER_NOTE_SERVICE)
                .setSupportAppLinkedNotebooks(false)
                .build(BuildConfig.EVERNOTE_CONSUMER_KEY, BuildConfig.EVERNOTE_CONSUMER_SECRET)
                .asSingleton();


        //check which fragment I should launch
        if(savedInstanceState == null) {
            if (!mEverNoteSession.isLoggedIn()) {
                launchLoginFragment();
            } else {
                launchNoteListFragment();
            }
        }
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch(id){
            case R.id.action_log_out:
                mEverNoteSession.logOut();
                launchLoginFragment();
                return true;
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    @Override
    public void onBackPressed(){
        Fragment myFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container_evernote);
        if (myFragment instanceof LoginFragment || myFragment instanceof NoteListFragment) {

            new AlertDialog.Builder(this)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle(getResources().getString(R.string.exit))
                    .setMessage(getResources().getString(R.string.confirm_exit))
                    .setPositiveButton(R.string.accept, new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    })
                    .setNegativeButton(R.string.cancel, null)
                    .show();
        }else{
            super.onBackPressed();
        }
    }

    private void launchLoginFragment(){
        LoginFragment fragment = (LoginFragment) getSupportFragmentManager().findFragmentByTag(
                LoginFragment.class.getCanonicalName());
        if(fragment == null){
            fragment = new LoginFragment();
        }
        // Add the fragment to the 'fragment_container' FrameLayout
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container_evernote, fragment,
                        LoginFragment.class.getCanonicalName()).commit();

    }

    private void launchNoteListFragment(){
        NoteListFragment fragment = (NoteListFragment) getSupportFragmentManager().findFragmentByTag(
                NoteListFragment.class.getCanonicalName());
        if(fragment == null){
            fragment = new NoteListFragment();
        }
        // Add the fragment to the 'fragment_container' FrameLayout
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container_evernote, fragment,
                        NoteListFragment.class.getCanonicalName()).commit();

    }

    private void launchNoteDetailsFragment(Note note){
        NoteDetailsFragment fragment = (NoteDetailsFragment) getSupportFragmentManager().findFragmentByTag(
                NoteDetailsFragment.class.getCanonicalName());
        if(fragment == null){
            fragment = NoteDetailsFragment.newInstance(note);
        }else{
            fragment.setNote(note);
        }
        // Add the fragment to the 'fragment_container' FrameLayout
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container_evernote, fragment,
                        NoteDetailsFragment.class.getCanonicalName());
        transaction.addToBackStack(null);
        transaction.commit();

    }

    private void launchAddNoteFragment(){
        AddNoteFragment fragment = (AddNoteFragment) getSupportFragmentManager().findFragmentByTag(
                AddNoteFragment.class.getCanonicalName());
        if(fragment == null){
            fragment = new AddNoteFragment();
        }

        // Add the fragment to the 'fragment_container' FrameLayout
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container_evernote, fragment,
                        AddNoteFragment.class.getCanonicalName());
        transaction.addToBackStack(null);
        transaction.commit();

    }


    public void loginIntoEvernote() {
        mEverNoteSession.authenticate(this);
    }



    public void getNotesFromEvernote(){
        if (!EvernoteSession.getInstance().isLoggedIn()) {
            return;
        }

        EvernoteNoteStoreClient noteStoreClient = EvernoteSession.getInstance().getEvernoteClientFactory().getNoteStoreClient();
        noteStoreClient.findNotesAsync(new NoteFilter(), 0, 1000, new EvernoteCallback<NoteList>() {

            @Override
            public void onSuccess(NoteList result) {
                List<Note> notes = result.getNotes();
                List<NoteView> notesForAdapter = new ArrayList<NoteView>();
                for (Note note : notes) {
                    notesForAdapter.add(new NoteView(NoteView.STATUS_NORMAL, note));
                }
                NoteListFragment fragment = (NoteListFragment) getSupportFragmentManager().findFragmentByTag(
                        NoteListFragment.class.getCanonicalName());
                if (fragment == null) {
                    fragment = new NoteListFragment();
                }
                if (!fragment.isResumed()) {
                    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.fragment_container_evernote, fragment, NoteListFragment.class.getCanonicalName());
                    transaction.addToBackStack(null);
                    transaction.commit();
                }
                fragment.setNotes(notesForAdapter);
                hideRefreshLayoutSwipeProgress();
            }

            @Override
            public void onException(Exception exception) {
                if (coordinatorLayout != null) {
                    Snackbar.make(coordinatorLayout, getResources().getString(R.string.error_notes), Snackbar.LENGTH_LONG).show();
                }
            }

        });
    }



    @Override
    public void onLoginClick() {
        loginIntoEvernote();
    }

    @Override
    public void onLoginFinished(boolean successful) {
        if (successful) {
            launchNoteListFragment();
        } else {
            if(coordinatorLayout != null) {
                Snackbar.make(coordinatorLayout, getResources().getString(R.string.no_succesful_login), Snackbar.LENGTH_LONG).show();
            }
        }
    }


    @Override
    public void onNoteActionClick(NoteView noteView) {
        if(noteView.getStatus() == NoteView.STATUS_EDITING){
            // TODO: 7/2/16 llamar a función de editar
            if(coordinatorLayout != null) {
                Snackbar.make(coordinatorLayout, getResources().getString(R.string.no_editing), Snackbar.LENGTH_LONG).show();
            }
        }else if(noteView.getStatus() == NoteView.STATUS_DELETING){
            // TODO: 7/2/16 llamar a función de borrar
            if(coordinatorLayout != null) {
                Snackbar.make(coordinatorLayout, getResources().getString(R.string.no_deleting), Snackbar.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onNoteClick(NoteView noteView) {
        getNoteDetails(noteView.getNote());
    }

    private void getNoteDetails(Note note){
        EvernoteNoteStoreClient client = EvernoteSession.getInstance().getEvernoteClientFactory().getNoteStoreClient();

        client.getNoteAsync(note.getGuid(), true, true, true, true, new EvernoteCallback<Note>() {
            @Override
            public void onSuccess(Note result) {
                launchNoteDetailsFragment(result);
            }

            @Override
            public void onException(Exception exception) {
                if (coordinatorLayout != null) {
                    Snackbar.make(coordinatorLayout, getResources().getString(R.string.error_note_details), Snackbar.LENGTH_LONG).show();
                }
            }
        });
    }

    private void selectAddNoteMethod(){
        final String [] items = new String [] {getResources().getString(R.string.type_with_keyboard),
                getResources().getString(R.string.type_with_finger)};
        ArrayAdapter<String> adapter = new ArrayAdapter<> (this, android.R.layout.select_dialog_item,items);
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle(getResources().getString(R.string.type_method));
        builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) { //pick from camera
                if (item == 0) {
                    launchAddNoteFragment();
                } else { //pick from file
                    if (coordinatorLayout != null) {
                        Snackbar.make(coordinatorLayout, "write with your finger", Snackbar.LENGTH_LONG).show();
                    }
                }
            }
        });
        final android.app.AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public void onSaveNote(Note note) {
        if (!EvernoteSession.getInstance().isLoggedIn()) {
            if (coordinatorLayout != null) {
                Snackbar.make(coordinatorLayout, "not logged in", Snackbar.LENGTH_LONG).show();
            }
        }

        EvernoteNoteStoreClient noteStoreClient = EvernoteSession.getInstance().getEvernoteClientFactory().getNoteStoreClient();

        noteStoreClient.createNoteAsync(note, new EvernoteCallback<Note>() {
            @Override
            public void onSuccess(Note result) {
                AddNoteFragment fragment = (AddNoteFragment) getSupportFragmentManager().findFragmentByTag(
                        AddNoteFragment.class.getCanonicalName());
                if(fragment != null && fragment.isResumed()){
                    NoteListFragment noteListFragment = (NoteListFragment) getSupportFragmentManager().findFragmentByTag(
                            NoteListFragment.class.getCanonicalName());
                    if(noteListFragment != null){
                        noteListFragment.setAvoidGettingNotes(false);
                    }
                    onBackPressed();
                }
            }

            @Override
            public void onException(Exception exception) {
                if (coordinatorLayout != null) {
                    Snackbar.make(coordinatorLayout, getResources().getString(R.string.error_adding_note), Snackbar.LENGTH_LONG).show();
                }
            }
        });

    }
}
