package com.example.android.roomwordssample;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Objects;

/**
 * Activity for entering a word.
 */

public class AddEditCarActivity extends AppCompatActivity {

    public static final String EXTRA_BRAND = "com.example.android.brand";
    public static final String EXTRA_MODEL = "com.example.android.model";
    public static final String EXTRA_ID = "com.example.android.id";

    private EditText mEditBrandView;
    private EditText mEditModelView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_car);
        mEditBrandView = findViewById(R.id.edit_brand);
        mEditModelView = findViewById(R.id.edit_model);
        Objects.requireNonNull(getSupportActionBar()).setHomeAsUpIndicator(R.drawable.ic_close);

        Intent intent = getIntent();

        if (intent.hasExtra(EXTRA_ID)) {
            setTitle("Изменить Авто");
            mEditBrandView.setText(intent.getStringExtra(EXTRA_BRAND));
            mEditModelView.setText(intent.getStringExtra(EXTRA_MODEL));
        }
        else {
            setTitle("Добавить Авто");
        }
    }

    private void saveCar() {
        String brand = mEditBrandView.getText().toString();
        String model = mEditModelView.getText().toString();

        if (brand.trim().isEmpty() || model.trim().isEmpty()) {
            Toast.makeText(this, "Укажите данные", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent replyIntent = new Intent();
        replyIntent.putExtra(EXTRA_BRAND, brand);
        replyIntent.putExtra(EXTRA_MODEL, model);

        long id = getIntent().getLongExtra(EXTRA_ID, -1);
        if (id != -1) {
            replyIntent.putExtra(EXTRA_ID, id);
        }
        setResult(RESULT_OK, replyIntent);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.add_car_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.save_car) {
            saveCar();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

