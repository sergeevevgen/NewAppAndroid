package com.example.android.roomwordssample;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

/**
 * The Room Magic is in this file, where you map a Java method call to an SQL query.
 *
 * When you are using complex data types, such as Date, you have to also supply type converters.
 * To keep this example basic, no types that require type converters are used.
 * See the documentation at
 * https://developer.android.com/topic/libraries/architecture/room.html#type-converters
 */

@Dao
public interface CarDao {
    // LiveData is a data holder class that can be observed within a given lifecycle.
    // Always holds/caches latest version of data. Notifies its active observers when the
    // data has changed. Since we are getting all the contents of the database,
    // we are notified whenever any of the database contents have changed.
    @Query("SELECT * FROM car_table ORDER BY brand ASC")
    LiveData<List<Car>> getAlphabetizedByBrands();

    @Query("SELECT * FROM car_table WHERE brand LIKE '%' || :query || '%' OR model LIKE '%' || :query || '%' ORDER BY brand ASC")
    LiveData<List<Car>> getFilteredCars(String query);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(Car car);

    @Update
    void update(Car car);

    @Query("DELETE FROM car_table")
    void deleteAll();

    @Delete
    void delete(Car car);

    @Query("SELECT * FROM car_table WHERE brand = :brand AND model = :model")
    LiveData<Car> getByBrandAndModel(String brand, String model);

    @Query("SELECT * FROM car_table WHERE isSelected = :select")
    List<Car> getSelected(Boolean select);

    @Query("DELETE FROM car_table WHERE isSelected = :selected")
    void deleteAllChecked(Boolean selected);
}
