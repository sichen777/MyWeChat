package com.example.hp.mywechat;


import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.view.View;
import android.support.v4.app.Fragment;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import java.io.IOException;

//public class MainActivity extends AppCompatActivity implements OnClickListener{
public class MainActivity  extends FragmentActivity implements View.OnClickListener{
    private Fragment mTab01 = new wexinFragment();
    private Fragment mTab02 = new frdFragment();
    private Fragment mTab03 = new contactFragment();
    private Fragment mTab04 = new settingsFragment();

//    private android.app.FragmentManager fm;
    private FragmentManager fm;

    private LinearLayout mTabWeixin;
    private LinearLayout mTabFrd;
    private LinearLayout mTabAddress;
    private LinearLayout mTabSettings;

    private ImageButton mImgWeixin;
    private ImageButton mImgFrd;
    private ImageButton mImgAddress;
    private ImageButton mImgSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        initView();
        initFragment();
        initEvent();
        setSelect(0);
    }

    private void initFragment(){
        fm =getSupportFragmentManager();
        FragmentTransaction transaction =fm.beginTransaction();
        transaction.add(R.id.id_content, mTab01);
        transaction.add(R.id.id_content, mTab02);
        transaction.add(R.id.id_content, mTab03);
        transaction.add(R.id.id_content, mTab04);
        transaction.commit();

    }

    private void initEvent(){
        mImgWeixin.setOnClickListener(this);
        mImgFrd.setOnClickListener(this);
        mImgAddress.setOnClickListener(this);
        mImgSettings.setOnClickListener(this);

    }
    private void initView(){
        mTabWeixin=(LinearLayout)findViewById(R.id.id_tab_weixin);
        mTabFrd=(LinearLayout)findViewById(R.id.id_tab_frd);
        mTabWeixin=(LinearLayout)findViewById(R.id.id_tab_contact);
        mTabWeixin=(LinearLayout)findViewById(R.id.id_tab_settings);

        mImgWeixin=(ImageButton)findViewById(R.id.id_tab_weixin_img);
        mImgFrd=(ImageButton)findViewById(R.id.id_tab_frd_img);
        mImgAddress=(ImageButton)findViewById(R.id.id_tab_contact_img);
        mImgSettings=(ImageButton)findViewById(R.id.id_tab_settings_img);

    }
    private  void setSelect(int i){
       FragmentTransaction transaction =fm.beginTransaction();
       hideFragment(transaction);
        switch (i){
            case 0:

                transaction.show(mTab01);
                mImgWeixin.setImageResource(R.drawable.tab_weixin_pressed);
                break;
            case 1:
                transaction.show(mTab02);
                mImgFrd.setImageResource(R.drawable.tab_find_frd_pressed);
                break;
            case 2:
                transaction.show(mTab03);
                mImgAddress.setImageResource(R.drawable.tab_address_pressed);
                break;
            case 3:
                transaction.show(mTab04);
                mImgSettings.setImageResource(R.drawable.tab_settings_pressed);
                break;
            case R.id.id_add_img:
                final View addDialog=getLayoutInflater().inflate(R.layout.add_contact,null);
                new AlertDialog.Builder(MainActivity.this).setView(addDialog).setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        EditText mname=(EditText)addDialog.findViewById(R.id.name);
                        EditText mphone=(EditText)addDialog.findViewById(R.id.phone);
                        final String name=mname.getText().toString();
                        final String phone=mphone.getText().toString();
                        ContentValues values = new ContentValues();
                        Uri rawContactUri = getContentResolver().insert(ContactsContract.RawContacts.CONTENT_URI, values);
                        long rawContactId = ContentUris.parseId(rawContactUri);
                        values.clear();
                        values.put(Data.RAW_CONTACT_ID, rawContactId);
                        values.put(Data.MIMETYPE, StructuredName.CONTENT_ITEM_TYPE);
                        values.put(StructuredName.GIVEN_NAME, name);
                        getContentResolver().insert(ContactsContract.Data.CONTENT_URI, values);
                        values.clear();
                        values.put(Data.RAW_CONTACT_ID, rawContactId);
                        values.put(Data.MIMETYPE, Phone.CONTENT_ITEM_TYPE);
                        values.put(Phone.NUMBER, phone);
                        values.put(Phone.TYPE, Phone.TYPE_MOBILE);
                        getContentResolver().insert(ContactsContract.Data.CONTENT_URI, values);
                        values.clear();
                        selectFragment(2);
                        Toast.makeText(MainActivity.this, "联系人添加成功", Toast.LENGTH_SHORT).show();
                    }
                }).show();
                break;

            default:
                break;
        }
        transaction.commit();

    }

    private void hideFragment(FragmentTransaction transaction){
        transaction.hide(mTab01);
        transaction.hide(mTab02);
        transaction.hide(mTab03);
        transaction.hide(mTab04);
    }

    public void onClick(View v){
        resetImgs();
        switch (v.getId()){
            case R.id.id_tab_weixin:
                setSelect(0);
                break;
            case R.id.id_tab_frd:
                setSelect(1);
                break;
            case R.id.id_tab_contact:
                setSelect(2);
                break;
            case R.id.id_tab_settings:
                setSelect(3);
                break;
            default:
                break;
        }
    }

    private void resetImgs(){
        mImgWeixin.setImageResource(R.drawable.tab_weixin_normal);
        mImgFrd.setImageResource(R.drawable.tab_find_frd_normal);
        mImgAddress.setImageResource(R.drawable.tab_address_normal);
        mImgSettings.setImageResource(R.drawable.tab_settings_normal);

    }
    /*
    本程序ChatService是蓝牙会话的服务程序
    定义了3个内部类：AcceptThread（接受新连接）、ConnectThread（发出连接）和ConnectedThread （已连接）
*/
    public class ChatService {
        //本应用的主Activity组件名称
        private static final String NAME = "BluetoothChat";
        // UUID：通用唯一识别码,是一个128位长的数字，一般用十六进制表示
        //算法的核心思想是结合机器的网卡、当地时间、一个随机数来生成
        //在创建蓝牙连接
        private static final UUID MY_UUID = UUID.fromString("fa87c0d0-afac-11de-8a39-0800200c9a66");
        private final BluetoothAdapter mAdapter;
        private final Handler mHandler;
        private AcceptThread mAcceptThread;
        private ConnectThread mConnectThread;
        private ConnectedThread mConnectedThread;
        private int mState;
        public static final int STATE_NONE = 0;
        public static final int STATE_LISTEN = 1;
        public static final int STATE_CONNECTING = 2;
        public static final int STATE_CONNECTED = 3;

        //构造方法，接收UI主线程传递的对象
        public ChatService(Context context, Handler handler) {
            //构造方法完成蓝牙对象的创建
            mAdapter = BluetoothAdapter.getDefaultAdapter();
            mState = STATE_NONE;
            mHandler = handler;
        }

        private synchronized void setState(int state) {
            mState = state;
            mHandler.obtainMessage(BluetoothChat.MESSAGE_STATE_CHANGE, state, -1).sendToTarget();
        }

        public synchronized int getState() {
            return mState;
        }

        public synchronized void start() {
            if (mConnectThread != null) {
                mConnectThread.cancel();
                mConnectThread = null;
            }
            if (mConnectedThread != null) {
                mConnectedThread.cancel();
                mConnectedThread = null;
            }
            if (mAcceptThread == null) {
                mAcceptThread = new AcceptThread();
                mAcceptThread.start();
            }
            setState(STATE_LISTEN);
        }

        //取消 CONNECTING 和 CONNECTED 状态下的相关线程，然后运行新的 mConnectThread 线程
        public synchronized void connect(BluetoothDevice device) {
            if (mState == STATE_CONNECTING) {
                if (mConnectThread != null) {
                    mConnectThread.cancel();
                    mConnectThread = null;
                }
            }
            if (mConnectedThread != null) {
                mConnectedThread.cancel();
                mConnectedThread = null;
            }
            mConnectThread = new ConnectThread(device);
            mConnectThread.start();
            setState(STATE_CONNECTING);
        }

        /*
            开启一个 ConnectedThread 来管理对应的当前连接。之前先取消任意现存的 mConnectThread 、
            mConnectedThread 、 mAcceptThread 线程，然后开启新 mConnectedThread ，传入当前刚刚接受的
            socket 连接。最后通过 Handler来通知UI连接
         */
        public synchronized void connected(BluetoothSocket socket, BluetoothDevice device) {
            if (mConnectThread != null) {
                mConnectThread.cancel();
                mConnectThread = null;
            }
            if (mConnectedThread != null) {
                mConnectedThread.cancel();
                mConnectedThread = null;
            }
            if (mAcceptThread != null) {
                mAcceptThread.cancel();
                mAcceptThread = null;
            }
            mConnectedThread = new ConnectedThread(socket);
            mConnectedThread.start();
            Message msg = mHandler.obtainMessage(BluetoothChat.MESSAGE_DEVICE_NAME);
            Bundle bundle = new Bundle();
            bundle.putString(BluetoothChat.DEVICE_NAME, device.getName());
            msg.setData(bundle);
            mHandler.sendMessage(msg);
            setState(STATE_CONNECTED);
        }

        // 停止所有相关线程，设当前状态为 NONE
        public synchronized void stop() {
            if (mConnectThread != null) {
                mConnectThread.cancel();
                mConnectThread = null;
            }
            if (mConnectedThread != null) {
                mConnectedThread.cancel();
                mConnectedThread = null;
            }
            if (mAcceptThread != null) {
                mAcceptThread.cancel();
                mAcceptThread = null;
            }
            setState(STATE_NONE);
        }

        // 在 STATE_CONNECTED 状态下，调用 mConnectedThread 里的 write 方法，写入 byte
        public void write(byte[] out) {
            ConnectedThread r;
            synchronized (this) {
                if (mState != STATE_CONNECTED)
                    return;
                r = mConnectedThread;
            }
            r.write(out);
        }

        // 连接失败的时候处理，通知 ui ，并设为 STATE_LISTEN 状态
        private void connectionFailed() {
            setState(STATE_LISTEN);
            Message msg = mHandler.obtainMessage(BluetoothChat.MESSAGE_TOAST);
            Bundle bundle = new Bundle();
            bundle.putString(BluetoothChat.TOAST, "链接不到设备");
            msg.setData(bundle);
            mHandler.sendMessage(msg);
        }

        // 当连接失去的时候，设为 STATE_LISTEN 状态并通知 ui
        private void connectionLost() {
            setState(STATE_LISTEN);
            Message msg = mHandler.obtainMessage(BluetoothChat.MESSAGE_TOAST);
            Bundle bundle = new Bundle();
            bundle.putString(BluetoothChat.TOAST, "设备链接中断");
            msg.setData(bundle);
            mHandler.sendMessage(msg);
        }

        // 创建监听线程，准备接受新连接。使用阻塞方式，调用 BluetoothServerSocket.accept()
        private class AcceptThread extends Thread {
            private final BluetoothServerSocket mmServerSocket;

            public AcceptThread() {
                BluetoothServerSocket tmp = null;
                try {
                    //使用射频端口（RF comm）监听
                    tmp = mAdapter.listenUsingRfcommWithServiceRecord(NAME, MY_UUID);
                } catch (IOException e) {
                }
                mmServerSocket = tmp;
            }

            @Override
            public void run() {
                setName("AcceptThread");
                BluetoothSocket socket = null;
                while (mState != STATE_CONNECTED) {
                    try {
                        socket = mmServerSocket.accept();
                    } catch (IOException e) {
                        break;
                    }
                    if (socket != null) {
                        synchronized (ChatService.this) {
                            switch (mState) {
                                case STATE_LISTEN:
                                case STATE_CONNECTING:
                                    connected(socket, socket.getRemoteDevice());
                                    break;
                                case STATE_NONE:
                                case STATE_CONNECTED:
                                    try {
                                        socket.close();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                    break;
                            }
                        }
                    }
                }
            }

            public void cancel() {
                try {
                    mmServerSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        /*
            连接线程，专门用来对外发出连接对方蓝牙的请求和处理流程。
            构造函数里通过 BluetoothDevice.createRfcommSocketToServiceRecord() ，
            从待连接的 device 产生 BluetoothSocket. 然后在 run 方法中 connect ，
            成功后调用 BluetoothChatSevice 的 connected() 方法。定义 cancel() 在关闭线程时能够关闭相关socket 。
         */
        private class ConnectThread extends Thread {
            private final BluetoothSocket mmSocket;
            private final BluetoothDevice mmDevice;

            public ConnectThread(BluetoothDevice device) {
                mmDevice = device;
                BluetoothSocket tmp = null;
                try {
                    tmp = device.createRfcommSocketToServiceRecord(MY_UUID);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                mmSocket = tmp;
            }

            @Override
            public void run() {
                setName("ConnectThread");
                mAdapter.cancelDiscovery();
                try {
                    mmSocket.connect();
                } catch (IOException e) {
                    connectionFailed();
                    try {
                        mmSocket.close();
                    } catch (IOException e2) {
                        e.printStackTrace();
                    }
                    ChatService.this.start();
                    return;
                }
                synchronized (ChatService.this) {
                    mConnectThread = null;
                }
                connected(mmSocket, mmDevice);
            }

            public void cancel() {
                try {
                    mmSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        /*
            双方蓝牙连接后一直运行的线程；构造函数中设置输入输出流。
            run()方法中使用阻塞模式的 InputStream.read()循环读取输入流，然后发送到 UI 线程中更新聊天消息。
            本线程也提供了 write() 将聊天消息写入输出流传输至对方，传输成功后回写入 UI 线程。最后使用cancel()关闭连接的 socket
         */
        private class ConnectedThread extends Thread {
            private final BluetoothSocket mmSocket;
            private final InputStream mmInStream;
            private final OutputStream mmOutStream;

            public ConnectedThread(BluetoothSocket socket) {
                mmSocket = socket;
                InputStream tmpIn = null;
                OutputStream tmpOut = null;
                try {
                    tmpIn = socket.getInputStream();
                    tmpOut = socket.getOutputStream();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                mmInStream = tmpIn;
                mmOutStream = tmpOut;
            }

            @Override
            public void run() {
                byte[] buffer = new byte[1024];
                int bytes;
                while (true) {
                    try {
                        bytes = mmInStream.read(buffer);
                        mHandler.obtainMessage(BluetoothChat.MESSAGE_READ, bytes, -1, buffer).sendToTarget();
                    } catch (IOException e) {
                        connectionLost();
                        break;
                    }
                }
            }

            public void write(byte[] buffer) {
                try {
                    mmOutStream.write(buffer);
                    mHandler.obtainMessage(BluetoothChat.MESSAGE_WRITE, -1, -1, buffer).sendToTarget();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            public void cancel() {
                try {
                    mmSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }





}
