package reader.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.CodeSource;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import reader.business.Template;

public class PropertiesUtil {

	static String CONFIG_FILE_NAME = "./config.properties";
	static Properties props = new Properties();
	static Logger LOGGER = LoggerFactory.getLogger(PropertiesUtil.class);

	static {
		try {
			CodeSource codeSource = PropertiesUtil.class.getProtectionDomain()
					.getCodeSource();
			File jarFile = new File(codeSource.getLocation().toURI().getPath());
			String currentPath = jarFile.getParentFile().getPath();
			LOGGER.info("Current Path: " + currentPath);
			File file = new File(currentPath + File.separator
					+ CONFIG_FILE_NAME);
			InputStream is = new FileInputStream(file);
			props.load(is);
			is.close();
		} catch (Exception e) {
			LOGGER.error("Could not open file: " + CONFIG_FILE_NAME);
		}
	}

	public static String getValue(String key) {
		if (props.containsKey(key)) {
			return props.getProperty(key);
		} else {
			return key;
		}
	}

	public static double getDoubleValue(String key) {
		if (props.containsKey(key)) {
			return new Double(props.getProperty(key)).doubleValue();
		} else {
			return new Double(key).doubleValue();
		}
	}
}
