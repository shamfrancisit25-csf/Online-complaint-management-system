package com.resolve.ocms.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
public class AppNotification {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    private String title;
    @Column(length=1200) private String message;
    private LocalDateTime createdAt;
    private String type;
    private boolean readFlag;
    public AppNotification() {}
    public AppNotification(String title,String message,String type){this.title=title;this.message=message;this.type=type;this.createdAt=LocalDateTime.now();this.readFlag=false;}
    public Long getId(){return id;} public void setId(Long v){id=v;}
    public String getTitle(){return title;} public void setTitle(String v){title=v;}
    public String getMessage(){return message;} public void setMessage(String v){message=v;}
    public LocalDateTime getCreatedAt(){return createdAt;} public void setCreatedAt(LocalDateTime v){createdAt=v;}
    public String getType(){return type;} public void setType(String v){type=v;}
    public boolean isRead(){return readFlag;} public void setRead(boolean v){readFlag=v;}
}
