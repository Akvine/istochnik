package ru.akvine.istochnik.services.generators.custom.uuid;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.akvine.istochnik.services.generators.Config;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.random.RandomGenerator;

@Service
@RequiredArgsConstructor
public class UuidGeneratorService {

    public List<String> generate(Config config) {
        List<String> generatedValues = new ArrayList<>();
        if (config.getSeed() == null) {
            for (int i = 0; i < config.getSize(); ++i) {
                generatedValues.add(UUID.randomUUID().toString());
            }
        } else {
            RandomGenerator random = config.getRandomGenerator();
            for (int i = 0; i < config.getSize(); ++i) {
                byte[] randomBytes = new byte[16];
                random.nextBytes(randomBytes);

                // Установка версии 4 и варианта
                randomBytes[6] &= 0x0f;  // очищаем версию
                randomBytes[6] |= 0x40;  // устанавливаем версию 4
                randomBytes[8] &= 0x3f;  // очищаем вариант
                randomBytes[8] |= 0x80;  // устанавливаем вариант RFC 4122

                long msb = 0;
                long lsb = 0;
                for (int k = 0; k < 8; k++) {
                    msb = (msb << 8) | (randomBytes[k] & 0xff);
                }

                for (int k = 8; k < 16; k++) {
                    lsb = (lsb << 8) | (randomBytes[k] & 0xff);
                }

                generatedValues.add(new UUID(msb, lsb).toString());
            }
        }

        return generatedValues;
    }
}
