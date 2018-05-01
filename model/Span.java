package edu.utep.cs.cs4330.notebookio.model;

/**
 * @author: Jesus Chavez
 * @macuser: aex on 4/30/18.
 */
public class Span {
    private final int id, size, start, end, format;
    private final String color, font;

    public Span(int id, int size, int start, int end, int format, String color, String font) {
        this.id = id;
        this.size = size;
        this.start = start;
        this.end = end;
        this.format = format;
        this.color = color;
        this.font = font;
    }
}
