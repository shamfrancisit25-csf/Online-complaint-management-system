package com.resolve.ocms.config;
import com.resolve.ocms.model.Complaint;
import com.resolve.ocms.model.AppNotification;
import com.resolve.ocms.repository.ComplaintRepository;
import com.resolve.ocms.repository.NotificationRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.time.LocalDate;
@Configuration
public class SeedData {
 @Bean CommandLineRunner seed(ComplaintRepository cr, NotificationRepository nr){return args->{
  if(cr.count()==0){
   add(cr,"CMP-1048","Electricity","Streetlight not working","Streetlight has not worked for three days near the main road.","Main Road","High","In Progress","Electrical Team",LocalDate.of(2026,10,14),"Technician assigned.");
   add(cr,"CMP-1047","Water Supply","Low water pressure","Water pressure is very low in our street.","North Street","Medium","Under Review","Water Department",LocalDate.of(2026,10,12),"Inspection requested.");
   add(cr,"CMP-1046","Road Maintenance","Pothole near bus stop","Large pothole near the bus stop.","Bus Stop","Low","Resolved","Roads Team",LocalDate.of(2026,10,10),"Pothole filled.");
   add(cr,"CMP-1045","Sanitation","Waste collection delayed","Waste has not been collected from the street.","Market Street","Medium","Resolved","Sanitation Team",LocalDate.of(2026,10,8),"Collection completed.");
   add(cr,"CMP-1044","Billing Issues","Incorrect bill amount","The latest bill amount appears incorrect.","Ward 4","Low","Closed","Billing Office",LocalDate.of(2026,10,6),"Bill corrected.");
   add(cr,"CMP-1043","Water Supply","Pipe leakage","Water is leaking from a pipe near the junction.","Junction","High","Escalated","Water Department",LocalDate.of(2026,10,4),"Escalated for urgent attention.");
   add(cr,"CMP-1042","Other","Public bench damaged","A public bench is damaged.","Park","Low","Resolved","General Services",LocalDate.of(2026,10,2),"Repair completed.");
   add(cr,"CMP-1041","Sanitation","Blocked drainage","Drain is blocked and overflowing.","Lake Road","High","Resolved","Sanitation Team",LocalDate.of(2026,9,30),"Drain cleaned.");
  }
  if(nr.count()==0){nr.save(new AppNotification("Complaint CMP-1048 is being handled","The Electrical Team has started working on your streetlight complaint.","info"));nr.save(new AppNotification("Complaint CMP-1046 resolved","Your pothole complaint has been marked as resolved.","success"));}
 };}
 private void add(ComplaintRepository r,String id,String cat,String title,String desc,String loc,String priority,String status,String assigned,LocalDate date,String remarks){Complaint c=new Complaint();c.setId(id);c.setCategory(cat);c.setTitle(title);c.setDescription(desc);c.setLocation(loc);c.setPriority(priority);c.setStatus(status);c.setAssigned(assigned);c.setDate(date);c.setRemarks(remarks);r.save(c);}
}
