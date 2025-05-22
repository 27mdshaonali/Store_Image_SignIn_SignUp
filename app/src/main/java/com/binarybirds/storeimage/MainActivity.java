package com.binarybirds.storeimage;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    ImageView imageView;
    EditText logInEmail, logInPass;
    Button logInBtn;
    TextView createAccount;
    String email, pass;
    String SIGN_IN_URL = "http://192.168.0.100/Store%20Image%20and%20SIgn%20in%20and%20Sign%20Up/sign_in.php";

    SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initialize();

        // Initialize session manager
        sessionManager = new SessionManager(this);

        // ✅ Check if already logged in
        if (sessionManager.isLoggedIn()) {
            startActivity(new Intent(MainActivity.this, Dashboard.class));
            finish();
        }

        // Open SignUp screen
        createAccount.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, SignUp.class));
        });

        // Handle login button click
        logInBtn.setOnClickListener(v -> {
            email = logInEmail.getText().toString().trim();
            pass = logInPass.getText().toString().trim();

            if (!email.isEmpty() && !pass.isEmpty()) {
                StringRequest stringRequest = new StringRequest(Request.Method.POST, SIGN_IN_URL, response -> {
                    if (response.trim().equals("success")) {
                        // ✅ Save login session only on success
                        sessionManager.createLoginSession(email);

                        SharedPreferences prefs = getSharedPreferences("UserData", MODE_PRIVATE);
                        prefs.edit().putString("email", email).apply();

                        Intent intent = new Intent(getApplicationContext(), Dashboard.class);

                        intent.putExtra("email", email);  // pass email here

                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(getApplicationContext(), "Invalid email or password", Toast.LENGTH_SHORT).show();
                    }
                }, error -> {
                    Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_SHORT).show();
                }) {
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> data = new HashMap<>();
                        data.put("email", email);
                        data.put("password", pass);
                        return data;
                    }
                };

                RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
                requestQueue.add(stringRequest);
            } else {
                Toast.makeText(getApplicationContext(), "Please fill all the fields", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initialize() {
        imageView = findViewById(R.id.imageView);
        logInEmail = findViewById(R.id.logInEmail);
        logInPass = findViewById(R.id.logInPass);
        logInBtn = findViewById(R.id.logInBtn);
        createAccount = findViewById(R.id.createAccount);
    }
}
