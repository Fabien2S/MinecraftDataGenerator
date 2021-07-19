package dev.fabien2s.mdg;

import dev.fabien2s.mdg.mapping.MappingClass;
import dev.fabien2s.mdg.mapping.MappingContext;
import dev.fabien2s.mdg.mapping.MappingMethod;
import dev.fabien2s.mdg.mapping.exceptions.MappingException;
import dev.fabien2s.mdg.mapping.exceptions.MappingNotFoundException;
import lombok.Getter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;

public class ServerRuntime {

    private static final Logger LOGGER = LogManager.getLogger(ServerRuntime.class);

    @Getter private final MappingContext mappingContext;

    private final ClassLoader classLoader;

    public ServerRuntime(MappingContext mappingContext, URL jarFile) {
        this.mappingContext = mappingContext;
        this.classLoader = URLClassLoader.newInstance(new URL[]{jarFile});
    }

    public void exportServerData() throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        LOGGER.info("Exporting server data");
        final Class<?> mainDataClass = Class.forName("net.minecraft.data.Main", true, this.classLoader);
        final Method mainDataMethod = mainDataClass.getDeclaredMethod("main", String[].class);
        mainDataMethod.invoke(null, (Object) new String[]{"-all"});
    }

    public Class<?> getClass(String className) throws ClassNotFoundException, MappingNotFoundException {
        final MappingClass mappingClass = this.mappingContext.remapClass(className);
        final String remapClassName = mappingClass.getObfuscated();
        return Class.forName(remapClassName, true, this.classLoader);
    }

    public Object invokeMethod(String className, String methodName, Object... args) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException, MappingException {
        final MappingClass mappingClass = this.mappingContext.remapClass(className);

        final String remapClassName = mappingClass.getObfuscated();
        final Class<?> clazz = Class.forName(remapClassName, true, this.classLoader);

        final MappingMethod mappingMethod = mappingClass.remapMethod(methodName);
        final Method method = clazz.getDeclaredMethod(mappingMethod.getObfuscated(), mappingMethod.getParametersClasses(this));

        method.setAccessible(true);
        return method.invoke(null, args);
    }

    public Object getField(String className, String fieldName) throws ClassNotFoundException, NoSuchFieldException, IllegalAccessException, MappingNotFoundException {
        final MappingClass mappingClass = this.mappingContext.remapClass(className);

        final String remapClassName = mappingClass.getObfuscated();
        final Class<?> clazz = Class.forName(remapClassName, true, this.classLoader);

        final String remapFieldName = mappingClass.remapField(fieldName);
        final Field field = clazz.getDeclaredField(remapFieldName);

        field.setAccessible(true);
        return field.get(null);
    }

    public Object invokeMethod(Object obj, Class<?> objClass, String methodName, Object... args) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, ClassNotFoundException, MappingException {
        final MappingClass mappingClass = this.mappingContext.remapClass(objClass);

        final MappingMethod mappingMethod = mappingClass.remapMethod(methodName);
        final Method method = objClass.getDeclaredMethod(mappingMethod.getObfuscated(), mappingMethod.getParametersClasses(this));

        method.setAccessible(true);
        return method.invoke(obj, args);
    }

    public Object getField(Object obj, Class<?> objClass, String fieldName) throws NoSuchFieldException, IllegalAccessException, MappingNotFoundException {
        final MappingClass mappingClass = this.mappingContext.remapClass(objClass);

        final String remapFieldName = mappingClass.remapField(fieldName);
        final Field field = objClass.getDeclaredField(remapFieldName);

        field.setAccessible(true);
        return field.get(obj);
    }
}

