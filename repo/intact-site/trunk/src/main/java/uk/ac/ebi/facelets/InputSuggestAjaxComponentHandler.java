/*
 * Copyright 2004 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package uk.ac.ebi.facelets;

import java.util.List;

import com.sun.facelets.tag.MetaRule;
import com.sun.facelets.tag.MetaRuleset;
import com.sun.facelets.tag.MethodRule;
import com.sun.facelets.tag.TagAttribute;
import com.sun.facelets.tag.jsf.ComponentConfig;
import com.sun.facelets.tag.jsf.html.HtmlComponentHandler;

/**
 * @author Peter Mahoney
 */

public class InputSuggestAjaxComponentHandler extends HtmlComponentHandler {

   private static final String SUGGESTED_ITEMS_METHOD_ATTRIBUTE_NAME = "suggestedItemsMethod";
   private static final String MAX_SUGGESTED_ITEMS_ATTRIBUTE_NAME = "maxSuggestedItems";

   public InputSuggestAjaxComponentHandler(ComponentConfig tagConfig) {
      super(tagConfig);
   }

   protected MetaRuleset createMetaRuleset(Class type)
    {
        MetaRuleset m = super.createMetaRuleset(type);
        m.addRule(getSuggestedItemsMethodRule(getAttribute(MAX_SUGGESTED_ITEMS_ATTRIBUTE_NAME)));

        return m;
    }

   private MetaRule getSuggestedItemsMethodRule(TagAttribute maxItems) {
      Class[] paramList = maxItems != null ?
            new Class[] {String.class, Integer.class} : new Class[]{String.class};

      return new MethodRule(SUGGESTED_ITEMS_METHOD_ATTRIBUTE_NAME, List.class, paramList);
   }

    
}

