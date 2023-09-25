package io.github.mjhaugsdal.client;

public class MyPacket {

    private String header;

    public String getHeader() {
        return header;
    }

    public byte[] getPayload() {
        return payload;
    }

    private byte[] payload;

    public MyPacket(String header, byte[] payload) {
        this.header = header;
        this.payload = payload;
    }
}
