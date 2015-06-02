package com.jinsen.multifinder;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.IBinder;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.daimajia.swipe.SwipeLayout;
import com.jinsen.multifinder.Events.RssiMessage;
import com.jinsen.multifinder.Events.StatusMessage;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;
import jp.wasabeef.recyclerview.animators.FadeInLeftAnimator;


public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    private RecyclerView mRecyclerView;
    private SwipeLayout mSwipeLayout;
    private FloatingActionButton mActionButton;
    private RecyclerView.LayoutManager mLayoutManager;
    private DeviceAdapter mAdapter;
    private FileCache mCache;
    private List<DeviceBean> mDevices;
    private List<Integer> mColors;

    public static final int REQUEST_CONNECT_DEVICE = 1;
    public static final int REQUEST_ENABLE_BT = 2;

    private BluetoothLeService mBluetoothLeService = null;
    private BluetoothAdapter mBluetoothAdapter;

    // Code to manage Service lifecycle.
    private final ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            mBluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();
            if (!mBluetoothLeService.initialize()) {
                Log.e(TAG, "Unable to initialize Bluetooth");
                finish();
            }

            for (int i = 0; i < mDevices.size(); i++) {
                String address = mDevices.get(i).getAddress();
                mBluetoothLeService.connect(address);
                Log.d(TAG, "Connecting " + address);
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBluetoothLeService = null;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initBluetooth();

        initViews();

        Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);
        bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);
    }

    @Override
    protected void onStart() {
        super.onStart();
        // If BT is not on, request that it be enabled.
        // setupChat() will then be called during onActivityResult
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
        }

        EventBus.getDefault().register(this);

    }

    @Override
    protected void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
        mCache.writeToFile();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Cache devices to file cache;
//        mCache.writeToFile();

        unbindService(mServiceConnection);
        mBluetoothLeService = null;

    }

    private void initBluetooth() {
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            ToastUtil.showToastLong(this, "并不支持低功耗蓝牙!\n正在退出..");
            finish();
        }

        // Initializes a Bluetooth adapter.  For API level 18 and above, get a reference to
        // BluetoothAdapter through BluetoothManager.
        final BluetoothManager bluetoothManager =
                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();

        // if Bluetooth is available
        if (mBluetoothAdapter == null) {
            ToastUtil.showToastShort(this, "请授予蓝牙权限!\n 正在退出..");
            finish();
            return;
        }
    }

    private void initViews() {
        mRecyclerView = ((RecyclerView) findViewById(R.id.recyclerView));
        mActionButton = ((FloatingActionButton) findViewById(R.id.fab));
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getResources().getDrawable(R.drawable.divider)));
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setItemAnimator(new FadeInLeftAnimator());

        // Load from cache
        mCache = FileCache.newInstance();
        mDevices = mCache.getDevices();
        mColors = new ArrayList<>();
        for (int i = 0; i < mDevices.size(); i++) {
            mColors.add(Color.RED);
        }

        mAdapter = new DeviceAdapter(mDevices, mColors);
        final View dialogView = getLayoutInflater().inflate(R.layout.dialog_pref, (ViewGroup) findViewById(R.id.dialog_pref));
        mAdapter.setOnItemClickListener(new DeviceAdapter.MyItemClickListener() {
            @Override
            public void onClick(View view, final int position) {
                AlertDialog dialog = new AlertDialog.Builder(MainActivity.this).setTitle("请输入设备名称")
//                        .setIcon(android.R.drawable.ic_dialog_info)
                        .setView(dialogView)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                setResult(RESULT_OK);
                                String name = ((EditText) dialogView.findViewById(R.id.title)).getText().toString();
                                mDevices.get(position).setTitle(name);
                                mAdapter.notifyDataSetChanged();
                            }
                        })
                        .setNegativeButton("返回", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        }).create();
                dialog.show();
            }
        });

        mRecyclerView.setAdapter(mAdapter);

        mActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(getBaseContext(), DeviceListActivity.class);
                startActivityForResult(intent, MainActivity.REQUEST_CONNECT_DEVICE);
            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CONNECT_DEVICE){
            // When DeviceListActivity returns with a device to connect
            if (resultCode == Activity.RESULT_OK) {

                // Get the device MAC address
                String address = data.getExtras().getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
                addNewDevice(address, address);

                // Connect the newly added device
                mBluetoothLeService.connect(address);

            } else Log.e("DeviceList", resultCode + "");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void addNewDevice(String name, String address) {
        DeviceBean bean = new DeviceBean(name, address, 0);
        mDevices.add(bean);
        mColors.add(Color.RED);
        mAdapter.notifyDataSetChanged();
    }

    private void updateDeviceState(String address, int rssi) {
        for (int i = 0; i < mDevices.size(); i++) {
            if (mDevices.get(i).getAddress().toLowerCase().equals(address.toLowerCase())) {
                mDevices.get(i).setRssi(rssi);
                Log.d(TAG, "update " + address + " rssi = " + rssi);
            }
        }
        Log.e(TAG, "update device state failed, no match found");
        mAdapter.notifyDataSetChanged();
    }

    public void onEventMainThread(StatusMessage message) {
        String address = message.getAddress();
        for (int i = 0; i < mDevices.size(); i++) {
            if (mDevices.get(i).getAddress().toLowerCase().equals(address.toLowerCase())) {
                if (message.getState() == BluetoothLeService.STATE_CONNECTED) {
                    mColors.set(i, Color.GREEN);
                    mAdapter.notifyDataSetChanged();
                    Log.d(TAG, address + "has connected!");
                }
                if (message.getState() == BluetoothLeService.STATE_DISCONNECTED) {
                    mColors.set(i, Color.RED);
                    mAdapter.notifyDataSetChanged();
                    Log.d(TAG, address + "has lost!");
                }
            }
        }
        mBluetoothLeService.readRemoteRssi();
    }

    public void onEventMainThread(RssiMessage message) {
        updateDeviceState(message.getAddress(), message.getRssi());
    }

}
