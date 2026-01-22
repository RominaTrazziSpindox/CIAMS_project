package com.spx.inventory_management.utils.validator;

import com.spx.inventory_management.utils.TextNormalizer;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.function.Function;

@Slf4j
@Component
public class ReadValidator {

    /* Using <Generics>:
       T = Generic entity Type ex. Office, Asset, SoftwareLicence
       K = Generic identifier type, can be a String, an int, a Long

        Using Optional:
        Optional T means that the Entity type could or could not returned.
        If there is no T-Entity is returns null

        Using Function with parameters K and Optional T
        A function that has:
            - K value as input
            - Optional<T> as output.

        This function is the repository function method given by JPA.
        We use lambda expression :: to put a method as a function parameter.

        The return type is an entity T.
     */

     /* Implemented example:

    String normalizedName = TextNormalizer.normalizeKey(name);

    Office office = officeRepository.findByNameIgnoreCase(normalizedName).orElseThrow(() -> {
        log.error("Office not found. Name: {}", normalizedName);
        return new EntityNotFoundException("Office not found"); // Throw 404 HTTP Status code
    }); */

    public <T, K> T checkIfEntityIsFound(String entityName, K entityRawValue, Function<K, Optional<T>> repositoryMethod) {

        final K valueToSearch;

        // Step 1: Normalized the incoming name (only if it is a String type. entityRawValue -> rawString)
        if (entityRawValue instanceof String rawString) {
            valueToSearch = (K) TextNormalizer.normalizeKey(rawString);
        } else {
            // If the valueToSearch is not a String skip controls
            valueToSearch = entityRawValue;
        }

        // Step 2:
        return repositoryMethod.apply(valueToSearch).orElseThrow(() -> {
            log.error("{} not found. value={}", entityName, valueToSearch);
            return new EntityNotFoundException(entityName + " not found: " + valueToSearch);    // Throw 404 HTTP Status code
        });

    }

}