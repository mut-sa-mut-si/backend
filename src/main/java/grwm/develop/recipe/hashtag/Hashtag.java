package grwm.develop.recipe.hashtag;

import grwm.develop.BaseEntity;
import grwm.develop.recipe.Recipe;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.*;

@Entity
@Builder
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class Hashtag extends BaseEntity {

    @Column(length = 128, nullable = false)
    private String content;

    @JoinColumn(name = "recipe_id")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Recipe recipe;
}
