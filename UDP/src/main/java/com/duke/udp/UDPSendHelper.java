package com.duke.udp;

import android.annotation.SuppressLint;
import android.content.Context;

import com.duke.udp.interf.UDPSendBase;
import com.duke.udp.multicast.UDPMulticastBase;
import com.duke.udp.multicast.UDPMulticastSend;
import com.duke.udp.util.DExecutor;
import com.duke.udp.util.InnerHandler;
import com.duke.udp.util.UDPListener;
import com.duke.udp.util.UDPUtil;

import java.text.SimpleDateFormat;
import java.util.Date;

public class UDPSendHelper {
    public byte[] bytes;
    private InnerHandler handler;
    private UDPSendBase sendSocket;
    private volatile boolean isStopSend;
    private volatile long sendGap = 1000;

    /**
     * 设置发送线程的时间间隔，避免发送速度过快
     *
     * @param sendGap
     */
    private void setSendGap(long sendGap) {
        this.sendGap = sendGap;
    }

    /**
     * 停止发送消息
     */
    public void stopSend() {
        isStopSend = true;
    }

    /**
     * 设置数据回调
     *
     * @param listener
     */
    public void setUDPListener(UDPListener listener) {
        handler.setListener(listener);
    }

    public UDPSendHelper(Context context, int sendPort, String ip) {
        handler = new InnerHandler();
        sendSocket = new UDPMulticastSend(context, sendPort, ip);
    }

    public void start() {
        // 发送线程
        DExecutor.get().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    String text = null;
                    text = bytesToHex(bytes);
                    sendSocket.send(bytes);
                    handler.sendSuccess(text);
                } catch (Exception e) {
                    e.printStackTrace();
                    handler.error(e.getLocalizedMessage());
                } finally {
                    if (sendSocket instanceof UDPMulticastBase) {
                        ((UDPMulticastBase) sendSocket).closeAll();
                    }
                }
            }

        });
    }

    public void onDestroy() {
        stopSend();
        DExecutor.get().shutdownNow();
    }

    public static String bytesToHex(byte[] bytes) {
        StringBuffer sb = new StringBuffer();
        for(int i = 0; i < bytes.length; i++) {
            String hex = Integer.toHexString(bytes[i] & 0xFF);
            if(hex.length() < 2){
                sb.append(0);
            }
            sb.append(hex);
        }
        return sb.toString();
    }


}
