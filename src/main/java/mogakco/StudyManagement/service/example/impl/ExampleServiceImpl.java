package mogakco.StudyManagement.service.example.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import mogakco.StudyManagement.service.common.LoggingService;
import mogakco.StudyManagement.service.example.ExampleService;

@Service
public class ExampleServiceImpl implements ExampleService {

    @Value("${jwt.expired.time}")
    private Long expiredTime;

    @Override
    public void exampleMethod(String var1, LoggingService lo) {

        if (true) {
            lo.setDBStart();
            // EX) Get the List of Post from DB
            // postRepository.findAll();
            lo.setDBEnd();

        }

        // EX) Other Service Logic
        int var2 = 1234;

        lo.setDBStart();
        // EX) Get the List of Study from DB
        // studyRepository.findAll();
        lo.setDBEnd();
    }

}
