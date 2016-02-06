package com.rukiasoft.androidapps.evernoteclient.ui;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
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
import com.rukiasoft.androidapps.evernoteclient.ui.LoginFragment;
import com.rukiasoft.androidapps.evernoteclient.ui.NoteListFragment;
import com.rukiasoft.androidapps.evernoteclient.ui.ToolbarAndRefreshActivity;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class EverNoteActivity extends ToolbarAndRefreshActivity implements LoginFragment.OnLoginListener,
        EvernoteLoginFragment.ResultCallback{

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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_note_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch(id){
            case R.id.action_settings:
                return true;
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    private void launchLoginFragment(){
        LoginFragment fragment = (LoginFragment) getSupportFragmentManager().findFragmentByTag(LoginFragment.class.getCanonicalName());
        if(fragment == null){
            fragment = new LoginFragment();
        }
        // Add the fragment to the 'fragment_container' FrameLayout
        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_container_evernote, fragment,
                        LoginFragment.class.getCanonicalName()).commit();
    }

    private void launchNoteListFragment(){
        NoteListFragment fragment = (NoteListFragment) getSupportFragmentManager().findFragmentByTag(NoteListFragment.class.getCanonicalName());
        if(fragment == null){
            fragment = new NoteListFragment();
        }
        // Add the fragment to the 'fragment_container' FrameLayout
        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_container_evernote, fragment,
                        NoteListFragment.class.getCanonicalName()).commit();
    }


    public void loginIntoEvernote() {
        //if(!mEverNoteSession.isLoggedIn()) {
            mEverNoteSession.authenticate(this);
        /*} else {
            testGetNotes();
            //testAddNote();

        }*/
    }

    @Override
    public void onLoginFinished(boolean successful) {
        if (successful) {
            Toast.makeText(getApplicationContext(), "yuhuuuu", Toast.LENGTH_LONG).show();
            testGetNotes();
        } else {
            // Do not change view and show a message
            Toast.makeText(getApplicationContext(), "Could not login. Try again.", Toast.LENGTH_LONG).show();

        }
    }

    void testGetNotes(){
        if (!EvernoteSession.getInstance().isLoggedIn()) {
            return;
        }

        EvernoteNoteStoreClient noteStoreClient = EvernoteSession.getInstance().getEvernoteClientFactory().getNoteStoreClient();
        noteStoreClient.findNotesAsync(new NoteFilter(), 0, 1000, new EvernoteCallback<NoteList>() {

            @Override
            public void onSuccess(NoteList result) {
                List<Note> notes = result.getNotes();
                for (Note note : notes) {
                    String titulo = note.getTitle();
                    String body = note.getContent();
                    EvernoteNoteStoreClient prueba = EvernoteSession.getInstance().getEvernoteClientFactory().getNoteStoreClient();

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
                    });


                }
            }

            @Override
            public void onException(Exception exception) {
                int i=0;
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
}
