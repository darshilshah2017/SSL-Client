package test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MainAppllication extends SpringApplication{

    public static void main(String[] args) {
        SpringApplication.run(MainAppllication.class,args);
    }
}
