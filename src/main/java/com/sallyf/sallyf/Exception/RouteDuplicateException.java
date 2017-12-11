package com.sallyf.sallyf.Exception;

import com.sallyf.sallyf.Routing.Route;

public class RouteDuplicateException extends FrameworkException
{
    public RouteDuplicateException(Route route)
    {
        super("Route already present: " + route);
    }
}
