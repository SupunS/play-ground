package partial;

import javax.xml.namespace.QName;
import javax.xml.stream.Location;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import java.io.Writer;
import java.util.Iterator;

/**
 * Created by isurur on 11/28/16.
 */
public class PartialXMLEventReader implements XMLEventReader {
    private XMLEventReader reader;

    private StartElement startElement;
    private QName qName;

    public PartialXMLEventReader(XMLEventReader reader, QName element) {
        this.reader = reader;
        this.qName = element;
    }

    public String getElementText() throws XMLStreamException {
        return reader.getElementText();
    }

    public Object getProperty(String name) throws IllegalArgumentException {
        return reader.getProperty(name);
    }

    public boolean hasNext() {
        try {
            if (isEof(reader.peek()))
                return false;

            return reader.hasNext();

        } catch (XMLStreamException e) {
            throw new RuntimeException(e);
        }
    }

    public XMLEvent nextEvent() throws XMLStreamException {
        if (isEof(reader.peek()))
            return new EndElementEvent(startElement);

        XMLEvent e = reader.nextEvent();
        if (isEof(e))
            return new EndElementEvent(startElement);

        return e;
    }

    public XMLEvent nextTag() throws XMLStreamException {
        if (isEof(reader.peek()))
            return new EndElementEvent(startElement);

        XMLEvent e = reader.nextTag();
        if (isEof(e))
            return new EndElementEvent(startElement);

        return e;
    }

    public XMLEvent peek() throws XMLStreamException {
        if (isEof(reader.peek()))
            return new EndElementEvent(startElement);

        return reader.peek();
    }

    public Object next() {
        return reader.next();
    }

    public void remove() {
        reader.remove();
    }

    public void close() throws XMLStreamException {
        reader.close();
    }

    private boolean isEof(XMLEvent e) {
        if (e.getEventType() != XMLEvent.START_ELEMENT)
            return false;

        StartElement se = (StartElement) e;

        if (startElement == null)
            startElement = se;

        return se.getName().equals(qName);
    }

    private class EndElementEvent implements EndElement {

        private final StartElement elem;

        public EndElementEvent(StartElement elem) {
            this.elem = elem;
        }

        public QName getName() {
            return elem.getName();
        }

        public Iterator getNamespaces() {
            return elem.getNamespaces();
        }

        public Characters asCharacters() {
            return elem.asCharacters();
        }

        public EndElement asEndElement() {
            return this;
        }

        public StartElement asStartElement() {
            return startElement;
        }

        public int getEventType() {
            return XMLEvent.END_ELEMENT;
        }

        public Location getLocation() {
            return elem.getLocation();
        }

        public QName getSchemaType() {
            return elem.getSchemaType();
        }

        public boolean isAttribute() {
            return elem.isAttribute();
        }

        public boolean isCharacters() {
            return elem.isCharacters();
        }

        public boolean isEndDocument() {
            return elem.isEndDocument();
        }

        public boolean isEndElement() {
            return true;
        }

        public boolean isEntityReference() {
            return elem.isEntityReference();
        }

        public boolean isNamespace() {
            return elem.isNamespace();
        }

        public boolean isProcessingInstruction() {
            return elem.isProcessingInstruction();
        }

        public boolean isStartDocument() {
            return elem.isStartDocument();
        }

        public boolean isStartElement() {
            return false;
        }

        public void writeAsEncodedUnicode(Writer writer) throws XMLStreamException {

            elem.asEndElement().writeAsEncodedUnicode(writer);
        }
    }
}
