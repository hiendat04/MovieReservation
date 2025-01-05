package com.datmai.moviereservation.common.seeder;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.datmai.moviereservation.domain.Screen;
import com.datmai.moviereservation.repository.ScreenRepository;
import com.datmai.moviereservation.common.constant.ScreenFormat;
import com.datmai.moviereservation.common.constant.ScreenName;

@Component
public class ScreenSeeder implements CommandLineRunner {

    private final ScreenRepository screenRepository;

    public ScreenSeeder(ScreenRepository screenRepository) {
        this.screenRepository = screenRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        if (screenRepository.count() == 0) {
            Screen screenA = new Screen();
            screenA.setName(ScreenName.A);
            screenA.setFormat(ScreenFormat.TWO_D);
            screenA.setCreatedAt(Instant.now());
            screenA.setCreatedBy("Admin");

            Screen screenB = new Screen();
            screenB.setName(ScreenName.B);
            screenB.setFormat(ScreenFormat.THREE_D);
            screenB.setCreatedAt(Instant.now());
            screenB.setCreatedBy("Admin");

            Screen screenC = new Screen();
            screenC.setName(ScreenName.C);
            screenC.setFormat(ScreenFormat.IMAX);
            screenC.setCreatedAt(Instant.now());
            screenC.setCreatedBy("Admin");

            Screen screenD = new Screen();
            screenD.setName(ScreenName.D);
            screenD.setFormat(ScreenFormat.TWO_D);
            screenD.setCreatedAt(Instant.now());
            screenD.setCreatedBy("Admin");

            Screen screenE = new Screen();
            screenE.setName(ScreenName.E);
            screenE.setFormat(ScreenFormat.THREE_D);
            screenE.setCreatedAt(Instant.now());
            screenE.setCreatedBy("Admin");

            // Save all screens to the repository
            List<Screen> screens = Arrays.asList(screenA, screenB, screenC, screenD, screenE);
            screenRepository.saveAll(screens);
            System.out.println("Screen data initialized successfully!");
        }
    }
}
