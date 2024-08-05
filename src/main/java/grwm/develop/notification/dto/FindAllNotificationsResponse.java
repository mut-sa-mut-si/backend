package grwm.develop.notification.dto;


import grwm.develop.notification.AnswerNotification;
import grwm.develop.notification.RecipeNotification;
import grwm.develop.notification.Type;
import java.util.List;

public record FindAllNotificationsResponse(List<ReviewNotice> reviewNotices,
                                           List<RecipeNotice> recipeNotices,
                                           List<AnswerNotice> answerNotices) {

    public static FindAllNotificationsResponse from(List<RecipeNotification> recipeNotifications,
                                                    List<AnswerNotification> answerNotifications) {

        return new FindAllNotificationsResponse(
                recipeNotifications.stream()
                        .filter(recipeNotification -> recipeNotification.getType().equals(Type.REVIEW))
                        .map(recipeNotification -> new ReviewNotice(
                                recipeNotification.getId(),
                                new RecipeDTO(recipeNotification.getRecipe().getId(),
                                        recipeNotification.getRecipe().getTitle()),
                                new MemberDTO(recipeNotification.getMember().getId(),
                                        recipeNotification.getMember().getName())))
                        .toList(),
                recipeNotifications.stream()
                        .filter(recipeNotification -> recipeNotification.getType().equals(Type.RECIPE))
                        .map(recipeNotification -> new RecipeNotice(
                                recipeNotification.getId(),
                                new RecipeDTO(recipeNotification.getRecipe().getId(),
                                        recipeNotification.getRecipe().getTitle()),
                                new MemberDTO(recipeNotification.getMember().getId(),
                                        recipeNotification.getMember().getName())))
                        .toList(),
                answerNotifications.stream()
                        .map(answerNotification -> new AnswerNotice(
                                answerNotification.getId(),
                                new QuestionDTO(answerNotification.getQuestion().getId(),
                                        answerNotification.getQuestion().getTitle()),
                                new MemberDTO(answerNotification.getMember().getId(),
                                        answerNotification.getMember().getName())))
                        .toList()
        );
    }

    private record ReviewNotice(Long id, RecipeDTO recipe, MemberDTO member) {
    }

    private record RecipeNotice(Long id, RecipeDTO recipe, MemberDTO member) {
    }

    private record AnswerNotice(Long id, QuestionDTO question, MemberDTO member) {
    }

    private record RecipeDTO(Long id, String title) {
    }

    private record MemberDTO(Long id, String name) {
    }

    private record QuestionDTO(Long id, String title) {
    }
}
