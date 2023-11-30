// K1 3

import java.util.*;

interface IFile extends Comparable<IFile> {
    String getFileName();
    long getFileSize();
    String getFileInfo(int sub);
    void sortBySize();
    long findLargestFile();

    @Override
    default int compareTo(IFile o) {
        return Long.compare(getFileSize(), o.getFileSize());
    }
}

class FileNameExistsException extends Exception {
    FileNameExistsException(String file, String folder) {
        super(String.format("There is already a file named %s in the folder %s", file, folder));
    }
}

class File implements IFile {
    String name;
    long size;

    File() {
        this.name = "Untitled";
        this.size = 0;
    }

    File(String name, long size) {
        this.name = name;
        this.size = size;
    }

    @Override
    public String getFileName() {
        return name;
    }

    @Override
    public long getFileSize() {
        return size;
    }

    @Override
    public String getFileInfo(int sub) {
        return " ".repeat(Math.max(0, 4 * sub)) + String.format("File name: %10s File size: %10d\n", name, size);
    }

    @Override
    public void sortBySize() { }

    @Override
    public long findLargestFile() {
        return size;
    }
}

class Folder implements IFile {
    String name;
    long size;
    ArrayList<IFile> list;

    Folder() {
        this.name = "Untitled";
        this.size = 0;
        this.list = new ArrayList<>();
    }

    Folder(String name) {
        this.name = name;
        this.size = 0;
        this.list = new ArrayList<>();
    }

    public void addFile(IFile file) throws FileNameExistsException {
        String fileName = file.getFileName();

        for(IFile f : list) {
            if(Objects.equals(fileName, f.getFileName())) {
                throw new FileNameExistsException(fileName, name);
            }
        }

        list.add(file);
        size += file.getFileSize();
    }

    @Override
    public String getFileName() {
        return name;
    }

    @Override
    public long getFileSize() {
        return size;
    }

    @Override
    public String getFileInfo(int sub) {
        StringBuilder str = new StringBuilder();
        str.append(" ".repeat(Math.max(0, 4 * sub)));
        str.append(String.format("Folder name: %10s Folder size: %10d\n", name, size));
        list.forEach(x -> str.append(x.getFileInfo(sub + 1)));
        return str.toString();
    }

    @Override
    public void sortBySize() {
        list.sort(IFile::compareTo);
        for(IFile f : list) {
            f.sortBySize();
        }
    }

    @Override
    public long findLargestFile() {
        return list.stream().mapToLong(IFile::findLargestFile).max().orElse(0);
    }
}

class FileSystem {
    Folder dir;

    FileSystem() {
        dir = new Folder("root");
    }

    public void addFile(IFile file) throws FileNameExistsException {
        dir.addFile(file);
    }

    public long findLargestFile() {
        return dir.findLargestFile();
    }

    public void sortBySize() {
        dir.sortBySize();
    }

    @Override
    public String toString() {
        return dir.getFileInfo(0);
    }
}

public class FileSystemTest {

    public static Folder readFolder (Scanner sc)  {

        Folder folder = new Folder(sc.nextLine());
        int totalFiles = Integer.parseInt(sc.nextLine());

        for (int i=0;i<totalFiles;i++) {
            String line = sc.nextLine();

            if (line.startsWith("0")) {
                String fileInfo = sc.nextLine();
                String [] parts = fileInfo.split("\\s+");
                try {
                    folder.addFile(new File(parts[0], Long.parseLong(parts[1])));
                } catch (FileNameExistsException e) {
                    System.out.println(e.getMessage());
                }
            }
            else {
                try {
                    folder.addFile(readFolder(sc));
                } catch (FileNameExistsException e) {
                    System.out.println(e.getMessage());
                }
            }
        }

        return folder;
    }

    public static void main(String[] args)  {

        //file reading from input

        Scanner sc = new Scanner (System.in);

        System.out.println("===READING FILES FROM INPUT===");
        FileSystem fileSystem = new FileSystem();
        try {
            fileSystem.addFile(readFolder(sc));
        } catch (FileNameExistsException e) {
            System.out.println(e.getMessage());
        }

        System.out.println("===PRINTING FILE SYSTEM INFO===");
        System.out.println(fileSystem.toString());

        System.out.println("===PRINTING FILE SYSTEM INFO AFTER SORTING===");
        fileSystem.sortBySize();
        System.out.println(fileSystem.toString());

        System.out.println("===PRINTING THE SIZE OF THE LARGEST FILE IN THE FILE SYSTEM===");
        System.out.println(fileSystem.findLargestFile());
    }
}
