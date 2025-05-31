package com.shailesha.clevertapandroidsdkdemo;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.clevertap.android.sdk.CleverTapAPI; // Import CleverTapAPI

import java.util.HashMap; // Import HashMap

public class MainActivity extends AppCompatActivity {

    private CleverTapAPI clevertapDefaultInstance;
    private EditText etName, etEmail;
    private Button btnLogin, btnTestEvent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // Initialize CleverTapAPI
        // It's good practice to check if getDefaultInstance returns null,
        // though it usually initializes if Manifest is set up correctly.
        try {
            clevertapDefaultInstance = CleverTapAPI.getDefaultInstance(getApplicationContext());
        } catch (Exception e) {
            // Log or handle initialization failure
            Toast.makeText(this, "CleverTap SDK initialization failed", Toast.LENGTH_LONG).show();
            // You might want to disable CleverTap related UI elements if initialization fails
        }


        // Enable CleverTap Debugger - Do this only in debug builds
        // For release builds, you would typically remove or conditionalize this.
        if (BuildConfig.DEBUG && clevertapDefaultInstance != null) { // Check BuildConfig.DEBUG
            CleverTapAPI.setDebugLevel(CleverTapAPI.LogLevel.DEBUG);
        }


        // Find UI elements
        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        btnLogin = findViewById(R.id.btnLogin);
        btnTestEvent = findViewById(R.id.btnTestEvent);

        // Raise "Product Viewed" event on launch
        if (clevertapDefaultInstance != null) {
            HashMap<String, Object> prodViewedAction = new HashMap<String, Object>();
            prodViewedAction.put("Product Name", "CleverTap Android SDK Demo App");
            prodViewedAction.put("Category", "Software");
            prodViewedAction.put("Price", 0.0); // Ensure this is a double or float
            prodViewedAction.put("Date", new java.util.Date()); // Event dates are automatically tracked by CT
            clevertapDefaultInstance.pushEvent("Product Viewed", prodViewedAction);
            Toast.makeText(this, "Event: Product Viewed", Toast.LENGTH_SHORT).show();
        }


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // LOGIN Button Click Listener
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = etName.getText().toString().trim();
                String email = etEmail.getText().toString().trim();

                if (name.isEmpty() || email.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Please enter Name and Email", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (clevertapDefaultInstance != null) {
                    HashMap<String, Object> profileUpdate = new HashMap<String, Object>();
                    profileUpdate.put("Name", name);             // String
                    profileUpdate.put("Email", email);           // String
                    // profileUpdate.put("Identity", email);     // String or number: Use email or a unique ID as identity
                    // IMPORTANT: The Identity field is crucial for identifying users.
                    // Choose a unique identifier. Email is a common choice.
                    // If you use Identity, ensure it's consistent.
                    profileUpdate.put("MSG-email", true);       // Enable email notifications for this user
                    profileUpdate.put("MSG-push", true);        // Enable push notifications
                    profileUpdate.put("MSG-sms", true);         // Enable SMS
                    profileUpdate.put("MSG-whatsapp", true);    // Enable WhatsApp

                    clevertapDefaultInstance.onUserLogin(profileUpdate);
                    // Or, if you want to set a unique CleverTap ID yourself:
                    // clevertapDefaultInstance.profilePush(profileUpdate); // Use this if you are not using onUserLogin's auto-generated ID
                    Toast.makeText(MainActivity.this, "Login Event Sent to CleverTap", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "CleverTap SDK not initialized.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // TEST Event Button Click Listener
        btnTestEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (clevertapDefaultInstance != null) {
                    // You can add properties to your custom event like this:
                    HashMap<String, Object> testEventProps = new HashMap<>();
                    testEventProps.put("Source", "Button Click");
                    testEventProps.put("Timestamp", new java.util.Date());
                    clevertapDefaultInstance.pushEvent("TEST", testEventProps);
                    Toast.makeText(MainActivity.this, "Event: TEST sent to CleverTap", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "CleverTap SDK not initialized.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Create Notification Channel (Required for Android 8.0 Oreo and above for Push Notifications)
        // This should ideally be done once, e.g., in your Application class,
        // but can be here for simplicity for this demo.
        // Ensure you have a unique channel ID.
        if (clevertapDefaultInstance != null) {
            CleverTapAPI.createNotificationChannel(getApplicationContext(),"YourChannelId","Your Channel Name","Your Channel Description", android.app.NotificationManager.IMPORTANCE_HIGH,true);
            // You can create multiple channels for different types of notifications
            // CleverTapAPI.createNotificationChannel(getApplicationContext(),"AnotherChannelId","Another Channel","Another Desc", android.app.NotificationManager.IMPORTANCE_DEFAULT,true,"sound_file.mp3");
        }
    }
}
