package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class NewsShortDto {
    private Long id;

    private LocalDateTime publishedAt;

    private String title;

    private String text;

    private String image;
}
