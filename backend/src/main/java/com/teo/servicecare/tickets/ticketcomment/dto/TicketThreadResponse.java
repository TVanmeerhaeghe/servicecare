package com.teo.servicecare.tickets.ticketcomment.dto;

import com.teo.servicecare.tickets.dto.TicketResponse;
import org.springframework.data.domain.Page;

import java.util.List;

public class TicketThreadResponse {
  private TicketResponse ticket;

  private List<TicketCommentResponse> comments;
  private int page;
  private int size;
  private int totalPages;
  private long totalElements;

  public static TicketThreadResponse of(TicketResponse ticket, Page<TicketCommentResponse> commentsPage) {
    var r = new TicketThreadResponse();
    r.ticket = ticket;
    r.comments = commentsPage.getContent();
    r.page = commentsPage.getNumber();
    r.size = commentsPage.getSize();
    r.totalPages = commentsPage.getTotalPages();
    r.totalElements = commentsPage.getTotalElements();
    return r;
  }

  public TicketResponse getTicket() { return ticket; }
  public List<TicketCommentResponse> getComments() { return comments; }
  public int getPage() { return page; }
  public int getSize() { return size; }
  public int getTotalPages() { return totalPages; }
  public long getTotalElements() { return totalElements; }
}
