package com.libreria.model.keys;

import java.io.Serializable;
import java.util.Base64;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserKey implements Serializable{
    private Long id;
    private String email;

    public UserKey(Long id, String email) {
        this.id = id;
        this.email = email;
    }

    
}