package com.example.microproject.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.microproject.R;
import com.example.microproject.models.Book;

import java.util.ArrayList;

public class BookAdapter extends RecyclerView.Adapter<BookAdapter.BookViewHolder> {
    private Context context;
    private ArrayList<Book> bookList;

    public BookAdapter(Context context, ArrayList<Book> bookList) { // Ensure this matches the usage
        this.context = context;
        this.bookList = bookList;
    }



    @NonNull
    @Override
    public BookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_book, parent, false);
        return new BookViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookViewHolder holder, int position) {
        Book book = bookList.get(position);
        holder.textViewBook.setText(book.getName());
        holder.checkBoxRead.setChecked(book.isRead());

        holder.checkBoxRead.setOnCheckedChangeListener((buttonView, isChecked) -> {
            book.setRead(isChecked);
        });
    }

    @Override
    public int getItemCount() {
        return bookList.size();
    }

    static class BookViewHolder extends RecyclerView.ViewHolder {
        TextView textViewBook;
        CheckBox checkBoxRead;

        public BookViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewBook = itemView.findViewById(R.id.textViewBook);
            checkBoxRead = itemView.findViewById(R.id.checkBoxRead);
        }
    }
}
