package com.ar.team.company.app.autordm.model;

import java.io.File;
import java.util.List;

@SuppressWarnings("unused")
public class ARMedia {

    // Fields:
    private final List<File> content;

    // Constructor:
    public ARMedia(List<File> content) {
        this.content = content;
    }

    // Getters:
    public List<File> getContent() {
        return content;
    }
}
