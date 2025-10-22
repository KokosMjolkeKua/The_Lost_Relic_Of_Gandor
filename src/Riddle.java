public class Riddle {
    private final String question;
    private final String answer;

    public Riddle(String question, String answer) { this.question = question; this.answer = answer.toLowerCase(); }
    public String getQuestion() { return question; }
    public boolean solve(String attempt) { return attempt != null && attempt.toLowerCase().trim().equals(answer); }
}
