package com.example.microproject.adapters;

import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.example.microproject.R;
import com.example.microproject.database.BookDao;
import com.example.microproject.models.Book;
import java.util.List;
import java.util.Random;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BookAdapter extends RecyclerView.Adapter<BookAdapter.BookViewHolder> {
    private Context context;
    private List<Book> bookList;
    private BookDao bookDao;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    private final int[] imgResources = {
            R.drawable.blue_book,
            R.drawable.yellow_book,
            R.drawable.lavender_book,
            R.drawable.green_book
    };

    private final int[] paddings = {8, 10, 6};

    public BookAdapter(Context context, List<Book> bookList, BookDao bookDao) {
        this.context = context;
        this.bookList = bookList;
        this.bookDao = bookDao;
    }

    public void updateList(List<Book> newList) {
        this.bookList = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public BookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_book, parent, false);
        return new BookViewHolder(view);
    }

    @Override

    public void onBindViewHolder(@NonNull BookViewHolder holder, int position) {
        Book book = bookList.get(position);
        holder.textViewBook.setText(book.getName());
        holder.checkBoxRead.setChecked(book.isRead());

        // Apply strikethrough if the book is marked as read
        if (book.isRead()) {
            holder.textViewBook.setPaintFlags(holder.textViewBook.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        } else {
            holder.textViewBook.setPaintFlags(holder.textViewBook.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
        }

        // Set checkbox listener
        holder.checkBoxRead.setOnCheckedChangeListener((buttonView, isChecked) -> {
            book.setRead(isChecked);

            // Update database when checkbox is toggled
            executorService.execute(() -> bookDao.updateBook(book));

            // Toggle strikethrough based on checkbox state
            if (isChecked) {
                holder.textViewBook.setPaintFlags(holder.textViewBook.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            } else {
                holder.textViewBook.setPaintFlags(holder.textViewBook.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
            }
        });
    }


    @Override
    public int getItemCount() {
        return bookList.size();
    }

    static class BookViewHolder extends RecyclerView.ViewHolder {
        TextView textViewBook;
        CheckBox checkBoxRead;
        RelativeLayout bg;
        LinearLayout main;

        public BookViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewBook = itemView.findViewById(R.id.textViewBook);
            checkBoxRead = itemView.findViewById(R.id.checkBoxRead);
            main = itemView.findViewById(R.id.tbr_main);
            bg = itemView.findViewById(R.id.book_bg);
        }
    }
}
