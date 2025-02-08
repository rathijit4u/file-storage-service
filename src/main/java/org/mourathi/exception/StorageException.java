package org.mourathi.exception;

public class StorageException extends RuntimeException {
    public StorageException(Exception ex) {
        super(ex);
    }
}
