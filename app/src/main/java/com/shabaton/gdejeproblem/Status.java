package com.shabaton.gdejeproblem;

import java.io.Serializable;


public class Status implements Serializable {

    public int id;
    public String naziv;
    public String boja;

    public Status(int id, String naziv, String boja) {
        this.id = id;
        this.naziv = naziv;
        this.boja = boja;
    }

    @Override
    public String toString() {
        return naziv;
    }

}
