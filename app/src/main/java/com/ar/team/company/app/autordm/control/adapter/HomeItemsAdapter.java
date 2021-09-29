package com.ar.team.company.app.autordm.control.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ar.team.company.app.autordm.R;
import com.ar.team.company.app.autordm.databinding.HomeItemBinding;
import com.ar.team.company.app.autordm.ui.interfaces.HomeItemClickListener;

import org.jetbrains.annotations.NotNull;

public class HomeItemsAdapter  extends RecyclerView.Adapter<HomeItemsAdapter.HomeViewHolder>{
    // init Interface
    private final HomeItemClickListener itemClickListener;
    private final Context context;
    // Fields:
    public final int[] iconsItems = {R.drawable.ic_chat,R.drawable.ic_status,R.drawable.ic_images , R.drawable.ic_videos,R.drawable.ic_voice, R.drawable.ic_documents};
    public final String[] namesItems ={"Chat","Status","Images","Videos","Voice","Document"};

    // Constructor:


    public HomeItemsAdapter(Context context)
    {
        this.context = context;

        this.itemClickListener = (HomeItemClickListener) context;
    }

    public @NotNull HomeViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        // Initializing:
        LayoutInflater inflater = LayoutInflater.from(context);
        HomeItemBinding binding = HomeItemBinding.inflate(inflater, parent, false);
        // Returning:
        return new HomeItemsAdapter.HomeViewHolder(binding);
    }


    @Override
    public void onBindViewHolder(@NonNull @NotNull HomeItemsAdapter.HomeViewHolder holder, int position)
    {
    holder.binding.imgItemHome.setImageResource(iconsItems[position]);
    holder.binding.tvItemHome.setText(namesItems[position]);

    //open Fragment when click
    holder.binding.layoutItem.setOnClickListener(view -> itemClickListener.openFragment(position));


    }

    /**
     * Returns the total number of items in the data set held by the adapter.
     *
     * @return The total number of items in this adapter.
     */
    @Override
    public int getItemCount() {
        return iconsItems.length;
    }


    // ViewHolder:
static class HomeViewHolder extends RecyclerView.ViewHolder {

    // Fields:
    private final HomeItemBinding binding;

    // Constructor:
    public HomeViewHolder(@NonNull @NotNull HomeItemBinding binding) {
        super(binding.getRoot());
        // Initializing:
        this.binding = binding;
    }

    // Getters:
    public HomeItemBinding getBinding() {
        return binding;
    }
}


}
