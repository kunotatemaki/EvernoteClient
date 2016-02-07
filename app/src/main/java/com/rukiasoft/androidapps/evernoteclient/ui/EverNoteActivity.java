package com.rukiasoft.androidapps.evernoteclient.ui;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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
import com.rukiasoft.androidapps.evernoteclient.ui.LoginFragment;
import com.rukiasoft.androidapps.evernoteclient.ui.NoteListFragment;
import com.rukiasoft.androidapps.evernoteclient.ui.ToolbarAndRefreshActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class EverNoteActivity extends ToolbarAndRefreshActivity implements LoginFragment.OnLoginListener,
        EvernoteLoginFragment.ResultCallback, NoteListAdapter.OnActionListener{

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
                // TODO: 6/2/16 función de añadir notas

            }
        });
        mEverNoteSession = new EvernoteSession.Builder(this)
                .setEvernoteService(EVER_NOTE_SERVICE)
                .setSupportAppLinkedNotebooks(false)
                .build(BuildConfig.EVERNOTE_CONSUMER_KEY, BuildConfig.EVERNOTE_CONSUMER_SECRET)
                .asSingleton();


        //check which fragment I should launch
        if(!mEverNoteSession.isLoggedIn()){
            launchLoginFragment();
        }else{
            launchNoteListFragment();
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
            /*case android.R.id.home:
                onBackPressed();
                return true;*/
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
        LoginFragment fragment = (LoginFragment) getSupportFragmentManager().findFragmentByTag(LoginFragment.class.getCanonicalName());
        if(fragment == null){
            fragment = new LoginFragment();
        }
        // Add the fragment to the 'fragment_container' FrameLayout
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container_evernote, fragment,
                        LoginFragment.class.getCanonicalName()).commit();

    }

    private void launchNoteListFragment(){
        NoteListFragment fragment = (NoteListFragment) getSupportFragmentManager().findFragmentByTag(NoteListFragment.class.getCanonicalName());
        if(fragment == null){
            fragment = new NoteListFragment();
        }
        // Add the fragment to the 'fragment_container' FrameLayout
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container_evernote, fragment,
                        NoteListFragment.class.getCanonicalName()).commit();

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
                    /*EvernoteNoteStoreClient prueba = EvernoteSession.getInstance().getEvernoteClientFactory().getNoteStoreClient();

                    prueba.getNoteAsync(note.getGuid(), true, true, true, true, new EvernoteCallback<Note>() {
                        @Override
                        public void onSuccess(Note result) {
                            String titulo1 = result.getTitle();
                            String body1 = result.getContent();
                            int i = 0;
                            i++;
                        }

                        @Override
                        public void onException(Exception exception) {
                            int i = 0;
                            i++;
                        }
                    });*/
                }
                NoteListFragment fragment = (NoteListFragment)getSupportFragmentManager().findFragmentByTag(
                        NoteListFragment.class.getCanonicalName());
                if(fragment == null){
                    fragment = new NoteListFragment();
                }
                if(!fragment.isResumed()){
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
                int i = 0;
                i++;

            }

        });
    }

    private void testAddNote(){
        if (!EvernoteSession.getInstance().isLoggedIn()) {
            return;
        }

        EvernoteNoteStoreClient noteStoreClient = EvernoteSession.getInstance().getEvernoteClientFactory().getNoteStoreClient();

        Note note = new Note();
        note.setTitle("nota añadida por la app");
        note.setContent(EvernoteUtil.NOTE_PREFIX + "a ver si publicas contenido, cojones" + EvernoteUtil.NOTE_SUFFIX);

        noteStoreClient.createNoteAsync(note, new EvernoteCallback<Note>() {
            @Override
            public void onSuccess(Note result) {
                Toast.makeText(getApplicationContext(), result.getTitle() + " has been created", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onException(Exception exception) {
                Log.e("LOGTAG", "Error creating note", exception);
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
                Snackbar.make(coordinatorLayout, getResources().getString(R.string.no_succesful_login), Snackbar.LENGTH_LONG);
            }
        }
    }


    @Override
    public void onActionClick(NoteView noteView) {

    }
}
