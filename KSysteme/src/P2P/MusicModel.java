package P2P;

/**
 *  Datenmodell eines Musikstückes zum Abspeichern von Titel und Interpret
 *
 *  @author Dustin Kröger [3728859]
 *  @version 1.0
 */
public class MusicModel {

    private String _artist;
    private String _title;

    /**
     * Konstruktor des Datenmodells. Interpret und Titel werden übergeben und initial zugewiesen.
     * @param artist Interpret des Musikstücks
     * @param title Titel des Musikstücks
     */
    public MusicModel(String artist, String title) {

        _artist = artist;
        _title = title;
    }

    /**
     * Getter des Interprets
     * @return Interpret als String
     */
    public String getArtist() {

        return _artist;
    }

    /**
     * Getter des Titels
     * @return Titel als String
     */
    public String getTitle() {

        return _title;
    }

    /**
     * Setter des Interprets
     * @param value Interpret als String
     */
    public void setArtist(String value) {

        // Behalten des alten Wertes falls leer
        _artist = value.trim().equals("") ? _artist : value;
    }

    /**
     * Setter des Titels
     * @param value Titel als String
     */
    public void setTitle(String value) {

        // Behalten des alten Wertes falls leer
        _title = value.trim().equals("") ? _title : value;
    }

    /**
     * Überschriebene toString-Methode. Gibt das jeweilige Musikstück als CSV zurück
     * @return Lied als CSV in der Form: Interpret;Titel;
     */
    @Override
    public String toString() {

        return _artist + ";" + _title + ";";
    }
}
