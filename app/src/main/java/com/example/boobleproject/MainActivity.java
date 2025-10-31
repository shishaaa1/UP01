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

    public void Registration(View view){
        setContentView(R.layout.activity_registation);
    }
    public void Main(View view) {
        Intent intent = new Intent(this, Mainpage.class);
        startActivity(intent);
    }
}