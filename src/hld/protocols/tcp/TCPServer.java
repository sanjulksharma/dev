package hld.protocols.tcp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;

public class TCPServer {

    static void main() throws IOException {

        try (ServerSocket serverSocket = new ServerSocket(5001)) {
            String line;
            while (true) {
                System.out.println("Waiting for new client socket");
                try (Socket clientSocket = serverSocket.accept(); BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                     PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);) {
                    while ((line = in.readLine()) != null) {
                        System.out.println("Received: " + line);
                        out.println("Echo: " + line); // send response
                    }
                }
            }
        }
    }

}
