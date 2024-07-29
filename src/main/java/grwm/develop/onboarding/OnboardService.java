package grwm.develop.onboarding;

import grwm.develop.Category;
import grwm.develop.member.Member;
import grwm.develop.onboarding.dto.LandingMemberResponse;
import grwm.develop.onboarding.dto.OnboardRequest;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OnboardService {

    public LandingMemberResponse landing(Member member) {
        return LandingMemberResponse.from(member);
    }

    @Transactional
    public void onboarding(OnboardRequest request, Member member) {
        List<Onboard> onboards = new ArrayList<>();
        addSkinOnboard(request, member, onboards);
        addHealthOnboard(request, member, onboards);
        addNutrientsOnboard(request, member, onboards);
    }

    private void addSkinOnboard(OnboardRequest request, Member member, List<Onboard> onboards) {
        if (request.getSkin() != null) {
            addOnboard(member, onboards, request.getSkin().getType(), Category.SKIN);
            request.getSkin().getConditions().forEach(
                    condition -> addOnboard(member, onboards, condition.getCondition(), Category.SKIN));
        }
    }

    private void addHealthOnboard(OnboardRequest request, Member member, List<Onboard> onboards) {
        if (request.getHealth() != null) {
            request.getHealth().getPurpose().forEach(
                    purpose -> addOnboard(member, onboards, purpose, Category.HEALTH)
            );
        }
    }

    private void addNutrientsOnboard(OnboardRequest request, Member member, List<Onboard> onboards) {
        if (request.getNutrients() != null) {
            request.getNutrients().getNutrient().forEach(
                    nutrient -> addOnboard(member, onboards, nutrient, Category.NUTRIENTS));
        }
    }

    private void addOnboard(Member member, List<Onboard> onboards, String content, Category category) {
        onboards.add(
                Onboard.builder()
                        .keyword(content)
                        .member(member)
                        .category(category)
                        .build());
    }
}
