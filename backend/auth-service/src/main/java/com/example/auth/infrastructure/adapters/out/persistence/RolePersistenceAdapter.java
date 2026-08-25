package com.example.auth.infrastructure.adapters.out.persistence;

import com.example.auth.domain.model.Role;
import com.example.auth.domain.ports.out.RoleRepositoryPort;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class RolePersistenceAdapter implements RoleRepositoryPort {

    private final RoleJpaRepository roleJpaRepository;
    private final ModelMapper modelMapper;

    public RolePersistenceAdapter(RoleJpaRepository roleJpaRepository, ModelMapper modelMapper) {
        this.roleJpaRepository = roleJpaRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public Optional<Role> buscarPorNombre(String nombre) {
        return roleJpaRepository.findByNombre(nombre)
                .map(entity -> modelMapper.map(entity, Role.class));
    }

    @Override
    public Role guardar(Role rol) {
        RoleEntity entity = modelMapper.map(rol, RoleEntity.class);
        RoleEntity savedEntity = roleJpaRepository.save(entity);
        return modelMapper.map(savedEntity, Role.class);
    }
}
