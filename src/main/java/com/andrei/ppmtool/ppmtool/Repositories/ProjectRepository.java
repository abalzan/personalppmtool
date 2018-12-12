package com.andrei.ppmtool.ppmtool.Repositories;

import com.andrei.ppmtool.ppmtool.model.Project;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectRepository extends JpaRepository<Project, Long> {

    Project findByProjectIdentifier(String projectIdentifier);

}
