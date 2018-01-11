package com.shabaton.gdejeproblem;

import java.io.Serializable;


public class Sluzba implements Serializable {
    public final int id;
    public final String naziv;
    public final String ikonica;
    public int tip; // 0 - zajednicka, 1 - njegova, 2 - prijavljen za nju
    public  String datum;

    public Sluzba(int id, String naziv, String ikonica, String datum) {
        this.id = id;
        this.naziv = naziv;
        this.ikonica = ikonica;
        this.datum = datum;
    }

    @Override
    public String toString() {
        return naziv;
    }
}


