package com.example.microproject.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.microproject.R;
import com.example.microproject.adapters.QuotesAdapter;
import com.example.microproject.models.Quote;

import java.util.ArrayList;
import java.util.List;

public class QuotesFragment extends Fragment {
    private List<Quote> quoteList = new ArrayList<>();
    private QuotesAdapter adapter;
    private RecyclerView quotesRecyclerView;
    private EditText quoteInput;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.quote_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize RecyclerView
        quotesRecyclerView = view.findViewById(R.id.quotesRecyclerView);
        quotesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new QuotesAdapter(getContext(), (ArrayList<Quote>) quoteList);
        quotesRecyclerView.setAdapter(adapter);

        quoteInput = view.findViewById(R.id.quoteInput);

        // Initialize "Add New" Button
        Button addQuoteBtn = view.findViewById(R.id.addQuoteBtn);
        addQuoteBtn.setOnClickListener(v -> {
            String q = quoteInput.getText().toString().trim();
            if (!q.isEmpty()) {
                quoteList.add(new Quote(q));
                adapter.notifyItemInserted(quoteList.size() - 1); // Efficient update
                quotesRecyclerView.scrollToPosition(quoteList.size() - 1); // Scroll to new quote
                quoteInput.setText(""); // Clear input field
            }
        });
    }
}
