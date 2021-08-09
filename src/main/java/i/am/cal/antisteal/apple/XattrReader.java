package i.am.cal.antisteal.apple;

import ch.securityvision.xattrj.Xattrj;

import java.nio.file.Path;
import java.nio.file.Paths;
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
        Xattrj xattrj;
        Properties prop = new Properties();
        try {
            xattrj = new Xattrj();
            String loc = xattrj.readAttribute(_path.toFile(), "kMDItemWhereFroms");
            prop.setProperty("HostUrl", loc);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return prop;
    }
}
