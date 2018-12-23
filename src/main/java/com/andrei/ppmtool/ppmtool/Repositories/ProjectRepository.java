package com.andrei.ppmtool.ppmtool.Repositories;

import com.andrei.ppmtool.ppmtool.model.Project;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProjectRepository extends JpaRepository<Project, Long> {

    Project findByProjectIdentifier(String projectIdentifier);

    List<Project> findAllByProjectLeader(String projectLeader);

}
