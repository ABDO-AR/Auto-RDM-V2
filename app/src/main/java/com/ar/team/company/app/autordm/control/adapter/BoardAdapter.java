package com.ar.team.company.app.autordm.control.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ar.team.company.app.autordm.databinding.OnBoardItemBinding;
import com.ar.team.company.app.autordm.model.Board;

import org.jetbrains.annotations.NotNull;

import java.util.List;

@SuppressWarnings("unused")
public class BoardAdapter extends RecyclerView.Adapter<BoardAdapter.BoardViewHolder> {

    // Fields:
    private final Context context;
    private final List<Board> boards;

    // Constructor:
    public BoardAdapter(Context context, List<Board> boards) {
        this.context = context;
        this.boards = boards;
        notifyDataSetChanged();
    }

    // Adapter:
    @NonNull
    @NotNull
    @Override
    public BoardViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        OnBoardItemBinding binding = OnBoardItemBinding.inflate(LayoutInflater.from(context), parent, false);
        return new BoardViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull BoardAdapter.BoardViewHolder holder, int position) {
        // Initializing:
        Board board = boards.get(position);
        // Developing:
        holder.binding.vectorImageView.setImageDrawable(board.getDrawable());
        holder.binding.headerTextView.setText(board.getHeader());
        holder.binding.contentTextView.setText(board.getContent());
    }

    @Override
    public int getItemCount() {
        return boards.size();
    }


    // Holders:
    static class BoardViewHolder extends RecyclerView.ViewHolder {

        // Fields:
        private final OnBoardItemBinding binding;

        // Constructor:
        public BoardViewHolder(@NonNull @NotNull OnBoardItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        // Getters:
        public OnBoardItemBinding getBinding() {
            return binding;
        }
    }

    // Getters:
    public Context getContext() {
        return context;
    }

    public List<Board> getBoards() {
        return boards;
    }
}
