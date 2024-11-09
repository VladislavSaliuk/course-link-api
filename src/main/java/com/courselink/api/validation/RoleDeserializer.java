package com.courselink.api.validation;

import com.courselink.api.entity.Role;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;

public class RoleDeserializer extends JsonDeserializer<Role> {

    @Override
    public Role deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        String roleStr = p.getText();
        try {
            return Role.valueOf(roleStr);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("This role doesn't exist!");
        }
    }
}
