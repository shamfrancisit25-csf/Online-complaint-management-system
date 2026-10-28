package com.resolve.ocms.controller;

import com.resolve.ocms.model.AppNotification;
import com.resolve.ocms.model.Complaint;
import com.resolve.ocms.repository.ComplaintRepository;
import com.resolve.ocms.repository.NotificationRepository;
import com.resolve.ocms.service.PriorityService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.time.LocalDate;
import java.util.*;

@RestController
@RequestMapping("/api")
public class ComplaintController {
    private final ComplaintRepository complaints;
    private final NotificationRepository notifications;
    private final PriorityService priorityService;
    public ComplaintController(ComplaintRepository c, NotificationRepository n, PriorityService p){complaints=c;notifications=n;priorityService=p;}

    @GetMapping("/complaints")
    public List<Complaint> all(@RequestParam(required=false) String status,@RequestParam(required=false) String search){
        return complaints.findAll().stream()
            .filter(c -> status==null || status.isBlank() || status.equalsIgnoreCase("All") || c.getStatus().equalsIgnoreCase(status))
            .filter(c -> search==null || search.isBlank() || (c.getId()+" "+c.getTitle()+" "+c.getCategory()+" "+c.getStatus()).toLowerCase().contains(search.toLowerCase()))
            .sorted(Comparator.comparing(Complaint::getDate,Comparator.nullsLast(Comparator.reverseOrder())))
            .toList();
    }
    @GetMapping("/complaints/{id}")
    public Complaint get(@PathVariable String id){return complaints.findById(id).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Complaint not found"));}

    @PostMapping("/complaints")
    @ResponseStatus(HttpStatus.CREATED)
    public Complaint create(@RequestBody Map<String,String> body){
        String category=required(body,"category"), title=required(body,"title"), description=required(body,"description");
        if(title.length()>90 || description.length()>1200) throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Title or description is too long");
        Complaint c=new Complaint(); c.setId(nextReference()); c.setCategory(category); c.setTitle(title); c.setDescription(description);
        c.setLocation(body.getOrDefault("location","")); c.setPriority(priorityService.calculate(category,description)); c.setStatus("Assigned");
        c.setAssigned(category.equals("Other")?"General Services":category+" Team"); c.setDate(LocalDate.now()); c.setRemarks("Complaint received and assigned.");
        Complaint saved=complaints.save(c);
        notifications.save(new AppNotification("Complaint "+saved.getId()+" submitted","Your "+category.toLowerCase()+" complaint has been received. Priority: "+saved.getPriority()+".","success"));
        return saved;
    }
    @PatchMapping("/complaints/{id}/status")
    public Complaint updateStatus(@PathVariable String id,@RequestBody Map<String,String> body){
        Complaint c=get(id); String status=required(body,"status");
        Set<String> valid=Set.of("Submitted","Under Review","Assigned","In Progress","Resolved","Closed","Escalated");
        if(!valid.contains(status)) throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Invalid complaint status");
        c.setStatus(status); if(body.containsKey("assigned")) c.setAssigned(body.get("assigned"));
        if(body.containsKey("remarks")) c.setRemarks(body.get("remarks"));
        Complaint saved=complaints.save(c);
        notifications.save(new AppNotification("Complaint "+id+" updated","Status changed to "+status+". "+(c.getRemarks()==null?"":c.getRemarks()),status.equals("Resolved")||status.equals("Closed")?"success":"info"));
        return saved;
    }
    @GetMapping("/stats")
    public Map<String,Long> stats(){List<Complaint> all=complaints.findAll(); Map<String,Long> s=new LinkedHashMap<>();
        s.put("total",(long)all.size()); s.put("inProgress",all.stream().filter(c->Set.of("Submitted","Under Review","Assigned","In Progress").contains(c.getStatus())).count());
        s.put("resolved",all.stream().filter(c->Set.of("Resolved","Closed").contains(c.getStatus())).count()); s.put("attention",all.stream().filter(c->c.getStatus().equals("Escalated")).count()); return s;}
    private String required(Map<String,String> b,String key){String v=b.get(key);if(v==null||v.trim().isEmpty())throw new ResponseStatusException(HttpStatus.BAD_REQUEST,key+" is required");return v.trim();}
    private String nextReference(){int max=1040;for(Complaint c:complaints.findAll()){try{max=Math.max(max,Integer.parseInt(c.getId().replace("CMP-","")));}catch(Exception ignored){}}return "CMP-"+(max+1);}
}
