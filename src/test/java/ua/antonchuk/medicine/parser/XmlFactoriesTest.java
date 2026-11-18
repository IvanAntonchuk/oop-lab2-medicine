package ua.antonchuk.medicine.parser;

import org.junit.jupiter.api.Test;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLInputFactory;
import javax.xml.transform.TransformerConfigurationException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class XmlFactoriesTest {

    private static final String DISALLOW_DOCTYPE =
            "http://apache.org/xml/features/disallow-doctype-decl";

    @Test
    void saxParserFactoryUsesSecureProcessingAndForbidsDoctype()
            throws ParserConfigurationException, SAXException {
        var factory = XmlFactories.saxParserFactory();

        assertTrue(factory.getFeature(XMLConstants.FEATURE_SECURE_PROCESSING));
        assertTrue(factory.getFeature(DISALLOW_DOCTYPE));
    }

    @Test
    void documentBuilderFactoryUsesSecureProcessingAndForbidsDoctype()
            throws ParserConfigurationException {
        var factory = XmlFactories.documentBuilderFactory();

        assertTrue(factory.getFeature(XMLConstants.FEATURE_SECURE_PROCESSING));
        assertTrue(factory.getFeature(DISALLOW_DOCTYPE));
    }

    @Test
    void documentBuilderFactoryMergesCdataIntoText() throws ParserConfigurationException {
        assertTrue(XmlFactories.documentBuilderFactory().isCoalescing());
    }

    @Test
    void xmlInputFactoryDisablesDtdAndExternalEntities() {
        XMLInputFactory factory = XmlFactories.xmlInputFactory();

        assertEquals(false, factory.getProperty(XMLInputFactory.SUPPORT_DTD));
        assertEquals(false, factory.getProperty(XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES));
    }

    @Test
    void xmlInputFactoryMergesAdjacentTextEvents() {
        assertEquals(true, XmlFactories.xmlInputFactory().getProperty(XMLInputFactory.IS_COALESCING));
    }

    @Test
    void transformerFactoryUsesSecureProcessingAndBlocksExternalAccess()
            throws TransformerConfigurationException {
        var factory = XmlFactories.transformerFactory();

        assertTrue(factory.getFeature(XMLConstants.FEATURE_SECURE_PROCESSING));
        assertEquals("", factory.getAttribute(XMLConstants.ACCESS_EXTERNAL_DTD));
        assertEquals("", factory.getAttribute(XMLConstants.ACCESS_EXTERNAL_STYLESHEET));
    }
}