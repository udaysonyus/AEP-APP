package com.example.aepvalidation;

import android.app.Application;
import android.util.Log;

import com.adobe.marketing.mobile.Edge;
import com.adobe.marketing.mobile.Identity;
import com.adobe.marketing.mobile.Lifecycle;
import com.adobe.marketing.mobile.LoggingMode;
import com.adobe.marketing.mobile.MobileCore;
import com.adobe.marketing.mobile.Signal;
import com.adobe.marketing.mobile.UserProfile;

import java.util.Arrays;

/**
 * MainApplication - Custom Application class for AEP SDK initialization
 * 
 * This is where you initialize the Adobe Experience Platform Mobile SDK.
 * The Environment ID from Adobe Data Collection is configured below.
 */
public class MainApplication extends Application {

    private static final String TAG = "AEPValidationApp";

    // =========================================================
    // YOUR AEP ENVIRONMENT ID (from Adobe Data Collection)
    // =========================================================
    private static final String ENVIRONMENT_ID = "YOUR_ENV_ID_HERE";

    // Flag to track initialization status
    private static boolean isAepInitialized = false;
    private static String initializationError = null;

    @Override
    public void onCreate() {
        super.onCreate();

        Log.d(TAG, "========================================");
        Log.d(TAG, "AEP init started");
        Log.d(TAG, "Environment ID: " + ENVIRONMENT_ID);
        Log.d(TAG, "========================================");

        // CRITICAL: Set the application context FIRST before any other MobileCore calls
        MobileCore.setApplication(this);
        Log.d(TAG, "MobileCore.setApplication() called");

        // Set the log level to DEBUG as specified in your Adobe code
        MobileCore.setLogLevel(LoggingMode.DEBUG);
        Log.d(TAG, "MobileCore log level set to DEBUG");

        // =========================================================
        // REGISTER EXTENSIONS AND INITIALIZE
        // =========================================================
        try {
            Log.d(TAG, "Registering Adobe extensions...");
            Log.d(TAG, "Extensions: Edge, EdgeIdentity, Identity, Lifecycle, Signal, UserProfile");

            // Register all required extensions
            MobileCore.registerExtensions(
                Arrays.asList(
                    Edge.EXTENSION,
                    com.adobe.marketing.mobile.edge.identity.Identity.EXTENSION,  // Edge Identity
                    Identity.EXTENSION,  // Legacy Identity
                    Lifecycle.EXTENSION,
                    Signal.EXTENSION,
                    UserProfile.EXTENSION
                ),
                o -> {
                    // This callback is called when all extensions are registered
                    Log.d(TAG, "========================================");
                    Log.d(TAG, "AEP Extensions registered successfully");
                    Log.d(TAG, "Configuring with Environment ID...");
                    Log.d(TAG, "========================================");

                    // Configure with your Environment ID
                    MobileCore.configureWithAppID(ENVIRONMENT_ID);

                    isAepInitialized = true;
                    Log.d(TAG, "========================================");
                    Log.d(TAG, "AEP init completed successfully!");
                    Log.d(TAG, "SDK is ready to send events");
                    Log.d(TAG, "========================================");
                }
            );

        } catch (Exception e) {
            initializationError = e.getMessage();
            Log.e(TAG, "========================================");
            Log.e(TAG, "AEP init FAILED with error: " + e.getMessage());
            Log.e(TAG, "========================================");
            e.printStackTrace();
        }
    }

    /**
     * Check if AEP SDK is initialized successfully
     */
    public static boolean isAepInitialized() {
        return isAepInitialized;
    }

    /**
     * Get initialization error message if any
     */
    public static String getInitializationError() {
        return initializationError;
    }

    /**
     * Get the configured Environment ID
     */
    public static String getEnvironmentId() {
        return ENVIRONMENT_ID;
    }
}
