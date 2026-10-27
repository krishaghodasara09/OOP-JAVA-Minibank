package util;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import model.annotation.Positive;
import model.annotation.MaxLength;

public class AnnotationValidator {

    public static String[] validate(Object obj) {
        List<String> errors = new ArrayList<>();

        if (obj == null) {
            return new String[]{"Object cannot be null."};
        }

        Class<?> currentClass = obj.getClass();

        while (currentClass != null
                && currentClass != Object.class) {

            Field[] fields = currentClass.getDeclaredFields();

            for (Field field : fields) {
                try {
                    field.setAccessible(true);
                    Object value = field.get(obj);

                    if (field.isAnnotationPresent(Positive.class)) {
                        Positive annotation =
                                field.getAnnotation(Positive.class);

                        if (value instanceof Number number
                                && number.doubleValue() <= 0) {
                            errors.add(field.getName() + " "
                                    + annotation.message());
                        }
                    }

                    if (field.isAnnotationPresent(MaxLength.class)) {
                        MaxLength annotation =
                                field.getAnnotation(MaxLength.class);

                        if (value instanceof String text
                                && text.length() > annotation.value()) {
                            errors.add(field.getName()
                                    + " length must be at most "
                                    + annotation.value() + " characters.");
                        }
                    }

                } catch (IllegalAccessException e) {
                    errors.add("Could not validate field: "
                            + field.getName());
                }
            }

            currentClass = currentClass.getSuperclass();
        }

        return errors.toArray(new String[0]);
    }
}

