/**
 * Copyright 2009 The European Bioinformatics Institute, and others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package uk.ac.ebi.intact.view.webapp.controller.browse;

import org.apache.commons.lang.StringUtils;
import org.apache.myfaces.trinidad.model.ChildPropertyTreeModel;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;

/**
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class AutoExpandedTreeModel extends ChildPropertyTreeModel {

    public AutoExpandedTreeModel(Object o, String property) {
        super(o, property);
    }

    @Override
    protected Object getChildData(Object o) {
        List children = invokeMethodToGetChildren(o);

        if (children.size() == 1) {
            Object childObj = getChildData(children.iterator().next());

            Collection grandchildren;

            if (childObj instanceof Collection) {
                 grandchildren = (Collection)childObj;
            } else {
                 grandchildren = invokeMethodToGetChildren(childObj);
            }

            if (grandchildren.isEmpty()) {
                return o;
            }

            return grandchildren;
        } else if (children.isEmpty()) {
            //return o;
        }

        return super.getChildData(o);
    }

    private List invokeMethodToGetChildren(Object o) {
        List children;
        Method method = null;
        String methodName = "get"+ StringUtils.capitalize(getChildProperty());
        try {
            method = o.getClass().getMethod(methodName, new Class[0]);
            children = (List) method.invoke(o);
        } catch (Exception e) {
            throw new IllegalArgumentException("Problem executing method: "+method, e);
        }
        return children;
    }
}
