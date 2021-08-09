package i.am.cal.antisteal;

import i.am.cal.antisteal.apple.XattrReader;
import i.am.cal.antisteal.windows.AlternateStreamReader;

import java.awt.*;
import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.HashMap;
import java.util.Properties;

public class Antisteal {
    public interface CloseEvent {
        public void close();
    }
    /**
     * Check if the mod is considered stolen.
     * @param pathToFile       Path to your mod file. See documentation on how to get.
     * @param closeEvent       See docs.
     * @param whitelist        The whitelisted domains. See documentation for format and storage.
     */
    public static void Check(Path pathToFile, CloseEvent closeEvent, HashMap<String, String> whitelist) {
        Properties props = new Properties();
        if (OSValidator.isWindows()) {
            AlternateStreamReader streamReader = new AlternateStreamReader(pathToFile, "ads");
            props = streamReader.read();
        }
        if (OSValidator.isMac()) {
            XattrReader streamReader = new XattrReader(pathToFile);
            props = streamReader.read();
        }
        if (OSValidator.isUnix() || OSValidator.isSolaris() || OSValidator.getOS().equals("err")) {
            return;
        }

        if (!whitelist.containsValue(props.get("HostUrl"))) {
            FileInputStream stream = (FileInputStream) Antisteal.class.getResourceAsStream("stolen.html");

            File temp = null;
            try {
                temp = File.createTempFile("stolenmod", ".html");
                FileWriter fileWriter = new FileWriter(temp);
                String content = getFileContent(stream, "utf-8");
                content = content.replaceAll("%jar_name%", pathToFile.getFileName().toString());
                content = content.replaceAll("%jar_loc%", "<code>" + pathToPortableString(pathToFile) + "</code>");
                content = content.replaceAll("%jar_downloaded%", "dl.9minecraft.net");
                content = content.replaceAll("%date%", new Date().toString());

                var ref = new Object() {
                    String trustedString = "";
                };

                whitelist.forEach((String a, String b) -> {
                    ref.trustedString = ref.trustedString + "<a href=\"" + b + "\">" + a + "</a><br>\n";
                });

                content = content.replace("%ul_content%", ref.trustedString);

                fileWriter.write(content);
                fileWriter.close();
                temp.deleteOnExit();
                Desktop.getDesktop().browse(temp.toURI());
            } catch (IOException e) {
                e.printStackTrace();
            }
            closeEvent.close();
        }
    }

    // HTML test
    public static void main(String[] args) {
        FileInputStream stream = null;
        try {
            stream = new FileInputStream(Antisteal.class.getResource("/stolen.html").getFile());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        Path pathToFile = Paths.get(".minecraft/mods/test_mod.jar");
        HashMap<String, String> trusted = new HashMap<>();
        trusted.put("Curseforge", "https://curseforge.com/");
        trusted.put("Modrinth", "https://modrinth.com/");
        trusted.put("GitHub", "https://github.com/");

        /////////////////////////////////////////////////////////////////////////////////////////////////

        File temp = null;
        try {
            temp = File.createTempFile("stolenmod", ".html");
            FileWriter fileWriter = new FileWriter(temp);
            String content = getFileContent(stream, "utf-8");
            content = content.replaceAll("%jar_name%", pathToFile.getFileName().toString());
            content = content.replaceAll("%jar_loc%", "<code>" + pathToPortableString(pathToFile) + "</code>");
            content = content.replaceAll("%jar_downloaded%", "dl.9minecraft.net");
            content = content.replaceAll("%date%", new Date().toString());

            var ref = new Object() {
                String trustedString = "";
            };

            trusted.forEach((String a, String b) -> {
                ref.trustedString = ref.trustedString + "<a href=\"" + b + "\">" + a + "</a><br>\n";
            });

            content = content.replace("%ul_content%", ref.trustedString);

            fileWriter.write(content);
            fileWriter.close();
            temp.deleteOnExit();
            Desktop.getDesktop().browse(temp.toURI());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private static String getFileContent(
            FileInputStream fis,
            String encoding) throws IOException {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(fis, encoding))) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
                sb.append('\n');
            }
            return sb.toString();
        }
    }
    private static String pathToPortableString(Path p)
    {
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        Path root = p.getRoot();
        if (root != null)
        {
            sb.append(root.toString().replace('\\','/'));
            /* root elements appear to contain their
             * own ending separator, so we don't set "first" to false
             */
        }
        for (Path element : p)
        {
            if (first)
                first = false;
            else
                sb.append("/");
            sb.append(element.toString());
        }
        return sb.toString();
    }
}
