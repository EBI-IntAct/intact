/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.application.dataConversion.psiUpload.gui;

import javax.swing.*;
import java.awt.*;

/**
 * That class allows us to display the progress of .
 * 
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 */
public class Monitor {

    private JProgressBar progressBar;
    private String name;
    private JFrame frame;
    private int max;

    public Monitor( final int max, final String name ) {

        if( max <= 0 ) {
            throw new IllegalArgumentException( "The count has to be greater than 0, you gave" + max + "." );
        }

        this.name = name;
        this.max = max;

        init();
    }


    private void init() {
        frame = new JFrame( name );
        Container cp = frame.getContentPane();

        cp.setLayout( new FlowLayout() );

        progressBar = new JProgressBar( JProgressBar.HORIZONTAL, 0, max - 1 );
        progressBar.setStringPainted( true );

        cp.add( progressBar );
        frame.pack();  // shrink-wrap alternative

        Dimension d = frame.getSize();
        frame.setSize( (int) ( d.getWidth() * 1.2 ),
                       (int) ( d.getHeight() * 1.2 ) );
    }

    public void show() {
        frame.setVisible( true );
    }

    public void hide() {
        frame.setVisible( false );
    }

    public void updateProteinProcessedCound( int newCount ) {

        if( progressBar == null ) {
            throw new NullPointerException( "The progress bar hasn't been created." );
        }

        progressBar.setValue( newCount );
    }


    /**
     * D E M O
     */
    public void testIt() {
        for ( int i = 0; i <= max; i++ ) {
            try {
                Thread.sleep( 10 );
            } catch ( InterruptedException e ) {
                e.printStackTrace();
            }
            progressBar.setValue( i );
        }
    }

    public static void main( String[] args ) {

        Monitor monitor = new Monitor( 3000, "Protein update" );
        monitor.show();
        monitor.testIt();
    }
}
