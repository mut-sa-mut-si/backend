package grwm.develop.qna.answer;

import grwm.develop.member.Member;
import grwm.develop.member.MemberService;
import grwm.develop.notification.AnswerNotification;
import grwm.develop.notification.AnswerNotificationRepository;
import grwm.develop.notification.Type;
import grwm.develop.qna.answer.dto.WriteAnswerRequest;
import grwm.develop.qna.question.Question;
import grwm.develop.qna.question.QuestionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AnswerService {

    private final AnswerRepository answerRepository;
    private final QuestionRepository questionRepository;
    private final MemberService memberService;
    private final AnswerNotificationRepository answerNotificationRepository;

    @Transactional
    public void writeAnswer(Long questionId, Member member, WriteAnswerRequest request) {
        memberService.addPoints(member.getId(), 50);
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new IllegalArgumentException("질문을 찾을 수 없습니다."));
        Answer answer = buildAnswer(member, request, question);
        answerRepository.save(answer);

        AnswerNotification answerNotification = buildAnswerNotification(question, member);
        answerNotificationRepository.save(answerNotification);
    }

    private Answer buildAnswer(Member member, WriteAnswerRequest request, Question question) {
        return Answer.builder()
                .content(request.content())
                .member(member)
                .question(question)
                .build();
    }

    private AnswerNotification buildAnswerNotification(Question question, Member member) {
        return AnswerNotification.builder()
                .member(member)
                .question(question)
                .type(Type.ANSWER)
                .build();
    }
}
