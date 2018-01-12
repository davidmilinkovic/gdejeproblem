package com.shabaton.gdejeproblem;


import java.util.Date;

public class Obavestenje {
    public final int id;
    public final String naslov;
    public final String tekst;
    public Sluzba sluzba;
    public final boolean imaKraj;
    public final boolean obavesteno;
    public final boolean uklonjeno;

    public Obavestenje(int id, String naslov, String tekst, Sluzba sluzba, boolean imaKraj) {
        this.id = id;
        this.naslov = naslov;
        this.tekst = tekst;
        this.sluzba = sluzba;
        this.imaKraj = imaKraj;
        this.obavesteno = false;
        this.uklonjeno = false;
    }


}
