package io.github.mjhaugsdal.multicast;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import jakarta.annotation.PostConstruct;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.ArrayList;
import java.util.List;

@Component
public class MulticastPublisher {

    @Value("${udp.port}")
    int udpPort;

    @Value("${udp.host}")
    String udpHost;

    ByteBuf message;


    @PostConstruct
    void start() throws IOException, InterruptedException {
        var group = InetAddress.getByName(udpHost);
        try (var socket = new MulticastSocket(udpPort)) {
            while (true) {

//                var is = MulticastPublisher.class.getClassLoader().getResourceAsStream("3KiB.bin");
//                assert is != null;
//                var bytes = is.readAllBytes();
                var testValue = "Hello world!";
//                message = Unpooled.copiedBuffer(testValue.getBytes());

                Thread.sleep(1000);

//                var split = split(message, group, udpPort);

                socket.send(new DatagramPacket(testValue.getBytes(), testValue.length(), group, udpPort));

//                .forEach(datagramPacket -> {
//                    try {
//                        socket.send(datagramPacket);
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                        throw new RuntimeException(e);
//                    }
//                });

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    List<DatagramPacket> split(ByteBuf message, InetAddress group, int udpPort) {

//        var id = 1234;
        var list = new ArrayList<DatagramPacket>();
        while (message.readableBytes() > 1024) {
            var bytes = new byte[1024];
            message.readBytes(bytes);
            byte[] both = ArrayUtils.addAll(String.valueOf(1024).getBytes(), bytes);

            list.add(new DatagramPacket(both, 1028, group, udpPort));
        }

        if (message.readableBytes() > 0) {
            var remainder = message.readableBytes();
            var bytes = new byte[remainder];
            message.readBytes(bytes);
            byte[] both = ArrayUtils.addAll(String.valueOf(remainder).getBytes(), bytes);
            list.add(new DatagramPacket(both, remainder + 4, group, udpPort));
        }
        return list;
    }
}
