package org.mourathi.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import org.hibernate.annotations.UuidGenerator;
import org.jetbrains.annotations.NotNull;

import java.util.Date;

@Entity
public class APIKey {
    @Id
    @UuidGenerator
    private String id;
    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private String key;
    private Date expiry;
    private boolean active;

    public APIKey() {
        this.active = true;
    }

    public APIKey(Date expiry, String key, String name) {
        this();
        this.expiry = expiry;
        this.key = key;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Date getExpiry() {
        return expiry;
    }

    public void setExpiry(Date expiry) {
        this.expiry = expiry;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
