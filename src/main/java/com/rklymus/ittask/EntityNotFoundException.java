package com.rklymus.ittask;

public class EntityNotFoundException extends RuntimeException {
    public EntityNotFoundException(Integer id) {
        super("Entity with id=" + id + " not found");
    }
}
