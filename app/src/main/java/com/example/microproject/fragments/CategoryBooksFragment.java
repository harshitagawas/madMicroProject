package com.example.microproject.fragments;

import static android.app.Activity.RESULT_OK;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.microproject.R;
import com.example.microproject.adapters.CategoryBooksAdapter;
import com.example.microproject.models.CategoryBook;

import java.util.ArrayList;
import java.util.List;

public class CategoryBooksFragment extends Fragment {
    private ActivityResultLauncher<Intent> imagePickerLauncher;
    private List<CategoryBook> categoryBooks = new ArrayList<>();
    private CategoryBooksAdapter adapter;
    private RecyclerView recycler;
    private Uri selectedImageUri;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.add_category_book_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recycler = view.findViewById(R.id.addBookRecycler);
        recycler.setLayoutManager(new GridLayoutManager(getContext(), 2));

        adapter = new CategoryBooksAdapter(getContext(),categoryBooks);
        recycler.setAdapter(adapter);

        Button btn = view.findViewById(R.id.add_book);
        btn.setOnClickListener(v -> showAddBookDialog());

        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        selectedImageUri = result.getData().getData();
                    }
                });
    }

    private void showAddBookDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Add Book");

        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_category_books, null);
        EditText bookNameInput = dialogView.findViewById(R.id.book_name_input);
        Button selectImageButton = dialogView.findViewById(R.id.select_image_button);
        ImageView imagePreview = dialogView.findViewById(R.id.image_preview);

        selectImageButton.setOnClickListener(v -> pickImage(imagePreview));

        builder.setView(dialogView);
        builder.setPositiveButton("Add", (dialog, which) -> {
            String bookName = bookNameInput.getText().toString().trim();
            if (!bookName.isEmpty() && selectedImageUri != null) {
                categoryBooks.add(new CategoryBook(bookName, selectedImageUri));
                adapter.notifyDataSetChanged();
            } else {
                Toast.makeText(getContext(), "Enter book name and select an image", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    private void pickImage(ImageView imagePreview) {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        imagePickerLauncher.launch(intent);
    }
}
