package com.codingapi.springboot.framework.servlet;


import com.codingapi.springboot.framework.exception.LocaleMessageException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Configuration
@ConditionalOnClass(name = "org.springframework.web.servlet.HandlerExceptionResolver")
public class BasicHandlerExceptionResolverConfiguration {

    @Bean
    public HandlerExceptionResolver servletExceptionHandler() {
        return new ServletExceptionHandler();
    }

    @Slf4j
    public static class ServletExceptionHandler implements HandlerExceptionResolver {

        private final static String DEFAULT_ERROR_CODE = "system.err";

        @Override
        public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
            MappingJackson2JsonView view = new MappingJackson2JsonView();
            ModelAndView mv = new ModelAndView(view);
            mv.addObject("success", false);
            if (ex instanceof LocaleMessageException) {
                LocaleMessageException localMessageException = (LocaleMessageException) ex;
                mv.addObject("errCode", localMessageException.getErrCode());
                mv.addObject("errMessage", localMessageException.getMessage());
                log.warn("controller errCode:{} exception:{}", localMessageException.getErrCode(), ex.getLocalizedMessage());
                return mv;
            }
            log.warn("controller exception:{}", ex.getLocalizedMessage(), ex);
            mv.addObject("errCode", DEFAULT_ERROR_CODE);
            mv.addObject("errMessage", ex.getMessage());

            return mv;
        }
    }
}
