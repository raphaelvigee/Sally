package com.sallyf.sallyf.Router;

import com.sallyf.sallyf.BaseController;
import com.sallyf.sallyf.Container.Container;
import com.sallyf.sallyf.Container.ContainerAware;
import com.sallyf.sallyf.Exception.FrameworkException;
import com.sallyf.sallyf.Exception.RouteDuplicateException;
import com.sallyf.sallyf.Exception.UnhandledParameterException;
import com.sallyf.sallyf.Server.HTTPSession;
import com.sallyf.sallyf.Server.Method;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Router extends ContainerAware
{
    private ArrayList<Route> routes = new ArrayList<>();

    private ArrayList<String> routeSignatures = new ArrayList<>();

    public void addController(Class<? extends BaseController> controllerClass) throws FrameworkException
    {
        com.sallyf.sallyf.Annotation.Route controllerAnnotation = controllerClass.getAnnotation(com.sallyf.sallyf.Annotation.Route.class);

        String pathPrefix = controllerAnnotation == null ? "" : controllerAnnotation.path();

        java.lang.reflect.Method[] methods = controllerClass.getMethods();

        for (java.lang.reflect.Method method : methods) {
            if (method.isAnnotationPresent(com.sallyf.sallyf.Annotation.Route.class)) {
                if (!java.lang.reflect.Modifier.isStatic(method.getModifiers())) {
                    System.err.println("Method `" + method.getName() + "` is not static, ignoring");
                    continue;
                }

                com.sallyf.sallyf.Annotation.Route routeAnnotation = method.getAnnotation(com.sallyf.sallyf.Annotation.Route.class);

                final Class<?>[] parameterTypes = method.getParameterTypes();

                addAction(routeAnnotation.method(), pathPrefix + routeAnnotation.path(), (container, session, route) -> {
                    try {
                        return (Response) method.invoke(null, getActionParameters(parameterTypes, container, session, route));
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        e.printStackTrace();
                        return null;
                    }
                });
            }
        }
    }

    public Object[] getActionParameters(Class<?>[] parameterTypes, Container container, HTTPSession session, Route route) throws UnhandledParameterException
    {
        Object[] parameters = new Object[parameterTypes.length];
        int i = 0;
        for (Class<?> parameterType : parameterTypes) {
            Object p;

            if (parameterType == Container.class) {
                p = container;
            } else if (parameterType == HTTPSession.class) {
                p = session;
            } else if (parameterType == Route.class) {
                p = route;
            } else if (parameterType == RouteParameters.class) {
                p = getRouteParameters(route, session);
            } else {
                throw new UnhandledParameterException(parameterType);
            }

            parameters[i++] = p;
        }

        return parameters;
    }

    public void addRoute(Route route) throws RouteDuplicateException
    {
        if (routeSignatures.contains(route.toString())) {
            throw new RouteDuplicateException(route);
        }

        routes.add(route);
        routeSignatures.add(route.toString());
    }

    public void addAction(Method method, String path, ActionInterface handler) throws RouteDuplicateException
    {
        addRoute(new Route(method, path, handler));
    }

    public ArrayList<Route> getRoutes()
    {
        return routes;
    }

    public Route match(HTTPSession session)
    {
        for (Route route : routes) {
            if (session.getMethod().toString().equals(route.getMethod().toString())) {
                Pattern r = Pattern.compile(route.getPath().pattern);

                Matcher m = r.matcher(session.getUri());

                if (m.matches()) {
                    return route;
                }
            }
        }

        return null;
    }

    public RouteParameters getRouteParameters(Route route, HTTPSession session)
    {
        Pattern r = Pattern.compile(route.getPath().getPattern());

        Matcher m = r.matcher(session.getUri());

        RouteParameters parameterValues = new RouteParameters();

        if (m.matches()) {
            route.getPath().parameters.forEach((index, name) -> {
                parameterValues.put(name, m.group(index));
            });
        }

        return parameterValues;
    }
}
