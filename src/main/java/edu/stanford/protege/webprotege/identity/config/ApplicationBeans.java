package edu.stanford.protege.webprotege.identity.config;


import edu.stanford.protege.webprotege.identity.project.GetAllOwlClassesRequest;
import edu.stanford.protege.webprotege.identity.project.GetAllOwlClassesResponse;
import edu.stanford.protege.webprotege.ipc.CommandExecutor;
import edu.stanford.protege.webprotege.ipc.impl.CommandExecutorImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.locks.*;

@Configuration
public class ApplicationBeans {

    @Bean
    CommandExecutor<GetAllOwlClassesRequest, GetAllOwlClassesResponse> executorForPostCoordination() {
        return new CommandExecutorImpl<>(GetAllOwlClassesResponse.class);
    }

    @Bean
    public ReadWriteLock readWriteLock() {
        return new ReentrantReadWriteLock(true);
    }
}
