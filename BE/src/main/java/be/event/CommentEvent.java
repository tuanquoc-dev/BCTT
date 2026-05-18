package be.event;

import be.dto.response.CommentResponse;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CommentEvent {

    private CommentResponse comment;
    private String action; // CREATE | UPDATE | DELETE
}