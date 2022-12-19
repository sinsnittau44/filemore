package filemore;

//引入必要的类库 
import java.io.*; 
import java.math.BigInteger;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class filemore {

    // 使用MD5算法计算指定文件的哈希值
    public static String getHash(String filePath) {
        File file = new File(filePath);
        if (!file.exists()) {
            return null;
        }
        String value = null;
        FileInputStream in = null;
        try {
            in = new FileInputStream(file);
            MappedByteBuffer byteBuffer = in.getChannel()
                    .map(FileChannel.MapMode.READ_ONLY, 0, file.length());
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.update(byteBuffer);
            BigInteger bi = new BigInteger(1, md5.digest());
            value = bi.toString(16);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != in) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return value;
    }

    // 获取指定文件夹下所有文件的哈希值
    public static List<String> getFileHashList(String folderPath) {
        List<String> results = new ArrayList<>();
        File folder = new File(folderPath);
        if (folder.exists() && folder.isDirectory()) {
            File[] fileList = folder.listFiles();
            if (null != fileList) {
                for (File file : fileList) {
                    if (file.isFile()) {
                        String hashValue = getHash(file.getAbsolutePath());
                        if (null != hashValue) {
                            results.add(hashValue);
                        }
                    }
                }
            }
        }
        return results;
    }

    public static List<File> findDuplicatedFiles(String folderPath) {
        List<String> hashList = getFileHashList(folderPath);
        List<File> duplicatedList = new ArrayList<>();
        Map<String, File> map = new HashMap<>();

        File folder = new File(folderPath);
        if (folder.exists() && folder.isDirectory()) {
            File[] fileList = folder.listFiles();
            if (null != fileList) {
                for (File file : fileList) {
                    if (file.isFile()) {
                        String value = getHash(file.getAbsolutePath());
                        if (value != null) {
                            if (hashList.contains(value) && !map.containsKey(value)) {
                                duplicatedList.add(file);
                                map.put(value, file);
                            }
                        }
                    }
                }
            }
        }
        return duplicatedList;
    }

    public static void duplicatedFiles(String folderPath) {

        List<File> duplicatedList = findDuplicatedFiles(folderPath);
        if (duplicatedList.size() > 0) {
            System.out.println("以下文件重复");
            for (File file : duplicatedList) {
                System.out.println(file.getName());
                file.delete();
            }
        } else {
            System.out.println("没有发现重复的文件");
        }
    }

    public static void main(String[] args) {
    	// 查找Users文件夹下的所有文件
        String folderPath = "/Users";
        duplicatedFiles(folderPath);
    }
}