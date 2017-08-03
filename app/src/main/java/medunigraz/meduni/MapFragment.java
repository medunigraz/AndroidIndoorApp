package medunigraz.meduni;
import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;


public class MapFragment extends Fragment {
    boolean PositionPermission = false;
    public KontaktBTScanner BeaconScanner;
    String Url="https://map.medunigraz.at/";
    WebView wv;
    JavaScriptInterface JSInterface;

    public MapFragment() {
    }
    public static MapFragment newInstance() {
        MapFragment fragment = new MapFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_map, container, false);
        super.onCreate(savedInstanceState);
        BeaconScanner = new KontaktBTScanner(getContext());
        BeaconScanner.RegisterBeaconFoundListener(new KontaktBTScanner.OnScanResult() {
            @Override
            public void onResultConverted(String JSONString) {

                Log.i("DEBUG","GEFUNDEN");
                wv.loadUrl("javascript:updatesignals('"+JSONString+"');");
            }
        });
        if(Build.VERSION.SDK_INT < 24){ BeaconScanner.SetTimerInterval(400);}
        wv = (WebView) view.findViewById(R.id.WebView);
        wv.getSettings().setJavaScriptEnabled(true);
        wv.getSettings().setDomStorageEnabled(true);
        wv.getSettings().setLoadWithOverviewMode(true);
        wv.getSettings().setUseWideViewPort(true);
        wv.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
        wv.setWebViewClient(new WebViewClient());
        JSInterface = new JavaScriptInterface(BeaconScanner);
        wv.addJavascriptInterface(JSInterface, "JSInterface");
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            requestPermissions(
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    128);
        }else {PositionPermission = true;}

        if(PositionPermission) {
            BeaconScanner.SetNoPermission(false);
        }else{
            BeaconScanner.SetNoPermission(true);
        }

        wv.loadUrl(Url);
        return view;
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        Log.i("DEBUG", Integer.toString(requestCode));
        switch (requestCode) {
            case 128: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.i("DEBUG", "GRANTED");
                    PositionPermission = true;
                    BeaconScanner.SetNoPermission(false);

                } else {
                    Log.i("DEBUG", "NOT GRANTED");
                    BeaconScanner.SetNoPermission(true);

                }
            }
        }
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if(BeaconScanner.KontaktIsScanning) {
            BeaconScanner.StopScan();
        }
        BeaconScanner.UnregisterBTStatusReceiver();

    }

}
