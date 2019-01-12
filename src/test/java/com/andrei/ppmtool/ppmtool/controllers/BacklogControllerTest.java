package com.andrei.ppmtool.ppmtool.controllers;

import com.andrei.ppmtool.ppmtool.BaseTestContext;
import com.andrei.ppmtool.ppmtool.model.Backlog;
import com.andrei.ppmtool.ppmtool.model.ProjectTask;
import com.andrei.ppmtool.ppmtool.services.ProjectTaskService;
import com.andrei.ppmtool.ppmtool.services.ValidationErrorService;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.security.Principal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class BacklogControllerTest extends BaseTestContext {

    @Mock
    private ProjectTaskService projectTaskService;

    @Mock
    private ValidationErrorService validationErrorService;

    private BacklogController controller;

    private Principal mockPrincipal;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        controller = new BacklogController(projectTaskService, validationErrorService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .build();

        mockPrincipal = Mockito.mock(Principal.class);
        Mockito.when(mockPrincipal.getName()).thenReturn("me");
    }

    @Test
    public void addProjectTaskBacklog() throws Exception {
        Mockito.when(validationErrorService.validationErrorService(Mockito.any())).thenReturn(null);

        Mockito.when(projectTaskService.addProjectTask(Mockito.anyString(), Mockito.any(ProjectTask.class), Mockito.anyString())).thenReturn(createProjectTask().get(0));

        mockMvc.perform(post("/api/backlog/PRJID")
                .principal(mockPrincipal)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"summary\": \"summary\", \"acceptanceCriteria\": \"acceptance\"}")
        ).andExpect(status().isCreated())
                .andExpect(jsonPath("$.projectSequence", Matchers.is("Sequence-1")))
                .andExpect(jsonPath("$.summary", Matchers.is("summary")))
                .andExpect(jsonPath("$.acceptanceCriteria", Matchers.is("acceptance")))
                .andExpect(jsonPath("$.status", Matchers.is("TO_DO")))
                .andExpect(jsonPath("$.priority", Matchers.is(3)))
                .andExpect(jsonPath("$.projectIdentifier", Matchers.is("PRJID")));
    }

    @Test
    public void getProjectBacklog() throws Exception {
        Mockito.when(projectTaskService.findBackLogById(Mockito.anyString(), Mockito.anyString())).thenReturn(createProjectTask());
        MockHttpServletResponse response = mockMvc.perform(get("/api/backlog/PRJID")
                .principal(mockPrincipal)
                .accept(MediaType.APPLICATION_JSON)).andReturn().getResponse();

        String expectedResult = "[{\"id\":2,\"createAt\":1546300801000,\"updateAt\":1546300801000,\"projectSequence\":\"Sequence-1\",\"summary\":\"summary\",\"acceptanceCriteria\":\"acceptance\",\"status\":\"TO_DO\",\"priority\":3,\"dueDate\":\"01/01/2019\",\"projectIdentifier\":\"PRJID\"},{\"id\":0,\"createAt\":null,\"updateAt\":null,\"projectSequence\":\"Sequence-2\",\"summary\":\"summary\",\"acceptanceCriteria\":\"acceptance\",\"status\":\"IN_PROGRESS\",\"priority\":3,\"dueDate\":\"01/01/2019\",\"projectIdentifier\":\"PRJ2\"}]";

        assertEquals(response.getStatus(), 200);
        assertEquals(expectedResult, response.getContentAsString());
    }

    @Test
    public void getProjectTask() throws Exception {
        Mockito.when(projectTaskService.findProjectTaskByProjectSequence(Mockito.anyString(), Mockito.anyString(), Mockito.anyString())).thenReturn(createProjectTask().get(0));
        MockHttpServletResponse response = mockMvc.perform(get("/api/backlog/PRJID/TASK")
                .principal(mockPrincipal)
                .accept(MediaType.APPLICATION_JSON)).andReturn().getResponse();

        String expectedResult = "{\"id\":2,\"createAt\":1546300801000,\"updateAt\":1546300801000,\"projectSequence\":\"Sequence-1\",\"summary\":\"summary\",\"acceptanceCriteria\":\"acceptance\",\"status\":\"TO_DO\",\"priority\":3,\"dueDate\":\"01/01/2019\",\"projectIdentifier\":\"PRJID\"}";

        assertEquals(response.getStatus(), 200);
        assertEquals(expectedResult, response.getContentAsString());
    }

    @Test
    public void updateProjectTask() throws Exception {
        Mockito.when(validationErrorService.validationErrorService(Mockito.any())).thenReturn(null);

        Mockito.when(projectTaskService.updateByProjectSequence(Mockito.any(ProjectTask.class), Mockito.anyString(), Mockito.anyString(), Mockito.anyString())).thenReturn(createProjectTask().get(0));

        mockMvc.perform(patch("/api/backlog/PRJID/TASK")
                .principal(mockPrincipal)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"summary\": \"summary\", \"acceptanceCriteria\": \"acceptance\"}")
        ).andExpect(status().isOk())
                .andExpect(jsonPath("$.projectSequence", Matchers.is("Sequence-1")))
                .andExpect(jsonPath("$.summary", Matchers.is("summary")))
                .andExpect(jsonPath("$.acceptanceCriteria", Matchers.is("acceptance")))
                .andExpect(jsonPath("$.status", Matchers.is("TO_DO")))
                .andExpect(jsonPath("$.priority", Matchers.is(3)))
                .andExpect(jsonPath("$.projectIdentifier", Matchers.is("PRJID")));
    }

    @Test
    public void deleteProject() {
        Mockito.doNothing().when(projectTaskService).deleteProjectTaskByProjectSequence(Mockito.anyString(), Mockito.anyString(), Mockito.anyString());

        projectTaskService.deleteProjectTaskByProjectSequence(Mockito.anyString(), Mockito.anyString(), Mockito.anyString());

        Mockito.verify(projectTaskService, Mockito.times(1)).deleteProjectTaskByProjectSequence("", "", "");

    }

    private List<ProjectTask> createProjectTask() throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

        Date NOW = sdf.parse("01/01/2019 00:00:01");

        ProjectTask p1 = ProjectTask.builder()
                .acceptanceCriteria("acceptance")
                .backlog(Backlog.builder().id(1L).build())
                .dueDate(NOW)
                .priority(3)
                .projectIdentifier("PRJID")
                .projectSequence("Sequence-1")
                .status("TO_DO")
                .summary("summary")
                .build();
        p1.setId(1L);
        p1.setCreateAt(NOW);
        p1.setUpdateAt(NOW);


        ProjectTask p2 = ProjectTask.builder()
                .acceptanceCriteria("acceptance")
                .backlog(Backlog.builder().id(1L).build())
                .dueDate(NOW)
                .priority(3)
                .projectIdentifier("PRJ2")
                .projectSequence("Sequence-2")
                .status("IN_PROGRESS")
                .summary("summary")
                .build();
        p1.setId(2L);
        p1.setCreateAt(NOW);
        p1.setUpdateAt(NOW);

        return Arrays.asList(p1, p2);
    }
}