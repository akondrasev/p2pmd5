package inc.dto;

public class CrackResult {
    private int resultCode;
    private String resultstring;

    public CrackResult(int resultCode, String resultstring) {
        this.resultCode = resultCode;
        this.resultstring = resultstring;
    }

    public CrackResult() {
    }

    public int getResultCode() {
        return resultCode;
    }

    public void setResultCode(int resultCode) {
        this.resultCode = resultCode;
    }

    public String getResultstring() {
        return resultstring;
    }

    public void setResultstring(String resultstring) {
        this.resultstring = resultstring;
    }
}
