package com.libreria.model.keys;

import java.io.Serializable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UserKey implements Serializable {
    private Long id;
    private String email;

    public UserKey(Long id, String email) {
        this.id = id;
        this.email = email;
    }

    
}