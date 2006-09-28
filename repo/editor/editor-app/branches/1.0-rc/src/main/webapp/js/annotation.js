/*
 * Validates an annotation text. Returns true only for a keystroke that is
 * within 31 and 127 of unicode characters or a backspace and the last two characters
 * not two consecutive spaces.
 * Author: smudali@ebi.ac.uk
 * Version: $Id$
 */
function validateComment(element, evt) {
    var keyCode = evt.which ? evt.which : evt.keyCode;
    //window.alert(keyCode);
    // Allow backspace or else a user can't delete his/own text!!
    if ((keyCode > 31 && keyCode < 127) || keyCode == 08) {
        var desc = element.value;//document.forms[0].elements['newAnnotation.description'].value;
        if (desc.charAt(desc.length - 1) == ' ' && keyCode == 32) {
            window.alert("Multiple spaces are not allowed");
            return false;
        }
        return true;
    }
    msg = "The character you entered is not allowed. Only Unicode characters from 0020";
    msg += "(space) to 007E(~) are allowed";
    window.alert(msg);
    return false;
}
