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

import org.junit.Test;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;

public class DefaultPropertyMapperTest {
    private static BeanProperty<String> fluidSetterProperty = new BeanProperty<>(String.class, "fluidSetterProperty", "hello");
    private static BeanProperty<Integer> witherProperty = new BeanProperty<>(Integer.class, "witherProperty", 234082803);
    private static BeanProperty<Long> setterProperty = new BeanProperty<>(Long.class, "setterProperty", 2083941L);

    private final PropertyMapper sut = new DefaultPropertyMapper();

    @Test
    public void aSetterCanBeJustThePropertyName() {
        FluentSetter<ClassWithVoidSetters> fluidSetter = sut.map(ClassWithVoidSetters.class, fluidSetterProperty);
        FluentSetter<ClassWithVoidSetters> witherSetter = sut.map(ClassWithVoidSetters.class, witherProperty);
        FluentSetter<ClassWithVoidSetters> setterSetter = sut.map(ClassWithVoidSetters.class, setterProperty);
        ClassWithVoidSetters testClass = new ClassWithVoidSetters();

        testClass = fluidSetter.set(testClass);
        testClass = witherSetter.set(testClass);
        testClass = setterSetter.set(testClass);

        assertThat(testClass.fluidSetterProperty, equalTo(fluidSetterProperty.value));
        assertThat(testClass.witherProperty, equalTo(witherProperty.value));
        assertThat(testClass.setterProperty, equalTo(setterProperty.value));
    }

    @Test
    public void honoursReturnOfFluentSetter() {
        FluentSetter<ClassThatReturnsSelf> setter = sut.map(ClassThatReturnsSelf.class, fluidSetterProperty);
        ClassThatReturnsSelf mockReturn = mock(ClassThatReturnsSelf.class);
        ClassThatReturnsSelf testClass = new ClassThatReturnsSelf(mockReturn);

        ClassThatReturnsSelf result = setter.set(testClass);

        assertThat(result, is(sameInstance(mockReturn)));
    }

    @Test(expected = IllegalArgumentException.class)
    public void unknownSettersThrowsRuntimeException() {
        sut.map(ClassWithVoidSetters.class, new BeanProperty<>(String.class, "nonExistentProperty", "NOOP"));
    }

    private class ClassWithVoidSetters {
        String fluidSetterProperty;
        Integer witherProperty;
        Long setterProperty;

        public void fluidSetterProperty(String value) {
            this.fluidSetterProperty = value;
        }

        public void withWitherProperty(Integer witherProperty) {
            this.witherProperty = witherProperty;
        }

        public void setSetterProperty(Long setterProperty) {
            this.setterProperty = setterProperty;
        }
    }

    private class ClassThatReturnsSelf {
        private final ClassThatReturnsSelf returnInstance;

        ClassThatReturnsSelf(ClassThatReturnsSelf returnInstance) {
            this.returnInstance = returnInstance;
        }

        public ClassThatReturnsSelf fluidSetterProperty(String value) {
            return returnInstance;
        }

        public ClassThatReturnsSelf withWitherProperty(Integer witherProperty) {
            return returnInstance;
        }

        public ClassThatReturnsSelf setSetterProperty(Long setterProperty) {
            return returnInstance;
        }
    }
}