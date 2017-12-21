package com.raphaelvigee.sally.Router;

import com.raphaelvigee.sally.Server.Method;

public class Route
{
    private Method method;

    private Path path;

    private ActionWrapperInterface handler;

    private String name;

    public Route(Method method, String pathDeclaration, ActionWrapperInterface handler)
    {
        this.method = method;
        this.path = new Path(pathDeclaration);
        this.handler = handler;
    }

    public Route(String name, Method method, String pathDeclaration, ActionWrapperInterface handler)
    {
        this.method = method;
        this.path = new Path(pathDeclaration);
        this.handler = handler;
        this.name = name;
    }

    public Method getMethod()
    {
        return method;
    }

    public Path getPath()
    {
        return path;
    }

    public ActionWrapperInterface getHandler()
    {
        return handler;
    }

    public void setMethod(Method method)
    {
        this.method = method;
    }

    public void setPath(Path path)
    {
        this.path = path;
    }

    public void setHandler(ActionWrapperInterface handler)
    {
        this.handler = handler;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    @Override
    public String toString()
    {
        return String.format("%s %s", getMethod(), getPath().declaration);
    }
}