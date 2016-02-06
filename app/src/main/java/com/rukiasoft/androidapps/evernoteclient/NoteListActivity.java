package com.rukiasoft.androidapps.evernoteclient;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.evernote.client.android.EvernoteSession;
import com.evernote.client.android.login.EvernoteLoginFragment;

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

        // TODO: 6/2/16 remove this. only forces reauthorization (for testing)
        mEvernoteSession.logOut();
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
            // TODO: 6/2/16 logged, show notes
        }
    }

    @Override
    public void onLoginFinished(boolean successful) {
        if (successful) {
            Toast.makeText(getApplicationContext(), "yuhuuuu", Toast.LENGTH_LONG).show();
        } else {
            // Do not change view and show a message
            Toast.makeText(getApplicationContext(), "Could not login. Try again.", Toast.LENGTH_LONG).show();

        }
    }
}
