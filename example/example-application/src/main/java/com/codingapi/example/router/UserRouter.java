package com.codingapi.example.router;

import com.codingapi.example.domain.User;
import com.codingapi.example.gateway.PasswordEncoder;
import com.codingapi.example.pojo.cmd.UserCmd;
import com.codingapi.example.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserRouter {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;


    public void save(UserCmd.UpdateRequest request){
        if(request.getId() ==0 ){
            User user = new User();
            user.setName(request.getName());
            user.setPassword(request.getPassword());
            user.setUsername(request.getUsername());
            user.setFlowManager(request.isFlowManager());
            user.setCreateTime(System.currentTimeMillis());
            user.encodePassword(passwordEncoder);
            userRepository.save(user);
        }else {
            User user = userRepository.getUserById(request.getId());
            user.setName(request.getName());
            user.setPassword(request.getPassword());
            user.setUsername(request.getUsername());
            user.setFlowManager(request.isFlowManager());
            user.encodePassword(passwordEncoder);
            userRepository.save(user);
        }
    }


    public void entrust(UserCmd.EntrustRequest request){
        User user = userRepository.getUserById(request.getId());
        User entrustOperator = userRepository.getUserById(request.getEntrustUserId());
        user.setEntrustOperator(entrustOperator);
        userRepository.save(user);
    }

    public void changeManager(Long id) {
        User user = userRepository.getUserById(id);
        user.setFlowManager(!user.isFlowManager());
        userRepository.save(user);
    }


    public void removeEntrust(Long id) {
        User user = userRepository.getUserById(id);
        user.setEntrustOperator(null);
        userRepository.save(user);
    }

    public void remove(Long id) {
        userRepository.delete(id);
    }
}
