package mogakco.StudyManagement.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class helloController {
    
    @GetMapping("/api/hello")
    public String test(){
        return "Hello~!~~!~!!!";
    }
    
}
