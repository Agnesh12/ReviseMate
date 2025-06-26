package com.example.revisemate.DTO;

public class DashboardDTO {// DashboardStats.java
   public record DashboardStats(long totalTopics,
                long todayRevisions,
                long completedRevisions) {}


}
