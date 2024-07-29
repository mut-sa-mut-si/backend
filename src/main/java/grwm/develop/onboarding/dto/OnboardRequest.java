package grwm.develop.onboarding.dto;

import jakarta.annotation.Nullable;
import java.util.List;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class OnboardRequest {

    private final List<CategoryDTO> categories;

    @Nullable
    private SkinDTO skin;

    @Nullable
    private HealthDTO health;

    @Nullable
    private NutrientsDTO nutrients;

    public record CategoryDTO(String category) {
    }

    @Getter
    public static class SkinDTO {

        private String type;
        private List<ConditionDTO> conditions;

        @Getter
        public static class ConditionDTO {

            private String condition;
        }
    }

    @Getter
    public static class HealthDTO {

        private List<String> purpose;
    }

    @Getter
    public static class NutrientsDTO {

        private List<String> nutrient;
    }
}
