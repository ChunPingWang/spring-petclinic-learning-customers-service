package org.springframework.samples.petclinic.customers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.samples.petclinic.customers.model.Owner;
import org.springframework.samples.petclinic.customers.model.Pet;
import org.springframework.samples.petclinic.customers.model.PetType;
import org.springframework.samples.petclinic.customers.repository.OwnerRepository;
import org.springframework.samples.petclinic.customers.repository.PetTypeRepository;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Slf4j
@Component
@RequiredArgsConstructor
@Profile("!test")
public class DataInitializer implements CommandLineRunner {

    private final OwnerRepository ownerRepository;
    private final PetTypeRepository petTypeRepository;

    @Override
    public void run(String... args) {
        if (ownerRepository.count() > 0) {
            log.info("Database already initialized, skipping...");
            return;
        }

        log.info("Initializing database with sample data...");

        // Create pet types
        PetType cat = petTypeRepository.save(new PetType("cat"));
        PetType dog = petTypeRepository.save(new PetType("dog"));
        PetType lizard = petTypeRepository.save(new PetType("lizard"));
        PetType snake = petTypeRepository.save(new PetType("snake"));
        PetType bird = petTypeRepository.save(new PetType("bird"));
        PetType hamster = petTypeRepository.save(new PetType("hamster"));

        // Create owner 1: George Franklin
        Owner george = new Owner("George", "Franklin");
        george.setAddress("110 W. Liberty St.");
        george.setCity("Madison");
        george.setTelephone("6085551023");

        Pet leo = new Pet("Leo", LocalDate.of(2020, 5, 10), dog);
        Pet basil = new Pet("Basil", LocalDate.of(2019, 8, 15), cat);
        george.addPet(leo);
        george.addPet(basil);
        ownerRepository.save(george);

        // Create owner 2: Betty Davis
        Owner betty = new Owner("Betty", "Davis");
        betty.setAddress("638 Cardinal Ave.");
        betty.setCity("Sun Prairie");
        betty.setTelephone("6085551749");

        Pet rosy = new Pet("Rosy", LocalDate.of(2021, 3, 20), dog);
        betty.addPet(rosy);
        ownerRepository.save(betty);

        // Create owner 3: Eduardo Rodriquez
        Owner eduardo = new Owner("Eduardo", "Rodriquez");
        eduardo.setAddress("2693 Commerce St.");
        eduardo.setCity("McFarland");
        eduardo.setTelephone("6085558763");

        Pet jewel = new Pet("Jewel", LocalDate.of(2022, 1, 5), lizard);
        eduardo.addPet(jewel);
        ownerRepository.save(eduardo);

        log.info("Database initialization completed. Created {} owners.", ownerRepository.count());
    }
}
