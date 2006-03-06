/*
 * Contains common JavaScript routines for the editor
 * Author: smudali@ebi.ac.uk
 * Version: $Id$
 */

// This is a global variable to setup a window.
var newWindow;

// Create a new window if it hasnt' created before and bring it to the
// front if it is focusable.
function makeNewWindow(link) {
    if (!newWindow || newWindow.closed) {
        newWindow = window.open(link, "display", "scrollbars=yes,height=500,width=600");
        newWindow.focus();
    }
    else if (newWindow.focus) {
        newWindow.focus();
        newWindow.location.href = link;
    }
}

// Displays the link from Xref's primary id in the same window as the
// search window.
function showXrefPId(link) {
    makeNewWindow(link);
}
