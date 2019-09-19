import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.ByteBuffer;

/**
 * Implementing a peer(server) for sending file to other peers.
 * It is implemented like a FIFO queue.
 * Created by amirphl on 12/17/2018.
 */
public class Sender extends Thread {
    private DatagramSocket ds = new DatagramSocket(Constraints.SEND_PORT);
    private String file_for_share_name;
    private String path_to_file;

    Sender(String file_for_share_name, String path_to_file) throws IOException {
        assert file_for_share_name != null;
        assert path_to_file != null;
        this.file_for_share_name = file_for_share_name;
        this.path_to_file = path_to_file;
    }

    public void run() {
        long start = System.currentTimeMillis();
        while (System.currentTimeMillis() - start < Constraints.LIFE_TIME) {
            Tuple t = null;
            try {
                t = receive_request();
            } catch (IOException e) {
                e.printStackTrace();
            }
            assert t != null;
            byte[] request = (byte[]) t.x;
            InetAddress address = (InetAddress) t.y;
            int port = (int) t.z;
            int size = (int) request[0];
            byte[] request_file_name = new byte[size];
            System.arraycopy(request, 1, request_file_name, 0, size);
            if (file_for_share_name.equals(new String(request_file_name)))
                try {
                    send(address, port);
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
    }

    private void send(InetAddress receiver_address, int port) throws IOException {
        byte[] buffer = new byte[Constraints.PACKET_SIZE];
        File file = new File(path_to_file);
        BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
        byte offset = 0;
        int size = (int) file.length();
        byte[] size_array = ByteBuffer.allocate(4).putInt(size).array();
        while ((bis.read(buffer, 5, Constraints.PACKET_SIZE - 5)) != -1) {
            buffer[0] = offset++;
            System.arraycopy(size_array, 0, buffer, 1, 4);
            DatagramPacket dp = new DatagramPacket(buffer, 0, buffer.length, receiver_address, port);
            ds.send(dp);
            System.out.println("offset " + buffer[0]);
        }
    }

    private Tuple<byte[], InetAddress, Integer> receive_request() throws IOException {
        byte[] receive = new byte[Constraints.PACKET_SIZE];
        DatagramPacket dpReceive = new DatagramPacket(receive, Constraints.PACKET_SIZE);
        ds.receive(dpReceive);
        InetAddress ia = dpReceive.getAddress();
        int port = dpReceive.getPort();
        return new Tuple<>(receive, ia, port);
    }

    class Tuple<X, Y, Z> {
        final X x;
        final Y y;
        final Z z;

        Tuple(X x, Y y, Z z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }
    }
}
