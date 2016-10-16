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

public class BeanProperty<T> {
    public final Class<T> type;
    public final String name;
    public final T value;

    public BeanProperty(Class<T> type, String name, T value){
        this.type = type;
        this.name = name;
        this.value = value;
    }
}
