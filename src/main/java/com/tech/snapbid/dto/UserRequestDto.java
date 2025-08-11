package com.tech.snapbid.dto;

import com.tech.snapbid.model.Role;

import lombok.Data;

@Data
public class UserRequestDto {

    private String username;
    private String email;
    private Role role;
    private String password;

}
