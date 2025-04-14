package com.codingapi.example.app.cmd.domain.router;

import com.codingapi.example.app.cmd.domain.pojo.UserCmd;
import com.codingapi.example.domain.user.entity.User;
import com.codingapi.example.domain.user.gateway.PasswordEncoder;
import com.codingapi.example.domain.user.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class UserRouter {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public void save(UserCmd.UpdateRequest request){
        if(request.hasId()){
            User user = userRepository.getUserById(request.getId());
            user.setName(request.getName());
            user.setPassword(request.getPassword());
            user.setFlowManager(request.isFlowManager());
            user.encodePassword(passwordEncoder);
            userRepository.save(user);
        }else {
            User user = new User();
            user.setName(request.getName());
            user.setPassword(request.getPassword());
            user.setFlowManager(request.isFlowManager());
            user.encodePassword(passwordEncoder);
            userRepository.save(user);
        }
    }
}
