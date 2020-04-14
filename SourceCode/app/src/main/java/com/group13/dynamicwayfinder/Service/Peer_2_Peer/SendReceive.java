//package com.group13.dynamicwayfinder.Service.Peer_2_Peer;
//
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.OutputStream;
//import java.net.Socket;
//import java.util.logging.Handler;
//
//import static com.group13.dynamicwayfinder.Service.Peer_2_Peer.WiFiServiceDiscoveryActivity.MESSAGE_READ;
//
//public class SendReceive extends Thread{
//    private Socket socket;
//    private InputStream inputStream;
//    private OutputStream outputStream;
//
//
//    public SendReceive(Socket skt){
//        socket = skt;
//        try{
//            inputStream=socket.getInputStream();
//            outputStream=socket.getOutputStream();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    @Override
//    public void run() {
//        byte[] buffer = new byte[1024];
//        int bytes;
//
//        while(socket!=null){
//            try{
//                bytes = inputStream.read(buffer);
//                if(bytes>0)
//                {
//                    handler.obtainMessage(MESSAGE_READ,bytes,-1,buffer).sendToTarget();
//                }
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//    }
//}
