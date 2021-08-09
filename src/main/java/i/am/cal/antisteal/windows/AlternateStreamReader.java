package i.am.cal.antisteal.windows;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class AlternateStreamReader {
    private final String _streamName;
    private final Path _path;

    public AlternateStreamReader(Path path, String streamName) {
        this._path = path;
        this._streamName = streamName;
    }

    public Path get_path() {
        return _path;
    }

    public String get_streamName() {
        return _streamName;
    }

    // Test
    public static void main(String[] args) {
        AlternateStreamReader streamReader = new AlternateStreamReader(Paths.get("C:\\Users\\{c}\\Downloads\\Unhealthy-Dying-Mod-1.17.1.jar"), "ads");
        System.out.println("Detecting potentially reposted mod: Unhealthy-Dying-Mod-1.17.1.jar");
        System.out.println("Platform - Windows - Using Alternate Data Stream");
        Properties props = streamReader.read();
        System.out.println("Referrer: " + props.get("ReferrerUrl"));
        System.out.println("Host: " +props.get("HostUrl"));
    }

    public Properties read() {
        String path = _path.toString();
        ArrayList<String> parsedADS = new ArrayList<>();

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

        String zoneIdentPath = path + ":Zone.Identifier:$DATA";
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
        assert contents != null;
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
