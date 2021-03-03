package buaa.uavswarm.udpdemo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.duke.udp.UDPReceiveHelper;
import com.duke.udp.UDPSendHelper;
import com.duke.udp.util.UDPListener;

import static buaa.uavswarm.udpdemo.ByteTransFormUtil.hexToByteArray;

public class MainActivity extends AppCompatActivity {
    public static final String TAG = "UDP_TAG";

    public static final String PARAM_CAST_TYPE = "param_cast_type";
    public static final String PARAM_SEND_PORT = "param_send_port";
    public static final String PARAM_RECEIVE_PORT = "param_receive_port";
    public static final String PARAM_IP = "param_ip";

    protected Context mContext;
    public byte[] recievepacket;

    private int sendPort = 20005;
    private int receivePort = 20005;
    private String ip = "224.1.1.1";

    private UDPReceiveHelper udpHelper;
    private UDPSendHelper udpSendHelper;

    private Button buttonSend;
    private Button buttontakeoff;
    private Button buttonStopReceive;
    public TextView textView;

    private RecyclerView recyclerView;
    private MyAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
        mContext=this;
        udpHelper = new UDPReceiveHelper(this,receivePort, ip);
        udpHelper.setUDPListener(udpListener);
        udpHelper.start();
    }

    private void initViews() {
        textView = findViewById(R.id.textView);

        buttonSend = findViewById(R.id.button_send);
        buttonStopReceive = findViewById(R.id.button_stop_receive);
        buttontakeoff = findViewById(R.id.button_takeoff);
        recyclerView = findViewById(R.id.recycler_view);

        adapter = new MyAdapter();
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                udpSendHelper=new UDPSendHelper(mContext,sendPort,ip);
                udpSendHelper.setUDPListener(udpListener);
                byte[] bytes=new byte[25];
                bytes[0]= (byte) 0xee;
                bytes[1]=(byte) 0x16;
                bytes[2]= (byte) 0xa5;
                udpSendHelper.bytes=bytes;
                udpSendHelper.start();
            }
        });
        buttonStopReceive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                udpHelper.stopReceive();
            }
        });
        buttontakeoff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                udpSendHelper=new UDPSendHelper(mContext,sendPort,ip);
                udpSendHelper.setUDPListener(udpListener);
                byte[] bytes=new byte[25];
                bytes[0]= (byte) 0xee;
                bytes[1]=(byte) 0x16;
                bytes[2]= (byte) 0xa5;
                udpSendHelper.bytes=bytes;
                udpSendHelper.start();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        udpHelper.onDestroy();
    }

    private UDPListener udpListener = new UDPListener() {
        @Override
        public void onError(String error) {
            buttonStopReceive.setBackgroundColor(Color.RED);
            Toast.makeText(MainActivity.this, "错误：" + error, Toast.LENGTH_SHORT).show();
            Log.v(TAG, error);
        }

        @Override
        public void onReceive(String content) {
            adapter.addItemToHead("收到内容：" + content);
            recievepacket = hexToByteArray(content);
            Log.v(TAG, content);
        }

        @Override
        public void onSend(String content) {
            Toast.makeText(MainActivity.this, "发送成功：" + content, Toast.LENGTH_SHORT).show();
            Log.v(TAG, content);
        }
    };


}