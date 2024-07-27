package grwm.develop.qna.question;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuestionRepository extends JpaRepository<Question, Long> {
    List<Question> findAllByCategory(String category);
    List<Question> searchQuestionsByContent(String keyword);
    List<Question> searchQuestionsByTitle(String keyword);
}
