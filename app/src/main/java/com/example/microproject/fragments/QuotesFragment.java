package com.example.microproject.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.microproject.R;
import com.example.microproject.adapters.QuotesAdapter;
import com.example.microproject.database.AppDatabase;
import com.example.microproject.database.QuoteDao;
import com.example.microproject.models.Quote;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class QuotesFragment extends Fragment {
    private List<Quote> quoteList = new ArrayList<>();
    private QuotesAdapter adapter;
    private RecyclerView quotesRecyclerView;
    private EditText quoteInput;
    private QuoteDao quoteDao;
    private ExecutorService executorService;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Initialize database and DAO
        AppDatabase database = AppDatabase.getInstance(requireContext());
        quoteDao = database.quoteDao();
        executorService = Executors.newSingleThreadExecutor();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.quote_fragment, container, false);
        return view;
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

        // Load quotes from database
        loadQuotes();

        // Initialize "Add New" Button
        Button addQuoteBtn = view.findViewById(R.id.addQuoteBtn);
        addQuoteBtn.setOnClickListener(v -> {
            String quoteText = quoteInput.getText().toString().trim();
            if (!quoteText.isEmpty()) {
                addQuote(quoteText);
                quoteInput.setText(""); // Clear input field
            } else {
                Toast.makeText(getContext(), "Please enter a quote", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadQuotes() {
        executorService.execute(() -> {
            List<Quote> quotes = quoteDao.getAllQuotes();
            requireActivity().runOnUiThread(() -> {
                quoteList.clear();
                quoteList.addAll(quotes);
                adapter.notifyDataSetChanged();
            });
        });
    }

    private void addQuote(String quoteText) {
        executorService.execute(() -> {
            Quote newQuote = new Quote(quoteText);
            long quoteId = quoteDao.insertQuote(newQuote);
            
            // Reload quotes to get the updated list
            requireActivity().runOnUiThread(() -> {
                loadQuotes();
                Toast.makeText(getContext(), "Quote added successfully", Toast.LENGTH_SHORT).show();
            });
        });
    }
}
