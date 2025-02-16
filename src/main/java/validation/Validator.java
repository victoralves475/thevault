package validation;

import annotations.NotBlank;
import annotations.NotNull;
import annotations.Min;
import annotations.Max;
import annotations.Size;
import validation.exceptions.ValidationException;

import java.lang.reflect.Field;

public class Validator {

	/**
	 * Método principal para validar a entidade contra todas as anotações suportadas.
	 */
	public static void validateEntity(Object entity) throws ValidationException {
		Class<?> clazz = entity.getClass();
		for (Field field : clazz.getDeclaredFields()) {
			field.setAccessible(true);

			// 1. NotBlank
			if (field.isAnnotationPresent(NotBlank.class)) {
				checkNotBlank(field, entity);
			}

			// 2. NotNull
			if (field.isAnnotationPresent(NotNull.class)) {
				checkNotNull(field, entity);
			}

			// 3. Min
			if (field.isAnnotationPresent(Min.class)) {
				checkMin(field, entity);
			}

			// 4. Max
			if (field.isAnnotationPresent(Max.class)) {
				checkMax(field, entity);
			}

			// 5. Size
			if (field.isAnnotationPresent(Size.class)) {
				checkSize(field, entity);
			}
		}
	}

	/**
	 * Se você quiser manter compatibilidade com o método antigo,
	 * pode apenas chamá-lo no validateEntity.
	 */
	public static void validateNotBlankFields(Object entity) throws ValidationException {
		validateEntity(entity);
	}

	// =============== Métodos Internos de Checagem ================

	private static Object getFieldValue(Field field, Object entity) throws ValidationException {
		try {
			return field.get(entity);
		} catch (IllegalAccessException e) {
			throw new ValidationException("Erro ao acessar o campo " + field.getName(), e);
		}
	}

	private static void checkNotBlank(Field field, Object entity) throws ValidationException {
		Object value = getFieldValue(field, entity);
		NotBlank ann = field.getAnnotation(NotBlank.class);

		if (value == null) {
			throw new ValidationException(ann.message());
		}
		if (value instanceof String) {
			String str = (String) value;
			if (str.trim().isEmpty()) {
				throw new ValidationException(ann.message());
			}
		}
	}

	private static void checkNotNull(Field field, Object entity) throws ValidationException {
		Object value = getFieldValue(field, entity);
		NotNull ann = field.getAnnotation(NotNull.class);

		if (value == null) {
			throw new ValidationException(ann.message());
		}
	}

	private static void checkMin(Field field, Object entity) throws ValidationException {
		Object value = getFieldValue(field, entity);
		if (value == null) return; // Se permitir nulo

		Min ann = field.getAnnotation(Min.class);
		double minVal = ann.value();

		if (value instanceof Number) {
			double numericVal = ((Number) value).doubleValue();
			if (numericVal < minVal) {
				throw new ValidationException(ann.message());
			}
		}
	}

	private static void checkMax(Field field, Object entity) throws ValidationException {
		Object value = getFieldValue(field, entity);
		if (value == null) return; // se permitir nulo

		Max ann = field.getAnnotation(Max.class);
		double maxVal = ann.value();

		if (value instanceof Number) {
			double numericVal = ((Number) value).doubleValue();
			if (numericVal > maxVal) {
				throw new ValidationException(ann.message());
			}
		}
	}

	private static void checkSize(Field field, Object entity) throws ValidationException {
		Object value = getFieldValue(field, entity);
		if (value == null) return;

		Size ann = field.getAnnotation(Size.class);
		int min = ann.min();
		int max = ann.max();

		if (value instanceof String) {
			String str = (String) value;
			int length = str.length();
			if (length < min || length > max) {
				throw new ValidationException(ann.message());
			}
		}

	}

}
