package africa.semicolon.goodreads.Services;

import africa.semicolon.goodreads.dtos.BookDto;
import africa.semicolon.goodreads.dtos.Request.BookItemUploadRequest;
import africa.semicolon.goodreads.exceptions.GoodReadException;
import africa.semicolon.goodreads.models.Book;
import africa.semicolon.goodreads.models.Credentials;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
@Service
public interface BookService {
    CompletableFuture<Map<String, Credentials>> generateUploadURLs(String fileExtension, String imageExtension) throws ExecutionException, InterruptedException;
    Book save(BookItemUploadRequest bookItemUploadRequest);
    Book findBookByTitle(String title) throws GoodReadException;
    Map<String, String> generateDownloadUrls(String fileName, String imageFileName) throws GoodReadException;
    Map<String, Object> findAll(int pageNumber, int noOfItems);

    List<BookDto> getAllBooksForUser(String email);
}
