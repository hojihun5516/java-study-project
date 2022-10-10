package com.example.all.account;

import com.example.all.domain.Account;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.then;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class AccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AccountRepository accountRepository;

    @MockBean
    JavaMailSender javaMailSender;

    @DisplayName("회원가입 화면 잘 보이는지")
    @Test
    void signUpForm() throws Exception {
        mockMvc.perform(get("/sign-up")).andExpect(status().isOk())
                .andExpect(view().name("account/sign-up"))
                .andExpect(model().attributeExists("signUpForm"));
    }

    @DisplayName("회원가입 처리 - 입력값 오류")
    @Test
    void signUpSubmit_with_wrong_input() throws Exception {
        mockMvc.perform(post("/sign-up")
                        .param("nickname", "hoon")
                        .param("email", "email...")
                        .param("password", "123")
                        .with(csrf())
                )
                .andExpect(status().isOk())
                .andExpect(view().name("account/sign-up"));
    }

    @DisplayName("회원가입 처리 - 입력값 정상")
    @Test
    void signUpSubmit_with_input() throws Exception {
        mockMvc.perform(post("/sign-up")
                        .param("nickname", "hoon")
                        .param("email", "test@gmail.com")
                        .param("password", "12341234")
                        .with(csrf())
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/"));

        assertTrue(accountRepository.existsByEmail("test@gmail.com"));
        Account account = accountRepository.findByEmail("test@gmail.com");
        assertNotNull(account);
        assertNotEquals(account.getPassword(),"12341234");
        assertNotNull(account.getEmailCheckToken());
        then(javaMailSender).should().send(any(SimpleMailMessage.class));
    }

}