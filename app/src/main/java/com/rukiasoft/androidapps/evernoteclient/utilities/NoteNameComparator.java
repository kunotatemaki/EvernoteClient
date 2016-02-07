package com.rukiasoft.androidapps.evernoteclient.utilities;

import com.rukiasoft.androidapps.evernoteclient.classes.NoteView;



public class NoteNameComparator implements java.util.Comparator<NoteView>{


    @Override
    public int compare(NoteView n1, NoteView n2) {
        return n1.getNote().getTitle().toLowerCase().compareTo(n2.getNote().getTitle().toLowerCase());
    }
}
