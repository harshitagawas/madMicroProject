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
import com.example.microproject.database.AppDatabase;
import com.example.microproject.database.BookDao;
import com.example.microproject.models.Book;
import com.example.microproject.adapters.BookAdapter;
import com.example.microproject.R;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TbrFragment extends Fragment {

    private BookAdapter bookAdapter;
    private EditText editTextBook;
    private BookDao bookDao;
    private ExecutorService executorService = Executors.newSingleThreadExecutor();

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

        // Initialize Room Database and DAO
        AppDatabase db = AppDatabase.getInstance(requireContext());
        bookDao = db.bookDao();

        // Load Books from Database
        executorService.execute(() -> {
            List<Book> bookList = bookDao.getAllBooks();
            requireActivity().runOnUiThread(() -> {
                bookAdapter = new BookAdapter(requireContext(), bookList, bookDao);
                recyclerViewBooks.setAdapter(bookAdapter);
            });
        });

        // Add new book to database when button is clicked
        buttonAddBook.setOnClickListener(v -> {
            String bookName = editTextBook.getText().toString().trim();
            if (!bookName.isEmpty()) {
                Book newBook = new Book(bookName, false);

                executorService.execute(() -> {
                    bookDao.insertBook(newBook);
                    List<Book> updatedList = bookDao.getAllBooks();

                    requireActivity().runOnUiThread(() -> {
                        bookAdapter.updateList(updatedList);
                        editTextBook.setText(""); // Clear input field
                    });
                });
            }
        });
    }
}
