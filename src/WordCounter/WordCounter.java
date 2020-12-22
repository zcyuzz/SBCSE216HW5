package WordCounter;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.*;

public class WordCounter {

    // The following are the ONLY variables we will modify for grading.
    // The rest of your code must run with no changes.
    public static final Path FOLDER_OF_TEXT_FILES = Paths.get("C:/Users/zzhhd/Desktop/input"); // path to the folder where input text files are located
    public static final Path WORD_COUNT_TABLE_FILE = Paths.get("C:/Users/zzhhd/Desktop/output.txt"); // path to the output plain-text (.txt) file
    public static final int NUMBER_OF_THREADS = 16;                // max. number of threads to spawn
    public static CopyOnWriteArrayList<HashMap<String, Integer>> sample = new CopyOnWriteArrayList<>();
    public static String[] temp = new String[0];

    public static void main(String... args) throws FileNotFoundException {
//        ConcurrentHashMap<String,Integer> wordcount = new ConcurrentHashMap<String,Integer>();

//        ExecutorService executor = Executors.newFixedThreadPool(5);
//        for (int i = 0; i < 10; i++) {
//            Runnable worker = new WorkerThread("" + i);
//            executor.execute(worker);
//        }
//        executor.shutdown();
//        while (!executor.isTerminated()) {
//        }
//        System.out.println("Finished all threads");
//        ArrayList<ConcurrentHashMap> sample = new ArrayList<ConcurrentHashMap>();
//        System.out.println(sample);
//        sample.add(wordcount);

        TreeMap<String, Integer> total = new TreeMap<>();
        File folderPath = new File(FOLDER_OF_TEXT_FILES.toString());
        File[] contents = new File[0];
        contents = folderPath.listFiles();
        temp = new String[contents.length];
        long startTime = System.nanoTime();
        ExecutorService executor = Executors.newFixedThreadPool(NUMBER_OF_THREADS > 0 ? NUMBER_OF_THREADS : 1);
        for (int i = 0; i < contents.length; i++) {
            Runnable worker = new wCounter(contents[i]);
            executor.execute(worker);
        }
        executor.shutdown();
        try {
            executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {
        }
        System.out.println("Files: " + sample.size());
        long endTime = System.nanoTime();
        long totalTime = endTime - startTime;
        ArrayList<String> finishOrder = new ArrayList<>();
        for(String s:temp){
            finishOrder.add(s);
        }
        Arrays.sort(temp);
        System.out.println("time take to count word: " + totalTime + "ns");
        for (int i = 0; i < sample.size(); i++) {
            for (Map.Entry<String, Integer> e : sample.get(i).entrySet()) {
                if (total.containsKey(e.getKey())) {
                    total.put(e.getKey(), total.get(e.getKey()) + e.getValue());
                } else total.put(e.getKey(), e.getValue());
            }
        }
        BufferedWriter bf = null;
        try {
            File output = new File(WORD_COUNT_TABLE_FILE.toString());
            if (output.createNewFile()) {
                System.out.println("File created");
                bf = new BufferedWriter(new FileWriter(output));
                bf.write(String.format("%-15s",""));
                for(int i=0;i<temp.length;i++){
                    bf.write(String.format("%-15s",temp[i]));
                }
                bf.write(String.format("%-15s","total"));
                bf.newLine();
                for(Map.Entry<String,Integer>e:total.entrySet()){
                    bf.write(String.format("%-15s",e.getKey()));
                    for(int i=0;i<temp.length;i++){
                        bf.write(String.format("%-15d",sample.get(finishOrder.indexOf(temp[i])).get(e.getKey())==null?0:sample.get(finishOrder.indexOf(temp[i])).get(e.getKey())));

                    }
                    bf.write(String.format("%-15d",e.getValue()));
                    bf.newLine();
                    bf.flush();
                }
            } else System.out.println("File already exists");

        } catch (IOException e) {
            System.out.println("An error occurred");
            e.printStackTrace();
        }
        long total2time = System.nanoTime()-startTime;
        System.out.println("Total time: " + total2time +"ns");
//
//        }
//        read(FOLDER_OF_TEXT_FILES.toString());
//        File dir = new File(FOLDER_OF_TEXT_FILES.toString());
//        for (File f : dir.listFiles()) {
//            System.out.println(f.toString());
//            InputStream inputStream = new BufferedInputStream(new FileInputStream(new File(f.toString())));
//            File file = f;
//            Scanner read = new Scanner(file);
//            String current ="";
//            while(read.hasNext()){
//                current = read.next();
//                current=current.replaceAll("[^a-zA-Z ]", "").toLowerCase();
//                if (wordcount.containsKey(current)){
//                    wordcount.put(current,wordcount.get(current)+1  );
//                }else wordcount.put(current,1);
//            }
//            System.out.println(wordcount);
//            System.out.println(sample);
//        }


//        InputStream inputStream = new BufferedInputStream(new FileInputStream(new File(FOLDER_OF_TEXT_FILES.toString())));
//        File file = new File("C:/Users/zzhhd/Desktop/test.txt");
//        Scanner read = new Scanner(file);
//        String current ="";
//        while(read.hasNext()){
//            current = read.next();
//            current=current.replaceAll("[^a-zA-Z ]", "").toLowerCase();
//            if (wordcount.containsKey(current)){
//                wordcount.put(current,wordcount.get(current)+1  );
//            }else wordcount.put(current,1);
//        }
//        System.out.println(wordcount);
//        System.out.println(sample);

    }

    public static void read(String fileNamepath) throws FileNotFoundException {

        File dir = new File(FOLDER_OF_TEXT_FILES.toString());
        for (File f : dir.listFiles()) {
            HashMap<String, Integer> wordcount = new HashMap<String, Integer>();
            System.out.println(f.toString());
            InputStream inputStream = new BufferedInputStream(new FileInputStream(new File(f.toString())));
            File file = f;
            Scanner read = new Scanner(file);
            String current = "";
            while (read.hasNext()) {
                current = read.next();
                current = current.replaceAll("[^a-zA-Z ]", "").toLowerCase();
                if (wordcount.containsKey(current)) {
                    wordcount.put(current, wordcount.get(current) + 1);
                } else wordcount.put(current, 1);
            }
            sample.add(wordcount);
            System.out.println("x" + wordcount);
        }

    }

    public static class wCounter implements Runnable {
        private File file;

        public wCounter(File f) {
            this.file = f;

        }

        public void run() {
            HashMap<String, Integer> wordcount = new HashMap<String, Integer>();
            Scanner read = null;
            try {
                read = new Scanner(file);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            String current = "";
            while (read.hasNext()) {
                current = read.next();
                current = current.replaceAll("[^a-zA-Z ]", "").toLowerCase();
                if (wordcount.containsKey(current)) {
                    wordcount.put(current, wordcount.get(current) + 1);
                } else wordcount.put(current, 1);
            }
            sample.add(wordcount);
            for (int i = 0; i < temp.length; i++) {
                if (temp[i] == null) {
                    temp[i] = file.getName();
                    break;
                }
            }
        }

    }
}
