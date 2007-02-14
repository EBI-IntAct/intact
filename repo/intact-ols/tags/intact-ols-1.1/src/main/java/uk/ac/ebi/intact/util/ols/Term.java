/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.util.ols;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Represents a term in OLS
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 * @since 1.1
 */
public class Term implements Serializable {

    private String id;
    private String name;
    private Term parent;
    private Collection<Term> children;

    /**
     * Constructs a new Term
     */
    public Term() {
    }

    /**
     * Constructs a new Term
     *
     * @param id   the id for the term
     * @param name name of the term
     */
    public Term(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Collection<Term> getChildren() {
        return children;
    }

    public void setChildren(Collection<Term> children) {
        this.children = children;
    }

    public void addChild(Term child) {
        if (children == null) {
            children = new ArrayList<Term>();
        }
        child.setParent(this);
        children.add(child);
    }

    public Term getParent() {
        return parent;
    }

    public void setParent(Term parent) {
        this.parent = parent;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("Term");
        sb.append("{children=").append(children);
        sb.append(", id='").append(id).append('\'');
        sb.append(", name='").append(name).append('\'');
        if (parent != null) sb.append(", parent=").append(parent.getId());
        sb.append('}');
        return sb.toString();
    }
}