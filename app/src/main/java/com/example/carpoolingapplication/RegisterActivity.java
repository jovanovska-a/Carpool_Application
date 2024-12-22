package com.example.carpoolingapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class RegisterActivity extends AppCompatActivity {

    EditText edUsername, edPassword, edEmail, edConfirm;
    Button btn;
    TextView tv;
    RadioGroup radioGroupUserType;
    RadioButton selectedRadioButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        edUsername = findViewById(R.id.editTextRegUsername);
        edPassword = findViewById(R.id.editTextRegPassword);
        edEmail = findViewById(R.id.editTextRegEmail);
        edConfirm = findViewById(R.id.editTextRegConfirmPassword);
        btn = findViewById(R.id.buttonLogin);
        tv = findViewById(R.id.textViewExistingUser);
        radioGroupUserType = findViewById(R.id.radioGroupUserType);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = edUsername.getText().toString();
                String email = edEmail.getText().toString();
                String password = edPassword.getText().toString();
                String confirm = edConfirm.getText().toString();
                Database db = new Database(getApplicationContext(),"carpool", null, 1);

                int selectedId = radioGroupUserType.getCheckedRadioButtonId();
                selectedRadioButton = findViewById(selectedId);
                String userType = selectedRadioButton.getText().toString();

                if(username.length() == 0 || password.length() == 0 || email.length() == 0 || confirm.length() == 0 || selectedId == -1) {
                    Toast.makeText(getApplicationContext(), "Please fill all the details", Toast.LENGTH_SHORT).show();
                } else {
                    if(password.compareTo(confirm)  == 0) {
                        if(isValid(password)) {
                            db.register(username, email, password, userType);
                            Toast.makeText(getApplicationContext(), "Record Inserted", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                        } else {
                            Toast.makeText(getApplicationContext(), "Password must contain at least 8 characters, having letter, digit and special symbol.", Toast.LENGTH_SHORT).show();
                        }
                    }
                    else{
                        Toast.makeText(getApplicationContext(), "Passwords do not match.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            }
        });
    }

    public static boolean isValid(String password) {
        int f1 = 0, f2 = 0, f3 = 0;
        if(password.length() < 8) {
            return false;
        } else {
            for(int i = 0; i < password.length(); i++) {
                if(Character.isLetter(password.charAt(i))){
                    f1 = 1;
                }
            }
            for(int i = 0; i < password.length(); i++) {
                if(Character.isDigit(password.charAt(i))){
                    f2 = 1;
                }
            }
            for(int i = 0; i < password.length(); i++) {
                char c = password.charAt(i);
                if(c >= 33 && c <= 46 || c == 64){
                    f3 = 1;
                }
            }
            if(f1 == 1 && f2 == 1 && f3 == 1) {
                return true;
            }
            return false;
        }
    }
}