package bluetoothlocker.iotasol.com.bluetoothlocker;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.coolu.blelibrary.inter.IBLE;
import com.coolu.blelibrary.inter.OnConnectionListener;
import com.coolu.blelibrary.inter.OnDeviceSearchListener;
import com.coolu.blelibrary.inter.OnResultListener;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Set;

public class MainActivity extends Activity {

    ListView listViewPaired;
    ListView listViewDetected;
    ArrayList<String> arrayListpaired;
    Button buttonSearch, buttonOn, buttonDesc, buttonOff;
    ArrayAdapter<String> adapter, detectedAdapter;
    static HandleSeacrh handleSeacrh;
    BluetoothDevice bdDevice;
    BluetoothClass bdClass;
    ArrayList<BluetoothDevice> arrayListPairedBluetoothDevices;
    private ButtonClicked clicked;
    ListItemClickedonPaired listItemClickedonPaired;
    BluetoothAdapter bluetoothAdapter = null;
    ArrayList<BluetoothDevice> arrayListBluetoothDevices = null;
    ListItemClicked listItemClicked;
    IBLE ible = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listViewDetected = (ListView) findViewById(R.id.listViewDetected);
        listViewPaired = (ListView) findViewById(R.id.listViewPaired);
        buttonSearch = (Button) findViewById(R.id.buttonSearch);
        buttonOn = (Button) findViewById(R.id.buttonOn);
        buttonDesc = (Button) findViewById(R.id.buttonDesc);
        buttonOff = (Button) findViewById(R.id.buttonOff);
        arrayListpaired = new ArrayList<String>();
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        clicked = new ButtonClicked();
        handleSeacrh = new HandleSeacrh();
        arrayListPairedBluetoothDevices = new ArrayList<BluetoothDevice>();
        /*
         * the above declaration is just for getting the paired bluetooth devices;
         * this helps in the removing the bond between paired devices.
         */
        listItemClickedonPaired = new ListItemClickedonPaired();
        arrayListBluetoothDevices = new ArrayList<BluetoothDevice>();
        adapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1, arrayListpaired);
        detectedAdapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_single_choice);
        listViewDetected.setAdapter(detectedAdapter);
        listItemClicked = new ListItemClicked();
        detectedAdapter.notifyDataSetChanged();
        listViewPaired.setAdapter(adapter);

        initializeIBL();
    }

    private void initializeIBL(){
        ible = CustomIBLE.init(this);
        ible.setDebug(true);

        if (!ible.isEnable()){
            Log.i("Log", "ible.enableBluetooth: " +ible.isEnable());
            ible.enableBluetooth();
        }else{
            //if(true) return;
            ible.startResultListener(new OnResultListener() {
                @Override
                public void DeviceResult(int i, boolean b, int i1) {
                    Log.i("OnResultListener", "DeviceResult: " +b + " : i "+i);
                }

                @Override
                public void errorResult(int i) {
                    Log.i("OnResultListener", "errorResult: " + " : i "+i);
                }
            });
            ible.startScan(new OnDeviceSearchListener() {
                @Override
                public void onScanDevice(final BluetoothDevice bluetoothDevice, int i, byte[] bytes) {
                    Log.i("device found : "," address found : "+bluetoothDevice.getAddress());
                    if(bluetoothDevice.getAddress().equals("3C:A3:08:C1:74:DA")){
                        Log.i(" hardcode lock : ", " i am inside the hole" + ible.getBattery());
                        ible.stopScan();
                        ible.disconnect();
                        boolean bConnection = ible.connect(bluetoothDevice.getAddress(), new OnConnectionListener() {
                            @Override
                            public void onDisconnect(int i) {
                                Log.i("Log", "ible.onDisconnect: " +i);
                            }

                           /* private boolean writeObject(TxOrder txOrder) {
                                if (ible.getBluetoothAdapter() != null && ible.getBluetoothAdapter().isEnabled()) {
                                    if (ible. != null && this.write_characteristic != null) {
                                        byte[] miwen = EncryptUtils.Encrypt(HexStringUtils.hexString2Bytes(txOrder.generateString()), Config.KEY);
                                        if (miwen != null) {
                                            this.write_characteristic.setValue(miwen);
                                            Logger.e(AndroidBle.class.getSimpleName(), txOrder.generateString());
                                            return ible.getBluetoothAdapter().writeCharacteristic(this.write_characteristic);
                                        } else {
                                            return false;
                                        }
                                    } else {
                                        GlobalParameterUtils.getInstance().getOnResultListener().errorResult(2);
                                        return false;
                                    }
                                } else {
                                    GlobalParameterUtils.getInstance().getOnResultListener().errorResult(1);
                                    return false;
                                }
                            }*/


                            @Override
                            public void onServicesDiscovered(String s, String s1) {
                                Log.i("Log", "ible.onServicesDiscovered: " +s + " : s1 : "+s1 + " : :"+ible.getConnectStatus());
                                ible.refreshDeviceCache();
                                //ible.bi
                                ible.openLock();
                               /* if(ible.openLock()){
                                    Log.i("Log", "ible.closeLock:  is succesfsfuelrj A");
                                }else{
                                    Log.i("Log", "ible.close:  is fafdasfewrrwr A");
                                    *//*if(ible.openLock()){
                                        Log.i("Log", "ible.openLock:  is succesfsfuelrj A");
                                    }else{

                                        Log.i("Log", "ible.opne:  is fafdaadfaewrsfewrrwr A");
                                    }*//*
                                }*/
                            }
                        });
                       // if(bluetoothDevice.createBond()){
                       //     Log.i(" hardcode lock : ", " i am inside the dipper hole");
                       // }
                        Log.i("Log", "ible.bConnection: " +bConnection);
                    }

                }
            });

            //startSearching();
            Log.i("Log", "ible.disabledBluetooth: " +ible.isEnable());
        }
        ible.startResultListener(new OnResultListener() {
            @Override

            public void DeviceResult(int txOrder, boolean flag, int battery) {
                Log.i("Log", "DeviceResult battery: " +battery + " flag: "+flag+ " txOrder: "+txOrder);
            }

            @Override

            public void errorResult(int status) {
                Log.i("Log", "DeviceResult status: " +status);
            }

        });
    }

    @Override
    protected void onStart() {
        // TODO Auto-generated method stub
        super.onStart();
        getPairedDevices();
        buttonOn.setOnClickListener(clicked);
        buttonSearch.setOnClickListener(clicked);
        buttonDesc.setOnClickListener(clicked);
        buttonOff.setOnClickListener(clicked);
        listViewDetected.setOnItemClickListener(listItemClicked);
        listViewPaired.setOnItemClickListener(listItemClickedonPaired);

        hasFileReadWritePermissions();
    }

    private void getPairedDevices() {
        Set<BluetoothDevice> pairedDevice = bluetoothAdapter.getBondedDevices();
        if (pairedDevice.size() > 0) {
            for (BluetoothDevice device : pairedDevice) {
                Log.i("pair device "," paired_device: "+device.getAddress());
                arrayListpaired.add(device.getName() + "\n" + device.getAddress());
                arrayListPairedBluetoothDevices.add(device);
            }
        }
        adapter.notifyDataSetChanged();
    }

    class ListItemClicked implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            // TODO Auto-generated method stub
            bdDevice = arrayListBluetoothDevices.get(position);
            //bdClass = arrayListBluetoothDevices.get(position);
            Log.i("Log", "The dvice : " + bdDevice.toString());
            /*
             * here below we can do pairing without calling the callthread(), we can directly call the
             * connect(). but for the safer side we must usethe threading object.
             */
            //callThread();
            //connect(bdDevice);
            Boolean isBonded = false;
            try {
                isBonded = createBond(bdDevice);
                if (isBonded) {
                    //arrayListpaired.add(bdDevice.getName()+"\n"+bdDevice.getAddress());
                    //adapter.notifyDataSetChanged();
                    getPairedDevices();
                    adapter.notifyDataSetChanged();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }//connect(bdDevice);
            Log.i("Log", "The bond is created: " + isBonded);
        }
    }

    class ListItemClickedonPaired implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            bdDevice = arrayListPairedBluetoothDevices.get(position);
            try {
                Boolean removeBonding = removeBond(bdDevice);
                if (removeBonding) {
                    arrayListpaired.remove(position);
                    adapter.notifyDataSetChanged();
                }


                Log.i("Log", "Removed" + removeBonding);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    /*private void callThread() {
        new Thread(){
            public void run() {
                Boolean isBonded = false;
                try {
                    isBonded = createBond(bdDevice);
                    if(isBonded)
                    {
                        arrayListpaired.add(bdDevice.getName()+"\n"+bdDevice.getAddress());
                        adapter.notifyDataSetChanged();
                    }
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }//connect(bdDevice);
                Log.i("Log", "The bond is created: "+isBonded);
            }
        }.start();
    }*/
    private Boolean connect(BluetoothDevice bdDevice) {
        Boolean bool = false;
        try {
            Log.i("Log", "service method is called ");
            Class cl = Class.forName("android.bluetooth.BluetoothDevice");
            Class[] par = {};
            Method method = cl.getMethod("createBond", par);
            Object[] args = {};
            bool = (Boolean) method.invoke(bdDevice);//, args);// this invoke creates the detected devices paired.
            //Log.i("Log", "This is: "+bool.booleanValue());
            //Log.i("Log", "devicesss: "+bdDevice.getName());
        } catch (Exception e) {
            Log.i("Log", "Inside catch of serviceFromDevice Method");
            e.printStackTrace();
        }
        return bool.booleanValue();
    }

    ;


    public boolean removeBond(BluetoothDevice btDevice)
            throws Exception {
        Class btClass = Class.forName("android.bluetooth.BluetoothDevice");
        Method removeBondMethod = btClass.getMethod("removeBond");
        Boolean returnValue = (Boolean) removeBondMethod.invoke(btDevice);
        return returnValue.booleanValue();
    }


    public boolean createBond(BluetoothDevice btDevice)
            throws Exception {
        Class class1 = Class.forName("android.bluetooth.BluetoothDevice");
        Method createBondMethod = class1.getMethod("createBond");
        Boolean returnValue = (Boolean) createBondMethod.invoke(btDevice);
        return returnValue.booleanValue();
    }


    class ButtonClicked implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.buttonOn:
                    Log.i("Log", "ible.closeLock:  is succesfsfuelrj A");
                    if(ible.closeLock()){
                        Log.i("Log", "ible.closeLock:  is succesfsfuelrj A");
                    }else{
                        Log.i("Log", "ible.close:  is fafdasfewrrwr A");
                                    /*if(ible.openLock()){
                                        Log.i("Log", "ible.openLock:  is succesfsfuelrj A");
                                    }else{

                                        Log.i("Log", "ible.opne:  is fafdaadfaewrsfewrrwr A");
                                    }*/
                    }
                    break;
                case R.id.buttonSearch:
                    if(ible.openLock()){
                        Log.i("Log", "ible.openLock:  is succesfsfuelrj A");
                    }else{
                        Log.i("Log", "ible.openLock:  is fafdasfewrrwr A");
                                    /*if(ible.openLock()){
                                        Log.i("Log", "ible.openLock:  is succesfsfuelrj A");
                                    }else{

                                        Log.i("Log", "ible.opne:  is fafdaadfaewrsfewrrwr A");
                                    }*/
                    }
                    //arrayListBluetoothDevices.clear();
                   // startSearching();
                    break;
                case R.id.buttonDesc:
                    makeDiscoverable();
                    break;
                case R.id.buttonOff:
                    offBluetooth();
                    break;
                default:
                    break;
            }
        }
    }

    private BroadcastReceiver myReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Message msg = Message.obtain();
            String action = intent.getAction();

            Log.i("Log", action+ " : in the action");

            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                Toast.makeText(context, "ACTION_FOUND", Toast.LENGTH_SHORT).show();

                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                try {
                    //device.getClass().getMethod("setPairingConfirmation", boolean.class).invoke(device, true);
                    //device.getClass().getMethod("cancelPairingUserInput", boolean.class).invoke(device);
                } catch (Exception e) {
                    Log.i("Log", "Inside the exception: ");
                    e.printStackTrace();
                }

                if (arrayListBluetoothDevices.size() < 1) // this checks if the size of bluetooth device is 0,then add the
                {                                           // device to the arraylist.
                    detectedAdapter.add(device.getName() + "\n" + device.getAddress());
                    arrayListBluetoothDevices.add(device);
                    detectedAdapter.notifyDataSetChanged();
                } else {
                    boolean flag = true;    // flag to indicate that particular device is already in the arlist or not
                    for (int i = 0; i < arrayListBluetoothDevices.size(); i++) {
                        if (device.getAddress().equals(arrayListBluetoothDevices.get(i).getAddress())) {
                            flag = false;
                        }
                    }
                    if (flag == true) {
                        detectedAdapter.add(device.getName() + "\n" + device.getAddress());
                        arrayListBluetoothDevices.add(device);
                        detectedAdapter.notifyDataSetChanged();
                    }
                }
            }
        }
    };

    private void startSearching() {
        Log.i("Log", "in the start searching method");
        IntentFilter intentFilter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        MainActivity.this.registerReceiver(myReceiver, intentFilter);
        bluetoothAdapter.startDiscovery();
    }

    private void onBluetooth() {
        if (!bluetoothAdapter.isEnabled()) {
            bluetoothAdapter.enable();
            Log.i("Log", "Bluetooth is Enabled");
        }
    }

    private void offBluetooth() {
        if (bluetoothAdapter.isEnabled()) {
            bluetoothAdapter.disable();
        }
    }

    private void makeDiscoverable() {
        Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
        startActivity(discoverableIntent);
        Log.i("Log", "Discoverable ");
    }

    protected void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    200);
        }
    }

    private void hasFileReadWritePermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    201);


        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 200: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startSearching(); // --->
                } else {
                    //TODO re-request
                }
                break;
            }
            case 201:{
                checkLocationPermission();
            }
        }
    }

    class HandleSeacrh extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 111:

                    break;

                default:
                    break;
            }
        }
    }
}