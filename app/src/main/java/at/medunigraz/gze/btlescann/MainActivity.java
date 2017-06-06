package at.medunigraz.gze.btlescann;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanRecord;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {



    JavaScriptInterface JSInterface;
    WebView wv;
    WifiManager mWifiManager;
    BluetoothAdapter BTAdapter;
    ScanSettings mScanSettings;
    String TAG = "DEBUGSCHEISSE";
    BluetoothLeScanner mBluetoothLeScanner;
    Timer mTimer = new Timer();
    HttpURLConnection PostConn;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BTAdapter = BluetoothAdapter.getDefaultAdapter();
        mWifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        wv = (WebView) findViewById(R.id.WebView);
        wv.getSettings().setJavaScriptEnabled(true);
        wv.getSettings().setDomStorageEnabled(true);
        wv.getSettings().setLoadWithOverviewMode(true);
        wv.getSettings().setUseWideViewPort(true);
        wv.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
        wv.setWebViewClient(new WebViewClient());




        JSInterface = new JavaScriptInterface(this, wv);
        wv.addJavascriptInterface(JSInterface, "JSInterface");
        wv.loadUrl("https://api.medunigraz.at/postest/");
        BTAdapter.enable();
        Log.i(TAG, "GLADN");
        mBluetoothLeScanner = BTAdapter.getBluetoothLeScanner();
        ScanSettings.Builder mBuilder = new ScanSettings.Builder();
        mBuilder.setReportDelay(0);
        mBuilder.setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY);
        mScanSettings = mBuilder.build();
        mWifiManager.setWifiEnabled(true);
        if (mWifiManager.getWifiState() == WifiManager.WIFI_STATE_ENABLED) {
            IntentFilter filter = new IntentFilter();
            filter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
            registerReceiver(new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    List<String> WMACS=new ArrayList<String>();
                    List<Integer> WSignal = new ArrayList<Integer>();
                    List<android.net.wifi.ScanResult> results = mWifiManager.getScanResults();
                    final int N = results.size();
                    for (int i = 0; i < N; ++i) {
                       /* Log.i(TAG, "  BSSID       =" + results.get(i).BSSID);
                        Log.i(TAG, "  SSID        =" + results.get(i).SSID);
                        Log.i(TAG, "  Capabilities=" + results.get(i).capabilities);
                        Log.i(TAG, "  Frequency   =" + results.get(i).frequency);
                        Log.i(TAG, "  Level       =" + results.get(i).level);
                        Log.i(TAG, "---------------");*/
                        WMACS.add(results.get(i).BSSID);
                        WSignal.add(results.get(i).level);
                    }
                    JSInterface.senddevice(CreateJson("WLAN",WMACS,WSignal));
                    WMACS.clear();
                    WSignal.clear();

                    mWifiManager.startScan();
                }
            }, filter);

        }
    }
    protected ScanCallback mScanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            List<String> BTMACS=new ArrayList<String>();
            List<Integer> BTSignal = new ArrayList<Integer>();
            ScanRecord mScanRecord = result.getScanRecord();
            BluetoothDevice device = result.getDevice();
            Log.i(TAG,device.getAddress());
            Log.i(TAG,device.getName());
            BTMACS.add(device.getAddress());
            BTSignal.add(result.getRssi());
            JSInterface.senddevice(CreateJson("BT",BTMACS,BTSignal));
            BTMACS.clear();
            BTSignal.clear();
            Log.i(TAG, "BLEAUFGERUFEN");
        }
    };
    public class JavaScriptInterface {
        Context mContext;
        private Activity activity;
        private WebView mywv;
        private String url;

        public JavaScriptInterface(Activity act, WebView wvInt) {
            this.activity = act;
            this.mywv = wvInt;
        }

        JavaScriptInterface(Context c) {
            mContext = c;

        }

        @JavascriptInterface
        public void senddevice(String JsonOut) {

            url = "javascript:addtableentry('"+JsonOut+"')";
            mywv.post(new Runnable() {
                @Override
                public void run() {
                    mywv.loadUrl(url);
                }
            });


        }

        @JavascriptInterface
        public void startscan()
        {
            mBluetoothLeScanner.startScan(null, mScanSettings, mScanCallback);
            mWifiManager.startScan();
            mTimer.schedule(new TimerTask() {
                @Override
                public void run() {

                    mBluetoothLeScanner.stopScan(mScanCallback);
                    mBluetoothLeScanner.startScan(null, mScanSettings, mScanCallback);
                }
            }, 400, 400);
            mTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    DownloadWebPageTask task = new DownloadWebPageTask();
                    task.execute();
                }
            }, 1000, 1000);
            Log.i(TAG, "AUFGERUFEN");
        }

        @JavascriptInterface
        public void StopLocate() {
           mBluetoothLeScanner.stopScan(mScanCallback);
        }
    }
    private class DownloadWebPageTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            try {
                URL url = new URL("http://httpbin.org/post");
                URLConnection conn = url.openConnection();
                conn.setReadTimeout(1500);
                conn.setConnectTimeout(1500);
                conn.setDoOutput(true);
                OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());

                wr.write("TEST");
                wr.flush();

                BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String line;
                while ((line = rd.readLine()) != null) {
                    Log.i(TAG, line);
                }
                wr.close();
                rd.close();
            } catch (Exception e) {
                Log.i(TAG, e.toString());
            }
            return "Fertig";
        }
    @Override
    protected void onPostExecute(String result) {
        Log.i(TAG,"ASYNCFERTIG");
    }
}
    public String CreateJson(String Type, List<String> MACS,List<Integer> Signal ){
        String JsonOut="[";
        String tmpstring;
        Log.i(TAG,MACS.toString());
        Log.i(TAG,Signal.toString());
        for (int i = 0; i < MACS.size(); i++) {
            tmpstring = String.format("{\"Type\":\"%s\",\"ID\":\"%s\",\"Value\":%d},",Type,MACS.get(i),Signal.get(i));
            Log.i("CreateJson",tmpstring);
            JsonOut = JsonOut + tmpstring;
        }
        StringBuilder sb = new StringBuilder(JsonOut);
        sb.setLength(Math.max(sb.length() - 1, 0));
        JsonOut = sb.toString();
        JsonOut = JsonOut +"]";
        return JsonOut;
    }

}
