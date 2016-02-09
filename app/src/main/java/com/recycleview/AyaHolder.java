package com.recycleview;

import android.text.SpannableString;

/**
 * Created by Asad on 2/7/2016.
 */
public class AyaHolder {

    SpannableString aya;

    public SpannableString getAya() {
        return aya;
    }

    public void setAya(SpannableString aya) {
        this.aya = aya;
    }

    public AyaHolder(SpannableString aya) {

        this.aya = aya;
    }
}
