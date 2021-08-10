package i.am.cal.antisteal.retriever;

import java.io.*;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class WindowsUrlRetriever implements UrlRetriever {


    private final Path path;
    private final String[] urls;


    @Override
    public String[] getUrls() {
        return urls;
    }

    @Override
    public Path getPath() {
        return path;
    }

    public WindowsUrlRetriever(Path path) {
        this.path = path;
        urls = new String[]{(String)read().get("HostUrl")};
    }

    //Windows vodoo
    private Properties read() {
        List<String> parsedADS = new ArrayList<>();

        final String command = "cmd.exe /c dir " + path + " /r"; // listing of given Path.

        final Pattern pattern = Pattern.compile(
                "\\s*"                 // any amount of whitespace
                        + "[0123456789,]+\\s*"   // digits (with possible comma), whitespace
                        + "([^:]+:"    // group 1 = file name, then colon,
                        + "[^:]+:"     // then ADS, then colon,
                        + ".+)");      // then everything else.

        try {
            Process process = Runtime.getRuntime().exec(command);
            process.waitFor();
            try (BufferedReader br = new BufferedReader(
                    new InputStreamReader(process.getInputStream()))) {
                String line;

                while ((line = br.readLine()) != null) {
                    Matcher matcher = pattern.matcher(line);
                    if (matcher.matches()) {
                        parsedADS.add((matcher.group(1)));
                    }
                }
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

        for (String parsedAD : parsedADS) System.out.println(parsedAD);

        List<String> contents = new ArrayList<>();
        try {
            File file = new File("test.txt:hidden");
            try (BufferedReader bf = new BufferedReader( new FileReader(file))) {
                contents = bf.lines().collect(Collectors.toList());
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        contents.remove("[ZoneTransfer]");
        String _contents = String.join("\n", contents);
        Properties properties = new Properties();
        try {
            properties.load(new StringReader(_contents));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return properties;
    }


}
