<?xml version="1.0" encoding="UTF-8"?>
<!--

    (c) Copyright 2017-2024, ANS. All rights reserved.

-->
<sch:schema xmlns:sch="http://purl.oclc.org/dsdl/schematron" queryBinding="xslt2" xmlns:sqf="http://www.schematron-quickfix.com/validator/process">

     <sch:pattern id="validite-idNat-Struct-TRLecture"> 
		<!-- Contexe de la règle Schematron  -->
        <sch:rule context="//*:facilityDirectory/*:facility">
            <sch:assert test="*:otherID"> Pas d'identifiant de facilities trouvé.</sch:assert>
        </sch:rule>
		<!-- Contexe de la règle Schematron  -->
        <sch:rule context="//*:organizationDirectory/*:organization">
            <sch:assert test="*:otherID"> Pas d'identifiant d'organisation trouvé.</sch:assert>
        </sch:rule>
        <!-- Contexe de la règle Schematron  -->
        <sch:rule context="//*:serviceDirectory/*:service">
            <sch:assert test="*:otherID"> Pas d'identifiant de service trouvé.</sch:assert>
        </sch:rule>
   </sch:pattern>  
</sch:schema>