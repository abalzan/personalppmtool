package com.andrei.ppmtool.ppmtool.controllers;

import com.andrei.ppmtool.ppmtool.BaseTestContext;
import com.andrei.ppmtool.ppmtool.model.User;
import com.andrei.ppmtool.ppmtool.security.JWTTokenProvider;
import com.andrei.ppmtool.ppmtool.services.UserService;
import com.andrei.ppmtool.ppmtool.services.ValidationErrorService;
import com.andrei.ppmtool.ppmtool.validators.UserValidator;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class UserControllerTest extends BaseTestContext {

    @Mock
    private UserService userService;

    @Mock
    private ValidationErrorService validationErrorService;

    @Mock
    private UserValidator userValidator;

    @Mock
    private JWTTokenProvider jwtTokenProvider;

    @Mock
    private AuthenticationManager authenticationManager;

    private UserController controller;


    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        controller = new UserController(validationErrorService, userService, userValidator, jwtTokenProvider, authenticationManager);
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .build();
    }

    @Test
    public void authenticateUser() throws Exception {
        Authentication authentication = mock(Authentication.class);

        SecurityContext securityContext = mock(SecurityContext.class);

        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);

        SecurityContextHolder.setContext(securityContext);
        Mockito.doNothing().when(SecurityContextHolder.getContext()).setAuthentication(Mockito.any());

        Mockito.when(validationErrorService.validationErrorService(Mockito.any())).thenReturn(null);
        Mockito.when(jwtTokenProvider.generateToken(Mockito.any())).thenReturn("FakeToken");

        mockMvc.perform(post("/api/users/login")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"username\": \"email@gmail.com\",\"password\": \"12345678\"}")
        ).andExpect(status().isOk())
                .andExpect(jsonPath("$.success", Matchers.is(true)))
                .andExpect(jsonPath("$.token", Matchers.is("Bearer FakeToken")));
    }

    @Test
    public void registerUser() throws Exception {
        Mockito.doNothing().when(userValidator).validate(Mockito.any(), Mockito.any());
        Mockito.when(validationErrorService.validationErrorService(Mockito.any())).thenReturn(null);

        Mockito.when(userService.saveUser(Mockito.any())).thenReturn(createUsers().get(0));

        mockMvc.perform(post("/api/users/register")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"username\": \"email@gmail.com\",\"fullname\": \"Full Name\",\"password\": \"12345678\",\"confirmPassword\": \"12345678\"}")
        ).andExpect(status().isCreated())
                .andExpect(jsonPath("$.username", Matchers.is("email@gmail.com")))
                .andExpect(jsonPath("$.fullname", Matchers.is("Full Name")))
                .andExpect(jsonPath("$.password", Matchers.is("12345678")));
    }

    private List<User> createUsers() throws Exception {

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

        Date NOW = sdf.parse("01/01/2019 00:00:01");

        User u1 = User.builder()
                .username("email@gmail.com")
                .fullname("Full Name")
                .password("12345678")
                .confirmPassword("12345678")
                .build();
        u1.setId(1L);
        u1.setCreateAt(NOW);
        u1.setUpdateAt(NOW);

        User u2 = User.builder()
                .username("secondemail@gmail.com")
                .fullname("Second Name")
                .password("87654321")
                .confirmPassword("87654321")
                .build();
        u2.setId(2L);
        u2.setCreateAt(NOW);
        u2.setUpdateAt(NOW);

        return Arrays.asList(u1, u2);
    }

//    private Authentication createAuthentication(){
//        Authentication a = new A
//    }
}