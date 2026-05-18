package be.event;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CommentEventPublisher {

    private final ApplicationEventPublisher publisher;

    public void publish(CommentEvent event) {
        publisher.publishEvent(event);
    }
}