package com.shabaton.gdejeproblem;

public class Status {

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
