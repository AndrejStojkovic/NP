// Lab 8.1

import java.util.ArrayList;
import java.util.List;

enum PlayerState {
    NONE,
    PLAYING,
    STOPPED,
    CHANGING_SONG
}

class Song {
    String title;
    String artist;

    Song(String title, String artist) {
        this.title = title;
        this.artist = artist;
    }

    @Override
    public String toString() {
        return String.format("Song{title=%s, artist=%s}", title, artist);
    }
}

class MP3Player {
    List<Song> songs;
    int currentSong;
    PlayerState state;

    MP3Player(List<Song> songs) {
        this.songs = songs;
        this.currentSong = 0;
        this.state = PlayerState.NONE;
    }

    private void playNext() {
        if(state == PlayerState.CHANGING_SONG) {
            currentSong = (currentSong + 1) % songs.size();
        }
    }

    private void playPrevious() {
        if(state == PlayerState.STOPPED) {
            currentSong = currentSong <= 0 ? songs.size() - 1 : currentSong - 1;
        }
    }

    public void pressPlay() {
        if(state == PlayerState.PLAYING) {
            System.out.println("Song is already playing");
            return;
        }
        state = PlayerState.PLAYING;
        playNext();
        System.out.println("Song " + currentSong + " is playing");
    }

    public void pressStop() {
        if(state == PlayerState.PLAYING) {
            System.out.println("Song " + currentSong + " is paused");
            currentSong = 0;
            state = PlayerState.STOPPED;
        } else if(state == PlayerState.STOPPED || state == PlayerState.CHANGING_SONG) {
            System.out.println("Songs are stopped");
            currentSong = 0;
            state = PlayerState.NONE;
        } else {
            System.out.println("Songs are already stopped");
        }
    }

    public void pressFWD() {
        System.out.println("Forward...");
        state = PlayerState.CHANGING_SONG;
        playNext();
    }

    public void pressREW() {
        System.out.println("Reward...");
        state = PlayerState.STOPPED;
        playPrevious();
    }

    public void printCurrentSong() {
        System.out.println(songs.get(currentSong));
    }

    @Override
    public String toString() {
        return String.format("MP3Player{currentSong = %d, songList = %s}", currentSong, String.join(", ", songs.toString()));
    }
}

public class PatternTest {
    public static void main(String args[]) {
        List<Song> listSongs = new ArrayList<Song>();
        listSongs.add(new Song("first-title", "first-artist"));
        listSongs.add(new Song("second-title", "second-artist"));
        listSongs.add(new Song("third-title", "third-artist"));
        listSongs.add(new Song("fourth-title", "fourth-artist"));
        listSongs.add(new Song("fifth-title", "fifth-artist"));
        MP3Player player = new MP3Player(listSongs);


        System.out.println(player.toString());
        System.out.println("First test");


        player.pressPlay();
        player.printCurrentSong();
        player.pressPlay();
        player.printCurrentSong();

        player.pressPlay();
        player.printCurrentSong();
        player.pressStop();
        player.printCurrentSong();

        player.pressPlay();
        player.printCurrentSong();
        player.pressFWD();
        player.printCurrentSong();

        player.pressPlay();
        player.printCurrentSong();
        player.pressREW();
        player.printCurrentSong();


        System.out.println(player.toString());
        System.out.println("Second test");


        player.pressStop();
        player.printCurrentSong();
        player.pressStop();
        player.printCurrentSong();

        player.pressStop();
        player.printCurrentSong();
        player.pressPlay();
        player.printCurrentSong();

        player.pressStop();
        player.printCurrentSong();
        player.pressFWD();
        player.printCurrentSong();

        player.pressStop();
        player.printCurrentSong();
        player.pressREW();
        player.printCurrentSong();


        System.out.println(player.toString());
        System.out.println("Third test");


        player.pressFWD();
        player.printCurrentSong();
        player.pressFWD();
        player.printCurrentSong();

        player.pressFWD();
        player.printCurrentSong();
        player.pressPlay();
        player.printCurrentSong();

        player.pressFWD();
        player.printCurrentSong();
        player.pressStop();
        player.printCurrentSong();

        player.pressFWD();
        player.printCurrentSong();
        player.pressREW();
        player.printCurrentSong();


        System.out.println(player.toString());
    }
}
