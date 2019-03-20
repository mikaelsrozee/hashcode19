import java.io.*;
import java.util.*;

public class HashCode2019 {

    public static void main(String[] args) throws IOException {
        int luckyNumber = 12;
        System.out.println("---[ Mikael Rozee's (Best Programmer Falkland Islands 2019's) Horrible Shuffle Generator 9000 ]---");
        System.out.println("My lucky number is " + luckyNumber);

        ArrayList<String> fileNames = new ArrayList<>();
        fileNames.add("a_example");
        fileNames.add("b_lovely_landscapes");
        fileNames.add("c_memorable_moments");
        fileNames.add("d_pet_pictures");
        fileNames.add("e_shiny_selfies");

        for (String fileName : fileNames) {
            System.out.println(fileName);
            long startTime = System.nanoTime();

            Reader reader = new Reader("src/in/" + fileName + ".txt");
            int numPhotos = reader.nextInt();

            ArrayList<Photo> horizontalPhotos = new ArrayList<>();
            ArrayList<Photo> verticalPhotos = new ArrayList<>();

            int id = 0;
            while (numPhotos-- > 0) {
                String[] line = reader.readLine().split(" ");
                String orientation = line[0];
                int numTags = Integer.parseInt(line[1]);
                String[] tags = new String[numTags];
                System.arraycopy(line, 2, tags, 0, numTags);

                if (orientation.equals("H"))
                    horizontalPhotos.add(new Photo(id++, orientation, numTags, tags));
                else verticalPhotos.add(new Photo(id++, orientation, numTags, tags));
            }

            while (luckyNumber-- > 0) {
                Collections.shuffle(horizontalPhotos);
                Collections.shuffle(verticalPhotos);
            }

            ArrayList<Slide> outputSlides = Sort.ugly(horizontalPhotos, verticalPhotos);

            Collections.shuffle(outputSlides);

            /* File output */
            StringBuilder fileContent = new StringBuilder();
            fileContent.append(outputSlides.size()).append("\n");

            for (Slide slide : outputSlides) {
                fileContent.append(slide.getLine()).append("\n");
            }

            BufferedWriter out = new BufferedWriter(new FileWriter("src/out/" + fileName + ".out"));
            out.write(fileContent.toString());
            out.close();

            long totalTime = System.nanoTime() - startTime;
            System.out.println(">>> Completed in " + (double) totalTime / 1000000000 + " seconds.");
        }
    }

}

class Photo {

    private int id;
    private String orientation;
    private int numTags;
    private String[] tags;

    Photo(int id, String orientation, int numTags, String[] tags) {
        this.id = id;
        this.orientation = orientation;
        this.numTags = numTags;
        this.tags = tags;
    }

    public String toString() {
        return Integer.toString(id) + ", " + orientation + ", " + Integer.toString(numTags) + ", " + Arrays.toString(tags);
    }

    int getId() {
        return id;
    }

    String[] getTags() {
        return tags;
    }

}

class Slide {

    private Photo photo0;
    private Photo photo1;

    Slide(Photo photo) {
        this.photo0 = photo;
    }

    Slide(Photo photo0, Photo photo1) {
        this.photo0 = photo0;
        this.photo1 = photo1;
    }

    public String getLine() {
        if (photo1 != null)
            return Integer.toString(photo0.getId()) + " " + Integer.toString(photo1.getId());
        else return Integer.toString(photo0.getId());
    }

}


/**
 * Faster reading file than the default Java scanner.
 *
 * @author Mikael Rozee
 * @version 1.0.0
 */
class Reader {

    private final int BUFFER_SIZE = 1 << 16;
    private DataInputStream dataIn;
    private byte[] buffer;
    private int bufferPointer, bytesRead;

    public Reader() {
        dataIn = new DataInputStream(System.in);
        buffer = new byte[BUFFER_SIZE];
        bufferPointer = bytesRead = 0;
    }

    public Reader(String file_name) throws IOException {
        dataIn = new DataInputStream(new FileInputStream(file_name));
        buffer = new byte[BUFFER_SIZE];
        bufferPointer = bytesRead = 0;
    }

    public String readLine() throws IOException {
        byte[] buf = new byte[65536]; // line length, in chars
        int cnt = 0, character;

        while ((character = read()) != -1) {
            if (character == '\n')
                break;
            buf[cnt++] = (byte) character;
        }
        return new String(buf, 0, cnt);
    }

    public int nextInt() throws IOException {
        int toReturn = 0;
        byte character = read();

        while (character <= ' ')
            character = read();

        boolean isNegative = (character == '-');

        if (isNegative)
            character = read();

        do {
            toReturn = toReturn * 10 + character - '0';
        } while ((character = read()) >= '0' && character <= '9');

        if (isNegative)
            return -toReturn;
        return toReturn;
    }

    public long nextLong() throws IOException {
        long toReturn = 0;
        byte character = read();

        while (character <= ' ')
            character = read();

        boolean isNegative = (character == '-');

        if (isNegative)
            character = read();

        do {
            toReturn = toReturn * 10 + character - '0';
        } while ((character = read()) >= '0' && character <= '9');

        if (isNegative)
            return -toReturn;
        return toReturn;
    }

    public double nextDouble() throws IOException {
        double toReturn = 0, div = 1;
        byte character = read();

        while (character <= ' ')
            character = read();

        boolean isNegative = (character == '-');
        if (isNegative)
            character = read();

        do {
            toReturn = toReturn * 10 + character - '0';
        }
        while ((character = read()) >= '0' && character <= '9');

        if (character == '.') {
            while ((character = read()) >= '0' && character <= '9') {
                toReturn += (character - '0') / (div *= 10);
            }
        }

        if (isNegative)
            return -toReturn;
        return toReturn;
    }

    private void fillBuffer() throws IOException {
        bytesRead = dataIn.read(buffer, bufferPointer = 0, BUFFER_SIZE);
        if (bytesRead == -1)
            buffer[0] = -1;
    }

    private byte read() throws IOException {
        if (bufferPointer == bytesRead)
            fillBuffer();
        return buffer[bufferPointer++];
    }

    public void close() throws IOException {
        if (dataIn == null)
            return;
        dataIn.close();
    }
}

class Sort {

    static ArrayList<Slide> ugly(ArrayList<Photo> horizontalPhotos, ArrayList<Photo> verticalPhotos) {
        ArrayList<Slide> outputSlides = new ArrayList<>();

        while (!horizontalPhotos.isEmpty()) {
            outputSlides.add(new Slide(horizontalPhotos.get(0)));
            horizontalPhotos.remove(horizontalPhotos.get(0));
        }

        while (!verticalPhotos.isEmpty()) {
            outputSlides.add(new Slide(verticalPhotos.get(0), verticalPhotos.get(1)));
            verticalPhotos.remove(verticalPhotos.get(0));
            verticalPhotos.remove(verticalPhotos.get(0));
        }

        return outputSlides;
    }
//
//    static ArrayList<Slide> uglier(ArrayList<Photo> horizontalPhotos, ArrayList<Photo> verticalPhotos) {
//        ArrayList<Slide> outputSlides = new ArrayList<>();
//        Random random = new Random();
//
//        while (!horizontalPhotos.isEmpty()) {
//            Photo photo = horizontalPhotos.get(random.nextInt(horizontalPhotos.size()));
//            outputSlides.add(new Slide(photo));
//            horizontalPhotos.remove(photo);
//        }
//
//        while (!verticalPhotos.isEmpty()) {
//            int randint = random.nextInt(verticalPhotos.size());
//            int secondInt = randint + 1;
//
//            if (secondInt >= verticalPhotos.size())
//                secondInt -= 2;
//
//            Photo photo0 = verticalPhotos.get(randint);
//            Photo photo1 = verticalPhotos.get(secondInt);
//            outputSlides.add(new Slide(photo0, photo1));
//            verticalPhotos.remove(photo0);
//            verticalPhotos.remove(photo1);
//        }
//
//        return outputSlides;
//    }
//
//    static ArrayList<Slide> shuffle(ArrayList<Photo> horizontalPhotos, ArrayList<Photo> verticalPhotos) {
//        ArrayList<Slide> outputSlides = new ArrayList<>();
//
//        while (!horizontalPhotos.isEmpty()) {
//            outputSlides.add(new Slide(horizontalPhotos.get(0)));
//            horizontalPhotos.remove(horizontalPhotos.get(0));
//        }
//
//        while (!verticalPhotos.isEmpty()) {
//            outputSlides.add(new Slide(verticalPhotos.get(0), verticalPhotos.get(1)));
//            verticalPhotos.remove(verticalPhotos.get(0));
//            verticalPhotos.remove(verticalPhotos.get(0));
//        }
//
//        Collections.shuffle(outputSlides);
//
//        return outputSlides;
//    }
//
//    static ArrayList<Slide> shufflePro(ArrayList<Photo> horizontalPhotos, ArrayList<Photo> verticalPhotos) {
//        ArrayList<Slide> outputSlides = new ArrayList<>();
//
//        Collections.shuffle(horizontalPhotos);
//        Collections.shuffle(verticalPhotos);
//
//        while (!horizontalPhotos.isEmpty()) {
//            outputSlides.add(new Slide(horizontalPhotos.get(0)));
//            horizontalPhotos.remove(horizontalPhotos.get(0));
//        }
//
//        while (!verticalPhotos.isEmpty()) {
//            outputSlides.add(new Slide(verticalPhotos.get(0), verticalPhotos.get(1)));
//            verticalPhotos.remove(verticalPhotos.get(0));
//            verticalPhotos.remove(verticalPhotos.get(0));
//        }
//
//        Collections.shuffle(outputSlides);
//
//        return outputSlides;
//    }
//
//    static ArrayList<Slide> extremelyUgly(ArrayList<Photo> horizontalPhotos, ArrayList<Photo> verticalPhotos) {
//        ArrayList<Slide> outputSlides = new ArrayList<>();
//
//        while (!horizontalPhotos.isEmpty()) {
//            outputSlides.add(new Slide(horizontalPhotos.get(0)));
//            ArrayList<String> firstTags = new ArrayList<>(Arrays.asList(horizontalPhotos.get(0).getTags()));
//
//            String mostFrequentTag = firstTags.get(0);
//            int frequency = 0;
//            for (String tag : firstTags) {
//
//            }
//
//            horizontalPhotos.remove(horizontalPhotos.get(0));
//        }
//
//        while (!verticalPhotos.isEmpty()) {
//            outputSlides.add(new Slide(verticalPhotos.get(0), verticalPhotos.get(1)));
//            verticalPhotos.remove(verticalPhotos.get(0));
//            verticalPhotos.remove(verticalPhotos.get(0));
//        }
//
//        return outputSlides;
//    }

}

