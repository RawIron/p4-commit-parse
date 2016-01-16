import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Arrays;
import java.util.ArrayList;

abstract class ExtractP4BodyPart {
    private static final String KEY_VALUE_DELIMITER = ":";
    public static final String YES_PATTERN = "[Yy]|YES|Yes";
    public static final String NO_PATTERN = "[Nn]|NO|No";
    private static final int HEAD = 0;
    private static final int KEY_POS = 0;
    private static final int VALUE_POS = 1;

    private Matcher matcher;
    private ExtractP4Body parser;

    public ExtractP4BodyPart(final ExtractP4Body parser) {
        this.parser = parser;
        Pattern pattern = Pattern.compile(myPattern(), Pattern.CASE_INSENSITIVE);
        matcher = pattern.matcher("");
    }

    public boolean matched(final String line) {
        matcher.reset(line);
        return matcher.lookingAt();
    }
    public String[] extractPart(P4Body intoBody, final String[] fromLines) {
        int i=0;
        String[] keyValue = fromLines[HEAD].split(KEY_VALUE_DELIMITER,2);
        if (keyValue.length > 1) {
            extract(intoBody, keyValue[VALUE_POS]);
        }
        for (i=1; i<fromLines.length && !(parser.findMatcher(fromLines[i]) != null); ++i) {
            extract(intoBody, fromLines[i]);
        }
        return Arrays.copyOfRange(fromLines, i, fromLines.length);
    }

    public abstract void extract(P4Body intoBody, final String fromLine);
    public abstract String myPattern();
}


class ExtractP4BodyReason extends ExtractP4BodyPart {
    private static final String PATTERN = "^(REASON FOR CHANGE|REASON)";
    public ExtractP4BodyReason(ExtractP4Body parser) {
        super(parser);
    }

    @Override
    public void extract(P4Body body, final String value) {
    	body.reasonForChange(value);
    }
    @Override
    public String myPattern() {
        return PATTERN;
    }
}

class ExtractP4BodyTfc extends ExtractP4BodyPart {
    private static final String PATTERN = "^TFC";

    public ExtractP4BodyTfc(ExtractP4Body parser) {
        super(parser);
    }

    @Override
    public void extract(P4Body body, final String value) {
    	body.tfc(value);
    }
    @Override
    public String myPattern() {
        return PATTERN;
    }
}

class ExtractP4BodyTaskId extends ExtractP4BodyPart {
    private static final String PATTERN = "^(TASK ID|TASKID)";

    public ExtractP4BodyTaskId(ExtractP4Body parser) {
        super(parser);
    }

    @Override
    public void extract(P4Body body, final String value) {
    	body.taskId(value);
    }
    @Override
    public String myPattern() {
        return PATTERN;
    }
}

class ExtractP4BodyReleaseNumber extends ExtractP4BodyPart {
    private static final String PATTERN = "^RM";

    public ExtractP4BodyReleaseNumber(ExtractP4Body parser) {
        super(parser);
    }

    @Override
    public void extract(P4Body body, final String value) {
    	body.taskId(value);
    }
    @Override
    public String myPattern() {
        return PATTERN;
    }
}

class ExtractP4BodyImplementation extends ExtractP4BodyPart {
    private static final String PATTERN = "^(IMPLEMENTATION|IMPL)";
    public ExtractP4BodyImplementation(ExtractP4Body parser) {
        super(parser);
    }

    @Override
    public void extract(P4Body body, final String value) {
    	body.implementation(value);
    }
    @Override
    public String myPattern() {
        return PATTERN;
    }
}

class ExtractP4BodyReview extends ExtractP4BodyPart {
    private static final String PATTERN = "^(Review|Reviewers|Reviewed by)";
    public ExtractP4BodyReview(ExtractP4Body parser) {
        super(parser);
    }

    @Override
    public void extract(P4Body body, final String value) {
    	body.review(value);
    }
    @Override
    public String myPattern() {
        return PATTERN;
    }
}

class ExtractP4BodyAuthor extends ExtractP4BodyPart {
    private static final String PATTERN = "^Author";
    public ExtractP4BodyAuthor(ExtractP4Body parser) {
        super(parser);
    }

    @Override
    public void extract(P4Body body, final String value) {
    	body.author(value);
    }
    @Override
    public String myPattern() {
        return PATTERN;
    }
}

class ExtractP4BodyObserver extends ExtractP4BodyPart {
    private static final String PATTERN = "^Observer";
    public ExtractP4BodyObserver(ExtractP4Body parser) {
        super(parser);
    }

    @Override
    public void extract(P4Body body, final String value) {
    	body.observer(value);
    }
    @Override
    public String myPattern() {
        return PATTERN;
    }
}

class ExtractP4BodyBuildFix extends ExtractP4BodyPart {
    private static final String PATTERN = "^Build Fix Approved";
    public ExtractP4BodyBuildFix(ExtractP4Body parser) {
        super(parser);
    }

    @Override
    public void extract(P4Body body, final String value) {
    	body.buildFixApproved(value.trim());
    }
    @Override
    public String myPattern() {
        return PATTERN;
    }
}

class ExtractP4BodyDbchange extends ExtractP4BodyPart {
    private static final String PATTERN = "^(DB CHANGE|DB)";
    public ExtractP4BodyDbchange(ExtractP4Body parser) {
        super(parser);
    }

    @Override
    public void extract(P4Body body, final String value) {
        if (value.trim().matches(YES_PATTERN)) {
        	body.dbChange(true);
        } else if (value.trim().matches(NO_PATTERN)) {
        	body.dbChange(false);
        }
    }
    @Override
    public String myPattern() {
        return PATTERN;
    }
}

class ExtractP4BodyNotifyTeam extends ExtractP4BodyPart {
    private static final String PATTERN = "^NOTIFY:";
    public ExtractP4BodyNotifyTeam(ExtractP4Body parser) {
        super(parser);
    }

    @Override
    public void extract(P4Body body, final String value) {
    	body.notifyTeam(value.trim());
    }
    @Override
    public String myPattern() {
        return PATTERN;
    }
}

class ExtractP4BodyNotifyChoice extends ExtractP4BodyPart {
    private static final String PATTERN = "^NOTIFY .*Y/N";
    public ExtractP4BodyNotifyChoice(ExtractP4Body parser) {
        super(parser);
    }

    @Override
    public void extract(P4Body body, final String value) {
        if (value.trim().matches(YES_PATTERN)) {
        	body.notifyChoice(true);
        } else if (value.trim().matches(NO_PATTERN)) {
        	body.notifyChoice(false);
        }
    }
    @Override
    public String myPattern() {
        return PATTERN;
    }
}

class ExtractP4BodyReleaseNote extends ExtractP4BodyPart {
    private static final String PATTERN = "^RELEASE NOTE";
    public ExtractP4BodyReleaseNote(ExtractP4Body parser) {
        super(parser);
    }

    @Override
    public void extract(P4Body body, final String value) {
        if (value.trim().matches(YES_PATTERN)) {
        	body.releaseNote(true);
        } else if (value.trim().matches(NO_PATTERN)) {
        	body.releaseNote(false);
        }
    }
    @Override
    public String myPattern() {
        return PATTERN;
    }
}

public class ExtractP4Body {
    private static final String LINE_DELIMITER = "\n";
    private static final int HEAD = 0;
    private static final int MAX_LINES_WITHOUT_PATTERN_MATCH = 5;

    private ArrayList<ExtractP4BodyPart> matchers = new ArrayList<ExtractP4BodyPart>();

    public ExtractP4Body() {
        matchers.add(new ExtractP4BodyReason(this));
        matchers.add(new ExtractP4BodyTfc(this));
        matchers.add(new ExtractP4BodyTaskId(this));
        matchers.add(new ExtractP4BodyImplementation(this));
        matchers.add(new ExtractP4BodyReview(this));
        matchers.add(new ExtractP4BodyAuthor(this));
        matchers.add(new ExtractP4BodyObserver(this));
        matchers.add(new ExtractP4BodyNotifyTeam(this));
        matchers.add(new ExtractP4BodyNotifyChoice(this));
        matchers.add(new ExtractP4BodyDbchange(this));
        matchers.add(new ExtractP4BodyReleaseNote(this));
        matchers.add(new ExtractP4BodyReleaseNumber(this));
        matchers.add(new ExtractP4BodyBuildFix(this));
    }

    public ExtractP4BodyPart findMatcher(final String line) {
        for (int i=0; i<matchers.size(); ++i) {
            if (matchers.get(i).matched(line)) {
                return matchers.get(i);
            }
        }
        return null;
    }

    public P4Body parse(final String rawBody) throws MaxPatternMismatchReachedException {
        int linesWithoutPatternMatchCounter = 0;
        P4Body body = new P4Body();

        String[] lines = rawBody.split(LINE_DELIMITER);
        while (lines.length > 0) {
            ExtractP4BodyPart foundMatcher = findMatcher(lines[HEAD]);
            if (foundMatcher != null) {
                lines = foundMatcher.extractPart(body, lines);
                linesWithoutPatternMatchCounter = 0;
            } else {
                lines = Arrays.copyOfRange(lines, 1, lines.length);
                if (!(++linesWithoutPatternMatchCounter < MAX_LINES_WITHOUT_PATTERN_MATCH)) {
                    throw new MaxPatternMismatchReachedException();
                }
            }
        }
        return body;
    }
}
