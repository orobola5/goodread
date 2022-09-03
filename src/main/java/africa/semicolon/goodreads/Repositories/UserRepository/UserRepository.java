package africa.semicolon.goodreads.Repositories.UserRepository;

import africa.semicolon.goodreads.models.User;

import java.util.Collection;

public interface UserRepository {
    Collection<Object> findUserByEmail(String s);

    void save(User user);
}
