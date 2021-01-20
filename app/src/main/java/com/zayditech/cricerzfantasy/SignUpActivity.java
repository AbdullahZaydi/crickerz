package com.zayditech.cricerzfantasy;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import static androidx.core.content.ContextCompat.startActivity;

public class SignUpActivity extends AppCompatActivity {
    EditText emailId, password, confirm_password;
    Button btnSignUp;
    TextView tvSignIn;
    FirebaseAuth mFirebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        this.getSupportActionBar().hide();
        mFirebaseAuth = FirebaseAuth.getInstance();
        emailId = findViewById(R.id.email);
        password = findViewById(R.id.password);
        confirm_password = findViewById(R.id.confirm_password);
        btnSignUp = findViewById(R.id.signUpBtn);
        tvSignIn = findViewById(R.id.signIn_button);
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailId.getText().toString();
                String pwd = password.getText().toString();
                String confirm_pwd = password.getText().toString();
                if(email.isEmpty()){
                    emailId.setError("Please enter email address");
                    emailId.requestFocus();
                }
                else  if(pwd.isEmpty()){
                    password.setError("Please enter your password");
                    password.requestFocus();
                }
                else if(confirm_pwd.isEmpty()) {
                    confirm_password.setError("Please confirm your password");
                    confirm_password.requestFocus();
                }
                else if(!confirm_pwd.equals(pwd)) {
                    confirm_password.setError("The both password don't match");
                    confirm_password.requestFocus();
                }
                else  if(email.isEmpty() && pwd.isEmpty()){
                    Toast.makeText(SignUpActivity.this,"Fields Are Empty!",Toast.LENGTH_SHORT).show();
                }
                else  if(!(email.isEmpty() && pwd.isEmpty())){
                    mFirebaseAuth.createUserWithEmailAndPassword(email, pwd).addOnCompleteListener(SignUpActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(!task.isSuccessful()){
                                Toast.makeText(SignUpActivity.this,"SignUp Unsuccessful, Please Try Again",Toast.LENGTH_SHORT).show();
                            }
                            else {
                                startActivity(new Intent(SignUpActivity.this, MainActivity.class));
                            }
                        }
                    });
                }
                else{
                    Toast.makeText(SignUpActivity.this,"Error Occurred!",Toast.LENGTH_SHORT).show();

                }
            }
        });

        tvSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(SignUpActivity.this,MainActivity.class);
                startActivity(i);
            }
        });
    }
}
