package com.example.auth.domain.ports.out;

import com.example.auth.domain.model.User;

public interface TokenPort {
    String generarToken(User usuario);
}
