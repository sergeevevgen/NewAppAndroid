package com.example.android.roomwordssample;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * View Model to keep a reference to the word repository and
 * an up-to-date list of all words.
 */

public class CarViewModel extends AndroidViewModel {

    private final CarRepository mRepository;
    // Using LiveData and caching what getAlphabetizedWords returns has several benefits:
    // - We can put an observer on the data (instead of polling for changes) and only update the
    //   the UI when the data actually changes.
    // - Repository is completely separated from the UI through the ViewModel.
    public ArrayList<Car> list_cars;
    private int CONSTANT_SOURCE = 0;
    public CarViewModel(@NonNull Application application) {
        super(application);
        mRepository = new CarRepository(application);
        list_cars = new ArrayList<>();
    }

    public void setCONSTANT_SOURCE(int k) {
        if ((k == 1 && CONSTANT_SOURCE == 1) || (k == 0 && CONSTANT_SOURCE == 1))
        {
            saveAllCarsForSwap();
        }

        CONSTANT_SOURCE = k;
    }

    public ArrayList<Car> getAllCars() throws ExecutionException, InterruptedException {
        if (CONSTANT_SOURCE == 0) {
            list_cars = (ArrayList<Car>) mRepository.getAllCars();
        }
        else {
            Future<List<Car>> list = JSONHelper.jsoneWriteExecutor.submit(new Callable<List<Car>>() {
                @Override
                public List<Car> call() {
                    return JSONHelper.importFromJSON(getApplication());
                }
            });
            ArrayList<Car> tmp = (ArrayList<Car>)list.get();
            if (tmp == null) {
                list_cars = new ArrayList<>();
            }
            else
                list_cars = tmp;
        }
        return list_cars;
    }

    public void saveAllCars() {
        if (CONSTANT_SOURCE == 1) {
            JSONHelper.jsoneWriteExecutor.execute(() -> {
                JSONHelper.exportToJSON(getApplication(), list_cars);
            });
        }
    }

    public void saveAllCarsForSwap() {
        JSONHelper.jsoneWriteExecutor.execute(() -> {
            JSONHelper.exportToJSON(getApplication(), list_cars);
        });
    }

    public void insert(Car car) {
        if (CONSTANT_SOURCE == 0) {
            mRepository.insert(car);
        }
    }

    public void delete(Car car) {
        if (CONSTANT_SOURCE == 0) {
            mRepository.delete(car);
        }
    }

    public void update(Car car) {
        if (CONSTANT_SOURCE == 0) {
            mRepository.update(car);
        }
    }

    public void deleteAllCars() {
        if (CONSTANT_SOURCE == 0) {
            mRepository.deleteAllCars();
        }
        list_cars.clear();
    }
}
