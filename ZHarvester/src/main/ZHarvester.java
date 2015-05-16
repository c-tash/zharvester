package main;

import java.io.File;
import java.io.FileWriter;
import java.lang.reflect.Field;
import java.util.Date;
import java.util.List;

import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import org.yaz4j.Connection;
import org.yaz4j.PrefixQuery;
import org.yaz4j.Record;
import org.yaz4j.ResultSet;
import org.yaz4j.exception.ZoomException;

import ru.umeta.harvesting.base.IHarvester;
import ru.umeta.harvesting.base.model.Query;

/*EndURL - адрес сервера в формате url:port/database, например, opac.ruslan.ru:210/lounb
 *StartURL - место для сохранения полученных данных (по умолчанию в папке results рядом с jar файлом)
 *Reg - количество записей в одном сохраняемом файле (по умолчанию - 1000)
 *Active - если равен "save", то сохранять файлы, иначе не сохранять (для получения только информации о том, сколько файлов доступно)
 *Struct_loc - язык для поиска данных, например, rus или eng (по умолчанию все языки)
 *Time - кодировка, если известна, иначе CharsetDetector будет распозновать */

public class ZHarvester implements IHarvester{
	@Override
	public int harvest(Query arg0) throws Exception {
		Date date = new Date();
		String dir = ZHarvester.class.getProtectionDomain().getCodeSource().getLocation().getPath();
		//System.out.println(dir);
		if (dir.substring(0, 4).equals("file"))
			dir = dir.substring(dir.indexOf(":") + 1, dir.lastIndexOf("\\"));
		else
			dir = dir.substring(1, dir.lastIndexOf("/"));
		System.setProperty( "java.library.path", System.getProperty( "java.library.path") + ";" + dir + "/libs");
		Field fieldSysPath = ClassLoader.class.getDeclaredField( "sys_paths" );
		fieldSysPath.setAccessible( true );
		fieldSysPath.set( null, null );
		System.load(dir + "/libs/msvcr120.dll");
		System.load(dir + "/libs/libxml2.dll");
		System.load(dir + "/libs/libxslt.dll");
		System.load(dir + "/libs/yaz5.dll");
		String encoding = null;
		String addr = arg0.getEndURL();
		String queryStr = "";
		String resultsDir = dir + "/results" ;
		if(!arg0.getStartURL().equals(""))
			resultsDir = arg0.getStartURL();
		int num = 1000;
		if (!arg0.getReg().equals(""))
			num = Integer.parseInt(arg0.getReg()); //number of records in the collection
		if (!arg0.getTime().equals(""))
			encoding = arg0.getTime();
		int counter = 0;
		int res = 0;
		int numberOfRecords = 0;
		boolean no_result = true;
		Connection con = new Connection(addr, 0);
		Source stylesheet = new StreamSource(new File(dir + "/libs/MARC21slim2MODS3-5.xsl"));
		con.setSyntax("marc21");
		try {
			con.connect();
			ResultSet s = null;
			if(arg0.getStruct_loc().length() != 3)
				counter++;
			do{
				try{
					switch(counter){
					case 0:
						queryStr = "@attr 1=54 '" + arg0.getStruct_loc() + "'";
						s = con.search(new PrefixQuery(queryStr));
						break;
					case 1:
						queryStr = "@attr 2=5 @attr 1=4 '0'";
						s = con.search(new PrefixQuery(queryStr));
						break;
					case 2:
						queryStr = "couldn't search";
						s = con.search(new PrefixQuery("@or @attr 5=1 @attr 1=31 '2' @attr 5=1 @attr 1=31 '1'"));
						queryStr = "@or @attr 5=1 @attr 1=31 '2' @attr 5=1 @attr 1=31 '1'";
					}
					no_result = false;
				}catch(ZoomException e){
					if (counter == 2)
						break;
					counter++;
				}
			}while(no_result);
			if(arg0.getActive().equals("save")){
				if (s != null && (numberOfRecords = (int) s.getHitCount()) > 0 && s.getRecord(0) != null && s.getRecord(0).getSyntax() != null && (s.getRecord(0).getSyntax().equalsIgnoreCase("usmarc")
						|| s.getRecord(0).getSyntax().equalsIgnoreCase("marc21"))){
					int i = 0;
					while (((encoding == null && i < s.getHitCount()) || (i < s.getHitCount() && !encoding.equals("ISO-8859-7") && !encoding.equals("ISO-8859-1")
							&& !encoding.equals("KOI8-R") && !encoding.equals("WINDOWS-1251") && !encoding.equals("GB18030") && !encoding.equals("WINDOWS-1252") && !encoding.equals("UTF-8"))) && i < 15){
						encoding = CharsetDetector.Detect(s.getRecord(i).getContent());
						i++;
					}
					if (encoding == null)
						encoding = "KOI8-R";
		    		i = num;
		    		List<Record> lst = null;
		    		for (int k = 0; k < s.getHitCount(); k += num) { 
		    			if(s.getHitCount() - k >= num)
		    				lst = s.getRecords(k, num); //get num records starting from k
		    			else{
		    				i = (int)s.getHitCount() - k;
		    				lst = s.getRecords(k, i);
		    			}
		    			res += ResultsSaver.save(lst, resultsDir + "/" + (k / num) + ".xml", encoding, stylesheet);
		    		}
				}
			}
			FileWriter sw = new FileWriter(dir + "/info.txt",true);
			sw.write("Server: " + addr + "\n");
			sw.write("Query: " + queryStr + "\n");
			if (res != 0)
				sw.write("Number of saved records: " + (numberOfRecords = res) + "\n");
			else
				sw.write("Number of records (not saved): " + (numberOfRecords = (int)((s == null) ? 0 : s.getHitCount())) + "\n");
			if (arg0.getActive().equals("save"))
				sw.write("Used encoding: " + encoding + "\n");
			sw.write("Started: " + date + "\n");
			sw.write("Finished: " + (new Date()) + "\n\n");
			sw.close();
		} catch (ZoomException ze) {
			FileWriter err = new FileWriter(dir + "/errors.txt",true);
			err.write((new Date()) + "\n" + ze.getMessage());
			err.close();
			return 1;
		} finally {
			con.close();
		}
		return java.lang.Math.max(4, numberOfRecords);
	}
	public static void main(String[] args) throws Exception {
		Query q = new Query("","","opac.ruslan.ru:210/lounb","","","","","","","","save");
		System.out.println(new ZHarvester().harvest(q));
	}
}
