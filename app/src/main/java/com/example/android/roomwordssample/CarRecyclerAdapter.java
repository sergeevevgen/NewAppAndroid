package com.example.android.roomwordssample;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CarRecyclerAdapter extends RecyclerView.Adapter<CarRecyclerAdapter.CarViewHolder> {
    private CarRecyclerAdapter.OnItemLongClickListener listener;
    private CarRecyclerAdapter.OnCheckedClickListener listener2;
    private List<Car> cars;
    private Map<Integer, Boolean> checked;

    public CarRecyclerAdapter(List<Car> cars) {
        this.cars = cars;
        checked = new HashMap<>();
        for (Car car: cars) {
            checked.put((int) car.getId(), car.getIsSelected());
        }
    }

    @NonNull
    @Override
    public CarRecyclerAdapter.CarViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View item = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recyclerview_item, viewGroup, false);
        return new CarViewHolder(item);
    }

    @Override
    public void onBindViewHolder(@NonNull CarRecyclerAdapter.CarViewHolder viewHolder, int i) {
        viewHolder.bind(cars.get(i));
    }

    @Override
    public int getItemCount() {
        return cars.size();
    }

    public Car getItem(int position) {
        return cars.get(position);
    }

    public void setItemChecked(int position) {
        checked.put(position, Boolean.FALSE.equals(checked.get(position)));
    }

    public List<Integer> getCheckedItems() {
        List<Integer> list = new ArrayList<>();
        for(Map.Entry<Integer, Boolean> item : checked.entrySet()) {
            if (item.getValue())
                list.add(item.getKey());
        }
        return list;
    }

    public List<Car> getCheckedCars() {
        List<Car> tmp = new ArrayList<>();
        List<Integer> ch = getCheckedItems();
        for(Integer i : ch) {
            tmp.add(getItem(i));
        }
        return tmp;
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

    public void setOnItemLongClickListener(CarRecyclerAdapter.OnItemLongClickListener listener) {
        this.listener = listener;
    }

    public interface OnCheckedClickListener {
        void OnCheckedClicked(boolean isChecked, Car car);
    }

    public void setOnCheckedClickListener(CarRecyclerAdapter.OnCheckedClickListener listener) {
        this.listener2 = listener;
    }
}
