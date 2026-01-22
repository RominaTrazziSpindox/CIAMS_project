package com.spx.inventory_management.utils.validator;

import com.spx.inventory_management.utils.TextNormalizer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Slf4j
@Component
public class CreateValidator {

    /*
       Using <Generics>:
       K = Generic identifier DTO Request type (raw)

        Using Function with parameters K (repositoryMethod)
        A function that has:
            - K value as input (a RequestDTO, in this case normalized)
            - Boolean as output.

        Using Function with two K parameters (normalizerMethod)
        A function that has:
            - K value as input (a RequestDTO, in this case raw)
            - K value as output (a RequestDTO, in this case normalized)

        The return type of the function is a RequestDTO (normalized).
     */


    /* Implemented example:

    OfficeRequestDTO normalizedDTO = OfficeRequestNormalizer.normalize(newOfficeDTO);

    if (officeRepository.existsByNameIgnoreCase(normalizedDTO.getOfficeName())) {
        throw new IllegalArgumentException("Office name already exists: " + normalizedDTO.getOfficeName());
    }

     */
    public <K> K checkIfEntityAlreadyExists(String entityName, K rawRequestDTO, Function<K, Boolean> repositoryMethod, Function<K, K> normalizerMethod) {

        // Step 1: Normalized the incoming name (only if it is a String type. entityRawValue -> rawString)
        K normalizedDTO = normalizerMethod.apply(rawRequestDTO);

        // Step 2: Check if the office name already exists
        if (repositoryMethod.apply(normalizedDTO)) {
            log.error("{} already exists. Tried dto: {}", entityName, normalizedDTO);
            throw new IllegalStateException(entityName + " already exists");
        }

        return normalizedDTO;
    }
}


