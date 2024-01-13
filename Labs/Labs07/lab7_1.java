// Lab 7.1

import com.sun.source.tree.Tree;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.TreeSet;
import java.util.stream.Collectors;

class NoSuchRoomException extends Exception {
    NoSuchRoomException(String roomName) {
        super(roomName + " does not exist.");
    }
}

class NoSuchUserException extends Exception {
    NoSuchUserException(String username) {
        super(username + " does not exist.");
    }
}

class ChatRoom implements Comparable<ChatRoom> {
    String name;
    TreeSet<String> users;

    ChatRoom() {
        name = "ChatRoom";
        users = new TreeSet<>();
    }

    ChatRoom(String name) {
        this.name = name;
        users = new TreeSet<>();
    }

    public void addUser(String username) {
        users.add(username);
    }

    public void removeUser(String username) {
        users.remove(username);
    }

    public boolean hasUser(String username) {
        return users.contains(username);
    }

    public String getName() {
        return name;
    }

    public int numUsers() {
        return users.size();
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();
        str.append(name).append("\n");
        if(users.isEmpty()) {
            str.append("EMPTY").append("\n");
        } else {
            users.forEach(x -> str.append(x).append("\n"));
        }
        return str.toString();
    }

    @Override
    public int compareTo(ChatRoom o) {
        return Integer.compare(users.size(), o.users.size());
    }

    public int sort(ChatRoom o) {
        return name.compareTo(o.name);
    }
}

class ChatSystem {
    Map<String, ChatRoom> rooms;
    Set<String> registeredUsers;

    ChatSystem() {
        rooms = new TreeMap<>();
        registeredUsers = new HashSet<>();
    }

    public void addRoom(String roomName) {
        rooms.put(roomName, new ChatRoom(roomName));
    }

    public void removeRoom(String roomName) {
        rooms.remove(roomName);
    }

    public ChatRoom getRoom(String roomName) throws NoSuchRoomException {
        ChatRoom room = rooms.get(roomName);
        if(room == null) {
            throw new NoSuchRoomException(roomName);
        }
        return rooms.get(roomName);
    }

    public void register(String username) {
        registeredUsers.add(username);
        rooms.values().stream()
                .min(Comparator.comparingInt(ChatRoom::numUsers).
                thenComparing(ChatRoom::getName)).ifPresent(x -> x.addUser(username));
    }

    public boolean isUserRegistered(String username) {
        return registeredUsers.contains(username);
    }

    public void registerAndJoin(String username, String roomName) {
        registeredUsers.add(username);

        try {
            ChatRoom room = getRoom(roomName);
            room.addUser(username);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void joinRoom(String username, String roomName) throws NoSuchUserException {
        if(!isUserRegistered(username)) {
            throw new NoSuchUserException(username);
        }

        try {
            ChatRoom room = getRoom(roomName);
            room.addUser(username);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void leaveRoom(String username, String roomName) throws NoSuchUserException {
        if(!isUserRegistered(username)) {
            throw new NoSuchUserException(username);
        }

        try {
            ChatRoom room = getRoom(roomName);
            room.removeUser(username);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void followFriend(String username, String friendUsername) throws NoSuchUserException {
        if(!isUserRegistered(friendUsername)) {
            throw new NoSuchUserException(friendUsername);
        }

        if(!isUserRegistered(username)) {
            throw new NoSuchUserException(username);
        }

        for(ChatRoom room : rooms.values()) {
            if(room.hasUser(friendUsername)) {
                room.addUser(username);
            }
        }
    }
}

public class ChatSystemTest {
    public static void main(String[] args) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException, NoSuchRoomException {
        Scanner jin = new Scanner(System.in);
        int k = jin.nextInt();
        if ( k == 0 ) {
            ChatRoom cr = new ChatRoom(jin.next());
            int n = jin.nextInt();
            for ( int i = 0 ; i < n ; ++i ) {
                k = jin.nextInt();
                if ( k == 0 ) cr.addUser(jin.next());
                if ( k == 1 ) cr.removeUser(jin.next());
                if ( k == 2 ) System.out.println(cr.hasUser(jin.next()));
            }
            System.out.println("");
            System.out.println(cr.toString());
            n = jin.nextInt();
            if ( n == 0 ) return;
            ChatRoom cr2 = new ChatRoom(jin.next());
            for ( int i = 0 ; i < n ; ++i ) {
                k = jin.nextInt();
                if ( k == 0 ) cr2.addUser(jin.next());
                if ( k == 1 ) cr2.removeUser(jin.next());
                if ( k == 2 ) cr2.hasUser(jin.next());
            }
            System.out.println(cr2.toString());
        }
        if ( k == 1 ) {
            ChatSystem cs = new ChatSystem();
            Method mts[] = cs.getClass().getMethods();
            while ( true ) {
                String cmd = jin.next();
                if ( cmd.equals("stop") ) break;
                if ( cmd.equals("print") ) {
                    System.out.println(cs.getRoom(jin.next())+"\n");continue;
                }
                for ( Method m : mts ) {
                    if ( m.getName().equals(cmd) ) {
                        String params[] = new String[m.getParameterTypes().length];
                        for ( int i = 0 ; i < params.length ; ++i ) params[i] = jin.next();
                        m.invoke(cs,(Object[])params);
                    }
                }
            }
        }
    }

}
