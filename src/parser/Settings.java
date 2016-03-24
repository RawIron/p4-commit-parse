package parser;

import java.util.ArrayList;
import java.util.Properties;
import java.util.Calendar;
import java.io.File;
import java.text.SimpleDateFormat;


class SettingsInvalidException extends Exception {
	private static final long serialVersionUID = 1804234455423437340L;
	public SettingsInvalidException(String message) {
		super(message);
	}
}


class CalendarAndClock {
	private static final String DATE_TIME_FORMAT_NOW = "yyyy-mm-dd HH:mm:ss";
	//	private static final String DATE_FORMAT_NOW = "yyyy-mm-dd";
	private static final String DATE_FORMAT_NOW = "yyyymmdd";

	public String today() {
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_NOW);
		return sdf.format(cal.getTime());
	}
}

class Depot {
	private String project = "";
	private String path = "";
	private boolean skip = false;

	public void project(final String value) {
		project = value;
	}
	public String project() {
		return project;
	}
	public void path(final String value) {
		path = value;
	}
	public String path() {
		return path;
	}
	public void skip(final boolean value) {
		skip = value;
	}
}

public class Settings {
	private int rangeBegin = 0;
	private int rangeEnd = 0;
	private ArrayList<Depot> depots = new ArrayList<Depot>();
	private String[] ignoreNames = null;
	private String workDirectory = "";
	private boolean parseOnly = false;
	private String p4Host = "your.perforce.com:1555";
	private String writeFileName = "all_depots";
	private boolean writeToStdout = false;
	private boolean writeP4Output = true;
	private boolean writeFilePerDepot = false;
	private int depotSelected = -1;

	public void rangeBegin(final int value) {
		rangeBegin = value;
	}
	public int rangeBegin() {
		return rangeBegin;
	}
	public void rangeEnd(final int value) {
		rangeEnd = value;
	}
	public int rangeEnd() {
		return rangeEnd;
	}
	public void depots(final Depot value) {
		depots.add(value);
	}
	public ArrayList<Depot> depots() {
		return depots;
	}
	public void ignoreNames(final String[] value) {
		ignoreNames = value;
	}
	public String[] ignoreNames() {
		return ignoreNames;
	}
	public void workDirectory(final String value) {
		workDirectory = value;
	}
	public String workDirectory() {
		return workDirectory;
	}
	public void parseOnly(final boolean value) {
		parseOnly = value;
	}
	public boolean parseOnly() {
		return parseOnly;
	}
	public boolean writeToStdout() {
		return writeToStdout;
	}
	public String writeFileName() {
		return writeFileName;
	}
	public boolean writeFilePerDepot() {
		return writeFilePerDepot;
	}
	public String p4Host() {
		return p4Host;
	}
	public boolean writeP4Output() {
		return writeP4Output;
	}
	public int depotSelected() {
		return depotSelected;
	}
}


class SettingsReader {
	private static final String FIELD_DELIMITER = ",";
	private static final int DEPOT_PROJECT = 0;
	private static final int DEPOT_PATH = 1;

	private ArrayList<String> depotsProperties = null;
	private Properties properties;
	private CalendarAndClock calendar;
	private int depotIndex = 0;
	private boolean depotSelected = false;
	private Settings settings;

	public SettingsReader(final Properties properties) {
		this.properties = properties;
		this.calendar = new CalendarAndClock();
	}

	public SettingsReader(final Properties properties, final CalendarAndClock calendar) {
		this.properties = properties;
		this.calendar = calendar;
	}

	public SettingsReader(final Properties properties, final CalendarAndClock calendar, final int depotIndex) {
		this.properties = properties;
		this.calendar = calendar;
		this.depotIndex = depotIndex;
		this.depotSelected= true;
	}

	public Settings fill() throws SettingsInvalidException {
		settings = new Settings();
		fillRange();
		fillDepots();
		fillIgnores();
		fillWorkDirectory();
		fillParseOnly();
		return settings;
	}

	private void fillRange() throws SettingsInvalidException {
		String today = calendar.today();
		String rangesProperty = properties.getProperty("RANGE." + today.trim());
		if (rangesProperty == null) {
			String message = "No RANGE found for date: " + today + ".";
			String action = " Please add to properties file RANGE." + today;
			throw new SettingsInvalidException(message + action);
		}

		String[] rawRanges = rangesProperty.split(FIELD_DELIMITER);
		if (rawRanges.length != 2) {
			String message = "RANGE expects two numbers separated by a comma, but found " + rangesProperty;
			throw new SettingsInvalidException(message);
		}

		int number;
		if ((number = isValidPositiveNumber(rawRanges[0])) < 0) {
			String message = "RANGE Begin is not a valid number: " + rawRanges[0] + ".";
			String action = " Please make it a positive number.";
			throw new SettingsInvalidException(message + action);
		}
		settings.rangeBegin(number);

		if ((number = isValidPositiveNumber(rawRanges[1])) < 0) {
			String message = "RANGE End is not a valid number: " + rawRanges[1] + ".";
			String action = " Please make it a positive number.";
			throw new SettingsInvalidException(message + action);
		}
		settings.rangeEnd(number);
	}

	private void fillDepots() throws SettingsInvalidException {
		depotsProperties = new ArrayList<String>();
		if (depotSelected) {
			readDepotPropertyWith(depotIndex);
		} else {
			readAllDepotProperties();
		}

		if (!(depotsProperties.size() > 0)) {
			String message = "No depots found.";
			String action = " Please add at least one depot to the properties file.";
			throw new SettingsInvalidException(message + action);
		}

		for (String value : depotsProperties) {
			String[] values = value.split(FIELD_DELIMITER);

			if (values.length != 2) {
				String message = "DEPOT should have a project and a path separated by a comma, but found " + value;
				throw new SettingsInvalidException(message);
			}

			if ((values[DEPOT_PROJECT] == null) || (values[DEPOT_PROJECT].trim().length() == 0)) {
				String message = "DEPOT should have a project, but found " + values[DEPOT_PROJECT];
				throw new SettingsInvalidException(message);
			}
			if ((values[DEPOT_PATH] == null) || (values[DEPOT_PATH].trim().length() == 0)) {
				String message = "DEPOT should have a path, but found " + values[DEPOT_PATH];
				throw new SettingsInvalidException(message);
			}

			Depot depot = new Depot();
			depot.project(values[DEPOT_PROJECT]);
			depot.path(values[DEPOT_PATH]);
			settings.depots(depot);
		}

		for (Depot depot : settings.depots()) {
			if (!isDepotAccessable(depot.path())) {
				depot.skip(true);
				String message = "parser.Depot is not accessable " + depot;
				String action = " Please check the path to the depot.";
				throw new SettingsInvalidException(message + action);
			}
		}
	}

	private void readDepotPropertyWith(final int index) {
		String depotProperty;
		String keyName;
		keyName = "DEPOT.PATH." + index;
		depotProperty = properties.getProperty(keyName);
		depotsProperties.add(depotProperty);
	}

	private void readAllDepotProperties() {
		final int TryAtLeast = 25;
		String depotProperty;
		String keyName;

		for (int i=0, counter=0; i<TryAtLeast; i += counter+1) {
			counter = 0;
			keyName = "DEPOT.PATH." + (i + counter);
			while ((depotProperty = properties.getProperty(keyName)) != null) {
				depotsProperties.add(depotProperty);
				++counter;
				keyName = "DEPOT.PATH." + (i + counter);
			}
		}
	}

	private void fillIgnores() {
		String ignoreProperty = properties.getProperty("IGNORE.NAMES");
		if (ignoreProperty == null) {
			// ignore for now
		}
		ignoreProperty = Sanitizer.RemoveSpaces(ignoreProperty);
		settings.ignoreNames(ignoreProperty.split(FIELD_DELIMITER));
	}

	private void fillWorkDirectory() throws SettingsInvalidException {
		String workdirectoryProperty = properties.getProperty("WORK.DIRECTORY");
		workdirectoryProperty = workdirectoryProperty.trim();

		if (workdirectoryProperty == null || workdirectoryProperty.isEmpty()) {
			String message = "No property WORK.DIRECTORY found.";
			String action = " Please add this property.";
			throw new SettingsInvalidException(message + action);
		}
		File workdir = new File(workdirectoryProperty);
		ReaderWriterUtil validator = new ReaderWriterUtil();
		if (!validator.doesDirectoryExistAndIsReabable(workdir)) {
			String message = "WORK.DIRECTORY is not accessable: " + workdirectoryProperty;
			String action = " Please provide a valid directory.";
			throw new SettingsInvalidException(message + action);
		}
		if (!workdirectoryProperty.endsWith("/")
			&& !workdirectoryProperty.endsWith("\\") )
		{
			workdirectoryProperty += "/";
		}
		settings.workDirectory(workdirectoryProperty);
	}


	private void fillParseOnly() throws SettingsInvalidException {
		String parseOnlyProperty = properties.getProperty("PARSE.ONLY");
		if (parseOnlyProperty == null || parseOnlyProperty.trim().isEmpty()) {
			// ignore
		} else {
			settings.parseOnly(true);
		}
	}


	private int isValidPositiveNumber(final String number) {
		int result;
		try {
			result = Integer.parseInt(number);
		} catch (NumberFormatException e) {
			result = -1;
		}
		return result;
	}

	private boolean isDepotAccessable(final String pathToDepot) {
		return true;
	}
}
