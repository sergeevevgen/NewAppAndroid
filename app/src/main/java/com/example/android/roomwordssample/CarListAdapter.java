package com.example.android.roomwordssample;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;


public class CarListAdapter extends ListAdapter<Car, CarListAdapter.CarViewHolder> {
    private OnItemLongClickListener listener;
    private OnCheckedClickListener listener2;

    private static final DiffUtil.ItemCallback<Car> DIFF_CALLBACK = new DiffUtil.ItemCallback<Car>() {
        @Override
        public boolean areItemsTheSame(@NonNull Car oldItem, @NonNull Car newItem) {
            return oldItem.getId() == newItem.getId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull Car oldItem, @NonNull Car newItem) {
            return oldItem.getBrand().equals(newItem.getBrand()) && oldItem.getModel().equals(newItem.getModel())
                    && oldItem.getIsSelected().equals(newItem.getIsSelected());
        }
    };

    public CarListAdapter() {
        super(DIFF_CALLBACK);
    }

    @NonNull
    @Override
    public CarViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_item, parent, false);
        return new CarViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(CarViewHolder holder, int position) {
        Car current = getItem(position);
        holder.bind(current);
    }

    public Car getCarAt(int position) {
        return getItem(position);
    }

    class CarViewHolder extends RecyclerView.ViewHolder {
        private final TextView brandItemView;
        private final TextView modelItemView;
        private final CheckBox checkBox;

        private CarViewHolder(View itemView) {
            super(itemView);
            brandItemView = itemView.findViewById(R.id.brandTextView);
            modelItemView = itemView.findViewById(R.id.modelTextView);
            checkBox = itemView.findViewById(R.id.checkbox_check);

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    int position = getAdapterPosition();
                    if (listener != null && position != RecyclerView.NO_POSITION)
                        listener.onItemLongClicked(getItem(position));
                    return true;
                }
            });
            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    int position = getAdapterPosition();
                    if (listener2 != null && position != RecyclerView.NO_POSITION)
                        listener2.OnCheckedClicked(isChecked, getItem(position));
                }
            });
        }

        public void bind(Car car) {
            brandItemView.setText(car.getBrand());
            modelItemView.setText(car.getModel());
            checkBox.setChecked(car.getIsSelected());
        }
    }

    public interface OnItemLongClickListener {
        boolean onItemLongClicked(Car car);
    }

    public void setOnItemLongClickListener(OnItemLongClickListener listener) {
        this.listener = listener;
    }

    public interface OnCheckedClickListener {
        void OnCheckedClicked(boolean isChecked, Car car);
    }

    public void setOnCheckedClickListener(OnCheckedClickListener listener) {
        this.listener2 = listener;
    }
}
