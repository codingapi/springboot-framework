package com.codingapi.springboot.fast;

import com.codingapi.springboot.fast.entity.Demo;
import com.codingapi.springboot.fast.entity.Profile;
import com.codingapi.springboot.fast.entity.User;
import com.codingapi.springboot.fast.jpa.map.QueryColumnsContext;
import com.codingapi.springboot.fast.jpa.map.MapViewResult;
import com.codingapi.springboot.fast.repository.DemoRepository;
import com.codingapi.springboot.fast.repository.ProfileRepository;
import com.codingapi.springboot.fast.repository.UserRepository;
import com.codingapi.springboot.framework.dto.request.PageRequest;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
@SpringBootTest
public class UserRepositoryTest {

    @Autowired
    private DemoRepository demoRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProfileRepository profileRepository;


    @Test
    void test1() {

        Demo demo = new Demo();
        demo.setName("123");
        demoRepository.save(demo);


        Profile profile = new Profile();
        profile.setDemo(demo);
        profile.setName("123");
        profileRepository.save(profile);


        User user = new User();
        user.setName("li");
        user.setProfile(profile);
        userRepository.save(user);

        assertTrue(demo.getId() > 0);
        assertTrue(user.getId() > 0);

        PageRequest request = new PageRequest();
        request.addFilter("profile.demo.id", 1);
        Page<User> page = userRepository.pageRequest(request);
        System.out.println(page.getContent());

    }


    @Test
    void testMapQuery() {

        Demo demo = new Demo();
        demo.setName("123");
        demoRepository.save(demo);


        Profile profile = new Profile();
        profile.setDemo(demo);
        profile.setName("123");
        profileRepository.save(profile);


        User user = new User();
        user.setName("li");
        user.setProfile(profile);
        userRepository.save(user);

        assertTrue(demo.getId() > 0);
        assertTrue(user.getId() > 0);

        PageRequest request = new PageRequest();
        request.addFilter("profile.demo.id", 1);

        String querySql = "from User u where u.profile.demo.id = ?1";
        String countSQL = "from User u where u.profile.demo.id = ?1";
        Page<MapViewResult> page =
                userRepository.dynamicMapPageQuery(
                        QueryColumnsContext.build("u.id as iii","u.name"),
                        querySql,
                        countSQL, request, 1);
        List<MapViewResult> queryMaps = page.getContent();
        System.out.println(queryMaps);

        List<MapViewResult> list =
                userRepository.dynamicMapListQuery(
                        QueryColumnsContext.build("u.id as iii","u.name"),
                        querySql,
                          1);
        System.out.println(list);

    }


    @Test
    void test2() {

        Demo demo = new Demo();
        demo.setName("123");
        demoRepository.save(demo);


        Profile profile = new Profile();
        profile.setDemo(demo);
        profile.setName("123");
        profileRepository.save(profile);


        User user = new User();
        user.setName("li");
        user.setProfile(profile);
        userRepository.save(user);

        assertTrue(demo.getId() > 0);
        assertTrue(user.getId() > 0);

        PageRequest request = new PageRequest();
        request.addFilter("name", "li");
        Page<User> page = userRepository.pageRequest(request);
        System.out.println(page.getContent());

    }

}
