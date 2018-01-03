package com.ankhrom.base.observable;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.ankhrom.base.model.ItemModel;

public class AdapterRecycleBinder<T extends ItemModel> extends RecyclerView.Adapter<AdapterRecycleBinder.ViewHolder> {

    private final LayoutInflater inflater;
    private final List<T> items;

    public AdapterRecycleBinder(Context context) {

        inflater = LayoutInflater.from(context);
        items = new ArrayList<>();
    }

    public AdapterRecycleBinder(Context context, List<T> items) {
        this(context);

        this.items.addAll(items);
    }

    public void add(T item) {

        items.add(item);
        notifyItemInserted(items.size() - 1);
    }

    public void addAll(@NonNull Collection<? extends T> collection) {

        items.addAll(collection);
        notifyDataSetChanged();
    }

    public void addAll(T[] items) {

        Collections.addAll(this.items, items);
        notifyDataSetChanged();
    }

    public void remove(T item) {

        remove(items.indexOf(item));
    }

    public void remove(int index) {

        items.remove(index);
        notifyItemRemoved(index);
    }

    public T get(int index) {

        return items.get(index);
    }

    public List<T> getItems() {

        return items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        ViewDataBinding binding = DataBindingUtil.inflate(inflater, viewType, parent, false);

        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        ItemModel model = items.get(position);
        ViewDataBinding binding = holder.binding;
        int variable = model.getVariableBindingResource();

        model.onItemBinded(binding);

        if (variable > 0) {
            binding.setVariable(model.getVariableBindingResource(), model);
            binding.executePendingBindings();
        }
    }

    @Override
    public int getItemViewType(int position) {

        return items.get(position).getLayoutResource();
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        final ViewDataBinding binding;

        public ViewHolder(ViewDataBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
