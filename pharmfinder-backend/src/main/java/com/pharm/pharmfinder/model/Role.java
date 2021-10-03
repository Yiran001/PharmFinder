package com.pharm.pharmfinder.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;

public enum Role implements GrantedAuthority {
    USER,
    USER_ADMIN,
    MEDICINE_ADMIN;

    @Override
    public String getAuthority() {
        return name();
    }
}
