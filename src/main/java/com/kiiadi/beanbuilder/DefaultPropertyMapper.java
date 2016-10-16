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

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.kiiadi.beanbuilder.utils.StringUtils.capitalize;
import static com.kiiadi.beanbuilder.utils.UncheckedExceptions.wrapSafely;

public class DefaultPropertyMapper implements PropertyMapper {
    private final List<MethodGetter> getters = new ArrayList<>();

    public DefaultPropertyMapper() {
        getters.add(new MethodGetter(p -> p));
        getters.add(new MethodGetter(p -> "with" + capitalize(p)));
        getters.add(new MethodGetter(p -> "set" + capitalize(p)));
    }

    @Override
    @SuppressWarnings("unchecked")
    public <Clz> FluentSetter<Clz> map(Class<Clz> clzz, BeanProperty<?> property) {
        Method m = getMethod(clzz, property);
        return instance -> wrapSafely(() -> {
            if (m.getReturnType().equals(clzz)) return (Clz) m.invoke(instance, property.value);
            m.invoke(instance, property.value);
            return instance;
        });
    }

    private Method getMethod(Class<?> clzz, BeanProperty<?> property) {
        List<Exception> problems = new ArrayList<>();
        for (MethodGetter getter : getters) {
            try {
                return getter.get(clzz, property);
            } catch (NoSuchMethodException e) {
                problems.add(e);
            }
        }
        throw new IllegalArgumentException(
                String.format("Can't find setter for %s: \n - %s",
                        property.name,
                        problems.stream().map(Object::toString).collect(Collectors.joining("\n - "))));
    }

    private class MethodGetter {
        private final GetterName getterName;

        MethodGetter(GetterName getterName) {
            this.getterName = getterName;
        }

        Method get(Class<?> clz, BeanProperty<?> property) throws NoSuchMethodException {
            return clz.getMethod(getterName.name(property.name), property.type);
        }
    }

    private interface GetterName {
        String name(String propertyName);
    }
}
