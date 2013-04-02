package reader;

import java.io.IOException;
import java.io.InputStream;
import java.util.jar.Manifest;

public class ManifestUtil {
	private static Manifest mf = null;

	static {
		InputStream file = ClassLoader
				.getSystemResourceAsStream("META-INF/MANIFEST.MF");
		try {
			mf = new Manifest(file);
		} catch (IOException e) {
			System.err.println("Error abriendo MANIFEST");
		}
	}

	public static String getAppVersion() {
		return mf.getMainAttributes().getValue("appVersion");
	}
}
