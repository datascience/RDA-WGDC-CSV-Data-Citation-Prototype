package at.stefanproell.Examples;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by stefan on 21.11.14.
 */
public class ListTest {

    public static void main(String[] args) {
        System.out.println("Java runtime version is_: " + getVersion());
        LinkedList<String> list = new LinkedList<String>();
        list.addFirst("Test");

        System.exit(0);


    }


    static String getVersion() {
        String version = System.getProperty("java.version");
        int pos = 0, count = 0;
        for (; pos < version.length() && count < 2; pos++) {
            if (version.charAt(pos) == '.') count++;
        }
        return (version.substring(0, pos));
    }
}
