package grwm.develop.qna.question;

import grwm.develop.qna.answer.Answer;
import grwm.develop.qna.answer.AnswerRepository;
import grwm.develop.qna.dto.QuestionMainResponse;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class QuestionService {

    private final QuestionRepository questionRepository;
    private final AnswerRepository answerRepository;

    public QuestionMainResponse getMainPage(String category) {
        List<Question> questions = questionRepository.findAllByCategory(category);
        List<QuestionMainResponse.Faq> faqs = getFaqs(questions);
        List<QuestionMainResponse.WaitingAnswerQuestion> waitingAnswerQuestions = getWaitingAnswerQuestions(questions);
        return new QuestionMainResponse(faqs, waitingAnswerQuestions);
    }

    private List<QuestionMainResponse.Faq> getFaqs(List<Question> questions) {
        List<Question> randomQuestions = questions.stream()
                .limit(5)
                .sorted(Comparator.comparingDouble(q -> Math.random()))
                .toList();

        return randomQuestions.stream()
                .map(question -> {
                    Optional<Answer> firstAnswer = answerRepository.findFirstByQuestion(question);

                    return new QuestionMainResponse.Faq(
                            question.getId(),
                            question.getTitle(),
                            question.getContent(),
                            new QuestionMainResponse.Writer(question.getMember().getId(), question.getMember().getName()),
                            firstAnswer.map(answer -> new QuestionMainResponse.Writer(answer.getMember().getId(), answer.getMember().getName())).orElse(null),
                            firstAnswer.map(Answer::getContent).orElse(null)
                    );
                })
                .toList();
    }

    private List<QuestionMainResponse.WaitingAnswerQuestion> getWaitingAnswerQuestions(List<Question> questions) {
        List<Question> waitingAnswerQuestions = questions.stream()
                .filter(question -> answerRepository.findFirstByQuestion(question).isEmpty())
                .sorted(Comparator.comparing(Question::getCreatedAt))
                .limit(5)
                .toList();

        return waitingAnswerQuestions.stream()
                .map(question -> new QuestionMainResponse.WaitingAnswerQuestion(
                        question.getId(),
                        question.getTitle(),
                        question.getContent(),
                        new QuestionMainResponse.Writer(question.getMember().getId(), question.getMember().getName())
                ))
                .toList();
    }
}
