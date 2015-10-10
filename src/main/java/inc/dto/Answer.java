package inc.dto;

public class Answer {
    private String host;
    private String answer;
    private String range;

    public Answer(String host, String answer, String range) {
        this.host = host;
        this.answer = answer;
        this.range = range;
    }

    @Override
    public String toString() {
        return "Answer{" +
                "host='" + host + '\'' +
                ", answer='" + answer + '\'' +
                ", range='" + range + '\'' +
                '}';
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getRange() {
        return range;
    }

    public void setRange(String range) {
        this.range = range;
    }
}
