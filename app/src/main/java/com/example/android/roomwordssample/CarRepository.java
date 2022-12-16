package com.example.android.roomwordssample;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * Abstracted Repository as promoted by the Architecture Guide.
 * https://developer.android.com/topic/libraries/architecture/guide.html
 */

class CarRepository {

    private final CarDao mCarDao;
    private final LiveData<List<Car>> mAllCars;

    // Note that in order to unit test the WordRepository, you have to remove the Application
    // dependency. This adds complexity and much more code, and this sample is not about testing.
    // See the BasicSample in the android-architecture-components repository at
    // https://github.com/googlesamples
    CarRepository(Application application) {
        CarRoomDatabase db = CarRoomDatabase.getDatabase(application);
        mCarDao = db.carDao();
        mAllCars = mCarDao.getAlphabetizedByBrands();
    }

    // Room executes all queries on a separate thread.
    // Observed LiveData will notify the observer when the data has changed.
    public LiveData<List<Car>> getAllCars() {
        return mAllCars;
    }

    public LiveData<List<Car>> getFilteredCars(String str) { return mCarDao.getFilteredCars(str); }

    // You must call this on a non-UI thread or your app will throw an exception. Room ensures
    // that you're not doing any long running operations on the main thread, blocking the UI.
    public void insert(Car car) {
        CarRoomDatabase.databaseWriteExecutor.execute(() -> {
            mCarDao.insert(car);
        });
    }

    public void delete(Car car) {
        CarRoomDatabase.databaseWriteExecutor.execute(() -> {
            mCarDao.delete(car);
        });
    }

    public void update(Car car) {
        CarRoomDatabase.databaseWriteExecutor.execute(() -> {
            mCarDao.update(car);
        });
    }

    public void deleteAllCars() {
        CarRoomDatabase.databaseWriteExecutor.execute(mCarDao::deleteAll);
    }

    public void deleteAllCheckedCars() {
        CarRoomDatabase.databaseWriteExecutor.execute(() -> {
            mCarDao.deleteAllChecked(true);
        });
    }

    List<Car> getSelectedCars() throws ExecutionException, InterruptedException {
        Future<List<Car>> list = CarRoomDatabase.databaseWriteExecutor.submit(new Callable<List<Car>>() {
            @Override
            public List<Car> call() {
                return mCarDao.getSelected(true);
            }
        });
        return list.get();
    }

    LiveData<Car> getByBrandAndModel() {
        return mCarDao.getByBrandAndModel("VW", "Polo");
    }
}