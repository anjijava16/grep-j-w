<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ms="urn:schemas-microsoft-com:xslt"
	xmlns:msxsl="urn:schemas-microsoft-com:xslt" exclude-result-prefixes="msxsl">

	<xsl:template match="/">
		<html>
			<head>
				<style type="text/css">
					.pagebreak {page-break-after: always;}
					p
					{line-height: 150%;}
					li{line-height: 150%;}
					td{text-align:left;vertical-align:top;}
				</style>
			</head>

			<body>
				<p align="right">
					<b>Date:</b>
					<xsl:value-of select="uiFlowData/inspectionDate" />

				</p>
				<center>
					<p style="text-transform:uppercase;align:centre">
						<u>
							<b>
								FAIR MARKET ASSESSMENT OF IMMOVABLE PROPERTY AS ON DATE
								BELONGS
								TO
								<xsl:for-each select="uiFlowData/customer">
									<xsl:value-of select="fieldLabel" />
									,
								</xsl:for-each>

							</b>
						</u>
					</p>
					<p style="align:centre">
						<u>
							(To be purchased by : Sri.
							<xsl:value-of select="uiFlowData/ownerName" />
							)
						</u>
					</p>
				</center>
				<p align="justify" style="text-indent: 20px;">
					Pursuant to the request from "The Manager", SBI
					Bank,Coimbatore,
					<xsl:value-of select="uiFlowData/evalDtls/bankBranch" />
					said to be owned by
					<xsl:for-each select="uiFlowData/customer">
						<xsl:value-of select="fieldLabel" />
						,
					</xsl:for-each>
					, is inspected on
					<xsl:value-of select="uiFlowData/evalDtls/inspectionDate" />
					for the purpose of estimating the
					present fair market value of the
					fixed asset in it.
				</p>
				<p align="left" style="color:black;font-size:18;font-weight:bold;">
					<u>Property-1</u>
				</p>
				<xsl:value-of select="uiFlowData/assetDtls/fieldValue" />
				<p>
					<xsl:for-each select="uiFlowData/legalDoc">
						<xsl:if test="string-length(.)!=0">
							<p>
								<xsl:number />
								) Photocopy of the
								<xsl:value-of select="fieldLabel" />
								document no
								<xsl:value-of select="docNo"></xsl:value-of>
								dated
								<xsl:value-of select="docDated"></xsl:value-of>
							</p>
						</xsl:if>
					</xsl:for-each>

				</p>
				<p align="left" style="color:black;font-size:18;font-weight:bold;">
					<u>Property-2</u>
				</p>
				<xsl:value-of select="uiFlowData/assetDtls1/fieldValue" />
				<p>
					<xsl:for-each select="uiFlowData/legalDoc1">
						<xsl:if test="string-length(.)!=0">
							<p>
								<xsl:number />
								) Photocopy of the
								<xsl:value-of select="fieldLabel" />
								document no
								<xsl:value-of select="docNo"></xsl:value-of>
								dated
								<xsl:value-of select="docDated"></xsl:value-of>
							</p>
						</xsl:if>
					</xsl:for-each>
				</p>
				<p>
				<xsl:if test="uiFlowData/assetDtls2/fieldValue[.!='']">
					<xsl:if test="uiFlowData/legalDoc2/fieldLabel[.!='']">
						<p align="left" style="color:black;font-size:18;font-weight:bold;">
							<u>Property-3</u>
						</p>
						<xsl:value-of select="uiFlowData/assetDtls2/fieldValue" />
							<xsl:for-each select="uiFlowData/legalDoc2">
								<xsl:if test="string-length(.)!=0">
									<p>
										<xsl:number />
										) Photocopy of the
										<xsl:value-of select="fieldLabel" />
										document no
										<xsl:value-of select="docNo"></xsl:value-of>
										dated
										<xsl:value-of select="docDated"></xsl:value-of>
									</p>
								</xsl:if>
							</xsl:for-each>
					</xsl:if>
				</xsl:if>
			</p>
				<p align="justify" style="text-indent: 20px;">
					Based upon the actual observations and
					also the
					particulars provided to
					me detailed valuation report is
					prepared and
					furnished.
				</p>
				<p align="justify" style="text-indent: 20px;">
					After giving careful considerations to
					the various
					important factors
					like the specifications, the present
					condition,
					age,future life,
					replacement cost,depreciation,potential
					for
					marketability,etc., as
					per known principles of valuation, I am of
					the opinion that the
					present value of the
					property is as follows
				</p>
				<table width="100%">
					<xsl:for-each select="uiFlowData/propertyValue">
						<xsl:if test="(position()=1)">
							<xsl:if test="fieldValue[.!='']">
								<tr>
									<th align="left" width="60%">Present fair market value of
										property-1</th>
									<td>:</td>
									<td width="39%">
										<xsl:value-of select="fieldValue" />
									</td>
								</tr>
							</xsl:if>
						</xsl:if>
					</xsl:for-each>
					<xsl:for-each select="uiFlowData/propertyValue1">
						<xsl:if test="(position()=1)">
							<xsl:if test="fieldValue[.!='']">
								<tr>
									<th align="left" width="60%">Present fair market value of
										property-2</th>
									<td>:</td>
									<td width="39%">
										<xsl:value-of select="fieldValue" />
									</td>
								</tr>
							</xsl:if>
						</xsl:if>
					</xsl:for-each>
					<xsl:for-each select="uiFlowData/propertyValue2">
						<xsl:if test="(position()=1)">
							<xsl:if test="fieldValue[.!='']">
								<tr>
									<th align="left" width="60%">Present fair market value of
										property-3</th>
									<td>:</td>
									<td width="39%">
										<xsl:value-of select="fieldValue" />
									</td>
								</tr>
							</xsl:if>
						</xsl:if>
					</xsl:for-each>
					<tr>
						<th align="left" width="60%"></th>
						<td></td>
						<td width="39%">
						<table><tr><td style="text-align:left;vertical-align:top;">_________________</td></tr></table>
							<xsl:value-of select="uiFlowData/totalFairMarket" />
						<table><tr><td style="text-align:left;vertical-align:top;">_________________</td></tr></table>
						</td>
					</tr>
				</table>
				<br />
				<table width="100%">
					<xsl:for-each select="uiFlowData/propertyValue">
						<xsl:if test="(position()=2)">
							<xsl:if test="fieldValue[.!='']">
								<tr>
									<th align="left" width="60%">Present realizable value of
										property-1</th>
									<td>:</td>
									<td width="39%">
										<xsl:value-of select="fieldValue" />
									</td>
								</tr>
							</xsl:if>
						</xsl:if>
					</xsl:for-each>
					<xsl:for-each select="uiFlowData/propertyValue1">
						<xsl:if test="(position()=2)">
							<xsl:if test="fieldValue[.!='']">
								<tr>
									<th align="left" width="60%">Present realizable value of
										property-2</th>
									<td>:</td>
									<td width="39%">
										<xsl:value-of select="fieldValue" />
									</td>
								</tr>
							</xsl:if>
						</xsl:if>
					</xsl:for-each>
					<xsl:for-each select="uiFlowData/propertyValue2">
						<xsl:if test="(position()=2)">
							<xsl:if test="fieldValue[.!='']">
								<tr>
									<th align="left" width="60%">Present realizable value of
										property-3</th>
									<td>:</td>
									<td width="39%">
										<xsl:value-of select="fieldValue" />
									</td>
								</tr>
							</xsl:if>
						</xsl:if>
					</xsl:for-each>
					<tr>
						<th align="left" width="60%"></th>
						<td></td>
						<td width="39%">
						<table><tr><td style="text-align:left;vertical-align:top;">_________________</td></tr></table>
							<xsl:value-of select="uiFlowData/totalRealizable" />
						<table><tr><td style="text-align:left;vertical-align:top;">_________________</td></tr></table>
						</td>
					</tr>
				</table>
				<br />
				<table width="100%">

					<xsl:for-each select="uiFlowData/propertyValue">
						<xsl:if test="(position()=3)">
							<xsl:if test="fieldValue[.!='']">
								<tr>
									<th align="left" width="60%">Present Distress value of property-1</th>
									<td>:</td>
									<td width="39%">
										<xsl:value-of select="fieldValue" />
									</td>
								</tr>
							</xsl:if>
						</xsl:if>
					</xsl:for-each>
					<xsl:for-each select="uiFlowData/propertyValue1">
						<xsl:if test="(position()=3)">
							<xsl:if test="fieldValue[.!='']">
								<tr>
									<th align="left" width="60%">Present Distress value of property-2</th>
									<td>:</td>
									<td width="39%">
										<xsl:value-of select="fieldValue" />
									</td>
								</tr>
							</xsl:if>
						</xsl:if>
					</xsl:for-each>
					<xsl:for-each select="uiFlowData/propertyValue2">
						<xsl:if test="(position()=3)">
							<xsl:if test="fieldValue[.!='']">
								<tr>
									<th align="left" width="60%">Present Distress value of property-3</th>
									<td>:</td>
									<td width="39%">
										<xsl:value-of select="fieldValue" />
									</td>
								</tr>
							</xsl:if>
						</xsl:if>
					</xsl:for-each>
					<tr>
						<th align="left" width="60%"></th>
						<td></td>
						<td width="39%">
						<table><tr><td style="text-align:left;vertical-align:top;">_________________</td></tr></table>
							<xsl:value-of select="uiFlowData/totalDistress" />
						<table><tr><td style="text-align:left;vertical-align:top;">_________________</td></tr></table>
						</td>
					</tr>
				</table>
				<br />
				<table width="100%">
					<xsl:for-each select="uiFlowData/propertyValue">
						<xsl:if test="(position()=4)">
							<xsl:if test="fieldValue[.!='']">
								<tr>
									<th align="left" width="60%">Present guideline value of property-1</th>
									<td>:</td>
									<td width="39%">
										<xsl:value-of select="fieldValue" />
									</td>
								</tr>
							</xsl:if>
						</xsl:if>
					</xsl:for-each>
					<xsl:for-each select="uiFlowData/propertyValue1">
						<xsl:if test="(position()=4)">
							<xsl:if test="fieldValue[.!='']">
								<tr>
									<th align="left" width="60%">Present guideline value of property-2</th>
									<td>:</td>
									<td width="39%">
										<xsl:value-of select="fieldValue" />
									</td>
								</tr>
							</xsl:if>
						</xsl:if>
					</xsl:for-each>
					<xsl:for-each select="uiFlowData/propertyValue2">
						<xsl:if test="(position()=4)">
							<xsl:if test="fieldValue[.!='']">
								<tr>
									<th align="left" width="60%">Present guideline value of property-3</th>
									<td>:</td>
									<td width="39%">
										<xsl:value-of select="fieldValue" />
									</td>
								</tr>
							</xsl:if>
						</xsl:if>
					</xsl:for-each>
					<tr>
						<th align="left" width="60%"></th>
						<td></td>
						<td width="39%">
						<table><tr><td style="text-align:left;vertical-align:top;">_________________</td></tr></table>
							<xsl:value-of select="uiFlowData/totalGuideline" />
						<table><tr><td style="text-align:left;vertical-align:top;">_________________</td></tr></table>
						</td>
					</tr>
				</table>
				<p>
					It is declared that,
				</p>
				<p>
					1. I have inspected the property on
					<xsl:value-of select="uiFlowData/inspectionDate" />


				</p>
				<p>2. I have no direct or indirect interest in the property valued.</p>
				<p>3. Further the information's and other details given/shown in
					this report are true and correct to the best of my knowledge and
					belief.</p>
				<br />
				<br />
				<br />
				<br />
				<p align="right" style="font-weight:bold;">(<xsl:value-of select="uiFlowData/signature" />)</p>
				
				<table width="100%" style="border-top:1px solid black;padding:5px; ">
					<tr>
						<th style="border-bottom: 1px solid black;"></th>
						<th style="border-bottom: 1px solid black;"></th>
						<th align="center"
							style="color:black;font-size:18;font-weight:bold;border-bottom: 1px solid black;">GUIDELINE VALUE OF PROPERTY1</th>
						<th style="border-bottom: 1px solid black;"></th>
						<th style="border-bottom: 1px solid black;"></th>
					</tr>
					<tr>
						<td align="left" style="border-bottom: 1px solid black;">Description</td>
						<td align="left" style=" border-bottom: 1px solid black;">Area</td>
						<td align="left" style=" border-bottom: 1px solid black;">Rate</td>
						<td align="left" style=" border-bottom: 1px solid black;">unit</td>
						<td align="left" style=" border-bottom: 1px solid black;">Amount</td>

					</tr>
					<tr>
						<td align="left" style="border-bottom: 1px solid black;"><xsl:value-of select="uiFlowData/guideline/fieldLabel"/></td>
						<td align="left" style="border-bottom: 1px solid black;"><xsl:value-of select="uiFlowData/guideline/area" /></td>
						<td style="border-bottom: 1px solid black;"><xsl:value-of select="uiFlowData/guideline/rate" /></td>
						<td style="border-bottom: 1px solid black;">sft</td>
						<td style="border-bottom: 1px solid black;">
							<xsl:value-of select="uiFlowData/guideline/amount" />
						</td>
					</tr>
					<tr>
						<td align="left" style="border-bottom: 1px solid black;">Total</td>
						<td align="left" style="border-bottom: 1px solid black;"></td>
						<td style="border-bottom: 1px solid black;"></td>
						<td style="border-bottom: 1px solid black;"></td>
						<td style="border-bottom: 1px solid black;">
							<xsl:value-of select="uiFlowData/guideline/amount" />

						</td>
					</tr>
				</table>
				<br />
				<br />

		<table width="100%" style="border-top:1px solid black;padding:5px; ">
					<tr>
						<th style="border-bottom: 1px solid black;"></th>
						<th style="border-bottom: 1px solid black;"></th>
						<th align="center"
							style="color:black;font-size:18;font-weight:bold;border-bottom: 1px solid black;">GUIDELINE VALUE OF PROPERTY2</th>
						<th style="border-bottom: 1px solid black;"></th>
						<th style="border-bottom: 1px solid black;"></th>
					</tr>
					<tr>
						<td align="left" style="border-bottom: 1px solid black;">Description</td>
						<td align="left" style=" border-bottom: 1px solid black;">Area</td>
						<td align="left" style=" border-bottom: 1px solid black;">Rate</td>
						<td align="left" style=" border-bottom: 1px solid black;">unit</td>
						<td align="left" style=" border-bottom: 1px solid black;">Amount</td>

					</tr>
					<tr>
						<td align="left" style="border-bottom: 1px solid black;"><xsl:value-of select="uiFlowData/guideline1/fieldLabel"/></td>
						<td align="left" style="border-bottom: 1px solid black;"><xsl:value-of select="uiFlowData/guideline1/area" /></td>
						<td style="border-bottom: 1px solid black;"><xsl:value-of select="uiFlowData/guideline1/rate" /></td>
						<td style="border-bottom: 1px solid black;">sft</td>
						<td style="border-bottom: 1px solid black;">
							<xsl:value-of select="uiFlowData/guideline1/amount" />
						</td>
					</tr>
					<tr>
						<td align="left" style="border-bottom: 1px solid black;">Total</td>
						<td align="left" style="border-bottom: 1px solid black;"></td>
						<td style="border-bottom: 1px solid black;"></td>
						<td style="border-bottom: 1px solid black;"></td>
						<td style="border-bottom: 1px solid black;">
							<xsl:value-of select="uiFlowData/guideline1/amount" />

						</td>
					</tr>
				</table>
				<br />
				<br />
				
		
				
						<table width="100%" style="border-top:1px solid black;padding:5px; ">
					<tr>
						<th style="border-bottom: 1px solid black;"></th>
						<th style="border-bottom: 1px solid black;"></th>
						<th align="center"
							style="color:black;font-size:18;font-weight:bold;border-bottom: 1px solid black;">GUIDELINE VALUE OF PROPERTY3</th>
						<th style="border-bottom: 1px solid black;"></th>
						<th style="border-bottom: 1px solid black;"></th>
					</tr>
					<tr>
						<td align="left" style="border-bottom: 1px solid black;">Description</td>
						<td align="left" style=" border-bottom: 1px solid black;">Area</td>
						<td align="left" style=" border-bottom: 1px solid black;">Rate</td>
						<td align="left" style=" border-bottom: 1px solid black;">unit</td>
						<td align="left" style=" border-bottom: 1px solid black;">Amount</td>

					</tr>
					<tr>
						<td align="left" style="border-bottom: 1px solid black;"><xsl:value-of select="uiFlowData/guideline2/fieldLabel"/></td>
						<td align="left" style="border-bottom: 1px solid black;"><xsl:value-of select="uiFlowData/guideline2/area" /></td>
						<td style="border-bottom: 1px solid black;"><xsl:value-of select="uiFlowData/guideline2/rate" /></td>
						<td style="border-bottom: 1px solid black;">sft</td>
						<td style="border-bottom: 1px solid black;">
							<xsl:value-of select="uiFlowData/guideline2/amount" />
						</td>
					</tr>
					<tr>
						<td align="left" style="border-bottom: 1px solid black;">Total</td>
						<td align="left" style="border-bottom: 1px solid black;"></td>
						<td style="border-bottom: 1px solid black;"></td>
						<td style="border-bottom: 1px solid black;"></td>
						<td style="border-bottom: 1px solid black;">
							<xsl:value-of select="uiFlowData/guideline2/amount" />

						</td>
					</tr>
				</table>
				<br />
				<br />
				<p align="right" style="font-weight:bold;">(<xsl:value-of select="uiFlowData/signature" />)</p>
				
			</body>
		</html>
	</xsl:template>
</xsl:stylesheet>