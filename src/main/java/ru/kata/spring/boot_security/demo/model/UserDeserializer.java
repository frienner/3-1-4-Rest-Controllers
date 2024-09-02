package ru.kata.spring.boot_security.demo.model;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

public class UserDeserializer extends JsonDeserializer<User> {
    @Override
    public User deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode node = p.getCodec().readTree(p);

        Long id = node.has("id") ? node.get("id").asLong() : null;
        String username = node.get("username").asText();
        String password = node.get("password").asText();
        String firstName = node.get("firstName").asText();
        String lastName = node.get("lastName").asText();
        int age = node.get("age").asInt();

        Collection<Role> roles = new ArrayList<>();
        for (JsonNode roleNode : node.get("roles")) {
            Long roleId = roleNode.get("id").asLong();
            String roleName = roleNode.get("name").asText();
            roles.add(new Role(roleId, roleName));
        }

        return new User(id, username, password, firstName, lastName, age, roles);
    }
}