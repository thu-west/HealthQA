package platform.nlp.ltp.server.xml;

import java.io.StringReader;

import javax.xml.bind.*;
import javax.xml.transform.stream.StreamSource;

import platform.nlp.ltp.xml.bean.LTPRoot;



public class Xml2Bean {
	public static LTPRoot xml2Bean( String target ) throws JAXBException {
		JAXBContext jc = JAXBContext.newInstance( LTPRoot.class );
		Unmarshaller u = jc.createUnmarshaller();
		return (LTPRoot) u.unmarshal( new StreamSource( new StringReader( target ) ) );
	}
}
