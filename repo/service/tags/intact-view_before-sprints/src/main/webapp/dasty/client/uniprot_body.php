<!-- START CHECKING -->
<a id="menu_checking" style="display:block; text-decoration:none;" class="maintitle" href="javascript:changeDisplayState('display_checking_div', 'menu_checking_img', 2)">
	<span id="menu_checking_img"><img src="img/plus02.gif" border="0" align="absbottom">&nbsp;</span>
    CHECKING
</a>
<div id="display_checking_div" class="divformat" style="display:block">
    <div id="display_checking">
	<table border="0" cellspacing="0" cellpadding="0">
  	<tr>
   	    <td  valign="top">
			<span style="display:block; border-bottom: 1px solid #000000; font-weight:bold; color:#222222; margin-bottom: 3px;">
				<a style="text-decoration:none;" href="javascript:changeDisplayState('display_server_checking_div', 'menu_server_checking_img', 1)">
					<span id="menu_server_checking_img"><img src="img/plus01.gif" border="0" align="absbottom">&nbsp;</span>
					Annotation servers loaded:
				</a>
			</span>
			<div id="progress_bar_empty" style="background-color:#F0F0F0;border:1px solid #999999;height:12px;width:267px;padding:0px;" align="left">
			<div id="progress_bar_2" style="position:relative;top:0px;left:0px;background-color:#B4B4B4;height:12px;width:0px;padding-top:5px;padding:0px;">
			<div id="progress_bar_1" style="position:relative;top:0px;left:0px;color:#f0ffff;height:12px;text-align:center;font:bold;padding:0px;padding-top:0px;">			</div></div>
	  </div>
	    </td>
		<td width="5" valign="top">&nbsp;
			
	    </td>
		<td valign="bottom">
			<input type="button" name="cancel_button" id="cancel_button" value="Cancel" onClick="stop()"/>
	    </td>
		<td width="30" valign="top">&nbsp;
			
	    </td>
	    <td width="390" valign="top">
			<span style="display:block; border-bottom: 1px solid #000000; font-weight:bold; color:#222222; margin-bottom: 5px;">
			System information:</span>
			<span style="font-style:italic;" id="system_information">... please be patient, Dasty2 is loading data from the DAS servers</span>								        </td>
  	</tr>
	</table>
		<div id="display_server_checking_div" class="divformat" style="display:none">
        	<div id="display_server_checking"></div>
        </div>
	</div>
</div>
<!-- END CHECKING -->

<!-- START FEATURE DETAILS -->	
<a id="menu_feature_details" style="display:none; text-decoration:none;" class="maintitle" href="javascript:changeDisplayState('display_feature_details_div', 'menu_feature_details_img', 2)">
	<span id="menu_feature_details_img"><img src="img/plus02.gif" border="0" align="absbottom">&nbsp;</span>
    FEATURES DETAILS
</a>
<div id="display_feature_details_div" class="divformat" style="display:none">
    <div id="display_feature_details">
    <span class="title" style="font-style:italic;">
	... to see features details click on one feature in the graphic display!
    </span>
    </div>
</div>
<!-- END FEATURE DETAILS -->	   

<!-- START FILTERING OPTIONS -->	
<a id="menu_maniputation_options3" style="display:block; text-decoration:none;" class="maintitle" href="javascript:changeDisplayState('display_maniputation_options3_div', 'menu_maniputation_options3_img', 2)">
	<span id="menu_maniputation_options3_img"><img src="img/plus02.gif" border="0" align="absbottom" />&nbsp;</span>
    FILTERING OPTIONS
</a>
<div id="display_maniputation_options3_div" class="divformat" style="display:none">
    <table border="0" cellspacing="0" cellpadding="0">
      <tr>
        <td width="350px" valign="top">
    <span style="color:#CCCCCC;">Click checkbox to hide/show features</span>    
    <a id="menu_maniputation_options3_type" style="display:block; text-decoration:none;" class="maintitle" href="javascript:changeDisplayState('display_maniputation_options3_type_div', 'menu_maniputation_options3_type_img', 2)"> <span id="menu_maniputation_options3_type_img"><img src="img/plus02.gif" border="0" align="absbottom" />&nbsp;</span>FEATURE TYPES</a>    
    <div id="display_maniputation_options3_type_div" style="display:block">
        <span class="title" style="font-style:italic;">
        ... please wait untill all the data is loaded in Dasty2.
        </span>
    </div>    
        </td>
        <td width="350px" valign="top">
    <span style="color:#CCCCCC;">Click checkbox to hide/show features</span>  
    <a id="menu_maniputation_options3_server" style="display:block; text-decoration:none;" class="maintitle" href="javascript:changeDisplayState('display_maniputation_options3_server_div', 'menu_maniputation_options3_server_img', 2)"> <span id="menu_maniputation_options3_server_img"><img src="img/plus02.gif" border="0" align="absbottom" />&nbsp;</span>SERVER NAME</a>        
    <div id="display_maniputation_options3_server_div" style="display:block">
        <span class="title" style="font-style:italic;">
        ... please wait untill all the data is loaded in Dasty2.
        </span>
    </div>    
        </td>
        <td width="350px" valign="top">
    <span style="color:#CCCCCC;">Click checkbox to hide/show features</span>  
    <a id="menu_maniputation_options3_category" style="display:block; text-decoration:none;" class="maintitle" href="javascript:changeDisplayState('display_maniputation_options3_category_div', 'menu_maniputation_options3_category_img', 2)"> <span id="menu_maniputation_options3_category_img"><img src="img/plus02.gif" border="0" align="absbottom" />&nbsp;</span>EVIDENCE (Category)</a>        
    <div id="display_maniputation_options3_category_div" style="display:block">
        <span class="title" style="font-style:italic;">
        ... please wait untill all the data is loaded in Dasty2.
        </span>
    </div>    
        </td>
      </tr>
    </table>
</div>
<!-- END FILTERING OPTIONS -->

<!-- START MANIPULATION OPTIONS -->
<a id="menu_maniputation_options" style="display:block; text-decoration:none;" class="maintitle" href="javascript:changeDisplayState('display_maniputation_options_div', 'menu_maniputation_options_img', 2)">
	<span id="menu_maniputation_options_img"><img src="img/plus02.gif" border="0" align="absbottom">&nbsp;</span>
    MANIPULATION OPTIONS  (Positional features)
</a>
<div id="display_maniputation_options_div" class="divformat" style="display:none"> 
    <div id="display_maniputation_options">
	  <table border="0" cellspacing="0" cellpadding="0">
        <tr>
          <td width="200" valign="top">
		  <div style="display:block;">
		    <a style="text-decoration:none; font-size: 8pt;" href="javascript:define_graphic_column('type')">
		      <span id="menu_mo_img_type_column"><img src="img/tick01.gif" border="0" align="absbottom">&nbsp;</span>
		      <span class="columnStyle">Type column</span> </a><br />
		    </div>
		  <div style="display:block;">
		    <a style="text-decoration:none; font-size: 8pt;" href="javascript:define_graphic_column('server')">
		      <span id="menu_mo_img_server_column"><img src="img/tick01.gif" border="0" align="absbottom">&nbsp;</span>
		      <span class="columnStyle">Server column</span>			</a><br />
		    </div>
	  	  <div style="display:block;">
		    <a style="text-decoration:none; font-size: 8pt;" href="javascript:define_graphic_column('category')">
		      <span id="menu_mo_img_category_column"><img src="img/tick01.gif" border="0" align="absbottom">&nbsp;</span>
		      <span class="columnStyle">Category column</span> </a><br />
		    </div>
		  <div id="id_column" style="display:none;">	
		    <a style="text-decoration:none; font-size: 8pt;" href="javascript:define_graphic_column('id')">
		      <span id="menu_mo_img_id_column"><img src="img/tick01.gif" border="0" align="absbottom">&nbsp;</span>
		      Id column		      </a><br />
		    </div>
		  <br />
		  <div style="display:block;">	
		    <a style="text-decoration:none; font-size: 8pt;" href="javascript:define_popups()">
		      <span id="menu_mo_img_popups"><img src="img/notick01.gif" border="0" align="absbottom">&nbsp;</span>
		      <span class="columnStyle">Retain pop-up windows<br />
	          </span>			</a>
		    </div>
		  <div style="display:none;">	
		    <a style="text-decoration:none; font-size: 8pt;" href="javascript:expanding()">
		      <span id="menu_mo_img_expand"><img src="img/tick01.gif" border="0" align="absbottom">&nbsp;</span>
	      Ungroup features		      </a>		    </div></td>
          <td width="150" valign="top">
		  <span style="display:block; border-bottom: 1px solid #000000; font-weight:bold; color:#222222; margin-bottom: 3px;">- Change Graphic width</span>
		  <table border="0" cellspacing="0" cellpadding="1">
            <tr>
              <td>
              <input id="graphic_width_px" class="textbox" style="width: 40px;" name="graphic_width_px" type="text" value="0"/>px&nbsp;</td>
              <td valign="bottom"><input name="resize" type="submit" id="resize" value="resize" onClick="change_graphic_width('graphic_width_px')" /></td>
            </tr>
          </table>
		  <br />
		  <span style="display:block; border-bottom: 1px solid #000000; font-weight:bold; color:#222222; margin-bottom: 3px;">- Zoom</span>
		  <table border="0" cellspacing="0" cellpadding="1">
            <tr>
              <td>Start:
              <input id="zoom_start_px" class="textbox" style="width: 40px;" name="zoom_start_px" type="text" value=""/></td>
              <td>End:
              <input id="zoom_end_px" class="textbox" style="width: 40px;" name="zoom_end_px" type="text" value=""/></td>
              <td valign="bottom"><input name="zoom" type="submit" id="zoom" value="zoom" onClick="zoom('zoom_start_px', 'zoom_end_px')" /></td>
			  <td valign="bottom"><input name="reset" type="submit" id="reset" value="reset" onClick="resetZoom('zoom_start_px', 'zoom_end_px')" /></td>
            </tr>
          </table>		  </td>	
        </tr>
      </table>
	</div>
</div>
<!-- END MANIPULATION OPTIONS -->	

<!-- START POSITIONAL FEATURES -->	
<a id="menu_graphic" style="display:block; text-decoration:none;" class="maintitle" href="javascript:changeDisplayState('display_graphic_div', 'menu_graphic_img', 2)">
	<span id="menu_graphic_img"><img src="img/plus02.gif" border="0" align="absbottom">&nbsp;</span>
    POSITIONAL FEATURES
</a>
<div id="display_graphic_div" class="divformat" style="display:block">
    <div id="display_graphic"></div>
    <div class="divformat" style="display:block">
		<img src="img/checkmark.gif" align="absbottom"> The annotation is in accordance with the version of the protein sequence.<br />
		<img src="img/warning.gif" align="absbottom"> Caution! The annotation may refer to an old version of the protein sequence, so the position of features may be incorrect.<br />
		<img src="img/group.gif" align="absbottom"> Group of features classified by the annotation server.<br />
		<img src="img/group2.gif" align="absbottom"> Features grouped in the same line by Dasty2.
	</div>
</div>
<!-- END POSITIONAL FEATURES -->	

<!-- START NON POSITIONAL FEATURES -->	
<a id="menu_nonpositional" style="display:block; text-decoration:none;" class="maintitle" href="javascript:changeDisplayState('display_nonpositional_div', 'menu_nonpositional_img', 2)">
	<span id="menu_nonpositional_img"><img src="img/plus02.gif" border="0" align="absbottom">&nbsp;</span>
    NON POSITIONAL FEATURES
</a>
<div id="display_nonpositional_div" class="divformat" style="display:none">
    	<div id="display_nonpositional"></div>
</div>
<!-- END NON POSITIONAL FEATURES -->		
    
<!-- START SEQUENCE -->
<a id="menu_seque" style="display:block; text-decoration:none;" class="maintitle" href="javascript:changeDisplayState('display_seque_div', 'menu_seque_img', 2)">
	<span id="menu_seque_img"><img src="img/plus02.gif" border="0" align="absbottom">&nbsp;</span>
    SEQUENCE
</a>
<div id="display_seque_div" class="divformat" style="display:block">
    <div id="display_seque"></div>
        
        <!-- START - QUERY INFORMATION -->	
		<a id="menu_query" style="display:none; text-decoration:none;" class="maintitle" href="javascript:changeDisplayState('display_query_div', 'menu_query_img', 2)">
			<span id="menu_query_img"><img src="img/plus02.gif" border="0" align="absbottom">&nbsp;</span>
        	QUERY INFORMATION
        </a>
    	<div id="display_query_div" style="display:block; margin-top:3px;">
    		<div id="display_query"></div>
    	</div>
		<!-- END - QUERY INFORMATION --> 
        
</div>
<!-- END SEQUENCE -->    
    
<!-- START 3D STRUCTURE -->	
<a id="menu_protstru" style="display:block; text-decoration:none;" class="maintitle" href="javascript:changeDisplayState('display_protstru', 'menu_protstru_img', 2)">
	<span id="menu_protstru_img"><img src="img/plus02.gif" border="0" align="absbottom">&nbsp;</span>
    PROTEIN STRUCTURE
</a>
<div id="display_protstru" style="display:block;">
    <span style="color:#999999;">&nbsp;
    	<a style="text-decoration:none;color:#999999;" href="javascript:pdbOnWindow('display_protstru_div')">Convert this section in a pop-up window</a>
    </span>
      <div id="display_protstru_div" style="width:350px; background:#FFFFFF">
      <span class="title" style="font-style:italic;">
      <br>
	&nbsp;&nbsp;... Dasty2 is looking for PDB data.
	  </span>
      <br>&nbsp;
      </div>         
</div>
<!-- END 3D STRUCTURE -->	

<!-- START SEARCH -->	
<a id="menu_query_box" style="display:block; text-decoration:none;" class="maintitle" href="javascript:changeDisplayState('display_query_box_div', 'menu_query_box_img', 2)">
	<span id="menu_query_box_img"><img src="img/plus02.gif" border="0" align="absbottom">&nbsp;</span>
    SEARCH
</a>
<div id="display_query_box_div" class="divformat" style="display:block">
    <div id="display_query_box">
	<table border="0" cellspacing="0" cellpadding="0">
      <tr>
        <td>Protein ID:
			<input id="feature_id_box" class="textbox" style="width: 80px;" name="feature_id_box" type="text" value=""/>
			 &nbsp;&nbsp;&nbsp;&nbsp;
		</td>
        <td><div id="feature_label_list"></div>
		</td>
        <td>
        &nbsp;&nbsp;&nbsp;&nbsp;<a style="text-decoration: none;" href="http://www.dasregistry.org/help_coordsys.jsp" target="_blank"><img src="img/info01.gif" border="0" /></a>
        "<a href="http://www.dasregistry.org/coordsys/CS_DS6">UniProt</a>" protein sequence <a href="http://www.dasregistry.org/help_coordsys.jsp">coordinate system</a>&nbsp;&nbsp;&nbsp;&nbsp;        </td>
        <td><input name="send_query" type="submit" id="send_query" value="Go" onClick="newid('feature_id_box', 'feature_label_list_select')" /></td>
      </tr>
    </table><br />
	<span class="title"><i>Examples:</i>
		<a style="font-weight:normal; text-decoration:none;" href="javascript:createDastyURLNewID('P05067')"> P05067</a>,
		<a style="font-weight:normal; text-decoration:none;" href="javascript:createDastyURLNewID('P03973')"> P03973</a>,
		<a style="font-weight:normal; text-decoration:none;" href="javascript:createDastyURLNewID('P13569')"> P13569</a>,
		<a style="font-weight:normal; text-decoration:none;" href="javascript:createDastyURLNewID('MDM2_MOUSE')"> MDM2_MOUSE</a>,
		<a style="font-weight:normal; text-decoration:none;" href="javascript:createDastyURLNewID('BRCA1_HUMAN')"> BRCA1_HUMAN</a>, ...</span>
	</div>
</div>
<!-- END SEARCH -->
	
<!-- START TEST -->	
<a id="menu_test" style="display:none; text-decoration:none;" class="maintitle" href="javascript:changeDisplayState('display_test_div', 'menu_test_img', 2)">
	<span id="menu_test_img"><img src="img/plus02.gif" border="0" align="absbottom">&nbsp;</span>
    TEST
</a>
<div id="display_test_div" class="divformat" style="display:none">
    <div id="display_test"></div>
</div>
<!-- END TEST -->	