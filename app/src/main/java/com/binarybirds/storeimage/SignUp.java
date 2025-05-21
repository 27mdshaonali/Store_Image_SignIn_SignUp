package com.binarybirds.storeimage;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

public class SignUp extends AppCompatActivity {

    EditText signUpName, signUpEmail, signUpPass, signUpRePass;
    TextView logInPage;
    Button signUpBtn;
    ImageView imageView;
    String name, email, pass, rePass;

    // Use a clean folder name (no spaces) on your server
    String SIGN_UP_URL = "http://192.168.0.100/Store%20Image%20and%20SIgn%20in%20and%20Sign%20Up/sign_up.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_up);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize views
        signUpName = findViewById(R.id.signUpName);
        signUpEmail = findViewById(R.id.signUpEmail);
        signUpPass = findViewById(R.id.signUpPass);
        signUpRePass = findViewById(R.id.signUpRePass);
        signUpBtn = findViewById(R.id.signUpBtn);
        logInPage = findViewById(R.id.logInPage);
        imageView = findViewById(R.id.imageView);

        // Sign Up Button Click
        signUpBtn.setOnClickListener(v -> {
            name = signUpName.getText().toString().trim();
            email = signUpEmail.getText().toString().trim();
            pass = signUpPass.getText().toString().trim();
            rePass = signUpRePass.getText().toString().trim();

            // Basic validation
            if (name.isEmpty() || email.isEmpty() || pass.isEmpty() || rePass.isEmpty()) {
                Toast.makeText(getApplicationContext(), "Please fill all the fields", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!pass.equals(rePass)) {
                Toast.makeText(getApplicationContext(), "Passwords do not match", Toast.LENGTH_SHORT).show();
                return;
            }

            // Send data to server using Volley
            StringRequest stringRequest = new StringRequest(Request.Method.POST, SIGN_UP_URL,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            if (response.trim().equals("success")) {
                                Toast.makeText(getApplicationContext(), "Sign Up Successful", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(getApplicationContext(), Dashboard.class));
                                signUpBtn.setClickable(false); // Prevent multiple clicks
                            } else if (response.trim().equals("exists")) {
                                Toast.makeText(getApplicationContext(), "Email already exists", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getApplicationContext(), "Sign Up Failed: " + response, Toast.LENGTH_SHORT).show();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(getApplicationContext(), "Error: " + error.toString(), Toast.LENGTH_LONG).show();
                        }
                    }) {

                @Nullable
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> data = new HashMap<>();
                    data.put("name", name);
                    data.put("email", email);
                    data.put("password", pass);
                    return data;

                }
            };

            // Add request to queue
            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            requestQueue.add(stringRequest);
        });

        // Go to login page
        logInPage.setOnClickListener(v -> {
            startActivity(new Intent(SignUp.this, MainActivity.class));
        });
    }
}
