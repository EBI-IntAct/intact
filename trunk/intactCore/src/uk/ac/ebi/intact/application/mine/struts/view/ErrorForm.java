/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others. All
 * rights reserved. Please see the file LICENSE in the root directory of this
 * distribution.
 */

package uk.ac.ebi.intact.application.mine.struts.view;

import org.apache.struts.action.ActionForm;

/**
 * An <tt>ErrorForm</tt> stores a thrown error of the application. After an
 * error was thrown the application is forwarded to a special error page where
 * the error message of this form is displayed.
 * 
 * @author Andreas Groscurth
 */
public class ErrorForm extends ActionForm {
	private String error;
	
	/**
	 * Creates a new error form with an error message;
	 * 
	 * @param error the message of the error
	 */
	public ErrorForm(String error) {
		this.error = error;
	}

	/**
	 * Returns the error message.
	 * 
	 * @return Returns the error message.
	 */
	public String getError() {
		return error;
	}
}