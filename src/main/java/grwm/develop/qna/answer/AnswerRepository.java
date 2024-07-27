package grwm.develop.qna.answer;

import grwm.develop.qna.question.Question;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AnswerRepository extends JpaRepository<Answer, Long> {
    Optional<Answer> findFirstByQuestion(Question question);
    List<Answer> findAllByQuestion(Question question);
}
