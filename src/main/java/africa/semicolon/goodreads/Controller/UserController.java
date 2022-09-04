package africa.semicolon.goodreads.Controller;

import africa.semicolon.goodreads.Services.BookService;
import africa.semicolon.goodreads.Services.UserService;
import africa.semicolon.goodreads.dtos.BookDto;
import africa.semicolon.goodreads.dtos.Request.UpdateRequest;
import africa.semicolon.goodreads.dtos.Response.ApiResponse;
import africa.semicolon.goodreads.dtos.UserDto;
import africa.semicolon.goodreads.exceptions.GoodReadException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.config.EnableHypermediaSupport;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@Slf4j
@RequestMapping("/api/v1/users")
@EnableHypermediaSupport(type = EnableHypermediaSupport.HypermediaType.HAL)
public class UserController {
    private final UserService userService;
    private final BookService bookService;

    public UserController(UserService userService, BookService bookService) {
        this.userService = userService;
        this.bookService = bookService;
    }
    @GetMapping("/{id}")
    public ResponseEntity<?> getUser(@PathVariable("id") @NotNull @NotBlank String userId) {
        try {
            if (("null").equals(userId) || ("").equals(userId.trim())){
                throw new GoodReadException("String id cannot be null", 400);
            }
            UserDto userDto = userService.findUserById(userId);
            Link selfLink = linkTo(UserController.class).slash(userDto.getId()).withSelfRel();
            userDto.add(selfLink);
            ApiResponse apiResponse = ApiResponse.builder()
                    .status("success")
                    .message("user found")
                    .data(userDto)
                    .result(1)
                    .build();
            return new ResponseEntity<>(apiResponse, HttpStatus.OK);
        } catch (GoodReadException e) {
            ApiResponse apiResponse = ApiResponse.builder()
                    .status("fail")
                    .message(e.getMessage())
                    .build();
            return new ResponseEntity<>(apiResponse, HttpStatus.valueOf(e.getStatusCode()));
        }
    }

    @GetMapping(value = "/", produces = { "application/hal+json" })
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public ResponseEntity<?> getAllUsers(){
        List<UserDto> users = userService.findAll();
        for (final UserDto user: users){
            Long userId = user.getId();
            Link selfLink = linkTo(UserController.class).slash(userId).withSelfRel();
            user.add(selfLink);

            List<BookDto> booksUploadedByUser = bookService.getAllBooksForUser(user.getEmail());

            if (booksUploadedByUser.size() > 0){
                Link booksLink = linkTo(methodOn(UserController.class).getAllBooksForUser(user.getEmail())).withRel("books uploaded");
                user.add(booksLink);
            }
        }
        Link link = linkTo(UserController.class).withSelfRel();
        CollectionModel<UserDto> result = CollectionModel.of(users, link);
        ApiResponse apiResponse = ApiResponse.builder()
                .status("success")
                .message(users.size() != 0 ? "users found" : "no user exists in database")
                .data(result)
                .result(users.size())
                .build();
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }


    @GetMapping("/books/{email}")
    public List<BookDto> getAllBooksForUser(@PathVariable("email") @Valid @NotBlank @NotNull String email) {
        return bookService.getAllBooksForUser(email);
    }

    @PatchMapping("/")
    public ResponseEntity<?> updateUserProfile(@Valid @NotBlank @NotNull @RequestParam String id,
                                               @RequestBody @NotNull UpdateRequest updateRequest ) throws GoodReadException {

        UserDto userDto = userService.updateUserProfile(id, updateRequest);
        ApiResponse apiResponse = ApiResponse.builder()
                .status("success")
                .message("user found")
                .data(userDto)
                .result(1)
                .build();
        return new ResponseEntity<>(apiResponse, HttpStatus.CREATED);
    }


}
