package africa.semicolon.goodreads.Controller;

import africa.semicolon.goodreads.Services.BookService;
import africa.semicolon.goodreads.dtos.Request.BookItemUploadRequest;
import africa.semicolon.goodreads.dtos.Response.ApiResponse;
import africa.semicolon.goodreads.exceptions.GoodReadException;
import africa.semicolon.goodreads.models.Book;
import africa.semicolon.goodreads.models.Credentials;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@RestController
@Slf4j
@RequestMapping("/api/v1/books")
public class BookController {
    private final BookService bookService;
    private final List<String> validImageExtensions;
    private final List<String> validFileExtensions;

    public BookController(BookService bookService) {
        this.bookService = bookService;
        validImageExtensions = Arrays.asList(".png", ".jpg",".jpeg");
        validFileExtensions = Arrays.asList(".txt", ".pdf", ".doc", ".docx", ".csv",
                ".epub", ".xlsx");
    }

    @PostMapping("/")
    public ResponseEntity<?> uploadBookItem(@RequestBody @Valid @NotNull BookItemUploadRequest bookItemUploadRequest){
        Book book = bookService.save(bookItemUploadRequest);
        ApiResponse apiResponse = ApiResponse.builder()
                .status("success")
                .message("book saved successfully")
                .data(book)
                .build();
        return  new ResponseEntity<>(apiResponse, HttpStatus.CREATED);
    }

    @GetMapping("/{pageNo}/{noOfItems}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public ResponseEntity<?> getAllBooks(
            @PathVariable(value = "pageNo", required = false) @DefaultValue({"0"}) @NotNull String pageNo,
            @PathVariable(value = "noOfItems", required = false) @DefaultValue({"10"}) @NotNull String numberOfItems){

        Map<String, Object> pageResult = bookService.findAll(Integer.parseInt(pageNo), Integer.parseInt(numberOfItems));
        ApiResponse apiResponse = ApiResponse.builder()
                .status("success")
                .message("pages returned")
                .data(pageResult)
                .result((int)pageResult.get("NumberOfElementsInPage"))
                .build();
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @GetMapping("/")
    public ResponseEntity<?> getBookByTitle(@RequestParam @NotNull @NotBlank String title) throws GoodReadException {
        Book book = bookService.findBookByTitle(title);
        ApiResponse apiResponse = ApiResponse.builder()
                .status("success")
                .message("book found")
                .data(book)
                .result(1)
                .build();
        return new ResponseEntity<>(apiResponse, HttpStatus.FOUND);
    }

    @GetMapping("/upload")
    public ResponseEntity<?> getUploadUrls(
            @RequestParam("fileExtension") @Valid @NotBlank @NotNull String fileExtension,
            @RequestParam("imageExtension") @Valid @NotBlank @NotNull String imageExtension) throws ExecutionException, InterruptedException, GoodReadException {
        if (!validFileExtensions.contains(fileExtension)){
            throw new GoodReadException("file extension not accepted", 400);
        }
        if (!validImageExtensions.contains(imageExtension)){
            throw new GoodReadException("image extension not accepted", 400);
        }
        Map<String, Credentials> map = bookService.generateUploadURLs(fileExtension, imageExtension).get();
        ApiResponse apiResponse = ApiResponse.builder()
                .status("success")
                .message("upload urls created")
                .data(map)
                .build();
        return new ResponseEntity<>(apiResponse, HttpStatus.CREATED);
    }

    @GetMapping("/download")
    public ResponseEntity<?> getDownloadUrls(
            @RequestParam("fileName") @Valid @NotBlank @NotNull String fileName,
            @RequestParam("imageFileName") @Valid @NotBlank @NotNull String imageFileName
    ) throws GoodReadException {
        if (!validFileExtensions.contains("."+fileName.split("\\.")[1])){
            throw new GoodReadException("file extension not accepted", 400);
        }
        if (!validImageExtensions.contains("."+imageFileName.split("\\.")[1])){
            throw new GoodReadException("image file extension not accepted", 400);
        }
        Map<String, String> map = bookService.generateDownloadUrls(fileName, imageFileName);
        ApiResponse apiResponse = ApiResponse.builder()
                .status("success")
                .message("download urls created")
                .data(map)
                .build();
        return new ResponseEntity<>(apiResponse, HttpStatus.CREATED);
    }
}
