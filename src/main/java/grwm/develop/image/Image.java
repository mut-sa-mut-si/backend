package grwm.develop.image;

import grwm.develop.BaseEntity;
import grwm.develop.recipe.Recipe;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Entity
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class Image extends BaseEntity {

    @Column(length = 2048, nullable = false)
    private String url;

    @JoinColumn(name = "recipe_id")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Recipe recipe;
}
