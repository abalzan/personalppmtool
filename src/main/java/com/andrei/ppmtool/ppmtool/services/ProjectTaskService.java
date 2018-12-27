package com.andrei.ppmtool.ppmtool.services;

import com.andrei.ppmtool.ppmtool.Repositories.BacklogRepository;
import com.andrei.ppmtool.ppmtool.Repositories.ProjectTaskRepository;
import com.andrei.ppmtool.ppmtool.exceptions.ProjectNotFoundException;
import com.andrei.ppmtool.ppmtool.model.Backlog;
import com.andrei.ppmtool.ppmtool.model.ProjectTask;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ProjectTaskService {

    private final BacklogRepository backlogRepository;
    private final ProjectTaskRepository projectTaskRepository;
    private final ProjectService projectService;

    public ProjectTaskService(BacklogRepository backlogRepository, ProjectTaskRepository projectTaskRepository, ProjectService projectService) {
        this.backlogRepository = backlogRepository;
        this.projectTaskRepository = projectTaskRepository;
        this.projectService = projectService;
    }

    public ProjectTask addProjectTask(String projectIdentifier, ProjectTask projectTask, String projectLeader) {

        Backlog backlog = projectService.findUserProjectIdentifier(projectIdentifier, projectLeader).getBacklog();

        projectTask.setBacklog(backlog);

        int backLogSequence = backlog.getPTSequence();
        backLogSequence++;
        backlog.setPTSequence(backLogSequence);


        projectTask.setProjectSequence(projectIdentifier + "-" + backLogSequence);
        projectTask.setProjectIdentifier(projectIdentifier);

        if (projectTask.getPriority() == 0) {
            projectTask.setPriority(3);
        }

        if (projectTask.getStatus() == null || projectTask.getStatus().isEmpty()) {
            projectTask.setStatus("TO_DO");
        }

        return projectTaskRepository.save(projectTask);
    }

    public Iterable<ProjectTask> findBackLogById(String backlogId, String projectLeader) {

        projectService.findUserProjectIdentifier(backlogId, projectLeader);

        return projectTaskRepository.findByProjectIdentifierOrderByPriority(backlogId);
    }

    public ProjectTask findProjectTaskByProjectSequence(String backlogId, String projectTaskId, String projectLeader) {

        projectService.findUserProjectIdentifier(backlogId, projectLeader);

        ProjectTask projectTask = projectTaskRepository.findByProjectSequence(projectTaskId)
                .orElseThrow(() -> new ProjectNotFoundException("Project Task " + projectTaskId + " not found!"));

        if (!projectTask.getProjectIdentifier().equals(backlogId)) {
            throw new ProjectNotFoundException("Project Task " + projectTaskId + " does not exist in project!");
        }

        return projectTask;
    }


    public ProjectTask updateByProjectSequence(ProjectTask updatedProjectTask, String backlogId, String projectTaskId, String projectLeader) {

        findProjectTaskByProjectSequence(backlogId, projectTaskId, projectLeader);

        return projectTaskRepository.save(updatedProjectTask);
    }

    public void deleteProjectTaskByProjectSequence(String backlogId, String projectTaskId, String projectLeader) {
        ProjectTask projectTaskByProjectSequence = findProjectTaskByProjectSequence(backlogId, projectTaskId, projectLeader);

        projectTaskRepository.delete(projectTaskByProjectSequence);

    }
}
