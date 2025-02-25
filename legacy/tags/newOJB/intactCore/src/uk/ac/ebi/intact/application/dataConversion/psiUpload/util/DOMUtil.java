/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.application.dataConversion.psiUpload.util;

import org.w3c.dom.*;

public class DOMUtil {

    public static class TagNotFoundException extends RuntimeException {

        public TagNotFoundException() {
        }

        public TagNotFoundException( Throwable cause ) {
            super( cause );
        }

        public TagNotFoundException( String message ) {
            super( message );
        }

        public TagNotFoundException( String message, Throwable cause ) {
            super( message, cause );
        }
    }

    public static class MultipleTagFoundException extends RuntimeException {

        public MultipleTagFoundException() {
        }

        public MultipleTagFoundException( Throwable cause ) {
            super( cause );
        }

        public MultipleTagFoundException( String message ) {
            super( message );
        }

        public MultipleTagFoundException( String message, Throwable cause ) {
            super( message, cause );
        }
    }


    public static Element getFirstElement( final Element element, final String name ) {

        if( element == null ) {
            throw new IllegalArgumentException( "You must give a non null DOM Element to parse." );
        }

        if( name == null || "".equals( name.trim() ) ) {
            throw new IllegalArgumentException( "You must give a non null/empty DOM Element's name to look for." );
        }

        final NodeList list = element.getChildNodes();
        Node myNode = null;
        for ( int i = 0; i < list.getLength(); i++ ) {
            final Node node = list.item( i );

            if( node.getNodeType() != Node.ELEMENT_NODE ) {
                continue; // skip all what's not an Element
            }

            if( name.equals( ( (Element) node ).getTagName() ) ) {
                if( myNode == null ) {
                    myNode = node;
                } else {
                    // there is really more than two node having the same name at that level
                    throw new MultipleTagFoundException( "More than one element named " + name +
                                                         " in the tag " + element.getTagName() );
                }
            }
        }

        if( myNode != null ) {
            return (Element) myNode;
        } else {
            return null;
//            throw new TagNotFoundException( "No element named " + name + " directly under the tag " +
//                                            element.getTagName() );
        }
    }


    public static String getSimpleElementText( final Element node, final String name ) {
        final Element namedElement = getFirstElement( node, name );
        if( namedElement == null ) {
            return null;
        }
        return getSimpleElementText( namedElement );
    }

    public static String getSimpleElementText( final Element node ) {
        final StringBuffer sb = new StringBuffer();
        final NodeList children = node.getChildNodes();
        for ( int i = 0; i < children.getLength(); i++ ) {
            final Node child = children.item( i );
            if( child instanceof Text ) {
                sb.append( child.getNodeValue() );
            }
        }
        return sb.toString();
    }

    /**
     * @param names
     * @return
     */
    public static String getShortLabel( final Element names ) {
        return getSimpleElementText( names, "shortLabel" );
    }

    /**
     * @param names
     * @return
     */
    public static String getFullName( final Element names ) {
        return getSimpleElementText( names, "fullName" );
    }

    /**
     * Display a string representation of the element given in parameter.
     * It display all the parents from the root up to the element given.
     *
     * @param element
     * @return
     */
    public static String getContext( final Element element ) {

        Node e = element;
        final StringBuffer sb = new StringBuffer();
        // context of the error
        String name;

        do {
            if( e == null ) {
                break;
            }
            name = e.getNodeName();
            final NamedNodeMap attrs = e.getAttributes();

            sb.insert( 0, '}' );

            if( attrs != null ) {
                // Get number of attributes in the element
                final int numAttrs = attrs.getLength();

                // Process each attribute
                for ( int i = 0; i < numAttrs; i++ ) {
                    final Attr attr = (Attr) attrs.item( i );

                    // Get attribute name and value
                    final String attrName = attr.getNodeName();
                    final String attrValue = attr.getNodeValue();
                    sb.insert( 0, ')' ).insert( 0, attrValue ).insert( 0, '=' ).insert( 0, attrName ).insert( 0, '(' );
                }

                if( numAttrs > 0 ) {
                    sb.insert( 0, ':' );
                }
            }

            sb.insert( 0, name ).insert( 0, '{' );

            e = e.getParentNode();
        } while ( false == "entry".equals( name ) );

        return sb.toString();
    }
}