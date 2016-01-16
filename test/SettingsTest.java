import static org.junit.Assert.*;

import org.junit.Test;
import java.util.Properties;


class TodayIs27042013 extends CalendarAndClock {
	public String today() {
		return "20130427";
	}
}
class TodayIs28042013 extends CalendarAndClock {
	public String today() {
		return "20130428";
	}
}
class TodayIs29042013 extends CalendarAndClock {
	public String today() {
		return "20130429";
	}
}
class TodayIs30042013 extends CalendarAndClock {
	public String today() {
		return "20130430";
	}
}


public class SettingsTest {

	private Settings topic(CalendarAndClock t, int index) throws SettingsInvalidException {
		PropertiesReaderFilePath preader = new PropertiesReaderFilePath(true);
		Properties properties = null;
		try {
			properties = preader.read();
		} catch (PropertiesLoadException e) { e.printStackTrace(); }

		SettingsReader sreader = new SettingsReader(properties, t, index);
		Settings settings = null;
		settings = sreader.fill();
		return settings;
	}

	@Test
	public void test_readValidSettings() {
		Settings settings = null;
		try {
			settings = topic(new TodayIs27042013(), 4);
		} catch (SettingsInvalidException e) { e.printStackTrace(); }

		assertEquals(1000112, settings.rangeBegin());
		assertEquals(1000214, settings.rangeEnd());
		assertEquals("security", settings.depots().get(0).project());
		assertEquals("//depot/projects/security/ml/src/main/java/com/domain/kernel/",
					 settings.depots().get(0).path().trim());
		assertEquals("/tmp/", settings.workDirectory());
		assertTrue(settings.parseOnly());
		assertEquals("ignoreMe", settings.ignoreNames()[0]);
		assertEquals("meToo", settings.ignoreNames()[1]);
	}

	@Test
	public void test_invalidRangeEnd() {
		Settings settings = null;
		String message = "";
		try {
			settings = topic(new TodayIs28042013(), 4);
		} catch (SettingsInvalidException e) {
			message = e.getLocalizedMessage();
		}
		assertTrue(message.startsWith("RANGE End is not a valid number"));
	}

	@Test
	public void test_tooManyRanges() {
		Settings settings = null;
		String message = "";
		try {
			settings = topic(new TodayIs30042013(), 4);
		} catch (SettingsInvalidException e) {
			message = e.getLocalizedMessage();
		}
		assertTrue(message.startsWith("RANGE expects two numbers separated by a comma"));
	}

	@Test
	public void test_missingProjectInDepot() {
		Settings settings = null;
		String message = "";
		try {
			settings = topic(new TodayIs27042013(), 0);
		} catch (SettingsInvalidException e) {
			message = e.getLocalizedMessage();
		}
		assertTrue(message.startsWith("DEPOT should have a project and a path separated by a comma"));
	}

	@Test
	public void test_missingPathInDepot() {
		Settings settings = null;
		String message = "";
		try {
			settings = topic(new TodayIs27042013(), 1);
		} catch (SettingsInvalidException e) {
			message = e.getLocalizedMessage();
		}
		assertTrue(message.startsWith("DEPOT should have a project"));
	}

	@Test
	public void test_emptyProjectInDepot() {
		Settings settings = null;
		String message = "";
		try {
			settings = topic(new TodayIs27042013(), 3);
		} catch (SettingsInvalidException e) {
			message = e.getLocalizedMessage();
		}
		assertTrue(message.startsWith("DEPOT should have a project"));
	}
}
