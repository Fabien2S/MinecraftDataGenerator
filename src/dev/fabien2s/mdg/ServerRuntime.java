package dev.fabien2s.mdg;

import dev.fabien2s.mdg.mappings.MappingClass;
import dev.fabien2s.mdg.mappings.MappingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;

public class ServerRuntime {

    private static final Logger LOGGER = LogManager.getLogger(ServerRuntime.class);

    private final MappingContext mappingContext;
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

    public Object invokeMethod(String className, String methodName, Object... args) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        final MappingClass mappingClass = this.mappingContext.remap(className);

        final String remapClassName = mappingClass.getObfuscated();
        final Class<?> clazz = Class.forName(remapClassName, true, this.classLoader);

        final String remapMethodName = mappingClass.remap(methodName + "()");
        final Method method = clazz.getDeclaredMethod(remapMethodName);

        return method.invoke(null, args);
    }

    public Object getField(String className, String fieldName) throws ClassNotFoundException, NoSuchFieldException, IllegalAccessException {
        final MappingClass mappingClass = this.mappingContext.remap(className);

        final String remapClassName = mappingClass.getObfuscated();
        final Class<?> clazz = Class.forName(remapClassName, true, this.classLoader);

        final String remapFieldName = mappingClass.remap(fieldName);
        final Field field = clazz.getDeclaredField(remapFieldName);
        return field.get(null);
    }
}

