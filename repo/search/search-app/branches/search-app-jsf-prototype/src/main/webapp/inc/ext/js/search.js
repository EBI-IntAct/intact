

///////////////////////////////////////////////// SRS GIZMO METHODS /////////////////////////////////////////////////

function fixQueryTerm (qstr) {
	if (qstr == null) return null;
	qstr = qstr.replace(/[\@\&\|\;\>\<\[\]\(\)\{\}]+/g, "");
	qstr = qstr.replace(/\*\*+/g, "*");
	qstr = qstr.replace(/ \*$/g, "");
	qstr = qstr.replace(/ \* /g, "");
	qstr = qstr.replace(/^\* /g, "");
	qstr = qstr.replace(/\s+$/, "");
	qstr = qstr.replace(/^\s+/, "");
	qstr = qstr.replace(/\s+/g, " ");
	return qstr;
}

function makeSRSString(qstr, seperator){
	singleTerms = qstr.split(" ");
	queryFixed="";
	for (i=0; i<singleTerms.length; i++) {
		if (singleTerms[i].length > 1){
			queryFixed += escape(singleTerms[i]);
			queryFixed += "*";
			queryFixed += seperator;
		}
	}
	queryFixed = queryFixed.substr(0, queryFixed.length-1);
	return queryFixed;
}

function querySRS(db, qstr) {
	querySRSExtended(db, qstr, null);
}

// extra terms should be a comma delimited list of srs terms, e.g "swall-Keywords:complete proteome, swall-NCBI_TaxID:56636"
function querySRSExtended(db, qstr, extraterms) {
	var defview = "";
	var query = "";
	var bits;
	var i;
	var url ="";
	var view ="";
	// strip extras away from query string...
	qstr = fixQueryTerm(qstr);
	// do the same to extra terms in case anyone's doing something silly
	if (extraterms != null){
		extraterms = fixQueryTerm(extraterms);
	}
	if(!qstr || qstr == "" || qstr == "*" || qstr.length > 200) {
		void(qstr=prompt("Search for:",""));
	}
	qstr = fixQueryTerm(qstr);
	if (qstr && qstr != "" && qstr != "*" && qstr.length < 201) {
		// non-srs databases
		switch (db) {
			case 'CluSTr' : {
				window.top.location="http://www.ebi.ac.uk/clustr-srv/CReport?qstr="+qstr ;
				return;
			}
			case 'pdb_atlas' : {
				window.top.location="http://www.ebi.ac.uk/msd-srv/atlas?id="+qstr ;
				return;
			}
			case 'SQUID' : {
				window.top.location="http://www.ebi.uniprot.org/uniprot-srv/ftSearch.do?querytext=" + qstr;
				return;
			}
			case 'arrayexpress' : {
				window.top.location="http://www.ebi.ac.uk/arrayexpress/query/result?queryFor=Experiment&eDescription=" + qstr;
				return;
			}
			case 'EBI' : {
				// website search....
				window.top.location="http://www.google.com/u/ebi?hq=inurl%3Awww.ebi.ac.uk&q=" + qstr + "&sa=Go";
				return;
			}
			default : {
				break;
			}
		}
		var singleTerms = qstr.split(" ");
		switch (db) {
		// rest of function is SRS stuff...
			case 'SWALL' : 
			case 'UNIPROT' : 
			case 'Knowledgebase' :{
				query = "";
				query += "(";
				query +="([libs%3d{uniprot%20uniparc%20uniref100}-AllText:";
				query += makeSRSString(qstr, "&");
				query +="])|";
				query +="([uniprot-ID:";
				query += makeSRSString(qstr, "|");
				query +="])|";
				query +="([libs%3d{uniprot%20uniparc%20uniref100}-AccNumber:";
				query += makeSRSString(qstr, "|");
				query +="])";
				query +=")";		
				break;
			}
			/*
			case 'EMBL' :{
				query += "(";
				query +="([libs%3d{embl%20emblcon%20emblcds}-alltext:";
				query += makeSRSString(qstr, "&");
				query +="])|";
				query +="([libs%3d{embl%20emblcon%20emblcds}-id:";
				query += makeSRSString(qstr, "|");
				query +="])|";
				query +="([libs%3d{embl%20emblcon%20emblcds}-acc:";
				query += makeSRSString(qstr, "|");
				query +="])";
				query += ")";
				break;
			}
			*/
			case 'EMBL' :{
				query += "(";
				query +="([{embl%20emblcon%20emblcds}-alltext:";
				query += makeSRSString(qstr, "&");
				query +="])|";


				query += "([emblcds-id:";
				query += makeSRSString(qstr, "|");
				query +="])|";
				
				
				query += "(([emblidacc-id:";
				query += makeSRSString(qstr, "|");
				query +="])%3Eembl)|";
				
				query +="([{embl%20emblcon%20emblcds}-acc:";
				query += makeSRSString(qstr, "|");
				query +="])";
				query += ")";
				break;
			}
			case 'INTERPRO' :{
				query += "(";
				query +="([interpro-AllText:";
				query += makeSRSString(qstr, "&");
				query +="])|";
				query +="([interpro-acc:";
				query += makeSRSString(qstr, "|");
				query +="])";
				query += ")";
				break;
			}
			case 'MEDLINE' :{
				query += "(";
				query +="([medline-alltext:";
				query += makeSRSString(qstr, "&");
				query +="])|";
				query +="([medline-id:";
				query += makeSRSString(qstr, "|");
				query +="])";
				query += ")";
				break;
			}
			case 'PDB' :{
				// check for resid....
					if( (qstr.indexOf("AA") == 0) && (qstr.length == 6) && !isNaN(qstr.charAt(2)) && !isNaN(qstr.charAt(3)) && !isNaN(qstr.charAt(4)) && !isNaN(qstr.charAt(5)) ){
						query=escape(qstr);
					}
					else{
						query += "(";
						query +="([pdb-AllText:";
						query += makeSRSString(qstr, "&");
						query +="])|";
						query +="([pdb-ID:";
						query += makeSRSString(qstr, "|");
						query +="])";
						query += ")";
					}
				break;
			}
			default : {
				if (qstr.indexOf(" ") > 0) {
					bits = qstr.split(" ");
					for (i=0; i<bits.length; i++) {
						if (bits[i].length > 1) bits[i] += '*';
					}
					query = escape(bits.join("|"));
					bits = null;
				} 
				else {
					if (qstr.indexOf("*") < 0 && qstr.length > 1){
						  qstr += "*"; //no wildcard appended to end of single string any more
					}
					query = escape(qstr);	
				}
			}
		}

		// SRS database specific bits and bobs...
		var dbString="";
		switch (db) {
			case 'SWALL' : 
			case 'UNIPROT' :
			case 'Knowledgebase' :{
				//dbString = "libs%3d{UNIPROT%20UNIPARC%20UNIREF100}";
				defview = "+-view+UniprotView+-page+qResult";
				break;
			}
			case 'SPTREMBL' :{
				dbString = "sptrembl";
				defview = "+-view+UniprotView+-page+qResult";
				break;
			}
			case 'SWISSPROT' : {
				dbString = "swissprot";
				defview = "+-view+UniprotView+-page+qResult";
				break;
			}
			case 'MEDLINE' : {
				defview = "+-vn+3+-page+qResult+-w";
				break;
			}
			case 'INTERPRO' : {  
				defview = "+-view+InterproTable+-page+qResult";
				break;
			}
			
			
			case 'PDB' : {
			if( (qstr.indexOf("AA") == 0) && (qstr.length == 6) && !isNaN(qstr.charAt(2)) && !isNaN(qstr.charAt(3)) && !isNaN(qstr.charAt(4)) && !isNaN(qstr.charAt(5)) ){
				// only match 6 digit strings starting with "AA" followed by 4 digits
				db = "resid"; // change to resid database
				dbString = "resid";  // string for query
			}
			else{
				//dbString = "PDB";
			}
				defview= "+-page+qResult";
				break;
			}
			case 'EMBL' : {		
				//dbString = "libs%3d{embl%20emblcon}";
				defview = "+-view+EMBLSeqSimpleView+-page+qResult";
				//defview ="";
				break;
			}
			default : {
				dbString = db;
				defview= "+-view+SeqSimpleView+-page+qResult";
				break;
			}
		}
		//catch things if url not defines and go to old default one..
		//query = escape(query);
		if(!url){
			switch (db) {
				case 'SWALL' : 
				case 'UNIPROT' : 
				case 'EMBL' :
				case 'INTERPRO' :
				case 'MEDLINE' :
				case 'PDB' :
				case 'Knowledgebase' :{
					url = "http://srs.ebi.ac.uk/srsbin/cgi-bin/wgetz?-newId+" + query;
					break;
				}
				default : {
					url = "http://srs.ebi.ac.uk/srsbin/cgi-bin/wgetz?-newId+[" + dbString + "-AllText:" + query + "]";
					break;
				}
			}
		}
		
		if (extraterms != null) {
			singleExtraTermsArray = extraterms.split(",");
			for (i=0; i<singleExtraTermsArray.length; i++) {
				if (singleExtraTermsArray[i].length > 0) url += escape('&') + "[" + escape(singleExtraTermsArray[i]) + "]";	
			}
		}
		url += defview ;
		window.top.location=url;
	} 
	else {
		alert("Please enter a valid query.");
		for(z=0; z<document.forms.length; z++){
			if(document.forms[z].qstr){
				document.forms[z].qstr.focus();
				document.forms[z].qstr.select();
				break;
			}
		}
    } 
}

function gethelp(db) {
     if (db=='CluSTr') {
 	  	window.top.location='http://www3.ebi.ac.uk/~sp/intern/projects2/cluster/clust.html'
	 } 
	 else if (db.indexOf('EMBL')>0) {
        window.top.location='http://srs.ebi.ac.uk/srsbin/cgi-bin/wgetz?-page+LibInfo+-lib+EMBL';
     } 
	 else {
        window.top.location='http://srs.ebi.ac.uk/srsbin/cgi-bin/wgetz?-page+LibInfo+-lib+'+db;
     }
}

function gotoPrintable() {
	window.top.location = "http://www.ebi.ac.uk/cgi-bin/printable?f=" + document.location;
	return false;
}

///////////////////////////////////////////////// FOOTER METHODS /////////////////////////////////////////////////

function addKeys(){
	if  (  (document.location.href).indexOf("/2can/") < 0  ){
	
	
	/*
	0: Home Page
	1: Information
	2: Groups
	3: Services
	4: Tools
	5: Databases
	6: Downloads
	7: Submissions
	8: EBI Site Map
	9: Database Queries
	h: [help menu of browser]
	-: Services help
	+: Contact Us
	
	LEVEL 2 OF KEYBOARD</b> (Supported on some pages.)
	
	q: Choose a database to query
	w: enter a database query
	e: [edit menu of browser]
	r: submit database query
	t: [tools menu of browser]
	y: site search query term
	u: submit site search query
	*/	
		
		// temp methods, will be added to html templates in the future...
		document.getElementById("FormsComboBox2").setAttribute('accessKey', "q");
		document.getElementById("FormsEditField3").setAttribute('accessKey', "w");
		document.getElementById("FormsButton3").setAttribute('accessKey', "r");
		
		document.getElementById("FormsEditField4").setAttribute('accessKey', "y");
		document.getElementById("FormsButton2").setAttribute('accessKey', "u");
		
		document.write('<img alt="Image" src="/services/images/trans.gif" height="1" width="1" border="0" usemap="#AccessMap" />');
		document.write('<map name="AccessMap" id="AccessMap"><area accessKey="0" shape="rect" coords="0,0,0,0" href="/"    alt="Map" title="Map"    />');
		document.write('<area accessKey="1" shape="rect" coords="0,0,0,0" href="/Information/" alt="Map" title="Map" />');
		document.write('<area accesskey="2" shape="rect" coords="0,0,0,0" href="/Groups/" alt="Map" title="Map" />');
		document.write('<area accessKey="3" shape="rect" coords="0,0,0,0" href="/services/"    alt="Map" title="Map" />');
		document.write('<area accesskey="4" shape="rect" coords="0,0,0,0" href="/Tools/" alt="Map" title="Map" />');
		document.write('<area accessKey="5" shape="rect" coords="0,0,0,0" href="/Databases/"   alt="Map" title="Map" />');
		document.write('<area accesskey="6" shape="rect" coords="0,0,0,0" href="/FTP/" alt="Map" title="Map" />');
		document.write('<area accessKey="7" shape="rect" coords="0,0,0,0" href="/Submissions/" alt="Map" title="Map" />');
		document.write('<area accesskey="8" shape="rect" coords="0,0,0,0" href="/Information/sitemap.html" alt="Map" title="Map" />');
		document.write('<area accessKey="9" shape="rect" coords="0,0,0,0" href="/queries/"     alt="Map" title="Map" />');
		document.write(' <area accesskey="-" shape="rect" coords="0,0,0,0" href="/help/" alt="Map" title="Map" />');
		document.write('<area accessKey="=" shape="rect" coords="0,0,0,0" href="/support/"     alt="Map" title="Map" />');
		document.write('</map>');
	}
}


if(!emailaddress){
	var emailaddress = "support";
}

function makeothermail(emailaddress, domain, style){
			document.write("<a class='" + style + "' href='mailto:" + emailaddress + "\@" + domain + "'>" + emailaddress + "\@" + domain + "</a>");
}

function makestylemail(emailaddress, style){
		if(emailaddress == "support"){
			document.write("<a  target='_top' title='Contact EBI Support' class='" + style + "'  href='http://www.ebi.ac.uk/support/'>EBI Support</a>");
			loadSelects(); //////////////////////////////////////
		}
		else{
			document.write("<a class='" + style + "' href='mailto:" + emailaddress + "\@ebi.ac.uk'>" + emailaddress + "\@ebi.ac.uk</a>");
		}
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

function makemailby(emailaddress){
	if(emailaddress == "support"){
		document.write("Page Maintained by <a  target='_top' title='Contact EBI Support' href='http://www.ebi.ac.uk/support/'>EBI Support</a>");
	}
	else{
		document.write("Page Maintained by <a  target='_top' title='Contact EBI Support' href='http://www.ebi.ac.uk/support/index.php?query=" + emailaddress + "'>EBI Support</a>");
	}
}

function addlastmod(){
	var lastSave = document.lastModified;
	var lastSave2 = ".&nbsp;&nbsp;Last updated:&nbsp;" + lastSave; // opera hack???
	document.writeln(lastSave2);
	loadSelects(); //////////////////////////////////////
	addKeys();
}

function addlastmodandprint(){
	var lastSave = document.lastModified;
	var lastSave2 = ".&nbsp;&nbsp;Last updated:&nbsp;" + lastSave; // opera hack???
	document.writeln(lastSave2);
	var p ="<a title = \"Page with no menus for printing purposes only\" class=\"small_list\" href = \"#\" onclick = \"window.top.location='http://www.ebi.ac.uk/cgi-bin/printable?f=" + document.location + "'; return false; \"><br><img src = \"http://www.ebi.ac.uk/images/print.gif\" border = \"0\" width = \"16\" height = \"14\" align = \"absbottom\" alt = \"Page with no menus for printing purposes only\" title = \"Page with no menus for printing purposes only\"> View Printer-friendly version of this page</a><span class = \"psmall\"> | </span><a title=\"Terms of Use\"  href=\"/Information/termsofuse.html\" class=\"small_list\">Terms of Use</a><br /><br />";
	document.writeln(p);
	loadSelects(); //////////////////////////////////////
	addKeys();
}

function adduniprotlastmodandprint(){
	var lastSave = document.lastModified;
	var lastSave2 = ".&nbsp;&nbsp;Last updated:&nbsp;" + lastSave; // opera hack???
	document.writeln(lastSave2);
	document.writeln("<br>Copyright &copy;2002 - 2003, <a href='/UniProt/index.shtml'>UniProt</a> All rights reserved.");
	var p ="<a title = \"Page with no menus for printing purposes only\" class=\"small_list\" href = \"#\" onclick = \"window.top.location='http://www.ebi.ac.uk/cgi-bin/printable?f=" + document.location + "'; return false; \"><br><img src = \"http://www.ebi.ac.uk/images/print.gif\" border = \"0\" width = \"16\" height = \"14\" align = \"absbottom\" alt = \"Page with no menus for printing purposes only\" title = \"Page with no menus for printing purposes only\"> View Printer-friendly version of this page</a><span class = \"psmall\"> | </span><a title=\"Terms of Use\" href=\"/Information/termsofuse.html\" class=\"small_list\">Terms of Use</a><br /><br />";
	document.writeln(p);
}

function makecgimailby(emailaddress){
	if(emailaddress == "support"){
		document.write("Please contact <a  target='_top' title='Contact EBI Support' class=\"normaltext\" href=\"http://www.ebi.ac.uk/support\">EBI Support</a> with any problems or suggestions regarding this site.");
		var p ="<a class=\"small_list\" href = \"#\" onclick = \"window.top.location='http://www.ebi.ac.uk/cgi-bin/printable?f=" + document.location + "'; return false; \"><br><span class = \"psmall\"><img src = \"http://www.ebi.ac.uk/images/print.gif\" border = \"0\" width = \"16\" height = \"14\" align = \"absbottom\" alt = \"Page with no menus for printing purposes only\" title = \"Page with no menus for printing purposes only\"> View Printer-friendly version of this page</span></a><span class = \"psmall\"> | </span><a title=\"Terms of Use\" href=\"/Information/termsofuse.html\" class=\"small_list\">Terms of Use</a><br /><br />";
		document.writeln(p);
	}
	else{
		document.write("Please contact <a  target='_top' title='Contact EBI Support' class=\"normaltext\" href=\"http://www.ebi.ac.uk/support/index.php?query=" + emailaddress + "\">EBI Support</a> with any problems or suggestions regarding this site.");
		var p ="<br><a class=\"small_list\" href = \"#\" onclick = \"window.top.location='http://www.ebi.ac.uk/cgi-bin/printable?f=" + document.location + "'; return false; \"><br><span class = \"psmall\"><img src = \"http://www.ebi.ac.uk/images/print.gif\" border = \"0\" width = \"16\" height = \"14\" align = \"absbottom\" alt = \"Page with no menus for printing purposes only\" title = \"Page with no menus for printing purposes only\"> View Printer-friendly version of this page</span><span class = \"psmall\"> | </span><a title=\"Terms of Use\" href=\"/Information/termsofuse.html\" class=\"small_list\">Terms of Use</a><br /><br />";
		document.writeln(p);
	}
	loadSelects(); //////////////////////////////////////
	addKeys();
}

///////////////////////////////////////////////// DREAMWEAVER METHODS /////////////////////////////////////////////////

function  EbiPreloadImages(menuImages){
	switch (menuImages) {
		case 'services' :
		case 'toolbox' :
		case 'databases' :
		case 'submissions' :
		case 'downloads' : {
			MM_preloadImages('/services/images/home_o.gif','/services/images/about_o.gif','/services/images/databases_o.gif','/services/images/utilities_o.gif','/services/images/submissions_o.gif','/services/images/research_o.gif','/services/images/downloads_o.gif','/services/images/services_o.gif');
			break;
		}
		case 'research' : {
			MM_preloadImages('/Groups/images/home_o.gif','/Groups/images/about_o.gif','/Groups/images/databases_o.gif','/Groups/images/utilities_o.gif','/Groups/images/submissions_o.gif','/Groups/images/research_o.gif','/Groups/images/downloads_o.gif','/Groups/images/services_o.gif');
			break;
		}
		case 'information' : {
			MM_preloadImages('/Information/images/home_o.gif','/Information/images/about_o.gif','/Information/images/databases_o.gif','/Information/images/utilities_o.gif','/Information/images/submissions_o.gif','/Information/images/research_o.gif','/Information/images/downloads_o.gif','/Information/images/services_o.gif');
			break;
		}
		case '2can' : {
			MM_preloadImages('/2can/bioinformatics/images/home_o.gif','/2can/images/about_o.gif','/2can/images/databases_o.gif','/2can/images/utilities_o.gif','/2can/images/submissions_o.gif','/2can/images/research_o.gif','/2can/images/downloads_o.gif','/2can/images/services_o.gif');
			break;
		}
		case 'training' : {
			MM_preloadImages('http://www.ebi.ac.uk/training/images/home_o.gif','http://www.ebi.ac.uk/training/images/about_o.gif','http://www.ebi.ac.uk/training/images/databases_o.gif','http://www.ebi.ac.uk/training/images/utilities_o.gif','http://www.ebi.ac.uk/training/images/submissions_o.gif','http://www.ebi.ac.uk/training/images/research_o.gif','http://www.ebi.ac.uk/training/images/downloads_o.gif','http://www.ebi.ac.uk/training/images/images/services_o.gif');
			break;
		}
		default : {
			MM_preloadImages('/services/images/home_o.gif','/services/images/about_o.gif','/services/images/databases_o.gif','/services/images/utilities_o.gif','/services/images/submissions_o.gif','/services/images/research_o.gif','/services/images/downloads_o.gif','/services/images/services_o.gif');
		}
	}
}

function MM_swapImgRestore() { //v3.0
  var i,x,a=document.MM_sr; for(i=0;a&&i<a.length&&(x=a[i])&&x.oSrc;i++) x.src=x.oSrc;
}

function MM_preloadImages() { //v3.0
  var d=document; if(d.images){ if(!d.MM_p) d.MM_p=new Array();
    var i,j=d.MM_p.length,a=MM_preloadImages.arguments; for(i=0; i<a.length; i++)
    if (a[i].indexOf("#")!=0){ d.MM_p[j]=new Image; d.MM_p[j++].src=a[i];}}
}

function MM_findObj(n, d) { //v4.0
  var p,i,x;  if(!d) d=document; if((p=n.indexOf("?"))>0&&parent.frames.length) {
    d=parent.frames[n.substring(p+1)].document; n=n.substring(0,p);}
  if(!(x=d[n])&&d.all) x=d.all[n]; for (i=0;!x&&i<d.forms.length;i++) x=d.forms[i][n];
  for(i=0;!x&&d.layers&&i<d.layers.length;i++) x=MM_findObj(n,d.layers[i].document);
  if(!x && document.getElementById) x=document.getElementById(n); return x;
}

function MM_swapImage() { //v3.0
  var i,j=0,x,a=MM_swapImage.arguments; document.MM_sr=new Array; for(i=0;i<(a.length-2);i+=3)
   if ((x=MM_findObj(a[i]))!=null){document.MM_sr[j++]=x; if(!x.oSrc) x.oSrc=x.src; x.src=a[i+2];}
}

function MM_jumpMenu(targ,selObj,restore){ 
//alert(selObj.options[selObj.selectedIndex].value);
  if(selObj.options[selObj.selectedIndex].value != "")
  {
  	eval(targ+".location='"+selObj.options[selObj.selectedIndex].value+"'");
  	if (restore) selObj.selectedIndex=0;
  }
}

///////////////////////////////////////////////// OVERRIDE SEARCH GIZMO METHODS /////////////////////////////////////////////////

//document.onload=loadSelects();

var gizmoSelectArray=new Array(1);

function loadSelects(){
// dont delete...hack in extra options here when political climate swings...

	if(document.Text1293FORM){
		if(document.location.href.indexOf("ebi.ac.uk/msd") == -1){
			gizmoSelectArray[0]=new Option("Nucleotide sequences", "EMBL");
			gizmoSelectArray[1]=new Option("Protein sequences", "SQUID");
			gizmoSelectArray[2]=new Option("Protein structures", "PDB");
			gizmoSelectArray[3]=new Option("Protein signatures", "INTERPRO");
			gizmoSelectArray[4]=new Option("Literature", "MEDLINE");
			gizmoSelectArray[5]=new Option("Protein seq's [SRS]", "UNIPROT");
			gizmoSelectArray[6]=new Option("ArrayExpress", "arrayexpress");
		}
		else{
			gizmoSelectArray[0]=new Option("Nucleotide sequences", "EMBL");
			gizmoSelectArray[1]=new Option("Protein sequences", "SQUID");
			gizmoSelectArray[2]=new Option("Protein structures", "PDB");
			gizmoSelectArray[3]=new Option("PDB by ID", "pdb_atlas");
			gizmoSelectArray[4]=new Option("Protein signatures", "INTERPRO");
			gizmoSelectArray[5]=new Option("Literature", "MEDLINE");
			gizmoSelectArray[6]=new Option("Protein seq's [SRS]", "UNIPROT");
			gizmoSelectArray[7]=new Option("ArrayExpress", "arrayexpress");
		}
		var i=0;
		var itemsToRemove= document.Text1293FORM.db.options.length;
		for (i=itemsToRemove-1; i>=0; i--){
			document.Text1293FORM.db.options[i]=null; // delete options
		}
		for (i=0; i<gizmoSelectArray.length; i++){
			document.Text1293FORM.db.options[i] = new Option(gizmoSelectArray[i].text, gizmoSelectArray[i].value);  // add new options
		}
		if(document.location.href.indexOf("ebi.ac.uk/msd") == -1){
			document.Text1293FORM.db.options[0].selected=true;   //select first option
		}
		else{
			document.Text1293FORM.db.options[3].selected=true;   //PDB by ID
		}
	}

}

function confirmSubmit(){
	var agree=confirm("Are you sure you wish to continue?");
	if (agree){
		return true ;
	}
	else{
		return false ;
	}
}

///////////////////////////////////////////////// OPEN WINDOW METHODS /////////////////////////////////////////////////

var qstr = "empty";
var newWin;

function openWindow(address)
{ 
	newWin = window.open(address,'_help','personalbar=0, toolbar=0,location=0,directories=0,menuBar=0,status=0,resizable=yes,scrollBars=0, resizable=1, width=800, height = 500,top=0,left=0'); 
    newWin.resizeTo(800, 500);
    newWin.focus();
}

function openWindow2(address)
{ 
	newWin = window.open(address,'_pic','personalbar=0, toolbar=0,location=0,directories=0,menuBar=0,status=0,scrollBars=0, resizable=1, width=587, height = 445,top=0,left=0'); 
	newWin.focus();
}

function openWindow2can(address)
{ 
	newWin = window.open(address,'_help','personalbar=0, toolbar=0,location=0,directories=0,menuBar=0,status=0,resizable=yes,scrollBars=1, resizable=1, width=450, height = 125,top=0,left=0'); 
    newWin.resizeTo(450, 125);
    newWin.focus();
}

function openWindowScroll(address)
{ 
	newWin = window.open(address,'_help','personalbar=0, toolbar=0,location=0,directories=0,menuBar=0,status=0,resizable=yes,scrollBars=1, resizable=1, width=800, height = 500,top=0,left=0'); 
    newWin.resizeTo(800, 500);
    newWin.focus();
}

function openWindowMenus(address)
{
	newWin = window.open(address,'_link','personalbar=1, toolbar=1,location=1,directories=1,menuBar=1,status=1,resizable=yes,scrollBars=1, resizable=1, width=800, height = 500,top=0,left=0');
    newWin.resizeTo(800, 500);
    newWin.focus();
}



function MM_jumpMenu(targ,selObj,restore)
{ 
  if(selObj.options[selObj.selectedIndex].value != "")
  {
  eval(targ+".location='"+selObj.options[selObj.selectedIndex].value+"'");
  if (restore) selObj.selectedIndex=0;
  }
}






/***********************************************************************************
*	(c) Ger Versluis 2000 version 5.411 24 December 2001 (updated Jan 31st, 2003 by Dynamic Drive for Opera7)
*	For info write to menus@burmees.nl		          *
*	You may remove all comments for faster loading	          *		
***********************************************************************************/

	var NoOffFirstLineMenus=9;			// Number of first level items
	
	var LowBgColor='#CECFCE';			// Background color when mouse is not over
	var LowSubBgColor='#CECFCE';			// Background color when mouse is not over on subs
	var HighBgColor='#628888';			// Background color when mouse is over
	var HighSubBgColor='#628888';			// Background color when mouse is over on subs
	
	var FontLowColor='black';			// Font color when mouse is not over
	var FontSubLowColor='black';			// Font color subs when mouse is not over
	var FontHighColor='white';			// Font color when mouse is over
	var FontSubHighColor='white';			// Font color subs when mouse is over
	
	var BorderColor='#eeeeee';			// Border color
	var BorderSubColor='#eeeeee';			// Border color for subs
	var BorderWidth=2;				// Border width
	var BorderBtwnElmnts=1;			// Border between elements 1 or 0
	
	var FontFamily="arial,comic sans ms,technical"	// Font family menu items
	var FontSize=9;				// Font size menu items
	var FontBold=1;				// Bold menu items 1 or 0
	var FontItalic=0;				// Italic menu items 1 or 0
	var MenuTextCentered='center';			// Item text position 'left', 'center' or 'right'
	var MenuCentered='left';			// Menu horizontal position 'left', 'center' or 'right'
	var MenuVerticalCentered='top';		// Menu vertical position 'top', 'middle','bottom' or static
	var ChildOverlap=.2;				// horizontal overlap child/ parent
	var ChildVerticalOverlap=.2;			// vertical overlap child/ parent
	var StartTop=64;				// Menu offset x coordinate
	var StartLeft=0;				// Menu offset y coordinate
	var VerCorrect=0;				// Multiple frames y correction
	var HorCorrect=0;				// Multiple frames x correction
	var LeftPaddng=3;				// Left padding
	var TopPaddng=2;				// Top padding
	var FirstLineHorizontal=1;			// SET TO 1 FOR HORIZONTAL MENU, 0 FOR VERTICAL
	var MenuFramesVertical=1;			// Frames in cols or rows 1 or 0
	var DissapearDelay=1000;			// delay before menu folds in
	var TakeOverBgColor=1;			// Menu frame takes over background color subitem frame
	var FirstLineFrame='navig';			// Frame where first level appears
	var SecLineFrame='space';			// Frame where sub levels appear
	var DocTargetFrame='space';			// Frame where target documents appear
	var TargetLoc='';				// span id for relative positioning
	var HideTop=0;				// Hide first level when loading new document 1 or 0
	var MenuWrap=1;				// enables/ disables menu wrap 1 or 0
	var RightToLeft=0;				// enables/ disables right to left unfold 1 or 0
	var UnfoldsOnClick=0;			// Level 1 unfolds onclick/ onmouseover
	var WebMasterCheck=0;			// menu tree checking on or off 1 or 0
	var ShowArrow=1;				// Uses arrow gifs when 1
	var KeepHilite=1;				// Keep selected path highligthed
	var Arrws=['menu_files/tri.gif',5,10,'http://www.ebi.ac.uk/services/images/trans.gif',10,5,'menu_files/trileft.gif',5,10];	// Arrow source, width and height

function BeforeStart(){return}
function AfterBuild(){return}
function BeforeFirstOpen(){return}
function AfterCloseAll(){return}


function  EbiPreloadMenu(menuImages){
	switch (menuImages) {
		case 'services' :
		case 'toolbox' :
		case 'databases' :
		case 'submissions' :
		case 'downloads' : 
		case 'research' : 
		case 'information' : 
		case 'training' : {
	document.write('<script type="text/javascript">');
	document.write('		function Go(){return}');
	document.write('	</script>');
	document.write('	<script type="text/javascript" src="menu_files/exmplmenu_var.js"></script>');
	document.write('	<script type="text/javascript" src="menu_files/menu_com.js"></script>');
			break;
		}
		default : {
			break;	
		}
	}



	switch (menuImages) {
		case 'services' :
		case 'toolbox' :
		case 'databases' :
		case 'submissions' :
		case 'downloads' : {
			LowBgColor='#CECFCE';
			LowSubBgColor='#CECFCE';
			HighBgColor='#628888';
			HighSubBgColor='#628888';
			FontLowColor='#000000';
			FontSubLowColor='#000000';
			FontHighColor='#ffffff';
			FontSubHighColor='#ffffff';
			break;
		}
		case 'research' : {
			LowBgColor='#CECFCE';
			LowSubBgColor='#CECFCE';
			HighBgColor='#628888';
			HighSubBgColor='#628888';
			FontLowColor='#000000';
			FontSubLowColor='#000000';
			FontHighColor='#ffffff';
			FontSubHighColor='#ffffff';
			break;
		}
		case 'information' : {
			LowBgColor='#CECFCE';
			LowSubBgColor='#CECFCE';
			HighBgColor='#628888';
			HighSubBgColor='#628888';
			FontLowColor='#000000';
			FontSubLowColor='#000000';
			FontHighColor='#ffffff';
			FontSubHighColor='#ffffff';
			break;
		}
		case 'training' : {
			LowBgColor='#CECFCE';
			LowSubBgColor='#CECFCE';
			HighBgColor='#628888';
			HighSubBgColor='#628888';
			FontLowColor='#000000';
			FontSubLowColor='#000000';
			FontHighColor='#ffffff';
			FontSubHighColor='#ffffff';
			break;
		}
		default : {
			break;	
		}
	}
}

















