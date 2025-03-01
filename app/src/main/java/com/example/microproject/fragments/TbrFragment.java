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
import com.example.microproject.models.Book;
import com.example.microproject.adapters.BookAdapter;
import com.example.microproject.R;
import java.util.ArrayList;

public class TbrFragment extends Fragment {

    private final ArrayList<Book> bookList = new ArrayList<>();
    private BookAdapter bookAdapter;
    private EditText editTextBook;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.tbr_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        editTextBook = view.findViewById(R.id.editTextBook);
        Button buttonAddBook = view.findViewById(R.id.buttonAddBook);
        RecyclerView recyclerViewBooks = view.findViewById(R.id.recyclerViewBooks);

        recyclerViewBooks.setLayoutManager(new LinearLayoutManager(requireContext()));
        bookAdapter = new BookAdapter(requireContext(), bookList);

        recyclerViewBooks.setAdapter(bookAdapter);

        buttonAddBook.setOnClickListener(v -> {
            String bookName = editTextBook.getText().toString().trim();
            if (!bookName.isEmpty()) {
                bookList.add(new Book(bookName));
                bookAdapter.notifyDataSetChanged();
                editTextBook.setText(""); // Clear input field
            }
        });
    }
}
