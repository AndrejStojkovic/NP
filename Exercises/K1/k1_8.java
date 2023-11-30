// K1 8

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Scanner;

class NonExistingItemException extends Exception {
    NonExistingItemException(int id) {
        super(String.format("Item with id %d doesn't exist", id));
    }
}

class Archive {
    int id;
    LocalDate dateArchived;

    Archive() {
        id = 0;
        dateArchived = LocalDate.MIN;
    }

    Archive(int id) {
        this.id = id;
        this.dateArchived = LocalDate.MIN;
    }

    Archive(int id, LocalDate dateArchived) {
        this.id = id;
        this.dateArchived = dateArchived;
    }

    public int getId() {
        return id;
    }

    public LocalDate getDateArchived() {
        return dateArchived;
    }

    public void setDateArchived(LocalDate dateArchived) {
        this.dateArchived = dateArchived;
    }
}

class LockedArchive extends Archive {
    LocalDate dateToOpen;

    LockedArchive() {
        super();
        dateToOpen = LocalDate.MIN;
    }

    LockedArchive(int id, LocalDate dateToOpen) {
        super(id);
        this.dateToOpen = dateToOpen;
    }

    public LocalDate getDateToOpen() {
        return dateToOpen;
    }
}

class SpecialArchive extends Archive {
    int maxOpen;
    int currentOpens;

    SpecialArchive() {
        super();
        maxOpen = 0;
        currentOpens = 0;
    }

    SpecialArchive(int id, int maxOpen) {
        super(id);
        this.maxOpen = maxOpen;
        this.currentOpens = 0;
    }

    public int getMaxOpen() {
        return maxOpen;
    }

    public void open() {
        currentOpens++;
    }

    public int getCurrentOpens() {
        return currentOpens;
    }
}

class ArchiveStore {
    ArrayList<Archive> archives;
    ArrayList<String> logs;

    ArchiveStore() {
        archives = new ArrayList<>();
        logs = new ArrayList<>();
    }

    public void archiveItem(Archive item, LocalDate date) {
        archives.add(item);
        archives.get(archives.size() - 1).setDateArchived(date);
        logs.add("Item " + item.getId() + " archived at " + date);
    }

    public void openItem(int id, LocalDate date) throws NonExistingItemException {
        Archive archive = archives.stream().filter(x -> x.getId() == id).findFirst().orElse(null);

        if(archive == null) {
            throw new NonExistingItemException(id);
        }

        if(archive instanceof LockedArchive) {
            LockedArchive locked = (LockedArchive) archive;
            if(date.isBefore(locked.getDateToOpen())) {
                logs.add("Item " + locked.getId() + " cannot be opened before " + locked.getDateToOpen());
                return;
            }
        } else if(archive instanceof SpecialArchive) {
            SpecialArchive special = (SpecialArchive) archive;
            if(special.getCurrentOpens() >= special.getMaxOpen()) {
                logs.add("Item " + special.getId() + " cannot be opened more than " + special.getMaxOpen() + " times");
                return;
            }
            special.open();
        }

        logs.add("Item " + archive.getId() + " opened at " + date);
    }

    public String getLog() {
        StringBuilder str = new StringBuilder();
        logs.forEach(x -> str.append(x).append("\n"));
        return str.toString();
    }
}

public class ArchiveStoreTest {
    public static void main(String[] args) {
        ArchiveStore store = new ArchiveStore();
        LocalDate date = LocalDate.of(2013, 10, 7);
        Scanner scanner = new Scanner(System.in);
        scanner.nextLine();
        int n = scanner.nextInt();
        scanner.nextLine();
        scanner.nextLine();
        int i;
        for (i = 0; i < n; ++i) {
            int id = scanner.nextInt();
            long days = scanner.nextLong();

            LocalDate dateToOpen = date.atStartOfDay().plusSeconds(days * 24 * 60 * 60).toLocalDate();
            LockedArchive lockedArchive = new LockedArchive(id, dateToOpen);
            store.archiveItem(lockedArchive, date);
        }
        scanner.nextLine();
        scanner.nextLine();
        n = scanner.nextInt();
        scanner.nextLine();
        scanner.nextLine();
        for (i = 0; i < n; ++i) {
            int id = scanner.nextInt();
            int maxOpen = scanner.nextInt();
            SpecialArchive specialArchive = new SpecialArchive(id, maxOpen);
            store.archiveItem(specialArchive, date);
        }
        scanner.nextLine();
        scanner.nextLine();
        while(scanner.hasNext()) {
            int open = scanner.nextInt();
            try {
                store.openItem(open, date);
            } catch(NonExistingItemException e) {
                System.out.println(e.getMessage());
            }
        }
        System.out.println(store.getLog());
    }
}
