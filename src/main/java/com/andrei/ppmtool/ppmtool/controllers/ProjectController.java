package com.andrei.ppmtool.ppmtool.controllers;

import com.andrei.ppmtool.ppmtool.model.Project;
import com.andrei.ppmtool.ppmtool.services.ProjectService;
import com.andrei.ppmtool.ppmtool.services.ValidationErrorService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;

@CrossOrigin
@RestController
@RequestMapping("/api/project")
public class ProjectController {

    private final ProjectService projectService;
    private final ValidationErrorService validationErrorService;

    public ProjectController(ProjectService projectService, ValidationErrorService validationErrorService) {
        this.projectService = projectService;
        this.validationErrorService = validationErrorService;
    }

    @PostMapping
    public ResponseEntity<?> createNewProject(@Valid @RequestBody Project project, BindingResult result, Principal principal) {

        ResponseEntity<?> responseEntity = validationErrorService.validationErrorService(result);

        if (responseEntity != null) return responseEntity;

        projectService.saveOrUpdate(project, principal.getName());
        return new ResponseEntity<>(project, HttpStatus.CREATED);
    }

    @GetMapping("/{projectIdentifier}")
    public ResponseEntity<?> getProjectByIdentifier(@PathVariable String projectIdentifier, Principal principal) {

        Project project = projectService.findUserProjectIdentifier(projectIdentifier, principal.getName());
        return new ResponseEntity<>(project, HttpStatus.OK);
    }

    @GetMapping("/all")
    public Iterable<Project> getAllProjects(Principal principal) {
        return projectService.findAllUserProjects(principal.getName());
    }

    @DeleteMapping("/{projectIdentifier}")
    public ResponseEntity<?> deleteProjectByIdentifier(@PathVariable String projectIdentifier, Principal principal) {
        projectService.deleteUserProject(projectIdentifier, principal.getName());
        return new ResponseEntity<>("Project with id " + projectIdentifier + " was deleted!", HttpStatus.OK);
    }
}
