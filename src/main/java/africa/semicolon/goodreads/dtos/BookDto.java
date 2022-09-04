package africa.semicolon.goodreads.dtos;

import africa.semicolon.goodreads.models.Enum.AgeRate;
import africa.semicolon.goodreads.models.Enum.Category;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import lombok.*;
import org.springframework.hateoas.RepresentationModel;

import java.io.Serializable;
import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class BookDto extends RepresentationModel<BookDto> implements Serializable {
    private String id;

    private String title;
    private String author;

    private String description;

    private String coverImageFileName;

    private String fileName;

    private AgeRate ageRate;

    private String uploadedBy;
    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    private LocalDate dateUploaded;

    private Category category;
}
