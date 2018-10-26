//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//
package bluetoothlocker.iotasol.com.bluetoothlocker;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothAdapter.LeScanCallback;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import com.coolu.blelibrary.config.Config;
import com.coolu.blelibrary.config.LockType;
import com.coolu.blelibrary.dispose.impl.AQ;
import com.coolu.blelibrary.dispose.impl.Battery;
import com.coolu.blelibrary.dispose.impl.CloseLock;
import com.coolu.blelibrary.dispose.impl.LockResult;
import com.coolu.blelibrary.dispose.impl.LockStatus;
import com.coolu.blelibrary.dispose.impl.OpenLock;
import com.coolu.blelibrary.dispose.impl.Password;
import com.coolu.blelibrary.dispose.impl.TY;
import com.coolu.blelibrary.dispose.impl.Token;
import com.coolu.blelibrary.inter.IBLE;
import com.coolu.blelibrary.inter.OnConnectionListener;
import com.coolu.blelibrary.inter.OnDeviceSearchListener;
import com.coolu.blelibrary.inter.OnResultListener;
import com.coolu.blelibrary.mode.BatteryTxOrder;
import com.coolu.blelibrary.mode.GetLockStatusTxOrder;
import com.coolu.blelibrary.mode.GetTokenTxOrder;
import com.coolu.blelibrary.mode.OpenLockTxOrder;
import com.coolu.blelibrary.mode.PasswordTxOrder;
import com.coolu.blelibrary.mode.ResetLockTxOrder;
import com.coolu.blelibrary.mode.TxOrder;
import com.coolu.blelibrary.mode.Order.TYPE;
import com.coolu.blelibrary.utils.EncryptUtils;
import com.coolu.blelibrary.utils.GlobalParameterUtils;
import com.coolu.blelibrary.utils.HexStringUtils;
import com.coolu.blelibrary.utils.Logger;
import java.lang.reflect.Method;
import java.util.UUID;

public class CustomIBLE implements IBLE {
    private static final String TAG = CustomIBLE.class.getSimpleName();
    private BluetoothAdapter mBluetoothAdapter;
    private OnDeviceSearchListener mOnDeviceSearchListener;
    private BluetoothGatt mBluetoothGatt;
    private OnConnectionListener mOnConnectionListener;
    private BluetoothGattCharacteristic write_characteristic;
    private Token mToken;
    private Context context;
    private boolean isConnected = false;
    private static IBLE ible;
    private Handler handler = new Handler(Looper.myLooper());
    private LeScanCallback mLeScanCallback = new LeScanCallback() {
        public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
            if (null != CustomIBLE.this.mOnDeviceSearchListener) {
                CustomIBLE.this.mOnDeviceSearchListener.onScanDevice(device, rssi, scanRecord);
            }

        }
    };
    private BluetoothGattCallback mBluetoothGattCallback = new BluetoothGattCallback() {
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            Logger.i(CustomIBLE.class.getSimpleName(), "status:" + status + ";newStatus:" + newState);
            if (status == 133) {
                CustomIBLE.this.isConnected = false;
                CustomIBLE.this.mOnConnectionListener.onDisconnect(3);
            } else {
                switch(newState) {
                    case 0:
                        CustomIBLE.this.isConnected = false;
                        if (null != CustomIBLE.this.mOnConnectionListener) {
                            CustomIBLE.this.mOnConnectionListener.onDisconnect(0);
                        }

                        gatt.close();
                        break;
                    case 2:
                        CustomIBLE.this.isConnected = true;
                        gatt.discoverServices();
                }

            }
        }

        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            if (status == 0) {
                CustomIBLE.this.isConnected = true;
                BluetoothGattService service = gatt.getService(Config.bltServerUUID);
                if (null != service) {

                    BluetoothGattCharacteristic read_characteristic = service.getCharacteristic(Config.readDataUUID);
                    CustomIBLE.this.write_characteristic = service.getCharacteristic(Config.writeDataUUID);
                    int properties = read_characteristic.getProperties();
                    if ((properties | 16) > 0) {
                        gatt.setCharacteristicNotification(read_characteristic, true);
                        BluetoothGattDescriptor descriptor = read_characteristic.getDescriptor(Config.CLIENT_CHARACTERISTIC_CONFIG);
                        if (null != descriptor) {
                            descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                            gatt.writeDescriptor(descriptor);
                        }
                    }

                    if (null != CustomIBLE.this.mOnConnectionListener) {
                        CustomIBLE.this.mOnConnectionListener.onServicesDiscovered(TextUtils.isEmpty(gatt.getDevice().getName()) ? "NokeLock" : gatt.getDevice().getName(), gatt.getDevice().getAddress());
                    }

                    CustomIBLE.this.handler.postDelayed(new Runnable() {
                        public void run() {
                            CustomIBLE.this.getToken();
                        }
                    }, 2000L);
                } else if (null != CustomIBLE.this.mOnConnectionListener) {
                    CustomIBLE.this.mOnConnectionListener.onDisconnect(5);
                }
            }

            super.onServicesDiscovered(gatt, status);
        }

        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            Object mingwenx = null;

            try {
                byte[] values = characteristic.getValue();
                byte[] x = new byte[16];
                System.arraycopy(values, 0, x, 0, 16);
                byte[] mingwen = EncryptUtils.Decrypt(x, Config.KEY);
                Logger.d(CustomIBLE.TAG, "onCharacteristicChanged：" + HexStringUtils.toHexString(mingwen));
                CustomIBLE.this.mToken.handlerRequest(HexStringUtils.toHexString(mingwen), 0);
            } catch (Exception var6) {
                if (GlobalParameterUtils.getInstance().getOnResultListener() != null) {
                    GlobalParameterUtils.getInstance().getOnResultListener().errorResult(4);
                }

                Logger.d(CustomIBLE.TAG, "onCharacteristicChanged exception ：" + var6);
            }

        }
    };

    private CustomIBLE(Context context) {
        this.context = context;
        BluetoothManager bluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
        this.mBluetoothAdapter = bluetoothManager.getAdapter();
        GlobalParameterUtils.getInstance().setContext(context.getApplicationContext());
        this.mToken = new Token();
        Battery battery = new Battery();
        OpenLock openLock = new OpenLock();
        TY ty = new TY();
        CloseLock closeLock = new CloseLock();
        LockStatus lockStatus = new LockStatus();
        Password password = new Password();
        LockResult lockResult = new LockResult();
        AQ aq = new AQ();
        this.mToken.nextHandler = battery;
        battery.nextHandler = openLock;
        openLock.nextHandler = ty;
        ty.nextHandler = closeLock;
        closeLock.nextHandler = lockStatus;
        lockStatus.nextHandler = password;
        password.nextHandler = lockResult;
        lockResult.nextHandler = aq;
    }

    public static IBLE init(Context context) {
        return (IBLE)(ible == null ? new CustomIBLE(context) : ible);
    }

    public boolean setDebug(boolean flag) {
        if (flag) {
            Logger.LOGLEVEL = 0;
        } else {
            Logger.LOGLEVEL = 6;
        }

        return true;
    }

    public boolean isSupportBluetooth() {
        return this.context.getPackageManager().hasSystemFeature("android.hardware.bluetooth_le");
    }

    public boolean enableBluetooth() {
        return this.mBluetoothAdapter.enable();
    }

    public boolean disableBluetooth() {
        return this.mBluetoothAdapter.disable();
    }

    public boolean isEnable() {
        return this.mBluetoothAdapter.isEnabled();
    }

    public BluetoothAdapter getBluetoothAdapter() {
        return this.mBluetoothAdapter;
    }

    public void setLockType(LockType lockType) {
        GlobalParameterUtils.getInstance().setLockType(lockType);
    }

    public void startScan(OnDeviceSearchListener onDeviceSearchListener) {
        if (this.mBluetoothAdapter != null) {
            this.mBluetoothAdapter.stopLeScan(this.mLeScanCallback);
            this.mOnDeviceSearchListener = onDeviceSearchListener;
            this.mBluetoothAdapter.startLeScan(this.mLeScanCallback);
        }
    }

    public void startFilterScan(OnDeviceSearchListener onDeviceSearchListener) {
        if (this.mBluetoothAdapter != null) {
            this.mBluetoothAdapter.stopLeScan(this.mLeScanCallback);
            this.mOnDeviceSearchListener = onDeviceSearchListener;
            this.mBluetoothAdapter.startLeScan(new UUID[]{Config.bltServerUUID}, this.mLeScanCallback);
        }
    }

    public void stopScan() {
        if (this.mBluetoothAdapter != null) {
            this.mBluetoothAdapter.stopLeScan(this.mLeScanCallback);
        }
    }

    public synchronized boolean connect(String address, OnConnectionListener onConnectionListener) {
        this.mOnConnectionListener = onConnectionListener;
        if (!TextUtils.isEmpty(address) && this.mBluetoothAdapter != null) {
            BluetoothDevice bluetoothDevice = this.mBluetoothAdapter.getRemoteDevice(address);
            if (null == bluetoothDevice) {
                onConnectionListener.onDisconnect(-1);
                return false;
            } else {
                if (this.mBluetoothGatt != null) {
                    this.mBluetoothGatt.close();
                    this.mBluetoothGatt = null;
                }

                this.mBluetoothGatt = bluetoothDevice.connectGatt(this.context, false, this.mBluetoothGattCallback);
                return true;
            }
        } else {
            onConnectionListener.onDisconnect(-1);
            return false;
        }
    }

    public synchronized boolean connectDevice(BluetoothDevice device, OnConnectionListener onConnectionListener) {
        this.mOnConnectionListener = onConnectionListener;
        if (device == null) {
            onConnectionListener.onDisconnect(-1);
            return false;
        } else {
            if (this.mBluetoothGatt != null) {
                this.mBluetoothGatt.close();
                this.mBluetoothGatt = null;
            }

            this.mBluetoothGatt = device.connectGatt(this.context, false, this.mBluetoothGattCallback);
            return true;
        }
    }

    public boolean startResultListener(OnResultListener onResultListener) {
        if (onResultListener == null) {
            return false;
        } else {
            GlobalParameterUtils.getInstance().setOnResultListener(onResultListener);
            return true;
        }
    }

    public boolean getToken() {
        return this.writeObject(new GetTokenTxOrder());
    }

    public boolean getBattery() {
        return this.writeObject(new BatteryTxOrder());
    }

    public boolean openLock() {
        return this.writeObject(new OpenLockTxOrder());
    }

    public boolean closeLock() {
        return this.writeObject(new ResetLockTxOrder());
    }

    public boolean getLockStatus() {
        return this.writeObject(new GetLockStatusTxOrder());
    }

    public boolean setPassword() {
        if (!this.writeObject(new PasswordTxOrder(TYPE.RESET_PASSWORD, Config.password))) {
            return false;
        } else {
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {
                    CustomIBLE.this.writeObject(new PasswordTxOrder(TYPE.RESET_PASSWORD2, Config.newPWD));
                }
            }, 3000L);
            return true;
        }
    }

    public void disconnect() {
        if (this.mBluetoothGatt != null) {
            this.isConnected = false;
            this.mBluetoothGatt.disconnect();
        }
    }

    public void close() {
        if (this.mBluetoothGatt != null) {
            this.stopScan();
            this.isConnected = false;
            this.mBluetoothGatt.close();
            this.mBluetoothGatt = null;
        }
    }

    public boolean getConnectStatus() {
        return this.isConnected;
    }

    private boolean writeObject(TxOrder txOrder) {
        if (this.mBluetoothAdapter != null && this.mBluetoothAdapter.isEnabled()) {
            if (this.mBluetoothGatt != null && this.write_characteristic != null) {
                byte[] miwen = EncryptUtils.Encrypt(HexStringUtils.hexString2Bytes(txOrder.generateString()), Config.KEY);
                if (miwen != null) {
                    Logger.e(CustomIBLE.class.getSimpleName(), " miwen: " +miwen);
                    this.write_characteristic.setValue( miwen);
                    Logger.e(CustomIBLE.class.getSimpleName(), " genarted string: " +miwen + " : "+ txOrder.generateString());
                    Logger.d(CustomIBLE.TAG, "we are in depth of it should work："+this.write_characteristic);
                    if(this.mBluetoothGatt.writeCharacteristic(this.write_characteristic)){
                        Logger.d(CustomIBLE.TAG, "while writinter characteristics all is good："+this.write_characteristic);
                    }else{
                        Logger.d(CustomIBLE.TAG, "while writinter characteristics all is bad："+this.write_characteristic);
                    }
                    return this.mBluetoothGatt.writeCharacteristic(this.write_characteristic);
                } else {
                    Logger.d(CustomIBLE.TAG, "we are in depth of it should work：miwen is null");
                    return false;
                }
            } else {
                Logger.d(CustomIBLE.TAG, "we are in depth of it should work：mBluetoothGatt is null or write_characteristic is null");
                GlobalParameterUtils.getInstance().getOnResultListener().errorResult(2);
                return false;
            }
        } else {
            Logger.d(CustomIBLE.TAG, "we are in depth of it should work：mBluetoothAdapter is null");
            GlobalParameterUtils.getInstance().getOnResultListener().errorResult(1);
            return false;
        }
    }

    public boolean refreshDeviceCache() {
        if (this.mBluetoothGatt != null) {
            try {
                BluetoothGatt localBluetoothGatt = this.mBluetoothGatt;
                Method localMethod = localBluetoothGatt.getClass().getMethod("refresh");
                if (localMethod != null) {
                    boolean bool = (Boolean)localMethod.invoke(localBluetoothGatt);
                    return bool;
                }
            } catch (Exception var4) {
                var4.printStackTrace();
            }
        }

        return false;
    }
}
