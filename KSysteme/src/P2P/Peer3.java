package P2P;

import java.net.*;
import java.util.ArrayList;

/**
 * Created by aervos on 17.01.2017.
 */
public class Peer3 {

    public static void main( String[] args ) {

       /* String s = "HalloWelt;HalloWelt;HalloWelt;HalloWelt;HalloWelt;HalloWelt;";
        String[] str = s.split(";");
        System.out.println(str.length);
        for(String strin: str)
            System.out.println(":"+strin);

        if((str.length % 2) != 0) {

            System.out.println("Corrupt MusicString");
            return;
        }

        for(int i = 0; i < str.length;) {

            System.out.println(str[i++] + str[i++]);
        } */
        ArrayList<MusicModel> ml = new ArrayList<>();
        ml.add(new MusicModel("Hallo5", "Welt5"));
        ml.add(new MusicModel("Hallo6", "Welt6"));

        MusicShareClient msc = new MusicShareClient(50003, ml);
    }
}
