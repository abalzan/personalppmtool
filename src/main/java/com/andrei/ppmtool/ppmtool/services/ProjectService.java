package com.andrei.ppmtool.ppmtool.services;

import com.andrei.ppmtool.ppmtool.Repositories.BacklogRepository;
import com.andrei.ppmtool.ppmtool.Repositories.ProjectRepository;
import com.andrei.ppmtool.ppmtool.Repositories.UserRepository;
import com.andrei.ppmtool.ppmtool.exceptions.ProjectIdException;
import com.andrei.ppmtool.ppmtool.exceptions.ProjectNotFoundException;
import com.andrei.ppmtool.ppmtool.model.Backlog;
import com.andrei.ppmtool.ppmtool.model.Project;
import com.andrei.ppmtool.ppmtool.model.User;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Log4j2
@Service
public class ProjectService {

    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;
    private final BacklogRepository backlogRepository;

    public ProjectService(UserRepository userRepository, ProjectRepository projectRepository, BacklogRepository backlogRepository) {
        this.userRepository = userRepository;
        this.projectRepository = projectRepository;
        this.backlogRepository = backlogRepository;
    }

    public Project saveOrUpdate(Project project, String projectLeader) {

        if (project.getId() > 0) {
            findUserProjectIdentifier(project.getProjectIdentifier(), projectLeader);
        }

        try {
            String projectIdentifier = project.getProjectIdentifier().toUpperCase();
            User user = userRepository.findByUsername(projectLeader).orElse(null);

            project.setUser(user);
            project.setProjectLeader(user.getUsername());
            project.setProjectIdentifier(projectIdentifier);

            if (project.getId() == 0) {
                Backlog backlog = new Backlog();
                project.setBacklog(backlog);
                backlog.setProject(project);
                backlog.setProjectIdentifier(projectIdentifier);
            }
            if (project.getId() > 0) {
                project.setBacklog(backlogRepository.findByProjectIdentifier(projectIdentifier)
                        .orElseThrow(() -> new ProjectNotFoundException("Project with Id " + projectIdentifier + " not found!")));
            }

            return projectRepository.save(project);
        } catch (Exception e) {
            log.error(e);
            throw new ProjectIdException("Project Id: " + project.getProjectIdentifier().toUpperCase() + " already exists");
        }

    }

    public Project findUserProjectIdentifier(String projectIdentifier, String projectLeader) {

        return Optional
                .ofNullable(
                        projectRepository.findByProjectIdentifierAndProjectLeader(projectIdentifier.toUpperCase(), projectLeader))
                .orElseThrow(
                        () -> new ProjectIdException("Project Id " + projectIdentifier.toUpperCase() + " not found for your user!"));
    }

    public List<Project> findAllUserProjects(String projectLeader) {
        return projectRepository.findAllByProjectLeader(projectLeader);
    }

    public void deleteUserProject(String projectIdentifier, String projectLeader) {

        projectRepository.delete(this.findUserProjectIdentifier(projectIdentifier, projectLeader));

    }
}
