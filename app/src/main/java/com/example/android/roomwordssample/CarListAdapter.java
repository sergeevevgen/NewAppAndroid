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
    private View view;
    private Context context;
    private final ArrayList<Car> cars;
    private final ArrayList<Car> checked_cars;
    private OnItemLongClickListener listener;
    private final ArrayList<Integer> checked_positions;
    public CarListAdapter(Context c, ArrayList<Car> cars) {
        this.context = c;
        this.cars = cars;
        checked_cars = new ArrayList<>();
        checked_positions = new ArrayList<>();
    }

    public ArrayList<Car> getChecked_cars() {
        return checked_cars;
    }

    public ArrayList<Integer> getChecked_positions() {
        return checked_positions;
    }

    public View getView() {
        return view;
    }
    @NonNull
    @Override
    public CarListAdapter.CarViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_item, parent, false);
        return new CarViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CarListAdapter.CarViewHolder holder, int position) {
        if (cars != null && cars.size() > 0) {
            Car car = cars.get(position);
            holder.brandItemView.setText(car.getBrand());
            holder.modelItemView.setText(car.getModel());
            holder.checkBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (holder.checkBox.isChecked()) {
                        checked_cars.add(car);
                        checked_positions.add(holder.getAdapterPosition());
                    }
                    else {
                        checked_cars.remove(car);
                        checked_positions.remove(holder.getAdapterPosition());
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        if (cars != null)
            return cars.size();
        return 0;
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
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    int position = getAdapterPosition();
                    if (listener != null && position != RecyclerView.NO_POSITION)
                        listener.onItemLongClicked(cars.get(position), position);
                    return true;
                }
            });
        }
    }
    public interface OnItemLongClickListener {
        boolean onItemLongClicked(Car car, int position);
    }

    public void setOnItemLongClickListener(OnItemLongClickListener listener) {
        this.listener = listener;
    }
}
