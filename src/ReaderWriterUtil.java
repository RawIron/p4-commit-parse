import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;


class FileNotAccessableException extends Exception {
	private static final long serialVersionUID = 3613966698763557212L;
}
class FileContentIsNotPlainTextException extends Exception {
	private static final long serialVersionUID = 7436559304943087671L;
}
class FileOperationException extends Exception {
	private static final long serialVersionUID = 1165541514618349222L;
}
class FileCloseException extends Exception {
	private static final long serialVersionUID = 3647685213582292872L;
}
class FileAlreadyExistsException extends Exception {
	private static final long serialVersionUID = -7340856298190071036L;
}


public class ReaderWriterUtil {

    public boolean doesFileExist(File file) {
    	if (file.exists() && file.isFile()) {
    		return true;
    	}
    	return false;
    }

    public boolean doesFileExistAndIsReabable(File file) {
    	if (file.exists() && file.isFile() && file.canRead()) {
    		return true;
    	}
    	return false;
    }

    public boolean doesDirectoryExistAndIsReabable(File file) {
    	if (file.exists() && file.isDirectory()) {
    		return true;
    	}
    	return false;
    }

    public boolean isTypeOfFileContentPlainText(File file) {
    	// Apache Tika does it.
    	return true;
    }
}


class ReaderWriterFactory {
	private ReaderWriterUtil util;
	public ReaderWriterFactory() {
		this.util = new ReaderWriterUtil();
	}

    public BufferedReader createReaderFrom(String fileName)
    		throws FileNotAccessableException, FileContentIsNotPlainTextException,
    		FileOperationException
	{
    	BufferedReader reader = null;
        File file = new File(fileName);
    	if (!util.doesFileExistAndIsReabable(file)) {
            throw new FileNotAccessableException();
        }
    	if (!util.isTypeOfFileContentPlainText(file)) {
            throw new FileContentIsNotPlainTextException();
        }
        try {
            reader = new BufferedReader(new FileReader(file));
        } catch (IOException e) {
        	throw new FileOperationException();
        }
        return reader;
    }

    public BufferedWriter createWriterFrom(String fileName)
    		throws FileAlreadyExistsException,
    		FileOperationException
	{
    	BufferedWriter writer = null;
        File file = new File(fileName);
    	if (util.doesFileExist(file)) {
            throw new FileAlreadyExistsException();
        }
        try {
            writer = new BufferedWriter(new FileWriter(file));
        } catch (IOException e) {
        	throw new FileOperationException();
        }
        return writer;
    }

    public BufferedWriter createAppendWriterFrom(String fileName)
    		throws FileOperationException
	{
    	BufferedWriter writer = null;
        File file = new File(fileName);
        try {
            writer = new BufferedWriter(new FileWriter(file));
        } catch (IOException e) {
        	throw new FileOperationException();
        }
        return writer;
    }

    public String createFileNamePer(Depot depot, Settings settings) {
    	CalendarAndClock calendar = new CalendarAndClock();
    	return settings.workDirectory() + depot.project() + calendar.today();
    }

    public String createFileName(Settings settings) {
    	CalendarAndClock calendar = new CalendarAndClock();
    	return settings.workDirectory() + settings.writeFileName() + calendar.today();
    }
}


class StdoutWriter {
	private static final String LINE_DELIMITER = System.getProperty("line.separator");

	public void write(String output) {
		String[] lines = output.split(LINE_DELIMITER);
		for (String line : lines) {
			System.out.println(line);
		}
	}
}


class FileWriterWrapper {

	public void write(String fileName, String output)
			throws FileAlreadyExistsException,
			FileOperationException, FileCloseException
	{
		BufferedWriter writer = null;

		try {
			ReaderWriterFactory writerFactory = new ReaderWriterFactory();
			writer = writerFactory.createWriterFrom(fileName);
		} catch (Exception e) {
			throw new FileAlreadyExistsException();
		}

		try {
			writer.write(output);
		} catch (IOException e) {
			throw new FileOperationException();
		} finally {
			try {
				writer.close();
			} catch (IOException e) {
				throw new FileCloseException();
			}
		}
	}
}


class FileAppendWriterWrapper {
	private String fileName;
	private BufferedWriter writer;

	public FileAppendWriterWrapper(String fileName) throws FileOperationException {
		this.fileName = fileName;
		init();
	}
	private void init() throws FileOperationException {
		try {
			ReaderWriterFactory writerFactory = new ReaderWriterFactory();
			writer = writerFactory.createAppendWriterFrom(fileName);
		} catch (Exception e) {
			throw new FileOperationException();
		}
	}

	public String fileName() {
		return fileName;
	}

	public void write(String output)
			throws FileOperationException, FileCloseException
	{
		try {
			writer.write(output);
		} catch (IOException e) {
			try {
				writer.close();
			} catch (IOException ex) {
				throw new FileCloseException();
			}
			throw new FileOperationException();
		}
	}

	public void close() {
		try {
			writer.close();
		} catch (IOException ex) {}
	}
}
