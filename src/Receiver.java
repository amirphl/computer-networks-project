import java.io.IOException;
import java.net.*;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Comparator;

/**
 * Implementing a receiver peer(client)
 * Created by amirphl on 12/17/2018.
 */
public class Receiver {
    private DatagramSocket ds = new DatagramSocket(Constraints.RECEIVE_PORT);
    private ArrayList<byte[]> data = new ArrayList<>();

    public Receiver() throws SocketException {
    }

    public byte[] download(String filename) throws IOException {
        InetAddress address = InetAddress.getByName(Constraints.BROAD_CAST_IP);
        ds.setBroadcast(true);
        ds.connect(address, Constraints.SEND_PORT);
        byte[] filename_bytes = filename.getBytes();
        byte[] request = new byte[filename_bytes.length + 1];
        request[0] = (byte) filename.getBytes().length;
        System.arraycopy(filename_bytes, 0, request, 1, filename_bytes.length);
        DatagramPacket dpSend = new DatagramPacket(request, request.length, address, Constraints.SEND_PORT);
        ds.send(dpSend);
        return receiveFile();
    }

    private byte[] receiveFile() throws IOException {
        long start = System.currentTimeMillis();
        byte[] buffer = receive();
        data.add(buffer);
        byte[] size = new byte[4];
        System.arraycopy(buffer, 1, size, 0, 4);
        int counter = ByteBuffer.wrap(size).getInt() / (Constraints.PACKET_SIZE - 5) + 1; // 1 byte for offset , 4 bytes for size
        while (System.currentTimeMillis() - start < Constraints.LIFE_TIME && (--counter != 0)) {
            buffer = receive();
            data.add(buffer);
        }
        sort();
        return append(ByteBuffer.wrap(size).getInt());
    }

    private byte[] receive() throws IOException {
        byte[] receive = new byte[Constraints.PACKET_SIZE];
        DatagramPacket DpReceive = new DatagramPacket(receive, Constraints.PACKET_SIZE);
        ds.receive(DpReceive);
        return receive;
    }

    private void sort() {
        data.sort(new SortByOffset());
    }

    private byte[] append(int total_size) {
        byte[] result = new byte[total_size];
        int counter = 0;
        for (byte[] datum : data)
            System.arraycopy(datum, 5, result, (counter++) * (Constraints.PACKET_SIZE - 5), Constraints.PACKET_SIZE - 5);
        return result;
    }

    class SortByOffset implements Comparator<byte[]> {
        @Override
        public int compare(byte[] o1, byte[] o2) {
            return (int) o1[0] - (int) o2[0];
        }
    }
}
