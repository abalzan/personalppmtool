package com.andrei.ppmtool.ppmtool.Repositories;

import com.andrei.ppmtool.ppmtool.model.Backlog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BacklogRepository extends JpaRepository<Backlog, Long> {

    Optional<Backlog> findByProjectIdentifier(String identifier);

}
