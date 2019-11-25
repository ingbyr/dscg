<?xml version="1.0" encoding="UTF-8"?>

<xsl:stylesheet version="1.0"
xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

<xsl:output method="xml" version="1.0" encoding="UTF-8" indent="yes"/>

<xsl:template match="/">
<services>
<xsl:for-each select="services/service">
	<service name="{@name}" Res="{@Res}" Ava="{@Ava}" Suc="{@Suc}" Rel="{@Rel}" Lat="{@Lat}" Pri="{@Pri}">
		<inputs>
				<xsl:copy-of select="inputs/instance"/>
		</inputs>
		<outputs-possibilities>
			<outputs prob="1.0">
				<xsl:copy-of select="outputs/instance"/>
			</outputs>
		</outputs-possibilities>
	</service>
</xsl:for-each>
</services>
</xsl:template>

</xsl:stylesheet>