class P4Body {
    private static final int MANDATORY_FIELDS = 6;
    private static final int TOTAL_FIELDS = 10;
    // mandatory
    private String taskId = "";
    private String reasonForChange = "";
    private String implementation = "";
    private String tfc = "";
    private boolean dbChange = false;
    private int dbChangeUpdatedCounter = 0;
    private boolean notifyChoice = false;
    private String notifyTeam = "";
    private int notifyUpdatedCounter = 0;
    // optional
    private String review = "";
    private String author = "";
    private String observer = "";
    private String buildFixApproved = "";
    private boolean releaseNote = false;
    private int releaseNoteUpdatedCounter = 0;

    private int mandatoryFieldsFoundCounter = 0;
    private int optionalFieldsFoundCounter = 0;


    public void taskId(String value) {
        if (taskId.equals("") && !(value.trim().isEmpty())) {
            ++mandatoryFieldsFoundCounter;
        }
        taskId += value;
    }
    public String taskId() {
        return taskId;
    }

    public void reasonForChange(String value) {
        if (reasonForChange.equals("") && !(value.trim().isEmpty())) {
            ++mandatoryFieldsFoundCounter;
        }
        reasonForChange += value;
    }
    public String reasonForChange() {
        return reasonForChange;
    }

    public void implementation(String value) {
        if (implementation.equals("") && !(value.trim().isEmpty())) {
            ++mandatoryFieldsFoundCounter;
        }
        implementation += value;
    }
    public String implementation() {
        return implementation;
    }

    public void tfc(String value) {
        if (tfc.equals("") && !(value.trim().isEmpty())) {
            ++mandatoryFieldsFoundCounter;
        }
        tfc += value;
    }
    public String tfc() {
        return tfc;
    }

    public void dbChange(boolean value) {
        if (dbChangeUpdatedCounter == 0) {
            ++mandatoryFieldsFoundCounter;
        }
        dbChange = value;
        ++dbChangeUpdatedCounter;
    }
    public boolean dbChange() {
        return dbChange;
    }

    public void notifyChoice(boolean value) {
        if (notifyUpdatedCounter == 0) {
            ++mandatoryFieldsFoundCounter;
        }
        notifyChoice = value;
        ++notifyUpdatedCounter;
    }
    public boolean notifyChoice() {
        return notifyChoice;
    }

    public void notifyTeam(String value) {
        if (notifyUpdatedCounter == 0) {
            ++mandatoryFieldsFoundCounter;
        }
        notifyTeam = value;
        ++notifyUpdatedCounter;
    }
    public String notifyTeam() {
        return notifyTeam;
    }

    public void review(String value) {
        if (review.equals("") && !(value.trim().isEmpty())) {
            ++optionalFieldsFoundCounter;
        }
        review += value;
    }
    public String review() {
        return review;
    }

    public void author(String value) {
        if (author.equals("") && !(value.trim().isEmpty())) {
            ++optionalFieldsFoundCounter;
        }
        author += value;
    }
    public String author() {
        return author;
    }

    public void observer(String value) {
        if (observer.equals("") && !(value.trim().isEmpty())) {
            ++optionalFieldsFoundCounter;
        }
        observer += value;
    }
    public String observer() {
        return observer;
    }

    public void buildFixApproved(String value) {
        if (buildFixApproved.equals("") && !(value.trim().isEmpty())) {
            ++optionalFieldsFoundCounter;
        }
        buildFixApproved += value;
    }
    public String buildFixApproved() {
        return buildFixApproved;
    }

    public void releaseNote(boolean value) {
        if (releaseNoteUpdatedCounter == 0) {
            ++optionalFieldsFoundCounter;
        }
        releaseNote = value;
        ++releaseNoteUpdatedCounter;
    }
    public boolean releaseNote() {
        return releaseNote;
    }

    public boolean hasMandatoryFields() {
        return (mandatoryFieldsFoundCounter == MANDATORY_FIELDS);
    }
    public boolean isEmpty() {
        return (totalFound() == 0);
    }
    public int totalFound() {
        return (mandatoryFieldsFoundCounter + optionalFieldsFoundCounter);
    }
    public int mandatoryFound() {
        return (mandatoryFieldsFoundCounter);
    }
    public int percentageFound() {
        return ((totalFound()*100) / TOTAL_FIELDS);
    }
}
