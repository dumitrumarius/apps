package com.example.marius.specsdetecto;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class CheckPasswordActivity extends AppCompatActivity {


    SharedPreferences sharedPreferences = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_password);
        sharedPreferences = getSharedPreferences("com.example.marius.specsdetecto", MODE_PRIVATE);

        Button checkPasswordButton = findViewById(R.id.checkPasswordButton);
        final EditText checkPasswordField = findViewById(R.id.checkPasswordField);
        final String password = sharedPreferences.getString("password", null);

        checkPasswordButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if(checkPasswordField.getText().toString().equals(password)){
                    Intent intent = new Intent(CheckPasswordActivity.this, EditSpecsActivity.class);
                    startActivity(intent);
                    finish();
                }else{
                    Toast.makeText(getApplicationContext(), "Access Denied!", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}
