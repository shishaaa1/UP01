package com.example.boobleproject.Logins;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.boobleproject.Api.ApiClient;
import com.example.boobleproject.Api.ApiService;
import com.example.boobleproject.Mainpage;
import com.example.boobleproject.R;
import com.example.boobleproject.Registration.Registation;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
public class MainActivity extends AppCompatActivity {

    private EditText loginEditText;
    private EditText passwordEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loginEditText = findViewById(R.id.loginEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
    }

    public void goToRegistration(View view) {
        Intent intent = new Intent(this, Registation.class);
        startActivity(intent);
    }

    public void onLoginClick(View view) {
        String login = loginEditText.getText().toString();
        String password = passwordEditText.getText().toString();

        if (login.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Введите логин и пароль", Toast.LENGTH_SHORT).show();
            return;
        }

        ApiService apiService = ApiClient.getApiService();
        apiService.loginUser(login, password).enqueue(new Callback<Integer>() {
            @Override
            public void onResponse(Call<Integer> call, Response<Integer> response) {
                if (response.isSuccessful() && response.body() != null && response.body() > 0) {
                    int userId = response.body();

                    // ПРОСТО СОХРАНЯЕМ ID, ПОЛ НЕ НУЖЕН
                    SharedPreferences sharedPreferences = getSharedPreferences("userPrefs", Context.MODE_PRIVATE);
                    sharedPreferences.edit().putInt("userId", userId).apply();

                    Log.d("LOGIN_DEBUG", "Сохранен пользователь ID: " + userId);

                    // Переход на главную страницу
                    Intent intent = new Intent(MainActivity.this, Mainpage.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(MainActivity.this, "Неверный логин или пароль", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Integer> call, Throwable t) {
                Log.e("API_ERROR", "Ошибка сети", t);
                Toast.makeText(MainActivity.this, "Ошибка сети: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}