package com.saga.opencms.util

import javax.persistence.Entity
import javax.persistence.Id
import java.lang.reflect.Field
import java.lang.reflect.Method

public class SgReflection {

    public static final List<String> OBJECT_PRIMITIVES = Arrays.asList(
            "java.lang.String", "java.lang.Boolean", "java.lang.Byte",
            "java.lang.Character", "java.lang.Double", "java.lang.Float",
            "java.lang.Integer", "java.lang.Long", "java.lang.Number",
            "java.lang.Short", "java.util.Currency", "java.util.Date",
            "java.sql.Date", "java.sql.Time", "java.sql.Timestamp");

    public static boolean isEntity(final Class<?> entityClass) {
        if (entityClass == null)
            return false;
        return entityClass.isAnnotationPresent(Entity.class) ? true : false;
    }

    public static Object getIdentity(final Object entity) {
        if (entity == null) {
            return null;
        }
        try {
            String identityName = getIdentityPropertyName(entity.getClass());
            return getFieldValue(identityName, entity);
        } catch (Exception e) {
            return null;
        }
    }

    public static String getEntityName(final Class<?> entityClass) {
        String entityName = null;
        if (entityClass != null) {
            Entity entity = entityClass.getAnnotation(Entity.class);
            if (entity == null) {
                entityName = entityClass.getSimpleName();
            } else {
                entityName = entity.name();
                if (entityName == null || entityName.length() < 1) {
                    entityName = entityClass.getSimpleName();
                }
            }
        }
        return entityName;
    }

    public static String getIdentityPropertyName(final Class<?> clazz) {
        String idPropertyName = searchFieldsForId(clazz);
        if (idPropertyName == null) {
            idPropertyName = searchMethodsForId(clazz);
        }
        return idPropertyName;
    }

    public static boolean hasIdentity(final Object entity) {
        Object id = getIdentity(entity);
        if (id == null)
            return false;
        return id instanceof Number && ((Number) id).longValue() >= 0 ? true
                : false;
    }

    private static String searchFieldsForId(final Class<?> clazz) {
        String pkName = null;
        for (Field field : clazz.getDeclaredFields()) {
            Id id = field.getAnnotation(Id.class);
            if (id != null) {
                pkName = field.getName();
                break;
            }
        }
        if (pkName == null && clazz.getSuperclass() != null) {
            pkName = searchFieldsForId((Class<?>) clazz.getSuperclass());
        }
        return pkName;
    }


    private static String searchMethodsForId(final Class<?> clazz) {
        String pkName = null;
        for (Method method : clazz.getDeclaredMethods()) {
            Id id = method.getAnnotation(Id.class);
            if (id != null) {
                pkName = method.getName().substring(4);
                pkName = method.getName().substring(3, 4).toLowerCase()
                +pkName;
                break;
            }
        }
        if (pkName == null && clazz.getSuperclass() != null) {
            pkName = searchMethodsForId(clazz.getSuperclass());
        }
        return pkName;
    }

    private static Object getFieldValue(Field field, Object target)
            throws Exception {
        boolean accessible = field.isAccessible();
        try {
            field.setAccessible(true);
            return field.get(target);
        } catch (IllegalArgumentException iae) {
            String message = "No se ha podido obtener el valor de la propiedad mediante reflexi�n: "
            +field.getName() + " en: " + target.getClass().getName();
            throw new IllegalArgumentException(message, iae);
        } finally {
            field.setAccessible(accessible);
        }
    }

    private static Object getFieldValue(String field, Object target)
            throws Exception {
        try {
            Field declaredField = target.getClass().getDeclaredField(field);
            return getFieldValue(declaredField, target);
        } catch (Exception e) {
            String message = "No se ha podido obtener el valor de la propiedad mediante reflexi�n: "
            +field + " en: " + target.getClass().getName();
            throw new IllegalArgumentException(message, e);
        }
    }
}
