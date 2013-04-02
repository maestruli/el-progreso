package reader.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PropertiesUtil {
	
	static String CONFIG_FILE_NAME = "config.properties";
	static Properties props = new Properties();
	static Logger LOGGER = LoggerFactory.getLogger(PropertiesUtil.class);

	static {
		InputStream file = ClassLoader
				.getSystemResourceAsStream(CONFIG_FILE_NAME);
		try {
			props.load(file);
			file.close();
		} catch (IOException e) {
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
