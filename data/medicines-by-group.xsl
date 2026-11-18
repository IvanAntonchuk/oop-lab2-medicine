<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

    <xsl:output method="xml" encoding="UTF-8" indent="yes"/>
    <xsl:strip-space elements="*"/>

    <xsl:key name="medicinesByGroup" match="Medicine" use="Group"/>

    <xsl:template match="/Medicines">
        <Medicines>
            <xsl:for-each select="Medicine[generate-id() = generate-id(key('medicinesByGroup', Group)[1])]">
                <xsl:sort select="Group"/>
                <xsl:element name="{Group}">
                    <xsl:apply-templates select="key('medicinesByGroup', Group)"/>
                </xsl:element>
            </xsl:for-each>
        </Medicines>
    </xsl:template>

    <xsl:template match="*">
        <xsl:element name="{local-name()}">
            <xsl:copy-of select="@*"/>
            <xsl:apply-templates/>
        </xsl:element>
    </xsl:template>

</xsl:stylesheet>