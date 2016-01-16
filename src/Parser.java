import java.util.ArrayList;


public class Parser {
    private String contents;
    private ArrayList<P4Change> changes = new ArrayList<P4Change>();
    private Settings settings;

    public Parser(String contents, Settings settings) {
    	this.contents = contents;
    	this.settings = settings;
    }

    public ArrayList<P4Change> parse() {
        sanitizeContents();
        readChangesFromContents();
        readBodyFromChanges();
        return changes;
    }

    private void sanitizeContents() {
        contents = Sanitizer.TranslateMultipleSpacesIntoOne(contents);
    }

    private void readChangesFromContents() {
        ExtractP4Changes extractor = new ExtractP4Changes(settings);
        try {
            changes = extractor.parse(contents);
        } catch (MaxPatternMismatchReachedException e) {}
        //P4ChangesWriter writer = new P4SimpleWriter();
        //writer.write(changes);
    }

    private void readBodyFromChanges() {
        ExtractP4Body bodyExtractor = new ExtractP4Body();
        for (P4Change change : changes) {
            try {
                change.body = bodyExtractor.parse(change.rawBody);
            } catch (MaxPatternMismatchReachedException e) {}
        }
    }
}
