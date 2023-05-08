package sysmap.socialmediabackend.services;

import java.io.File;
import java.io.IOException;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import sysmap.socialmediabackend.model.Role;

@Service
public class AuthorizationService {
    public boolean isAuthorized(String action, Set<Role> roles) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            String workingDir = System.getProperty("user.dir");
            File privilegesJson = new File(workingDir+"/src/main/java/sysmap/socialmediabackend/services/privileges.json");

            JsonNode rootNode = objectMapper.readTree(privilegesJson);
            for (Role role : roles){
                String roleName = role.getName().name();
                if (rootNode.get(roleName).has(action)){
                    return true;
                }
            }
            return false;
        } catch (IOException e) {
            throw new RuntimeException("Error reading privileges.json file", e);
        }
    }
}
