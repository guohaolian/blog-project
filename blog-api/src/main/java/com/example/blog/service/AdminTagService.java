package com.example.blog.service;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.example.blog.common.BizException;
import com.example.blog.common.ErrorCodes;
import com.example.blog.entity.Tag;
import com.example.blog.mapper.TagMapper;
import com.example.blog.vo.TagVO;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdminTagService {

    private final TagMapper tagMapper;

    public AdminTagService(TagMapper tagMapper) {
        this.tagMapper = tagMapper;
    }

    public List<TagVO> list() {
        List<Tag> list = tagMapper.selectList(Wrappers.<Tag>lambdaQuery().orderByAsc(Tag::getId));
        return list.stream().map(t -> new TagVO(t.getId(), t.getName())).collect(Collectors.toList());
    }

    public Long create(String name) {
        Tag t = new Tag();
        t.setName(name.trim());
        t.setCreatedAt(LocalDateTime.now());
        t.setUpdatedAt(LocalDateTime.now());
        tagMapper.insert(t);
        return t.getId();
    }

    public void update(Long id, String name) {
        Tag t = tagMapper.selectById(id);
        if (t == null) {
            throw new BizException(ErrorCodes.NOT_FOUND, "tag not found");
        }
        t.setName(name.trim());
        t.setUpdatedAt(LocalDateTime.now());
        tagMapper.updateById(t);
    }

    public void delete(Long id) {
        // v1: hard delete for simplicity
        tagMapper.deleteById(id);
    }
}
