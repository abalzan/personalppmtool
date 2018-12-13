package com.andrei.ppmtool.ppmtool.services;

import com.andrei.ppmtool.ppmtool.Repositories.BacklogRepository;
import com.andrei.ppmtool.ppmtool.Repositories.ProjectRepository;
import com.andrei.ppmtool.ppmtool.exceptions.ProjectIdException;
import com.andrei.ppmtool.ppmtool.model.Backlog;
import com.andrei.ppmtool.ppmtool.model.Project;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Log4j2
@Service
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final BacklogRepository backlogRepository;

    public ProjectService(ProjectRepository projectRepository, BacklogRepository backlogRepository) {
        this.projectRepository = projectRepository;
        this.backlogRepository = backlogRepository;
    }

    public Project saveOrUpdate(Project project) {
        try {
            String projectIdentifier = project.getProjectIdentifier().toUpperCase();
            project.setProjectIdentifier(projectIdentifier);

            if (project.getId() == 0) {
                Backlog backlog = new Backlog();
                project.setBacklog(backlog);
                backlog.setProject(project);
                backlog.setProjectIdentifier(projectIdentifier);
            }
            if (project.getId() > 0) {
                project.setBacklog(backlogRepository.findByProjectIdentifier(projectIdentifier));
            }

            return projectRepository.save(project);
        } catch (Exception e) {
            log.error(e);
            throw new ProjectIdException("Project Id: " + project.getProjectIdentifier().toUpperCase() + " already exists");
        }

    }

    public Project findProjectByIdentifier(String projectIdentifier) {

        return Optional
                .ofNullable(projectRepository.findByProjectIdentifier(projectIdentifier.toUpperCase()))
                .orElseThrow(() -> new ProjectIdException("Project Id " + projectIdentifier.toUpperCase() + " not found!"));

    }

    public Iterable<Project> findAllProjects() {
        return projectRepository.findAll();
    }

    public void deleteByProjectIdentier(String projectIdentifier) {

        projectRepository.delete(this.findProjectByIdentifier(projectIdentifier));

    }
}
