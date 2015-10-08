package inc.dto;

public class CrackResult {
    private String resultCode;
    private String resultstring;

    public CrackResult(String resultCode, String resultstring) {
        this.resultCode = resultCode;
        this.resultstring = resultstring;
    }

    public CrackResult() {
    }

    public String getResultCode() {
        return resultCode;
    }

    public void setResultCode(String resultCode) {
        this.resultCode = resultCode;
    }

    public String getResultstring() {
        return resultstring;
    }

    public void setResultstring(String resultstring) {
        this.resultstring = resultstring;
    }
}
