#!/usr/bin/python2.4
import sys, os
import fileinput, string

# script transforming a standard obo file (input) into a more specific obo format called '4intact'
# xref analog  are used to specify alias type of synonyms and annotation topic(attribute name)of annotations
# obsolete and url are extracted from definition and report them as xref analog annotation of type obsolete and url
# xref qualifier are specified for each xref

input= open('D:/luisa/ebi-luisa/PSI-IMEX/master_cv_copy/psi-mi25.obo','r')
output= open('D:/luisa/ebi-luisa/PSI-IMEX/master_cv_copy/psi-mi25-4intact.obo','w')
inputt=input.readlines()
printFlag=1
for k in range(0,len(inputt)) :
    line=inputt[k]
    line=line.strip()
    if line[0:6] == 'name: ':
        name=line[6:]
        printFlag = 0
        xref_analog=[]  # empty list to store reformat xref analog and write them AFTER the definition (dag edit crash if different order)
        output.write(line+'\n')
    elif line[0:5] == 'def: ' :      
        pezzi=line[5:].split('" [')
        text=pezzi[0]
        l=text.find('OBSOLETE')
        j=text.find('http')
        if l != -1 :            
            xrefa=''
            xrefa='xref_analog: obsolete:'+text[l:]+ ' "ANNOTATION"'
            defi=text[0:(l-2)]  # -2 necessary to remove the \n before 'OBSOLETE'
            xref_analog.append(xrefa)
        elif j != -1 :
            defi=text[0:(j-2)] # -2 necessary to remove the \n before 'http'
            urls=text[j:].split('http')
            for every in urls[1:] :                
                if every.strip()[-3] == '.' :   # when more than one url within the same definition they are separated by '.\n'
                    every=every[:-3]            # remove the '.\n' 
                xrefa=''
                xrefa='xref_analog: url:http'+every+ ' "ANNOTATION"'                
                xref_analog.append(xrefa)
        else :
            defi=text
        defRefs=[]
        defRefs=pezzi[1][0:-1].split(',')
        newDefRefs=''
        i=0
        resid_count=pezzi[1].count('RESID')
        
        if defRefs[0][0:5] == 'PMID:' :
            newDefRefs=defRefs[0]+' "primary-reference"' # assign to the FIRST PMID ref the qualifier 'primary-reference'
            i=1
        elif (defRefs[0][0:3] == 'GO:') or (defRefs[0][0:3] == 'SO:') or (defRefs[0][0:6] == 'RESID:'): # and resid_count == 1) :              
            newDefRefs=defRefs[0]+' "identity"'
            i=1
            print pezzi[1]
            print defRefs[1]
            if defRefs[1][1:6] == 'PMID:' :
                
                newDefRefs=newDefRefs+', ' +defRefs[1][1:]+' "primary-reference"' # in ptm terms assign to the first PMID ref the qualifier 'primary-reference'
                i=2       
        if len(defRefs) > i :            
            for each in defRefs[i:] :
                if each[0] == ' ':
                    each=each[1:]
                if each[0:5] == 'PMID:' :           # in other PMID has the qualifier 'method reference'
                    newDefRefs=newDefRefs+', '+each+' "method reference"'
                elif each [0:6] == 'RESID:' and resid_count == 1:
                    newDefRefs=newDefRefs+', '+each+' "identity"' # xref to resid has a qualifier 'identity' only when is unique                    
                elif each [0:6] == 'RESID:' and resid_count > 1:
                    newDefRefs=newDefRefs+', '+each+' "see-also"' # xrefs to resid has a qualifier 'see-also' only when they are NOT unique
                elif each [0:30] == 'PMID for application instance:' :
                    newDefRefs=newDefRefs+', PMID:'+each[30:]+' "see-also"'   # replace tag 'for aplication instance' by qualifier 'see-also'
                elif each [0:3] == 'GO:' :
                    newDefRefs=newDefRefs+', '+each+' "identity"'  #, go and so have the qualifier 'identity'
                elif each [0:3] == 'SO:' :
                    newDefRefs=newDefRefs+', '+each+' "identity"'  
  
                else :
                    print 'Xref type with unknown qualifier'
                    print name
                    print each                                      
       
        newDefRefs=newDefRefs.replace('PMID:','pubmed:') #replace PMID tag with pubmed the short label (exact synonym) of pubmed term in CV database
        newDefRefs=newDefRefs.replace('RESID:','resid:') #replace RESID tag with resid the short label (exact synonym) of resid term in CV database
        newDefRefs=newDefRefs.replace('GO:0','go:GO:0')# add go short label to go xref
        newDefRefs=newDefRefs.replace('SO:0','so:SO:0')# add so short label to so xref
       
        output.write('def: '+defi+'" ['+newDefRefs+']\n')
        if xref_analog != [] : 
            for element in xref_analog :
                output.write(element+'\n')
        printFlag = 1
        newDefRefs=''
        defRefs=[]
    elif line[0:9] == 'comment: ':     
        output.write('xref_analog: comment:'+line[9:]+ ' "ANNOTATION"'+'\n')      
    elif line[0:9] == 'synonym: ' :
        output.write('xref_analog: go synonym:'+line[10:-4]+ ' "ALIAS"'+'\n')
    elif line[0:34] == 'xref_analog: id-validation-regexp:' :
        output.write('xref_analog: id-validation-regexp:'+line[34:-1]+ '" "ANNOTATION"'+'\n')
    elif line[0:25] == 'xref_analog: search-url: ' :
        output.write('xref_analog: search-url: '+line[26:-2]+ ' "ANNOTATION"'+'\n')
    elif line[0:9] == '[Typedef]':  # end of obo file definition of relationship 'part_of' should not be modified
        for j in range(k,len(inputt)) :
            output.write(inputt[j])
        break    
    elif printFlag == 1 :
        output.write(line+'\n')
        printFlag = 1
        newDefRefs=''
        defRefs=[]
         
