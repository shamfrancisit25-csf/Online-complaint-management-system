package com.resolve.ocms.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "complaints")
public class Complaint {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long databaseId;
    @Column(unique = true, nullable = false) private String id;
    @Column(nullable = false) private String category;
    @Column(nullable = false, length = 90) private String title;
    @Column(nullable = false, length = 1200) private String description;
    private String location;
    @Column(nullable = false) private String priority;
    @Column(nullable = false) private String status;
    private String assigned;
    @Column(nullable = false) private LocalDate date;
    @Column(length = 1200) private String remarks;

    public Complaint() {}
    public Long getDatabaseId(){return databaseId;} public void setDatabaseId(Long v){databaseId=v;}
    public String getId(){return id;} public void setId(String v){id=v;}
    public String getCategory(){return category;} public void setCategory(String v){category=v;}
    public String getTitle(){return title;} public void setTitle(String v){title=v;}
    public String getDescription(){return description;} public void setDescription(String v){description=v;}
    public String getLocation(){return location;} public void setLocation(String v){location=v;}
    public String getPriority(){return priority;} public void setPriority(String v){priority=v;}
    public String getStatus(){return status;} public void setStatus(String v){status=v;}
    public String getAssigned(){return assigned;} public void setAssigned(String v){assigned=v;}
    public LocalDate getDate(){return date;} public void setDate(LocalDate v){date=v;}
    public String getRemarks(){return remarks;} public void setRemarks(String v){remarks=v;}
}
