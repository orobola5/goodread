package africa.semicolon.goodreads.Config;

import africa.semicolon.goodreads.Repositories.UserRepository.UserRepository;
import africa.semicolon.goodreads.models.RoleType;
import africa.semicolon.goodreads.models.User;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@Slf4j
@AllArgsConstructor
public class SetupDataLoader implements ApplicationListener<ContextRefreshedEvent> {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;



    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        if (userRepository.findUserByEmail("adminuser@gmail.com").isEmpty()){
            User user = new User("Admin", "User","adminuser@gmail.com", passwordEncoder
                    .encode("password1234#"),
                    RoleType.ROLE_ADMIN);
            user.setDateJoined(LocalDate.now());
            userRepository.save(user);
        }
    }
}
