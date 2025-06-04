package com.codingapi.example.domain.user.service;

import com.codingapi.example.domain.user.entity.User;
import com.codingapi.example.domain.user.entity.UserMetric;
import com.codingapi.example.domain.user.gateway.PasswordEncoder;
import com.codingapi.example.domain.user.repository.UserRepository;
import lombok.AllArgsConstructor;

import static com.codingapi.example.domain.user.entity.User.USER_ADMIN_USERNAME;

@AllArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public void initAdmin() {
        User admin = userRepository.getUserByUsername(USER_ADMIN_USERNAME);
        if (admin == null) {
            admin = User.admin(passwordEncoder);
            userRepository.save(admin);
        }
    }

    public void create(UserMetric userMetric,boolean isFlowManager) {
        User user = new User();
        user.setFlowManager(isFlowManager);
        user.setUserMetric(userMetric);
        user.encodePassword(passwordEncoder);
        userRepository.save(user);
    }


    public void update(long id, UserMetric metric,boolean isFlowManager) {
        User user = userRepository.getUserById(id);
        user.setFlowManager(isFlowManager);
        user.setUserMetric(metric);
        user.encodePassword(passwordEncoder);
        userRepository.save(user);
    }

    public void removeEntrust(long id) {
        User user = userRepository.getUserById(id);
        if(user!=null){
            user.removeEntrust();
            userRepository.save(user);
        }
    }

    public void createEntrust(long userId,long entrustId) {
        User user = userRepository.getUserById(userId);
        if(user!=null){
            User entrustOperator = userRepository.getUserById(entrustId);
            user.setEntrustOperator(entrustOperator);
            userRepository.save(user);
        }
    }

    public void changeManager(long id){
        User user = userRepository.getUserById(id);
        if(user!=null){
            user.changeManager();
            userRepository.save(user);
        }
    }

    public void removeUser(long id){
        User user = userRepository.getUserById(id);
        if(user!=null){
            userRepository.delete(id);
        }
    }
}
