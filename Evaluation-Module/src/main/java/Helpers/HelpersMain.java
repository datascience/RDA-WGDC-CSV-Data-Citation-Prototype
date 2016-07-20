package Helpers;

import java.io.File;

/**
 * Created by stefan on 13.07.16.
 */
public class HelpersMain {
    public static void main(String[] args) {
        FileHelper fH = new FileHelper();
        long size = fH.getFileFolderSize(new File("/tmp/Evaluation"));
        System.out.println(size);

    }
}
