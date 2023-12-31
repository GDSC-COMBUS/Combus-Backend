package combus.backend.controller;

import combus.backend.domain.Driver;
import combus.backend.repository.DriverRepository;
import combus.backend.request.LoginRequest;
import combus.backend.service.DriverService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/drivers")
public class DriverController {

    @Autowired
    private final DriverService driverService;

    @Autowired
    private DriverRepository driverRepository;

    public DriverController(DriverService driverService) {
        this.driverService = driverService;
    }

    @PostMapping("/login")
    public String login(@RequestBody LoginRequest LoginRequest, BindingResult bindingResult,
                        HttpServletRequest httpServletRequest) {

        String loginId = LoginRequest.getLoginId();
        System.out.println("loginId: "+ loginId);

        Driver loginDriver = driverService.authenticateDriver(loginId);

        // 회원 번호가 틀린 경우
        if(loginDriver == null){
            bindingResult.reject("login failed", "로그인 실패! 회원 번호를 확인해주세요.");
        }
        if(bindingResult.hasErrors()) {
            return "redirect:/users/login";
        }


        // 로그인 성공 => 세션 생성
        // 세션을 생성하기 전에 기존의 세션 파기
        httpServletRequest.getSession().invalidate();
        HttpSession session = httpServletRequest.getSession(true);  // Session이 없으면 생성

        // 세션에 user의 기본키 Id를 넣어줌
        session.setAttribute("userId", loginDriver.getId());
        session.setMaxInactiveInterval(7200); // Session이 2시간동안 유지

        return "home";
    }


}
