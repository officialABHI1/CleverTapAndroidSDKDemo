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
import com.shailesha.clevertapandroidsdkdemo.BuildConfig; // <<<--- ADD THIS IMPORT

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
            Toast.makeText(this, "CleverTap SDK initialization failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
            // You might want to disable CleverTap related UI elements if initialization fails
        }


        // Enable CleverTap Debugger - Do this only in debug builds
        // For release builds, you would typically remove or conditionalize this.
        // Check BuildConfig.DEBUG to ensure this runs only in debug builds
        if (BuildConfig.DEBUG && clevertapDefaultInstance != null) {
            CleverTapAPI.setDebugLevel(CleverTapAPI.LogLevel.DEBUG);
            Toast.makeText(this, "CleverTap Debug Mode Enabled", Toast.LENGTH_SHORT).show();
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
            // prodViewedAction.put("Date", new java.util.Date()); // Event dates are automatically tracked by CT, so this is optional
            clevertapDefaultInstance.pushEvent("Product Viewed", prodViewedAction);
            Toast.makeText(this, "Event: Product Viewed", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "CleverTap not initialized. 'Product Viewed' event not sent.", Toast.LENGTH_LONG).show();
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

                if (name.isEmpty()) {
                    etName.setError("Name is required");
                    etName.requestFocus();
                    return;
                }
                if (email.isEmpty()) {
                    etEmail.setError("Email is required");
                    etEmail.requestFocus();
                    return;
                }
                // Basic email validation (optional, but good practice)
                if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    etEmail.setError("Enter a valid email address");
                    etEmail.requestFocus();
                    return;
                }


                if (clevertapDefaultInstance != null) {
                    HashMap<String, Object> profileUpdate = new HashMap<String, Object>();
                    profileUpdate.put("Name", name);             // String
                    profileUpdate.put("Email", email);           // String
                    // IMPORTANT: The Identity field is crucial for identifying users.
                    // Choose a unique identifier. Email is a common choice.
                    // If you use Identity, ensure it's consistent.
                    profileUpdate.put("Identity", email);     // Using email as Identity for this demo

                    // Optional: User properties for segmentation and personalization
                    profileUpdate.put("Customer Type", "Demo User");


                    // Optional: Explicitly set message subscription preferences
                    // profileUpdate.put("MSG-email", true);       // Enable email notifications for this user
                    // profileUpdate.put("MSG-push", true);        // Enable push notifications
                    // profileUpdate.put("MSG-sms", false);         // Disable SMS
                    // profileUpdate.put("MSG-whatsapp", false);    // Disable WhatsApp

                    clevertapDefaultInstance.onUserLogin(profileUpdate);
                    // Or, if you want to set a unique CleverTap ID yourself and not rely on onUserLogin's auto-generation:
                    // clevertapDefaultInstance.profilePush(profileUpdate);
                    Toast.makeText(MainActivity.this, "Login Event Sent to CleverTap", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "CleverTap SDK not initialized. Login event not sent.", Toast.LENGTH_SHORT).show();
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
                    testEventProps.put("Source", "Button Click from Demo App");
                    testEventProps.put("Timestamp", new java.util.Date());
                    clevertapDefaultInstance.pushEvent("TEST", testEventProps);
                    Toast.makeText(MainActivity.this, "Event: TEST sent to CleverTap", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "CleverTap SDK not initialized. TEST event not sent.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Create Notification Channel (Required for Android 8.0 Oreo and above for Push Notifications)
        // This should ideally be done once, e.g., in your Application class,
        // but can be here for simplicity for this demo.
        // Ensure you have a unique channel ID.
        if (clevertapDefaultInstance != null) {
            // Channel ID, Name, Description, Importance, Show Badge
            CleverTapAPI.createNotificationChannel(getApplicationContext(),"CTDemoChannel","CleverTap Demo Channel","Notifications from CleverTap Demo App", android.app.NotificationManager.IMPORTANCE_HIGH,true);
            // You can create multiple channels for different types of notifications
            // CleverTapAPI.createNotificationChannel(getApplicationContext(),"AnotherChannelId","Another Channel","Another Desc", android.app.NotificationManager.IMPORTANCE_DEFAULT,true,"sound_file.mp3");
            Toast.makeText(this, "Notification Channel Created/Verified", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "CleverTap not initialized. Notification channel not created.", Toast.LENGTH_LONG).show();
        }
    }
}