package africa.semicolon.goodreads.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class Credentials{
    private String fileName;
    private String uploadUrl;
}
