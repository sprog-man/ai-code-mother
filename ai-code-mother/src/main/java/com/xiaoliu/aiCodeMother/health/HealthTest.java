package com.xiaoliu.aiCodeMother.health;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/health")
public class HealthTest {

    @RequestMapping("/test")
    public String test() {
        return "success";
    }
}
