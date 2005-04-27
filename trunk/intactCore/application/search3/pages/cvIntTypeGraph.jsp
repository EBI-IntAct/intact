<html><head>
<%@ taglib uri="/WEB-INF/tld/cvGraph.tld" prefix="advancedSearch" %>

<script language="javascript" type="text/javascript">
 function SendInfo(theValue){
     //var txtVal = theValue;
     //window.opener.document.advancedForm.cvInteractionType.value = txtVal;

     var options = window.opener.document.advancedForm["cvInteractionType"];
     for(var i = 0; i < options.length; i++) {
	    var option = options[i];
        if(option.value == theValue) {
		    option.selected = true;
	        break;
		}
	 }
}
</script>
<title> CvInteractionType</title>
</head>
<body>

<form name="formPop">
<input type='button' onclick='javascript:window.close();' name="button" value="CLOSE" />
    <!--
        Displays the cvDag graph if the picture
        has been generated and stored in the response.
      -->
    <advancedSearch:displayCvDag/>
<br><input type='button' onclick='javascript:window.close();' name="button" value="CLOSE" />
</form>
</body>
</html>
