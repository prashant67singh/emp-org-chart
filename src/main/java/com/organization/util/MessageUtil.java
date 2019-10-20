package com.organization.util;

import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.stereotype.Service;

import java.util.Locale;

@Service
public class MessageUtil implements MessageSourceAware {

    MessageSource messageSource;

    @Override
    public void setMessageSource(MessageSource messageSource) {
        this.messageSource= messageSource;
    }

    public String getMessage(String string){
        return  messageSource.getMessage(string,null, Locale.US);
    }
}
