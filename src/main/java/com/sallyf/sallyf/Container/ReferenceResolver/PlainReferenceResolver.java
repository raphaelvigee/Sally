package com.sallyf.sallyf.Container.ReferenceResolver;

import com.sallyf.sallyf.Container.*;

public class PlainReferenceResolver<T extends ServiceInterface, V> implements ReferenceResolverInterface<T, PlainReference<V>, V>
{
    @Override
    public boolean supports(ServiceDefinition serviceDefinition, ReferenceInterface reference)
    {
        return reference instanceof PlainReference;
    }

    @Override
    public V resolve(ServiceDefinition<T> serviceDefinition, PlainReference<V> reference)
    {
        return reference.value;
    }
}
