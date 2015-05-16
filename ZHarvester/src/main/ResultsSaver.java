package main;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamResult;

import org.marc4j.MarcException;
import org.marc4j.MarcReader;
import org.marc4j.MarcStreamReader;
import org.marc4j.MarcXmlWriter;
import org.yaz4j.Record;

public class ResultsSaver {
	public static int save(List<Record> lst, String dir, String encoding, Source stylesheet) throws IOException{
		FileOutputStream fileOutputStream = new FileOutputStream(dir);
		ByteArrayInputStream in = null;
		MarcReader reader = null;
		ByteArrayOutputStream out = new ByteArrayOutputStream(10000000); // < 10 000 000 bytes
		org.marc4j.marc.Record record = null;
		Result result = new StreamResult(out);
		MarcXmlWriter writer = new MarcXmlWriter(result, stylesheet);
		for (int m = 0; m < lst.size(); m++){
			Record r = lst.get(m);
			if (r != null && r.getContent() != null) {
				try{
					in = new ByteArrayInputStream(r.getContent(),0,r.getContent().length);
					reader = new MarcStreamReader(in, encoding);
					record = reader.next();
					writer.write(record);
				}catch (MarcException e) {
					e.printStackTrace();
					System.out.println(r.render());
				}
			}
			else{
				fileOutputStream.close();
				return -1;
			}
		}
		writer.close();
		byte[] bytes = out.toByteArray();
		fileOutputStream.write(bytes);
		fileOutputStream.close();
		return 0;
	}
}
