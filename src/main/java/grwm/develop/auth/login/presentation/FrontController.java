package grwm.develop.auth.login.presentation;

import grwm.develop.auth.jwt.JwtService;
import grwm.develop.auth.login.presentation.controllerservice.ControllerService;
import grwm.develop.member.Member;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/api/v1/login/{name}")
public class FrontController {

    private final JwtService jwtService;
    private final ControllerServiceCondition condition;

    @GetMapping
    public String redirect(@PathVariable("name") String name) {
        ControllerService controllerService = condition.getControllerService(name);
        return controllerService.getRedirectURL();
    }

    @ResponseBody
    @GetMapping("/redirect")
    public ResponseEntity<String> callback(@PathVariable("name") String name,
                                           @RequestParam("code") String code) {

        log.info("code={}", code);
        ControllerService controllerService = condition.getControllerService(name);
        Member member = controllerService.authorize(code);
        String jwt = jwtService.create(member.getEmail());
        log.info("jwt={}", jwt);
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", jwt);
        return ResponseEntity.ok()
                .headers(headers)
                .body("Token: " + jwt);
    }
}
