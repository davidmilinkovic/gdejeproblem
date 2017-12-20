package com.shabaton.gdejeproblem;

import java.io.Serializable;

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 * <p>
 * TODO: Replace all uses of this class before publishing your app.
 */

public class Vrsta implements Serializable {
    public final int id;
    public final String naziv;
    public final Sluzba sluzba;

    public Vrsta(int id, String naziv, Sluzba s) {
        this.id = id;
        this.naziv = naziv;
        this.sluzba = s;
    }

    @Override
    public String toString() {
        return naziv;
    }
}



