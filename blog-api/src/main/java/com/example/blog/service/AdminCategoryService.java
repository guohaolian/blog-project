package com.example.blog.service;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.example.blog.common.BizException;
import com.example.blog.common.ErrorCodes;
import com.example.blog.entity.Category;
import com.example.blog.mapper.CategoryMapper;
import com.example.blog.vo.CategoryVO;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdminCategoryService {

    private final CategoryMapper categoryMapper;

    public AdminCategoryService(CategoryMapper categoryMapper) {
        this.categoryMapper = categoryMapper;
    }

    public List<CategoryVO> list() {
        List<Category> list = categoryMapper.selectList(Wrappers.<Category>lambdaQuery().orderByAsc(Category::getId));
        return list.stream().map(c -> new CategoryVO(c.getId(), c.getName())).collect(Collectors.toList());
    }

    public Long create(String name) {
        Category c = new Category();
        c.setName(name.trim());
        c.setCreatedAt(LocalDateTime.now());
        c.setUpdatedAt(LocalDateTime.now());
        categoryMapper.insert(c);
        return c.getId();
    }

    public void update(Long id, String name) {
        Category c = categoryMapper.selectById(id);
        if (c == null) {
            throw new BizException(ErrorCodes.NOT_FOUND, "category not found");
        }
        c.setName(name.trim());
        c.setUpdatedAt(LocalDateTime.now());
        categoryMapper.updateById(c);
    }

    public void delete(Long id) {
        // v1: hard delete for simplicity
        categoryMapper.deleteById(id);
    }
}
