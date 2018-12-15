package com.andrei.ppmtool.ppmtool.services;

import com.andrei.ppmtool.ppmtool.Repositories.BacklogRepository;
import com.andrei.ppmtool.ppmtool.Repositories.ProjectTaskRepository;
import com.andrei.ppmtool.ppmtool.exceptions.ProjectNotFoundException;
import com.andrei.ppmtool.ppmtool.model.Backlog;
import com.andrei.ppmtool.ppmtool.model.ProjectTask;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class ProjectTaskService {

    private final BacklogRepository backlogRepository;
    private final ProjectTaskRepository projectTaskRepository;

    public ProjectTaskService(BacklogRepository backlogRepository, ProjectTaskRepository projectTaskRepository) {
        this.backlogRepository = backlogRepository;
        this.projectTaskRepository = projectTaskRepository;
    }

    public ProjectTask addProjectTask(String projectIdentifier, ProjectTask projectTask) {

        try {
            Backlog backlog = backlogRepository.findByProjectIdentifier(projectIdentifier);
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
        } catch (Exception e) {
            log.error("Project not found: " + e);
            throw new ProjectNotFoundException("Project not found");
        }
    }

    public Iterable<ProjectTask> findBackLogById(String backlogId) {

        List<ProjectTask> findResult = projectTaskRepository.findByProjectIdentifierOrderByPriority(backlogId);

        if (findResult.isEmpty()) {
            throw new ProjectNotFoundException("Project Id " + backlogId + " not found!");
        }

        return findResult;
    }

    public ProjectTask findProjectTaskByProjectSequence(String backlogId, String projectTaskId) {

        Optional
                .ofNullable(backlogRepository.findByProjectIdentifier(backlogId))
                .orElseThrow(() -> new ProjectNotFoundException("Project with Id " + backlogId + " not found!"));

        ProjectTask projectTask = Optional
                .ofNullable(projectTaskRepository.findByProjectSequence(projectTaskId))
                .orElseThrow(() -> new ProjectNotFoundException("Project Task " + projectTaskId + " not found!"));

        if (!projectTask.getProjectIdentifier().equals(backlogId)) {
            throw new ProjectNotFoundException("Project Task " + projectTaskId + " does not exist in project!");
        }

        return projectTaskRepository.findByProjectSequence(projectTaskId);
    }


    public ProjectTask updateByProjectSequence(ProjectTask updatedProjectTask, String backlogId, String projectTaskId) {

        findProjectTaskByProjectSequence(backlogId, projectTaskId);

        return projectTaskRepository.save(updatedProjectTask);
    }

    public void deleteProjectTaskByProjectSequence(String backlogId, String projectTaskId) {
        ProjectTask projectTaskByProjectSequence = findProjectTaskByProjectSequence(backlogId, projectTaskId);

        projectTaskRepository.delete(projectTaskByProjectSequence);

    }
}
