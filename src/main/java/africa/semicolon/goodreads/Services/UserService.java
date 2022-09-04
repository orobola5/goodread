package africa.semicolon.goodreads.Services;

import africa.semicolon.goodreads.dtos.Request.AccountCreationRequest;
import africa.semicolon.goodreads.dtos.Request.UpdateRequest;
import africa.semicolon.goodreads.dtos.UserDto;
import africa.semicolon.goodreads.exceptions.GoodReadException;
import africa.semicolon.goodreads.models.User;
import com.mashape.unirest.http.exceptions.UnirestException;

import java.util.List;
import java.util.concurrent.ExecutionException;

public interface UserService {
    UserDto createUserAccount(String host, AccountCreationRequest accountCreationRequest) throws GoodReadException, UnirestException, ExecutionException, InterruptedException;
    UserDto findUserById(String userId) throws GoodReadException;
    List<UserDto> findAll();
    UserDto updateUserProfile(String id, UpdateRequest updateRequest) throws GoodReadException;
    User findUserByEmail(String email) throws GoodReadException;

    void verifyUser(String token) throws GoodReadException;
}
