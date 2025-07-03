package com.ensao.hadrpredictapi.service;
import com.ensao.hadrpredictapi.service.EcoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class StartupRunner implements CommandLineRunner {

    @Autowired
    private EcoleService ecoleService;

    @Override
    public void run(String... args) {
        ecoleService.updateAllSchoolsWithMissingCoordinates();
    }
}
