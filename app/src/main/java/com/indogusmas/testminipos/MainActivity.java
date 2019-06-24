package com.indogusmas.testminipos;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.mocoo.hang.rtprinter.driver.Contants;
import com.mocoo.hang.rtprinter.driver.HsBluetoothPrintDriver;

import java.lang.ref.WeakReference;

public class MainActivity extends AppCompatActivity {


    private static final int REQUEST_CONNECT_DEVICE = 1;
    private static final int REQUEST_ENABLE_BT = 2;
    private BluetoothAdapter mBluetoothAdapter;
    private static BluetoothDevice device;
    public static HsBluetoothPrintDriver BLUETOOTH_PRINTER=null;
    private android.app.AlertDialog.Builder alertDlgBuilder = null;
    private  static Context CONTEXT;
    private Button btnPrint;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnPrint = (Button)findViewById(R.id.btn_print);

        CONTEXT = getApplicationContext();
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        alertDlgBuilder = new android.app.AlertDialog.Builder(MainActivity.this);
        btnPrint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                connecBluetooth();
                PrintReceipt.printBillFromOrder(MainActivity.CONTEXT);

            }
        });
        if(mBluetoothAdapter == null){
            Toast.makeText(MainActivity.this, "Blouetooh is not Available", Toast.LENGTH_LONG).show();
            finish();
            return;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch (requestCode) {
            case REQUEST_CONNECT_DEVICE:
                // When DeviceListActivity returns with a device to connect
                if (resultCode == Activity.RESULT_OK) {
                    // Get the device MAC address
                    String address = data.getExtras()
                            .getString(DeviceListAcitivity.EXTRA_DEVICE_ADDRESS);
                    // Get the BLuetoothDevice object
                    device = mBluetoothAdapter.getRemoteDevice(address);
                    // Attempt to connect to the device
                    BLUETOOTH_PRINTER.start();
                    BLUETOOTH_PRINTER.connect(device);
                }
                break;
            case REQUEST_ENABLE_BT:
                // When the request to enable Bluetooth returns
                if (resultCode == Activity.RESULT_OK) {
                    // Bluetooth is now enabled, so set up a chat session
                    initializeBluetoothDevice();
                } else {
                    // User did not enable Bluetooth or an error occured
                    finish();
                }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private class BluetoothHandler extends Handler {

        private final WeakReference<MainActivity> myWeakReference;

        public BluetoothHandler(MainActivity mainActivity) {
            myWeakReference=new WeakReference<MainActivity>(mainActivity);
        }

        @Override
        public void handleMessage(Message msg) {
            MainActivity mainActivity=myWeakReference.get();
            if(mainActivity!=null){
                super.handleMessage(msg);
                Bundle data=msg.getData();
                switch (data.getInt("flag")) {
                    case Contants.FLAG_STATE_CHANGE:
                        int state=data.getInt("state");
                        switch (state) {
                            case HsBluetoothPrintDriver.CONNECTED_BY_BLUETOOTH:
                                Toast.makeText(MainActivity.this, "terhubung", Toast.LENGTH_SHORT).show();
                                break;
                            case HsBluetoothPrintDriver.FLAG_SUCCESS_CONNECT:
                                Toast.makeText(MainActivity.this, "terhubung", Toast.LENGTH_SHORT).show();
                                break;
                            case HsBluetoothPrintDriver.UNCONNECTED:
                                Toast.makeText(MainActivity.this, "Tidak terhubung", Toast.LENGTH_SHORT).show();
                                break;
                        }
                        break;
                    case Contants.FLAG_SUCCESS_CONNECT:
                        Toast.makeText(MainActivity.this, "Connection...", Toast.LENGTH_SHORT).show();
                        break;
                    case Contants.FLAG_FAIL_CONNECT:
                        Toast.makeText(MainActivity.this, "failed", Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        break;

                }
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(!mBluetoothAdapter.isEnabled()){
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
        }else {
            if(BLUETOOTH_PRINTER == null){
                initializeBluetoothDevice();
            }else {
                if(BLUETOOTH_PRINTER.IsNoConnection()){
                    Toast.makeText(MainActivity.this, "no connection", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(MainActivity.this, "connection", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void initializeBluetoothDevice() {
        // Initialize HsBluetoothPrintDriver class to perform bluetooth connections
        BLUETOOTH_PRINTER = HsBluetoothPrintDriver.getInstance();//
        BLUETOOTH_PRINTER.setHandler(new BluetoothHandler(MainActivity.this));
    }



    private  void connecBluetooth(){
        final Intent[] serverIntent={null};
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
        }else{//If the connection is lost with last connected bluetooth printer
            if(BLUETOOTH_PRINTER.IsNoConnection()){
                serverIntent[0]= new Intent(MainActivity.this, DeviceListAcitivity.class);
                startActivityForResult(serverIntent[0], REQUEST_CONNECT_DEVICE);
            }else{ //If an existing connection is still alive then ask user to kill it and re-connect again
                alertDlgBuilder.setTitle(getResources().getString(R.string.alert_title));
                alertDlgBuilder.setMessage(getResources().getString(R.string.alert_message));
                alertDlgBuilder.setNegativeButton(getResources().getString(R.string.alert_btn_negative), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        }
                );
                alertDlgBuilder.setPositiveButton(getResources().getString(R.string.alert_btn_positive), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                BLUETOOTH_PRINTER.stop();
                                serverIntent[0]= new Intent(MainActivity.this, DeviceListAcitivity.class);
                                startActivityForResult(serverIntent[0], REQUEST_CONNECT_DEVICE);
                            }
                        }
                );
                alertDlgBuilder.show();

            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(BLUETOOTH_PRINTER.IsNoConnection()){
            BLUETOOTH_PRINTER.stop();
        }
    }
}
