package com.example.blog;

import com.example.blog.dto.admin.AdminPostCreateRequest;
import com.example.blog.dto.PostQuery;
import com.example.blog.service.AdminPostService;
import com.example.blog.service.PostService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class PostFlowSmokeTest {

    @Autowired
    private AdminPostService adminPostService;

    @Autowired
    private PostService postService;

    @Test
    void draftPublishVisible() {
        AdminPostCreateRequest req = new AdminPostCreateRequest();
        req.setTitle("Hello");
        req.setSummary("Summary");
        req.setContent("# md");
        req.setCategoryId(null);
        req.setTagIds(Collections.emptyList());

        Long id = adminPostService.create(req);
        assertNotNull(id);

        // not visible when draft
        PostQuery q = new PostQuery();
        q.setPageNum(1L);
        q.setPageSize(10L);
        assertTrue(postService.listPublished(q).getList().stream().noneMatch(p -> p.getId().equals(id)));

        adminPostService.publish(id);
        assertTrue(postService.listPublished(q).getList().stream().anyMatch(p -> p.getId().equals(id)));

        assertEquals(id, postService.getPublishedDetail(id).getId());
    }
}
