package com.rukiasoft.androidapps.evernoteclient.utilities;

import com.rukiasoft.androidapps.evernoteclient.classes.NoteView;


public class NoteDateComparator implements java.util.Comparator<NoteView>{


    @Override
    public int compare(NoteView n1, NoteView n2) {
        Long dateN1 = n1.getNote().getCreated();
        Long dateN2 = n2.getNote().getCreated();
        return dateN1.compareTo(dateN2);
    }
}
