package com.ar.team.company.app.autordm.control.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ar.team.company.app.autordm.databinding.DocumentsItemViewBinding;
import com.ar.team.company.app.autordm.model.Document;

import org.jetbrains.annotations.NotNull;

import java.util.List;

@SuppressWarnings("unused")
public class DocumentsAdapter extends RecyclerView.Adapter<DocumentsAdapter.DocumentViewHolder> {

    // Fields:
    private final Context context;
    private final List<Document> documents;

    // Constructor:
    public DocumentsAdapter(Context context, List<Document> documents) {
        // Initializing:
        this.context = context;
        this.documents = documents;
        // Notify:
        notifyDataSetChanged();
    }

    // Adapter:
    @NonNull
    @NotNull
    @Override
    public DocumentViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        // Initializing:
        LayoutInflater inflater = LayoutInflater.from(context);
        DocumentsItemViewBinding binding = DocumentsItemViewBinding.inflate(inflater, parent, false);
        // Retuning:
        return new DocumentViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull DocumentsAdapter.DocumentViewHolder holder, int position) {
        // Initializing:
        Document document = documents.get(position);
        // Developing:
        holder.binding.folderImageView.setImageDrawable(document.getDocIcon());
        holder.binding.fileExtensionTextView.setText(document.getDocType());
        holder.binding.fileSizeTextView.setText(document.getDocSize());
        holder.binding.lastFileNameTextView.setText(document.getLastDocName());
        holder.binding.fileExtensionTextView.setTextColor(document.getDocColor());
    }

    @Override
    public int getItemCount() {
        return documents.size();
    }

    // Holders:
    @SuppressWarnings("unused")
    static class DocumentViewHolder extends RecyclerView.ViewHolder {

        // Fields:
        private final DocumentsItemViewBinding binding;

        // Constructor:
        public DocumentViewHolder(@NonNull @NotNull DocumentsItemViewBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        // Getters:
        public DocumentsItemViewBinding getBinding() {
            return binding;
        }
    }

    // Getters(&Setters):
    public Context getContext() {
        return context;
    }

    public List<Document> getDocuments() {
        return documents;
    }
}
