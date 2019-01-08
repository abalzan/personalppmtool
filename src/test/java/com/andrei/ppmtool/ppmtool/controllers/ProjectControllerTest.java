package com.andrei.ppmtool.ppmtool.controllers;

import com.andrei.ppmtool.ppmtool.BaseTestContext;
import com.andrei.ppmtool.ppmtool.model.Project;
import com.andrei.ppmtool.ppmtool.services.ProjectService;
import com.andrei.ppmtool.ppmtool.services.ValidationErrorService;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.security.Principal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ProjectControllerTest extends BaseTestContext {

    @MockBean
    private ProjectService projectService;

    @MockBean
    private ValidationErrorService validationErrorService;

    private ProjectController controller;

    private Principal mockPrincipal;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        controller = new ProjectController(projectService, validationErrorService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .build();

        mockPrincipal = Mockito.mock(Principal.class);
        Mockito.when(mockPrincipal.getName()).thenReturn("me");
    }

    @Test
    public void createNewProject() throws Exception {
        Mockito.when(validationErrorService.validationErrorService(Mockito.any())).thenReturn(null);

        Mockito.when(projectService.saveOrUpdate(Mockito.any(Project.class), Mockito.anyString())).thenReturn(createProjects().get(0));

        mockMvc.perform(post("/api/project")
                .principal(mockPrincipal)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"projectName\": \"new proje\", \"projectIdentifier\": \"PRJ12\", \"description\": \"desc\"}")
        ).andExpect(status().isCreated())
                .andExpect(jsonPath("$.projectName", Matchers.is("new proje")))
                .andExpect(jsonPath("$.projectIdentifier", Matchers.is("PRJ12")))
                .andExpect(jsonPath("$.description", Matchers.is("desc")));
    }


    @Test
    public void getProjectByIdentifier() throws Exception {
        Mockito.when(projectService.findUserProjectIdentifier(Mockito.anyString(), Mockito.anyString())).thenReturn(createProjects().get(0));

        MockHttpServletResponse response = mockMvc.perform(get("/api/project/1")
                .principal(mockPrincipal)
                .accept(MediaType.APPLICATION_JSON)).andReturn().getResponse();

        String expectedResult = "{\"id\":1,\"createAt\":1546300801000,\"updateAt\":1546300801000,\"projectName\":\"FAKE NAME\",\"projectIdentifier\":\"FAKE\",\"description\":\"FAKE description\",\"startDate\":\"31/12/2018\",\"endDate\":\"01/01/2019\",\"projectLeader\":\"LEADER\"}";

        assertEquals(response.getStatus(), 200);
        assertEquals(expectedResult, response.getContentAsString());
    }

    @Test
    public void getAllProjects() throws Exception {
        Mockito.when(projectService.findAllUserProjects(Mockito.anyString())).thenReturn(createProjects());

        MockHttpServletResponse response = mockMvc.perform(get("/api/project/all")
                .principal(mockPrincipal)
                .accept(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse();

        String expectedResult = "[" +
                "{\"id\":1,\"createAt\":1546300801000,\"updateAt\":1546300801000,\"projectName\":\"FAKE NAME\",\"projectIdentifier\":\"FAKE\",\"description\":\"FAKE description\",\"startDate\":\"31/12/2018\",\"endDate\":\"01/01/2019\",\"projectLeader\":\"LEADER\"}," +
                "{\"id\":2,\"createAt\":1546300801000,\"updateAt\":1546300801000,\"projectName\":\"TEST NAME\",\"projectIdentifier\":\"TEST\",\"description\":\"TEST description\",\"startDate\":\"21/10/2018\",\"endDate\":\"21/12/2018\",\"projectLeader\":\"Another leader\"}" +
                "]";

        assertEquals(response.getStatus(), 200);
        assertEquals(expectedResult, response.getContentAsString());
    }

    @Test
    public void deleteProjectByIdentifier() {
        Mockito.doNothing().when(projectService).deleteUserProject(Mockito.anyString(), Mockito.any());

        projectService.deleteUserProject(Mockito.anyString(), Mockito.anyString());

        Mockito.verify(projectService, Mockito.times(1)).deleteUserProject("", "");
    }


    private List<Project> createProjects() throws ParseException {

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

        Date NOW = sdf.parse("01/01/2019 00:00:01");

        Project p1 = Project.builder()
                .projectIdentifier("FAKE")
                .description("FAKE description")
                .projectName("FAKE NAME")
                .startDate(LocalDate.of(2018, 12, 31))
                .endDate(LocalDate.of(2019, 01, 01))
                .projectLeader("LEADER")
                .build();
        p1.setId(1L);
        p1.setCreateAt(NOW);
        p1.setUpdateAt(NOW);

        Project p2 = Project.builder()
                .projectIdentifier("TEST")
                .description("TEST description")
                .projectName("TEST NAME")
                .startDate(LocalDate.of(2018, 10, 21))
                .endDate(LocalDate.of(2018, 12, 21))
                .projectLeader("Another leader")
                .build();
        p2.setId(2L);
        p2.setCreateAt(NOW);
        p2.setUpdateAt(NOW);
        return Arrays.asList(p1, p2);
    }
}