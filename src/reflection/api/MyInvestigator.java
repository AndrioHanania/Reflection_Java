package reflection.api;

import java.lang.reflect.*;
import java.util.HashSet;
import java.util.Set;

public class MyInvestigator implements Investigator
{
    private Class<?> instance;
    private Object objectInstance;

    public MyInvestigator(){}

    @Override
    public void load(Object anInstanceOfSomething)
    {
        instance = anInstanceOfSomething.getClass();
        objectInstance = anInstanceOfSomething;
    }

    @Override
    public int getTotalNumberOfMethods()
    {
        return instance.getDeclaredMethods().length;
    }

    @Override
    public int getTotalNumberOfConstructors()
    {
        return  instance.getDeclaredConstructors().length;
    }

    @Override
    public int getTotalNumberOfFields()
    {
        return  instance.getDeclaredFields().length;
    }

    @Override
    public Set<String> getAllImplementedInterfaces()
    {
        Set<String> allImplementedInterfacesNames = new HashSet<>();
        Class<?>[] interfaces = instance.getInterfaces();
        for (Class<?> interfaceClass : interfaces)
        {
            allImplementedInterfacesNames.add(interfaceClass.getSimpleName());
        }
        return allImplementedInterfacesNames;
    }

    @Override
    public int getCountOfConstantFields()
    {
        int countOfConstantFields = 0;
        Field[] Fields = instance.getDeclaredFields(); //only from the given class
        for (Field field: Fields)
        {
            if( Modifier.isFinal(field.getModifiers()))
                countOfConstantFields++;
        }
        return  countOfConstantFields;
    }

    @Override
    public int getCountOfStaticMethods()
    {
        int CountOfStaticMethods=0;
        Method[] methods = instance.getDeclaredMethods(); //only from the given class
        for(Method method : methods)
        {
            if(Modifier.isStatic(method.getModifiers()))
                CountOfStaticMethods++;
        }
        return CountOfStaticMethods;
    }

    @Override
    public boolean isExtending()
    {
        return instance.getSuperclass() != null;
    }

    @Override
    public String getParentClassSimpleName()
    {
        if(!isExtending())
            return null;
        return instance.getSuperclass().getSimpleName();
    }

    @Override
    public boolean isParentClassAbstract()
    {
       return Modifier.isAbstract(this.getClass().getSuperclass().getModifiers());
    }

    @Override
    public Set<String> getNamesOfAllFieldsIncludingInheritanceChain()
    {
        Set<String> namesOfAllFields = new HashSet<>();
        Field[] classFields = instance.getDeclaredFields();
        Field[] superClassFields = instance.getSuperclass().getDeclaredFields();
        for (Field field: classFields)
        {
            namesOfAllFields.add(field.getName());
        }
        for (Field field: superClassFields)
        {
            namesOfAllFields.add(field.getName());
        }
        return namesOfAllFields;
    }

    @Override
    public int invokeMethodThatReturnsInt(String methodName, Object... args)
    {
        try
        {
            Method method = instance.getDeclaredMethod(methodName);
            method.setAccessible(true);
            return (int) method.invoke(objectInstance, args);
        }
        catch(InvocationTargetException | IllegalAccessException | NoSuchMethodException | IllegalArgumentException e) {
            return -1;
        }
    }

    @Override
    public Object createInstance(int numberOfArgs, Object... args)
    {
        Constructor<?>[] ctors = instance.getDeclaredConstructors(); //only from the given class
        for (Constructor<?> ctor: ctors)
        {
            if(ctor.getParameters().length == numberOfArgs)
            {
                try
                {
                    return ctor.newInstance(args);
                }
                catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
                    return null;
                }
            }
        }
        return null;
    }

    @Override
    public Object elevateMethodAndInvoke(String name, Class<?>[] parametersTypes, Object... args)
    {
        try
        {
            Method method = instance.getDeclaredMethod(name, parametersTypes);
            method.setAccessible(true);
            return method.invoke(objectInstance, args);
        }
        catch(InvocationTargetException | IllegalAccessException | NoSuchMethodException | IllegalArgumentException e) {
            return null;
        }
    }

    @Override
    public String getInheritanceChain(String delimiter)
    {
        StringBuilder inheritanceChain = new StringBuilder(delimiter + instance.getSimpleName());
        Class<?> superr = instance.getSuperclass();
        if(isExtending())
        {
            do
            {
                inheritanceChain.insert(0, delimiter + superr.getSimpleName());
                superr = superr.getSuperclass();
            } while (superr.getSuperclass() != null);
        }

        inheritanceChain.insert(0, "Object");
        return inheritanceChain.toString();
    }
}