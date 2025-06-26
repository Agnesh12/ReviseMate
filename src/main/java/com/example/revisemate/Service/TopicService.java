package com.example.revisemate.Service;

import com.example.revisemate.DTO.TopicDTO;
import com.example.revisemate.Model.Revision;
import com.example.revisemate.Model.Topic;
import com.example.revisemate.Model.User;
import com.example.revisemate.Repository.RevisionRepository;
import com.example.revisemate.Repository.TopicRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

// TopicService.java
@Service
@RequiredArgsConstructor
public class TopicService {
    private final TopicRepository topicRepo;
    private final RevisionRepository revisionRepo;
    private static final List<Integer> REVISION_DAYS = List.of(1, 3, 7);

    @Transactional
    public TopicDTO.TopicResponse createTopic(Long userId, TopicDTO.TopicCreateRequest req) {
        Topic topic = new Topic();
        topic.setUser(new User() {{ setId(userId); }}); // only the ID is needed
        topic.setTitle(req.title());
        topic.setDescription(req.description());
        topicRepo.save(topic);

        LocalDate today = LocalDate.now();
        for (int i = 0; i < REVISION_DAYS.size(); i++) {
            Revision r = new Revision();
            r.setTopic(topic);
            r.setRevisionNumber(i + 1);
            r.setDueDate(today.plusDays(REVISION_DAYS.get(i)));
            revisionRepo.save(r);
        }
        return toDto(topic);
    }

    public List<TopicDTO.TopicResponse> listTopics(Long userId) {
        return topicRepo.findAllByUserIdOrderByCreatedAtDesc(userId)
                .stream().map(this::toDto).toList();
    }

    private TopicDTO.TopicResponse toDto(Topic t) {
        return new TopicDTO.TopicResponse(t.getId(), t.getTitle(), t.getDescription(), t.getCreatedAt());
    }
}

