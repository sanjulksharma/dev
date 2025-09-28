package hld.protocols.udp;

import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class UDPServer {

    static void main() throws Exception {
        byte[] buffer = new byte[1024];

        System.out.println("UDP Server is listening...");

        while (true) {
            try (DatagramSocket socket = new DatagramSocket(6000)) {
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet); // wait for data

                String msg = new String(packet.getData(), 0, packet.getLength());
                System.out.println("Received: " + msg);

                // send reply
                String response = "Echo: " + msg;
                byte[] respData = response.getBytes();
                DatagramPacket respPacket = new DatagramPacket(
                        respData, respData.length,
                        packet.getAddress(), packet.getPort()
                );
                socket.send(respPacket);
            }

        }
    }
}
