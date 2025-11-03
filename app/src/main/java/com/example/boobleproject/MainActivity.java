package com.example.boobleproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

    }

    public void goToRegistration(View view) {
        Intent intent = new Intent(this, Registation.class); // ← ЗАПУСК АКТИВИТИ!
        startActivity(intent);
    }
    public void Main(View view) {
        Intent intent = new Intent(this, Mainpage.class);
        startActivity(intent);
    }
}