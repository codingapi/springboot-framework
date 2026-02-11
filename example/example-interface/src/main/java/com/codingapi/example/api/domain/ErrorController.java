package com.codingapi.example.api.domain;

import com.codingapi.springboot.framework.dto.response.Response;
import com.codingapi.springboot.framework.exception.LocaleMessageException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/open/error")
public class ErrorController {

    @GetMapping("/error1")
    public Response error1(){
        throw new LocaleMessageException("error1");
    }

    @GetMapping("/error2")
    public Response error2(){
        throw new LocaleMessageException("error2","error 2 message");
    }

    @GetMapping("/error3")
    public Response error3(){
        throw new LocaleMessageException("error3",new Object[]{"my is arg"});
    }
}
