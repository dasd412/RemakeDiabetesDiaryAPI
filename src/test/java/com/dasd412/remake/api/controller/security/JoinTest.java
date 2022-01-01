package com.dasd412.remake.api.controller.security;

import com.dasd412.remake.api.controller.security.writer.UserJoinRequestDTO;
import com.dasd412.remake.api.domain.diary.writer.Writer;
import com.dasd412.remake.api.domain.diary.writer.WriterRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.context.annotation.Profile;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import javax.persistence.NoResultException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Profile("test")
public class JoinTest {

    @LocalServerPort
    private int port;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private WriterRepository writerRepository;

    private MockMvc mockMvc;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());


    @Before
    public void setup() throws Exception {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .build();
        //given
        UserJoinRequestDTO dto = new UserJoinRequestDTO("before", "before", "before@naver.com");
        String url = "http://localhost:" + port + "/signup/user";

        //when and then
        mockMvc.perform(post(url)
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .content(new ObjectMapper().writeValueAsString(dto)))
                .andExpect(status().isOk());
    }

    @After
    public void clean() {
        writerRepository.deleteAll();
    }

    @Test
    public void signUp() throws Exception {
        //given
        UserJoinRequestDTO dto = new UserJoinRequestDTO("test", "test", "test@naver.com");
        String url = "http://localhost:" + port + "/signup/user";

        //when and then
        mockMvc.perform(post(url)
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .content(new ObjectMapper().writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(MockMvcResultMatchers.jsonPath("$.success").value("true"));

        Writer writer = writerRepository.findWriterByName("test").orElseThrow(NoResultException::new);
        assertThat(writer.getName()).isEqualTo(dto.getName());
        assertThat(writer.getEmail()).isEqualTo(dto.getEmail());
    }

    @Test
    public void duplicateName() throws Exception {
        //given
        UserJoinRequestDTO dto = new UserJoinRequestDTO("before", "before", "duplicateName@naver.com");
        String url = "http://localhost:" + port + "/signup/user";
        //when and then
        mockMvc.perform(post(url)
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .content(new ObjectMapper().writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andDo(print());
    }

    @Test
    public void duplicateEmail() throws Exception {

    }
}