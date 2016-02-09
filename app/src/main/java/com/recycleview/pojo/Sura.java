package com.recycleview.pojo;

import com.google.gson.annotations.SerializedName;

/**
 * Created by hp on 1/19/2016.
 */
public class Sura {
    @SerializedName("@index")
    private String index;

    @SerializedName("@name")
    private String name;

    @SerializedName("aya")
    private Aya[] aya;

    public String getIndex ()
    {
        return index;
    }

    public void setIndex (String index)
    {
        this.index = index;
    }

    public String getName ()
    {
        return name;
    }

    public void setName (String name)
    {
        this.name = name;
    }

    public Aya[] getAya ()
    {
        return aya;
    }

    public void setAya (Aya[] aya)
    {
        this.aya = aya;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [index = "+index+", name = "+name+", aya = "+aya+"]";
    }
}