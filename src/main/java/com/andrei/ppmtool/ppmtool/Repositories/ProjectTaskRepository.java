package com.andrei.ppmtool.ppmtool.Repositories;

import com.andrei.ppmtool.ppmtool.model.ProjectTask;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProjectTaskRepository extends JpaRepository<ProjectTask, Long> {

    List<ProjectTask> findByProjectIdentifierOrderByPriority(String backlogId);

    Optional<ProjectTask> findByProjectSequence(String projectSequence);

}
