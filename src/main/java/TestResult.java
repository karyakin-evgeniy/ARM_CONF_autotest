import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class TestResult {
    private boolean passTest;
    private int runsCount = 0;
    private String testName;
    private String resultString;
    private List<String> exceptions = new LinkedList<>();
    private List<String> comments = new LinkedList<>();
    private String commentInRun = "";

    public TestResult(String testName) {
        this.testName = testName;
    }

    public void pass(String resultString) {
        passTest = true;
        this.resultString = resultString;
        checkEmptyComment();
        comments.add(commentInRun);
        runsCount++;
    }

    public void failedTest(String resultString, String exception) {
        passTest = false;
        exceptions.add(exception);
        checkEmptyComment();
        comments.add(commentInRun);
        commentInRun = "";
        this.resultString = resultString;
        runsCount++;

    }

    public void addCommentInRun(String comment) {
        commentInRun += comment;
    }

    private void checkEmptyComment() {
        if (commentInRun.isEmpty()) {
            commentInRun = "-";
        }
    }

    public boolean isPassTest() {
        return passTest;
    }

    public void setPassTest(boolean passTest) {
        this.passTest = passTest;
    }

    public int getRunsCount() {
        return runsCount;
    }

    public void setRunsCount(int runsCount) {
        this.runsCount = runsCount;
    }

    public String getTestName() {
        return testName;
    }

    public void setTestName(String testName) {
        this.testName = testName;
    }

    public String getResultString() {
        return resultString;
    }

    public void setResultString(String resultString) {
        this.resultString = resultString;
    }

    public List<String> getExceptions() {
        return exceptions;
    }

    public void setExceptions(List<String> exceptions) {
        this.exceptions = exceptions;
    }

    public List<String> getComments() {
        return comments;
    }

    public void setComments(List<String> comments) {
        this.comments = comments;
    }
}
