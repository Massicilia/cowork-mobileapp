package com.example.coworkandroid.model;

public class Ticket {


    private String uuidTicket;
    private String title;
    private String description;
    private String uuidCreator;
    private String uuidAssignee;
    private String status;
    private String dateTicketCreation;
    private String dateExpectedResolution;


    public Ticket(String uuidTicket, String title, String description, String uuidCreator, String uuidAssignee, String status, String dateTicketCreation, String dateExpectedResolution) {
        this.uuidTicket = uuidTicket;
        this.uuidCreator = uuidCreator;
        this.uuidAssignee = uuidAssignee;
        this.status = status;
        this.dateTicketCreation = dateTicketCreation;
        this.dateExpectedResolution = dateExpectedResolution;
        this.title = title;
        this.description = description;
    }

    public String getUuidTicket() {
        return uuidTicket;
    }

    public String getUuidCreator() {
        return uuidCreator;
    }

    public String getUuidAssignee() {
        return uuidAssignee;
    }

    public String getStatus() {
        return status;
    }

    public String getDateTicketCreation() {
        return dateTicketCreation;
    }

    public String getDateExpectedResolution() {
        return dateExpectedResolution;
    }

    public String getTitle() {return title;}

    public String getDescription() {return description;}

}
