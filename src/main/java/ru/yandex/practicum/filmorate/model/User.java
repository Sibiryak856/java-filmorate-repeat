package ru.yandex.practicum.filmorate.model;


import lombok.Builder;
import lombok.Data;
import ru.yandex.practicum.filmorate.validation.CorrectLogin;

import javax.validation.constraints.Email;
import javax.validation.constraints.PastOrPresent;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@Builder
public class User {
    private Long id;
    @Email
    private String email;
    @CorrectLogin
    private String login;
    private String name;
    @PastOrPresent
    private LocalDate birthday;
    private List<User> friends;

    public Map<String,Object> toMap() {
        Map<String, Object> values = new HashMap<>();
        values.put("email", email);
        values.put("login", login);
        values.put("user_name", name);
        values.put("birthday", birthday);
        return values;
    }
}
