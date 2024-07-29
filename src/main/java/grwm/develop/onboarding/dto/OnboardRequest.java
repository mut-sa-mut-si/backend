package grwm.develop.onboarding.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.annotation.Nullable;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL) // Null 값을 JSON에서 제외
public class OnboardRequest {

    @Nullable
    private List<CategoryDTO> categories;

    @Nullable
    private SkinDTO skin;

    @Nullable
    private List<HealthDTO> health;

    @Nullable
    private List<NutrientsDTO> nutrients;
    
    @JsonCreator
    public OnboardRequest(
            @JsonProperty("categories") @Nullable List<CategoryDTO> categories,
            @JsonProperty("skin") @Nullable SkinDTO skin,
            @JsonProperty("health") @Nullable List<HealthDTO> health,
            @JsonProperty("nutrients") @Nullable List<NutrientsDTO> nutrients
    ) {
        this.categories = categories;
        this.skin = skin;
        this.health = health;
        this.nutrients = nutrients;
    }

    @Getter
    @NoArgsConstructor
    public static class CategoryDTO {
        private String category;

        @JsonCreator
        public CategoryDTO(@JsonProperty("category") String category) {
            this.category = category;
        }
    }

    @Getter
    @NoArgsConstructor
    public static class SkinDTO {
        private String type;
        private List<ConditionDTO> conditions;

        @JsonCreator
        public SkinDTO(
                @JsonProperty("type") String type,
                @JsonProperty("conditions") @Nullable List<ConditionDTO> conditions
        ) {
            this.type = type;
            this.conditions = conditions;
        }

        @Getter
        @NoArgsConstructor
        public static class ConditionDTO {
            private String condition;

            @JsonCreator
            public ConditionDTO(@JsonProperty("condition") String condition) {
                this.condition = condition;
            }
        }
    }

    @Getter
    @NoArgsConstructor
    public static class HealthDTO {
        private String purpose;

        @JsonCreator
        public HealthDTO(@JsonProperty("purpose") String purpose) {
            this.purpose = purpose;
        }
    }

    @Getter
    @NoArgsConstructor
    public static class NutrientsDTO {
        private String nutrient;

        @JsonCreator
        public NutrientsDTO(@JsonProperty("nutrient") String nutrient) {
            this.nutrient = nutrient;
        }
    }
}
