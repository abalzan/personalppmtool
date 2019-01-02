package com.andrei.ppmtool.ppmtool.services;

import com.andrei.ppmtool.ppmtool.Repositories.BacklogRepository;
import com.andrei.ppmtool.ppmtool.Repositories.ProjectRepository;
import com.andrei.ppmtool.ppmtool.Repositories.UserRepository;
import com.andrei.ppmtool.ppmtool.exceptions.ProjectIdException;
import com.andrei.ppmtool.ppmtool.model.Project;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class ProjectServiceTest {

    private ProjectService service;

    @Mock
    private UserRepository userRepository;
    @Mock
    private ProjectRepository projectRepository;
    @Mock
    private BacklogRepository backlogRepository;

    private List<Project> projectList;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        service = new ProjectService(userRepository, projectRepository, backlogRepository);
        projectList = createProjects();
    }

    @Test
    public void saveOrUpdate() {

    }

    @Test
    public void findUserProjectIdentifier() {
        Mockito.when(projectRepository.findByProjectIdentifierAndProjectLeader(Mockito.anyString(), Mockito.anyString())).thenReturn(projectList.get(0));

        Project project = service.findUserProjectIdentifier(Mockito.anyString(), Mockito.anyString());

        assertEquals("NAME", project.getProjectName());
        assertEquals("FKID1", project.getProjectIdentifier());
        assertEquals("Fake Description", project.getDescription());
        assertEquals("fake@email.com", project.getProjectLeader());
    }

    @Test(expected = ProjectIdException.class)
    public void findUserProjectIdentifierWithInvalidData() {
        Mockito.when(projectRepository.findByProjectIdentifierAndProjectLeader(Mockito.anyString(), Mockito.anyString())).thenReturn(null);
        service.findUserProjectIdentifier(Mockito.anyString(), Mockito.anyString());
    }

    @Test
    public void findAllUserProjects() {
        Mockito.when(projectRepository.findAllByProjectLeader(Mockito.anyString())).thenReturn(projectList);

        List<Project> allUserProjects = service.findAllUserProjects(Mockito.anyString());

        assertEquals(2, allUserProjects.size());
        assertEquals("FKID1", allUserProjects.get(0).getProjectIdentifier());
        assertEquals("PRID1", allUserProjects.get(1).getProjectIdentifier());

    }

    @Test
    public void deleteUserProject() {
    }


    private List<Project> createProjects() {
        Project project1 = Project.builder().projectName("NAME").projectIdentifier("FKID1").description("Fake Description").projectLeader("fake@email.com").build();
        Project project2 = Project.builder().projectName("Another").projectIdentifier("PRID1").description("Another Description").projectLeader("other@email.com").build();

        return Arrays.asList(project1, project2);
    }

}