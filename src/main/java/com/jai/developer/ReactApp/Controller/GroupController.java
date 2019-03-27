package com.jai.developer.ReactApp.Controller;

import com.jai.developer.ReactApp.model.Group;
import com.jai.developer.ReactApp.model.GroupRepository;
import com.jai.developer.ReactApp.model.User;
import com.jai.developer.ReactApp.model.UserRepository;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;


@RestController
@RequestMapping("/api")
public class GroupController {

    private final Logger logger = (Logger) LoggerFactory.getLogger(GroupController.class);

    private GroupRepository groupRepository;
    private UserRepository userRepository;

    public GroupController(GroupRepository groupRepository, UserRepository userRepository) {
        this.groupRepository = groupRepository;
        this.userRepository = userRepository;
    }

    @GetMapping("/groups")
    Collection<Group> groups(){
        return groupRepository.findAll();
    }

    @GetMapping("/group/{id}")
    ResponseEntity<?> getGroup(@PathVariable Long id){
        Optional<Group> group = groupRepository.findById(id);
        return group.map(response -> ResponseEntity.ok().body(response)).orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping("/group")
    ResponseEntity<Group> createGroup(@Valid @RequestBody Group group,
                                      @AuthenticationPrincipal OAuth2User principal) throws URISyntaxException {
        logger.info("Request to create group: {}", group);
        Map<String, Object> details = principal.getAttributes();
        String userId = details.get("sub").toString();

        // check to see if user already exists
        Optional<User> user = userRepository.findById(userId);
        group.setUser(user.orElse(new User(userId,
                details.get("name").toString(), details.get("email").toString())));

        Group result = groupRepository.save(group);
        return ResponseEntity.created(new URI("/api/group/" + result.getId()))
                .body(result);
    }

    @PutMapping("/group/{id}")
    ResponseEntity<Group> UpdateGroup(@Valid @RequestBody Group group){

        logger.info("Request to update a group details: {}",group);

        Group res = groupRepository.save(group);

        return ResponseEntity.ok().body(res);
    }

    @DeleteMapping("/group/{id}")
    ResponseEntity<?> DeleteGroup(@PathVariable Long id){
        logger.info("Request to delete a group: {}", id);

        groupRepository.deleteById(id);

        return ResponseEntity.ok().build();

    }

}
