package validation;

import java.lang.reflect.Field;

import javax.xml.bind.ValidationException;

import annotations.NotBlank;

public class Validator {

	public static void validateNotBlankFields(Object entity) throws ValidationException {
		Class<?> clazz = entity.getClass();
		for (Field field : clazz.getDeclaredFields()) {
			if (field.isAnnotationPresent(NotBlank.class)) {
				field.setAccessible(true);
				try {
					Object value = field.get(entity);
					if (value == null) {
						NotBlank ann = field.getAnnotation(NotBlank.class);
						throw new ValidationException(ann.message());
					}
					if (value instanceof String) {
						String s = (String) value;
						if (s.trim().isEmpty()) {
							NotBlank ann = field.getAnnotation(NotBlank.class);
							throw new ValidationException(ann.message());
						}
					}

				} catch (IllegalAccessException e) {
					throw new ValidationException("Erro ao campo " + field.getName(), e);

				}

			}

		}

	}

}
