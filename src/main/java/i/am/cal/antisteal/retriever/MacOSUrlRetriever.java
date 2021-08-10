package i.am.cal.antisteal.retriever;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Both Flytre and Anti-steal retain full rights to this source under their licenses thereof.
 */
public class MacOSUrlRetriever implements UrlRetriever {

    private static final Pattern HEX = Pattern.compile("^([0-9A-F]{2,} ?)+$", Pattern.MULTILINE);


    private final Path path;
    private final String[] urls;

    public MacOSUrlRetriever(@NotNull Path path) {
        String attribute = run(new String[]{"xattr", "-p", "com.apple.metadata:kMDItemWhereFroms", path.toString()});


        if (attribute == null)
            attribute = "";

        if (HEX.matcher(attribute).find())
            attribute = decryptHex(attribute);

        this.path = path;
        this.urls = extractUrls(attribute).toArray(new String[0]);
    }

    private static String decryptHex(String str) {
        String[] hexes = str.split("\s|\n");
        StringBuilder result = new StringBuilder();
        for (String hex : hexes)
            result.append((char) Integer.parseInt(hex, 16));
        return result.toString();
    }

    private static String run(String[] cmd) {
        String result = null;
        try (InputStream inputStream = Runtime.getRuntime().exec(cmd).getInputStream();
             Scanner s = new Scanner(inputStream).useDelimiter("^")) {
            result = s.hasNext() ? s.next() : null;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    private static List<String> extractUrls(String text) {
        List<String> containedUrls = new ArrayList<String>();
        String urlRegex = "((https?|ftp|gopher|telnet|file):((//)|(\\\\))+[\\w\\d:#@%/;$()~_?\\+-=\\\\\\.&]*)";
        Pattern pattern = Pattern.compile(urlRegex, Pattern.CASE_INSENSITIVE);
        Matcher urlMatcher = pattern.matcher(text);

        while (urlMatcher.find()) {
            containedUrls.add(text.substring(urlMatcher.start(0),
                    urlMatcher.end(0)));
        }

        return containedUrls;
    }

    @Override
    public Path getPath() {
        return path;
    }

    @Override
    public String[] getUrls() {
        return urls;
    }

}
