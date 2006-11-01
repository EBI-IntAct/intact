/*
 * Validates an annotation text. Returns true only for a keystroke that is
 * within 31 and 127 of unicode characters or a backspace and the last two characters
 * not two consecutive spaces.
 * Author: smudali@ebi.ac.uk
 * Version: $Id$
 */
function validateComment(element, evt) {
    frm=document.forms[0];
    var keyCode = evt.which ? evt.which : evt.keyCode;
    //window.alert(keyCode);
    // Allow backspace or else a user can't delete his/own text!!
    if ((keyCode > 31 && keyCode < 127) || keyCode == 08) {
        var desc = element.value;//document.forms[0].elements['newAnnotation.description'].value;
        if (desc.charAt(desc.length - 1) == ' ' && desc.charAt(desc.length - 2) == ' ') {
            //keyCode == 32) {
            window.alert("Multiple spaces are not allowed");
            frm.dispatch.disabled = true;
            return false;
        }
        frm.dispatch.disabled = false;

        return true;
    }

    var s = element.value;
    var o="";
    for( m=0;s.charAt(m);++m ) {
		if ( (c=s.charCodeAt(m)) < 128 && c != 38 ) {
			o+=s.charAt(m);
  	        } else if (c==38) {
			o+="&";
		} else {
			o+="&#"+c+";";
		}

	}


    msg = "The character you entered is not allowed. Only Unicode characters from 0020";
    msg += "(space) to 007E(~) are allowed : '"+o+"`'" ;
    o="";
    window.alert(msg);
    document.anchors('annotation').dispatch.disabled= true;
    return false;
}
