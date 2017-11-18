package com.shabaton.gdejeproblem;

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 * <p>
 * TODO: Replace all uses of this class before publishing your app.
 */


public class Sluzba {
    public final int id;
    public final String naziv;
    public final String ikonica;

    public Sluzba(int id, String naziv, String ikonica) {
        this.id = id;
        this.naziv = naziv;
        this.ikonica = ikonica;
    }

    @Override
    public String toString() {
        return naziv;
    }
}


