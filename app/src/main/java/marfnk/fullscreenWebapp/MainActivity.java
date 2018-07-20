package marfnk.fullscreenWebapp;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.PowerManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebView;

public class MainActivity extends AppCompatActivity {
    protected PendingIntent pendingIntent;
    protected IntentFilter[] intentFiltersArray;
    protected String[][] techListsArray;
    protected NfcAdapter nfcAdapter;
    private static final int UI_ANIMATION_DELAY = 300;
    private final Handler mHideHandler = new Handler();
    private WebView mContentView;
    private WebAppInterface mWebInterface;
    private static final String TAG = "FullscreenWebapp";

    private PowerManager.WakeLock wakelock;
    private final Runnable mHidePart2Runnable = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {
            mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
    };

    private final Runnable mShowPart2Runnable = new Runnable() {
        @Override
        public void run() {
            // Delayed display of UI elements
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.show();
            }
        }
    };

    private final Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            hide();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        setContentView(R.layout.activity_main);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        mContentView = (WebView) findViewById(R.id.webview);

        mWebInterface = new WebAppInterface(this);
        mContentView.loadUrl("https://github.com");  //enter your URL here
        mContentView.addJavascriptInterface(mWebInterface, "Android");
        mContentView.getSettings().setJavaScriptEnabled(true);
        mContentView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);

        initializeNfcReader();
    }

    @Override
    public void onBackPressed() {
        // blocks the back button
    }

    private void initializeNfcReader() {
        this.pendingIntent = PendingIntent.getActivity(
                this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        this.intentFiltersArray = new IntentFilter[]{new IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED)};
        this.techListsArray = new String[][]{
                new String[]{android.nfc.tech.NfcA.class.getName()},
                new String[]{android.nfc.tech.IsoDep.class.getName()},
                new String[]{android.nfc.tech.MifareClassic.class.getName()},
                new String[]{android.nfc.tech.NfcV.class.getName()}
        };
        this.nfcAdapter = NfcAdapter.getDefaultAdapter(this);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        delayedHide(100);
    }


    private void hide() {
        // Hide UI first
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        // Schedule a runnable to remove the status and navigation bar after a delay
        mHideHandler.removeCallbacks(mShowPart2Runnable);
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);
    }

    @Override
    public void onPause() {
        super.onPause();
        nfcAdapter.disableForegroundDispatch(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        nfcAdapter.enableForegroundDispatch(this, pendingIntent, intentFiltersArray, techListsArray);
        hide();
        Log.i(TAG, "resume");
    }

    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }

    @Override
    public void onNewIntent(Intent intent) {
        Tag currentTag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        cardScanned(currentTag);
    }


    private void cardScanned(Tag tag) {
        StringBuilder sb = new StringBuilder();
        for (byte b : tag.getId()) {
            sb.append("0x");
            sb.append(String.format("%02X", b).toLowerCase());
        }
        Log.i("Got Card", sb.toString());

        mContentView.evaluateJavascript("if (onRfidReceived) { onRfidReceived('" + sb.toString() + "') } ", null);
    }

}
