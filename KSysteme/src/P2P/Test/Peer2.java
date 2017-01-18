package P2P.Test;

import P2P.MusicModel;
import P2P.MusicShareClient;
import java.util.ArrayList;


/**
 * Testklasse 2 für MusicShareClient
 * @author Dustin Kröger [3728859]
 */
public class Peer2 {

    public static void main( String[] args ) {

        ArrayList<MusicModel> ml = new ArrayList<>();
        ml.add(new MusicModel("Hallo", "Welt"));
        ml.add(new MusicModel("Hallo2", "Welt2"));

        MusicShareClient msc = new MusicShareClient(50001, "MusicList2");

    }
}
