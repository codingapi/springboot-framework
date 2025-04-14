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

    public void createOrUpdate(UserCmd.UpdateRequest request){
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

    public void removeEntrust(long id) {
        User user = userRepository.getUserById(id);
        if(user!=null){
            user.removeEntrust();
            userRepository.save(user);
        }
    }

    public void createEntrust(UserCmd.EntrustRequest request){
        User user = userRepository.getUserById(request.getId());
        if(user!=null){
            User entrustOperator = userRepository.getUserById(request.getEntrustUserId());
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
