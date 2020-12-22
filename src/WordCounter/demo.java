package WordCounter;

import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class demo {
    //定义单词总数
    private static Integer n=0;
    //存储单词,数量
    private static Map<String,Integer> map = new TreeMap<String,Integer>();
    /**
     * 读取方法
     * @param fileNamePath 文件名字
     */
    public void read(String fileNamePath){
        try {
            BufferedReader br = new BufferedReader(new FileReader(fileNamePath));
            StringBuffer sb = new StringBuffer();
            String line = "";
            while((line=br.readLine())!=null){
                sb.append(line);
            }
            br.close();
            //正则
            Pattern p = Pattern.compile("[a-zA-Z]+");
            String words = sb.toString();
            Matcher matcher = p.matcher(words);
            int times=0;
            while(matcher.find()){
                String word = matcher.group();
                n++;
                if(map.containsKey(word)){
                    times = map.get(word);
                    // 利用map的key统计数量
                    map.put(word, times+1);
                }else{
                    map.put(word, 1);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * 创建线程
     * @param fileNamePath
     * @throws Exception
     */
    public void creatThread(String fileNamePath) throws Exception{
        Thread thread = new Thread(){
            public void run(){
                read(fileNamePath);
            }
        };
        thread.start();
        thread.join();
    }
    /**
     * 排序方法
     * @throws IOException
     */
    public void comparatorToWord() throws IOException {
        //对统计进行排序
        List<Map.Entry<String,Integer>> list = new ArrayList<Map.Entry<String,Integer>>(map.entrySet());
        Comparator<Map.Entry<String,Integer>> com = new Comparator<Map.Entry<String,Integer>>(){
            public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
                return (o2.getValue().compareTo(o1.getValue()));
            }
        };
        Collections.sort(list,com);
        // 写出到文件
        BufferedWriter bw = new BufferedWriter(new FileWriter("C:/Users/zzhhd/Desktop/txt.txt"));
        for(Map.Entry<String,Integer> e:list){
            bw.write(e.getKey()+" "+e.getValue());
            bw.newLine();
        }
        bw.flush();
        bw.close();
    }
    public static void main(String[] args) throws IOException {
        // 读取D盘的文件
        File file = new File("C:/Users/zzhhd/Desktop");
        demo r = new demo();
        if(file.isDirectory()){
            File[] files = file.listFiles();
            // 定义文件类型
            String match = "\\w+.txt";
            for(int i=0;i<files.length;i++){
                String fileName = files[i].getName();

                if(fileName.matches(match)){
                    try {
                        System.out.println(fileName+"xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx");
                        r.creatThread("C:/Users/zzhhd/Desktop\\"+fileName);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            r.comparatorToWord();
        }
    }
}