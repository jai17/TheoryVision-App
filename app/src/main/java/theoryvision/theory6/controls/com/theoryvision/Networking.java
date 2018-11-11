package theoryvision.theory6.controls.com.theoryvision;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import edu.wpi.first.wpilibj.networktables.NetworkTable;
import edu.wpi.first.wpilibj.networktables.NetworkTablesJNI;

/**
 *
 * Class in which Network Tables and UDP Connection is setup to send necessary data to Roborio
 * Inputs accessed through sendData methods (sendDataUDP etc.)
 *
 * @author RickHansenRobotics
 * @since 2017-07-28
 *
 */

class Networking {

    private static final String TAG = "NetworkingFilter";

    public static String ROBORIO_ADDRESS = "192.168.42.44";
    private static final int DATA_PORT = 5808;

    private static DatagramSocket dataSocket = null;
    private static InetAddress roboRioAddress = null;

    private static boolean udpConnected = false;

    private static NetworkTable table;

    static void initNetworkTables(String tableName){
            NetworkTable.setClientMode();
            NetworkTable.setIPAddress(ROBORIO_ADDRESS);
            NetworkTable.initialize();

            table = NetworkTable.getTable(tableName);
    }

    static void initUDPServer(){
        try{
            dataSocket = new DatagramSocket();
        }
        catch (SocketException e){
            Log.d(TAG, e.getMessage());
        }

        try {
            roboRioAddress = InetAddress.getByName(ROBORIO_ADDRESS);
        } catch (UnknownHostException e) {
            Log.d(TAG, e.getMessage());
        }

    }

    static void reconnectNetworkTable(){
        NetworkTable.shutdown();
        NetworkTable.setIPAddress(ROBORIO_ADDRESS);
        NetworkTable.initialize();
    }

    static void stopNetworkTables(){
        NetworkTable.shutdown();
    }

    static void sendNumberNT(String key, double number){
        table.putNumber(key, number);
    }

    static void sendStringNT(String key, String input){
        table.putString(key, input);
    }

    static void sendBooleanNT(String key, boolean state){

        table.putBoolean(key, state);
    }

    static void sendNumberArray(String key, double[] array){
        table.putNumberArray(key, array);
    }

    static double getNumberNT(String key){
        return table.getNumber(key, 0.0);
    }

    static boolean isConnectedNT(){
        return NetworkTablesJNI.getConnections().length > 0;
    }

    static boolean isConnectedUDP(){
        return udpConnected;
    }

    static void sendSingleDataUDP(String message){
        int msg_length = message.length();
        byte[] msg = message.getBytes();
        DatagramPacket packet = new DatagramPacket(msg, msg_length, roboRioAddress, DATA_PORT);
        try {
            dataSocket.send(packet);
            udpConnected = true;
        } catch (IOException e) {
            Log.d(TAG, e.getMessage());
        }
    }

    static void sendDataUDP(double angle_h, double skewRatio, double sysTime) {
        JSONObject json = new JSONObject();
        try {
            json.put("Horizontal Angle", angle_h);
            //json.put("Vertical Angle", angle_v);
            json.put("Skew Ratio", skewRatio);
            json.put("System Time", sysTime);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String messageToSend = json.toString();
        int msg_length = messageToSend.length();
        byte[] msg = messageToSend.getBytes();
        DatagramPacket packet = new DatagramPacket(msg, msg_length, roboRioAddress, DATA_PORT);
        try {
            dataSocket.send(packet);
            udpConnected = true;
        } catch (IOException e) {
            Log.d(TAG, e.getMessage());
            udpConnected = false;
        }
    }

    static void sendDataUDP(JSONObject json) {

        String messageToSend = json.toString();
        int msg_length = messageToSend.length();
        byte[] msg = messageToSend.getBytes();
        DatagramPacket packet = new DatagramPacket(msg, msg_length, roboRioAddress, DATA_PORT);
        try {
            dataSocket.send(packet);
            udpConnected = true;
        } catch (IOException e) {
            Log.d(TAG, e.getMessage());
            udpConnected = false;
        }
    }
}
