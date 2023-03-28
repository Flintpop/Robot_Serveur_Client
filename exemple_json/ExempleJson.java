package exemple_json;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ExempleJson {

	public static void main(String [] args) {
				
		// objet à convertir en texte JSON
		Data d1 = new Data();
		d1.setCmd("prog");
		d1.setParam1("(space add robi (rect.class new))");
		
		// 
		StringWriter sw = new StringWriter();
		
		// conversion en JSON
		try {
			JsonGenerator generator = new JsonFactory().createGenerator(sw);
			ObjectMapper mapper = new ObjectMapper();
			generator.setCodec(mapper);
			generator.writeObject(d1);
			generator.close();
		}
		catch (Exception e) {
			System.out.println("Erreur production JSON "+e.getMessage());
		}
		
		System.out.println("Résultat JSON = "+sw.toString());
		
		// conversion texte JSON en objet Java
		try {
			Data d2 = new ObjectMapper().readValue(new StringReader(sw.toString()), Data.class);
			System.out.println("d2 = "+d2.toString());
		}
		catch (Exception e) {
			System.out.println("Erreur lecture JSON "+e.getMessage());
		}
	}
}
