package com.example.android.roomwordssample;

import android.content.Context;
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


public class CarListAdapter extends RecyclerView.Adapter<CarListAdapter.CarViewHolder> {

    private Context context;
    private ArrayList<Car> cars;
    private ArrayList<Car> checked_cars;

    public CarListAdapter(Context c, ArrayList<Car> cars) {
        this.context = c;
        this.cars = cars;
    }

    @NonNull
    @Override
    public CarListAdapter.CarViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_item, parent, false);
        return new CarViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(CarListAdapter.CarViewHolder holder, int position) {
        Car car = cars.get(position);
        holder.brandItemView.setText(car.getBrand());
        holder.modelItemView.setText(car.getModel());
        holder.checkBox.setChecked(car.getIsSelected());
    }

    @Override
    public int getItemCount() {
        return cars.size();
    }

    class CarViewHolder extends RecyclerView.ViewHolder{
        private final TextView brandItemView;
        private final TextView modelItemView;
        private final CheckBox checkBox;

        private CarViewHolder(View itemView) {
            super(itemView);
            brandItemView = itemView.findViewById(R.id.brandTextView);
            modelItemView = itemView.findViewById(R.id.modelTextView);
            checkBox = itemView.findViewById(R.id.checkbox_check);

            checkBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    boolean isChecked = ((CheckBox) v).isChecked();

                    cars.get(getAdapterPosition()).setSelected(isChecked);
                    notifyItemChanged(getAdapterPosition());
                }
            });
        }
    }
}
