package com.recycleview.pojo;

import com.google.gson.annotations.SerializedName;

public class Aya {
    @SerializedName("@index")
    private String index;

    @SerializedName("@text")
    private String text;

    public String getIndex ()
    {
        return index;
    }

    public void setIndex (String index)
    {
        this.index = index;
    }

    public String getText ()
    {
        return text;
    }

    public void setText (String text)
    {
        this.text = text;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [index = "+index+", text = "+text+"]";
    }


}