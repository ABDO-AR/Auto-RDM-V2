package com.ar.team.company.app.autordm.model;

import android.graphics.drawable.Drawable;

@SuppressWarnings("unused")
public class Document {

    // Fields:
    private final String lastDocName, docSize, docType;
    private final Drawable docIcon;
    private final int docColor;

    // Constructor:
    public Document(String lastDocName, String docSize, String docType, Drawable docIcon, int docColor) {
        this.lastDocName = lastDocName;
        this.docSize = docSize;
        this.docType = docType;
        this.docIcon = docIcon;
        this.docColor = docColor;
    }

    // Getters:
    public String getLastDocName() {
        return lastDocName;
    }

    public String getDocSize() {
        return docSize;
    }

    public String getDocType() {
        return docType;
    }

    public Drawable getDocIcon() {
        return docIcon;
    }

    public int getDocColor() {
        return docColor;
    }
}
