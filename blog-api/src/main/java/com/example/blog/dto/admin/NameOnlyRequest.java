package com.example.blog.dto.admin;

import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public class NameOnlyRequest {

    @Schema(description = "Name", example = "Java")
    @NotBlank
    @Size(max = 50)
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
