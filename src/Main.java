import java.io.IOException;
import java.net.SocketException;

/**
 * args[0]      p2p
 * args[1]      -receive        -serve
 * args[2]      filename        -name
 * args[3]      filename
 * args[4]      -path
 * args[5]      path_to_file
 * examples:
 * p2p -serve -name hello.txt -path /home/amirphl/hello.txt ( server side )
 * p2p -receive hello.txt ( client side )
 * Created by amirphl on 12/17/2018.
 */
public class Main {
    public static void main(String[] args) throws SocketException {
        try {
            if (args[0].equals(Constraints.PtoP)) {
                if (args[1].equals(Constraints.RECEIVE)) {
                    Receiver receiver = new Receiver();
                    try {
                        byte[] content = receiver.download(args[2]);
                        System.out.println("Content of file " + args[2]);
                        System.out.println(new String(content, "UTF-8"));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    try {
                        Sender sender = new Sender(args[3], args[5]);
                        sender.start();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } else {
                //TODO client server
            }
        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
        }
    }
}
