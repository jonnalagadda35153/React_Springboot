package com.jai.developer.ReactApp;

import com.jai.developer.ReactApp.model.Event;
import com.jai.developer.ReactApp.model.Group;
import com.jai.developer.ReactApp.model.GroupRepository;
import com.jai.developer.ReactApp.model.User;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Collections;
import java.util.stream.Stream;

@Component
public class initializer implements CommandLineRunner {

    private final GroupRepository groupRepository;

    public initializer(GroupRepository groupRepository) {
        this.groupRepository = groupRepository;
    }


    @Override
    public void run(String... args) throws Exception {
        Stream.of("Denver Dev", "UTAH Dev", "Jersey Dev", "Seattle Dev").forEach(name -> groupRepository.save(new Group(name)));

        Group dev = groupRepository.findByName("Denver Dev");

        Event e = Event.builder().id((long) 1).title("Full Stack Reactive")
                .description("Reactive with Spring Boot + React")
                .date(Instant.parse("2018-12-12T18:00:00.000Z"))
                .build();

        dev.setEvents(Collections.singleton(e));

        groupRepository.save(dev);

        groupRepository.findAll().forEach(System.out :: println);
    }


}
