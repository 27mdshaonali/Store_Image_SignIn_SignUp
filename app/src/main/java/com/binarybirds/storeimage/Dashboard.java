package com.binarybirds.storeimage;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Dashboard extends AppCompatActivity {

    Button signOutBtn;
    SessionManager sessionManager;
    TextView result;
    String url = "http://192.168.0.103/Store%20Image%20and%20SIgn%20in%20and%20Sign%20Up/get_user_orders.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_dashboard);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        result = findViewById(R.id.result);
        signOutBtn = findViewById(R.id.signOutBtn);

        // Initialize SessionManager
        sessionManager = new SessionManager(getApplicationContext());

        // Check if user is logged in
        if (!sessionManager.isLoggedIn()) {
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
            return;
        }

        // Get stored email from SharedPreferences
        SharedPreferences prefs = getSharedPreferences("UserData", MODE_PRIVATE);
        String email = prefs.getString("email", null);

        if (email == null) {
            Toast.makeText(this, "Email not found. Please log in again.", Toast.LENGTH_SHORT).show();
            sessionManager.logout();
            startActivity(new Intent(this, MainActivity.class));
            finish();
            return;
        }

        // Fetch user orders using the stored email
        getUserPreferences(email);

        // Logout button listener
        signOutBtn.setOnClickListener(v -> {
            sessionManager.logout();
            // Clear saved email on logout
            prefs.edit().remove("email").apply();
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
        });
    }

    public void getUserPreferences(String email) {
        StringRequest request = new StringRequest(Request.Method.POST, url, response -> {
            Log.d("OrderResponse", response);
            try {
                JSONArray jsonArray = new JSONArray(response);
                result.setText("");  // Clear previous results
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject order = jsonArray.getJSONObject(i);
                    String itemName = order.getString("item_name");
                    int quantity = order.getInt("quantity");
                    int totalPrice = order.getInt("total_price");
                    String status = order.getString("status");

                    String orderText = "Item name : " + itemName +
                            "\nQuantity: " + quantity +
                            "\nTotal Price: " + totalPrice +
                            "\nStatus: " + status + "\n\n";
                    result.append(orderText);

                    // Optional: Toast for debug (you can remove if you want)
                    Toast.makeText(this, "Loaded: " + itemName, Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(this, "Error parsing order data", Toast.LENGTH_SHORT).show();
            }
        }, error -> {
            error.printStackTrace();
            Toast.makeText(this, "Failed to fetch orders: " + error.getMessage(), Toast.LENGTH_SHORT).show();
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("email", email);  // Use the actual logged-in email
                return params;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        queue.add(request);
    }
}
