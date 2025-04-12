package com.example.microproject.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.microproject.R;
import com.example.microproject.models.Quote;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class QuotesAdapter extends RecyclerView.Adapter<QuotesAdapter.QuoteViewHolder> {
    private ArrayList<Quote> quoteList;
    private Context context;

    public QuotesAdapter(Context context, ArrayList<Quote> quoteList) {
        this.context = context;
        this.quoteList = quoteList;
    }

    @NonNull
    @Override
    public QuotesAdapter.QuoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_quote, parent, false);
        return new QuotesAdapter.QuoteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull QuotesAdapter.QuoteViewHolder holder, int position) {
        Quote quote = quoteList.get(position);
        holder.quoteText.setText(quote.getQuote());
        
        // Format and display the timestamp
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
        String formattedDate = sdf.format(new Date(quote.getTimestamp()));
        holder.quoteDate.setText(formattedDate);
    }

    @Override
    public int getItemCount() {
        return quoteList.size();
    }

    static class QuoteViewHolder extends RecyclerView.ViewHolder {
        TextView quoteText;
        TextView quoteDate;

        public QuoteViewHolder(@NonNull View itemView) {
            super(itemView);
            quoteText = itemView.findViewById(R.id.quoteText);
            quoteDate = itemView.findViewById(R.id.quoteDate);
        }
    }
}
