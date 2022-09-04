package africa.semicolon.goodreads.Controller;

import africa.semicolon.goodreads.Services.UserService;
import africa.semicolon.goodreads.dtos.Request.AccountCreationRequest;
import africa.semicolon.goodreads.dtos.Request.LoginRequest;
import africa.semicolon.goodreads.dtos.Response.ApiResponse;
import africa.semicolon.goodreads.dtos.Response.AuthToken;
import africa.semicolon.goodreads.dtos.UserDto;
import africa.semicolon.goodreads.exceptions.GoodReadException;
import africa.semicolon.goodreads.models.User;
import africa.semicolon.goodreads.security.jwt.TokenProvider;
import com.mashape.unirest.http.exceptions.UnirestException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@RestController
@Slf4j
@RequestMapping("/api/v1/auth")

public class AuthController {
    private final UserService userService;
    private final AuthenticationManager authenticationManager;

    private final TokenProvider tokenProvider;

    public AuthController( UserService userService, AuthenticationManager authenticationManager, TokenProvider tokenProvider) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.tokenProvider = tokenProvider;
    }

    @PostMapping("/signup")
    public ResponseEntity<?> createUser(HttpServletRequest request, @RequestBody @Valid @NotNull AccountCreationRequest accountCreationRequest)
            throws UnirestException,  ExecutionException, InterruptedException, GoodReadException {
        String host = request.getRequestURL().toString();
        int index = host.indexOf("/", host.indexOf("/", host.indexOf("/"))+2);
        host = host.substring(0, index+1);
        log.info("Host --> {}", host);
        UserDto userDto = userService.createUserAccount(host,accountCreationRequest);
        ApiResponse apiResponse = ApiResponse.builder()
                .status("success")
                .message("user created successfully")
                .data(userDto)
                .build();
        log.info("Returning response");
        return new ResponseEntity<>(apiResponse, HttpStatus.CREATED);
    }


    @RequestMapping("/verify/{token}")
    public ModelAndView verify(@PathVariable("token") String token) throws GoodReadException {
        userService.verifyUser(token);
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("verification_success");
        return modelAndView;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) throws GoodReadException {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(),
                        loginRequest.getPassword())
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        final String token = tokenProvider.generateJWTToken(authentication);
        User user = userService.findUserByEmail(loginRequest.getEmail());
        return new ResponseEntity<>(new AuthToken(token, user.getId()), HttpStatus.OK);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, String> handleValidationExceptions(MethodArgumentNotValidException exception) {
        Map<String, String> errors = new HashMap<>();
        exception.getBindingResult().getAllErrors().forEach((error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        }));
        return errors;
    }
}
