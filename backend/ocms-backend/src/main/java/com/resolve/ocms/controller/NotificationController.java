package com.resolve.ocms.controller;
import com.resolve.ocms.model.AppNotification;
import com.resolve.ocms.repository.NotificationRepository;
import org.springframework.web.bind.annotation.*;
import java.util.List;
@RestController
@RequestMapping("/api/notifications")
public class NotificationController {
    private final NotificationRepository repository;
    public NotificationController(NotificationRepository repository){this.repository=repository;}
    @GetMapping public List<AppNotification> all(){return repository.findAll().stream().sorted((a,b)->b.getCreatedAt().compareTo(a.getCreatedAt())).toList();}
    @PatchMapping("/{id}/read") public AppNotification markRead(@PathVariable Long id){AppNotification n=repository.findById(id).orElseThrow();n.setRead(true);return repository.save(n);}
    @PatchMapping("/read-all") public List<AppNotification> markAllRead(){List<AppNotification> list=repository.findAll();list.forEach(n->n.setRead(true));return repository.saveAll(list);}
}
