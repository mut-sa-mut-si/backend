package grwm.develop.qna.question;

import grwm.develop.Category;
import grwm.develop.member.Member;
import grwm.develop.qna.answer.Answer;
import grwm.develop.qna.answer.AnswerRepository;
import grwm.develop.qna.dto.QuestionDetailResponse;
import grwm.develop.qna.dto.QuestionDetailResponse.AnswerDetail;
import grwm.develop.qna.dto.QuestionDetailResponse.QuestionDetail;
import grwm.develop.qna.dto.QuestionMainResponse;
import grwm.develop.qna.question.dto.SearchMyQuestionResponse;
import grwm.develop.qna.question.dto.SearchQuestionResponse;
import grwm.develop.qna.question.dto.SearchQuestionResponse.SearchQuestion;
import grwm.develop.qna.question.dto.WriteQuestionRequest;
import jakarta.transaction.Transactional;
import java.util.ArrayList;
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
        List<Question> questions = questionRepository.findAllByCategory(getCategory(category));
        List<QuestionMainResponse.WaitingAnswerQuestion> waitingAnswerQuestions = getWaitingAnswerQuestions(questions);
        List<QuestionMainResponse.CategoryQuestion> categoryQuestions = getCategoryQuestions(questions);
        return new QuestionMainResponse(waitingAnswerQuestions, categoryQuestions);
    }

    public QuestionMainResponse getMainPageLoggedIn(String category, Member member) {
        List<Question> questions = questionRepository.findAllByCategory(getCategory(category));
        List<QuestionMainResponse.WaitingAnswerQuestion> waitingAnswerQuestions =
                getWaitingAnswerQuestionsWithOutMine(questions, member);
        List<QuestionMainResponse.CategoryQuestion> categoryQuestions = getCategoryQuestions(questions);
        return new QuestionMainResponse(waitingAnswerQuestions, categoryQuestions);
    }

    private List<QuestionMainResponse.WaitingAnswerQuestion> getWaitingAnswerQuestionsWithOutMine(
            List<Question> questions,
            Member member
    ) {
        List<Question> waitingAnswerQuestions = questions.stream()
                .filter(question -> answerRepository.findFirstByQuestion(question).isEmpty())
                .filter(question -> !question.getMember().getId().equals(member.getId()))
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

    private List<QuestionMainResponse.CategoryQuestion> getCategoryQuestions(List<Question> questions) {
        return questions.stream()
                .sorted(Comparator.comparing(Question::getCreatedAt).reversed())
                .map(question -> new QuestionMainResponse.CategoryQuestion(
                        question.getId(),
                        question.getTitle(),
                        new QuestionMainResponse.Writer(question.getMember().getId(), question.getMember().getName())
                ))
                .toList();
    }

    @Transactional
    public void writeQuestion(Member member, WriteQuestionRequest request) {
        Question question = buildQuestion(member, request);
        questionRepository.save(question);
    }

    private Question buildQuestion(Member member, WriteQuestionRequest request) {
        return Question.builder()
                .title(request.title())
                .content(request.content())
                .member(member)
                .category(getCategory(request.category()))
                .build();
    }

    private Category getCategory(String category) {
        if (category.equals("SKIN")) {
            return Category.SKIN;
        }
        if (category.equals("HEALTH")) {
            return Category.HEALTH;
        }
        return Category.NUTRIENTS;
    }

    public QuestionDetailResponse readQuestionLoggedIn(Member member, Long questionId) {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new IllegalArgumentException("질문을 찾을 수 없습니다."));

        List<Answer> answers = answerRepository.findAllByQuestion(question);
        List<AnswerDetail> answerDetails = getAnswerDetails(answers);

        QuestionDetail questionDetail = getQuestionDetail(question);

        boolean isWritten = false;
        for (Answer answer : answers) {
            if (answer.getMember().getId().equals(member.getId())) {
                isWritten = true;
                break;
            } else if (question.getMember().getId().equals(member.getId())) {
                isWritten = true;
                break;
            }
        }

        return new QuestionDetailResponse(isWritten, questionDetail, answerDetails);
    }

    public QuestionDetailResponse readQuestion(Long questionId) {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new IllegalArgumentException("질문을 찾을 수 없습니다."));

        boolean isWritten = false;
        List<Answer> answers = answerRepository.findAllByQuestion(question);
        List<AnswerDetail> answerDetails = getAnswerDetails(answers);

        QuestionDetail questionDetail = getQuestionDetail(question);

        return new QuestionDetailResponse(isWritten, questionDetail, answerDetails);
    }

    private QuestionDetail getQuestionDetail(Question question) {
        return new QuestionDetail(
                question.getId(),
                question.getTitle(),
                question.getContent(),
                new QuestionDetailResponse.MemberDTO(question.getMember().getId(), question.getMember().getName())
        );
    }

    private List<AnswerDetail> getAnswerDetails(List<Answer> answers) {
        return answers.stream()
                .map(answer -> new AnswerDetail(
                        answer.getId(),
                        answer.getContent(),
                        new QuestionDetailResponse.MemberDTO(answer.getMember().getId(), answer.getMember().getName())))
                .toList();
    }

    public SearchQuestionResponse searchQuestions(String keyword) {
        List<Question> contentMatches = questionRepository.findByContentContaining(keyword);
        List<Question> titleMatches = questionRepository.findByTitleContaining(keyword);

        List<SearchQuestion> questions = getSearchQuestions(contentMatches, titleMatches);

        return new SearchQuestionResponse(keyword, questions);
    }

    private List<SearchQuestion> getSearchQuestions(List<Question> contentMatches, List<Question> titleMatches) {
        List<SearchQuestion> questions = new ArrayList<>();
        questions.addAll(contentMatches.stream()
                .map(q -> new SearchQuestion(q.getId(), q.getTitle(), q.getContent()))
                .toList());
        questions.addAll(titleMatches.stream()
                .filter(q -> !contentMatches.contains(q))
                .map(q -> new SearchQuestion(q.getId(), q.getTitle(), q.getContent()))
                .toList());
        return questions;
    }

    public SearchMyQuestionResponse searchMyQuestions(Member member) {
        List<Question> myQuestions = questionRepository.findAllByMember(member);
        List<SearchMyQuestionResponse.Question> questions = getQuestions(myQuestions);

        return new SearchMyQuestionResponse(questions);
    }

    private List<SearchMyQuestionResponse.Question> getQuestions(List<Question> myQuestions) {
        return myQuestions.stream()
                .sorted(Comparator.comparing(Question::getCreatedAt).reversed())
                .map(q -> {
                    Optional<Answer> firstAnswer = answerRepository.findFirstByQuestion(q);
                    String content = firstAnswer.map(Answer::getContent).orElse(null);
                    SearchMyQuestionResponse.Member answerMember = firstAnswer
                            .filter(answer -> answer.getMember() != null)
                            .map(answer -> new SearchMyQuestionResponse.Member(answer.getMember().getId(),
                                    answer.getMember().getName()))
                            .orElse(null);

                    return new SearchMyQuestionResponse.Question(
                            q.getId(),
                            q.getTitle(),
                            content,
                            answerMember
                    );
                })
                .toList();
    }
}
