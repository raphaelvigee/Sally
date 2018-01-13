package com.sallyf.sallyf.ExpressionLanguage;

import java.util.function.Function;

@FunctionalInterface
public interface ThrowingFunction<T, R> extends Function<T, R>
{
    @Override
    default R apply(T t)
    {
        try {
            return applyThrows(t);
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    R applyThrows(T elem) throws Exception;
}
