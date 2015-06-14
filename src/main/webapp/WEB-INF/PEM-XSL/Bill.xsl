<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:template match="/">
		<!-- TODO: Auto-generated template -->
		<html>
			<head>
				<style type="text/css">
					.pagebreak {page-break-after: always;}
					p
					{line-height: 150%;}
					li{line-height: 150%;}
					td{text-align:left;vertical-align:top;}
     <!--  .footer { position: fixed; bottom: 0px; }
      .pagenum:before { content: counter(footer); } -->
					
				</style>
			</head>

			<body class="pagenum:before;footer ">
				<p align="right" style="margin:0;"><b>Bill No: </b><xsl:value-of select="uiFlowData/bill/billNo" /></p>
				<p align="right" style="margin:0;"><b>Date: </b><xsl:value-of select="uiFlowData/billDate" /></p>
				<p align="left" style="line-height:100% ">To<br/>
				The Manager,<br/>
				<xsl:value-of select="uiFlowData/evalDtls/bankName" />,<br/>
				<xsl:value-of select="uiFlowData/evalDtls/bankBranch" />
				</p>
				<h4 align="center">VALUATION BILL</h4>
				<P>Towards valuation of the property at 	
				<xsl:for-each select="uiFlowData/assetDtls">
				<xsl:if test="position()=1">
				<xsl:value-of select="fieldLabel" />
				</xsl:if>
				</xsl:for-each>
				 belongs to 
				<xsl:for-each select="uiFlowData/customer">
				<xsl:value-of select="fieldLabel" />
				,</xsl:for-each> 
				is inspected on <xsl:value-of select="uiFlowData/inspectionDate" />.</P>
				<p align="left">
						<u>To be purchased by 
								<xsl:for-each select="uiFlowData/assetDtls">
				<xsl:if test="position()=4">
				<xsl:value-of select="fieldLabel" />
				</xsl:if>
				</xsl:for-each>
						</u><br/>
					<u>Fees as per norms of <xsl:value-of select="uiFlowData/evalDtls/bankName" /></u></p>
				<table width="86%">
				<xsl:for-each select="uiFlowData">
					<tr>
							<td style="color:white">space</td>
							<td width="60%">Value of the property</td>
							<td width="3%">:</td>
							<td width="3%">Rs.</td>
							<td width="20%" style="text-align:right">
								<xsl:value-of select="bill/propertyValue"></xsl:value-of>
							</td>
						</tr>
						</xsl:for-each>
					<xsl:for-each select="uiFlowData/billDtls">
					<tr>
							<td style="color:white">space</td>
							<td>Fees for Rs.<xsl:value-of select="endValue"></xsl:value-of>	&#160;@&#160;<xsl:value-of select="commPercent"></xsl:value-of></td>
							<td width="3%">:</td>
							<td width="3%">Rs.</td>
							<td width="20%" style="text-align:right">
								<xsl:value-of select="startValue"></xsl:value-of>
							</td>
						</tr>
						
						</xsl:for-each>
						<tr>
							<td style="color:white">space</td>
							<td width="60%"></td>
							<td width="3%"></td>
							<td width="3%"></td>
							<td width="20%">
							<table><tr><td style="text-align:left;vertical-align:top;">_________________</td></tr></table>
							</td>
						</tr>
						<xsl:for-each select="uiFlowData/bill">
						<tr>
							<td style="color:white">space</td>
							<td width="60%"></td>
							<td width="3%">:</td>
							<td width="3%">Rs.</td>
							<td width="20%" style="text-align:right">
							<xsl:value-of select="basicBillValue"></xsl:value-of>
							</td>
						</tr>
						<tr>
							<td style="color:white">space</td>
							<td width="60%">Discount</td>
							<td width="3%">:</td>
							<td width="3%">Rs.</td>
							<td width="20%" style="text-align:right">
							<xsl:value-of select="discntValue"></xsl:value-of>
							</td>
						</tr>
						<tr>
							<td style="color:white">space</td>
							<td width="60%"></td>
							<td width="3%">:</td>
							<td width="3%">Rs.</td>
							<td width="20%" style="text-align:right">
							<xsl:value-of select="discntBillValue"></xsl:value-of>
							</td>
						</tr>
						<tr>
							<td style="color:white">space</td>
							<td width="60%">Service tax @&#160;<xsl:value-of select="stPrcnt"></xsl:value-of>%</td>
							<td width="3%">:</td>
							<td width="3%">Rs.</td>
							<td width="20%" style="text-align:right">
								<xsl:value-of select="stValue"></xsl:value-of>
							</td>
						</tr>
						<tr>
							<td style="color:white">space</td>
							<td width="60%">SC @&#160;<xsl:value-of select="scPrcnt"></xsl:value-of>%</td>
							<td width="3%">:</td>
							<td width="3%">Rs.</td>
							<td width="20%" style="text-align:right">
								<xsl:value-of select="scValue"></xsl:value-of>
							</td>
						</tr>
						<tr>
							<td style="color:white">space</td>
							<td width="60%">SEHC @&#160;<xsl:value-of select="sehcPrcnt"></xsl:value-of>%</td>
							<td width="3%">:</td>
							<td width="3%">Rs.</td>
							<td width="20%" style="text-align:right">
								<xsl:value-of select="sehcValue"></xsl:value-of>
								<table><tr><td style="text-align:left;vertical-align:top;">_________________</td></tr></table>
							</td>
						</tr>
						<tr>
							<td style="color:white">space</td>
							<td width="60%"></td>
							<td width="3%">:</td>
							<td width="3%">Rs.</td>
							<td width="20%" style="text-align:right">
							<xsl:value-of select="paymentAmount"></xsl:value-of>
							<table><tr><td style="text-align:left;vertical-align:top;">_________________</td></tr></table>
							</td>
						</tr>
						<tr>
							<td style="color:white">space</td>
							<td width="60%">Round Off</td>
							<td width="3%">:</td>
							<td width="3%">Rs.</td>
							<td width="20%" style="text-align:right">
							<xsl:value-of select="billValue"></xsl:value-of>
							</td>
						</tr>
						</xsl:for-each>
				</table>
				<p align="right">(<xsl:value-of select="uiFlowData/bill/amountinwords"></xsl:value-of>)</p>
				<p align="right" style="font-weight:bold;">(<xsl:value-of select="uiFlowData/signature" />)</p>
				<p><b>Note:<br/>Please deposit the above amount in our A/c at Syndicate Bank,Tatabad Branch,A/c No. and indicating the bill number also.</b></p>
		</body>
		</html>
	</xsl:template>
</xsl:stylesheet>