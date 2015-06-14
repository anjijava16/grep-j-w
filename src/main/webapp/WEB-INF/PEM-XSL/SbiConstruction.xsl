<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" 
xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ms="urn:schemas-microsoft-com:xslt"  
 xmlns:msxsl="urn:schemas-microsoft-com:xslt" exclude-result-prefixes="msxsl">
	
	<xsl:template match="/">
		<html>
			<head>
				<style type="text/css">
					.pagebreak {page-break-after: always;}
					p {line-height: 150%;}
					li{line-height: 150%;}
					td{text-align:left;vertical-align:top;}
				</style>
			</head>

			<body>
			<input type="text" id="whatever" readonly = "" style="width:600px" >
  <xsl:attribute name="value">
  <xsl:value-of select="uiFlowData/evalnumber"/>
  </xsl:attribute>
</input>
				<p align="right">
				<b>Date:</b><xsl:value-of select="uiFlowData/inspectionDate"/>
     		
				</p>
				<center>
					<p style="text-transform:uppercase;align:centre">
						<u>
							<b>
								PROGRESS REPORT TOWARDS THE CONSTRUCTION OF
								<xsl:value-of select="uiFlowData/propDesc" />
								<xsl:value-of select="uiFlowData/propertyAddress" />
								, BELONGS TO
							<xsl:for-each select="uiFlowData/customer">
									<xsl:value-of select="fieldLabel" />
									,
								</xsl:for-each>

							</b>
						</u>
					</p>
				<p style="align:centre">
						<u>
							(To be purchased by : 
							<xsl:value-of select="uiFlowData/customername" />
							)
						</u>
					</p>
				</center>
				<p style="text-indent: 20px;text-align:justify;">
					Pursuant to the request from "The Manager",SBI
					Bank,
					<xsl:value-of select="uiFlowData/bankBranch" /> Branch, Coimbatore,
					the property at
					<xsl:value-of select="uiFlowData/propertyAddress" />
					, said to be owned by
					<xsl:for-each select="uiFlowData/customer">
					<xsl:value-of select="fieldLabel" />
									,
					</xsl:for-each>
					is inspected on
					<xsl:value-of select="uiFlowData/inspectionDate" />
					for the purpose of estimating the
					present fair market value of the
					property. The property is a
					<xsl:value-of select="uiFlowData/propDesc" />
				</p>
		<p style="line-height: 100%;margin :1;">
					The following documents were produced before me for perusal.<br/>
				<xsl:for-each select="uiFlowData/document">
						<p style="line-height: 100%;margin :0;">
							<xsl:number format="1)" /><xsl:value-of select="fieldLabel" />
						</p>
					</xsl:for-each>
					<xsl:for-each select="uiFlowData/legalDoc">
					<p style="line-height: 100%;margin :1;">Legal Documents</p>
						<p style="line-height: 100%;margin :0;">
							<xsl:number />
							) Photocopy of the
							<xsl:value-of select="fieldLabel" />
							document no
							<xsl:value-of select="docNo"></xsl:value-of>
							dated
							<xsl:value-of select="docDated"></xsl:value-of>
						</p>
					</xsl:for-each>
				</p>
				<br/>
				<p style="text-indent: 20px;text-align:justify;margin :0;">
					Based upon the actual observations and
					also the
					particulars provided to
					me detailed valuation report is
					prepared and
					furnished.
				</p>
				<p style="text-indent: 20px;text-align:justify;margin :0;">
					After giving careful considerations to the various important factors
					like the specifications, the present
					condition, age, future life, replacement cost, depreciation, potential for marketability, etc., as
					per known principles of valuation, I am of the opinion that the present value of the property is as follows
				</p>
				<table width="100%"
					style="table-layout : fixed;border-top:1px solid black;border-left:1px solid black; border-right:1px solid black;padding:5px; ">
					<tr>
						<th align="left"
							style=" border-bottom: 1px solid black;border-right: 1px solid black;"></th>
						<th align="left"
							style=" border-bottom: 1px solid black;border-right: 1px solid black;">Land</th>
						<th align="left"
							style=" border-bottom: 1px solid black;border-right: 1px solid black;">Construction</th>
						<th align="left" style=" border-bottom: 1px solid black;">Total</th>
					</tr>
					<tr>
						<th width="30%" align="left"
							style=" border-bottom: 1px solid black;border-right: 1px solid black;">Present Fair Market Value of Property</th>
						<td width="23%"
							style=" border-bottom: 1px solid black;border-right: 1px solid black;">
							Rs. <xsl:value-of select="uiFlowData/marketValue" />
						</td>
						<td width="20%"
							style=" border-bottom: 1px solid black;border-right: 1px solid black;">
							Rs. <xsl:value-of select="uiFlowData/constructionValue" />
						</td>
						<td width="26%" style=" border-bottom: 1px solid black;">
							Rs. <xsl:value-of select="uiFlowData/totalExtraItem" />
						</td>
					</tr>
					<tr>
						<th width="30%" align="left"
							style=" border-bottom: 1px solid black;border-right: 1px solid black;">Realizable Value of property</th>
						<td width="23%"
							style=" border-bottom: 1px solid black;border-right: 1px solid black;">
							Rs. <xsl:value-of select="uiFlowData/realizablevalue" />
						</td>
						<td width="20%"
							style=" border-bottom: 1px solid black;border-right: 1px solid black;">
							Rs. <xsl:value-of select="uiFlowData/constructionValue" />
						</td>
						<td width="26%" style=" border-bottom: 1px solid black;">
							Rs. <xsl:value-of select="uiFlowData/totalAdditional" />
						</td>
					</tr>
					<tr>
						<th width="30%" align="left"
							style=" border-bottom: 1px solid black;border-right: 1px solid black;">Distress value of the property</th>
						<td width="23%"
							style=" border-bottom: 1px solid black;border-right: 1px solid black;">
							Rs. <xsl:value-of select="uiFlowData/distressvalue" />
						</td>
						<td width="20%"
							style=" border-bottom: 1px solid black;border-right: 1px solid black;">
							Rs. <xsl:value-of select="uiFlowData/constructionValue" />
						</td>
						<td width="26%" style=" border-bottom: 1px solid black;">
							Rs. <xsl:value-of select="uiFlowData/totalMiscellaneous" />
						</td>
					</tr>
					<tr>
						<th width="30%" align="left"
							style=" border-bottom: 1px solid black;border-right: 1px solid black;">Guideline value of the property</th>
						<td width="23%"
							style=" border-bottom: 1px solid black;border-right: 1px solid black;">
							Rs. <xsl:value-of select="uiFlowData/guideland" />
						</td>
						<td width="20%"
							style=" border-bottom: 1px solid black;border-right: 1px solid black;">
							Rs. <xsl:value-of select="uiFlowData/constructionValue" />
						</td>
						<td width="26%" style=" border-bottom: 1px solid black;">
							Rs. <xsl:value-of select="uiFlowData/totalServices" />
						</td>
					</tr>
				</table>
				<p style="line-height: 100%;margin :0;">
					It is declared that,<br />
					1. I have inspected the property on
					<xsl:value-of select="uiFlowData/inspectionDate" />.<br />
					2. I have no direct or indirect interest in the property valued.<br />
					3. Further the information's and other details given/shown in
					this report are true and correct to the best of my knowledge and
					belief.</p>
				<br />
				<br />
				<p align="right"  style="font-weight:bold;">(<xsl:value-of select="uiFlowData/signature" />)</p>
				<br class="pagebreak" />
				<h4 align="center">VALUATION REPORT</h4>
			<!-- CUSTOEMR -->
				<p align="left" style="color:black;font-size:16;font-weight:bold;">I. GENERAL</p>
				<table width="100%">
				<tr>
							<td width="2%"  style="text-align:left;vertical-align:top;">1) </td>
							<td width="45%" style="text-align:left;vertical-align:top;">Purpose for which the valuation is made</td>
							<td width="3%" style="text-align:left;vertical-align:top;">:</td>
							<td width="45%" style="text-align:left;vertical-align:top;">
								<xsl:value-of select="uiFlowData/valuationPurpose"></xsl:value-of>
							</td>
				</tr>
				<tr>
							<td width="2%"  style="text-align:left;vertical-align:top;">2) </td>
							<td width="45%" style="text-align:left;vertical-align:top;">a) Date of inspection</td>
							<td width="3%" style="text-align:left;vertical-align:top;">:</td>
							<td width="45%" style="text-align:left;vertical-align:top;">
								<xsl:value-of select="uiFlowData/inspectionDate"></xsl:value-of>
							</td>
				</tr>
				<tr>
							<td width="2%"  style="text-align:left;vertical-align:top;"></td>
							<td width="45%" style="text-align:left;vertical-align:top;">b) Date on which the valuation is made</td>
							<td width="3%" style="text-align:left;vertical-align:top;">:</td>
							<td width="45%" style="text-align:left;vertical-align:top;">
								<xsl:value-of select="uiFlowData/inspectionDate"></xsl:value-of>
							</td>
				</tr>
				</table>
				<!-- DOCUMNENT DETAILS -->

				<table width="100%">
					<tr>
						<td width="2%" >3) </td>
						<td width="45%" >List of Documents produced for perusal</td>
						<td width="3%" ></td>
						<td width="45%" ></td>
					</tr>
				</table>
				<xsl:for-each select="uiFlowData/document">
						<p style="line-height: 100%;margin :0;">
							&#160;&#160;&#160;&#160;<xsl:number format="i)" value="position()+0" />Photocopy of the <xsl:value-of select="fieldLabel" />
						</p>
					</xsl:for-each>
				<xsl:for-each select="uiFlowData/legalDoc">
				&#160;&#160;&#160;&#160;<p style="line-height: 100%;margin :1;">Legal Documents</p>
						<p style="line-height: 100%;margin :0;">
							&#160;&#160;&#160;&#160;<xsl:number format="i)"  value="position()+0" />
							Photocopy of the
							<xsl:value-of select="fieldLabel" />
							document no
							<xsl:value-of select="docNo"></xsl:value-of>
							dated
							<xsl:value-of select="docDated"></xsl:value-of>
						</p>
					</xsl:for-each>	
				<table width="100%">
					<xsl:for-each select="uiFlowData/customer">

						<tr>
							<td width="2%"><xsl:if test="position() &lt; 2"><xsl:number format="1)" value="position()+3"/></xsl:if></td>
							<td width="45%"><xsl:if test="position() &lt;2">
							Name of the owner(s) and his/their addresses(es) with phone No.(Details of share of each owner in case of joint ownership)
							</xsl:if></td>
							<td width="3%">:</td>
							<td width="45%">
								<xsl:value-of select="fieldLabel"></xsl:value-of><br/>
								<xsl:value-of select="fieldValue"></xsl:value-of>
							</td>
						</tr>
				</xsl:for-each>
				</table>
				<table width="100%">

					<xsl:for-each select="uiFlowData">

						<tr>
							<td width="2%"><xsl:number format="1)" value="position()+4"/></td>
							<td width="45%">Brief Description of the property</td>
							<td width="3%">:</td>
							<td width="45%">
								<xsl:value-of select="propDesc"></xsl:value-of>
							</td>
						</tr>
						</xsl:for-each>
					</table>
				<table width="100%">
					<xsl:for-each select="uiFlowData/assetDtls">
					<xsl:if test="position()=4">
						<tr>
							<td width="2%"  style="text-align:left;vertical-align:top;"><xsl:number format="1)" value="position()+2"/></td>
							<td width="45%" style="text-align:left;vertical-align:top;">
								Name of the applicant / borrower with Address and telephone No.
							</td>
							<td width="3%" style="text-align:left;vertical-align:top;">:</td>
							<td width="45%" style="text-align:left;vertical-align:top;">
								<xsl:value-of select="fieldValue"></xsl:value-of>
								<xsl:if test="position()=5"><br/>
								<xsl:value-of select="fieldValue"></xsl:value-of>
								</xsl:if>
							</td>
						</tr>
						</xsl:if>
					</xsl:for-each>
				</table>
				<!-- DESCRIPTION OF THE PROPERTY-->
				<p align="left" style="color:black;font-size:16;font-weight:bold;">II. DESCRIPTION OF THE PROPERTY</p>
				<table width="100%">
					<xsl:for-each select="uiFlowData/propertyDescription">
						<tr>
							<td width="2%"  style="text-align:left;vertical-align:top;"><xsl:number format="1)" value="position()+0"/></td>
							<td width="45%" style="text-align:left;vertical-align:top;">
								<xsl:value-of select="fieldLabel"></xsl:value-of>
							</td>
							<td width="3%" style="text-align:left;vertical-align:top;">:</td>
							<td width="45%" style="text-align:left;vertical-align:top;">
								<xsl:value-of select="fieldValue"></xsl:value-of>
							</td>
						</tr>
					</xsl:for-each>
					<tr>
						<td width="2%" >2) </td>
						<td width="45%" >Location of the property </td>
						<td width="3%" ></td>
						<td width="45%" ></td>
					</tr>
					<xsl:for-each select="uiFlowData/propertyDescription1">
						<tr>
							<td width="2%"  style="text-align:left;vertical-align:top;"></td>
							<td width="45%" style="text-align:left;vertical-align:top;">
								<xsl:number format="a) " value="position()+0"/><xsl:value-of select="fieldLabel"></xsl:value-of>
							</td>
							<td width="3%" style="text-align:left;vertical-align:top;">:</td>
							<td width="45%" style="text-align:left;vertical-align:top;">
								<xsl:value-of select="fieldValue"></xsl:value-of>
							</td>
						</tr>
					</xsl:for-each>
					<xsl:for-each select="uiFlowData/propertyDescription2">
						<tr>
							<td width="2%"  style="text-align:left;vertical-align:top;">
							<xsl:if test="position() &lt;3">
							<xsl:number format="1)" value="position()+2"/>
							</xsl:if></td>
							<td width="45%" style="text-align:left;vertical-align:top;">
								<xsl:value-of select="fieldLabel"></xsl:value-of>
							</td>
							<td width="3%" style="text-align:left;vertical-align:top;">:</td>
							<td width="45%" style="text-align:left;vertical-align:top;">
								<xsl:value-of select="fieldValue"></xsl:value-of>
							</td>
						</tr>
					</xsl:for-each>
					<tr>
						<td width="2%" >5) </td>
						<td width="45%" >Classification of the area </td>
						<td width="3%" ></td>
						<td width="45%" ></td>
					</tr>
					<xsl:for-each select="uiFlowData/propertyDescription3">
						<tr>
							<td width="2%"  style="text-align:left;vertical-align:top;"></td>
							<td width="45%" style="text-align:left;vertical-align:top;"><xsl:number format="i) " value="position()+5"/><xsl:value-of select="fieldLabel"></xsl:value-of></td>
							<td width="3%" style="text-align:left;vertical-align:top;">:</td>
							<td width="45%" style="text-align:left;vertical-align:top;"><xsl:value-of select="fieldValue"></xsl:value-of></td>
						</tr>
					</xsl:for-each>
					<xsl:for-each select="uiFlowData/propertyDescription4">
						<tr>
							<td width="2%"  style="text-align:left;vertical-align:top;">
							<xsl:number format="1)" value="position()+5"/></td>
							<td width="45%" style="text-align:left;vertical-align:top;">
								<xsl:value-of select="fieldLabel"></xsl:value-of>
							</td>
							<td width="3%" style="text-align:left;vertical-align:top;">:</td>
							<td width="45%" style="text-align:left;vertical-align:top;">
								<xsl:value-of select="fieldValue"></xsl:value-of>
							</td>
						</tr>
					</xsl:for-each>
					</table>
					<table width="100%">
				<tr>
						<td width="2%" >10) </td>
						<td width="45%" >Boundaries of the property </td>
						<td width="3%" ></td>
						<td width="45%" ></td>
					</tr>
					</table>
		<table width="100%">
				<xsl:for-each select="uiFlowData/adjoinProperty">
					<xsl:if test="position() != last()">
							<xsl:if test="(position() mod 4)=1">
							<xsl:if test="position()!=1"><br/></xsl:if>
							<tr>
									<th width="15%" style="font-weight:bold;text-align:left;vertical-align:top;">
										<xsl:value-of select="groupHdr"></xsl:value-of>
									</th>
									<th width="2%"></th>
									<xsl:if test="asPerDeed[.!='']">
									<th  width="28%" align="left" style="font-weight:bold;text-decoration:underline;"><xsl:value-of select="deedValue"></xsl:value-of></th>
									</xsl:if>
									<xsl:if test="asPerPlan[.!='']">
									<th width="28%" align="left" style="font-weight:bold;text-decoration:underline;"><xsl:value-of select="planValue"></xsl:value-of></th>
									</xsl:if>
									<xsl:if test="asPerSite[.!='']">
									<th width="27%" align="left" style="font-weight:bold;text-decoration:underline;"><xsl:value-of select="siteValue"></xsl:value-of></th>
									</xsl:if>
								</tr>
							</xsl:if>
						</xsl:if>
						<tr>
							<td style="text-align:left;vertical-align:top;">
								<xsl:value-of select="fieldLabel"></xsl:value-of>
							</td>
							<td style="text-align:left;vertical-align:top;">:</td>
							<xsl:if test="asPerDeed[.!='']">
							<td style="text-align:left;vertical-align:top;">
								<xsl:value-of select="asPerDeed"></xsl:value-of>
							</td>
							</xsl:if>
							<xsl:if test="asPerPlan[.!='']">
							<td  style="text-align:left;vertical-align:top;">
								<xsl:value-of select="asPerPlan"></xsl:value-of>
							</td>
							</xsl:if>
							<xsl:if test="asPerSite[.!='']">
							<td  style="text-align:left;vertical-align:top;">
								<xsl:value-of select="asPerSite"></xsl:value-of>
							</td>
							</xsl:if>

						</tr>
					</xsl:for-each>

				</table>

				<table width="100%">
				<tr>
						<td width="2%" >11) </td>
						<td width="45%" >Dimensions of the site </td>
						<td width="3%" ></td>
						<td width="45%" ></td>
					</tr>
					</table>
				<table width="100%">

					<xsl:for-each select="uiFlowData/dimension">
						<xsl:if test="position() != last()">
							<xsl:if test="(position() mod 9)=1">
							<xsl:if test="position()!=1"><br/></xsl:if>
								<tr>
									<th width="24%" style="text-align:left;vertical-align:top;">
										<xsl:value-of select="groupHdr"></xsl:value-of>
									</th>
									<th width="1%"></th>
									<xsl:if test="asPerDeed[.!='']">
									<th width="25%" align="left" style="font-weight:bold;text-decoration:underline;"><xsl:value-of select="deedValue"></xsl:value-of></th>
									</xsl:if>
									<xsl:if test="asPerPlan[.!='']">
									<th width="25%" align="left" style="font-weight:bold;text-decoration:underline;"><xsl:value-of select="planValue"></xsl:value-of></th>
									</xsl:if>
									<xsl:if test="asPerSite[.!='']">
									<th  width="25%" align="left" style="font-weight:bold;text-decoration:underline;"><xsl:value-of select="siteValue"></xsl:value-of></th>
									</xsl:if>
								</tr>
							</xsl:if>
						</xsl:if>
						<xsl:if test="fieldLabel[.!='']">
						<tr>
							<td  style="text-align:left;vertical-align:top;">
								<xsl:value-of select="fieldLabel"></xsl:value-of>
							</td>
							<td style="text-align:left;vertical-align:top;">:</td>
							<xsl:if test="asPerDeed[.!='']">
							<td  style="text-align:left;vertical-align:top;">
								<xsl:value-of select="asPerDeed"></xsl:value-of>
							</td>
							</xsl:if>
							<xsl:if test="asPerPlan[.!='']">
							<td  style="text-align:left;vertical-align:top;">
								<xsl:value-of select="asPerPlan"></xsl:value-of>
							</td>
							</xsl:if>
							<xsl:if test="asPerSite[.!='']">
							<td  style="text-align:left;vertical-align:top;">
								<xsl:value-of select="aspersite"></xsl:value-of>
							</td>
							</xsl:if>
						</tr>
						</xsl:if>
					</xsl:for-each>
				</table>
				<table width="100%">
					<xsl:for-each select="uiFlowData/propertyDescription5">
						<tr>
							<td width="2%"  style="text-align:left;vertical-align:top;"><xsl:number format="1)" value="position()+11"/></td>
							<td width="45%" style="text-align:left;vertical-align:top;">
								<xsl:value-of select="fieldLabel"></xsl:value-of>
							</td>
							<td width="3%" style="text-align:left;vertical-align:top;">:</td>
							<td width="45%" style="text-align:left;vertical-align:top;">
								<xsl:value-of select="fieldValue"></xsl:value-of>
							</td>
						</tr>
					</xsl:for-each>
				</table>
				<!-- CHARACTERISTICS OF THE PROPERTY-->
				<p align="left" style="color:black;font-size:16;font-weight:bold;">III.CHARACTERISTICS OF THE PROPERTY</p>
				<table width="100%">
					<xsl:for-each select="uiFlowData/propertyConstruct">
						<tr>
							<td width="2%"  style="text-align:left;vertical-align:top;"><xsl:number format="1)" value="position()+0"/></td>
							<td width="45%" style="text-align:left;vertical-align:top;">
								<xsl:value-of select="fieldLabel"></xsl:value-of>
							</td>
							<td width="3%" style="text-align:left;vertical-align:top;">:</td>
							<td width="45%" style="text-align:left;vertical-align:top;">
								<xsl:value-of select="fieldValue"></xsl:value-of>
							</td>
						</tr>
					</xsl:for-each>
					<xsl:for-each select="uiFlowData/propertyConstruct1">
						<tr>
							<td width="2%"  style="text-align:left;vertical-align:top;"></td>
							<td width="45%" style="text-align:left;vertical-align:top;">
								<xsl:value-of select="fieldLabel"></xsl:value-of>
							</td>
							<td width="3%" style="text-align:left;vertical-align:top;">:</td>
							<td width="45%" style="text-align:left;vertical-align:top;">
								<xsl:value-of select="fieldValue"></xsl:value-of>
							</td>
						</tr>
					</xsl:for-each>
					<xsl:for-each select="uiFlowData/propertyConstruct2">
						<tr>
							<td width="2%"  style="text-align:left;vertical-align:top;"><xsl:number format="1)" value="position()+9"/></td>
							<td width="45%" style="text-align:left;vertical-align:top;">
								<xsl:value-of select="fieldLabel"></xsl:value-of>
							</td>
							<td width="3%" style="text-align:left;vertical-align:top;">:</td>
							<td width="45%" style="text-align:left;vertical-align:top;">
								<xsl:value-of select="fieldValue"></xsl:value-of>
							</td>
						</tr>
					</xsl:for-each>
					<xsl:for-each select="uiFlowData/propertyConstruct3">
						<tr>
							<td width="2%"  style="text-align:left;vertical-align:top;"></td>
							<td width="45%" style="text-align:left;vertical-align:top;">
								<xsl:value-of select="fieldLabel"></xsl:value-of>
							</td>
							<td width="3%" style="text-align:left;vertical-align:top;">:</td>
							<td width="45%" style="text-align:left;vertical-align:top;">
								<xsl:value-of select="fieldValue"></xsl:value-of>
							</td>
						</tr>
					</xsl:for-each>
					<xsl:for-each select="uiFlowData/propertyConstruct4">
						<tr>
							<td width="2%"  style="text-align:left;vertical-align:top;"><xsl:number format="1)" value="position()+12"/></td>
							<td width="45%" style="text-align:left;vertical-align:top;">
								<xsl:value-of select="fieldLabel"></xsl:value-of>
							</td>
							<td width="3%" style="text-align:left;vertical-align:top;">:</td>
							<td width="45%" style="text-align:left;vertical-align:top;">
								<xsl:value-of select="fieldValue"></xsl:value-of>
							</td>
						</tr>
					</xsl:for-each>
				</table>
				<br/>

					<!--LAND VALUATION -->
				<p align="left" style="color:black;font-size:16;font-weight:bold;"><u>PART-A (Valuation of Land)</u></p>
				<table width="100%">

					<xsl:for-each select="uiFlowData/landval">
							<tr>
							<td width="2%"  style="text-align:left;vertical-align:top;"><xsl:number format="1)" value="position()+0"/></td>
							<td width="45%" style="text-align:left;vertical-align:top;">
								<xsl:value-of select="fieldLabel"></xsl:value-of>
							</td>
							<td width="3%" style="text-align:left;vertical-align:top;">:</td>
							<td width="45%" style="text-align:left;vertical-align:top;">
								<xsl:value-of select="fieldValue"></xsl:value-of>
							</td>
						</tr>
					</xsl:for-each>
				</table>
				<br/>
					<!--PART-B ESIMATE FOR CONSTRUCTION-->
				<p align="left" style="color:black;font-size:16;font-weight:bold;">PART-B (Estimate for construction)</p>
				<table width="100%">
					<xsl:for-each select="uiFlowData/flatValuation">
						<tr>
							<td width="2%"  style="text-align:left;vertical-align:top;"><xsl:number format="1)" value="position()"/></td>
							<td width="45%" style="text-align:left;vertical-align:top;">
								<xsl:value-of select="fieldLabel"></xsl:value-of>
							</td>
							<td width="3%" style="text-align:left;vertical-align:top;">:</td>
							<td width="45%" style="text-align:left;vertical-align:top;">
								<xsl:value-of select="fieldValue"></xsl:value-of>
							</td>
						</tr>
					</xsl:for-each>
				</table>
				<br/>
				<!--DETAILS OF PLAN APPROVAL -->
				<p align="left" style="color:black;font-size:16;font-weight:bold;text-decoration:underline;"><u>COST OF CONSTRUCTION OF RESIDENTIAL BUILDING AS ON DATE:</u></p>
				<p align="left" style="color:black;font-size:16;font-weight:bold;">I. DETAILS OF BUILDING PLAN APPROVAL : </p>
				<table width="100%">
					<xsl:for-each select="uiFlowData/planApproval">
						<tr>
							<td width="2%"  style="text-align:left;vertical-align:top;"><xsl:number format="1)" value="position()+0"/></td>
							<td width="45%" style="text-align:left;vertical-align:top;">
								<xsl:value-of select="fieldLabel"></xsl:value-of>
							</td>
							<td width="3%" style="text-align:left;vertical-align:top;">:</td>
							<td width="45%" style="text-align:left;vertical-align:top;">
								<xsl:value-of select="fieldValue"></xsl:value-of>
							</td>
						</tr>
					</xsl:for-each>
				</table>
				<br />
				<!--DETAILS OF APARTMENT BUILDING-->
				<p align="left" style="color:black;font-size:16;font-weight:bold;">II. DETAILS OF CONSTRUCTION</p>
				<!-- PLINTH AREA DETAILS -->
				<table width="100%">
							<tr>
							<td width="2%">1)</td>
							<td width="45%">Plinth Area Details</td>
							<td width="3%" ></td>
							<td width="45%"></td>
						</tr>
				</table>
				<table width="100%">
					<tr>
						<th></th>
						<th align="left" style="font-weight:bold;text-decoration:underline;">Description</th>
						<th></th>
						<th align="left" style="font-weight:bold;text-decoration:underline;">As per the plan</th>
						<th align="left" style="font-weight:bold;text-decoration:underline;">As at site</th>
					</tr>
					<xsl:for-each select="uiFlowData/plinthArea">

						<tr>
							<td style="color:white">space</td>
							<td>
								<xsl:value-of select="fieldLabel"></xsl:value-of>
							</td>
							<td>:</td>
							<td>
								<xsl:value-of select="asPerPlan"></xsl:value-of>
							</td>
							<td>
								<xsl:value-of select="asPerSite"></xsl:value-of>
							</td></tr>
					</xsl:for-each>
				</table>
				<table width="100%">
					<xsl:for-each select="uiFlowData/planApproval1">
							<tr>
							<td width="2%" style="text-align:left;vertical-align:top;"><xsl:number value="position()+1" format="1)" /></td>
							<td width="45%" style="text-align:left;vertical-align:top;">
								<xsl:value-of select="fieldLabel"></xsl:value-of>
							</td>
							<td width="3%" style="text-align:left;vertical-align:top;">:</td>
							<td width="45%" style="text-align:left;vertical-align:top;">
								<xsl:value-of select="fieldValue"></xsl:value-of>
							</td>
						</tr>
					</xsl:for-each>
				</table>
			<!--APPLICANT ESTIMATE -->
				<table width="100%">

					<xsl:for-each select="uiFlowData/applicantEstimate">
					<xsl:if test="fieldLabel[.!='']">
						<tr>
							<td width="2%" style="text-align:left;vertical-align:top;">
								<xsl:if test="position()&lt; 3">
									<xsl:number value="position()+5" format="1)" />
								</xsl:if>
							</td>
							<td width="45%" style="text-align:left;vertical-align:top;">
								<xsl:value-of select="fieldLabel"></xsl:value-of>
							</td>
							<td width="3%" style="text-align:left;vertical-align:top;">:</td>
							<td width="45%" style="text-align:left;vertical-align:top;">
								<xsl:value-of select="fieldValue"></xsl:value-of>
							</td>
						</tr>
						</xsl:if>
					</xsl:for-each>
				</table>
				<!--APPLICANT REASONABLE -->
				<table width="100%">

					<xsl:for-each select="uiFlowData/applicantReason">
						<xsl:if test="fieldLabel[.!='']">
						<tr>
							<td width="2%" style="text-align:left;vertical-align:top;">
								<xsl:if test="position()&lt;4">
									<xsl:number value="position()+7" format="1)" />
								</xsl:if>
							</td>
						<td width="45%" style="text-align:left;vertical-align:top;">
								<xsl:value-of select="fieldLabel"></xsl:value-of>
							</td>
							<td width="3%" style="text-align:left;vertical-align:top;">:</td>
							<td width="45%" style="text-align:left;vertical-align:top;">
								<xsl:value-of select="fieldValue"></xsl:value-of>
							</td>
						</tr>
						</xsl:if>
					</xsl:for-each>
				</table>
					<!--SPECIFICATION OF BUILDING -->
				<table width="100%">
							<tr>
							<td width="2%">9)</td>
							<td width="45%">Specification of the building(floor wise)</td>
							<td width="3%" ></td>
							<td width="45%"></td>
						</tr>
				</table>
				<table width="100%">
				<xsl:for-each select="uiFlowData/buildSpec">
					<xsl:if test="position() != last()">
							<xsl:if test="(position() mod 8)=1">
								<tr>
									<td align="left" style="font-weight:bold;text-decoration:underline;">Description</td>
									<td align="left" style="font-weight:bold;text-decoration:underline;">As per deed</td>
									<xsl:if test="asPerSite[.!='']">
									<td align="left" style="font-weight:bold;text-decoration:underline;">As at site</td>
									</xsl:if>
									<xsl:if test="asPerPlan[.!='']">
									<td align="left" style="font-weight:bold;text-decoration:underline;">As per plan</td>
									</xsl:if>
								</tr>
								<tr>
									<td></td>
									<td style="font-weight:bold;text-decoration:underline;"><xsl:value-of select="groupHdr"></xsl:value-of></td>
									<td style="font-weight:bold;text-decoration:underline;"><xsl:value-of select="groupHdrSite"></xsl:value-of></td>
									<td style="font-weight:bold;text-decoration:underline;"><xsl:value-of select="groupHdrPlan"></xsl:value-of></td>
								</tr>
							</xsl:if>
						</xsl:if>
						<tr>
							<td width="25%">
								<xsl:value-of select="fieldLabel" />
							</td>
							<td width="25%">
								<xsl:value-of select="asPerDeed" />
							</td>
							<xsl:if test="asPerSite[.!='']">
							<td width="25%">
								<xsl:value-of select="asPerSite" />
							</td>
							</xsl:if>
							<xsl:if test="asPerPlan[.!='']">
							<td width="25%">
								<xsl:value-of select="asPerPlan" />
							</td>
							</xsl:if>
						</tr>
					</xsl:for-each>
				</table>
			<!--EARTH QUAKE -->
				<table width="100%">

					<xsl:for-each select="uiFlowData/earthQuake">
						<tr>
							<td width="2%" style="text-align:left;vertical-align:top;">
								<xsl:if test="position()&lt; 2">
									<xsl:number value="position()+9" format="1)" />
								</xsl:if>
							</td>
							<td width="45%" style="text-align:left;vertical-align:top;">
								<xsl:value-of select="fieldLabel"></xsl:value-of>
							</td>
							<td width="3%" style="text-align:left;vertical-align:top;">:</td>
							<td width="45%" style="text-align:left;vertical-align:top;">
								<xsl:value-of select="fieldValue"></xsl:value-of>
							</td>
						</tr>
					</xsl:for-each>
				</table>
				<!--STAGE OF CONSTRUCTION -->
				<table width="100%">

					<xsl:for-each select="uiFlowData/stgofConstn">
						<tr>
							<td width="2%" style="text-align:left;vertical-align:top;">
								<xsl:if test="position()&lt; 2">
									<xsl:number value="position()+10" format="1)" />
								</xsl:if>
							</td>
							<td width="45%" style="text-align:left;vertical-align:top;">
								<xsl:value-of select="fieldLabel"></xsl:value-of>
							</td>
							<td width="3%" style="text-align:left;vertical-align:top;">:</td>
							<td width="45%" style="text-align:left;vertical-align:top;">
								<xsl:value-of select="fieldValue"></xsl:value-of>
							</td>
						</tr>
					</xsl:for-each>
				</table>
				<!-- COST OF CONSTRUCTION -->
				<table width="100%">

					<xsl:for-each select="uiFlowData/constValuation">
						<tr>
							<td width="2%" style="text-align:left;vertical-align:top;">
								<xsl:if test="position()&lt; 2">
									<xsl:number value="position()+11" format="1)" />
								</xsl:if>
							</td>
							<td width="45%" style="text-align:left;vertical-align:top;">
								<xsl:value-of select="fieldLabel"></xsl:value-of>
							</td>
							<td width="3%" style="text-align:left;vertical-align:top;">:</td>
							<td width="45%" style="text-align:left;vertical-align:top;">
								Rs.<xsl:value-of select="fieldValue"></xsl:value-of>
							</td>
						</tr>
					</xsl:for-each>
				</table>
					<table width="100%">

					<xsl:for-each select="uiFlowData/stgofConstn1">
						<tr>
							<td width="2%" style="text-align:left;vertical-align:top;">
									<xsl:number value="position()+12" format="1)" />
							</td>
							<td width="45%" style="text-align:left;vertical-align:top;">
								<xsl:value-of select="fieldLabel"></xsl:value-of>
							</td>
							<td width="3%" style="text-align:left;vertical-align:top;">:</td>
							<td width="45%" style="text-align:left;vertical-align:top;">
								<xsl:value-of select="fieldValue"></xsl:value-of>
							</td>
						</tr>
					</xsl:for-each>
				</table>
			<br class="pagebreak" />
				<!-- TOTAL ABSTRACT -->
				<p align="center" style="color:black;font-size:16;font-weight:bold">TOTAL ABSTRACT</p>
				<table width="100%"
					style="border-top:1px solid black;border-left:1px solid black; border-right:1px solid black;padding:5px; ">

					<tr>
						<td align="left"
							style=" border-bottom: 1px solid black;border-right: 1px solid black;">Land</td>
						<td align="left" style=" border-bottom: 1px solid black;">
							Rs. <xsl:value-of select="uiFlowData/marketValue" />
						</td>
					</tr>
					<tr>
						<td align="left"
							style=" border-bottom: 1px solid black;border-right: 1px solid black;">Under Construction Building</td>
						<td align="left" style=" border-bottom: 1px solid black;">
							Rs. <xsl:value-of select="uiFlowData/constructionValue" />
						</td>
					</tr>
					<tr>
						<td align="left"
						
							style="border-bottom: 1px solid black;border-right: 1px solid black;">Total</td>
						<td style="border-bottom: 1px solid black;">
							Rs. <xsl:value-of select="uiFlowData/totalAbstractvalue" />
						</td>
					</tr>
				</table>
				<p style="margin:0;">
				The Present fair market value of the above property is Rs. <xsl:value-of select="uiFlowData/totalExtraItem" />
				<br/>and<br/>
				The Realizable value of the property is would be Rs. <xsl:value-of select="uiFlowData/totalAdditional" />
				<br/>and<br/>
				The Distress value of the property would be Rs. <xsl:value-of select="uiFlowData/totalMiscellaneous" />
				<br/>and<br/>
				The Guideline value of the property would be Rs. <xsl:value-of select="uiFlowData/totalServices" />
				</p>
				<br />

				<p align="right" style="font-weight:bold;">(<xsl:value-of select="uiFlowData/signature" />)</p>
				<br class="pagebreak" />

<table width="100%" style="border-top:1px solid black;padding:5px; ">
					<caption><b>GUIDELINE VALUE OF THE PROPERTY</b></caption>
					<tr>
						<td align="left" style="border-bottom: 1px solid black;border-top: 1px solid black;">Description</td>
						<td align="left" style=" border-bottom: 1px solid black;border-top: 1px solid black;">Area</td>
						<td align="left" style=" border-bottom: 1px solid black;border-top: 1px solid black;">Rate</td>
						<td align="left" style=" border-bottom: 1px solid black;border-top: 1px solid black;">unit</td>
						<td align="left" style=" border-bottom: 1px solid black;border-top: 1px solid black;">Amount</td>

					</tr>
					<xsl:for-each select="uiFlowData/guideline">
						<tr>
							<td align="left" style="border-bottom: 1px solid black;">
								<xsl:value-of select="fieldLabel" />
							</td>
							<td align="left" style="border-bottom: 1px solid black;">
								<xsl:value-of select="area" />
							</td>
							<td style="border-bottom: 1px solid black;">
								<xsl:value-of select="rate" />
							</td>
							<td style="border-bottom: 1px solid black;">sft</td>
							<td style="border-bottom: 1px solid black;">
								Rs. <xsl:value-of select="amount" />
							</td>
						</tr>
					</xsl:for-each>
					<tr>
						<td align="left" style="border-bottom: 1px solid black;">Total</td>
						<td align="left" style="border-bottom: 1px solid black;"></td>
						<td style="border-bottom: 1px solid black;"></td>
						<td style="border-bottom: 1px solid black;"></td>
						<td style="border-bottom: 1px solid black;">
							Rs. <xsl:value-of select="uiFlowData/guidelinevalue" />

						</td>
					</tr>
				</table>
				<p align="right">(<xsl:value-of select="uiFlowData/amountWordsGuideline" />)</p>
				<p align="right" style="font-weight:bold;">(<xsl:value-of select="uiFlowData/signature" />)</p>
				<p align="left" style="margin:0;">The guideline value of the property was obtained from the</p>
				<p>
					<font color="#157DEC">
						<u>http://www.tnreginet/igreen/guideline_value.html.</u>
					</font>
				</p>

				<p style="text-indent: 20px;">
					<xsl:for-each select="uiFlowData/guidelineref">
						<font color="red">Zone:</font>
						<font color="#157DEC">
							<xsl:value-of select="zone" />
						</font>
						&#160;
						<font color="red">Sro:</font>
						<font color="#157DEC">
							<xsl:value-of select="sro" />
						</font>
						&#160;
						<font color="red">Village:</font>
						<font color="#157DEC">
							<xsl:value-of select="village" />
						</font>
						&#160;
						<font color="red">RevnueDistName:</font>
						<font color="#157DEC">
							<xsl:value-of select="district" />
						</font>
						&#160;
						<font color="red">TalukName:</font>
						<font color="#157DEC">
							<xsl:value-of select="taluk" />
						</font>
						&#160;
					</xsl:for-each>
				</p>
				<table width="100%"
					style="border-top:1px solid black;border-left:1px solid black; border-right:1px solid black;padding:5px; ">
					<tr>
						<th bgcolor="#82CAFA" style="border-right:1px solid black;">
							<xsl:value-of select="uiFlowData/guidelineref/fieldLabel" />
						</th>
						<th bgcolor="#82CAFA" style="border-right:1px solid black;">GUIDELINE VALUE</th>
						<th bgcolor="#82CAFA" style="border-right:1px solid black;">GUIDELINE VALUE(IN METRIC)</th>
						<th bgcolor="#82CAFA">CLASSIFICATION</th>
					</tr>
					<xsl:for-each select="uiFlowData/guidelineref">
						<tr>
							<td style="border-right:1px solid black;border-bottom:1px solid black;text-transform:uppercase;">
								<xsl:value-of select="streetName" />
							</td>
							<td style="border-right:1px solid black;border-bottom:1px solid black;">
								<xsl:value-of select="guidelineValue" />
							</td>
							<td style="border-right:1px solid black;border-bottom:1px solid black;">
								<xsl:value-of select="guidelineMatric" />
							</td>
							<td style="border-bottom:1px solid black;">
								<xsl:value-of select="classification" />
							</td>
						</tr>
					</xsl:for-each>
				</table>
				<br class="pagebreak" />

				<p align="right">
				<b>Date:</b>
					<xsl:value-of select="uiFlowData/inspectionDate" />
				</p>
				<h4 align="center">CERTIFICATE</h4>
				<p align="left" class="big" style="line-height: 2;">
					<ol>
						<li>
							The Present fair market value of the above property with the
							existing condition and
							specification is in my opinion
							Rs.
							<xsl:value-of select="uiFlowData/marketValue" />
							(
							<xsl:value-of select="uiFlowData/amountInWords" />
							).
							<br />
						</li>
						<li>
							If the property is offered as collateral security, the concerned
							financial institution is requested to
							verify the name of the
							owner,address of the property and the extend of
							the land shown in
							the report
							with respect to the latest legal opinion.
							<br />
						</li>
						<li>
							Value varies with purpose. This report is not to be referred if
							the purpose is different other than
							the present fair market value
							of the property.
							<br />
						</li>
						<li>
							The stability of structure is not considering the seismic forces.
							<br />
						</li>
					</ol>
				</p>
				<br />
				<br />
				<br />
				<br />
				<p align="right" style="font-weight:bold;">(<xsl:value-of select="uiFlowData/signature" />)</p>
				<p align="left">Note : This report contains -- pages</p>
		</body>
		</html>
	</xsl:template>
</xsl:stylesheet>