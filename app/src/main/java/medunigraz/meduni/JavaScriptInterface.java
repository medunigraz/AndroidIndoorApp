package medunigraz.meduni;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.webkit.JavascriptInterface;


public class JavaScriptInterface {
    Context mContext;
    private KontaktBTScanner InternalScanner;
    public JavaScriptInterface(Context c, KontaktBTScanner BTSCANNER) {
        this.InternalScanner = BTSCANNER;
        this.mContext = c;
    }
    @JavascriptInterface
    public int checkdevice() {
        Log.i("DEBUG", Integer.toString(InternalScanner.CheckAdapter()));
        return InternalScanner.CheckAdapter();
    }
    @JavascriptInterface
    public void OpenURL(String URL)
    {
            mContext.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(URL)));
    }
    @JavascriptInterface
    public void startscan() {
        InternalScanner.StartScan();
    }

    @JavascriptInterface
    public void stopscan() {
        InternalScanner.StopScan();
    }
}
