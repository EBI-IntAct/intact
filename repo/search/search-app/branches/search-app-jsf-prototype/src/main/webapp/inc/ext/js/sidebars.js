
document.onload=activateLeftNav();

function process(sidelis){
	for (i=0; i<sidelis.length; i++) {
		if(sidelis[i].childNodes){
			if(sidelis[i].childNodes.length>2){				
				sidelis[i].style.cursor="pointer";
				if(sidelis[i].className != "clickmeopen"){					
					sidelis[i].className="clickme";
				}
				else{
					if(sidelis[i].childNodes[2].style){
						sidelis[i].childNodes[2].style.display="block";
						sidelis[i].childNodes[2].style.margin="0";
						sidelis[i].childNodes[2].className="clickmeopen";
					}
				}
				sidelis[i].childNodes[0].onclick=function(ev) {
					if(   (window.event) && ((navigator.userAgent.indexOf("Safari") == -1))  ){
						cancelEvent(ev);
					}
					else{
						ev.stopPropagation();
					}	
				}
				sidelis[i].onclick=function(ev) {	
					if(this.childNodes[2].style){
						if(this.childNodes[2].style.display=="block"){
							this.childNodes[2].style.display="none";
							this.className="clickme";
						}				
						else{
							this.childNodes[2].style.display="block";
							this.childNodes[2].style.margin="0";
							this.className="clickmeopen";
						}
					}
					if(   (window.event) && ((navigator.userAgent.indexOf("Safari") == -1))  ){
							cancelEvent(ev);
					}
					else{
						ev.stopPropagation();
					}
				}
			} 
			else {
				sidelis[i].style.cursor="default";
				sidelis[i].onclick=function(ev) {
					if(   (window.event) && ((navigator.userAgent.indexOf("Safari") == -1))  ){
						cancelEvent(ev);
					}
					else{
						ev.stopPropagation();
					}
				}
			}
		}
	}
}

function activateLeftNav(){
	if(!document.layers){
		parent.document.getElementById('leftmenu').style.display="block"; // make param avail...
		var sidenavroot = document.getElementById('sidemenuid');
		if(sidenavroot){
			var sidelis=sidenavroot.getElementsByTagName("LI"); 
			process(sidelis); 	
			if(navigator.userAgent.indexOf("Safari") != -1){
				var safari_sidenavroot = document.getElementById('sidemenuid');
				var safari_ancs=safari_sidenavroot.getElementsByTagName("A");  	
				for (i=0; i<safari_ancs.length; i++) {
					safari_ancs[i].onclick=function(ev) {	
							document.location=this.href;
					}
				}
			}
		}
	}
}

function expandAllLeftmenu(){
	var sidenavroot = document.getElementById('sidemenuid');
	var sidelis=sidenavroot.getElementsByTagName("LI");  
	for (i=0; i<sidelis.length; i++) {
		if(sidelis[i].childNodes.length>2){
			sidelis[i].childNodes[2].style.display="block";
			sidelis[i].childNodes[2].style.margin="0";
			sidelis[i].className="clickmeopen";
		}
	}
}

function closeAllLeftmenu(){
	var sidenavroot = document.getElementById('sidemenuid');
	var sidelis=sidenavroot.getElementsByTagName("LI");  
	for (i=0; i<sidelis.length; i++) {
		if(sidelis[i].childNodes.length>2){
			sidelis[i].childNodes[2].style.display="none";
			sidelis[i].className="clickme";
		}
	}	
}

function cancelEvent(e){
	if (!e) var e = window.event;
	e.cancelBubble = true;
	if (e.stopPropagation) e.stopPropagation();
} 	


