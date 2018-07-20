package marfnk.fullscreenWebapp;

import android.app.Activity;
import android.view.WindowManager;
import android.webkit.JavascriptInterface;
import android.widget.Toast;

public class WebAppInterface {
    private Activity mActivity;

    WebAppInterface(Activity a) {
        mActivity = a;
    }

    @JavascriptInterface
    public void showToast(String toast) {
        Toast.makeText(mActivity, toast, Toast.LENGTH_SHORT).show();
    }

    @JavascriptInterface
    public void setScreenBrightness(final float brightness) {
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                WindowManager.LayoutParams lp = mActivity.getWindow().getAttributes();
                lp.screenBrightness = brightness;
                mActivity.getWindow().setAttributes(lp);
            }
        });

    }

    @JavascriptInterface
    public float getScreenBrightness() {
        WindowManager.LayoutParams lp = mActivity.getWindow().getAttributes();
        return lp.screenBrightness;
    }

}
