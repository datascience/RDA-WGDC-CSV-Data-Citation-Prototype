package at.stefanproell.Examples;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by stefan on 24.11.14.
 */
public class RegexTest {

    public static void main(String[] args) {
        System.out.println("API Mini Test");
        RegexTest reg = new RegexTest();
        reg.runRegex("1237/ab2cf/rt567u/bbsayy");
        System.exit(0);


    }


    public void runRegex(String txt) {


        String regexPrefix = "^(\\d{4})";    // Any Single Digit 1


        String testrex = "(\\/[a-zA-Z0-9]+)";
        String testword = "1234/11ww33/aabbcc";


        Pattern p2 = Pattern.compile(regexPrefix);
        Matcher m2 = p2.matcher(testword);
        this.print(m2);


    }

    private void print(Matcher m) {
        System.out.println("Testing String");
        while (m.find()) {
            for (int i = 1; i <= m.groupCount(); i++) {
                System.out.println("matched text: " + m.group(i));
                //    System.out.println("matched start: " + m.start(i));
                //    System.out.println("matched end: " + m.end(i));
            }
        }
    }
}
