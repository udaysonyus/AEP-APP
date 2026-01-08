package com.example.aepvalidation;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.adobe.marketing.mobile.AdobeCallbackWithError;
import com.adobe.marketing.mobile.AdobeError;
import com.adobe.marketing.mobile.Edge;
import com.adobe.marketing.mobile.EdgeCallback;
import com.adobe.marketing.mobile.EdgeEventHandle;
import com.adobe.marketing.mobile.ExperienceEvent;
import com.adobe.marketing.mobile.Identity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

/**
 * MainActivity - Main UI for AEP Validation App
 * 
 * This activity provides buttons to:
 * - Send Edge events with dummy data
 * - Retrieve and display ECID (Experience Cloud ID)
 * - View SDK status and event responses
 */
public class MainActivity extends AppCompatActivity {

    private static final String TAG = "AEPValidationApp";

    // UI Elements
    private TextView statusTextView;
    private TextView ecidTextView;
    private TextView logTextView;
    private Button sendEdgeEventButton;
    private Button sendEdgeEventWithDataButton;
    private Button getEcidButton;
    private Button clearLogButton;

    // Log buffer for display
    private StringBuilder logBuffer = new StringBuilder();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d(TAG, "MainActivity onCreate");

        // Initialize UI components
        initializeViews();
        
        // Set up button click listeners
        setupButtonListeners();
        
        // Check and display AEP initialization status
        checkAepStatus();
    }

    /**
     * Initialize all view references
     */
    private void initializeViews() {
        statusTextView = findViewById(R.id.statusTextView);
        ecidTextView = findViewById(R.id.ecidTextView);
        logTextView = findViewById(R.id.logTextView);
        sendEdgeEventButton = findViewById(R.id.sendEdgeEventButton);
        sendEdgeEventWithDataButton = findViewById(R.id.sendEdgeEventWithDataButton);
        getEcidButton = findViewById(R.id.getEcidButton);
        clearLogButton = findViewById(R.id.clearLogButton);

        Log.d(TAG, "Views initialized");
    }

    /**
     * Set up click listeners for all buttons
     */
    private void setupButtonListeners() {
        sendEdgeEventButton.setOnClickListener(v -> onSendEdgeEventClicked());
        sendEdgeEventWithDataButton.setOnClickListener(v -> onSendEdgeEventWithDataClicked());
        getEcidButton.setOnClickListener(v -> onGetEcidClicked());
        clearLogButton.setOnClickListener(v -> onClearLogClicked());

        Log.d(TAG, "Button listeners set up");
    }

    /**
     * Check AEP initialization status and update UI
     */
    private void checkAepStatus() {
        String envId = MainApplication.getEnvironmentId();
        boolean isInitialized = MainApplication.isAepInitialized();
        String error = MainApplication.getInitializationError();

        Log.d(TAG, "Checking AEP status - Initialized: " + isInitialized);

        if (error != null) {
            updateStatus("❌ AEP Init Error: " + error);
            appendLog("ERROR: AEP initialization failed: " + error);
        } else if (isInitialized) {
            updateStatus("✅ AEP SDK Initialized");
            appendLog("SDK initialized with Environment ID: " + envId);
        } else {
            updateStatus("⏳ AEP SDK Initializing...");
            appendLog("SDK is initializing with Environment ID: " + envId);
        }

        // Check if placeholder is still set
        if ("YOUR_ENV_ID_HERE".equals(envId)) {
            appendLog("⚠️ WARNING: You need to replace YOUR_ENV_ID_HERE with your actual Environment ID in MainApplication.java");
        }
    }

    /**
     * Handle "Send Edge Event" button click
     * Sends a basic validation event to Adobe Edge
     */
    private void onSendEdgeEventClicked() {
        Log.d(TAG, "========================================");
        Log.d(TAG, "Send Edge Event button clicked");
        Log.d(TAG, "========================================");

        updateStatus("📤 Sending Edge Event...");
        appendLog("Sending basic Edge event...");

        try {
            // Build XDM data map
            Map<String, Object> xdmData = new HashMap<>();
            
            // Required: eventType
            xdmData.put("eventType", "mobile.validation");
            
            // Required: timestamp in ISO 8601 format
            xdmData.put("timestamp", getCurrentTimestamp());

            Log.d(TAG, "XDM Data: " + xdmData.toString());

            // Create the ExperienceEvent
            ExperienceEvent experienceEvent = new ExperienceEvent.Builder()
                .setXdmSchema(xdmData)
                .build();

            // Send the event
            Edge.sendEvent(experienceEvent, new EdgeCallback() {
                @Override
                public void onComplete(List<EdgeEventHandle> handles) {
                    runOnUiThread(() -> {
                        Log.d(TAG, "========================================");
                        Log.d(TAG, "Edge event sent SUCCESS");
                        Log.d(TAG, "Handles received: " + (handles != null ? handles.size() : 0));
                        Log.d(TAG, "========================================");

                        updateStatus("✅ Edge Event Sent Successfully!");
                        appendLog("SUCCESS: Edge event sent");
                        
                        if (handles != null && !handles.isEmpty()) {
                            for (EdgeEventHandle handle : handles) {
                                String handleInfo = "Handle - Type: " + handle.getType();
                                Log.d(TAG, handleInfo);
                                appendLog(handleInfo);
                            }
                        }
                    });
                }
            });

        } catch (Exception e) {
            Log.e(TAG, "Error sending Edge event: " + e.getMessage());
            e.printStackTrace();
            updateStatus("❌ Error: " + e.getMessage());
            appendLog("ERROR: " + e.getMessage());
        }
    }

    /**
     * Handle "Send Edge Event with Product Data" button click
     * Sends a more complex event with custom data
     */
    private void onSendEdgeEventWithDataClicked() {
        Log.d(TAG, "========================================");
        Log.d(TAG, "Send Edge Event with Data button clicked");
        Log.d(TAG, "========================================");

        updateStatus("📤 Sending Edge Event with Product Data...");
        appendLog("Sending Edge event with product data...");

        try {
            // =========================================================
            // BUILD XDM DATA
            // This follows XDM schema structure
            // =========================================================
            Map<String, Object> xdmData = new HashMap<>();
            
            // Required fields
            xdmData.put("eventType", "commerce.productViews");
            xdmData.put("timestamp", getCurrentTimestamp());

            // Commerce data (XDM standard)
            Map<String, Object> commerce = new HashMap<>();
            Map<String, Object> productViews = new HashMap<>();
            productViews.put("value", 1);
            commerce.put("productListViews", productViews);
            xdmData.put("commerce", commerce);

            // Product list items (XDM standard)
            Map<String, Object> productItem = new HashMap<>();
            productItem.put("SKU", "PROD-12345");
            productItem.put("name", "Test Product");
            productItem.put("quantity", 1);
            productItem.put("priceTotal", 99.99);
            
            java.util.List<Map<String, Object>> productListItems = new java.util.ArrayList<>();
            productListItems.add(productItem);
            xdmData.put("productListItems", productListItems);

            Log.d(TAG, "XDM Data: " + xdmData.toString());

            // =========================================================
            // BUILD CUSTOM DATA (non-XDM, will go to custom data path)
            // =========================================================
            Map<String, Object> customData = new HashMap<>();

            // App info
            Map<String, Object> appInfo = new HashMap<>();
            appInfo.put("name", "AEPValidationApp");
            appInfo.put("version", "1.0");
            customData.put("app", appInfo);

            // User info (dummy data for testing)
            Map<String, Object> userInfo = new HashMap<>();
            userInfo.put("testUserId", "U12345");
            userInfo.put("segment", "dummy_segment");
            customData.put("user", userInfo);

            // Action info
            Map<String, Object> actionInfo = new HashMap<>();
            actionInfo.put("screen", "Main");
            actionInfo.put("button", "Send Edge Event with Data");
            customData.put("action", actionInfo);

            Log.d(TAG, "Custom Data: " + customData.toString());

            // =========================================================
            // CREATE AND SEND THE EVENT
            // =========================================================
            ExperienceEvent experienceEvent = new ExperienceEvent.Builder()
                .setXdmSchema(xdmData)
                .setData(customData)  // Custom data goes here
                .build();

            Edge.sendEvent(experienceEvent, new EdgeCallback() {
                @Override
                public void onComplete(List<EdgeEventHandle> handles) {
                    runOnUiThread(() -> {
                        Log.d(TAG, "========================================");
                        Log.d(TAG, "Edge event with data sent SUCCESS");
                        Log.d(TAG, "Handles received: " + (handles != null ? handles.size() : 0));
                        Log.d(TAG, "========================================");

                        updateStatus("✅ Edge Event with Data Sent!");
                        appendLog("SUCCESS: Edge event with product data sent");
                        
                        if (handles != null && !handles.isEmpty()) {
                            for (EdgeEventHandle handle : handles) {
                                String payload = handle.getPayload() != null ? 
                                    handle.getPayload().toString() : "no payload";
                                String handleInfo = "Handle Type: " + handle.getType() + 
                                    ", Payload: " + payload;
                                Log.d(TAG, handleInfo);
                                appendLog(handleInfo);
                            }
                        }
                    });
                }
            });

        } catch (Exception e) {
            Log.e(TAG, "Error sending Edge event with data: " + e.getMessage());
            e.printStackTrace();
            updateStatus("❌ Error: " + e.getMessage());
            appendLog("ERROR: " + e.getMessage());
        }
    }

    /**
     * Handle "Get ECID" button click
     * Retrieves the Experience Cloud ID from Edge Identity extension
     */
    private void onGetEcidClicked() {
        Log.d(TAG, "========================================");
        Log.d(TAG, "Get ECID button clicked");
        Log.d(TAG, "========================================");

        updateStatus("🔍 Fetching ECID...");
        appendLog("Requesting ECID from Identity extension...");

        try {
            Identity.getExperienceCloudId(new AdobeCallbackWithError<String>() {
                @Override
                public void call(String ecid) {
                    runOnUiThread(() -> {
                        Log.d(TAG, "========================================");
                        Log.d(TAG, "ECID retrieved: " + ecid);
                        Log.d(TAG, "========================================");

                        if (ecid != null && !ecid.isEmpty()) {
                            ecidTextView.setText("ECID: " + ecid);
                            updateStatus("✅ ECID Retrieved");
                            appendLog("ECID: " + ecid);
                        } else {
                            ecidTextView.setText("ECID: (empty or null)");
                            updateStatus("⚠️ ECID is empty");
                            appendLog("WARNING: ECID returned empty or null");
                        }
                    });
                }

                @Override
                public void fail(AdobeError adobeError) {
                    runOnUiThread(() -> {
                        String errorMsg = adobeError != null ? 
                            adobeError.getErrorName() : "Unknown error";
                        
                        Log.e(TAG, "========================================");
                        Log.e(TAG, "Failed to get ECID: " + errorMsg);
                        Log.e(TAG, "========================================");

                        ecidTextView.setText("ECID: Error - " + errorMsg);
                        updateStatus("❌ Failed to get ECID");
                        appendLog("ERROR getting ECID: " + errorMsg);
                    });
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "Exception getting ECID: " + e.getMessage());
            e.printStackTrace();
            updateStatus("❌ Error: " + e.getMessage());
            appendLog("EXCEPTION: " + e.getMessage());
        }
    }

    /**
     * Handle "Clear Log" button click
     */
    private void onClearLogClicked() {
        Log.d(TAG, "Clear Log button clicked");
        
        logBuffer = new StringBuilder();
        logTextView.setText("Log cleared.\n");
        ecidTextView.setText("ECID: (not retrieved yet)");
        checkAepStatus(); // Reset to initial status
    }

    /**
     * Update the status TextView
     */
    private void updateStatus(String status) {
        runOnUiThread(() -> {
            statusTextView.setText(status);
        });
    }

    /**
     * Append a message to the log TextView
     */
    private void appendLog(String message) {
        String timestamp = new SimpleDateFormat("HH:mm:ss", Locale.US).format(new Date());
        String logEntry = "[" + timestamp + "] " + message + "\n";
        
        logBuffer.append(logEntry);
        
        runOnUiThread(() -> {
            logTextView.setText(logBuffer.toString());
        });
    }

    /**
     * Get current timestamp in ISO 8601 format for XDM
     */
    private String getCurrentTimestamp() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        return sdf.format(new Date());
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "MainActivity onResume");
        
        // You could trigger Lifecycle start here if needed
        // MobileCore.lifecycleStart(null);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "MainActivity onPause");
        
        // You could trigger Lifecycle pause here if needed
        // MobileCore.lifecyclePause();
    }
}
