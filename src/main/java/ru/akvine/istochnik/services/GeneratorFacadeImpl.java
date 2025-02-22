package ru.akvine.istochnik.services;

import org.springframework.stereotype.Service;
import ru.akvine.istochnik.services.dto.GenerateData;

import java.util.List;

@Service
public class GeneratorFacadeImpl implements GeneratorFacade {


    @Override
    public List<?> generate(GenerateData generateData) {

        
        return List.of();
    }
}
