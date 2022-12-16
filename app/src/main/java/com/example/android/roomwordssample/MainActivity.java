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
    private final CarListAdapter adapter;
    private CarViewModel mCarViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecyclerView recyclerView = findViewById(R.id.recyclerview);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        recyclerView.setAdapter(adapter);
        // Get a new or existing ViewModel from the ViewModelProvider.
        mCarViewModel = new ViewModelProvider(this).get(CarViewModel.class);

        // Add an observer on the LiveData returned by getAlphabetizedWords.
        // The onChanged() method fires when the observed data changes and the activity is
        // in the foreground.
        // Update the cached copy of the words in the adapter.
        mCarViewModel.getAllCars().observe(this, adapter::submitList);

        adapter.setOnItemLongClickListener(new CarListAdapter.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClicked(Car car) {
                Intent intent = new Intent(MainActivity.this, AddEditCarActivity.class);
                intent.putExtra(AddEditCarActivity.EXTRA_ID, car.getId());
                intent.putExtra(AddEditCarActivity.EXTRA_BRAND, car.getBrand());
                intent.putExtra(AddEditCarActivity.EXTRA_MODEL, car.getModel());
                startActivityForResult(intent, EDIT_CAR_ACTIVITY_REQUEST_CODE);
                return true;
            }
        });

        adapter.setOnCheckedClickListener(new CarListAdapter.OnCheckedClickListener() {
            @Override
            public void OnCheckedClicked(boolean isChecked, Car car) {
                car.setSelected(isChecked);
                mCarViewModel.update(car);
            }
        });

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
                mCarViewModel.delete(adapter.getCarAt(viewHolder.getAdapterPosition()));

                Toast.makeText(MainActivity.this, "Car deleted", Toast.LENGTH_SHORT).show();
            }
        }).attachToRecyclerView(recyclerView);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == NEW_CAR_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
            Car car = new Car(data.getStringExtra(AddEditCarActivity.EXTRA_BRAND),
                    data.getStringExtra(AddEditCarActivity.EXTRA_MODEL), false);
            mCarViewModel.insert(car);
            Toast.makeText(getApplicationContext(), "Car saved", Toast.LENGTH_SHORT).show();
        } else if (requestCode == EDIT_CAR_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK){
            long id = data.getLongExtra(AddEditCarActivity.EXTRA_ID, -1);

            if (id == -1) {
                Toast.makeText(this, "Car can't be updated", Toast.LENGTH_SHORT).show();
                return;
            }
            String brand = data.getStringExtra(AddEditCarActivity.EXTRA_BRAND);
            String model = data.getStringExtra(AddEditCarActivity.EXTRA_MODEL);

            Car car = new Car(brand, model, false);
            car.setId(id);
            mCarViewModel.update(car);

            Toast.makeText(getApplicationContext(), "Car updated", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(
                    getApplicationContext(),
                    "You closed it",
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
        searchView.setQueryHint("Search here");
        searchView.setSubmitButtonEnabled(false);
        searchView.setOnQueryTextListener(this);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.delete_all_cars:
                mCarViewModel.deleteAllCars();
                Toast.makeText(this, "All cars deleted", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.delete_all_checked_cars:
                    mCarViewModel.deleteAllCheckedCars();
                Toast.makeText(this, "All checked cars deleted", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.show_all_checked:

                List<Car> list = null;
                try {
                    list = mCarViewModel.getSelectedCars();
                } catch (ExecutionException | InterruptedException e) {
                    e.printStackTrace();
                }
                StringBuilder str = new StringBuilder();
                assert list != null;
                if (list.size() == 0) {
                    Toast.makeText(this, "Nothing is checked", Toast.LENGTH_SHORT).show();
                }
                else {
                    for (Car car : list) {
                        str.append(car.getId()).append(" ").append(car.getBrand()).append(" ").append(car.getModel()).append(" ");
                    }
                    Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
                }
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
        if (s != null) {
            searchIntoModel(s);
        }
        return true;
    }

    private void searchIntoModel(String s) {
        String str = '%' + s + '%';
        mCarViewModel.getFilteredCars(str).observe(this, adapter::submitList);
    }
}
