package parser;

import parser.P4Body;

class P4Change {
	// header
    private static final int TOTAL_FIELDS = 3;
    private int beginsAtLine = 0;
    private int id;
    private int idUpdatedCounter = 0;
    private String dateWhenChangeSubmitted = "";
    private String developer = "";
    // not part of header
    private boolean ignoreDeveloper = false;
    private int ignoreDeveloperUpdatedCounter = 0;
    public String rawBody = "";
    public P4Body body = null;

    private int fieldsFoundCounter = 0;


    public void beginsAtLine(int lineNumber) {
        beginsAtLine = lineNumber;
    }
    public int beginsAtLine() {
        return beginsAtLine;
    }

    public void id(int value) {
        if (idUpdatedCounter == 0) {
        	++idUpdatedCounter;
            ++fieldsFoundCounter;
        }
        id = value;
    }
    public int id() {
        return id;
    }

    public void dateWhenChangeSubmitted(String value) {
        if (dateWhenChangeSubmitted.equals("") && !(value.trim().isEmpty())) {
            ++fieldsFoundCounter;
        }
        dateWhenChangeSubmitted += value;
    }
    public String dateWhenChangeSubmitted() {
        return dateWhenChangeSubmitted;
    }

    public void developer(String value) {
        if (developer.equals("") && !(value.trim().isEmpty())) {
            ++fieldsFoundCounter;
        }
        developer += value;
    }
    public String developer() {
        return developer;
    }

    public void ignoreDeveloper(boolean value) {
        if (ignoreDeveloperUpdatedCounter == 0) {
        	++ignoreDeveloperUpdatedCounter;
            ++fieldsFoundCounter;
        }
        ignoreDeveloper = value;
    }
    public boolean ignoreDeveloper() {
        return ignoreDeveloper;
    }


    public boolean hasAllFields() {
        return (fieldsFoundCounter == TOTAL_FIELDS);
    }
    public boolean isComplete() {
    	if (hasEmptyBody()) {
    		return false;
    	}
        return (hasAllFields() && body.hasMandatoryFields());
    }
    public boolean hasEmptyBody() {
        return ((body == null) || (body.isEmpty()));
    }
}
