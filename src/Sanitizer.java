public class Sanitizer {
    private static final String MULTIPLE_SPACES = "[ \\t\\x0B\\f\\r]";
    private static final String NEWLINES = "[\\n\\x0B\\f\\r]";

    public static String RemoveSpaces(String dirty) {
        return dirty.replaceAll(MULTIPLE_SPACES, "");
    }
    public static String TranslateMultipleSpacesIntoOne(String dirty) {
        return dirty.replaceAll(MULTIPLE_SPACES, " ");
    }
    public static String TranslateNewlineIntoPunctation(String dirty) { return dirty.replaceAll(NEWLINES, "; "); }
}
