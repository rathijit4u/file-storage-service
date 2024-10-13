package org.mourathi.dto;

import java.time.ZonedDateTime;
import java.util.Date;

public class BucketDto {
    private String name;
    private ZonedDateTime created;

    public BucketDto() {
    }

    public BucketDto(String name, ZonedDateTime created) {
        this.name = name;
        this.created = created;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ZonedDateTime getCreated() {
        return created;
    }

    public void setCreated(ZonedDateTime created) {
        this.created = created;
    }
}
