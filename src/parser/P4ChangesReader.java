package parser;

import java.io.*;



interface P4ChangesReader {
    public String read() throws FileOperationException, FileCloseException;
}


class LineByLineAndRemoveEmptyLinesReader implements P4ChangesReader {
    StringBuilder contents = new StringBuilder();
    BufferedReader reader = null;

    public LineByLineAndRemoveEmptyLinesReader(String fileName)
    		throws FileNotAccessableException, FileContentIsNotPlainTextException,
    		FileOperationException
	{
    	ReaderWriterFactory readerFactory = new ReaderWriterFactory();
    	this.reader = readerFactory.createReaderFrom(fileName);
    }

    public LineByLineAndRemoveEmptyLinesReader(BufferedReader reader) {
        this.reader = reader;
    }


    public String read() throws FileOperationException, FileCloseException {
        try {
        	fillContents();
        } catch (IOException e) {
            throw new FileOperationException();
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e) {
                throw new FileCloseException();
            }
        }
        return contents.toString();
    }

    private void fillContents() throws IOException {
        String text = null;
        while ((text = reader.readLine()) != null) {
            text = text.trim();
            if (!text.equals("")) {
                contents
                    .append(text)
                    .append(System.getProperty(
                        "line.separator"));
            }
        }
    }
}
