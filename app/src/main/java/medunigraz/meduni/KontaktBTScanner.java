package medunigraz.meduni;


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
import android.util.Log;
import android.widget.Toast;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class KontaktBTScanner {
    private static Context context;
    private Timer BTRestartTimer;
    public boolean KontaktIsScanning = false;
    private ScanSettings mScanSettings;
    private boolean BTLeNotEnabled = true;
    private BluetoothLeScanner mBluetoothLeScanner;
    private BluetoothAdapter BTAdapter;
    private boolean NoPermission = false;
    private int interval = 10000;
    private OnScanResult OnScanResultCallback;
    public interface OnScanResult{
        public void onResultConverted(String JSONString);
    }
    public void RegisterBeaconFoundListener(OnScanResult myInterface) {
        this.OnScanResultCallback = myInterface;
    }
    public void SetTimerInterval(int Interval)
    {
        this.interval = Interval;
    }
    public void SetNoPermission(boolean Permission)
    {
        this.NoPermission= Permission;
    }



    public KontaktBTScanner(Context c)
    {
        Log.i("DEBUG","KONSTRUKTOR AUFGERUFEN");
        context = c;
        BTAdapter = BluetoothAdapter.getDefaultAdapter();
        if(!BTAdapter.isEnabled())
        {
            Toast.makeText(context, "Bluetooth einschalten!",
                    Toast.LENGTH_LONG).show();
        }
        ScanSettings.Builder mBuilder = new ScanSettings.Builder();
        mBuilder.setReportDelay(0);
        mBuilder.setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY);
        mScanSettings = mBuilder.build();
        RegisterBTStatusReceiver();
        mBluetoothLeScanner = BTAdapter.getBluetoothLeScanner();
    }

    public void RegisterBTStatusReceiver(){
        IntentFilter filter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        context.getApplicationContext().registerReceiver(BTStatusReceiver, filter);
    }
    public void UnregisterBTStatusReceiver(){
        context.getApplicationContext().unregisterReceiver(BTStatusReceiver);
    }

    private void EnableLEScanner(){
        Log.i("DEBUG","ENABLE BTLE");
        mBluetoothLeScanner = BTAdapter.getBluetoothLeScanner();
        BTLeNotEnabled = false;
    }
    public int CheckAdapter()
    {
        if (BTAdapter.getState() == BluetoothAdapter.STATE_ON && !NoPermission && mBluetoothLeScanner != null) {
           return 0;
        } else if (BTAdapter.getState() == BluetoothAdapter.STATE_OFF) {
            return 2;
        }else if (mBluetoothLeScanner == null){return 1;}
        else{
            return 4;
        }
    }
    public void StartScan()
    {
        BTRestartTimer = new Timer();
        BTRestartTimer.schedule(new TimerTask()
        {
            @Override
            public void run() {
                if(BTAdapter !=null && mBluetoothLeScanner !=null) {
                    if (BTAdapter.getState() == BluetoothAdapter.STATE_ON ) {
                        try {
                            mBluetoothLeScanner.stopScan(mScanCallback);
                            mBluetoothLeScanner.startScan(null, mScanSettings, mScanCallback);
                            KontaktIsScanning = true;
                        }catch (Exception e)
                        {
                            Log.i("DEBUG",e.getMessage());
                        }
                    }
                }
        }},interval,interval);
        Log.i("DEBUG","AUFGERUFEN");

    }
    public void StopScan()
    {
        if(KontaktIsScanning) {
            mBluetoothLeScanner.stopScan(mScanCallback);
        }
        if(BTRestartTimer !=null) {
            BTRestartTimer.cancel();
            BTRestartTimer = null;
        }
    }
    protected ScanCallback mScanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            ScanRecord mScanRecord = result.getScanRecord();
            BluetoothDevice device = result.getDevice();
            List<String> MACS=new ArrayList<>();
            List<String> Name=new ArrayList<>();
            List<Integer> Signal = new ArrayList<>();
            List<Integer> Batterie = new ArrayList<>();


            try {
                byte[] UniqueBeaconName;
                byte[] Kontakt;
                byte BatterieVal = Array.getByte(mScanRecord.getBytes(),52);
                UniqueBeaconName = Arrays.copyOfRange(mScanRecord.getBytes(),46,50);
                Kontakt = Arrays.copyOfRange(mScanRecord.getBytes(),32,39);
                String CheckBeaconManufacturer = new String(Kontakt, "UTF-8");
                if ( !CheckBeaconManufacturer.equals("Kontakt") || result.getRssi() > 0){
                    Log.i("DEBUG","Unknown BTLE Device");
                    return;
                }
                String UniqueBeaconNameStr = new String(UniqueBeaconName, "UTF-8");
                MACS.add(device.getAddress());
                Signal.add(result.getRssi());
                Name.add(UniqueBeaconNameStr);
                Batterie.add((int)BatterieVal);


            }catch (Exception e){
                Log.i("DEBUG",e.getMessage());
            }
            CreateJson("BT",MACS,Signal,Name,Batterie);
            Log.i("DEBUG", "BLEAUFGERUFEN");
        }
        private void CreateJson(String Type, List<String> MACS,List<Integer> Signal, List<String> Name,List<Integer> Batterie ){
            String JsonOut="[";
            for (int i = 0; i < MACS.size(); i++) {
                JsonOut = JsonOut + String.format(Locale.getDefault(),"{\"Type\":\"%s\",\"ID\":\"%s\",\"Value\":%d,\"Name\":\"%s\",\"Batterie\":%d},",Type,MACS.get(i),Signal.get(i),Name.get(i),Batterie.get(i));
            }
            StringBuilder sb = new StringBuilder(JsonOut);
            sb.setLength(Math.max(sb.length() - 1, 0));
            JsonOut = sb.toString();
            JsonOut = JsonOut +"]";
            Log.i("DEBUG","FERTIGER JSON STRING: " + JsonOut);
            OnScanResultCallback.onResultConverted(JsonOut);
        }
    };
    private final BroadcastReceiver BTStatusReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE,
                        BluetoothAdapter.ERROR);
                switch (state) {
                    case BluetoothAdapter.STATE_OFF:
                        break;
                    case BluetoothAdapter.STATE_TURNING_OFF:
                        if(BTRestartTimer !=null) {
                            BTRestartTimer.cancel();
                            BTRestartTimer = null;
                        }
                        BTLeNotEnabled = true;
                        break;
                    case BluetoothAdapter.STATE_ON:
                        EnableLEScanner();
                        break;
                    case BluetoothAdapter.STATE_TURNING_ON:
                        break;
                }
            }
        }
    };

}
