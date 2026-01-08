# AEP Validation App

A minimal Android app for validating Adobe Experience Platform (AEP) Mobile SDK data collection using Android Studio and Logcat via wireless debugging.

## Overview

This app allows you to:
- ✅ Initialize the AEP Mobile SDK with your Environment ID
- ✅ Send Edge events with XDM data
- ✅ Send Edge events with custom product data
- ✅ Retrieve and display your Experience Cloud ID (ECID)
- ✅ View verbose logs in the app UI and Logcat

## Prerequisites

1. **Android Studio** (latest stable version recommended, e.g., Hedgehog or newer)
2. **Android device** running Android 8.0+ (API 26+), preferably Android 11+
3. **AEP Mobile Property** configured in Adobe Data Collection UI with:
   - Edge extension
   - Edge Identity extension
   - Configured datastream
4. **Environment ID** from your mobile property

## Where to Paste Your Embed/Init Code

### Step 1: Get your Environment ID

1. Go to [Adobe Data Collection](https://experience.adobe.com/#/data-collection/)
2. Navigate to **Tags** → Select your mobile property
3. Go to **Environments**
4. Click on the environment you want to use (Development/Staging/Production)
5. Copy the **Environment File ID** (looks like: `your-org/your-property/environment-id`)

### Step 2: Paste the Environment ID in the app

Open the file:
```
app/src/main/java/com/example/aepvalidation/MainApplication.java
```

Find this line (around line 28):
```java
private static final String ENVIRONMENT_ID = "YOUR_ENV_ID_HERE";
```

Replace `YOUR_ENV_ID_HERE` with your actual Environment ID:
```java
private static final String ENVIRONMENT_ID = "your-actual-environment-id";
```

> ⚠️ **Important**: Do NOT include the full embed code or script tags. Only paste the Environment ID string.

## Run on Device Using Wireless Debugging (ADB over Wi-Fi)

### Enable Wireless Debugging on Your Android Device

1. **Enable Developer Options**:
   - Go to **Settings** → **About phone**
   - Tap **Build number** 7 times
   - Go back, you'll see **Developer options**

2. **Enable Wireless Debugging**:
   - Go to **Settings** → **Developer options**
   - Enable **Wireless debugging**
   - Tap on **Wireless debugging** to open settings

3. **Pair with Android Studio**:
   - **Method 1 - QR Code** (Android 11+):
     - In Android Studio, go to **View** → **Tool Windows** → **Device Manager**
     - Click **Pair using Wi-Fi**
     - On your phone, tap **Pair device with QR code** and scan
   
   - **Method 2 - Pairing Code**:
     - On your phone, tap **Pair device with pairing code**
     - In terminal, run: `adb pair <ip>:<port>` and enter the code
     - Then: `adb connect <ip>:<port>` (use the port shown under "IP address & port")

### Run the App

1. Open the project in Android Studio
2. Wait for Gradle sync to complete
3. Select your device from the device dropdown
4. Click **Run** (▶️) or press `Shift+F10`

## How to Verify in Logcat

### Open Logcat

1. In Android Studio: **View** → **Tool Windows** → **Logcat**
2. Or use the keyboard shortcut: `Alt+6` (Windows/Linux) / `Cmd+6` (Mac)

### Filter Logs

Use these filters to see relevant logs:

| Filter | Description |
|--------|-------------|
| `tag:AEPValidationApp` | App-specific logs (our custom logging) |
| `tag:AdobeExperienceSDK` | All Adobe SDK internal logs |
| `package:com.example.aepvalidation` | All logs from this app process |

**Recommended combined filter:**
```
package:com.example.aepvalidation (tag:AEPValidationApp | tag:AdobeExperienceSDK)
```

### What to Look For

#### ✅ Successful Initialization
```
D/AEPValidationApp: ========================================
D/AEPValidationApp: AEP init started
D/AEPValidationApp: Environment ID: your-environment-id
D/AEPValidationApp: ========================================
D/AEPValidationApp: Registering Adobe extensions...
D/AEPValidationApp: AEP Extensions registered successfully
D/AEPValidationApp: Configuring with Environment ID...
D/AEPValidationApp: AEP init completed successfully!
```

#### ✅ Successful Edge Event
```
D/AEPValidationApp: Send Edge Event button clicked
D/AEPValidationApp: XDM Data: {eventType=mobile.validation, timestamp=2024-...}
D/AEPValidationApp: Edge event sent SUCCESS
D/AEPValidationApp: Handles received: 1
```

#### ✅ Successful ECID Retrieval
```
D/AEPValidationApp: Get ECID button clicked
D/AEPValidationApp: ECID retrieved: 12345678901234567890123456789012345678
```

## What to Click in the App

| Button | Action |
|--------|--------|
| **📤 Send Edge Event** | Sends a basic XDM event with `eventType: mobile.validation` |
| **📦 Send Edge Event (with Product Data)** | Sends a commerce event with product data and custom user/app info |
| **🔍 Get ECID** | Retrieves your Experience Cloud ID and displays it |
| **🗑️ Clear Log / Reset UI** | Clears the on-screen log and resets status |

## Optional: How to Verify in Adobe Assurance

Adobe Assurance provides a real-time visual debugging tool for your AEP Mobile SDK implementation.

### Enable Assurance Extension

1. Uncomment in `app/build.gradle`:
   ```groovy
   implementation 'com.adobe.marketing.mobile:assurance'
   ```

2. Uncomment in `MainApplication.java`:
   ```java
   , com.adobe.marketing.mobile.Assurance.EXTENSION
   ```

3. Rebuild the app

### Start an Assurance Session

1. Go to [Adobe Experience Platform](https://experience.adobe.com/)
2. Navigate to **Assurance** → **Create Session**
3. Select your mobile property
4. Choose **QR Code** or **Link** as the connection method
5. Scan the QR code or open the link on your device
6. The app will connect to Assurance

### Verify Events

In the Assurance UI, you'll see:
- **Events Timeline**: All SDK events in real-time
- **Event Details**: Full payload of each event
- **Configuration**: Your SDK configuration
- **Extensions**: Registered extensions and versions

## Project Structure

```
AEPValidation/
├── app/
│   ├── build.gradle              # App dependencies (Adobe SDK here)
│   ├── src/main/
│   │   ├── AndroidManifest.xml   # App manifest
│   │   ├── java/com/example/aepvalidation/
│   │   │   ├── MainApplication.java  # ⭐ AEP SDK init (paste ID here)
│   │   │   └── MainActivity.java     # UI and event handlers
│   │   └── res/
│   │       ├── layout/activity_main.xml
│   │       └── values/...
├── build.gradle                  # Project-level config
├── settings.gradle               # Repository configuration
└── README.md                     # This file
```

## Troubleshooting

### "ECID is empty"
- Ensure your Environment ID is correct
- Check that your datastream is properly configured
- Look for errors in Logcat with tag `AdobeExperienceSDK`

### "Edge event sent but no handles"
- This is normal if your datastream doesn't have response destinations
- Check Adobe Assurance for full event details

### "Network errors"
- Ensure your device has internet connectivity
- Check that `INTERNET` permission is in the manifest

### "SDK not initializing"
- Verify the Environment ID format
- Check Logcat for `AEP init FAILED` messages
- Ensure all extensions are properly registered

## Optional Analytics Extension

If you need Adobe Analytics tracking (`trackAction`/`trackState`), uncomment:

1. In `app/build.gradle`:
   ```groovy
   implementation 'com.adobe.marketing.mobile:analytics'
   ```

2. In `MainApplication.java`:
   ```java
   , com.adobe.marketing.mobile.Analytics.EXTENSION
   ```

3. Add tracking calls in `MainActivity.java`:
   ```java
   import com.adobe.marketing.mobile.Analytics;
   
   // Track action
   Map<String, String> contextData = new HashMap<>();
   contextData.put("key", "value");
   Analytics.trackAction("action_name", contextData);
   
   // Track state
   Analytics.trackState("screen_name", contextData);
   ```

---

**Happy Testing!** 🚀

For more information, see the [Adobe Experience Platform Mobile SDK Documentation](https://developer.adobe.com/client-sdks/documentation/).
