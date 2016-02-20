package com.recycleview;

import android.text.SpannableString;

/**
 * Created by Asad on 2/7/2016.
 */
public class WithoutTranslationHolder {

    SpannableString aya;

    public SpannableString getAya() {
        return aya;
    }

    public void setAya(SpannableString aya) {
        this.aya = aya;
    }

    public WithoutTranslationHolder(SpannableString aya) {

        this.aya = aya;
    }
}
