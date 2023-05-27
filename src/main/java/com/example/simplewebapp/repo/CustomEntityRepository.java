package com.example.simplewebapp.repo;

import com.example.simplewebapp.models.CustomEntity;
import org.springframework.data.repository.CrudRepository;

public interface CustomEntityRepository extends CrudRepository<CustomEntity, Long> {
}
