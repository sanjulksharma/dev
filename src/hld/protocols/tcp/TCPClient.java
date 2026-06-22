package hld.protocols.tcp;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.time.Duration;
import java.util.Date;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class TCPClient {

    static void main() throws IOException {
        AtomicBoolean shutDown = new AtomicBoolean(false);
        try (Socket socket = new Socket("localhost", 5001);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             final BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
            Flux.interval(Duration.ofSeconds(1)).doOnNext(tick -> {
                try {
                    out.println("Hello Server! " + tick + " " + new Date()) ;
                    System.out.println("Server replied: " + in.readLine());
                } catch (IOException e) {
                    e.printStackTrace();
                    shutDown.set(true);
                    throw new RuntimeException(e);
                }
            }).subscribe();
            while (!shutDown.get()) {

            }
        }


    }
}
