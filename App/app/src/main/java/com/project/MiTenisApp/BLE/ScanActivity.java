package com.project.MiTenisApp.BLE;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import com.project.MiTenisApp.BaseDatos.Actividad;
import com.project.MiTenisApp.BaseDatos.DatabaseSQLHelper;
import com.project.MiTenisApp.BaseDatos.Movimiento;
import com.project.MiTenisApp.BaseDatos.Usuario;
import com.project.MiTenisApp.Datos.Conversion;
import com.project.MiTenisApp.R;
import com.project.MiTenisApp.RecordsFragment;
import com.project.MiTenisApp.RegisterActivityFragment;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Queue;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;


public class ScanActivity extends AppCompatActivity implements DevicesListFragment.OnDevicesFragmentInteractionListener {

    //Definición de variables
    private DatabaseSQLHelper mDatabaseSQLHelper;
    private MenuItem refreshItem = null;
    private LocationManager locationManager;
    public boolean NEW_MOV = false;
    private boolean isWriting;
    public boolean mSearching;

    String mov;
    public String mUserId;
    String[] devicesArray = null;
    public static final String EXTRA_USER_ID= "extra_user_id";
    public ArrayList<Movimiento> movimiento = new ArrayList();
    private static final Queue<Object> writeQueue = new ConcurrentLinkedQueue<Object>();

    private static final int MSG_MOVEMENT = 101;

    //Variables del sistema Bluetooth
    public static BluetoothGatt mBleGatt;
    BluetoothManager mBleManager;
    BluetoothAdapter mBleAdapter;
    BluetoothLeScanner mBleScanner;
    private final Map< String, BluetoothDevice> mBleDevices = new HashMap<String, BluetoothDevice>();
    private BluetoothDevice mBleDevice;

    //Definición de fragments
    Fragment deviceList = new DevicesListFragment().newInstance(devicesArray);
    Fragment newMovement = new newMovementFragment().newInstance();
    final FragmentManager fm = getSupportFragmentManager();


    // This is one of the most used descriptor: Client Characteristic Configuration Descriptor. 0x2902
    public static final UUID UUID_DESCRIPTOR = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");

    public static final UUID UUID_SERVICE_SENSORS_DATA = UUID.fromString("00000000-0001-11e1-9ab4-0002a5d5c51b");
    public static final UUID UUID_CHARACTERISTIC_SENSORS_DATA = UUID.fromString("00e00000-0001-11e1-ac36-0002a5d5c51b");

    public static final UUID UUID_CONFIG_SERVICE = UUID.fromString("00000000-000f-11e1-9ab4-0002a5d5c51b");
    public static final UUID UUID_REGISTER_ACCESS = UUID.fromString("00000001-000f-11e1-ac36-0002a5d5c51b");


    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 1;
    private static final int PERMISSION_REQUEST_BLUETOOTH_ENABLE = 1;
    private static final int PERMISSION_REQUEST_LOCATION_ENABLE= 2;

    private static final int D_LOCATION_DISABLED = 1;
    private static final int D_NEED_PERMISSION_LOCATION = 2;
    private static final int D_NEED_LOCATION = 3;
    private static final int D_NEED_BLE = 4;
    private static final int D_DEVICE_DISCONNECTED = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Definir el layout a usar
        setContentView(R.layout.activity_scan);

        //Recoger los datos pasados por la actividad anterior
        Bundle b = getIntent().getExtras();
        if (b != null) {
            mUserId = b.getString(EXTRA_USER_ID);
        }

        //Definición y configuración de la barra de herramientas
        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);


        //Añadir los fragments a usar y establecer el activo
        fm.beginTransaction().add(R.id.scan_container, newMovement, "2").hide(newMovement).commit();
        fm.beginTransaction().add(R.id.scan_container,deviceList, "1").commit();

        //Nueva instancia de conexión a la base de datos
        mDatabaseSQLHelper = new DatabaseSQLHelper(this);

        //Configuración del adaptador Bluetooth
        mBleManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBleAdapter = mBleManager.getAdapter();

        //Configuración del escáner Bluetooth
        mBleScanner = mBleAdapter.getBluetoothLeScanner();

        //Comprobar si el dispositivo soporta Low Energy Bluetooth
        if (mBleAdapter == null) {
            // El dispositivo no soporta Low Energy Bluetooth
            Toast.makeText(this,
                    "El dispositivo no soporta Low Energy Bluetooth", Toast.LENGTH_LONG).show();
        }

        //Comprobar si posee permiso de ubicación y si no pedir al usuario
        if (this.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(
                    new String[] {Manifest.permission.ACCESS_COARSE_LOCATION },
                    PERMISSION_REQUEST_COARSE_LOCATION);
        }

        //Habilitación del Bluetooth
        if (mBleAdapter != null && !mBleAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, PERMISSION_REQUEST_BLUETOOTH_ENABLE);
        }

        //Encendido del sistema GPS si está apagado
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            //Mostrar mensaje
            dialogos(D_LOCATION_DISABLED);
        }else{
            //Empezar búsqueda de dispositivos
            startScan(true);
        }

    }

    @Override
    protected void onDestroy(){
        mDatabaseSQLHelper.close();
        super.onDestroy();
        if (mBleGatt == null) return;
        mBleGatt.close();
        mBleGatt = null;

    }

    @Override
    protected void onRestart(){
        super.onRestart();

        //Comprobar si sigue teniendo permiso de ubicación, y si no pedir al usuario
        if (this.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(
                    new String[] {Manifest.permission.ACCESS_COARSE_LOCATION },
                    PERMISSION_REQUEST_COARSE_LOCATION);
        }

        //Comprobar si Bluetooth sigue habilitado y si no habilitar
        if (mBleAdapter != null && !mBleAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, PERMISSION_REQUEST_BLUETOOTH_ENABLE);
        }

        //Comprobar si GPS sigue encendido y si no encender
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            dialogos(D_LOCATION_DISABLED);
        }else{
            //Empezar escaneo
            startScan(true);
        }


    }
        

    @Override
    public void OnDevicesFragmentInteractionListener(String deviceName) {
        BluetoothDevice device = mBleDevices.get(deviceName);

        if (device != null) {
            connectBLEDevice(device);
            startCapturing();
            mBleDevice = device;
        }
    }

    /**
     * Método para crear el menú
     * @return true
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.device_list, menu);
        refreshItem = menu.findItem(R.id.ic_refresh);
        return true;
    }

    /**
     * Método que inicializa los elementos del menú
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (refreshItem != null) {
            refreshItem.setEnabled(true);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    /**
     * Método que define las acciones a realizar al pulsar los elementos del menú
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.ic_refresh) {
            mBleDevices.clear();
            startScan(true);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    /**
     * Método que comprueba el resultado de la petición del permiso de ubicación
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_COARSE_LOCATION: {
                if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                   dialogos(D_NEED_PERMISSION_LOCATION);
                }
            }
        }
    }


    /**
     * Método que comprueba el resultado de las peticiones de activar Bluetooth
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch(requestCode) {
            //Comprobar resultado petición activar Bluetooth
            case PERMISSION_REQUEST_BLUETOOTH_ENABLE:
                if (resultCode != Activity.RESULT_OK) {
                    dialogos(D_NEED_BLE);
                }
                break;

            //Comprobar resultado petición encender GPS
            case PERMISSION_REQUEST_LOCATION_ENABLE:
                if (resultCode != Activity.RESULT_OK){

                    if(!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                        dialogos(D_NEED_LOCATION);
                    }else{
                        //Empezar escaneo
                        startScan(true);
                    }
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    /**
     * Método  para manejar los diálogos
     */
    private void dialogos (int code){
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Acceso limitado");

        switch(code){

            case D_LOCATION_DISABLED:
                builder.setMessage("El sistema GPS está desactivado. ¿Desea activarlo?").setCancelable(false)
                        .setPositiveButton("Sí", new DialogInterface.OnClickListener() {
                            public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                                startActivityForResult(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS), PERMISSION_REQUEST_LOCATION_ENABLE);
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                                dialog.dismiss();
                                dialogos(D_NEED_LOCATION);
                            }
                        });
                break;

            case D_NEED_PERMISSION_LOCATION:
                builder.setMessage("Se debe permitir acceso a la ubicación para poder usar esta aplicación");
                builder.setPositiveButton(android.R.string.ok, null);
                builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        finish();
                    }
                });

                break;

            case D_NEED_LOCATION:

                builder.setMessage("Se debe activar la ubicación para poder usar esta aplicación.");
                builder.setPositiveButton(android.R.string.ok, null);
                builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                         finish();
                    }
                });

                break;

            case D_NEED_BLE:
                builder.setTitle("Acceso limitado");
                builder.setMessage("Se debe activar el Bluetooth para poder usar esta aplicación.");
                builder.setPositiveButton(android.R.string.ok, null);
                builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        finish();
                    }
                });

                break;

            case D_DEVICE_DISCONNECTED:
                builder.setTitle("Problema de conexión");
                builder.setMessage("¿Quieres buscar de nuevo tu dispositivo?");
                builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        //eliminar el fragmento anterior
                       // getSupportFragmentManager().beginTransaction().hide(getSupportFragmentManager().findFragmentById(R.id.container_4)).commit();
                        //connectBLEDevice(mBleDevice);
                        startScan(true);
                    }
                });
                builder.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });
                builder.setCancelable(false);
        }
        builder.show();
    }



    /////////////////////////////// MÉTODOS BUSCAR DISPOSITIVOS BLE////////////////////////////////

    /**
     * Método para empezar la búsqueda de dipositivos
     * @param enable true para iniciar
     *               false para parar
     */
    public void startScan(boolean enable){
        if (!enable) {
            stopScan();
        } else {
            mSearching = true;
            devicesArray = null;
            startDeviceList();
            devicesArray=null;
            mBleScanner.startScan(leScanCallback);
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    stopScan();
                }
            }, 3000);
        }
    }


    /**
     * Método para parar la búsqueda de dipositivos
     */
    public void stopScan() {
        mSearching=false;
        System.out.println("stopping scanning");
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                mBleScanner.stopScan(leScanCallback);
            }
        });
        startDeviceList();
    }


    /**
     * Método para manejar el resultado de la búsqueda de dipositivos
     */
    private ScanCallback leScanCallback = new ScanCallback() {

        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            BluetoothDevice mBleDevice;
            mBleDevice = result.getDevice();
            if (mBleDevice != null && mBleDevice.getName() != null && mBleDevice.getName().contains("WeSU") && !mBleDevices.containsValue(mBleDevice)) {
                //Añadir dispositivo a la lista
                mBleDevices.put(mBleDevice.getAddress(), mBleDevice);
                devicesArray = mBleDevices.keySet().toArray(new String[mBleDevices.keySet().size()]);
            }
        }
    };


/////////////////// MÉTODOS TRANSMISIÓN Y RECEPCIÓN DATOS POR BLE//////////////////////////////////

    /**
     * Método para conectarse a un dispositivo Blueetooth
     * @param bluetoothDevice dispositivo Bluetooth al que se quiere conectar
     */
    public void connectBLEDevice( BluetoothDevice bluetoothDevice) {
        try {
            mBleGatt = bluetoothDevice.connectGatt(this, false, mBleGattCallback);
            Toast.makeText(this,
                    "Conectado al dispositivo", Toast.LENGTH_SHORT).show();

        }
        catch (Exception e) {
            Toast.makeText(this,
                    "Error al conectarse al dispositivo", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * @param status int que representa el estado de la conexión Bluetooth
     * @return string que representa el estado de la conexión Bluetooth
     */
    private String connectionState(int status) {
        switch (status) {
            case BluetoothProfile.STATE_CONNECTED:
                return "Connected";
            case BluetoothProfile.STATE_DISCONNECTED:
                return "Disconnected";
            case BluetoothProfile.STATE_CONNECTING:
                return "Connecting";
            case BluetoothProfile.STATE_DISCONNECTING:
                return "Disconnecting";
            default:
                return String.valueOf(status);
        }
    }

    /**
     * Método que implementa las llamadas del BluetoothGatt
     */
    private final BluetoothGattCallback mBleGattCallback = new BluetoothGattCallback() {

        @Override
        public void onConnectionStateChange(final BluetoothGatt gatt, final int status, final int newState) {
            // this will get called when a device connects or disconnects
            if (status == BluetoothGatt.GATT_SUCCESS && newState == BluetoothProfile.STATE_CONNECTED) {
                // Start discovering services
                gatt.discoverServices();
                mHandler.sendMessage(Message.obtain(null, MSG_PROGRESS, "Buscando servicios..."));
                new Timer().schedule(new TimerTask() {
                    @Override
                    public void run() {
                        startCapturing();
                    }
                }, 30000);

            }else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                Log.i("FIT APP", "Disconnected from GATT client");
                gatt.close();
                connectBLEDevice(mBleDevice);

            }else if(status!= BluetoothGatt.GATT_SUCCESS){
                gatt.disconnect();
                connectBLEDevice(mBleDevice);
            }
        }

        @Override
        public void onServicesDiscovered(final BluetoothGatt gatt, final int status) {
            // this will get called after the client initiates a BluetoothGatt.discoverServices() call
            mBleGatt = gatt;

            mHandler.sendEmptyMessage(MSG_DISMISS);

        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {

            if(characteristic.getUuid().equals(UUID_CHARACTERISTIC_SENSORS_DATA)){
                mHandler.sendMessage(Message.obtain(null, MSG_MOVEMENT, characteristic));
            }
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, final BluetoothGattCharacteristic characteristic) {
            // this will get called anytime you perform a read or write characteristic operation

            if (characteristic.getUuid().equals(UUID_CHARACTERISTIC_SENSORS_DATA)) {
                mHandler.sendMessage(Message.obtain(null, MSG_MOVEMENT, characteristic));

            }
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            Log.v("fit app", "onCharacteristicWrite: " + status);
            // Synchronous writing
            isWriting = false;
            nextGattWrite();
        }

        @Override
        public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {

            Log.v("fit app", "onDescriptorWrite: " + status);
            // Synchronous writing
          isWriting = false;
          nextGattWrite();
        }

    };

    /**
     * Enable notifications and configure sensors
     **/
    public void enableNotificationsAndConfigureSensors() {
        enableNotifications(mBleGatt);
        mHandler.sendEmptyMessage(MSG_DISMISS);
        configureSensors();
        NEW_MOV = true;
    }

    /**
     * Método para habilitar las notificaciones de los sensores configurados
     * @param gatt GATT que se usará
     */
    public void enableNotifications(BluetoothGatt gatt) {

        BluetoothGattService service, service_config;
        BluetoothGattCharacteristic characteristic, characteristic_config;
        BluetoothGattDescriptor descriptor;

        try {
            service = gatt.getService(UUID_SERVICE_SENSORS_DATA);
            characteristic = service.getCharacteristic(UUID_CHARACTERISTIC_SENSORS_DATA);

            // Habilitar notificationes locales
            gatt.setCharacteristicNotification(characteristic, true);

            // Habilitar notificationes remotas
            descriptor= characteristic.getDescriptor(UUID_DESCRIPTOR);
            descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
            gatt.writeDescriptor(descriptor);

            service_config = gatt.getService(UUID_CONFIG_SERVICE);
            characteristic_config = service_config.getCharacteristic(UUID_REGISTER_ACCESS);

            // Habilitar notificationes locales
            gatt.setCharacteristicNotification(characteristic_config, true);

            // Habilitar notificationes remotas
            gatt.writeDescriptor(descriptor);

        } catch (NullPointerException npe) {
            gatt.disconnect();
            dialogos(D_DEVICE_DISCONNECTED);
        }



    }

    /**
     *  Disable notifications in a sensor
     * @param gatt GATT that will be used
     */
    public void disableNotifications(BluetoothGatt gatt) {
        BluetoothGattService service;
        BluetoothGattCharacteristic characteristic;

        service = gatt.getService(UUID_SERVICE_SENSORS_DATA);
        characteristic = service.getCharacteristic(UUID_CHARACTERISTIC_SENSORS_DATA);

        // Disable local notifications
        gatt.setCharacteristicNotification(characteristic, false);
        // Disable remote notifications
        BluetoothGattDescriptor descriptor = characteristic.getDescriptor(UUID_DESCRIPTOR);
        descriptor.setValue(BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE);
        gattWrite(descriptor);

        service = gatt.getService(UUID_CONFIG_SERVICE);
        characteristic = service.getCharacteristic(UUID_REGISTER_ACCESS);

        // Disable local notifications
        gatt.setCharacteristicNotification(characteristic, false);
        // Disable remote notifications
        descriptor = characteristic.getDescriptor(UUID_DESCRIPTOR);
        descriptor.setValue(BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE);
        gattWrite(descriptor);

    }

    /**
     * Método para configurar el periodo y los sensores
     */
    public void configureSensors() {
        setPeriod(mBleGatt,104);                        //Configurar frecuencia timer a 104 Hz
        setSensors(mBleGatt,0,false,4);     //Configurar ACC  FS:  4 g
        setSensors(mBleGatt,0,true,104);   //Configurar ACC  ODR: 104 Hz
        setSensors(mBleGatt,1,false,2000);  //Configurar GYR  FS:  2000 dps
        setSensors(mBleGatt,1,true,104);   //Configurar GYR  ODR: 104 Hz
        setSensors(mBleGatt,2,false,32);    //Configurar MAG  FS:  32  gauss
        setSensors(mBleGatt,2,true,20);   //Configurar MAG  ODR: 20 Hz

        Log.i("FIT APP", "Sensors configured");
    }

    /**
     * Configure sensors data
     * @param gatt GATT that will be used
     * @param value new value to configure
     */
    private synchronized void setPeriod(BluetoothGatt gatt, int value) {

        BluetoothGattCharacteristic characteristic;
        // Conversion of the value to an array of 2 bytes length
        byte[] data;
        data = ByteBuffer.allocate(2).order(ByteOrder.LITTLE_ENDIAN).putShort((short)value).array();

        byte[] data_def= null;          //Array of bytes that will contain the data written in the register
        data_def = new byte[4 + data.length];  //Definition of the length of the array of bytes

        //Initialization of the first 4 bytes according the configuration selected

        data_def[0] = (byte) (0x80|0x40|0x20|0x08);    // 0xE8 Configure mode execution, persistent settings, write mode and ACK mode
        data_def[0]  = (byte) (232 & 0xFF);
        data_def[1] = (byte) 0x21; //Initialization required
        data_def[2] = (byte) 0;   // No error
        data_def[3] = (byte) 1;      //Length of the register


        //Initialization of the rest of the array by copying the data selected
        System.arraycopy(data, 0, data_def, 4, data.length);

        BluetoothGattService service = gatt.getService(UUID_CONFIG_SERVICE);
        try {
            BluetoothGattCharacteristic charact = service.getCharacteristic(UUID_REGISTER_ACCESS);
            charact.setValue(data_def);
            gattWrite(charact);
        }catch (NullPointerException npe){
        }

    }

    /**
     * Configure sensors data
     * @param gatt GATT that will be used
     * @param sensor
     *      0: configure accelerometer
     *      1: configure gyroscope
     *      2: configure magnetometer
     * @param config
     *      0: configure full scale (FS)
     *      1: configure output data rate (ODR)
     * @param value new value to configure
     */
    private synchronized void setSensors(BluetoothGatt gatt, int sensor, boolean config, int value) {

            int val = (value & 0xFFFF);

            // Conversion of the value to an array of 2 bytes length
            byte[] data;
            data = ByteBuffer.allocate(2).order(ByteOrder.LITTLE_ENDIAN).putShort((short) val).array();

            byte CTRL = (byte) (0x80|0x40|0x20|0x08);   // Configure mode execution, persistent settings, write mode and ACK mode

            byte ADDR= (byte) 0; //Initialization required

            switch(sensor){
                case 0:          //ACCELEROMETER
                    if(!config) {  //   FS
                        ADDR = (byte) 0x74;   // Address of the register
                    }else{         //  ODR
                        ADDR = (byte) 0x75;   // Address of the register
                    }
                    break;

                case 1:     //GYROSCOPE
                    if(!config) {    //   FS
                        ADDR = (byte) 0x76;   // Address of the register
                    }else{          //  ODR
                        ADDR = (byte) 0x77;   // Address of the register
                    }
                    break;

                case 2:          //MAGNETOMETER
                    if(!config) {    //   FS
                       ADDR = (byte) 0x78;   // Address of the register
                    }else{           //  ODR
                       ADDR = (byte) 0x79;   // Address of the register
                    }
                    break;
            }

            byte ERR  = (byte) 0;   // No error
            byte LEN  = (byte) 1;      //Length of the register

            byte[] data_def= null;          //Array of bytes that will contain the data written in the register
            data_def = new byte[4 + data.length];  //Definition of the length of the array of bytes

            //Initialization of the first 4 bytes according the configuration selected
            data_def[0]= CTRL;
            data_def[1]= ADDR;
            data_def[2]= ERR;
            data_def[3]= LEN;

            //Initialization of the rest of the array by copying the data selected
            System.arraycopy(data, 0, data_def, 4, data.length);

        BluetoothGattService service = gatt.getService(UUID_CONFIG_SERVICE);
        try {
            BluetoothGattCharacteristic charact = service.getCharacteristic(UUID_REGISTER_ACCESS);
            charact.setValue(data_def);
            gattWrite(charact);

        }catch (NullPointerException npe){
        }

    }


    // Methods used to avoid problems due to asynchronous writings
    private synchronized void gattWrite(Object o) {
        if(writeQueue.isEmpty() && !isWriting) {
            doGattWrite(o);
        } else {
            writeQueue.add(o);
        }
    }

    // Methods used to avoid problems due to asynchronous writings
    private synchronized void nextGattWrite() {
        if(!writeQueue.isEmpty() && !isWriting) {
            doGattWrite(writeQueue.poll());
        }
    }

    // Methods used to avoid problems due to asynchronous writings
    private synchronized void doGattWrite(Object o) {
        if(o instanceof BluetoothGattCharacteristic) {
            isWriting = true;
            mBleGatt.writeCharacteristic((BluetoothGattCharacteristic) o);
        } else if(o instanceof BluetoothGattDescriptor) {
            isWriting = true;
            mBleGatt.writeDescriptor((BluetoothGattDescriptor) o);
        } else {
            nextGattWrite();
        }
    }

////////////////////////////////// MÉTODOS ALMACENAR BASE DATOS ////////////////////////////////////

    /**
     * Guardar en la base de datos los valores de los sensores obtenidos
     * @param value  cadena de bytes que contiene la información de los sensores
     * @param mov   identificador de la actividad a realizar
     **/
    private Movimiento split (byte[] value, String mov){
        //Obtener valores convertidos de los sensores
        Integer ts = Conversion.convertTs(value);
        Double[] ACC = Conversion.convertACC(value);
        Double[] GYR = Conversion.convertGYR(value);
        Double[] MAG = Conversion.convertMAG(value);
        //Crear un objeto de la clase Movimiento
        return new Movimiento(mov,ts,ACC,GYR,MAG);
    }

    /**
     * Guardar el movimiento en la base de datos
     * @param mov      movimiento de la clase Movimiento a guardar
     **/
    private void addMovement(Movimiento... mov) {
        long i = mDatabaseSQLHelper.saveMovement(mov[0]);
    }

    /**
     * Guardar la actividad completada y los movimientos en la base de datos
     * @param duration  tiempo duración de la actividad
     * @return String con el identificador de la actividad
     **/
    public String toDatabase (Double duration) {

        // Guardar todos los movimientos de la actividad en la base de datos
        for(int i=0;i< movimiento.size(); i++ ){
            addMovement(movimiento.get(i));
        }

        //Obtener los datos actuales del usuario
        Cursor c = mDatabaseSQLHelper.getUserById(mUserId);
        c.moveToLast();
        Usuario user = new Usuario(c);
        if (c.moveToLast()) {
            user = new Usuario(c);
        }


        //Crear un objeto de la clase Actividad
        Actividad actividad = new Actividad(mov, mBleDevice.getName(), mUserId, duration, user.getName(), user.getAge(), user.getWeight(), user.getHeight());

        //Añadir Actividad a la base de datos
        addActivity(actividad);

        mDatabaseSQLHelper.close();

        return mov;
    }

    /**
     * Resetear todas las variables
     **/
    public void reset(){
        mov = null;
        movimiento.clear();
    }

    /**
     * Guardar la actividad en la base de datos
     * @param act       actividad de la clase Actividad a guardar
     **/
    private void addActivity(Actividad... act) {
        long i = mDatabaseSQLHelper.saveActivity(act[0]);
    }


    ////////////////////////////    FRAGMENTS   ///////////////////////////////////////////////////

    /**
     * Método para empezar el fragment con la lista de dispositivos
     */
    public void startDeviceList() {
        deviceList = new DevicesListFragment().newInstance(devicesArray);
        fm.beginTransaction().replace(R.id.scan_container, deviceList, "1").hide(newMovement).commitAllowingStateLoss();
    }

    /**
     * Método para cambiar el fragment y empezar a capturar
     */
    public void startCapturing() {
        newMovement = new newMovementFragment().newInstance();
        fm.beginTransaction().replace(R.id.scan_container, newMovement, "2").hide(deviceList).commitAllowingStateLoss();
        //newMovementFragment n = newMovementFragment.newInstance();
    }



    /*
     * We have a Handler to process event results on the main thread
     */
    private static final int MSG_PROGRESS = 201;
    private static final int MSG_DISMISS = 202;
    private Handler mHandler = new Handler()
    {
        @SuppressLint("NewApi")
        @Override
        public void handleMessage(Message msg)
        {
            BluetoothGattCharacteristic characteristic;
            switch (msg.what)
            {
                case MSG_MOVEMENT:

                    characteristic = (BluetoothGattCharacteristic) msg.obj;
                    if(characteristic.getValue() == null){
                        Log.w("FIT APP", "Error obtaining movement values");
                        return;
                    }

                    //Crear identificador de nueva actividad
                    if(NEW_MOV) {
                        SimpleDateFormat data = new SimpleDateFormat("ddMMyyyyHHmm", Locale.getDefault());
                        mov =  data.format(new Date());
                    }

                    //Crear objeto de la clase Movimiento a partir de los datos obtenidos
                    Movimiento m = split(characteristic.getValue(), mov);

                    //Añadir el objeto a una lista
                    movimiento.add(m);

                    NEW_MOV = false;
                    break;

                case MSG_DISMISS:
                   break;

            }
        }
    };
}

