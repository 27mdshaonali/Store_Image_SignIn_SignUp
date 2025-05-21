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

    }


    public void initialize() {

        imageView = findViewById(R.id.imageView);
        logInEmail = findViewById(R.id.logInEmail);
        logInPass = findViewById(R.id.logInPass);
        logInBtn = findViewById(R.id.logInBtn);
        createAccount = findViewById(R.id.createAccount);

        email = pass = "";

        createAccount.setOnClickListener(v -> {

            startActivity(new Intent(MainActivity.this, SignUp.class));

        });

        logInBtn.setOnClickListener(v -> {

            email = logInEmail.getText().toString().trim();
            pass = logInPass.getText().toString().trim();

            if (!email.isEmpty() && !pass.isEmpty()) {
                StringRequest stringRequest = new StringRequest(Request.Method.POST, SIGN_IN_URL, response -> {
                    if (response.trim().equals("success")) {
                        startActivity(new Intent(getApplicationContext(), Dashboard.class));
                    } else {
                        Toast.makeText(getApplicationContext(), "Invalid email or password", Toast.LENGTH_SHORT).show();
                    }
                }, error -> Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_SHORT).show()) {
                    @Nullable
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

}