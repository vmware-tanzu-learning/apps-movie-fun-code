package org.superbiz.moviefun;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Objects;

@Component
public class DatabaseServiceCredentials {

    private String vcapServices;

    public DatabaseServiceCredentials(@Value("${VCAP_SERVICES:NOT_SET}") String vcapServiceJson) {
        this.vcapServices = vcapServiceJson;
    }

    public String jdbcUrl(String name, String type) {
        ObjectMapper objectMapper = new ObjectMapper();

        JsonNode root;

        try {
            root = objectMapper.readTree(vcapServices);
        } catch (IOException e) {
            throw new IllegalStateException("No VCAP_SERVICES found", e);
        }

        JsonNode services = root.path(type);

        for (JsonNode service : services) {
            if (Objects.equals(service.get("name").asText(), name)) {
                String jdbcUrl = service.get("credentials").get("jdbcUrl").asText();
                System.out.println("******** JDBC URL ***** :"+ jdbcUrl);
                return jdbcUrl;
            }
        }

        throw new IllegalStateException("No "+ name + " found in VCAP_SERVICES");
    }

}
