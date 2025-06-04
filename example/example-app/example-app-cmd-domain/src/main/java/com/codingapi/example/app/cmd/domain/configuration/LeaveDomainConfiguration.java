package com.codingapi.example.app.cmd.domain.configuration;

import com.codingapi.example.domain.leave.repository.LeaveRepository;
import com.codingapi.example.domain.leave.service.LeaveService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LeaveDomainConfiguration {

    @Bean
    public LeaveService leaveService(LeaveRepository leaveRepository){
        return new LeaveService(leaveRepository);
    }
}
