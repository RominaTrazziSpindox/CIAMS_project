package com.spx.inventory_management.utils.validator;

import java.util.Optional;
import java.util.function.Function;

public interface ServiceUtils {

    public <T, K> T checkIfEntityIsFound(String entityName, K entityRawValue, Function<K, Optional<T>> repositoryMethod);

    public <K> K checkIfEntityAlreadyExists(String entityName, K rawRequestDTO, Function<K, Boolean> repositoryMethod, Function<K, K> normalizerMethod);

    public void checkIfUpdateIsAllowed(String entityName,String currentValue, String newValue, Function<String, Boolean> repositoryMethod);

}
