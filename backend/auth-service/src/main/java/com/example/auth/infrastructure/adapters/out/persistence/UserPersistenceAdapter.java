package com.example.auth.infrastructure.adapters.out.persistence;

import com.example.auth.domain.model.User;
import com.example.auth.domain.ports.out.UserRepositoryPort;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class UserPersistenceAdapter implements UserRepositoryPort {

    private final UserJpaRepository userJpaRepository;
    private final ModelMapper modelMapper;

    public UserPersistenceAdapter(UserJpaRepository userJpaRepository, ModelMapper modelMapper) {
        this.userJpaRepository = userJpaRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public User guardar(User usuario) {
        UserEntity entity = modelMapper.map(usuario, UserEntity.class);
        UserEntity savedEntity = userJpaRepository.save(entity);
        return modelMapper.map(savedEntity, User.class);
    }

    @Override
    public Optional<User> buscarPorCorreo(String correo) {
        return userJpaRepository.findByCorreo(correo)
                .map(entity -> modelMapper.map(entity, User.class));
    }

    @Override
    public Optional<User> buscarPorId(Long id) {
        return userJpaRepository.findById(id)
                .map(entity -> modelMapper.map(entity, User.class));
    }
}
