package com.teo.servicecare.dashboard.dto;

public class DashboardOverviewResponse {
  private long openCount;
  private long breachedOpenCount;
  private double avgResponseHours;
  private double avgResolveHours;
  private long todayNewTickets;
  private long myAssignedOpen;

  public DashboardOverviewResponse() {}

  public DashboardOverviewResponse(long openCount, long breachedOpenCount,
                                   double avgResponseHours, double avgResolveHours,
                                   long todayNewTickets, long myAssignedOpen) {
    this.openCount = openCount;
    this.breachedOpenCount = breachedOpenCount;
    this.avgResponseHours = avgResponseHours;
    this.avgResolveHours = avgResolveHours;
    this.todayNewTickets = todayNewTickets;
    this.myAssignedOpen = myAssignedOpen;
  }

  public long getOpenCount() { return openCount; }
  public void setOpenCount(long openCount) { this.openCount = openCount; }
  public long getBreachedOpenCount() { return breachedOpenCount; }
  public void setBreachedOpenCount(long breachedOpenCount) { this.breachedOpenCount = breachedOpenCount; }
  public double getAvgResponseHours() { return avgResponseHours; }
  public void setAvgResponseHours(double avgResponseHours) { this.avgResponseHours = avgResponseHours; }
  public double getAvgResolveHours() { return avgResolveHours; }
  public void setAvgResolveHours(double avgResolveHours) { this.avgResolveHours = avgResolveHours; }
  public long getTodayNewTickets() { return todayNewTickets; }
  public void setTodayNewTickets(long todayNewTickets) { this.todayNewTickets = todayNewTickets; }
  public long getMyAssignedOpen() { return myAssignedOpen; }
  public void setMyAssignedOpen(long myAssignedOpen) { this.myAssignedOpen = myAssignedOpen; }
}
