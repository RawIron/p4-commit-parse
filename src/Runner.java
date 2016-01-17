import java.util.Properties;
import java.util.List;
import java.util.ArrayList;



public class Runner {
    public static void main(String[] args) {
        Runner runner = new Runner();
        runner.run();
    }

    private Settings settings;
    private ReaderWriterFactory utils;
    private WriteP4Output writer;

    public Runner() {
        this.utils = new ReaderWriterFactory();
    }


    public void run() {
        settings = readSettings();
        writer = new WriteP4Output(settings);

        for (Depot depot : settings.depots()) {
            System.out.println("");
            System.out.println("==> DEPOT " + depot.project());

            ArrayList<P4Change> changes = null;
            if (settings.parseOnly()) {
                System.out.println("==> Parse Only enabled");
                String fileName = utils.createFileNamePer(depot, settings) + ".txt";
                System.out.println("Looking for file " + fileName);
                String contents = readChangesFromFileIntoContents(fileName);
                if (!contents.isEmpty()) {
                    changes = parseP4Output(depot, contents);
                }
            } else {
                String output = executeP4Command(depot, settings);
                changes = parseP4Output(depot, output);
            }

            writer.write(depot, changes);
        }
        writer.close();
    }


    private Settings readSettings() {
        System.out.print("Reading properties file");
        PropertiesReaderFilePath preader = new PropertiesReaderFilePath();
        Properties properties = null;
        try {
            properties = preader.read();
        } catch (PropertiesLoadException e) {
            System.out.println(" ==> FAILED <==");
            System.out.println(e.getMessage());
            System.exit(1);
        }
        System.out.println(" ==> ok");

        System.out.print("Create settings");
        SettingsReader sreader = new SettingsReader(properties);
        Settings settings = null;
        try {
            settings = sreader.fill();
        } catch (SettingsInvalidException e) {
            System.out.println(" ==> FAILED <==");
            System.out.println(e.getMessage());
            System.exit(1);
        }
        System.out.println(" ==> ok");
        return settings;
    }

    private String executeP4Command(final Depot depot, final Settings settings) {
        System.out.println("Create p4 command");
        SystemCommand p4Changes = new P4SubmittedChanges();
        p4Changes.optargs("-p " + settings.p4Host());
        String range = "...@" + settings.rangeBegin() + "," + settings.rangeEnd();
        p4Changes.args(depot.path() + range);
        System.out.print("p4 command is: " + p4Changes.command());
        System.out.println(" ==> ok");

        System.out.print("Run p4 changes submitted");
        Exec runner = new Exec();
        runner.execute(p4Changes);
        String output = runner.stdout();
        System.out.println(" ==> ok");

        if (output.trim().isEmpty()) {
            System.out.println("==> P4 output is empty");
            return output;
        }

        if (settings.writeP4Output()) {
            System.out.println("==> write P4 output enabled");
            String fileName = utils.createFileNamePer(depot, settings) + ".txt";
            System.out.print("Writing to file: " + fileName);
            FileWriterWrapper writer = new FileWriterWrapper();
            try {
                writer.write(fileName, output);
            } catch (FileAlreadyExistsException e) {
                System.out.println(" ==> FAILED <==");
                String message = "File already exists.";
                String action = " Please rename or drop existing file.";
                System.out.println(message + action);
            } catch (FileOperationException e) {
                System.out.println(" ==> FAILED <==");
                String message = "Writing stopped unexpectedly";
                String action = " Please remove the file and try again";
                System.out.println(message + action);
            } catch(FileCloseException e) {
                // ignore
            }
            System.out.println(" ==> ok");
        }

        return output;
    }

    private ArrayList<P4Change> parseP4Output(final Depot depot, final String contents) {
        System.out.println("Run parser on change list");
        Parser parser = new Parser(contents, settings);
        ArrayList<P4Change> changes = null;
        try {
            changes = parser.parse();
        } catch (Exception e) {
            System.out.println("==> FAILED <==");
            e.printStackTrace();
        }
        return changes;
    }


    private String readChangesFromFileIntoContents(String fileName) {
        String contents = "";
        try {
            P4ChangesReader reader = new LineByLineAndRemoveEmptyLinesReader(fileName);
            contents = reader.read();
        } catch (FileOperationException e) {
            System.out.println(" ==> FAILED <==");
            String message = "Reading stopped unexpectedly";
            String action = " Please check the file and try again";
            System.out.println(message + action);
        } catch (FileCloseException e) {
            // ignore
        } catch (FileNotAccessableException e) {
            System.out.println(" ==> FAILED <==");
            String message = "File does not exists or is not readable.";
            String action = " Please make sure file exists and it has read rights.";
            System.out.println(message + action);
        } catch (FileContentIsNotPlainTextException e) {
            System.out.println(" ==> FAILED <==");
            String message = "File does not look like a text.";
            String action = " Please check content of file.";
            System.out.println(message + action);
        }
        return contents;
        //System.out.println(contents);
    }

}


class WriteP4Output {
    private Settings settings;
    private ReaderWriterFactory util;
    private P4AppendFileWriter writer;

    public WriteP4Output(Settings settings) {
        this.settings = settings;
        this.util = new ReaderWriterFactory();
        init();
    }

    private void init() {
        if (!(settings.writeToStdout() || settings.writeFilePerDepot())) {
            String fileName = util.createFileName(settings) + "_out.txt";
            try {
                writer = new P4AppendFileWriter(fileName, settings);
            } catch(FileOperationException e) {
                System.out.println(" ==> FAILED <==");
                String message = "Writing stopped unexpectedly";
                String action = " Please remove the file and try again";
                System.out.println(message + action);
            }
        }
    }

    public void write(Depot depot, List<P4Change> changes) {
        if (changes == null) {
            return;
        }

        if (settings.writeToStdout()) {
            System.out.println("==> writing to stdout enabled");
            P4ChangesWriter bodyWriter = new P4SimpleBodyWriter(settings);
            bodyWriter.write(depot.project(), changes);
        } else if (settings.writeFilePerDepot()) {
            System.out.println("==> write file per depot enabled");
            String fileName = util.createFileNamePer(depot, settings) + "_out.txt";
            System.out.println("Writing to file " + fileName);
            P4FileWriter writer = new P4FileWriter(fileName, settings);
            try {
                writer.writeHeader(depot.project());
                writer.write(depot.project(), changes);
            } catch (FileAlreadyExistsException e) {
                System.out.println(" ==> FAILED <==");
                String message = "File already exists.";
                String action = " Please rename or drop existing file.";
                System.out.println(message + action);
            } catch (FileOperationException e) {
                System.out.println(" ==> FAILED <==");
                String message = "Writing stopped unexpectedly";
                String action = " Please remove the file and try again";
                System.out.println(message + action);
            } catch(FileCloseException e) {
                // ignore
            }
        } else {
            System.out.println("Append to file " + writer.fileName());
            try {
                writer.writeHeader(depot.project());
                writer.write(depot.project(), changes);
            } catch (FileOperationException e) {
                System.out.println(" ==> FAILED <==");
                String message = "Writing stopped unexpectedly";
                String action = " Please remove the file and try again";
                System.out.println(message + action);
            } catch(FileCloseException e) {
                // ignore
            }
        }

        P4ChangesWriter statsWriter = new P4StatsWriter();
        statsWriter.write(depot.project(), changes);
    }

    public void close() {
        writer.close();
    }
}