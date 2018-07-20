# Android Fullscreen Web App
This app renders a programmatically defined website in a WebView in Fullscreen mode.

Perfect for IoT projects or home or office automation.

## Setup
* Clone the repo
* open with Android Studio
* Set your URL at `marfnk/fullscreenWebapp/MainActivity.java`
* compile and run

## Features
* automatically goes to fullscreen mode
* JavaScript interface
  * set the device brightness
  * receive RFID tags

## Fullscreen
The user can always exit the fullscreen mode by swiping up or pressing the home button.
You can prevent this by going into the pinned mode (see Android docs).
The back button is disabled by the app.

## JavaScript Interface

The JavaScript interface lets you change Android system settings from your web app.
It is available under `window.Android`.

### Set the screen brightness

    const brightness = 0.1; //number between 0 (dark) and 1 (bright)
    if (window['Android']) {
      window['Android'].setScreenBrightness(brightness);
    }

### Receive RFID tags (NFC devices only)
Please be sure to enable NFC on your device. Then, register a function like this:

    window.onRfidReceived = ((rfid: string) => {
      console.log('RFID TAG RECEIVED: ' + rfid);
    });

Use the app NFCTools from the App Store to debug your RFID cards.