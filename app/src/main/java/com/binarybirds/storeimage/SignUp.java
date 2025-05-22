package com.binarybirds.storeimage;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class SignUp extends AppCompatActivity {
    private static final int REQUEST_GALLERY = 1;
    EditText signUpName, signUpEmail, signUpPass, signUpRePass;
    TextView logInPage;
    Button signUpBtn;
    ImageView imageView;
    Bitmap selectedBitmap = null;

    String SIGN_UP_URL = "http://192.168.0.100/Store%20Image%20and%20SIgn%20in%20and%20Sign%20Up/sign_up.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        signUpName = findViewById(R.id.signUpName);
        signUpEmail = findViewById(R.id.signUpEmail);
        signUpPass = findViewById(R.id.signUpPass);
        signUpRePass = findViewById(R.id.signUpRePass);
        signUpBtn = findViewById(R.id.signUpBtn);
        logInPage = findViewById(R.id.logInPage);
        imageView = findViewById(R.id.imageView);

        imageView.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityForResult(intent, REQUEST_GALLERY);
        });

        signUpBtn.setOnClickListener(v -> {
            String name = signUpName.getText().toString().trim();
            String email = signUpEmail.getText().toString().trim();
            String pass = signUpPass.getText().toString().trim();
            String rePass = signUpRePass.getText().toString().trim();

            if (name.isEmpty() || email.isEmpty() || pass.isEmpty() || rePass.isEmpty()) {
                Toast.makeText(getApplicationContext(), "Please fill all the fields", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!pass.equals(rePass)) {
                Toast.makeText(getApplicationContext(), "Passwords do not match", Toast.LENGTH_SHORT).show();
                return;
            }

            if (selectedBitmap == null) {
                Toast.makeText(getApplicationContext(), "Please select a profile image", Toast.LENGTH_SHORT).show();
                return;
            }

            String encodedImage = bitmapToBase64(selectedBitmap);

            StringRequest stringRequest = new StringRequest(Request.Method.POST, SIGN_UP_URL, response -> {
                switch (response.trim()) {
                    case "success":
                        Toast.makeText(getApplicationContext(), "Sign Up Successful", Toast.LENGTH_SHORT).show();

                        // ✅ Create session after successful sign-up
                        SessionManager sessionManager = new SessionManager(SignUp.this);
                        sessionManager.createLoginSession(email);

                        // Navigate to Dashboard
                        startActivity(new Intent(getApplicationContext(), Dashboard.class));
                        finish();
                        break;

                    case "exists":
                        Toast.makeText(getApplicationContext(), "Email already exists", Toast.LENGTH_SHORT).show();
                        break;
                    case "image_upload_failed":
                        Toast.makeText(getApplicationContext(), "Image upload failed", Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        Toast.makeText(getApplicationContext(), "Failed: " + response, Toast.LENGTH_SHORT).show();
                        break;
                }
            }, error -> Toast.makeText(getApplicationContext(), "Error: " + error.toString(), Toast.LENGTH_LONG).show()) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> data = new HashMap<>();
                    data.put("name", name);
                    data.put("email", email);
                    data.put("password", pass);
                    data.put("image", encodedImage);
                    return data;
                }
            };

            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            requestQueue.add(stringRequest);
        });

        logInPage.setOnClickListener(v -> {
            startActivity(new Intent(SignUp.this, MainActivity.class));
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_GALLERY && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            try {
                InputStream inputStream = getContentResolver().openInputStream(data.getData());
                selectedBitmap = BitmapFactory.decodeStream(inputStream);
                imageView.setImageBitmap(selectedBitmap);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private String bitmapToBase64(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 70, stream);
        byte[] imageBytes = stream.toByteArray();
        return Base64.encodeToString(imageBytes, Base64.DEFAULT);
    }
}