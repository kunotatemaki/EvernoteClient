package com.rukiasoft.androidapps.evernoteclient.classes;

import com.evernote.edam.type.Note;

import java.io.Serializable;

/**
 * Created by Raúl Feliz on 7/2/16.
 *
 */
public class NoteView implements Serializable{

    public static final int STATUS_NORMAL = 0;
    public static final int STATUS_EDITING = 1;
    public static final int STATUS_DELETING = 2;

    private Integer status;
    private Note note;

    public NoteView() {
        this.status = STATUS_NORMAL;
    }

    public NoteView(Integer status, Note note) {
        this.status = status;
        this.note = note;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Note getNote() {
        return note;
    }

    public void setNote(Note note) {
        this.note = note;
    }

}
