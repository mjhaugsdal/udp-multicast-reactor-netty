package io.github.mjhaugsdal.client;


import io.netty.channel.ChannelOption;
import io.netty.handler.codec.DatagramPacketDecoder;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.netty.udp.UdpServer;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

@Component
public class MulticastReceiver {

    @Value("${udp.port}")
    int udpPort;

    @Value("${udp.host}")
    String udpHost;

    @PostConstruct
    void setup() throws Exception {
        var group = InetAddress.getByName(udpHost);
        var ifaces = NetworkInterface.getNetworkInterfaces();

        UdpServer.create()
                .option(ChannelOption.SO_REUSEADDR, true)
                .bindAddress(() -> new InetSocketAddress(udpPort))
                .doOnBound(connection -> connection.addHandlerLast(new DatagramPacketDecoder(new MyDecoder())))
                .handle((in, out) -> {
                    Flux.<NetworkInterface>generate(s -> {
                                if (ifaces.hasMoreElements()) {
                                    s.next(ifaces.nextElement());
                                } else {
                                    s.complete();
                                }
                            }).flatMap(iface -> {
                                if (isMulticastEnabledIPv4Interface(iface)) {
                                    return in.join(group,
                                            iface);
                                }
                                return Flux.empty();
                            })
                            .log()
                            .thenMany(in.receiveObject().cast(MyPacket.class))
                            .subscribe(myPacket -> {
                                var header = myPacket.getHeader();
                                var payload = myPacket.getPayload();
                                System.out.println(new String(myPacket.getPayload()));
                            });
                    return Flux.never();
                }).bind().block();
    }


    private boolean isMulticastEnabledIPv4Interface(NetworkInterface iface) {
        try {
            if (!iface.supportsMulticast() || !iface.isUp()) {
                return false;
            }
        } catch (SocketException se) {
            return false;
        }

        // Suppressed "JdkObsolete", usage of Enumeration is deliberate
        for (Enumeration<InetAddress> i = iface.getInetAddresses(); i.hasMoreElements(); ) {
            InetAddress address = i.nextElement();
            if (address.getClass() == Inet4Address.class) {
                return true;
            }
        }
        return false;
    }
}
