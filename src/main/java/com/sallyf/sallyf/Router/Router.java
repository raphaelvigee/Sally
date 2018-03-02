package com.sallyf.sallyf.Router;

import com.sallyf.sallyf.Annotation.Requirement;
import com.sallyf.sallyf.Container.Container;
import com.sallyf.sallyf.Container.ServiceInterface;
import com.sallyf.sallyf.Controller.ControllerInterface;
import com.sallyf.sallyf.Event.RouteParametersEvent;
import com.sallyf.sallyf.Event.RouteRegisterEvent;
import com.sallyf.sallyf.EventDispatcher.EventDispatcher;
import com.sallyf.sallyf.Exception.*;
import com.sallyf.sallyf.KernelEvents;
import com.sallyf.sallyf.Router.ActionParameterResolver.RequestResolver;
import com.sallyf.sallyf.Router.ActionParameterResolver.RouteParameterResolver;
import com.sallyf.sallyf.Router.ActionParameterResolver.RuntimeBagResolver;
import com.sallyf.sallyf.Router.ActionParameterResolver.ServiceResolver;
import com.sallyf.sallyf.Router.ResponseTransformer.HttpExceptionTransformer;
import com.sallyf.sallyf.Router.ResponseTransformer.PrimitiveTransformer;
import com.sallyf.sallyf.Server.RuntimeBag;
import com.sallyf.sallyf.Utils.ClassUtils;
import org.eclipse.jetty.server.Request;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class Router implements ServiceInterface
{
    private final Container container;

    private final EventDispatcher eventDispatcher;

    private HashMap<String, Route> routes = new HashMap<>();

    private Map<Class, ControllerInterface> controllers = new HashMap<>();

    private ArrayList<RouteParameterResolverInterface> routeParameterResolvers = new ArrayList<>();

    private ArrayList<ActionParameterResolverInterface> actionParameterResolvers = new ArrayList<>();

    private ArrayList<ResponseTransformerInterface> responseTransformers = new ArrayList<>();

    public Router(Container container, EventDispatcher eventDispatcher)
    {
        this.container = container;
        this.eventDispatcher = eventDispatcher;
    }

    public void initialize(Container container)
    {
        addActionParameterResolver(new RequestResolver());
        addActionParameterResolver(new RuntimeBagResolver());
        addActionParameterResolver(new RouteParameterResolver(container));
        addActionParameterResolver(new ServiceResolver(container));

        addResponseTransformer(new PrimitiveTransformer());
        addResponseTransformer(new HttpExceptionTransformer());
    }

    public <C extends ControllerInterface> C registerController(Class<C> controllerClass)
    {
        C controller = instantiateController(controllerClass);

        controllers.put(controllerClass, controller);

        com.sallyf.sallyf.Annotation.Route controllerAnnotation = controllerClass.getAnnotation(com.sallyf.sallyf.Annotation.Route.class);

        String pathPrefix = controllerAnnotation == null ? "" : controllerAnnotation.path();

        String actionNamePrefix = controllerClass.getSimpleName() + ".";
        if (controllerAnnotation != null && !controllerAnnotation.name().isEmpty()) {
            actionNamePrefix = controllerAnnotation.name();
        }

        java.lang.reflect.Method[] methods = controllerClass.getMethods();

        for (java.lang.reflect.Method method : methods) {
            if (method.isAnnotationPresent(com.sallyf.sallyf.Annotation.Route.class)) {
                com.sallyf.sallyf.Annotation.Route routeAnnotation = method.getAnnotation(com.sallyf.sallyf.Annotation.Route.class);

                com.sallyf.sallyf.Annotation.Route[] annotations;
                if (controllerAnnotation == null) {
                    annotations = new com.sallyf.sallyf.Annotation.Route[]{routeAnnotation};
                } else {
                    annotations = new com.sallyf.sallyf.Annotation.Route[]{controllerAnnotation, routeAnnotation};
                }

                final Parameter[] parameterTypes = method.getParameters();

                String actionName = method.getName();
                if (!routeAnnotation.name().isEmpty()) {
                    actionName = routeAnnotation.name();
                }

                String fullName = actionNamePrefix + actionName;
                String fullPath = pathPrefix + routeAnnotation.path();

                ActionWrapperInterface handler = (runtimeBag) -> {
                    Object[] parameters = resolveActionParameters(parameterTypes, runtimeBag);

                    try {
                        return method.invoke(controller, parameters);
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        e.printStackTrace();
                        return null;
                    }
                };

                Route route = new Route(fullName, routeAnnotation.methods(), fullPath, handler);
                Path path = route.getPath();

                for (com.sallyf.sallyf.Annotation.Route annotation : annotations) {
                    for (Requirement requirement : annotation.requirements()) {
                        path.getRequirements().put(requirement.name(), requirement.requirement());
                    }
                }

                registerRoute(fullName, route);

                eventDispatcher.dispatch(KernelEvents.ROUTE_REGISTER, new RouteRegisterEvent(route, controller, method));
            }
        }

        return controller;
    }

    public Object[] resolveActionParameters(Parameter[] parameters, RuntimeBag runtimeBag)
    {
        Object[] outParameters = new Object[parameters.length];
        int i = 0;
        for (Parameter parameter : parameters) {
            outParameters[i++] = resolveActionParameter(parameter, runtimeBag);
        }

        return outParameters;
    }

    public Object resolveActionParameter(Parameter parameter, RuntimeBag runtimeBag)
    {
        for (ActionParameterResolverInterface resolver : actionParameterResolvers) {
            if (resolver.supports(parameter, runtimeBag)) {
                return resolver.resolve(parameter, runtimeBag);
            }
        }

        throw new UnhandledParameterException(parameter);
    }

    public Route registerRoute(String name, Route route)
    {
        route.getPath().computePattern();

        route.setName(name);

        routes.put(name, route);

        return route;
    }

    private <T extends ControllerInterface> T instantiateController(Class<T> controllerClass)
    {
        T controller = ClassUtils.newInstance(controllerClass, ControllerInstantiationException::new);

        controller.setContainer(container);

        return controller;
    }

    public HashMap<String, Route> getRoutes()
    {
        return routes;
    }

    public Route match(Request request)
    {
        for (Route route : routes.values()) {
            if (Stream.of(route.getMethods()).map(Enum::toString).anyMatch(request.getMethod()::equalsIgnoreCase)) {
                Pattern r = Pattern.compile(route.getPath().getPattern());

                Matcher m = r.matcher(request.getPathInfo());

                if (m.matches()) {
                    return route;
                }
            }
        }

        return null;
    }

    public RouteParameters getRouteParameters(RuntimeBag runtimeBag)
    {
        Pattern r = Pattern.compile(runtimeBag.getRoute().getPath().getPattern());

        Matcher m = r.matcher(runtimeBag.getRequest().getPathInfo());

        RouteParameters parameterValues = new RouteParameters();

        if (m.matches()) {
            runtimeBag.getRoute().getPath().getParameters().forEach((index, name) -> {
                parameterValues.put(name, resolveRouteParameter(m, index, name, runtimeBag));
            });
        }

        eventDispatcher.dispatch(KernelEvents.ROUTE_PARAMETERS, new RouteParametersEvent(runtimeBag, parameterValues));

        return parameterValues;
    }

    public Object resolveRouteParameter(Matcher m, Integer index, String name, RuntimeBag runtimeBag)
    {
        String value = m.group(index);

        for (RouteParameterResolverInterface resolver : routeParameterResolvers) {
            if (resolver.supports(name, value, runtimeBag)) {
                return resolver.resolve(name, value, runtimeBag);
            }
        }

        return value;
    }

    public Response transformResponse(RuntimeBag runtimeBag, Object response)
    {
        while (true) {
            boolean transformed = false;
            for (ResponseTransformerInterface transformer : responseTransformers) {
                if (transformer.supports(runtimeBag, response)) {
                    try {
                        response = transformer.transform(runtimeBag, response);
                    } catch (Exception e) {
                        throw new FrameworkException(e);
                    }
                    transformed = true;
                }
            }

            if (response instanceof Response) {
                return (Response) response;
            }

            if (!transformed) {
                throw new InvalidResponseTypeException(response);
            }
        }
    }

    public Map<Class, ControllerInterface> getControllers()
    {
        return controllers;
    }

    public void addRouteParameterResolver(RouteParameterResolverInterface resolver)
    {
        routeParameterResolvers.add(resolver);
    }

    public void addActionParameterResolver(ActionParameterResolverInterface resolver)
    {
        actionParameterResolvers.add(resolver);
    }

    public void addResponseTransformer(ResponseTransformerInterface transformer)
    {
        responseTransformers.add(transformer);
    }
}
