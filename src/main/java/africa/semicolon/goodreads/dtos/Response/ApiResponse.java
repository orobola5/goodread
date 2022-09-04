package africa.semicolon.goodreads.dtos.Response;

import lombok.*;

import java.io.Serializable;
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ApiResponse implements Serializable {
    private String status;
    private String message;
    private Object data;
    private int result;
}
