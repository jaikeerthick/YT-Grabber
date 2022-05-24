package com.jaikeerthick.ytgrabber;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.jaikeerthick.ytgrabber.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (binding.editText.getText().toString().trim().length() == 0){
                    // show toast
                    Toast.makeText(MainActivity.this, "Please enter an URL", Toast.LENGTH_SHORT).show();
                }else {
                    Intent intent = new Intent(MainActivity.this, ResultActivity.class);
                    intent.putExtra("url", binding.editText.getText().toString().trim());
                    startActivity(intent);
                }
            }
        });
    }
}