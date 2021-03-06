package demo.simplerest;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import demo.simplerest.controllers.UserController;
import demo.simplerest.entities.User;
import demo.simplerest.services.UserService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;

import static org.hamcrest.Matchers.is;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@RunWith(SpringJUnit4ClassRunner.class)
public class UserControllerTest {
    private static final MediaType APPLICATION_JSON_UTF8 = new MediaType(MediaType.APPLICATION_JSON.getType(), MediaType.APPLICATION_JSON.getSubtype(), Charset.forName("utf8"));

    @InjectMocks
    private UserController userController;
    @Mock
    private UserService userService;

    private MockMvc mockMvc;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
    }

    @Test
    public void shouldReturnOkStatusOnSave() throws Exception {
        User user = newUser();
        when(userService.create(user)).thenReturn(user);
        mockMvc.perform(
                post("/users/")
                        .contentType(APPLICATION_JSON_UTF8)
                        .content(convertObjectToJsonBytes(user)))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void shouldReturnOkStatusOnUpdate() throws Exception {
        User user = newUser();
        when(userService.findById(user.getId())).thenReturn(user);
        when(userService.update(any(User.class))).thenReturn(user);
        mockMvc.perform(
                put("/users/")
                        .contentType(APPLICATION_JSON_UTF8)
                        .content(convertObjectToJsonBytes(user)))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void shouldReturn404IfUpdateNotExistedUser() throws Exception {
        User user = newUser();
        when(userService.findById(user.getId())).thenReturn(null);
        when(userService.update(user)).thenReturn(user);
        mockMvc.perform(
                put("/users/")
                        .contentType(APPLICATION_JSON_UTF8)
                        .content(convertObjectToJsonBytes(user)))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }


    @Test
    public void shouldReturnOkStatusOnDelete() throws Exception {
        User user = newUser();
        when(userService.findById(user.getId())).thenReturn(user);
        doNothing().when(userService).remove(user.getId());
        mockMvc.perform(
                delete("/users/" + user.getId()))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void shouldReturn404IfDeleteNotExistedUser() throws Exception {
        User user = newUser();
        when(userService.findById(user.getId())).thenReturn(null);
        doNothing().when(userService).remove(user.getId());
        mockMvc.perform(
                delete("/users/" + user.getId()))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    public void shouldReturnUsersOnFindAll() throws Exception {
        when(userService.findAll()).thenReturn(new ArrayList<>());
        User user1 = newUser();
        User user2 = new User();
        user2.setName("Jane");
        user2.setId(19l);

        when(userService.findAll()).thenReturn(Arrays.asList(user1, user2));
        mockMvc.perform(get("/users")).andDo(print());
        mockMvc.perform(
                get("/users"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(content().json("[{'id':15,'name':'Jane','phone':null},{'id':19,'name':'Jane','phone':null}]"));
    }

    @Test
    public void shouldReturnEmptyListOnFindAllIfUsersNotExist() throws Exception {
        when(userService.findAll()).thenReturn(new ArrayList<>());
        mockMvc.perform(
                get("/users"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(content().json("[]"));

    }

    @Test
    public void shouldReturn404WhenUserNotFoundById() throws Exception {
        User user = newUser();
        when(userService.findById(user.getId())).thenReturn(null);
        mockMvc.perform(
                get("/users/name/" + user.getName()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(content().json("[]"));
    }

    @Test
    public void shouldReturnEmptyListWhenUserNotFoundByName() throws Exception {
        User user = newUser();
        when(userService.findByName(user.getName())).thenReturn(new ArrayList<>());
        mockMvc.perform(
                get("/users/name/" + user.getName()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(content().json("[]"));
    }

    @Test
    public void shouldReturnUserIfItFoundByName() throws Exception {
        User user = newUser();
        when(userService.findByName(user.getName())).thenReturn(Arrays.asList(user));
        mockMvc.perform(
                get("/users/name/" + user.getName()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(content().json("[{'id':15,'name':'Jane','phone':null}]"));
    }

    @Test
    public void shouldReturnUserIfItFoundById() throws Exception {
        User user = newUser();
        when(userService.findById(user.getId())).thenReturn(user);

        mockMvc.perform(
                get("/users/" + user.getId()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("id", is(user.getId().intValue())))
                .andExpect(jsonPath("name", is("Jane")));

    }

    private User newUser() {
        User user = new User();
        user.setName("Jane");
        user.setId(15l);
        return user;
    }

    private static byte[] convertObjectToJsonBytes(Object object) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        return mapper.writeValueAsBytes(object);
    }
}
