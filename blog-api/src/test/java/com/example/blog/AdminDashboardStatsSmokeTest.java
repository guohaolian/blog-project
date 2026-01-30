package com.example.blog;

import com.example.blog.service.AdminDashboardService;
import com.example.blog.vo.admin.AdminDashboardStatsVO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class AdminDashboardStatsSmokeTest {

    @Autowired
    private AdminDashboardService adminDashboardService;

    @Test
    void statsReturnsNonNegativeCounts() {
        AdminDashboardStatsVO vo = adminDashboardService.stats();
        assertNotNull(vo);
        assertTrue(vo.getTotal() >= 0);
        assertTrue(vo.getDraft() >= 0);
        assertTrue(vo.getPublished() >= 0);
        assertTrue(vo.getTotal() >= vo.getDraft());
        assertTrue(vo.getTotal() >= vo.getPublished());
    }
}
