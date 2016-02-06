package com.rukiasoft.androidapps.evernoteclient.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.rukiasoft.androidapps.evernoteclient.EverNoteActivity;
import com.rukiasoft.androidapps.evernoteclient.R;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * A placeholder fragment containing a simple view.
 */
public class LoginFragment extends Fragment {

    @Bind(R.id.login_button)
    Button loginButton;

    public interface OnLoginListener {
        void onLoginClick();
    }

    private OnLoginListener mCallback;

    public LoginFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        ButterKnife.bind(this, view);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallback.onLoginClick();
            }
        });
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {

            if(context instanceof OnLoginListener) {
                mCallback = (OnLoginListener) context;
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

    public void onResume(){
        super.onResume();
        if(getActivity() instanceof EverNoteActivity){
            ((EverNoteActivity)getActivity()).hideBackArrow();
            ((EverNoteActivity)getActivity()).disableRefreshLayoutSwipe();
            ((EverNoteActivity)getActivity()).hideFabButton();
            ((EverNoteActivity)getActivity()).setTitle(getResources().getString(R.string.app_name));

        }
    }
}
