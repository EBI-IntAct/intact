/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others. All
 * rights reserved. Please see the file LICENSE in the root directory of this
 * distribution.
 */
package uk.ac.ebi.intact.application.mine.business.graph;

import java.util.Hashtable;
import java.util.Map;

import jdsl.graph.api.Vertex;

/**
 * The class implements the interface <tt>Storage</tt> and uses a
 * <tt>Hashtable</tt> as storage structure.
 * 
 * @author Andreas Groscurth 
 */
public class MineStorage implements Storage {
	private static final int SIZE_DIVIDER = 2;
	private static final int ELEMENTS = 3;
	private Map storageMap;

	//TODO: CHANGE THE SIZE OF THE MAP ?!
	public MineStorage(int numberOfNodes) {
		storageMap = new Hashtable(numberOfNodes / SIZE_DIVIDER);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see uk.ac.ebi.intact.application.mine.business.graph.Storage#getElements(jdsl.graph.api.Vertex)
	 */
	public Object[] getElements(Vertex v) {
		Object[] elements = (Object[]) storageMap.get(v);
		// of no elements yet are given new ones are created
		if (elements == null) {
			elements = new Object[ELEMENTS];
			storageMap.put(v, elements);
		}
		return elements;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see uk.ac.ebi.intact.application.mine.business.graph.Storage#getElements(jdsl.graph.api.Vertex,
	 *      int)
	 */
	public Object getElement(Vertex v, int index) {
		return getElements(v)[index];
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see uk.ac.ebi.intact.application.mine.business.graph.Storage#setElements(jdsl.graph.api.Vertex,
	 *      int, java.lang.Object)
	 */
	public void setElement(Vertex v, int index, Object o) {
		Object[] se = getElements(v);
		se[index] = o;
		storageMap.put(v, se);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see uk.ac.ebi.intact.application.mine.business.graph.Storage#has(jdsl.graph.api.Vertex,
	 *      int)
	 */
	public boolean has(Vertex v, int index) {
		Object[] se = (Object[]) storageMap.get(v);
		return se[index] != null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see uk.ac.ebi.intact.application.mine.business.graph.Storage#cleanup()
	 */
	public void cleanup() {
		storageMap.clear();
	}
}