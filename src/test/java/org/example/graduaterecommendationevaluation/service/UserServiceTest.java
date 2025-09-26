package org.example.graduaterecommendationevaluation.service;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@SpringBootTest
class UserServiceTest {

    @Autowired
    private UserService userService;

    @Test
    void getCategoryIdByUserId() {
        List<Long> l = userService.getCategoryIdByUserId(Long.valueOf("1421046391072681984"));
        log.debug("{}",l.toString());
    }
}