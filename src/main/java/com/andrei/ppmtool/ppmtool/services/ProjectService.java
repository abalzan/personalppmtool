package com.andrei.ppmtool.ppmtool.services;

import com.andrei.ppmtool.ppmtool.Repositories.ProjectRepository;
import com.andrei.ppmtool.ppmtool.exceptions.ProjectIdException;
import com.andrei.ppmtool.ppmtool.model.Project;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

@Log4j2
@Service
public class ProjectService {

    private final ProjectRepository projectRepository;

    public ProjectService(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    public Project saveOrUpdate(Project project) {
        try {
            project.setProjectIdentifier(project.getProjectIdentifier().toUpperCase());
            return projectRepository.save(project);
        } catch (Exception e) {
            log.error(e);
            throw new ProjectIdException("Project Id: " + project.getProjectIdentifier().toUpperCase() + " already exists");
        }

    }
}
