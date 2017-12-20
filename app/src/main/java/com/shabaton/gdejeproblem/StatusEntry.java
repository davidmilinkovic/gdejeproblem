package com.shabaton.gdejeproblem;

import java.io.Serializable;

public class StatusEntry implements Serializable
{
    public Status status;
    public String datum;
    public String komentar;

    public StatusEntry(Status s, String d, String komentar)
    {
        this.status = s;
        this.datum = d;
        this.komentar = komentar;
    }
}
