import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Comparator;

/**
 * Created by amirphl on 12/17/2018.
 */
public class Sender {
    private DatagramSocket ds = new DatagramSocket(Constraints.RECEIVE_PORT);

    public Sender(String filename) throws IOException {
        InetAddress address = InetAddress.getByName(Constraints.BROAD_CAST_IP);
        ds.setBroadcast(true);
        ds.connect(address, Constraints.SEND_PORT);
        DatagramPacket dpSend = new DatagramPacket(filename.getBytes(), filename.getBytes().length, address, Constraints.SEND_PORT);
        ds.send(dpSend);
    }

    public byte[] receiveFile() throws IOException {
        long start = System.currentTimeMillis();
        byte[] buffer = receive();
        data.add(buffer);
        int counter = (int) buffer[1] / (Constraints.PACKET_SIZE - 2) + 1; // 2 byte for offset and size
        while (System.currentTimeMillis() - start < Constraints.LIFE_TIME && (--counter != 0)) {
            buffer = receive();
            data.add(buffer);
        }
        sort();
        return append((int) buffer[1]);
    }

    private byte[] receive() throws IOException {
        byte[] receive = new byte[Constraints.PACKET_SIZE];
        DatagramPacket DpReceive = new DatagramPacket(receive, Constraints.PACKET_SIZE);
        ds.receive(DpReceive);
        return receive;
    }

    private void sort() {
        data.sort(new Receiver.SortByOffset());
    }

    private byte[] append(int total_size) {
        byte[] result = new byte[total_size];
        int counter = 0;
        for (byte[] datum : data)
            System.arraycopy(datum, 2, result, (counter++) * (Constraints.PACKET_SIZE - 2), Constraints.PACKET_SIZE - 2);
        return result;
    }

    class SortByOffset implements Comparator<byte[]> {
        @Override
        public int compare(byte[] o1, byte[] o2) {
            return (int) o1[0] - (int) o2[0];
        }
    }
}
