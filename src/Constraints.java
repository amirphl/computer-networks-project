/**
 * Created by amirphl on 12/17/2018.
 */
public class Constraints {
    static final int SEND_PORT = 15001;
    static final int RECEIVE_PORT = 16001;
    static final int PACKET_SIZE = 1005; // 1005 bytes
    static final long LIFE_TIME = 100000000;//millisecond
    static final String BROAD_CAST_IP = "255.255.255.255";
    static final int OFFSET_BYTES = 1;//maximum number of packets to send
    static final int MAXIMUM_DATA_LENGTH = 4;
    static final String PtoP = "p2p";
    static final String RECEIVE = "-receive";
    static final String SERVE = "-serve";
    static final String NAME = "-name";
    static final String PATH = "-path";
}
