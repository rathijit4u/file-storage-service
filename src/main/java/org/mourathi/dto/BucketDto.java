package org.mourathi.dto;

import java.time.LocalDate;
import java.util.Date;

public class BucketDto {
    private String name;
    private LocalDate createdDate;

    public BucketDto() {
    }

    public BucketDto(String name, LocalDate createdDate) {
        this.name = name;
        this.createdDate = createdDate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDate getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDate createdDate) {
        this.createdDate = createdDate;
    }

    @Override
    public String toString() {
        return "BucketDto{" +
                "name='" + name + '\'' +
                '}';
    }
}
