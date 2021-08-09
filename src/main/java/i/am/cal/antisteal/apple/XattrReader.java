package i.am.cal.antisteal.apple;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.UserDefinedFileAttributeView;
import java.util.Properties;

public class XattrReader {
    private final Path _path;

    public XattrReader(Path path) {
        this._path = path;
    }

    // Test
    public static void main(String[] args) {
        XattrReader reader = new XattrReader(Paths.get("/Users/nick/Downloads/Dynamic-FPS-Mod-Fabric-1.17.1.jar"));
        Properties prop = reader.read();
        System.out.println(prop.get("HostUrl"));
    }

    public Path get_path() {
        return _path;
    }

    public Properties read() {
        Properties prop = new Properties();
        Object view = null;
        try {
            view = Files.getAttribute(_path, "UserDefinedFileAttributeView:kMDItemWhereFroms", LinkOption.NOFOLLOW_LINKS);
        } catch (IOException e) {
            e.printStackTrace();
        }
        prop.setProperty("HostUrl", (String) view);
        return prop;
    }
}
