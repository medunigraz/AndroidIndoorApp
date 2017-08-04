package medunigraz.meduni;

import android.content.Context;
import android.util.Log;
import android.webkit.JavascriptInterface;


public class JavaScriptInterface extends MapFragment {
    Context mContext;
    private KontaktBTScanner InternalScanner;
    private String url;

    public JavaScriptInterface(KontaktBTScanner BTSCANNER) {
        this.InternalScanner = BTSCANNER;
    }

    JavaScriptInterface(Context c) {

        mContext = c;

    }

    @JavascriptInterface
    public int checkdevice() {
        Log.i("DEBUG", Integer.toString(InternalScanner.CheckAdapter()));
        return InternalScanner.CheckAdapter();


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
