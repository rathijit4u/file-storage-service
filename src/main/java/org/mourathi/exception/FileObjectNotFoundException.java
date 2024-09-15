package org.mourathi.exception;

public class FileObjectNotFoundException extends RuntimeException{
    public FileObjectNotFoundException() {
    }

    public FileObjectNotFoundException(String message) {
        super(message);
    }
}
