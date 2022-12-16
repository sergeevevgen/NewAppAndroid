package com.example.android.roomwordssample;

import android.app.Application;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import java.util.List;
import java.util.concurrent.ExecutionException;

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
    private final List<Car> mAllCars;

    public CarViewModel(@NonNull Application application) {
        super(application);
        mRepository = new CarRepository(application);
        mAllCars = mRepository.getAllCars();
    }

    public List<Car> getAllCars() {
        return mAllCars;
    }

    public List<Car> getSelectedCars() throws ExecutionException, InterruptedException {
        return mRepository.getSelectedCars();
    }

    public List<Car> getFilteredCars(String str) {
        return mRepository.getFilteredCars(str);
    }

    public void deleteAllCheckedCars() { mRepository.deleteAllCheckedCars(); }

    public void insert(Car car) {
        mRepository.insert(car);
    }

    public void delete(Car car) {
        mRepository.delete(car);
    }

    public void update(Car car) {
        mRepository.update(car);
    }

    public void deleteAllCars() {
        mRepository.deleteAllCars();
    }
}
