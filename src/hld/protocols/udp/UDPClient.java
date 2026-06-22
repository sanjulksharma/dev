package hld.protocols.udp;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class UDPClient {

    static void main() throws Exception {
        DatagramSocket socket = new DatagramSocket();
        InetAddress serverAddress = InetAddress.getByName("localhost");

        String msg = "Hello UDP Server!";
        byte[] data = msg.getBytes();
        DatagramPacket packet = new DatagramPacket(data, data.length, serverAddress, 6000);

        socket.send(packet);

        byte[] buffer = new byte[1024];
        DatagramPacket response = new DatagramPacket(buffer, buffer.length);
        socket.receive(response);

        System.out.println("Server replied: " + new String(response.getData(), 0, response.getLength()));

        socket.close();
    }

}
