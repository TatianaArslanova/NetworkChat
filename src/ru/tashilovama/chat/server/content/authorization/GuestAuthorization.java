package ru.tashilovama.chat.server.content.authorization;

import java.util.TreeSet;

public class GuestAuthorization {
    private final int MAX_GUESTS = 2;
    private final String NAME;
    private TreeSet<Integer> guestIDs;

    public GuestAuthorization() {
        guestIDs = new TreeSet<>();
        NAME = "Гость";
    }

    public String getGuestName() {
        int id = calcGuestId();
        if (id == -1) return null;
        return NAME + id;
    }

    private int calcGuestId() {
        if (guestIDs.size() + 1 > MAX_GUESTS) return -1;
        for (int element = 1; element <= MAX_GUESTS; element++) {
            if (!guestIDs.contains(element)) {
                guestIDs.add(element);
                return element;
            }
        }
        return -1;
    }

    public void removeGuest(String nick){
        for (int id=1; id<=MAX_GUESTS; id++){
            if (nick.equals(NAME+id)){
                guestIDs.remove(id);
                break;
            }
        }
    }
}
