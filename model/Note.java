package edu.utep.cs.cs4330.notebookio.model;

/**
 * @author: Jesus Chavez
 * @macuser: aex on 4/30/18.
 */
public class Note {
    private final String paragraph;
    private final Span[] spans;
    public Note(String paragraph, Span[] spans) {
        this.paragraph = paragraph;
        this.spans = spans;
    }
}

