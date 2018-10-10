import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;

public class WriterPasos {

	public void writeDB2(Map<String, String> datos, String letraPaso, int paso, BufferedWriter writerCortex) throws IOException {
		// TODO Auto-generated method stub
		//----------------Fichero de plantilla DB2--------------------------
	    FileReader ficheroDB2 = new FileReader("C:\\Users\\0014879\\Desktop\\Cortex\\Plantillas\\JDB2.txt");
	    BufferedReader lectorDB2 = new BufferedReader(ficheroDB2);	
	    //----------------Variables------------------------------------------
	    String linea;
	    paso++;
	    String numeroPaso = (paso < 10) ? "0" + String.valueOf(paso) : String.valueOf(paso) ;
	    int contadorLinea = 0;
	    int contadorArchivos = 0;
	    
	    
	    //----------------Método---------------------------------------------
	    
	    //--------------- Miramos si hay archivos para borrar antes de ejecutar:
	    for (int i = 1; datos.containsKey("Borrar" + String.valueOf(i)); i++) {
	    	writeJBORRAF(numeroPaso, i, letraPaso, writerCortex);
	    }
	    
	    //---------------- Escribimos la plantilla JDB2
	    while((linea = lectorDB2.readLine()) != null) {
	    	contadorLinea ++;
	    	switch (contadorLinea) {
	    	case 2:
	    		linea = linea.replace("//---", "//" + letraPaso + numeroPaso);
	    		linea = linea.replace("NOMPROGR", datos.get("PGM"));
				break;
	    	case 3:
	    		linea = linea.replace("&VAR1-&VAR2-..." , datos.get("PARDB2"));
			default:
				break;
			}
	    	System.out.println("Escribimos: " + linea);
	    	writerCortex.write(linea);
	    	writerCortex.newLine();
	    }
	    lectorDB2.close();
	    
//--------------- Miramos si hay ficheros de entrada:
	    for (int i = 1; datos.containsKey("Entrada" + String.valueOf(i)); i++) {
	    	writeJFICHENT(datos, numeroPaso, i, letraPaso, writerCortex);
	    }
	}

	private void writeJFICHENT(Map<String, String> datos, String numeroPaso, int i, String letraPaso, BufferedWriter writerCortex) throws IOException {
		// TODO Auto-generated method stub
		//----------------Fichero de plantilla JFICHENT--------------------------
	    FileReader ficheroJFICHENT = new FileReader("C:\\Users\\0014879\\Desktop\\Cortex\\Plantillas\\JFICHENT.txt");
	    BufferedReader lectorJFICHENT = new BufferedReader(ficheroJFICHENT);	
	    //----------------Variables------------------------------------------
	    String linea, nombre;
	    int contadorLinea = 0;
	    
	    //----------------Método---------------------------------------------
	    while((linea = lectorJFICHENT.readLine()) != null) {
	    	contadorLinea ++;
	    	switch (contadorLinea) {
	    	case 2:
	    		nombre = datos.get("Entrada" + String.valueOf(i));
	    		for(i = nombre.length(); i < 8; i++) {
	    			nombre += " ";
	    		}
	    		linea = linea.replace("DDNAME--", nombre);
	    		break;
	    	default:
				break;
	    	}
	    	System.out.println("Escribimos: " + linea);
	    	writerCortex.write(linea);
	    	writerCortex.newLine();
	    }
	    lectorJFICHENT.close();	 
	}

	private void writeJBORRAF(String numeroPaso, int i, String letraPaso, BufferedWriter writerCortex) throws IOException {
		// TODO Auto-generated method stub
		//----------------Fichero de plantilla JBORRAF--------------------------
	    FileReader ficheroJBORRAF = new FileReader("C:\\Users\\0014879\\Desktop\\Cortex\\Plantillas\\JBORRAF.txt");
	    BufferedReader lectorJBORRAF = new BufferedReader(ficheroJBORRAF);	
	    //----------------Variables------------------------------------------
	    String linea;
	    int contadorLinea = 0;
	    
	    //----------------Método---------------------------------------------
	    while((linea = lectorJBORRAF.readLine()) != null) {
	    	contadorLinea ++;
	    	switch (contadorLinea) {
	    	case 2:
	    		linea = linea.replace("//---D-", "//" + letraPaso + numeroPaso + "D" + String.valueOf(i));
	    		break;
	    	default:
				break;
	    	}
	    	System.out.println("Escribimos: " + linea);
	    	writerCortex.write(linea);
	    	writerCortex.newLine();
	    }
	    lectorJBORRAF.close();	 
	}

	public void writeMAILTXT(Map<String, String> datos, String letraPaso, int paso, BufferedWriter writerCortex) throws IOException {
		// TODO Auto-generated method stub
				//----------------Fichero de plantilla JJMAILTXT--------------------------
			    FileReader ficheroMAILTXT = new FileReader("C:\\Users\\0014879\\Desktop\\Cortex\\Plantillas\\JMAILTXT.txt");
			    BufferedReader lectorMAILTXT = new BufferedReader(ficheroMAILTXT);	
			    //----------------Variables------------------------------------------
			    String linea;
			    paso++;
			    String numeroPaso = (paso < 10) ? "0" + String.valueOf(paso) : String.valueOf(paso) ;
			    int contadorLinea = 0;
			    
			    
			    //----------------Método---------------------------------------------
			    while((linea = lectorMAILTXT.readLine()) != null) {
			    	contadorLinea ++;
			    	switch (contadorLinea) {
			    	case 2:
			    		linea = linea.replace("//---", "//" + letraPaso + numeroPaso);
						break;
			    	case 4:
			    		linea = (datos.get("ASUNTO") == null) ? linea.trim() : linea.trim() + datos.get("ASUNTO");
			    		break;
			    	case 5:
			    		linea = (datos.get("ADREMI") == null) ? linea.trim() : linea.trim() + datos.get("ADREMI");
			    		break;
			    	case 6:
			    		linea = (datos.get("ADRDES") == null) ? linea.trim() : linea.trim() + datos.get("ADRDES");
			    		break;
			    	case 7:
			    		linea = (datos.get("ADRDE1") == null) ? linea.trim() : linea.trim() + datos.get("ADRDE1");
			    		break;
			    	case 8:
			    		linea = (datos.get("ADRDE2") == null) ? linea.trim() : linea.trim() + datos.get("ADRDE2");
			    		break;
			    	case 9:
			    		linea = (datos.get("ADRDE3") == null) ? linea.trim() : linea.trim() + datos.get("ADRDE3");
			    		break;
			    	case 10:
			    		linea = (datos.get("TIPMAIL") == null) ? linea.trim() : linea.trim() + datos.get("TIPMAIL");
			    		break;
			    	case 14:
			    		linea = (datos.get("DATAENVI") == null) ? linea.trim() : linea.trim() + datos.get("DATAENVI");
			    		break;
			    	case 15:
			    		linea = (datos.get("HORENVI") == null) ? linea.trim() : linea.trim() + datos.get("HORENVI");
			    		break;
			    	case 16:
			    		linea = (datos.get("DADA721") == null) ? linea.trim() : linea.trim() + datos.get("DADA721");
			    		break;
			    	case 17:
			    		linea = (datos.get("DADA722") == null) ? linea.trim() : linea.trim() + datos.get("DADA722");
			    		break;
			    	case 18:
			    		linea = (datos.get("DADA723") == null) ? linea.trim() : linea.trim() + datos.get("DADA723");
			    		break;
			    	case 19:
			    		linea = (datos.get("DADA724") == null) ? linea.trim() : linea.trim() + datos.get("DADA724");
			    		break;
			    	case 20:
			    		//Revisar nombre variable
			    		linea = (datos.get("DADA725") == null) ? linea.trim() : linea.trim() + datos.get("DADA725");
			    		break;
					default:
						break;
					}
			    	System.out.println("Escribimos: " + linea);
			    	writerCortex.write(linea);
			    	writerCortex.newLine();
			    }
			    lectorMAILTXT.close();		
	}
}
