package com.codingapi.springboot.fast;

import com.codingapi.springboot.fast.entity.Menu;
import com.codingapi.springboot.fast.repository.MenuRepository;
import com.codingapi.springboot.framework.dto.request.PageRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;

import java.util.Arrays;
import java.util.List;

@SpringBootTest
public class MenuRepositoryTest {

    @Autowired
    private MenuRepository menuRepository;

    @Test
    void test() {

        Menu parent = new Menu();
        parent.setName("parent");
        parent = menuRepository.save(parent);


        Menu menu = new Menu();
        menu.setName("test");
        menu.setParent(parent);
        menuRepository.save(menu);


        PageRequest request = PageRequest.of(0, 10);
        request.addFilter("parent.id", 1);
        Page<Menu> page = menuRepository.pageRequest(request);
        System.out.println(page.getTotalElements());
        System.out.println(page.getContent());
    }


    @Test
    void test1() {

        Menu parent = new Menu();
        parent.setName("parent");
        parent = menuRepository.save(parent);


        Menu menu = new Menu();
        menu.setName("test");
        menu.setParent(parent);
        menuRepository.save(menu);


        List<Menu> menuList = menuRepository.dynamicListQuery("from Menu m where m.id in (?1)", Arrays.asList(1, 2));
        System.out.println(menuList);
    }
}

