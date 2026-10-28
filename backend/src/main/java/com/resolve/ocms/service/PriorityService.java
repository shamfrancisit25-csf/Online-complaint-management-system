package com.resolve.ocms.service;
import org.springframework.stereotype.Service;
import java.util.Map;
@Service
public class PriorityService {
    private static final Map<String,Integer> WEIGHTS=Map.of("Electricity",4,"Water Supply",3,"Sanitation",3,"Road Maintenance",2,"Billing Issues",2,"Other",1);
    public String calculate(String category,String description){
        String text=description==null?"":description.toLowerCase();
        int score=WEIGHTS.getOrDefault(category,1);
        for(String word:new String[]{"danger","accident","fire","shock","overflow","no supply"}) if(text.contains(word)){score+=3;break;}
        if(text.contains("again")||text.contains("repeated")) score++;
        return score>=6?"High":score>=3?"Medium":"Low";
    }
}
