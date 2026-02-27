package org.example.bookmyshowpaymentservice.common.exception;

public class DownstreamServiceException extends ServiceException {

    private final int statusCode;

    public DownstreamServiceException(int statusCode, String message) {
        super(message);
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return statusCode;
    }
}

