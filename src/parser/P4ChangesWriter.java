package parser;

import java.util.List;


public interface P4ChangesWriter {
    public void write(String project, List<P4Change> changes);
}


class P4SimpleWriter implements P4ChangesWriter {
    private static final String FIELD_DELIMITER = " ";
    public void write(String project, List<P4Change> changes) {
        for (P4Change change: changes) {
            System.out.print(change.id() + FIELD_DELIMITER);
            System.out.print(change.dateWhenChangeSubmitted() + FIELD_DELIMITER);
            System.out.print(change.developer() + FIELD_DELIMITER);
            System.out.println(change.rawBody);
        }
    }
}


class P4SimpleBodyWriter implements P4ChangesWriter {
	private static final String LINE_DELIMITER = System.getProperty("line.separator");
    private static final String FIELD_DELIMITER = "^";
    private Settings settings;

    public P4SimpleBodyWriter(Settings settings) {
    	this.settings = settings;
    }

    public void write(String project, List<P4Change> changes) {
        StringBuilder builder = new StringBuilder();
        for (P4Change change: changes) {
            if (change.hasEmptyBody()) {
                continue;
            }
            builder.append(change.id() + FIELD_DELIMITER);
            builder.append(change.dateWhenChangeSubmitted() + FIELD_DELIMITER);
            builder.append(change.developer() + FIELD_DELIMITER);

            builder.append(change.body.reasonForChange() + FIELD_DELIMITER);
            builder.append(change.body.implementation() + FIELD_DELIMITER);
            builder.append(change.body.tfc() + FIELD_DELIMITER);
            builder.append(change.body.taskId() + FIELD_DELIMITER);
            builder.append(change.body.dbChange() + FIELD_DELIMITER);
            builder.append(change.body.notifyTeam() + FIELD_DELIMITER);
            builder.append(LINE_DELIMITER);
        }
        StdoutWriter writer = new StdoutWriter();
        writer.write(builder.toString());
    }
}

class P4FileWriter {
	private static final String LINE_DELIMITER = System.getProperty("line.separator");
    private static final String FIELD_DELIMITER = "^";
    private Settings settings;
    private String fileName;

    public P4FileWriter(String fileName, Settings settings) {
    	this.fileName = fileName;
    	this.settings = settings;
    }

    public void writeHeader(String project)
    		throws FileAlreadyExistsException,
    		FileOperationException, FileCloseException
    {
        StringBuilder builder = new StringBuilder();

        builder.append("Project" + FIELD_DELIMITER);
        builder.append("CL Number" + FIELD_DELIMITER);
        builder.append("Change Submitted" + FIELD_DELIMITER);
        builder.append("Developer" + FIELD_DELIMITER);
        builder.append("Reason for Change" + FIELD_DELIMITER);
        builder.append("TFC" + FIELD_DELIMITER);
        builder.append("DB Change" + FIELD_DELIMITER);
        builder.append("Notify" + FIELD_DELIMITER);
        builder.append("Ignore Developer" + FIELD_DELIMITER);
        builder.append(LINE_DELIMITER);

        FileWriterWrapper writer = new FileWriterWrapper();
        writer.write(fileName, builder.toString());
    }

    public void write(String project, List<P4Change> changes)
    		throws FileAlreadyExistsException,
    		FileOperationException, FileCloseException
    {
        StringBuilder builder = new StringBuilder();
        for (P4Change change: changes) {
            if (change.hasEmptyBody()) {
                continue;
            }
            builder.append(project + FIELD_DELIMITER);
            builder.append(change.id() + FIELD_DELIMITER);
            builder.append(change.dateWhenChangeSubmitted() + FIELD_DELIMITER);
            builder.append(change.developer() + FIELD_DELIMITER);
            builder.append(change.body.reasonForChange() + FIELD_DELIMITER);
            builder.append(change.body.tfc() + FIELD_DELIMITER);
            builder.append(change.body.dbChange() + FIELD_DELIMITER);
            builder.append(change.body.notifyTeam() + FIELD_DELIMITER);
            builder.append(change.ignoreDeveloper() + FIELD_DELIMITER);
            builder.append(LINE_DELIMITER);
        }
        FileWriterWrapper writer = new FileWriterWrapper();
        writer.write(fileName, builder.toString());
    }
}


class P4AppendFileWriter {
	private static final String LINE_DELIMITER = System.getProperty("line.separator");
    private static final String FIELD_DELIMITER = "^";
    private Settings settings;
    private String fileName;
    private boolean headerWasWritten = false;
    private FileAppendWriterWrapper writer;

    public P4AppendFileWriter(String fileName, Settings settings)
    		throws FileOperationException
    {
    	this.fileName = fileName;
    	this.settings = settings;
        writer = new FileAppendWriterWrapper(fileName);
    }
    public String fileName() {
    	return fileName;
    }

    public void writeHeader(String project)
    		throws FileOperationException, FileCloseException
    {
    	if (headerWasWritten) {
    		return;
    	}
        StringBuilder builder = new StringBuilder();

        builder.append("Project" + FIELD_DELIMITER);
        builder.append("CL Number" + FIELD_DELIMITER);
        builder.append("Change Submitted" + FIELD_DELIMITER);
        builder.append("Developer" + FIELD_DELIMITER);
        builder.append("Reason for Change" + FIELD_DELIMITER);
        builder.append("TFC" + FIELD_DELIMITER);
        builder.append("DB Change" + FIELD_DELIMITER);
        builder.append("Notify" + FIELD_DELIMITER);
        builder.append("Ignore Developer" + FIELD_DELIMITER);
        builder.append(LINE_DELIMITER);

        writer.write(builder.toString());

        headerWasWritten = true;
    }

    public void write(String project, List<P4Change> changes)
    		throws FileOperationException, FileCloseException
    {
        StringBuilder builder = new StringBuilder();
        for (P4Change change: changes) {
            if (change.hasEmptyBody()) {
                continue;
            }
            builder.append(project + FIELD_DELIMITER);
            builder.append(change.id() + FIELD_DELIMITER);
            builder.append(change.dateWhenChangeSubmitted() + FIELD_DELIMITER);
            builder.append(change.developer() + FIELD_DELIMITER);
            builder.append(change.body.reasonForChange() + FIELD_DELIMITER);
            builder.append(change.body.tfc() + FIELD_DELIMITER);
            builder.append(change.body.dbChange() + FIELD_DELIMITER);
            builder.append(change.body.notifyTeam() + FIELD_DELIMITER);
            builder.append(change.ignoreDeveloper() + FIELD_DELIMITER);
            builder.append(LINE_DELIMITER);
        }

        writer.write(builder.toString());
    }

    public void close() {
    	writer.close();
    }
}



class P4StatsWriter implements P4ChangesWriter {

    public void write(String project, List<P4Change> changes) {
        int totalComplete = 0;
        int totalCompleteBody = 0;
        int totalCompleteHeader = 0;
        int totalNoBody = 0;
        int totalMissingHeaderFields = 0;
        int totalMissingMandatoryBodyFields = 0;

        for (P4Change change: changes) {
            if (change.isComplete()) {
                ++totalComplete;
            }
            if (change.hasEmptyBody()) {
                ++totalNoBody;
            }
            if (change.hasAllFields()) {
                ++totalCompleteHeader;
            }
            if (!change.hasEmptyBody() && change.body.hasMandatoryFields()) {
                ++totalCompleteBody;
            }
        }

        System.out.println("==> PARSING SUMMARY");
        System.out.println("  total changes: " + changes.size());
        System.out.println("  total complete: " + totalComplete);
        System.out.println("  total header complete: " + totalCompleteHeader);
        System.out.println("  total body complete: " + totalCompleteBody);
        System.out.println("  total body empty: " + totalNoBody);
        System.out.println("");
    }
}
