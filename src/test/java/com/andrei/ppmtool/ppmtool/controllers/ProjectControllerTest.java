package com.andrei.ppmtool.ppmtool.controllers;

import com.andrei.ppmtool.ppmtool.model.Project;
import com.andrei.ppmtool.ppmtool.services.ProjectService;
import com.andrei.ppmtool.ppmtool.services.ValidationErrorService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
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

public class ProjectControllerTest {

    @Mock
    private ProjectService projectService;

    @Mock
    private ValidationErrorService validationErrorService;

    private ProjectController controller;

    private MockMvc mockMvc;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        controller = new ProjectController(projectService, validationErrorService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .build();
    }


    @Test
    public void getProjectByIdentifier() throws Exception {

        Principal mockPrincipal = Mockito.mock(Principal.class);
        Mockito.when(mockPrincipal.getName()).thenReturn("me");
        Mockito.when(projectService.findUserProjectIdentifier(Mockito.anyString(), Mockito.anyString())).thenReturn(createProjects().get(0));

        RequestBuilder requestBuilder = get("/api/project/1")
                .principal(mockPrincipal)
                .accept(MediaType.APPLICATION_JSON);

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        MockHttpServletResponse response = result.getResponse();

        String expectedResult = "{\"id\":1,\"createAt\":1546300801000,\"updateAt\":1546300801000,\"projectName\":\"FAKE NAME\",\"projectIdentifier\":\"FAKE\",\"description\":\"FAKE description\",\"startDate\":\"31/12/2018\",\"endDate\":\"01/01/2019\",\"projectLeader\":\"LEADER\"}";

        assertEquals(response.getStatus(), 200);
        assertEquals(expectedResult, response.getContentAsString());
    }

    @Test
    public void getAllProjects() throws Exception {
        Principal mockPrincipal = Mockito.mock(Principal.class);
        Mockito.when(mockPrincipal.getName()).thenReturn("me");
        Mockito.when(projectService.findAllUserProjects(Mockito.anyString())).thenReturn(createProjects());

        RequestBuilder requestBuilder = get("/api/project/all")
                .principal(mockPrincipal)
                .accept(MediaType.APPLICATION_JSON);

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        MockHttpServletResponse response = result.getResponse();

        String expectedResult = "[" +
                "{\"id\":1,\"createAt\":1546300801000,\"updateAt\":1546300801000,\"projectName\":\"FAKE NAME\",\"projectIdentifier\":\"FAKE\",\"description\":\"FAKE description\",\"startDate\":\"31/12/2018\",\"endDate\":\"01/01/2019\",\"projectLeader\":\"LEADER\"}," +
                "{\"id\":2,\"createAt\":1546300801000,\"updateAt\":1546300801000,\"projectName\":\"TEST NAME\",\"projectIdentifier\":\"TEST\",\"description\":\"TEST description\",\"startDate\":\"21/10/2018\",\"endDate\":\"21/12/2018\",\"projectLeader\":\"Another leader\"}" +
                "]";

        assertEquals(response.getStatus(), 200);
        assertEquals(expectedResult, response.getContentAsString());
    }

    @Test
    public void deleteProjectByIdentifier() throws Exception {
        Principal mockPrincipal = Mockito.mock(Principal.class);
        Mockito.when(mockPrincipal.getName()).thenReturn("me");

        Mockito.doNothing().when(projectService).deleteUserProject(Mockito.anyString(), Mockito.any());

        projectService.deleteUserProject(Mockito.anyString(), Mockito.anyString());

        Mockito.verify(projectService, Mockito.times(1)).deleteUserProject("", "");
    }


    private List<Project> createProjects() throws ParseException {

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

        Date NOW = sdf.parse("01/01/2019 00:00:01");

        Project p1 = new Project().builder()
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

        Project p2 = new Project().builder()
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