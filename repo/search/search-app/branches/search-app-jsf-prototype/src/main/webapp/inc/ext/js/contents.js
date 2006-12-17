


var loc = ""  + document.location; // turn to string...



if (loc.indexOf(".xml")== -1) {
	document.write('<style type="text/css">@media print { body, .contents, .header, .contentsarea, .head{ position: relative;}  } </style>');
}
	
	
if(document.layers){
	document.write('<script type="text/javascript" src="http://www.ebi.ac.uk/inc/js/netscape47.js"></script>');
}

if (navigator.userAgent.indexOf("Opera") != -1) {
	//window.attachEvent("onload", fixtrasparency);
	document.write('<link rel="stylesheet"  href="http://www.ebi.ac.uk/inc/css/Opera.css"   type="text/css" />');
}

if (navigator.userAgent.indexOf("MSIE") != -1) {
	document.write('<link rel="stylesheet"  href="http://www.ebi.ac.uk/inc/css/contents_IE.css"   type="text/css" />');
}

if(navigator.userAgent.indexOf("Safari") != -1) {
	document.write('<link rel="stylesheet"  href="http://www.ebi.ac.uk/inc/css/contents_Safari.css"   type="text/css" />');	
}


function showRightMenu(){						
	document.getElementById('rightmenu').style.visibility='visible'; 
	document.getElementById('rightmenu').style.display='block';
	document.getElementById('rightmenu').style.width='145px'; 
}

function hideRightMenu(){
	document.getElementById('rightmenu').style.visibility='hidden'; 
	document.getElementById('rightmenu').style.display='none';
	document.getElementById('rightmenu').style.width='1px'; 
}

function showLeftMenu(){
	document.getElementById('leftmenu').style.visibility='visible'; 
	document.getElementById('leftmenu').style.display='block';
	document.getElementById('leftmenu').style.width='145px'; 
}
				
function hideLeftMenu(){
	document.getElementById('leftmenu').style.visibility='hidden'; 
	document.getElementById('leftmenu').style.display='none';
	document.getElementById('leftmenu').style.width='1px'; 
}

function maximisePage(){
	document.getElementById('contentspane').style.width='100%';
}

function minimisePage(){
	document.getElementById('contentspane').style.width='790px';
}

//Select All Text in Input Box

function selectAll(theField) {
  var tempval = eval("document." + theField);
  tempval.focus();
  tempval.select();
} 

// OPEN WINDOW METHODS 

var newWin;

function openWindow(address){ 
	newWin = window.open(address,'_help','personalbar=0, toolbar=0,location=0,directories=0,menuBar=0,status=0,resizable=yes,scrollBars=0, resizable=1, width=800, height = 500,top=0,left=0'); 
    newWin.focus();
}

function openWindow2(address){ 
	newWin = window.open(address,'_pic','personalbar=0, toolbar=0,location=0,directories=0,menuBar=0,status=0,scrollBars=0, resizable=1, width=587, height = 445,top=0,left=0'); 
	newWin.focus();
}

function openWindowScroll(address){ 
	newWin = window.open(address,'_help','personalbar=0, toolbar=0,location=0,directories=0,menuBar=0,status=0,resizable=yes,scrollBars=1, resizable=1, width=800, height = 500,top=0,left=0'); 
    newWin.resizeTo(800, 500);
    newWin.focus();
}

// jump menu method

function MM_jumpMenu(targ,selObj,restore){ //v3.0
  eval(targ+".location='"+selObj.options[selObj.selectedIndex].value+"'");
  if (restore) selObj.selectedIndex=0;
}

function makemail(emailaddress){
		if(emailaddress == "support"){
			document.write("<a  target='_top'  title='Contact EBI Support' href='http://www.ebi.ac.uk/support/'>EBI Support</a>");
			loadSelects(); //////////////////////////////////////
		}
		else{
			document.write("<a href='mailto:" + emailaddress + "\@ebi.ac.uk'>" + emailaddress + "\@ebi.ac.uk</a>");
		}
}
