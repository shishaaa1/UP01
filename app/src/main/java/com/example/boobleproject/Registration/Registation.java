package com.example.boobleproject.Registration;


import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.boobleproject.Api.ApiClient;
import com.example.boobleproject.Api.ApiService;
import com.example.boobleproject.R;
import com.google.android.material.button.MaterialButtonToggleGroup;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Registation extends AppCompatActivity {

    private EditText etFirstName, etLastName, etBirthday, etLogin, etPassword, etBio;
    private MaterialButtonToggleGroup genderGroup;
    private Button btnRegister;
    private String selectedBirthday = "";
    private DatePicker datePicker;

    private final Calendar calendar = Calendar.getInstance();
    private boolean isMale = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_registation);

        etFirstName = findViewById(R.id.et_firstname);
        etLastName = findViewById(R.id.et_lastname);
        etLogin = findViewById(R.id.et_login);
        etPassword = findViewById(R.id.et_password);
        etBio = findViewById(R.id.et_bio);
        genderGroup = findViewById(R.id.gender_group);
        btnRegister = findViewById(R.id.btn_register);
        datePicker = findViewById(R.id.datePicker);

        genderGroup.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if (isChecked) {
                isMale = (checkedId == R.id.btn_male);
            }
        });
        datePicker.init(
                datePicker.getYear(),
                datePicker.getMonth(),
                datePicker.getDayOfMonth(),
                (view, year, month, dayOfMonth) -> {
                    Calendar cal = Calendar.getInstance();
                    cal.set(year, month, dayOfMonth);
                    SimpleDateFormat iso = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                    selectedBirthday = iso.format(cal.getTime()); // ← ISO для сервера!

                }
        );

        btnRegister.setOnClickListener(v -> {

            registerUser();
        });
    }

    private void registerUser() {
        String firstName = etFirstName.getText().toString().trim();
        String lastName = etLastName.getText().toString().trim();
        String login = etLogin.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String bio = etBio.getText().toString().trim();

        if (firstName.isEmpty() || lastName.isEmpty() || selectedBirthday.isEmpty() ||
                login.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Заполните все поля!", Toast.LENGTH_SHORT).show();
            return;
        }

        ApiService apiService = ApiClient.getApiService();
        Call<Void> call = apiService.registerUser(
                firstName, lastName, selectedBirthday, bio, isMale, login, password
        );

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(Registation.this, "Регистрация успешна!", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    String errorMessage = "Неизвестная ошибка";
                    ResponseBody errorBody = response.errorBody();
                    if (errorBody != null) {
                        try {
                            errorMessage = errorBody.string();

                        } catch (IOException e) {
                            Log.e("RegistrationError", "Ошибка чтения ответа: " + e.getMessage());
                            errorMessage = "Ошибка чтения ответа сервера";
                        }
                    }
                    Toast.makeText(Registation.this, "Ошибка регистрации: " + errorMessage, Toast.LENGTH_LONG).show();
                    Log.e("RegistrationError", "Код: " + response.code() + ", Тело: " + errorMessage);
                }

            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                String errorMessage = "Ошибка соединения: " + t.getMessage();
                Toast.makeText(Registation.this, errorMessage, Toast.LENGTH_LONG).show();

                Log.e("REGISTRATION_ERROR", errorMessage, t);  // ← ПОЛНЫЙ СТЭК-ТРЕЙС!
            }
        });
    }
}