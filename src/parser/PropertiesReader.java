package parser;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;


class PropertiesLoadException extends Exception {
	private static final long serialVersionUID = -6295258571192607655L;
	public PropertiesLoadException(String message) {
		super(message);
	}
}


public abstract class PropertiesReader {
	protected static final String PROPERTY_FILE_PATH = "/home/achtmhz/workspace_java/p4-commit-parse/";
	private static final String PROPERTY_FILE_NAME = "p4CL.properties";
	private static final String PROPERTY_TEST_FILE_NAME = "p4CLTest.properties";

	protected Properties properties = new Properties();
	protected String usePropertyFile = "";

	public PropertiesReader() {
		usePropertyFile = PROPERTY_FILE_NAME;
	}
	public PropertiesReader(Boolean isTest) {
		if (isTest) {
			usePropertyFile = PROPERTY_TEST_FILE_NAME;
		}
	}

	public Properties read() throws PropertiesLoadException {
		try {
			load();
		} catch (IOException e) {
			String message = "Failed to load properties: " + usePropertyFile + ".";
			String action = " Please make sure file exists, is readable.";
			throw new PropertiesLoadException(message + action);
	    }
		return properties;
	}

	public abstract void load() throws IOException;
}


class PropertiesReaderFilePath extends PropertiesReader {
	public PropertiesReaderFilePath() {
		super();
	}
	public PropertiesReaderFilePath(Boolean isTest) {
		super(isTest);
	}

	public void load() throws IOException{
		properties.load(new FileInputStream(PROPERTY_FILE_PATH + usePropertyFile));
	}
}


class PropertiesReaderClassPath extends PropertiesReader {
	public PropertiesReaderClassPath() {
		super();
	}
	public PropertiesReaderClassPath(Boolean isTest) {
		super(isTest);
	}

	public void load() throws IOException{
		properties.load(PropertiesReader.class.getClassLoader().getResourceAsStream(usePropertyFile));
	}
}
