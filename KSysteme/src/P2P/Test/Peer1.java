package P2P.Test;

import P2P.MusicModel;
import P2P.MusicShareClient;
import java.util.ArrayList;

/**
 * Testklasse 1 für MusicShareClient
 * @author Dustin Kröger [3728859]
 */
public class Peer1 {

    public static void main( String[] args ) {

        ArrayList<MusicModel> ml = new ArrayList<>();
        ml.add(new MusicModel("Hallo3", "Welt3"));
        ml.add(new MusicModel("Hallo4", "Welt4"));

        MusicShareClient msc = new MusicShareClient(50004, "MusicList1");

    }
}