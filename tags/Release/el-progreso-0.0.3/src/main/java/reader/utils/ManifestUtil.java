package reader.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.jar.Manifest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ManifestUtil {
	private static Manifest mf = null;
	static Logger LOGGER = LoggerFactory.getLogger(ManifestUtil.class);

	static {
		InputStream file = ClassLoader
				.getSystemResourceAsStream("META-INF/MANIFEST.MF");
		try {
			mf = new Manifest(file);
			file.close();
		} catch (IOException e) {
			LOGGER.error("Could not open file MANIFEST.MF");
		}
	}

	public static String getAppVersion() {
		return mf.getMainAttributes().getValue("appVersion");
	}
}
