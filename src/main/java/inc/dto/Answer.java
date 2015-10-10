package inc.dto;

public class Answer {
    private String host;
    private String answer;

    public Answer(String host, String answer) {
        this.host = host;
        this.answer = answer;
    }

    @Override
    public String toString() {
        return "Answer{" +
                "host='" + host + '\'' +
                ", answer='" + answer + '\'' +
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
}
