<?xml version="1.0" encoding="UTF-8"?>
<!--

    (c) Copyright 2017-2024, ANS. All rights reserved.

-->
<sch:schema xmlns:sch="http://purl.oclc.org/dsdl/schematron" queryBinding="xslt2"
    xmlns:sqf="http://www.schematron-quickfix.com/validator/process">
    
    <sch:pattern id="champsActivite"> 
               
        <!-- Les UE doivent avoir un unique champ d'activité et un unique mode de prise en charge -->
        <sch:rule context="//*:facilityDirectory/*:facility[*:codedType[@code='4']]/*:extension">                     
            <sch:assert test="count(*:typeActivity)=1"> Les UE doivent avoir un et un seul champ d'activité. Nombre de champ d'activité de cette UE : <sch:value-of select="count(*:typeActivity)"/>.</sch:assert>
            <sch:assert test="count(*:careMode)=1"> Les UE doivent avoir un et un seul mode de prise en charge. Nombre de modes de prise en charge de cette UE : <sch:value-of select="count(*:careMode)"/>.</sch:assert>
        </sch:rule>
    
        <sch:let name="codesUE" value="//*:facility[*:codedType[@code='4']]/*:extension/*:typeActivity"/>
        <sch:let name="codesOI" value="//*:facility[*:codedType[not(@code='4')]]/*:extension/*:typeActivity"/>        
        <sch:let name="compteurUE" value="count(*:facility[*:codedType[@code='4']])"/> 
    
            <!-- on vérifie que les codes des UE sont dans la liste des codes des OI -->
        <sch:rule context="//*:facility[*:codedType[@code='4']]/*:extension/*:typeActivity"> 
            <sch:let name="facilityEntityID" value="current()/../../@entityID"/>
            <sch:assert test="count($codesOI)=0 or count($codesOI)=0 or $codesOI[@code=current()/@code]"> Le Champ d'activité <sch:value-of select="current()/@code"/> pour la facility dont l'EntityID est <sch:value-of select="$facilityEntityID"/> n'est pas remontée dans les OI. Code(s) au niveau OI : <sch:value-of select="distinct-values($codesOI/@code)"/> </sch:assert>
        </sch:rule> 
        
    </sch:pattern>   
</sch:schema>