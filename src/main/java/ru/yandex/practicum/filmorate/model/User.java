package ru.yandex.practicum.filmorate.model;


import javax.validation.constraints.Email;
import javax.validation.constraints.PastOrPresent;
import lombok.Builder;
import lombok.Data;
import ru.yandex.practicum.filmorate.validation.CorrectLogin;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

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
    private LocalDateTime birthday;
    private Set<Long> friends = new HashSet<>();


    public void addFriend(Long friendId) {
        friends.add(friendId);
    }

    public void deleteFriend(Long friendId) {
        friends.remove(friendId);
    }
}
