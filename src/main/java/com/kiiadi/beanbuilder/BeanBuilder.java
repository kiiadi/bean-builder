/*
 * Copyright 2016 Kyle Thomson
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.kiiadi.beanbuilder;

import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;

import static com.kiiadi.beanbuilder.utils.UncheckedExceptions.wrapSafely;

public class BeanBuilder<T> {
    private final Class<T> typeToBuild;
    private final Collection<FluentSetter<T>> setters;

    public BeanBuilder(final Class<T> typeToBuild, Collection<BeanProperty<?>> properties, final PropertyMapper mapper) {
        this.typeToBuild = typeToBuild;
        this.setters = properties.stream().map(p -> mapper.map(typeToBuild, p)).collect(Collectors.toSet());
    }

    public BeanBuilder(final Class<T> typeToBuild, Collection<BeanProperty<?>> properties) {
        this(typeToBuild, properties, new DefaultPropertyMapper());
    }

    public BeanBuilder(final Class<T> typeToBuild, BeanProperty<?> property) {
        this(typeToBuild, Collections.singleton(property));
    }

    public T build() {
        T instance = wrapSafely(typeToBuild::newInstance);
        for (FluentSetter<T> setter: setters) {
            instance = setter.set(instance);
        }
        return instance;
    }
}
