package com.spx.inventory_service.utils.validator;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Slf4j
@Component
public class UpdateValidator {

    public void checkIfUpdateIsAllowed(String entityName, String currentValue, String newValue, Function<String, Boolean> repositoryMethod) {

        if (!currentValue.equalsIgnoreCase(newValue) && repositoryMethod.apply(newValue)) {

            log.error("{} already exists. Tried value: {}", entityName, newValue);
            throw new IllegalArgumentException(entityName + " already exists: " + newValue);


        }
    }

}