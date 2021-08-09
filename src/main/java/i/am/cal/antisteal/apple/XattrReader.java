package i.am.cal.antisteal.apple;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.UserDefinedFileAttributeView;
import java.util.Properties;

public class XattrReader {
    private final Path _path;

    public XattrReader(Path path) {
        this._path = path;
    }

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
        UserDefinedFileAttributeView view = Files.getFileAttributeView(_path, UserDefinedFileAttributeView.class);
        ByteBuffer buffer = null;
        try {
            buffer = ByteBuffer.allocate(view.size("com.apple.metadata:kMDItemWhereFroms"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            view.read("com.apple.metadata:kMDItemWhereFroms", buffer);
        } catch (IOException e) {
            e.printStackTrace();
        }
        buffer.flip();
        prop.setProperty("HostUrl", Charset.defaultCharset().decode(buffer).toString());
        return prop;
    }
}
