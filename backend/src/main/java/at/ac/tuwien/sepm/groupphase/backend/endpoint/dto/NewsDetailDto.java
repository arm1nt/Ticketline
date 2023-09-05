package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
public class NewsDetailDto {

    private Long id;

    private LocalDateTime publishedAt;
    @Size(min = 1, message = "Title can't be shorter then 1 character (leading/trailing whitespace is ignored)")
    @Size(max = 100, message = "Title can't be longer then 100 characters (leading/trailing whitespace is ignored)")
    @NotNull(message = "Title must not be null")
    @NotBlank(message = "Title must not be blank")
    private String title;
    @Size(min = 1, message = "Text can't be shorter then 1 character (leading/trailing whitespace is ignored)")
    @Size(max = 10000, message = "Text can't be longer then 10000 characters (leading/trailing whitespace is ignored)")
    @NotNull(message = "Text must not be null")
    @NotBlank(message = "Text must not be blank")
    private String text;

    @Size(max = 10000000, message = "Image file to large")
    private String image;

    public void setTitle(String title) {
        if (title != null) {
            this.title = title.trim();
        } else {
            this.title = title;
        }
    }


    public void setText(String text) {
        if (text != null) {
            this.text = text.trim();
        } else {
            this.text = text;
        }
    }
}
