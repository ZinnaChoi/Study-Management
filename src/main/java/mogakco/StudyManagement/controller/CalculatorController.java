package mogakco.StudyManagement.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import mogakco.StudyManagement.service.CalculatorService;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/calculator")
public class CalculatorController {
    private final CalculatorService calculatorService;

    public CalculatorController(CalculatorService calculatorService) {
        this.calculatorService = calculatorService;
    }

    @GetMapping("/add/{a}/{b}")
    public int add(@PathVariable(name = "a") int a, @PathVariable(name = "b") int b) {
        return calculatorService.add(a, b);
    }

    @GetMapping("/divide/{a}/{b}")
    public ResponseEntity<?> divide(@PathVariable(name = "a") int a, @PathVariable(name = "b") int b) {
        if (b == 0) {
            return ResponseEntity.badRequest().body("Division by zero is not allowed");
        }
        return ResponseEntity.ok(calculatorService.divide(a, b));
    }
}
