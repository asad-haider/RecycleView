package com.recycleview;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class WithoutTranslationAdapter extends
        RecyclerView.Adapter<WithoutTranslationAdapter.ViewHolder> {

    Context context;
    WithoutTranslationAdapter adapter;
    private Typeface ayaFont;


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        adapter = this;
        LayoutInflater inflater = LayoutInflater.from(context);

        ayaFont = Typeface.createFromAsset(context.getAssets(), "fonts/PDMS_Saleem_QuranFont-signed.ttf");

        View ayaView = inflater.inflate(R.layout.without_trans_row_item, parent, false);

        return new ViewHolder(ayaView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        WithoutTranslationHolder withoutTranslationHolder = withoutTranslationHolders.get(position);

        TextView textView = holder.textView;
        textView.setText(withoutTranslationHolder.getAya());
        textView.setTypeface(ayaFont);

    }


    @Override
    public int getItemCount() {
        return withoutTranslationHolders.size();
    }

    private List<WithoutTranslationHolder> withoutTranslationHolders;

    // Pass in the contact array into the constructor
    public WithoutTranslationAdapter(List<WithoutTranslationHolder> withoutTranslationHolders) {
        this.withoutTranslationHolders = withoutTranslationHolders;
    }

    // Provide a direct reference to each of the views within a data item
    // Used to cache the views within the item layout for fast access
    public static class ViewHolder extends RecyclerView.ViewHolder{
        // Your holder should contain a member variable
        // for any view that will be set as you render a row
        public TextView textView;

        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public ViewHolder(View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);

            textView = (TextView) itemView.findViewById(R.id.textView);
            textView.setMovementMethod(LinkMovementMethod.getInstance());
            textView.setHighlightColor(Color.TRANSPARENT);
        }
    }
}