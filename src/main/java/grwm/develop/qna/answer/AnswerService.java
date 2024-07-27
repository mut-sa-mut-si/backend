package grwm.develop.qna.answer;

import grwm.develop.member.Member;
import grwm.develop.qna.answer.dto.WriteAnswerRequest;
import grwm.develop.qna.question.Question;
import grwm.develop.qna.question.QuestionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AnswerService {

    private final AnswerRepository answerRepository;
    private final QuestionRepository questionRepository;

    public void writeAnswer(Long questionId, Member member, WriteAnswerRequest request) {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new IllegalArgumentException("질문을 찾을 수 없습니다."));
        Answer answer = buildAnswer(member, request, question);
        answerRepository.save(answer);
    }

    private Answer buildAnswer(Member member, WriteAnswerRequest request, Question question) {
        return Answer.builder()
                .content(request.content())
                .member(member)
                .question(question)
                .build();
    }
}
