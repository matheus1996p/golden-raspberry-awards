package com.outsera.goldenraspberry.repository;

import com.outsera.goldenraspberry.domain.Studio;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StudioRepository extends JpaRepository<Studio, Long> {

    Optional<Studio> findByName(String name);
}
