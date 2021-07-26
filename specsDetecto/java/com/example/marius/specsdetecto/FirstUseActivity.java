package com.example.marius.specsdetecto;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.GradientDrawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class FirstUseActivity extends AppCompatActivity {

    EditText pwdField;
    EditText pwdCheckField;
    Button btnSetPwd;
    TextView textNoMatchPwd;

    SharedPreferences sharedPreferences = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_use);
        sharedPreferences = getSharedPreferences("com.example.marius.specsdetecto", MODE_PRIVATE);

    }

    @Override
    protected void onResume(){
        super.onResume();
        if (sharedPreferences.getBoolean("isFirstUse", false)){
                Intent intent = new Intent(FirstUseActivity.this, SpecsActivity.class);
                startActivity(intent);
                finish();
            }else{
                pwdField = findViewById(R.id.pwdField);
                pwdCheckField = findViewById(R.id.pwdCheckField);
                btnSetPwd = findViewById(R.id.btnSetPwd);
                textNoMatchPwd = findViewById(R.id.textNoMatchPwd);

                btnSetPwd.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v){
                        if (pwdCheckField.getText().toString().equals(pwdField.getText().toString())){
                            Intent intent = new Intent(FirstUseActivity.this, SpecsActivity.class);
                            sharedPreferences.edit().putString("password", pwdField.getText().toString()).apply();
                            startActivity(intent);
                        }else{
                            textNoMatchPwd.setText("The passwords do not match!");
                            pwdField.setText("");
                            pwdCheckField.setText("");
                        }
                    }
                });
                sharedPreferences.edit().putBoolean("isFirstUse", true).apply();
        }
    }

}
