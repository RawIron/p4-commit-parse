import static org.junit.Assert.*;

import org.junit.Test;
import java.util.Properties;


public class PropertiesReaderTest {
	private Properties topic() {
		PropertiesReader reader = new PropertiesReaderFilePath(true);
		Properties config = null;
		try {
			config = reader.read();
		} catch (PropertiesLoadException e) {}
		return config;
	}

	@Test
	public void test_readSingleValue() {
		assertEquals("ignoreMe, meToo", topic().getProperty("IGNORE.NAMES"));
	}

	@Test
	public void test_readDateValue() {
		assertEquals("1000112,1000214", topic().getProperty("RANGE.20130427"));
	}

	@Test
	public void test_readIndexedValue() {
		int index = 1;
		String key = "DEPOT.PATH." + index;
		assertEquals("trader,", topic().getProperty(key).trim());
	}

	@Test
	public void test_parseInvalidInteger() {
		boolean caughtException = false;
		try {
			int result = Integer.parseInt("123A4");
		} catch (NumberFormatException e) {
			caughtException = true;
		}
		assertTrue(caughtException);
	}
}
