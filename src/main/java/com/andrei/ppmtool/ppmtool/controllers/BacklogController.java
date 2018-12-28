package com.andrei.ppmtool.ppmtool.controllers;

import com.andrei.ppmtool.ppmtool.model.ProjectTask;
import com.andrei.ppmtool.ppmtool.services.ProjectTaskService;
import com.andrei.ppmtool.ppmtool.services.ValidationErrorService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;

@RestController
@RequestMapping("/api/backlog")
@CrossOrigin
public class BacklogController {

    private final ProjectTaskService projectTaskService;
    private final ValidationErrorService validationErrorService;

    public BacklogController(ProjectTaskService projectTaskService, ValidationErrorService validationErrorService) {
        this.projectTaskService = projectTaskService;
        this.validationErrorService = validationErrorService;
    }

    @PostMapping("/{backlogId}")
    public ResponseEntity<?> addProjectTaskBacklog(@Valid @RequestBody ProjectTask projectTask, BindingResult result,
                                                   @PathVariable String backlogId, Principal principal) {
        ResponseEntity<?> errorMap = validationErrorService.validationErrorService(result);

        if (errorMap != null) return errorMap;

        ProjectTask projectTask1 = projectTaskService.addProjectTask(backlogId, projectTask, principal.getName());

        return new ResponseEntity<>(projectTask1, HttpStatus.CREATED);
    }

    @GetMapping("/{backlogId}")
    public Iterable<ProjectTask> getProjectBacklog(@PathVariable String backlogId, Principal principal) {
        return projectTaskService.findBackLogById(backlogId, principal.getName());
    }

    @GetMapping("/{backlogId}/{projectTaskId}")
    public ResponseEntity<?> getProjectTask(@PathVariable String backlogId, @PathVariable String projectTaskId, Principal principal) {

        ProjectTask projectTask = projectTaskService.findProjectTaskByProjectSequence(backlogId, projectTaskId, principal.getName());

        return new ResponseEntity<>(projectTask, HttpStatus.OK);
    }

    @PatchMapping("/{backlogId}/{projectTaskId}")
    public ResponseEntity<?> updateProjectTask(@Valid @RequestBody ProjectTask updatedProjectTask, BindingResult result,
                                               @PathVariable String backlogId, @PathVariable String projectTaskId, Principal principal) {

        ResponseEntity<?> errorMap = validationErrorService.validationErrorService(result);

        if (errorMap != null) return errorMap;

        ProjectTask updatedTask = projectTaskService.updateByProjectSequence(updatedProjectTask, backlogId, projectTaskId, principal.getName());

        return new ResponseEntity<>(updatedTask, HttpStatus.OK);
    }

    @DeleteMapping("/{backlogId}/{projectTaskId}")
    public ResponseEntity<?> deleteProject(@PathVariable String backlogId, @PathVariable String projectTaskId, Principal principal) {

        projectTaskService.deleteProjectTaskByProjectSequence(backlogId, projectTaskId, principal.getName());

        return new ResponseEntity<>("Project task " + projectTaskId + " was delete successfully", HttpStatus.OK);
    }
}
