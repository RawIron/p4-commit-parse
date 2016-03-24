package parser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


class MaxPatternMismatchReachedException extends Exception {
    private static final long serialVersionUID = 4609661404447134056L;
}


interface PatternMatcher {
    public boolean matched(String line);
}

class ExtractP4ChangesHeader {
    private static final String CHANGE_BEGIN_PATTERN = "^Change [0-9]++ ";
    private static final String FIELD_DELIMITER = " ";
    private static final String EMAIL_DELIMITER = "@";
    private static final int EMAIL_NAME = 0;
    private static final int ID = 1;
    private static final int DATE_SUBMITTED= 3;
    private static final int DEVELOPER = 5;
    private static final String DEVELOPER_DELIMITER = "\\\\";
    private static final int DEVELOPER_DOMAIN = 0;
    private static final int DEVELOPER_USER = 1;

    private String[] ignoreNames;
    private Matcher matcher;

    public ExtractP4ChangesHeader(final String[] ignoreNames) {
    	this.ignoreNames = ignoreNames;
        Pattern pattern = Pattern.compile(CHANGE_BEGIN_PATTERN);
        matcher = pattern.matcher("");
    }

    public boolean matched(final String line) {
        matcher.reset(line);
        return matcher.lookingAt();
    }
    public void extractPart(final String line, P4Change change) {
       String[] words = line.split(FIELD_DELIMITER);
       change.id(Integer.parseInt(words[ID]));
       change.dateWhenChangeSubmitted(words[DATE_SUBMITTED]);
       change.developer(words[DEVELOPER].split(EMAIL_DELIMITER)[EMAIL_NAME]);
       String[] developerFullname = change.developer().split(DEVELOPER_DELIMITER);
       if (developerFullname.length == 2) {
    	   String developerWithoutDomain = developerFullname[DEVELOPER_USER];
	       if (Arrays.asList(ignoreNames).contains(developerWithoutDomain)) {
	    	   change.ignoreDeveloper(true);
	       }
       }
       change.rawBody = "";
       change.body = null;
    }
}


public class ExtractP4Changes {
    private static final int MAX_LINES_WITHOUT_PATTERN_MATCH = 100;
    private static final String LINE_DELIMITER = "\n";
    // private static final String LINE_DELIMITER = System.getProperty("line.separator");
    private Settings settings;

    public ExtractP4Changes(Settings settings) {
    	this.settings = settings;
    }

    public ArrayList<P4Change> parse(final String changes) throws MaxPatternMismatchReachedException {
    	ExtractP4ChangesHeader header = new ExtractP4ChangesHeader(settings.ignoreNames());
        P4Change change = new P4Change();
        ArrayList<P4Change> extractedChanges = new ArrayList<P4Change>();
        int linesWithoutPatternMatchCounter = 0;
        int lineNumber = 0;

        String[] lines = changes.split(LINE_DELIMITER);
        for (String line : lines) {
            if (!(++linesWithoutPatternMatchCounter < MAX_LINES_WITHOUT_PATTERN_MATCH)) {
                throw new MaxPatternMismatchReachedException();
            }
            ++lineNumber;
            if (header.matched(line)) {
                change = new P4Change();
                change.beginsAtLine(lineNumber);
                header.extractPart(line, change);
                extractedChanges.add(change);
                linesWithoutPatternMatchCounter = 0;
            } else {
                change.rawBody += line + LINE_DELIMITER;
            }
        }
        return extractedChanges;
    }
}
