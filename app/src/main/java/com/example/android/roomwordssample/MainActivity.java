package com.example.android.roomwordssample;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;


public class MainActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {

    public static final int NEW_CAR_ACTIVITY_REQUEST_CODE = 1;
    public static final int EDIT_CAR_ACTIVITY_REQUEST_CODE = 2;
    private CarListAdapter adapter;
    private CarViewModel mCarViewModel;
    RecyclerView recyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("RoomDB");
        recyclerView = findViewById(R.id.recyclerview);

        // Get a new or existing ViewModel from the ViewModelProvider.
        mCarViewModel = new ViewModelProvider(this).get(CarViewModel.class);
        try {
            getData();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        setRecyclerView();
        refresh();

        FloatingActionButton fab = findViewById(R.id.button_add_car);
        fab.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, AddEditCarActivity.class);
            startActivityForResult(intent, NEW_CAR_ACTIVITY_REQUEST_CODE);
        });

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder viewHolder1) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
                Car car = mCarViewModel.list_cars.get(viewHolder.getAdapterPosition());
                mCarViewModel.delete(car);
                mCarViewModel.list_cars.remove(car);
                refresh();

                Toast.makeText(MainActivity.this, "Авто удалено", Toast.LENGTH_SHORT).show();
            }
        }).attachToRecyclerView(recyclerView);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == NEW_CAR_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
            Car car = new Car(data.getStringExtra(AddEditCarActivity.EXTRA_BRAND),
                    data.getStringExtra(AddEditCarActivity.EXTRA_MODEL));
            mCarViewModel.list_cars.add(car);
            mCarViewModel.insert(car);
            refresh();
            Toast.makeText(getApplicationContext(), "Авто сохранено", Toast.LENGTH_SHORT).show();
        } else if (requestCode == EDIT_CAR_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK){
            long id = data.getLongExtra(AddEditCarActivity.EXTRA_ID, -1);
            int position = data.getIntExtra(AddEditCarActivity.EXTRA_POSITION, -1);
            if (id == -1 || position == -1) {
                Toast.makeText(this, "Авто не может быть обновлено", Toast.LENGTH_SHORT).show();
                return;
            }
            String brand = data.getStringExtra(AddEditCarActivity.EXTRA_BRAND);
            String model = data.getStringExtra(AddEditCarActivity.EXTRA_MODEL);

            Car car = new Car(brand, model);
            car.setId(id);
            mCarViewModel.update(car);
            mCarViewModel.list_cars.set(position, car);
            refresh();
            Toast.makeText(getApplicationContext(), "Авто обновлено", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(
                    getApplicationContext(),
                    "Вы закрыли её",
                    Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main_menu, menu);

        MenuItem item = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) item.getActionView();
        searchView.setMaxWidth(Integer.MAX_VALUE);
        searchView.setQueryHint("Искать здесь");
        searchView.setSubmitButtonEnabled(false);
        searchView.setOnQueryTextListener(this);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.delete_all_cars:
                mCarViewModel.deleteAllCars();
                refresh();
                Toast.makeText(this, "Все авто удалены", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.delete_all_checked_cars:
                deleteChecked();
                Toast.makeText(this, "Все выделенные авто удалены", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.show_all_checked:

                ArrayList<Car> list = adapter.getChecked_cars();
                StringBuilder str = new StringBuilder();
                assert list != null;
                if (list.size() == 0) {
                    Toast.makeText(this, "Ничто не выделено", Toast.LENGTH_SHORT).show();
                }
                else {
                    for (Car car : list) {
                        str.append(car.getId()).append(" ").append(car.getBrand()).append(" ").append(car.getModel()).append(" ");
                    }
                    Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
                }
                return true;
            case R.id.use_db:
                setTitle("RoomDB");
                mCarViewModel.setCONSTANT_SOURCE(0);
                try {
                    getData();
                } catch (ExecutionException | InterruptedException e) {
                    e.printStackTrace();
                }
                restore();
                return true;
            case R.id.use_json:
                setTitle("JSON");
                mCarViewModel.setCONSTANT_SOURCE(1);
                try {
                    getData();
                } catch (ExecutionException | InterruptedException e) {
                    e.printStackTrace();
                }
                restore();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onQueryTextSubmit(String s) {
        return true;
    }

    @Override
    public boolean onQueryTextChange(String s) {
//        if (s != null) {
//            searchIntoModel(s);
//        }
        return true;
    }

//    private void searchIntoModel(String s) {
//        String str = '%' + s + '%';
//        mCarViewModel.getFilteredCars(str).observe(this, adapter::submitList);
//    }

    private void getData() throws ExecutionException, InterruptedException {
        mCarViewModel.getAllCars();
    }

    private void deleteChecked() {
        ArrayList<Integer> list_2 = adapter.getChecked_positions();
        for (int i = 0; i < list_2.size(); i++) {
            Car car = mCarViewModel.list_cars.get(list_2.get(i));
            mCarViewModel.delete(car);
            mCarViewModel.list_cars.remove((int) list_2.get(i));
        }
        restore();
    }

    private void refresh() {
        adapter.notifyDataSetChanged();
    }

    private void restore() {
        adapter = new CarListAdapter(this, mCarViewModel.list_cars);
        recyclerView.setAdapter(adapter);
        adapter.setOnItemLongClickListener(new CarListAdapter.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClicked(Car car, int position) {
                Intent intent = new Intent(MainActivity.this, AddEditCarActivity.class);
                intent.putExtra(AddEditCarActivity.EXTRA_ID, car.getId());
                intent.putExtra(AddEditCarActivity.EXTRA_BRAND, car.getBrand());
                intent.putExtra(AddEditCarActivity.EXTRA_MODEL, car.getModel());
                intent.putExtra(AddEditCarActivity.EXTRA_POSITION, position);
                startActivityForResult(intent, EDIT_CAR_ACTIVITY_REQUEST_CODE);
                return true;
            }
        });
    }

    private void setRecyclerView() {
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new CarListAdapter(this, mCarViewModel.list_cars);
        recyclerView.setAdapter(adapter);
        adapter.setOnItemLongClickListener(new CarListAdapter.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClicked(Car car, int position) {
                Intent intent = new Intent(MainActivity.this, AddEditCarActivity.class);
                intent.putExtra(AddEditCarActivity.EXTRA_ID, car.getId());
                intent.putExtra(AddEditCarActivity.EXTRA_BRAND, car.getBrand());
                intent.putExtra(AddEditCarActivity.EXTRA_MODEL, car.getModel());
                intent.putExtra(AddEditCarActivity.EXTRA_POSITION, position);
                startActivityForResult(intent, EDIT_CAR_ACTIVITY_REQUEST_CODE);
                return true;
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        mCarViewModel.saveAllCars();
    }
}
