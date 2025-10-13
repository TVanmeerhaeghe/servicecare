package com.teo.servicecare.tickets.sla;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.teo.servicecare.tickets.Ticket;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "ticket_sla_events")
public class TicketSlaEvent {

    public enum Type {
        WAIT_START, WAIT_END, STATUS_CHANGE, PRIORITY_CHANGE
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "ticket_id")
    @JsonIgnore
    private Ticket ticket;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, columnDefinition = "enum('WAIT_START','WAIT_END','STATUS_CHANGE','PRIORITY_CHANGE')")
    private Type type;

    @Column(name = "happened_at", nullable = false)
    private LocalDateTime happenedAt;

    @Column(name = "actor_user_id")
    private Long actorUserId;

    @Column(name = "actor_user_name")
    private String actorUserName;

    @Column(name = "note", length = 255)
    private String note;

    @Column(name = "payload", columnDefinition = "json")
    private String payloadJson;

    public Long getId() {
        return id;
    }

    public Ticket getTicket() {
        return ticket;
    }

    public void setTicket(Ticket ticket) {
        this.ticket = ticket;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public LocalDateTime getHappenedAt() {
        return happenedAt;
    }

    public void setHappenedAt(LocalDateTime happenedAt) {
        this.happenedAt = happenedAt;
    }

    public Long getActorUserId() {
        return actorUserId;
    }

    public void setActorUserId(Long actorUserId) {
        this.actorUserId = actorUserId;
    }

    public String getActorUserName() {
        return actorUserName;
    }

    public void setActorUserName(String actorUserName) {
        this.actorUserName = actorUserName;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getPayloadJson() {
        return payloadJson;
    }

    public void setPayloadJson(String payloadJson) {
        this.payloadJson = payloadJson;
    }
}