package P2P.Test;

import P2P.MusicModel;
import P2P.MusicShareClient;
import java.util.ArrayList;

/**
 * Testklasse 3 für MusicShareClient
 * @author Dustin Kröger [3728859]
 */
public class Peer3 {

    public static void main( String[] args ) {

        ArrayList<MusicModel> ml = new ArrayList<>();
        ml.add(new MusicModel("Hallo5", "Welt5"));
        ml.add(new MusicModel("Hallo6", "Welt6"));

        MusicShareClient msc = new MusicShareClient(50003, "MusicList3");
    }
}
