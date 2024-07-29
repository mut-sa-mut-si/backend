package grwm.develop.qna.question;

import grwm.develop.member.Member;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuestionRepository extends JpaRepository<Question, Long> {
    List<Question> findAllByCategory(String category);
    List<Question> findAllByMember(Member member);
    List<Question> searchQuestionsByContent(String keyword);
    List<Question> searchQuestionsByTitle(String keyword);
}
