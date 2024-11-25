package com.datmai.moviereservation.service;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.datmai.moviereservation.domain.Screen;
import com.datmai.moviereservation.repository.ScreenRepository;
import com.datmai.moviereservation.util.constant.ScreenName;

@Service
public class ScreenService {
    private final ScreenRepository screenRepository;

    public ScreenService(ScreenRepository screenRepository) {
        this.screenRepository = screenRepository;
    }

    public Screen fetchScreenById(long id) {
        Optional<Screen> screenOptional = this.screenRepository.findById(id);
        Screen currentScreen = screenOptional.isPresent() ? screenOptional.get() : null;
        return currentScreen;
    }

    public boolean isScreenExist(ScreenName name) {
        return this.screenRepository.existsByName(name);
    }

    public Screen createScreen(Screen screen) {
        if (!this.isScreenExist(screen.getName())) {
            return this.screenRepository.save(screen);
        }
        return null;
    }
}
