package com.recycleview.pojo;

import com.google.gson.annotations.SerializedName;

/**
 * Created by hp on 1/19/2016.
 */
public class Quran {
    @SerializedName("sura")
    private Sura[] sura;

    public Sura[] getSura ()
    {
        return sura;
    }

    public void setSura (Sura[] sura)
    {
        this.sura = sura;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [sura = "+sura+"]";
    }
}