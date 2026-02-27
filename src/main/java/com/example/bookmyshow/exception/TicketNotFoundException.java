package com.example.bookmyshow.exception;


public class TicketNotFoundException extends ServiceException {

    public TicketNotFoundException(Long ticketId) {
        super("Ticket not found with id: " + ticketId);
    }

    public TicketNotFoundException(String message) {
        super(message);
    }
}






