package com.rukiasoft.androidapps.evernoteclient;

import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.evernote.client.android.EvernoteSession;
import com.evernote.client.android.EvernoteUtil;
import com.evernote.client.android.asyncclient.EvernoteCallback;
import com.evernote.client.android.asyncclient.EvernoteNoteStoreClient;
import com.evernote.client.android.login.EvernoteLoginFragment;
import com.evernote.edam.error.EDAMNotFoundException;
import com.evernote.edam.error.EDAMSystemException;
import com.evernote.edam.error.EDAMUserException;
import com.evernote.edam.notestore.NoteFilter;
import com.evernote.edam.notestore.NoteList;
import com.evernote.edam.type.Note;
import com.evernote.edam.type.Notebook;
import com.evernote.thrift.TException;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

public class NoteListActivity extends AppCompatActivity implements EvernoteLoginFragment.ResultCallback{

    private static final EvernoteSession.EvernoteService EVERNOTE_SERVICE = EvernoteSession.EvernoteService.SANDBOX;
    private EvernoteSession mEvernoteSession;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginIntoEvernote();
            }
        });
        mEvernoteSession = new EvernoteSession.Builder(this)
                .setEvernoteService(EVERNOTE_SERVICE)
                .setSupportAppLinkedNotebooks(false)
                .build(BuildConfig.EVERNOTE_CONSUMER_KEY, BuildConfig.EVERNOTE_CONSUMER_SECRET)
                .asSingleton();


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
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void loginIntoEvernote() {
        if(!mEvernoteSession.isLoggedIn()) {
            mEvernoteSession.authenticate(this);
        } else {
            testGetNotes();
            //testAddNote();

        }
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
                                int i=0;
                                i++;
                            }

                            @Override
                            public void onException(Exception exception) {
                                    int i=0;
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
}
