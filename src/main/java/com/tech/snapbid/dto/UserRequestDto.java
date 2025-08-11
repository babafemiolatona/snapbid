package com.tech.snapbid.dto;


import com.tech.snapbid.models.Role;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import lombok.Data;

@Data
public class UserRequestDto {


    private String username;

    @Email(message = "Email should be valid.")
    private String email;

    @NotNull(message = "Role is required and must be either SELLER or BIDDER.")
    private Role role; // Only SELLER and BIDDER allowed for registration

    private String password;

}
