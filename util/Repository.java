package util;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;

import model.annotation.Id;

public class Repository<T> {

    private final HashMap<String, T> data = new HashMap<>();

    // Save one object using its @Id field as the key
    public T save(T entity) {
        if (entity == null) {
            throw new IllegalArgumentException(
                    "Entity cannot be null.");
        }

        String id = extractId(entity);
        data.put(id, entity);

        return entity;
    }

    // Find an object using its ID
    public T findById(String id) {
        return data.get(id);
    }

    // Return all stored objects
    public ArrayList<T> findAll() {
        return new ArrayList<>(data.values());
    }

    // Delete an object using its ID
    public boolean delete(String id) {
        return data.remove(id) != null;
    }

    // Save multiple objects
    @SafeVarargs
    public final void saveAll(T... items) {
        for (T item : items) {
            save(item);
        }
    }

    // Find the field marked with @Id and read its value
    private String extractId(T entity) {
        Class<?> type = entity.getClass();

        while (type != null) {
            for (Field field : type.getDeclaredFields()) {
                if (field.isAnnotationPresent(Id.class)) {
                    try {
                        field.setAccessible(true);
                        Object value = field.get(entity);

                        if (value == null) {
                            throw new IllegalArgumentException(
                                    "@Id field cannot be null.");
                        }

                        return value.toString();

                    } catch (IllegalAccessException e) {
                        throw new IllegalStateException(
                                "Could not read the @Id field.", e);
                    }
                }
            }

            type = type.getSuperclass();
        }

        throw new IllegalArgumentException(
                "No field marked with @Id found in "
                        + entity.getClass().getName());
    }

    // Generic method with a bounded type parameter
    public static <N extends Number> double average(N[] values) {
        if (values == null || values.length == 0) {
            throw new IllegalArgumentException(
                    "Array cannot be null or empty.");
        }

        double total = 0;

        for (N value : values) {
            if (value == null) {
                throw new IllegalArgumentException(
                        "Array cannot contain null values.");
            }

            total += value.doubleValue();
        }

        return total / values.length;
    }
}

