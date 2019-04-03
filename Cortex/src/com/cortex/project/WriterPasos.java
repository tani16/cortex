package com.cortex.project;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import com.cortex.project.templates.JFICHSAL;
import com.cortex.project.templates.JFTPSEND;
import com.cortex.project.templates.JMAILTXT;


public class WriterPasos {
	static final String NUMLIN = "NUMLIN";
	static final String DATENVI = "DATENVI";
	//static Avisos  avisos = new Avisos();
	MetodosAux metodosAux = new MetodosAux();
	public static int pasoS = -1;
	public static Map<String, String[]> histPasos = new HashMap<>();
	public static boolean masMail = false;
	
	

	public void writeDB2(Map<String, String> datos, String letraPaso, int pasoE, BufferedWriter writerCortex) throws ExceptionCortex, IOException {
	    //----------------Variables------------------------------------------
	    String linea;
	    pasoS += 2;
	    String numeroPaso = (pasoS < 10) ? "0" + pasoS : String.valueOf(pasoS) ;
	    String numeroPasoE = (pasoE < 10) ? "0" + pasoE : String.valueOf(pasoE) ;
	    String[] valor = {"DB2", numeroPaso};
	    histPasos.put(numeroPasoE, valor);
	    int contadorLinea = 0;
	    
	    //----------------Fichero de plantilla DB2--------------------------
	    FileReader ficheroDB2 = TratamientoDeFicheros.openTemplate(Constantes.JDB2_TEMPLATE);  	    
	    BufferedReader lectorDB2 = TratamientoDeFicheros.readerTemplate(ficheroDB2);
	    
	    //----------------Método---------------------------------------------
	    //--------------- Miramos si hay archivos para borrar antes de ejecutar:
	    for (int i = 1; datos.containsKey(Constantes.BORRAR + i); i++) {
	    	if(!datos.get(Constantes.BORRAR + String.valueOf(i)).equals("No")) {
	    		writeJBORRAF(datos, numeroPaso, i, letraPaso, writerCortex, pasoE);
	    	}
	    }
	    
	    //---------------- Escribimos la plantilla JDB2
	    while((linea = lectorDB2.readLine()) != null) {
	    	contadorLinea ++;
	    	switch (contadorLinea) {
	    	case 2:
	    		linea = linea.replace(Constantes.STEP_START, "//" + letraPaso + numeroPaso);
	    		linea = linea.replace("NOMPROGR,", datos.get("PGM"));
	    		if (datos.containsKey("TIME")) {
	    			linea = linea.trim() + ",TEXEC=" + datos.get("TIME");
	    		}
	    		if(datos.containsKey(Constantes.PARDB2)) {
	    			linea = linea.trim() + ",";
	    		}
				break;
	    	case 3:
	    		if(!datos.containsKey(Constantes.PARDB2)) {
	    			continue;
	    		}
	    		if (metodosAux.checkLiteralesPARDB2(datos.get(Constantes.PARDB2).replace("*&", "-&"))) {
	    			writerCortex.write("****** LITERALES EN LOS PARAMETROS DEL PROGRAMA: " + datos.get(Constantes.PARDB2) + "*****");
	    	    	writerCortex.newLine();
	    	    	String mensaje = letraPaso + String.valueOf(pasoE) + " // Literales en el programa: " + datos.get(Constantes.PARDB2);
	    	    	Avisos.LOGGER.log(Level.INFO, mensaje);
	    		}	    		
	    		linea = linea.replace("&VAR1-&VAR2-..." , metodosAux.tratarLiteralesPARDB2(datos.get(Constantes.PARDB2).replace("*&", "-&")));
	    		break;
	    	default:
				break;
	    	}
	    	System.out.println(Constantes.WRITING + linea);
	    	writerCortex.write(linea.replaceAll(Constantes.END_SPACES,""));
	    	writerCortex.newLine();
	    }

//--------------- Miramos si hay ficheros Cortex:
	    if (datos.containsKey(Constantes.F_CORTEX)) {
	    	writeFCortex(datos, writerCortex);
	    }
//--------------- Miramos si hay ficheros de entrada:
	    for (int i = 1; datos.containsKey(Constantes.ENTRADA + String.valueOf(i)); i++) {
	    	writeJFICHENT(datos, i, letraPaso, writerCortex, pasoE);
	    }
//--------------- Miramos si hay ficheros de Salida:
	    for (int i = 1; datos.containsKey(Constantes.SALIDA + String.valueOf(i)); i++) {
	    	writeJFICHSAL(datos, i, letraPaso, writerCortex, pasoE);
	    }
//--------------- Miramos si hay reportes para informar:
	    writeReports(datos, writerCortex, pasoE, letraPaso);
//--------------- Miramos si hay IF o ENDIF:
	    writeIF(datos, writerCortex);
//--------------- Miramos si hay Comentarios:
	    writeComments(datos, writerCortex);
//--------------- Escribimos la alerta de Condicionales:
	    writeCondicionales(datos, writerCortex);    
	    
	}

	private void writeCondicionales(Map<String, String> datos, BufferedWriter writerCortex) throws IOException {
		for (int i = 1; datos.containsKey("COND" + i); i++) {
			String mensaje = mainApp.letraPaso + String.valueOf(mainApp.pasoE) + " // Condicional " + i + ": " + datos.get("COND" + i);
			Avisos.LOGGER.log(Level.INFO, mensaje);
			System.out.println(Constantes.WRITING + "*** CONDICIONAL " + i + ": " + datos.get("COND" + i));
	    	writerCortex.write("*** CONDICIONAL " + i + ": " + datos.get("COND" + i));
	    	writerCortex.newLine();
		}
	}

	private void writeFCortex(Map<String, String> datos, BufferedWriter writerCortex) throws IOException {
		System.out.println(Constantes.WRITING + "//*--FICHERO CORTEX----------------------------------------------------");
    	writerCortex.write("//*--FICHERO CORTEX----------------------------------------------------");
    	writerCortex.newLine();
		StringBuilder fCortex = new StringBuilder("//" + datos.get(Constantes.F_CORTEX));
		for(int j = fCortex.length(); j < 11; j++) {
			fCortex.append(" ");
		}
		System.out.println(Constantes.WRITING + fCortex + "DD *");
    	writerCortex.write(fCortex + "DD *");
    	writerCortex.newLine();
		for (int i = 1; datos.containsKey("FC" + i); i++) {
			System.out.println(Constantes.WRITING + datos.get("FC" + i));
	    	writerCortex.write(datos.get("FC" + i));
	    	writerCortex.newLine();
		}
	}

	public void writeIF(Map<String, String> datos, BufferedWriter writerCortex) throws IOException {
		if (datos.containsKey(Constantes.ENDIF)) {
			System.out.println(Constantes.WRITING + datos.get(Constantes.ENDIF));
	    	writerCortex.write(datos.get(Constantes.ENDIF));
	    	writerCortex.newLine();
		}
		if (datos.containsKey("IF")) {
			String valorIF = datos.get("IF");
			int index1 = valorIF.indexOf("IF " + mainApp.letraPaso);
			int index2 = valorIF.indexOf('.', index1);
			String pasoCortex = valorIF.substring(index1 + 4, index2);
			String[] infoPaso = WriterPasos.histPasos.get(pasoCortex.substring(0,2));
			if(!infoPaso[0].equals("IF referido a paso no migrado")) {
				valorIF = valorIF.replace(" " + mainApp.letraPaso + pasoCortex + ".", " " + mainApp.letraPaso + infoPaso[1] + "." + infoPaso[0] + ".");
			}else {
				valorIF = valorIF.replace("//", "/*");
				String mensaje = mainApp.letraPaso + String.valueOf(mainApp.pasoE) + " // IF referido a paso no migrado: ";
    			Avisos.LOGGER.log(Level.INFO, mensaje);
				System.out.println(Constantes.WRITING + "***** IF REFERIDO A PASO NO MIGRADO *****");
		    	writerCortex.write("***** IF REFERIDO A PASO NO MIGRADO *****");
		    	writerCortex.newLine();
			}

			System.out.println(Constantes.WRITING + valorIF);
	    	writerCortex.write(valorIF);
	    	writerCortex.newLine();
		}
		if (datos.containsKey("ELSE")) {
			System.out.println(Constantes.WRITING + datos.get("ELSE"));
	    	writerCortex.write(datos.get("ELSE"));
	    	writerCortex.newLine();
		}
	}

	private void writeReports(Map<String, String> datos, BufferedWriter writerCortex, int pasoE, String letraPaso) throws IOException, ExceptionCortex {
		Map<String, String> infoRep;
		for (int i = 1; datos.containsKey("Reporte" + i); i++) {
			infoRep = metodosAux.infoReportes(datos.get("Reporte" + i), pasoE, letraPaso);
			writerCortex.write("//*--REPORT-----------------------------------------------------------");
	    	writerCortex.newLine();
			writerCortex.write(infoRep.get("ReportKey"));
	    	writerCortex.newLine();
			infoRep.clear();
		}
	}

	public void writeComments(Map<String, String> datos, BufferedWriter writerCortex) throws IOException {
		for (int i = 1; datos.containsKey(Constantes.COMENTARIO + i); i++) {
			System.out.println(Constantes.WRITING + "//" + datos.get(Constantes.COMENTARIO + i));
	    	writerCortex.write("//" + datos.get(Constantes.COMENTARIO + i));
	    	writerCortex.newLine();
	    }
	}

	private void writeComments(Map<String, String> datos, BufferedWriter writerCortex, String tipo, int numFich) throws IOException {
		for (int i = 1; datos.containsKey(tipo + String.valueOf(numFich) + String.valueOf(i)); i++) {
			System.out.println(Constantes.WRITING + "//" + datos.get(tipo + String.valueOf(numFich) + String.valueOf(i)));
	    	writerCortex.write("//" + datos.get(tipo + String.valueOf(numFich) + String.valueOf(i)));
	    	writerCortex.newLine();
	    }
	}
	
	public void writeJFICHSAL(Map<String, String> datos, int i, String letraPaso,
			BufferedWriter writerCortex, int pasoE) throws IOException, ExceptionCortex {
	  //----------------Variables------------------------------------------
	    Map<String, String> infoFich;
	    String linea; 
	    String nombre;
	    int contadorLinea = 0;
	  //----------------Fichero de plantilla JFICHENT--------------------------
	    FileReader ficheroJFICHSAL = TratamientoDeFicheros.openTemplate(Constantes.JFICHSAL_TEMPLATE);
	    BufferedReader lectorJFICHSAL = TratamientoDeFicheros.readerTemplate(ficheroJFICHSAL);	
	  //----------------Método---------------------------------------------     
    	writeComments(datos, writerCortex, "ComFichS", i);
    
	    nombre = datos.get(Constantes.SALIDA + String.valueOf(i));
	    infoFich = metodosAux.infoFichero(pasoE, letraPaso, nombre);
	    for(int j = nombre.length(); j < 8; j++) {
			nombre += " ";
		}
	    JFICHSAL jfichsal = new JFICHSAL(letraPaso, nombre, pasoE, infoFich, datos);
	    if (!infoFich.containsKey(Constantes.DUMMY)) {
		    while((linea = lectorJFICHSAL.readLine()) != null) {
		    	contadorLinea ++;
		    	linea = jfichsal.processJFICHSAL(linea, contadorLinea);

		    	if (!jfichsal.getAvisos().equals("")) {
	    			Avisos.LOGGER.log(Level.INFO, jfichsal.getAvisos());
	    			writerCortex.write(jfichsal.getAvisos());
	    	    	writerCortex.newLine();  
		    	}
		    	if (!linea.equals("")) {
			    	System.out.println(Constantes.WRITING + linea);
			    	writerCortex.write(linea);
			    	writerCortex.newLine();
		    	}
		    }
	    }else {
	    	writerCortex.write("//*--DUMMY-----------------------------------------------------------");
	    	writerCortex.newLine();
			writerCortex.write(infoFich.get(Constantes.DUMMY));
	    	writerCortex.newLine();	
	    }
	    infoFich.clear();
    }

	public void writeJFICHENT(Map<String, String> datos, int i, String letraPaso, BufferedWriter writerCortex, int pasoE) throws IOException, ExceptionCortex {		
	    //----------------Variables------------------------------------------
	    String linea;
	    String nombre;
	    int contadorLinea = 0;
	    
	    //----------------Fichero de plantilla JFICHENT--------------------------
	    FileReader ficheroJFICHENT = TratamientoDeFicheros.openTemplate(Constantes.JFICHENT_TEMPLATE);
	    BufferedReader lectorJFICHENT = TratamientoDeFicheros.readerTemplate(ficheroJFICHENT);
	    //----------------Método---------------------------------------------
	    writeComments(datos, writerCortex, "ComFichE", i);
	    Map<String, String> infoFich;
	    
	    nombre = datos.get(Constantes.ENTRADA + i);
		for(int j = nombre.length(); j < 8; j++) {
			nombre += " ";
		}
	    infoFich = metodosAux.infoFichero(pasoE, letraPaso, nombre);
	    
	    if (!infoFich.containsKey(Constantes.DUMMY)) {
		    while((linea = lectorJFICHENT.readLine()) != null) {
		    	contadorLinea ++;
		    	switch (contadorLinea) {
		    	case 2:
		    		linea = linea.replace(Constantes.getDdname(), nombre);
		    		if(infoFich.get(Constantes.getDsn()).contains(Constantes.getCortex())) {
		    			Avisos.LOGGER.log(Level.INFO,Constantes.LOG_LIBRERIA_CORTEX);
		    			writerCortex.write(Constantes.LOG_LIBRERIA_CORTEX);
		    	    	writerCortex.newLine();
		    		}
		    		linea = linea.replace(Constantes.getDefinicionFichero(), "Z." + infoFich.get(Constantes.getDsn()));
			    	writerCortex.write(linea.replaceAll(Constantes.getEndSpaces(),""));
			    	writerCortex.newLine();
			    	linea = "";
			    	if(infoFich.containsKey("DSN1")) {
		    			Avisos.LOGGER.log(Level.INFO,"**** Fichero de entrada con varias DSN, Revisar ****");
		    			writerCortex.write("**** Fichero de entrada con varias DSN, Revisar ****");
		    	    	writerCortex.newLine();
			    	}
		    		for(int k = 1; infoFich.containsKey(Constantes.DSN+k); k++) {
		    			writerCortex.write("//         DD DISP=SHR,DSN=Z." + infoFich.get(Constantes.DSN+k));
		    			writerCortex.newLine();
		    			linea = "";
		    		}
		    		break;
		    	default:
					break;
		    	}
		    	if(i > 1 && contadorLinea == 1) {
		    		//No queremos que vuelva a escribir la primera línea de la plantilla
		    		continue;
		    	}
		    	if (!linea.equals("")) {
			    	System.out.println(Constantes.WRITING + linea);
			    	writerCortex.write(linea.replaceAll(Constantes.END_SPACES,""));
			    	writerCortex.newLine();
		    	} 
		    }
	    }else {
	    	writerCortex.write("//*--DUMMY-----------------------------------------------------------");
	    	writerCortex.newLine();
			writerCortex.write(infoFich.get(Constantes.DUMMY));
	    	writerCortex.newLine();	
	    }
    }	 

	public void writeJBORRAF(Map<String, String> datos, String numeroPaso, int i, String letraPaso, BufferedWriter writerCortex, int pasoE) throws IOException, ExceptionCortex {
		//----------------Variables------------------------------------------
	    String linea;
	    String nombre;
	    int contadorLinea = 0;
	    
	    //----------------Fichero de plantilla JBORRAF--------------------------
	    FileReader ficheroJBORRAF = TratamientoDeFicheros.openTemplate(Constantes.JBORRAF_TEMPLATE);
	    BufferedReader lectorJBORRAF = TratamientoDeFicheros.readerTemplate(ficheroJBORRAF);	
	    //----------------Método---------------------------------------------
	    Map<String, String> infoFich;
	    nombre = datos.get(Constantes.BORRAR + String.valueOf(i));
	    infoFich = metodosAux.infoFichero(pasoE, letraPaso, nombre);
	    
	    if (!infoFich.containsKey(Constantes.DUMMY)) {
		    while((linea = lectorJBORRAF.readLine()) != null) {
		    	contadorLinea ++;
		    	if(i > 1 && contadorLinea == 1) {
		    		//No queremos que vuelva a escribir la primera línea de la plantilla
		    		continue;
		    	}
		    	switch (contadorLinea) {
		    	case 2:
		    		if(i < 10) {
		    			linea = linea.replace(Constantes.DELETE_STEP_START, "//" + letraPaso + numeroPaso + "D" + i);
		    		}else {
		    			linea = linea.replace("//---D- ", "//" + letraPaso + numeroPaso + "D" + i);
		    		}
		    		if(infoFich.get(Constantes.DSN).contains(Constantes.CORTEX)) {
		    			Avisos.LOGGER.log(Level.INFO,Constantes.LOG_LIBRERIA_CORTEX);
		    			writerCortex.write(Constantes.LOG_LIBRERIA_CORTEX);
		    	    	writerCortex.newLine();
		    		}
		    		linea = linea.replace(Constantes.DEFINICION_FICHERO, infoFich.get(Constantes.DSN));
		    		break;
		    	default:
					break;
		    	}
		    	System.out.println(Constantes.WRITING + linea);
		    	writerCortex.write(linea.replaceAll(Constantes.END_SPACES,""));
		    	writerCortex.newLine();
		    }
	    } 
    }

	public void writeMAILTXT(Map<String, String> datos, String letraPaso, int pasoE, BufferedWriter writerCortex) throws IOException, ExceptionCortex {
	    //----------------Variables------------------------------------------
	    String linea;
	    pasoS += 2;
	    String numeroPaso = (pasoS < 10) ? "0" + pasoS : String.valueOf(pasoS) ;
	    String numeroPasoE = (pasoE < 10) ? "0" + pasoE : String.valueOf(pasoE) ;
	    String[] valor = {"MAIL00", numeroPaso};
	    histPasos.put(numeroPasoE, valor);
	    int contadorLinea = 0;	    
	    //----------------Fichero de plantilla JJMAILTXT--------------------------
	    FileReader ficheroMAILTXT = TratamientoDeFicheros.openTemplate(Constantes.JMAILTXT_TEMPLATE);
	    BufferedReader lectorMAILTXT = TratamientoDeFicheros.readerTemplate(ficheroMAILTXT);	
	    //----------------Método---------------------------------------------
	    
	    JMAILTXT jmailtxt = new JMAILTXT(letraPaso, numeroPaso, datos);
	    while((linea = lectorMAILTXT.readLine()) != null) {
	    	contadorLinea ++;
	    	
	    	linea = jmailtxt.processJMAILTXT(linea, contadorLinea);

	    	if (!jmailtxt.getAvisos().equals("")) {
    			Avisos.LOGGER.log(Level.INFO, jmailtxt.getAvisos());
    			writerCortex.write(jmailtxt.getAvisos());
    	    	writerCortex.newLine();  
	    	}
	    	System.out.println(Constantes.WRITING + linea);
	    	writerCortex.write(linea);
	    	writerCortex.newLine();
	    }
	    	    
	    masMail = jmailtxt.isMasMail();
	    lectorMAILTXT.close();
	    writeReports(datos, writerCortex, pasoE, letraPaso);
	    writeIF(datos, writerCortex);
	    writeComments(datos, writerCortex);
	    writeCondicionales(datos, writerCortex);   
	}

	public void writeSORT(Map<String, String> datos, String letraPaso, int pasoE, BufferedWriter writerCortex) throws IOException, ExceptionCortex {
	    //----------------Variables------------------------------------------
	    String linea;
	    pasoS += 2;
	    String numeroPaso = (pasoS < 10) ? "0" + pasoS : String.valueOf(pasoS) ;
	    String numeroPasoE = (pasoE < 10) ? "0" + pasoE : String.valueOf(pasoE) ;
	    String[] valor = {"TSSORT", numeroPaso};
	    histPasos.put(numeroPasoE, valor);
	    int contadorLinea = 0;
	    int i = 1;
	    Map<String, String> infoSortOut;
	    //----------------Fichero de plantilla SORT--------------------------
	    FileReader ficheroJSORT = TratamientoDeFicheros.openTemplate(Constantes.JSORT_TEMPLATE);
	    BufferedReader lectorJSORT = TratamientoDeFicheros.readerTemplate(ficheroJSORT);	
	    
	    //----------------Método---------------------------------------------
	    
	    infoSortOut = metodosAux.infoFichero(pasoE, letraPaso, Constantes.SORTOUT);
	    //---------------- Escribimos la plantilla JSORT
	    while((linea = lectorJSORT.readLine()) != null) {
	    	contadorLinea ++;
	    	switch (contadorLinea) {
	    	case 2:
	    		linea = linea.replace("//---D1", "//" + letraPaso + numeroPaso + "D" + i);
	    		i++;
	    		if(infoSortOut.get(Constantes.DSN).contains(Constantes.CORTEX)) {
	    			Avisos.LOGGER.log(Level.INFO,Constantes.LOG_LIBRERIA_CORTEX);
	    			writerCortex.write(Constantes.LOG_LIBRERIA_CORTEX);
	    	    	writerCortex.newLine();
	    		}
	    		linea = linea.replace(Constantes.DEFINICION_FICHERO, infoSortOut.get(Constantes.DSN));
				break;
	    	case 4:
	    		linea = linea.replace(Constantes.STEP_START, "//" + letraPaso + numeroPaso);
	    		break;
	    	case 5:
	    		ArrayList<String> sortIn;
	    		sortIn =  (ArrayList<String>) metodosAux.infoSORTIN(pasoE, letraPaso);
	    		for (int j = 0; j < sortIn.size(); j++) {
	    			System.out.println(Constantes.WRITING + sortIn.get(j));
		    		writerCortex.write(sortIn.get(j));
		    		writerCortex.newLine();
	    		}
	    		linea = "";
	    		break;
	    	case 6:
	    		if(infoSortOut.get(Constantes.DSN).contains(Constantes.CORTEX)) {
	    			Avisos.LOGGER.log(Level.INFO,Constantes.LOG_LIBRERIA_CORTEX);
	    			writerCortex.write(Constantes.LOG_LIBRERIA_CORTEX);
	    	    	writerCortex.newLine();
	    		}
	    		linea = linea.replace(Constantes.DEFINICION_FICHERO, infoSortOut.get(Constantes.DSN));
	    		break;
	    	case 8:
	    		if(infoSortOut.containsKey(Constantes.MGMTCLAS)) {
	    			linea = linea.replace("//*", "// ");
	    			linea = linea.replace(Constantes.EXLIXXXX, infoSortOut.get(Constantes.MGMTCLAS));
	    		}
	    		break;
	    	case 9:
	    		linea = linea.replace("XXX", infoSortOut.get(Constantes.LRECL));
	    		break;
	    	case 10:
	    		linea = linea.replace(Constantes.TAMANIO_FICHERO, infoSortOut.get(Constantes.DEFINICION));
	    		break;
	    	case 12:
	    		for (int j = 1; datos.containsKey("SORT" + j); j++) {
	    			if (datos.get("SORT" + j).startsWith("SORT")) {
	    				linea = linea.replace("SORT FIELDS=(X,XX,XX,X)", datos.get("SORT" + j));
	    			}else {
	    				linea = "   " + datos.get("SORT" + j); 
	    			}
	    			System.out.println(Constantes.WRITING + linea);
	    	    	writerCortex.write(linea);
	    	    	writerCortex.newLine();
	    	    	linea = "";
	    		}
	    		
	    		break;
			default:
				break;
			}
	    	if (!linea.equals("")) {
	    		System.out.println(Constantes.WRITING + linea);
	    		writerCortex.write(linea.replaceAll(Constantes.END_SPACES, Constantes.EMPTY));
	    		writerCortex.newLine();
	    	}
	    }
	    lectorJSORT.close();
	    writeReports(datos, writerCortex, pasoE, letraPaso);
	    writeIF(datos, writerCortex);
	    writeComments(datos, writerCortex);
	    writeCondicionales(datos, writerCortex);  
	}

	public void writeJFTPSEND(Map<String, String> datos, String letraPaso, int pasoE, BufferedWriter writerCortex) throws IOException, ExceptionCortex {
	    //----------------Variables------------------------------------------
	    String linea;
	    pasoS += 2;
	    String numeroPaso = (pasoS < 10) ? "0" + pasoS: String.valueOf(pasoS) ;
	    String numeroPasoE = (pasoE < 10) ? "0" + pasoE : String.valueOf(pasoE) ;
	    String[] valor = {"TSF01", numeroPaso};
	    histPasos.put(numeroPasoE, valor);
	    int contadorLinea = 0;
	    //----------------Fichero de plantilla JFTPSEND--------------------------
	    FileReader ficheroJFTPSEND = TratamientoDeFicheros.openTemplate(Constantes.JFTPSEND_TEMPLATE);
	    BufferedReader lectorJFTPSEND = TratamientoDeFicheros.readerTemplate(ficheroJFTPSEND);	
	    //----------------Método---------------------------------------------
	    JFTPSEND jftpsend = new JFTPSEND(letraPaso, numeroPaso, pasoE, datos);
	    while((linea = lectorJFTPSEND.readLine()) != null) {
	    	contadorLinea ++;
	    	
	    	linea = jftpsend.processJFTPSEND(linea, contadorLinea);
	    	
	    	if (!jftpsend.getAvisos().equals("")) {
    			Avisos.LOGGER.log(Level.INFO, jftpsend.getAvisos());
    			writerCortex.write(jftpsend.getAvisos());
    	    	writerCortex.newLine();  
	    	}
	    	System.out.println(Constantes.WRITING + linea);
	    	writerCortex.write(linea);
	    	writerCortex.newLine();
	    	
	    }
	    lectorJFTPSEND.close();		
	    writeReports(datos, writerCortex, pasoE, letraPaso);
	    writeIF(datos, writerCortex);
	    writeComments(datos, writerCortex);
	    writeCondicionales(datos, writerCortex);  
	}
	
	public void writeJFTPREB(Map<String, String> datos, String letraPaso, int pasoE, BufferedWriter writerCortex) throws IOException, ExceptionCortex {
	    //----------------Variables------------------------------------------
	    String linea;
	    pasoS += 2;
	    String numeroPaso = (pasoS < 10) ? "0" + pasoS : String.valueOf(pasoS) ;
	    String numeroPasoE = (pasoE < 10) ? "0" + pasoE : String.valueOf(pasoE) ;
	    String[] valor = {"F01", numeroPaso};
	    histPasos.put(numeroPasoE, valor);
	    int contadorLinea = 0;
	    int spaces = 0;
	    Map<String, String> infoFtpReb;
	    
		//----------------Fichero de plantilla JFTPREB--------------------------
	    FileReader ficheroJFTPREB = TratamientoDeFicheros.openTemplate(Constantes.JFTPREB_TEMPLATE);
	    BufferedReader lectorJFTPREB = TratamientoDeFicheros.readerTemplate(ficheroJFTPREB);
	    //----------------Método---------------------------------------------
	    
	    infoFtpReb = metodosAux.infoFtpReb(pasoE, letraPaso);
	    //----------------Método---------------------------------------------
	    while((linea = lectorJFTPREB.readLine()) != null) {
	    	contadorLinea ++;
	    	switch (contadorLinea) {
	    	case 2:
	    		linea = linea.replace(Constantes.STEP_START, "//" + letraPaso + numeroPaso);
	    		if(infoFtpReb.get(Constantes.DSN).contains(Constantes.CORTEX)) {
	    			Avisos.LOGGER.log(Level.INFO,Constantes.LOG_LIBRERIA_CORTEX);
	    			writerCortex.write(Constantes.LOG_LIBRERIA_CORTEX);
	    	    	writerCortex.newLine();
	    		}
	    		linea = linea.replace(Constantes.DEFINICION_FICHERO, infoFtpReb.get(Constantes.DSN));
				break;
	    	case 3:
	    		linea = linea.replace(Constantes.STEP_START, "//" + letraPaso + numeroPaso);
	    		break;
	    	case 4:
	    		//Calculamos cuantos espacios hay que añadir detrás para que no se muevan los comentarios de posición
	    		StringBuilder orig = new StringBuilder(Constantes.CAMPO_ORIG + datos.get("ORIG") + ",");
	    		spaces = 39 - orig.length();
	    		for (int j = 0; j < spaces; j++) {
	    			orig.append(" ");
	    		}
	    		linea = linea.replace("ORIG=SERVIDOR_ORIGEN,                  ", orig);
				break;
	    	case 5:
	    		if(datos.get(Constantes.FORIG).contains("_")) {
	    			String aux = "'" + datos.get(Constantes.FORIG) + "'";
	    			datos.replace(Constantes.FORIG, aux);
	    		}
	    		if(datos.get(Constantes.FORIG).contains("_&")) {
//	    			String aux = datos.get("FORIG");
//	    			aux = aux.replaceAll("_&", "-&");
//	    			datos.replace("FORIG", aux);
	    			String mensaje = letraPaso + String.valueOf(pasoE) + " // Revisar fichero -  contiene _& ";
					Avisos.LOGGER.log(Level.INFO, mensaje);
	    			System.out.println(Constantes.LOG_FICHERO_CON);
	    	    	writerCortex.write(Constantes.LOG_FICHERO_CON);
	    	    	writerCortex.newLine();
	    		}
	    		if(datos.get(Constantes.FORIG).contains("*")) {
	    			System.out.println(Constantes.LOG_FICHERO_ASTERISCOS);
			    	writerCortex.write(Constantes.LOG_FICHERO_ASTERISCOS);
			    	writerCortex.newLine();
			    	String mensaje = letraPaso + String.valueOf(pasoE) + " // Fichero con * - Avisar Aplicacion ";
					Avisos.LOGGER.log(Level.INFO, mensaje);
	    		}
	    	    StringBuilder forig = new StringBuilder("FIT=" + datos.get(Constantes.FORIG).replace("*", "****"));
	    	    if(datos.containsKey("DIR")) {
	    	    	forig.append(",");
	    	    }
	    	    spaces = 39 - forig.length();  		
	    		for (int j = 0; j < spaces; j++) {
	    			forig.append(" ");
	    		}
	    		linea = linea.replace("FIT=NOMFICHRED.TXT                     ", forig);
	    		break;
	    	case 6:
	    		if(datos.containsKey("DIR")) {
	    			linea = linea.replace("//*", "// "); 
	    			StringBuilder dir = new StringBuilder(Constantes.DIR_EQUALS + datos.get("DIR") + "'");
	    			spaces = 38 - dir.length();  		
		    		for (int j = 0; j < spaces; j++) {
		    			dir.append(" ");
		    		}
		    		linea = linea.replace("DIR=XXX                               ", dir);
	    		}
	    		break;
	    	case 7:
	    		if(infoFtpReb.get(Constantes.DSN).contains(Constantes.CORTEX)) {
	    			Avisos.LOGGER.log(Level.INFO,Constantes.LOG_LIBRERIA_CORTEX);
	    			writerCortex.write(Constantes.LOG_LIBRERIA_CORTEX);
	    	    	writerCortex.newLine();
	    		}
	    		linea = linea.replace(Constantes.DEFINICION_FICHERO, infoFtpReb.get(Constantes.DSN));
	    		break;
	    	case 9:
	    		if(infoFtpReb.containsKey(Constantes.MGMTCLAS)) {
	    			linea = linea.replace("//*", "// ");
	    			linea = linea.replace(Constantes.EXLIXXXX, infoFtpReb.get(Constantes.MGMTCLAS));
	    		}
	    		break;
	    	case 10:
	    		linea = linea.replace(Constantes.TAMANIO_FICHERO, infoFtpReb.get(Constantes.DEFINICION));
	    		break;
	    	case 11:
	    		linea = linea.replace("LONGREG", infoFtpReb.get(Constantes.LRECL));
	    		break;
			default:
				break;
			}
	    	System.out.println(Constantes.WRITING + linea);
	    	writerCortex.write(linea.replaceAll(Constantes.END_SPACES,""));
	    	writerCortex.newLine();
	    }
	    lectorJFTPREB.close();
	    writeReports(datos, writerCortex, pasoE, letraPaso);
	    writeIF(datos, writerCortex);
	    writeComments(datos, writerCortex);
	    writeCondicionales(datos, writerCortex);  
	}
	
	public void writeFTPDEL(Map<String, String> datos, String letraPaso, int pasoE, BufferedWriter writerCortex) throws IOException, ExceptionCortex {
		//----------------Fichero de plantilla JFTDEL--------------------------
	    FileReader ficheroJFTPDEL = TratamientoDeFicheros.openTemplate(Constantes.JFTPDEL_TEMPLATE);
	    BufferedReader lectorJFTPDEL = TratamientoDeFicheros.readerTemplate(ficheroJFTPDEL);
	    //----------------Variables------------------------------------------
	    String linea;
	    pasoS += 2;
	    String numeroPaso = (pasoS < 10) ? "0" + pasoS : String.valueOf(pasoS) ;
	    String numeroPasoE = (pasoE < 10) ? "0" + pasoE : String.valueOf(pasoE) ;
	    String[] valor = {"F01", numeroPaso};
	    histPasos.put(numeroPasoE, valor);
	    int contadorLinea = 0;
	    int spaces = 0;
	    //----------------Método---------------------------------------------
	    
	    while((linea = lectorJFTPDEL.readLine()) != null) {
	    	contadorLinea ++;
	    	switch (contadorLinea) {
	    	case 2:
    			linea = linea.replace(Constantes.STEP_START, "//" + letraPaso + numeroPaso);
    			break;
	    	case 3:	
	    		StringBuilder orig = new StringBuilder(Constantes.CAMPO_ORIG + datos.get("ORIG") + ",");
	    		spaces = 40 - orig.length();
	    		for (int j = 0; j < spaces; j++) {
	    			orig.append(" ");
	    		}
	    		linea = linea.replace("ORIG=SERVIDOR_ORIGEN,                   ", orig);
				break;
	    	case 4:
	    		if(datos.get(Constantes.FITXER).contains("_")) {
	    			String aux = "'" + datos.get(Constantes.FITXER) + "'";
	    			datos.replace(Constantes.FITXER, aux);
	    		}
	    		if(datos.get(Constantes.FITXER).contains("_&")) {
//	    			String aux = datos.get("FITXER");
//	    			aux = aux.replaceAll("_&", "-&");
//	    			datos.replace("FITXER", aux);
	    			String mensaje = letraPaso + String.valueOf(pasoE) + " // Revisar fichero -  contiene _& ";
					Avisos.LOGGER.log(Level.INFO, mensaje);
	    			System.out.println(Constantes.LOG_FICHERO_CON);
	    	    	writerCortex.write(Constantes.LOG_FICHERO_CON);
	    	    	writerCortex.newLine();
	    		}
	    		if(datos.get(Constantes.FITXER).contains("*")) {
	    			System.out.println(Constantes.LOG_FICHERO_ASTERISCOS);
			    	writerCortex.write(Constantes.LOG_FICHERO_ASTERISCOS);
			    	writerCortex.newLine();
			    	String mensaje = letraPaso + String.valueOf(pasoE) + " // Fichero con * - Avisar Aplicacion ";
					Avisos.LOGGER.log(Level.INFO, mensaje);
	    		}
	    	    StringBuilder forig = new StringBuilder("FIT=" + datos.get(Constantes.FITXER));
	    	    if(datos.containsKey("DIR")) {
	    	    	forig.append(",");
	    	    }
	    	    spaces = 40 - forig.length();  		
	    		for (int j = 0; j < spaces; j++) {
	    			forig.append(" ");
	    		}
	    		linea = linea.replace(Constantes.CAMPO_FIT, forig);
	    		break;
	    	case 5:
	    		if(datos.containsKey("DIR")) {
	    			linea = linea.replace("//*", "// "); 
	    			StringBuilder dir = new StringBuilder(Constantes.DIR_EQUALS + datos.get("DIR") + "'");
	    			spaces = 40 - dir.length();  		
		    		for (int j = 0; j < spaces; j++) {
		    			dir.append(" ");
		    		}
		    		linea = linea.replace(Constantes.CAMPO_DIR, dir);
	    		}
	    		break;
			default:
				break;
			}
	    	System.out.println(Constantes.WRITING + linea);
	    	writerCortex.write(linea.replaceAll(Constantes.END_SPACES,""));
	    	writerCortex.newLine();
	    }
	    lectorJFTPDEL.close();
	    writeReports(datos, writerCortex, pasoE, letraPaso);
	    writeIF(datos, writerCortex);
	    writeComments(datos, writerCortex);
	    writeCondicionales(datos, writerCortex);  
	}

	public void writeJMAILMSG(Map<String, String> datos, String letraPaso, int pasoE, BufferedWriter writerCortex) throws IOException, ExceptionCortex {	
	    //----------------Variables------------------------------------------
	    String linea;
	    pasoS += 2;
	    String numeroPaso = (pasoS < 10) ? "0" + pasoS : String.valueOf(pasoS) ;
	    String numeroPasoE = (pasoE < 10) ? "0" + pasoE : String.valueOf(pasoE) ;
	    String[] valor = {"TSMAIL04", numeroPaso};
	    histPasos.put(numeroPasoE, valor);
	    int contadorLinea = 0;
	    int spaces = 0;
		//----------------Fichero de plantilla JMAILMSG--------------------------
	    FileReader ficheroJMAILMSG = TratamientoDeFicheros.openTemplate(Constantes.JMAILMSG_TEMPLATE);
	    BufferedReader lectorJMAILMSG = TratamientoDeFicheros.readerTemplate(ficheroJMAILMSG);
	    //----------------Método---------------------------------------------    
	    
	    // Aviso paso JMAILMSG
	    String mensaje = letraPaso + String.valueOf(pasoE) + " // Revisar este Paso JMAILMSG ";
		Avisos.LOGGER.log(Level.INFO, mensaje);
		System.out.println(mensaje);
    	writerCortex.write("***** " + mensaje + " *****");
    	writerCortex.newLine();
    	//---------------------
    	
	    while((linea = lectorJMAILMSG.readLine()) != null) {
	    	contadorLinea ++;
	    	switch (contadorLinea) {
	    	case 2:
	    		linea = linea.replace(Constantes.STEP_START, "//" + letraPaso + numeroPaso);
				break;
	    	case 3:
	    		//Calculamos cuantos espacios hay que añadir detrás para que no se muevan los comentarios de posición
	    		StringBuilder dsnName = new StringBuilder("DSNAME=Z." + metodosAux.infoDSN(pasoE, letraPaso, "ENTRA1") + ",");
	    		spaces = 42 - dsnName.length();
	    		for (int j = 0; j < spaces; j++) {
	    			dsnName.append(" ");
	    		}
	    		if(dsnName.indexOf(Constantes.CORTEX) != -1) {
	    			Avisos.LOGGER.log(Level.INFO,Constantes.LOG_LIBRERIA_CORTEX);
	    			writerCortex.write(Constantes.LOG_LIBRERIA_CORTEX);
	    	    	writerCortex.newLine();
	    		}
	    		linea = linea.replace("DSNAME=,                                  ", dsnName);
				break;
	    	case 4:
	    		if(datos.get(Constantes.SORTIDA1).contains("_")) {
	    			String aux = "'" + datos.get(Constantes.SORTIDA1) + "'";
	    			datos.replace(Constantes.SORTIDA1, aux);
	    		}
	    		if(datos.get(Constantes.SORTIDA1).contains("_&")) {
//	    			String aux = datos.get("SORTIDA");
//	    			aux = aux.replaceAll("_&", "-&");
//	    			datos.replace("SORTIDA", aux);
	    			mensaje = letraPaso + String.valueOf(pasoE) + " // Revisar fichero -  contiene _& ";
					Avisos.LOGGER.log(Level.INFO, mensaje);
	    			System.out.println(Constantes.LOG_FICHERO_CON);
	    	    	writerCortex.write(Constantes.LOG_FICHERO_CON);
	    	    	writerCortex.newLine();
	    		}
	    		//Calculamos cuantos espacios hay que añadir detrás para que no se muevan los comentarios de posición
	    		StringBuilder fitTxt = new StringBuilder(Constantes.CAMPO_FITTXT + datos.get(Constantes.SORTIDA1));
	    		spaces = 42 - fitTxt.length();
	    		for (int j = 0; j < spaces; j++) {
	    			fitTxt.append(" ");
	    		}
	    		linea = linea.replace("FITTXT=                                   ", fitTxt);
				break;
			default:
				break;
			}
	    	System.out.println(Constantes.WRITING + linea);
	    	writerCortex.write(linea.replaceAll(Constantes.END_SPACES,""));
	    	writerCortex.newLine();
	    }
	    lectorJMAILMSG.close();	
	    writeReports(datos, writerCortex, pasoE, letraPaso);
	    writeIF(datos, writerCortex);
	    writeComments(datos, writerCortex);
	    writeCondicionales(datos, writerCortex);  
	}

	public void writeJFTPSAPP(Map<String, String> datos, String letraPaso, int pasoE, BufferedWriter writerCortex) throws IOException, ExceptionCortex {
	    //----------------Variables------------------------------------------
	    String linea;
	    pasoS += 2;
	    String numeroPaso = (pasoS < 10) ? "0" + pasoS : String.valueOf(pasoS) ;
	    String numeroPasoE = (pasoE < 10) ? "0" + pasoE : String.valueOf(pasoE) ;
	    String[] valor = {"TSF02", numeroPaso};
	    histPasos.put(numeroPasoE, valor);
	    int contadorLinea = 0;
	    int spaces = 0;
	    
		//----------------Fichero de plantilla JFTPSAPP--------------------------
	    FileReader ficheroJFTPSAPP = TratamientoDeFicheros.openTemplate(Constantes.JFTPSAPP_TEMPLATE);
	    BufferedReader lectorJFTPSAPP = TratamientoDeFicheros.readerTemplate(ficheroJFTPSAPP);	
	    //---------------- Método -----------------------------------------------
	    while((linea = lectorJFTPSAPP.readLine()) != null) {
	    	contadorLinea ++;
	    	switch (contadorLinea) {
	    	case 2:
	    		linea = linea.replace(Constantes.STEP_START, "//" + letraPaso + numeroPaso);
				break;
	    	case 3:
	    		//Calculamos cuantos espacios hay que añadir detrás para que no se muevan los comentarios de posición
	    		StringBuilder des = new StringBuilder("DES=" + datos.get("DES") + ",");
	    		spaces = 40 - des.length();
	    		for (int j = 0; j < spaces; j++) {
	    			des.append(" ");
	    		}
	    		linea = linea.replace(Constantes.CAMPO_DESTINO, des);
				break;
	    	case 4:
	    	    StringBuilder sqlin = new StringBuilder(Constantes.SQLIN_EQUALS + datos.get(Constantes.SQLIN) + "',");
	    	    spaces = 40 - sqlin.length();  		
	    		for (int j = 0; j < spaces; j++) {
	    			sqlin.append(" ");
	    		}
	    		linea = linea.replace("SQLIN=,                                 ", sqlin);
	    		break;
	    	case 5:
	    		if(datos.get(Constantes.FDEST).contains("_")) {
	    			String aux = "'" + datos.get(Constantes.FDEST) + "'";
	    			datos.replace(Constantes.FDEST, aux);
	    		}
	    		if(datos.get(Constantes.FDEST).contains("_&")) {
//	    			String aux = datos.get("FDEST");
//	    			aux = aux.replaceAll("_&", "-&");
//	    			datos.replace("FDEST", aux);
	    			String mensaje = letraPaso + String.valueOf(pasoE) + " // Revisar fichero -  contiene _& ";
					Avisos.LOGGER.log(Level.INFO, mensaje);
	    			System.out.println(Constantes.LOG_FICHERO_CON);
	    	    	writerCortex.write(Constantes.LOG_FICHERO_CON);
	    	    	writerCortex.newLine();
	    		}
	    		if(datos.get(Constantes.FDEST).contains("*")) {
	    			System.out.println(Constantes.LOG_FICHERO_ASTERISCOS);
			    	writerCortex.write(Constantes.LOG_FICHERO_ASTERISCOS);
			    	writerCortex.newLine();
			    	String mensaje = letraPaso + String.valueOf(pasoE) + " // Fichero con * - Avisar Aplicacion ";
					Avisos.LOGGER.log(Level.INFO, mensaje);
	    		}
	    		StringBuilder fit = new StringBuilder("FIT=" + datos.get(Constantes.FDEST));
	    		if(datos.containsKey("MSG") || datos.containsKey("DIR")) {
	    			fit.append(",");
	    		}
	    		spaces = 40 - fit.length();  		
	    		for (int j = 0; j < spaces; j++) {
	    			fit.append(" ");
	    		}
	    		linea = linea.replace(Constantes.CAMPO_FIT, fit);
	    		break;
	    	case 6:
	    		if(datos.containsKey("DIR")) {
	    			linea = linea.replace("//*", "// "); 
	    			StringBuilder dir = new StringBuilder(Constantes.DIR_EQUALS + datos.get("DIR") + "'");
		    		if(datos.containsKey("MSG")) {
		    			dir.append(",");
		    		}
		    		spaces = 40 - dir.length();  		
		    		for (int j = 0; j < spaces; j++) {
		    			dir.append(" ");
		    		}
		    		linea = linea.replace(Constantes.CAMPO_DIR, dir);
	    		}
	    		break;
	    	case 7:
	    		if(datos.containsKey("MSG")) {
	    			linea = linea.replace("//*", "// ");
	    			if(!datos.containsKey("MSG2")) { 
		    			StringBuilder msg = new StringBuilder(Constantes.MSG_EQUALS + datos.get("MSG").replace("-", ",") + "'");
			    		spaces = 40 - msg.length();  		
			    		for (int j = 0; j < spaces; j++) {
			    			msg.append(" ");
			    		}
			    		linea = linea.replace(Constantes.CAMPO_MSG, msg);
	    			}else {
	    				StringBuilder msg = new StringBuilder(Constantes.MSG_EQUALS + datos.get("MSG").replace("-", ",")
	    						+ datos.get("MSG2").trim().replace("-", ",") + "'");
	    				if (msg.length() > 68) {
	    					String mensaje = letraPaso + String.valueOf(pasoE) + " // Variable MSG excede de la longitud permitida - " + msg;
	    					Avisos.LOGGER.log(Level.INFO, mensaje);
	    	    			System.out.println(Constantes.LOG_REVISAR_LONGITUD);
	    	    	    	writerCortex.write(Constantes.LOG_REVISAR_LONGITUD);
	    	    	    	writerCortex.newLine();
	    				}
	    				linea = linea.replace(Constantes.CAMPO_MSG_2, msg);
	    			}
	    		}
	    		break;	
	    	default:
				break;	
	    	}
		    System.out.println(Constantes.WRITING + linea);
	    	writerCortex.write(linea.replaceAll(Constantes.END_SPACES,""));
	    	writerCortex.newLine();
	    }
	    lectorJFTPSAPP.close();
	    writeReports(datos, writerCortex, pasoE, letraPaso);
	    writeIF(datos, writerCortex);
	    writeComments(datos, writerCortex);
	    writeCondicionales(datos, writerCortex);  
	}

	public void writeJMAILANX(Map<String, String> datos, String letraPaso, int pasoE, BufferedWriter writerCortex) throws IOException, ExceptionCortex {
	    //----------------Variables------------------------------------------
	    String linea;
		String fi = "";
	    pasoS += 2;
	    String numeroPaso = (pasoS < 10) ? "0" + pasoS : String.valueOf(pasoS) ;
	    String numeroPasoE = (pasoE < 10) ? "0" + pasoE : String.valueOf(pasoE) ;
	    String[] valor = {"MAIL06", numeroPaso};
	    histPasos.put(numeroPasoE, valor);
	    int contadorLinea = 0;	    
	    ArrayList<String> salida;
	    
		//----------------Fichero de plantilla JMAILANX--------------------------
	    FileReader ficheroJMAILANX = TratamientoDeFicheros.openTemplate(Constantes.JMAILANX_TEMPLATE);
	    BufferedReader lectorJMAILANX = TratamientoDeFicheros.readerTemplate(ficheroJMAILANX);	
	    //----------------Método---------------------------------------------
	    while((linea = lectorJMAILANX.readLine()) != null) {
	    	contadorLinea ++;
	    	switch (contadorLinea) {
	    	case 2:
	    		linea = linea.replace(Constantes.STEP_START, "//" + letraPaso + numeroPaso);
				break;
	    	case 3:
	    		StringBuilder dsname = new StringBuilder("DSNAME=Z." + metodosAux.infoDSN(pasoE, letraPaso, "ENTRA1") + ", ");
	    		if(dsname.indexOf(Constantes.CORTEX) != -1) {
	    			Avisos.LOGGER.log(Level.INFO,Constantes.LOG_LIBRERIA_CORTEX);
	    			writerCortex.write(Constantes.LOG_LIBRERIA_CORTEX);
	    	    	writerCortex.newLine();
	    		}
	    		linea = linea.replace("DSNAME=,               ", dsname);
	    		break;
	    	case 4:
	    		if(datos.get(Constantes.SORTIDA1).contains("_")) {
	    			String aux = "'" + datos.get(Constantes.SORTIDA1) + "'";
	    			datos.replace(Constantes.SORTIDA1, aux);
	    		}
	    		if(datos.get(Constantes.SORTIDA1).contains("_&")) {
//	    			String aux = datos.get("SORTIDA");
//	    			aux = aux.replaceAll("_&", "-&");
//	    			datos.replace("SORTIDA", aux);
	    			String mensaje = letraPaso + String.valueOf(pasoE) + " // Revisar fichero -  contiene _& ";
					Avisos.LOGGER.log(Level.INFO, mensaje);
	    			System.out.println(Constantes.LOG_FICHERO_CON);
	    	    	writerCortex.write(Constantes.LOG_FICHERO_CON);
	    	    	writerCortex.newLine();
	    		}
	    		String aux = linea.replace("------.TXT      ", datos.get(Constantes.SORTIDA1) +" ");
	    		if (aux.length() > 72) {
	    			linea = linea.replace("FITTXT=------.TXT      <= debe ser idÃ©ntico al informado en IDEANEX", Constantes.CAMPO_FITTXT + datos.get(Constantes.SORTIDA1));
				}else {
		    		linea = linea.replace("------.TXT      ", datos.get(Constantes.SORTIDA1)+" ");
				}
	    		break;
	    	case 6:
	    		linea = (datos.get(Constantes.ASUNTO) == null) ? linea.trim() : linea.trim() + datos.get(Constantes.ASUNTO);
	    		break;
	    	case 7:
	    		linea = (datos.get(Constantes.ADREMI) == null) ? linea.trim() : linea.trim() + datos.get(Constantes.ADREMI);
	    		break;
	    	case 8:
	    		if (datos.get(Constantes.ADRDES) == null && fi == "") {
					linea = linea.trim();
				}
				else {
					salida = (ArrayList<String>) MetodosAux.checkLineSize(Constantes.ADRDES, linea, fi, datos); 
					linea = salida.get(0);
					fi = salida.get(1);
				}
    			break;
	    	case 9:
	    		if (datos.get(Constantes.ADRDE1) == null && fi == "") {
					linea = linea.trim();
				}
				else {
					salida = (ArrayList<String>) MetodosAux.checkLineSize(Constantes.ADRDE1, linea, fi, datos); 
					linea = salida.get(0);
					fi = salida.get(1);
				}
    			break;
	    	case 10:
	    		if (datos.get(Constantes.ADRDE2) == null && fi == "") {
					linea = linea.trim();
				}
				else {
					salida = (ArrayList<String>) MetodosAux.checkLineSize(Constantes.ADRDE2, linea, fi, datos); 
					linea = salida.get(0);
					fi = salida.get(1);
				}
    			break;
	    	case 11:
	    		if (datos.get(Constantes.ADRDE3) == null && fi == "") {
					linea = linea.trim();
					masMail = false;
				}
				else {
					salida = (ArrayList<String>) MetodosAux.checkLineSize(Constantes.ADRDE3, linea, fi, datos); 
					linea = salida.get(0);
					fi = salida.get(1);
					if (!fi.isEmpty()) {
						masMail = true;
						datos.put(Constantes.ADRDES, fi);
						datos.put(Constantes.ADRDE1, "");
						datos.put(Constantes.ADRDE2, "");
//		    			Avisos.LOGGER.log(Level.INFO, letraPaso + String.valueOf(pasoE) + " // No caben todos los correos - Falta añadir: " + fi);
//		    			System.out.println("Escribimos: " + "***** No caben todos los correos. Revisar  ****");
//		    	    	writerCortex.write("***** No caben todos los correos. Revisar  ****");
//		    	    	writerCortex.newLine();
		    	    	fi = "";
					}
				}
    			break;
	    	case 12:
	    		linea = (datos.get(Constantes.TIPMAIL) == null) ? linea.trim() : linea.replace("???", datos.get(Constantes.TIPMAIL));
	    		break;
	    	case 14:
	    		linea = (datos.get(Constantes.UIDPETI) == null) ? linea.trim() : linea.trim() + datos.get(Constantes.UIDPETI);
	    		break;
	    	case 15:
	    		linea = (datos.get(Constantes.SORTIDA1) == null) ? linea.trim() : linea.replace("------.TXT", datos.get(Constantes.SORTIDA1));
	    		break;
	    	case 16:
	    		linea = (datos.get(DATENVI) == null) ? linea.trim() : linea.trim() + datos.get(DATENVI);
	    		break;
	    	case 17:
	    		linea = (datos.get(Constantes.HORENVI) == null) ? linea.trim() : linea.trim() + datos.get(Constantes.HORENVI);
	    		break;
	    	case 18:
				if (datos.get(Constantes.DADA721) == null && fi == "") {
					linea = linea.trim();
				}
				else {
					salida = (ArrayList<String>) MetodosAux.checkLineSize(Constantes.DADA721, linea, fi, datos); 
					linea = salida.get(0);
					fi = salida.get(1);
				}
	    		break;
	    	case 19:
				if (datos.get(Constantes.DADA722) == null && fi == "") {
					linea = linea.trim();
				}
				else {
					salida = (ArrayList<String>) MetodosAux.checkLineSize(Constantes.DADA722, linea, fi, datos); 
					linea = salida.get(0);
					fi = salida.get(1);
				}
	    		break;
	    	case 20:
				if (datos.get(Constantes.DADA723) == null && fi == "") {
					linea = linea.trim();
				}
				else {
					salida = (ArrayList<String>) MetodosAux.checkLineSize(Constantes.DADA723, linea, fi, datos); 
					linea = salida.get(0);
					fi = salida.get(1);
				}
				break;
	    	case 21:
				if (datos.get(Constantes.DADA724) == null && fi == "") {
					linea = linea.trim();
				}
				else {
					salida = (ArrayList<String>) MetodosAux.checkLineSize(Constantes.DADA724, linea, fi, datos); 
					linea = salida.get(0);
					fi = salida.get(1);
				}
				break;
	    	case 22:
				if (datos.get(Constantes.DADA725) == null && fi == "") {
					linea = linea.trim();
				}
				else {
					salida = (ArrayList<String>) MetodosAux.checkLineSize(Constantes.DADA725, linea, fi, datos); 
					linea = salida.get(0);
					fi = salida.get(1);
				}
				break;
	    	default:
				break;
			}
	    	System.out.println(Constantes.WRITING + linea);
	    	writerCortex.write(linea.replaceAll(Constantes.END_SPACES,""));
	    	writerCortex.newLine();
	    }
	    lectorJMAILANX.close();	
	    writeReports(datos, writerCortex, pasoE, letraPaso);
	    writeIF(datos, writerCortex);
	    writeComments(datos, writerCortex);
	    writeCondicionales(datos, writerCortex);  
	}
	
	public void writeJFIVACIO(Map<String, String> datos, String letraPaso, int pasoE, BufferedWriter writerCortex) throws IOException, ExceptionCortex {
	    //----------------Variables------------------------------------------
	    String linea;
	    pasoS += 2;
	    String numeroPaso = (pasoS < 10) ? "0" + pasoS : String.valueOf(pasoS) ;
	    String numeroPasoE = (pasoE < 10) ? "0" + pasoE : String.valueOf(pasoE) ;
	    String[] valor = {"A00TS", numeroPaso};
	    histPasos.put(numeroPasoE, valor);
	    int contadorLinea = 0;
		//----------------Fichero de plantilla JFIVACIO--------------------------
	    FileReader ficheroJFIVACIO = TratamientoDeFicheros.openTemplate(Constantes.JFIVACIO_TEMPLATE);
	    BufferedReader lectorJFIVACIO = TratamientoDeFicheros.readerTemplate(ficheroJFIVACIO);	
	    //----------------Método---------------------------------------------
	    			    
	    //----------------Método---------------------------------------------
	    while((linea = lectorJFIVACIO.readLine()) != null) {
	    	contadorLinea ++;
	    	switch (contadorLinea) {
	    	case 2:			    		
	    		linea = linea.replace(Constantes.STEP_START, "//" + letraPaso + numeroPaso);	
	    		if(metodosAux.infoDSN(pasoE, letraPaso, "ENTRADA").contains(Constantes.CORTEX)) {
	    			Avisos.LOGGER.log(Level.INFO,Constantes.LOG_LIBRERIA_CORTEX);
	    			writerCortex.write(Constantes.LOG_LIBRERIA_CORTEX);
	    	    	writerCortex.newLine();
	    		}
	    		linea = linea.replace(Constantes.DEFINICION_FICHERO, metodosAux.infoDSN(pasoE, letraPaso, "ENTRADA"));
	    		if(datos.containsKey(NUMLIN) && !datos.get(NUMLIN).trim().equals("")) {
	    			linea = linea.trim() + ",LINIA=" + datos.get(NUMLIN);
	    		}
				break;			    	
			}
	    	System.out.println(Constantes.WRITING + linea);
	    	writerCortex.write(linea.replaceAll(Constantes.END_SPACES,""));
	    	writerCortex.newLine();
	    }
	    lectorJFIVACIO.close();		
	    writeReports(datos, writerCortex, pasoE, letraPaso);
	    writeIF(datos, writerCortex);
	    writeComments(datos, writerCortex);
	    writeCondicionales(datos, writerCortex);  
	}

	public void writeJOPCREC(Map<String, String> datos, String letraPaso, int pasoE, BufferedWriter writerCortex) throws IOException, ExceptionCortex {
	    //----------------Variables------------------------------------------
	    String linea;
	    pasoS += 2;
	    String numeroPaso = (pasoS < 10) ? "0" + pasoS : String.valueOf(pasoS) ;
	    String numeroPasoE = (pasoE < 10) ? "0" + pasoE : String.valueOf(pasoE) ;
	    String[] valor = {"OPCREC", numeroPaso};
	    histPasos.put(numeroPasoE, valor);
	    int contadorLinea = 0;	 
	    
		//----------------Fichero de plantilla JOPCREC--------------------------
	    FileReader ficheroJOPCREC = TratamientoDeFicheros.openTemplate(Constantes.JOPCREC_TEMPLATE);
	    BufferedReader lectorJOPCREC = TratamientoDeFicheros.readerTemplate(ficheroJOPCREC);	
	    //----------------Método---------------------------------------------
	    while((linea = lectorJOPCREC.readLine()) != null) {
	    	contadorLinea ++;
	    	switch (contadorLinea) {
	    	case 2:
	    		linea = linea.replace(Constantes.STEP_START, "//" + letraPaso + numeroPaso);
				break;
	    	case 4:
	    		linea = linea.replace("'APL.XXXXXXXX.NOMMEM'", "'Z." + datos.get("SRSTAT") + "'");
	    		break;
	    	default:
				break;
			}
	    	System.out.println(Constantes.WRITING + linea);
	    	writerCortex.write(linea.replaceAll(Constantes.END_SPACES,""));
	    	writerCortex.newLine();
	    }
	    lectorJOPCREC.close();	
	    writeReports(datos, writerCortex, pasoE, letraPaso);
	    writeIF(datos, writerCortex);
	    writeComments(datos, writerCortex);
	    writeCondicionales(datos, writerCortex);  
	}
	
	public void writeJFUSION(Map<String, String> datos, String letraPaso, int pasoE, BufferedWriter writerCortex) throws IOException, ExceptionCortex {
		// TODO Auto-generated method stub
		//----------------Fichero de plantilla JFUSION--------------------------
	    FileReader ficheroJFUSION = new FileReader("C:\\Cortex\\Plantillas\\JFUSION.txt");
	    BufferedReader lectorJFUSION = new BufferedReader(ficheroJFUSION);	
	    //----------------Variables------------------------------------------
	    String linea;
	    pasoS += 2;
	    String numeroPaso = (pasoS < 10) ? "0" + String.valueOf(pasoS) : String.valueOf(pasoS) ;
	    String numeroPasoE = (pasoE < 10) ? "0" + String.valueOf(pasoE) : String.valueOf(pasoE) ;
	    String[] valor = {"JFUSION", numeroPaso};
	    histPasos.put(numeroPasoE, valor);
	    int contadorLinea = 0;
	    //----------------Método---------------------------------------------    
	    
	    metodosAux.infoJFUSION(datos, pasoE, letraPaso);
	    
	    
	    while((linea = lectorJFUSION.readLine()) != null) {
	    	contadorLinea ++;
	    	switch (contadorLinea) {
	    	case 3:
	    		linea = linea.replace(Constantes.STEP_START, "//" + letraPaso + numeroPaso);
	    		if(datos.get(Constantes.DSN).contains(Constantes.CORTEX)) {
	    			Avisos.LOGGER.log(Level.INFO,Constantes.LOG_LIBRERIA_CORTEX);
	    			writerCortex.write(Constantes.LOG_LIBRERIA_CORTEX);
	    	    	writerCortex.newLine();
	    		}
	    		linea = linea.replace(Constantes.DEFINICION_FICHERO, datos.get(Constantes.DSN));
				break;
	    	case 5:
	    		linea = linea.replace(Constantes.STEP_START, "//" + letraPaso + numeroPaso);
	    		break;
	    	case 7:
	    		linea = linea.replace("&APLIC", datos.get("APL"));
	    		break;
	    	case 8:
	    		linea = linea.replace("&NOMQDRE", datos.get("QUADRE"));
	    		break;
	    	case 10:
	    		for(int i = 1; datos.containsKey(Constantes.DSN + i); i++) {
	    			String lineaEditada = linea;
	    			if(datos.get(Constantes.DSN + i).contains(Constantes.CORTEX)) {
		    			Avisos.LOGGER.log(Level.INFO,Constantes.LOG_LIBRERIA_CORTEX);
		    			writerCortex.write(Constantes.LOG_LIBRERIA_CORTEX);
		    	    	writerCortex.newLine();
		    		}
	    			lineaEditada = lineaEditada.replace("TSFUS01.DD----1", "TSFUS01." + datos.get("FICH" + i));
	    			if (i < 10) {
	    				lineaEditada = lineaEditada.replace("&DUMY01", "&DUMY0" + i);
	    			}else {
	    				lineaEditada = lineaEditada.replace("&DUMY01", "&DUMY" + i);
	    			} 
	    			lineaEditada = lineaEditada.replace("APL.XXXXXXXX.NOMMEM.&GENE1", "Z." + datos.get(Constantes.DSN + i));
	    			
	    			System.out.println(Constantes.WRITING + lineaEditada);
	    	    	writerCortex.write(lineaEditada);
	    	    	writerCortex.newLine();
	    		}
	    		linea = "";
	    		break;
	    	case 12:
	    		linea = "";
	    		for(int i = 1; datos.containsKey("FICHA" + i); i++){
	    			linea = datos.get("FICHA" + i);
	    			
	    			System.out.println(Constantes.WRITING + linea);
	    	    	writerCortex.write(linea);
	    	    	writerCortex.newLine();	
	    		}
	    		linea = "";
	    		break;
	    	case 13:
	    		linea = "";
	    		break;
	    	case 14:
	    		StringBuffer nameFich = new StringBuffer(datos.get("SALIDA"));
	    		for (int i = nameFich.length(); i < 9; i++) {
	    			nameFich.append(" ");
	    		}
	    		linea = linea.replace("//DDSAL--  ", "//" + nameFich);
	    		if(datos.get(Constantes.DSN).contains(Constantes.CORTEX)) {
	    			Avisos.LOGGER.log(Level.INFO,Constantes.LOG_LIBRERIA_CORTEX);
	    			writerCortex.write(Constantes.LOG_LIBRERIA_CORTEX);
	    	    	writerCortex.newLine();
	    		}
	    		linea = linea.replace(Constantes.DEFINICION_FICHERO, datos.get(Constantes.DSN));
	    		break;
	    	case 16:
	    		if (datos.containsKey(Constantes.MGMTCLAS)) {
	    			linea = linea.replace("//*", "// ");
	    			linea = linea.replace(Constantes.EXLIXXXX, datos.get(Constantes.MGMTCLAS));
	    		}
	    		break;
	    	case 17:
	    		linea = linea.replace(Constantes.TAMANIO_FICHERO, datos.get(Constantes.DEFINICION));
	    		break;
	    	case 19:
	    		linea = linea.replace("//---IF-", "//" + letraPaso + numeroPaso + "IF1");
	    		break;
	    	case 21:
	    		pasoS += 2;
	    		numeroPaso = (pasoS < 10) ? "0" + String.valueOf(pasoS) : String.valueOf(pasoS) ;
	    		for(int i = 1; datos.containsKey(Constantes.DSN + i); i++) {
	    			String lineaEditada = linea;
	    			if(datos.get(Constantes.DSN + i).contains(Constantes.CORTEX)) {
		    			Avisos.LOGGER.log(Level.INFO,Constantes.LOG_LIBRERIA_CORTEX);
		    			writerCortex.write(Constantes.LOG_LIBRERIA_CORTEX);
		    	    	writerCortex.newLine();
		    		}
	    			lineaEditada = lineaEditada.replace(Constantes.DELETE_STEP_START, "//" + letraPaso + numeroPaso + "D" + i);
	    			lineaEditada = lineaEditada.replace("APL.XXXXXXXX.NOMMEM.&GENE1", "Z." + datos.get(Constantes.DSN + i));
	    			
	    			System.out.println(Constantes.WRITING + lineaEditada);
	    	    	writerCortex.write(lineaEditada);
	    	    	writerCortex.newLine();
	    		}
	    		linea = "";
	    		break;
	    	case 22:
	    		numeroPaso = (pasoS - 2 < 10) ? "0" + String.valueOf(pasoS - 2) : String.valueOf(pasoS - 2) ;
	    		linea = linea.replace("//E---IF-", "//E" + letraPaso + numeroPaso + "IF1");
	    		break;
			default:
				break;
			}
	    	if(!linea.equals("")) {
		    	System.out.println(Constantes.WRITING + linea);
		    	writerCortex.write(linea.replaceAll(Constantes.END_SPACES,""));
		    	writerCortex.newLine();
	    	}
	    }
	    lectorJFUSION.close();
	    writeReports(datos, writerCortex, pasoE, letraPaso);
	    writeIF(datos, writerCortex);
	    writeComments(datos, writerCortex);
	    writeCondicionales(datos, writerCortex);  
	}
	
	public void writeJGENCUAD(Map<String, String> datos, String letraPaso, int pasoE, BufferedWriter writerCortex) throws IOException, ExceptionCortex {
		// TODO Auto-generated method stub
		//----------------Fichero de plantilla JGENCUAD--------------------------
	    FileReader ficheroJGENCUAD = new FileReader("C:\\Cortex\\Plantillas\\JGENCUAD.txt");
	    BufferedReader lectorJGENCUAD = new BufferedReader(ficheroJGENCUAD);	
	    //----------------Variables------------------------------------------
	    String linea;
	    pasoS += 2;
	    String numeroPaso = (pasoS < 10) ? "0" + String.valueOf(pasoS) : String.valueOf(pasoS) ;
	    String numeroPasoE = (pasoE < 10) ? "0" + String.valueOf(pasoE) : String.valueOf(pasoE) ;
	    String[] valor = {"JGENCUAD", numeroPaso};
	    histPasos.put(numeroPasoE, valor);
	    int contadorLinea = 0;
	    //----------------Método---------------------------------------------    
	    
	    metodosAux.infoJFUSION(datos, pasoE, letraPaso);
	    
	    
	    while((linea = lectorJGENCUAD.readLine()) != null) {
	    	contadorLinea ++;
	    	switch (contadorLinea) {
	    	case 3:
	    		linea = linea.replace(Constantes.STEP_START, "//" + letraPaso + numeroPaso);
	    		if(datos.get(Constantes.DSN).contains(Constantes.CORTEX)) {
	    			Avisos.LOGGER.log(Level.INFO,Constantes.LOG_LIBRERIA_CORTEX);
	    			writerCortex.write(Constantes.LOG_LIBRERIA_CORTEX);
	    	    	writerCortex.newLine();
	    		}
	    		linea = linea.replace(Constantes.DEFINICION_FICHERO, datos.get(Constantes.DSN));
				break;
	    	case 5:
	    		linea = linea.replace(Constantes.STEP_START, "//" + letraPaso + numeroPaso);
	    		break;
	    	case 7:
	    		linea = linea.replace("&APLIC", datos.get("APL"));
	    		break;
	    	case 8:
	    		linea = linea.replace("&NOMQDRE", datos.get("QUADRE"));
	    		break;
	    	case 9:
	    		for(int i = 1; datos.containsKey(Constantes.DSN + i); i++) {
	    			String lineaEditada = linea;
	    			if(datos.get(Constantes.DSN + i).contains(Constantes.CORTEX)) {
		    			Avisos.LOGGER.log(Level.INFO,Constantes.LOG_LIBRERIA_CORTEX);
		    			writerCortex.write(Constantes.LOG_LIBRERIA_CORTEX);
		    	    	writerCortex.newLine();
		    		}
	    			lineaEditada = lineaEditada.replace("TSGENQ1.DD----1", "TSGENQ1." + datos.get("FICH" + i)); 
	    			lineaEditada = lineaEditada.replace("APL.XXXXXXXX.NOMMEM.&GENE1", "Z." + datos.get(Constantes.DSN + i));
	    			
	    			System.out.println(Constantes.WRITING + lineaEditada);
	    	    	writerCortex.write(lineaEditada);
	    	    	writerCortex.newLine();
	    		}
	    		linea = "";
	    		break;
	    	case 11:
	    		linea = "";
	    		for(int i = 1; datos.containsKey("FICHA" + i); i++){
	    			linea = datos.get("FICHA" + i);
	    			
	    			System.out.println(Constantes.WRITING + linea);
	    	    	writerCortex.write(linea);
	    	    	writerCortex.newLine();	
	    		}
	    		linea = "";
	    		break;
	    	case 12:
	    		linea = "";
	    		break;
	    	case 13:
	    		StringBuffer nameFich = new StringBuffer(datos.get("SALIDA"));
	    		for (int i = nameFich.length(); i < 9; i++) {
	    			nameFich.append(" ");
	    		}
	    		linea = linea.replace("//DDSAL--  ", "//" + nameFich);
	    		if(datos.get(Constantes.DSN).contains(Constantes.CORTEX)) {
	    			Avisos.LOGGER.log(Level.INFO,Constantes.LOG_LIBRERIA_CORTEX);
	    			writerCortex.write(Constantes.LOG_LIBRERIA_CORTEX);
	    	    	writerCortex.newLine();
	    		}
	    		linea = linea.replace(Constantes.DEFINICION_FICHERO, datos.get(Constantes.DSN));
	    		break;
	    	case 15:
	    		if (datos.containsKey(Constantes.MGMTCLAS)) {
	    			linea = linea.replace("//*", "// ");
	    			linea = linea.replace(Constantes.EXLIXXXX, datos.get(Constantes.MGMTCLAS));
	    		}
	    		break;
	    	case 16:
	    		linea = linea.replace(Constantes.TAMANIO_FICHERO, datos.get(Constantes.DEFINICION));
	    		break;
	    	case 18:
	    		linea = linea.replace("//---IF-", "//" + letraPaso + numeroPaso + "IF1");
	    		break;
	    	case 20:
	    		pasoS += 2;
	    		numeroPaso = (pasoS < 10) ? "0" + String.valueOf(pasoS) : String.valueOf(pasoS) ;
	    		for(int i = 1; datos.containsKey(Constantes.DSN + i); i++) {
	    			String lineaEditada = linea;
	    			if(datos.get(Constantes.DSN + i).contains(Constantes.CORTEX)) {
		    			Avisos.LOGGER.log(Level.INFO,Constantes.LOG_LIBRERIA_CORTEX);
		    			writerCortex.write(Constantes.LOG_LIBRERIA_CORTEX);
		    	    	writerCortex.newLine();
		    		}
	    			lineaEditada = lineaEditada.replace(Constantes.DELETE_STEP_START, "//" + letraPaso + numeroPaso + "D" + i);
	    			lineaEditada = lineaEditada.replace("APL.XXXXXXXX.NOMMEM.&GENE1", "Z." + datos.get(Constantes.DSN + i));
	    			
	    			System.out.println(Constantes.WRITING + lineaEditada);
	    	    	writerCortex.write(lineaEditada);
	    	    	writerCortex.newLine();
	    		}
	    		linea = "";
	    		break;
	    	case 21:
	    		numeroPaso = (pasoS - 2 < 10) ? "0" + String.valueOf(pasoS - 2) : String.valueOf(pasoS - 2) ;
	    		linea = linea.replace("//E---IF-", "//E" + letraPaso + numeroPaso + "IF1");
	    		break;
			default:
				break;
			}
	    	if(!linea.equals("")) {
		    	System.out.println(Constantes.WRITING + linea);
		    	writerCortex.write(linea.replaceAll(Constantes.END_SPACES,""));
		    	writerCortex.newLine();
	    	}
	    }
	    lectorJGENCUAD.close();		
	    writeReports(datos, writerCortex, pasoE, letraPaso);
	    writeIF(datos, writerCortex);
	    writeComments(datos, writerCortex);
	    writeCondicionales(datos, writerCortex);  
	}

	public void writeJPAPYRUS(Map<String, String> datos, String letraPaso, int pasoE, BufferedWriter writerCortex) throws IOException, ExceptionCortex {
		// TODO Auto-generated method stub
		//----------------Fichero de plantilla JPAPYRUS--------------------------
	    FileReader ficheroJPAPYRUS = new FileReader("C:\\Cortex\\Plantillas\\JPAPYRUS.txt");
	    BufferedReader lectorJPAPYRUS = new BufferedReader(ficheroJPAPYRUS);	
	    //----------------Variables------------------------------------------
	    String linea;
	    pasoS += 2;
	    String numeroPaso = (pasoS < 10) ? "0" + String.valueOf(pasoS) : String.valueOf(pasoS) ;
	    String numeroPasoE = (pasoE < 10) ? "0" + String.valueOf(pasoE) : String.valueOf(pasoE) ;
	    String[] valor = {"PAP06", numeroPaso};
	    histPasos.put(numeroPasoE, valor);
	    int contadorLinea = 0;	    
	    
	    //----------------Método---------------------------------------------
	    while((linea = lectorJPAPYRUS.readLine()) != null) {
	    	contadorLinea ++;
	    	switch (contadorLinea) {
	    	case 2:
	    		linea = linea.replace(Constantes.STEP_START, "//" + letraPaso + numeroPaso);
				break;
	    	case 3:
	    		linea = linea.replace("DFA=XXXXXXXX", "DFA=" + datos.get("DFA"));
	    		break;
	    	case 4:
	    		linea = linea.replace("DESTI=XXXXXX", "DESTI=" + datos.get("DESTI"));
	    		break;
	    	case 5:
	    		linea = linea.replace("FORMU=XXXX", "FORMU=" + datos.get("FORMU"));
	    		break;
	    	case 6:
	    		StringBuffer entre = new StringBuffer(datos.get("ENTRE"));
	    		if (datos.containsKey("POSTPRO") || datos.containsKey("DISTRIB") || datos.containsKey("B")) {
	    			entre.append(",");
	    		}
	    		for (int k = entre.length(); k < 12; k++) {
	    			entre.append(" ");
	    		}
	    		linea = linea.replace("ENTREGAR=XXXXXXXXXX  ", "ENTREGAR=" + entre);
	    	case 7:
	    		if (datos.containsKey("POSTPRO")) {
	    			linea = linea.replace("//*", "// ");
	    			StringBuffer postpro = new StringBuffer(datos.get("POSTPRO"));
	    			if (datos.containsKey("DISTRIB") || datos.containsKey("B")) {
		    			postpro.append(",");
		    		}
	    			for (int k = postpro.length(); k < 13; k++) {
		    			postpro.append(" ");
		    		}
	    			linea = linea.replace("POSTPRO=X,           ", "POSTPRO=" + postpro);	
	    		}
	    		break;
	    	case 8:
	    		if (datos.containsKey("DISTRIB")) {
	    			linea = linea.replace("//*", "// ");
	    			StringBuffer distrib = new StringBuffer(datos.get("DISTRIB"));
	    			if (datos.containsKey("B")) {
		    			distrib.append(",");
		    		}
	    			for (int k = distrib.length(); k < 13; k++) {
		    			distrib.append(" ");
		    		}
	    			linea = linea.replace("DISTRIB=X,           ", "DISTRIB=" + distrib);	
	    		}
	    		break;
	    	case 9:
	    		if (datos.containsKey("B")) {
	    			linea = linea.replace("B=X", "B=" + datos.get("B"));	
	    		}
	    		break;
	    	case 10:
	    		if(metodosAux.infoDSN(pasoE, letraPaso, "ENTRADA").contains(Constantes.CORTEX)) {
	    			Avisos.LOGGER.log(Level.INFO,Constantes.LOG_LIBRERIA_CORTEX);
	    			writerCortex.write(Constantes.LOG_LIBRERIA_CORTEX);
	    	    	writerCortex.newLine();
	    		}
	    		linea = linea.replace(Constantes.DEFINICION_FICHERO, "Z." + metodosAux.infoDSN(pasoE, letraPaso, "ENTRADA"));
	    		break;
	    	default:
				break;
			}
	    	System.out.println(Constantes.WRITING + linea);
	    	writerCortex.write(linea.replaceAll(Constantes.END_SPACES,""));
	    	writerCortex.newLine();
	    }
	    lectorJPAPYRUS.close();
	    writeReports(datos, writerCortex, pasoE, letraPaso);
	    writeIF(datos, writerCortex);
	    writeComments(datos, writerCortex);
	    writeCondicionales(datos, writerCortex);  
	}

	public void writeJPAUSA(Map<String, String> datos, String letraPaso, int pasoE, BufferedWriter writerCortex) throws IOException, ExceptionCortex {
		// TODO Auto-generated method stub
		//----------------Fichero de plantilla JPAUSA--------------------------
	    FileReader ficheroJPAUSA = new FileReader("C:\\Cortex\\Plantillas\\JPAUSA.txt");
	    BufferedReader lectorJPAUSA = new BufferedReader(ficheroJPAUSA);	
	    //----------------Variables------------------------------------------
	    String linea;
	    pasoS += 2;
	    String numeroPaso = (pasoS < 10) ? "0" + String.valueOf(pasoS) : String.valueOf(pasoS) ;
	    String numeroPasoE = (pasoE < 10) ? "0" + String.valueOf(pasoE) : String.valueOf(pasoE) ;
	    String[] valor = {"JPAUSA", numeroPaso};
	    histPasos.put(numeroPasoE, valor);
	    int contadorLinea = 0;
	    //----------------Método---------------------------------------------    
	    while((linea = lectorJPAUSA.readLine()) != null) {
	    	contadorLinea ++;
	    	switch (contadorLinea) {
	    	case 2:
	    		linea = linea.replace("//---P", "//" + letraPaso + numeroPaso);
	    		linea = linea.replace("TTT", datos.get("PARM"));
				break;
	    	default:
				break;
			}
	    	System.out.println(Constantes.WRITING + linea);
	    	writerCortex.write(linea.replaceAll(Constantes.END_SPACES,""));
	    	writerCortex.newLine();
	    }
	    lectorJPAUSA.close();		
	    writeReports(datos, writerCortex, pasoE, letraPaso);
	    writeIF(datos, writerCortex);
	    writeComments(datos, writerCortex);
	    writeCondicionales(datos, writerCortex);  
	}

	public void writeJSOFCHEC(Map<String, String> datos, String letraPaso, int pasoE, BufferedWriter writerCortex) throws IOException, ExceptionCortex {
		// TODO Auto-generated method stub
		//----------------Fichero de plantilla JSOFCHEC--------------------------
	    FileReader ficheroJSOFCHEC = new FileReader("C:\\Cortex\\Plantillas\\JSOFCHEC.txt");
	    BufferedReader lectorJSOFCHEC = new BufferedReader(ficheroJSOFCHEC);	
	    //----------------Variables------------------------------------------
	    String linea;
	    pasoS += 2;
	    String numeroPaso = (pasoS < 10) ? "0" + String.valueOf(pasoS) : String.valueOf(pasoS) ;
	    String numeroPasoE = (pasoE < 10) ? "0" + String.valueOf(pasoE) : String.valueOf(pasoE) ;
	    String[] valor = {"JSOFCHEC", numeroPaso};
	    histPasos.put(numeroPasoE, valor);
	    // Borrado de ficheros de salida
	    for (int i = 1; datos.containsKey(Constantes.BORRAR + String.valueOf(i)); i++) {
	    	if(!datos.get(Constantes.BORRAR + String.valueOf(i)).equals("No")) {
	    		writeJBORRAF(datos, numeroPaso, i, letraPaso, writerCortex, pasoE);
	    	}
	    }
	    
	    int contadorLinea = 0;
	    while((linea = lectorJSOFCHEC.readLine()) != null) {
	    	contadorLinea ++;
	    	switch (contadorLinea) {
	    	case 3:
	    		linea = linea.replace(Constantes.STEP_START, "//" + letraPaso + numeroPaso);
	    		if (datos.containsKey("COND1") && datos.get("COND1").equals("EVEN")){
	    			linea = linea.replace("SOFCHEC3,", "SOFCHEC3," + "COND=((EVEN)),");
	    		}
	    		
				break;
	    	case 4:
	    		String[] valores = datos.get(Constantes.PARDB2).split(" ");
	    		String[] plantillas = {"&NOMQDRE", "&FECHAQ", "&OPCIONQ"};
	    		String lineaEditada = "";
	    		int i = 0;
	    		for(i=0; i < plantillas.length; i++) {
		    		if(valores[i].startsWith("&")) {
		    			linea = linea.replace(plantillas[i], valores[i]);
		    		}else{
		    			lineaEditada = "**   SET " + plantillas[i].substring(1) +"='" + valores[i] + "'";
		    			Avisos.LOGGER.log(Level.INFO, letraPaso + String.valueOf(pasoE) + " // añadir literial cabecera PROG=SOFCHEC3");
		    			System.out.println(Constantes.WRITING + lineaEditada);
		    	    	writerCortex.write(lineaEditada);
		    	    	writerCortex.newLine();
		    		}
	    		}
	    		for(int j = i; j < valores.length; j++) {
	    			if(valores[i].startsWith("&")) {
	    				linea = linea.trim() + "-" + valores[i];
	    			}else {
	    				lineaEditada = "**   SET " + valores[i] +"='" + valores[i] + "'";
	    				Avisos.LOGGER.log(Level.INFO, letraPaso + String.valueOf(pasoE) + " // añadir literial cabecera PROG=SOFCHEC3");
		    			System.out.println(Constantes.WRITING + lineaEditada);
		    	    	writerCortex.write(lineaEditada);
		    	    	writerCortex.newLine();
		    	    	linea = linea + "-&" + valores[i];
	    			}
	    		}
	    		break;
	    	case 5:
	    		if(datos.containsKey("Salida1") || datos.containsKey("Entrada1")) {
		    		linea = "";
	    		}
	    		break;
	    	default:
				break;
			}
	    	if (!linea.equals("")) {
		    	System.out.println(Constantes.WRITING + linea);
		    	writerCortex.write(linea.replaceAll(Constantes.END_SPACES,""));
		    	writerCortex.newLine();
	    	}
	    }
	    lectorJSOFCHEC.close();		
	    
	  //--------------- Miramos si hay ficheros de entrada:
	    for (int i = 1; datos.containsKey(Constantes.ENTRADA + String.valueOf(i)); i++) {
	    	writeJFICHENT(datos, i, letraPaso, writerCortex, pasoE);
	    }
	  //--------------- Miramos si hay ficheros de Salida:
	    for (int i = 1; datos.containsKey(Constantes.SALIDA + String.valueOf(i)); i++) {
	    	writeJFICHSAL(datos, i, letraPaso, writerCortex, pasoE);
	    }
	    
	    writeReports(datos, writerCortex, pasoE, letraPaso);
	    writeIF(datos, writerCortex);
	    writeComments(datos, writerCortex);
	    writeCondicionales(datos, writerCortex);  
	}

	public void writeJSOFINF(Map<String, String> datos, String letraPaso, int pasoE, BufferedWriter writerCortex) throws IOException, ExceptionCortex {
		// TODO Auto-generated method stub
		//----------------Fichero de plantilla JSOFINF--------------------------
	    FileReader ficheroJSOFINF = new FileReader("C:\\Cortex\\Plantillas\\JSOFINF.txt");
	    BufferedReader lectorJSOFINF = new BufferedReader(ficheroJSOFINF);	
	    //----------------Variables------------------------------------------
	    String linea;
	    pasoS += 2;
	    String numeroPaso = (pasoS < 10) ? "0" + String.valueOf(pasoS) : String.valueOf(pasoS) ;
	    String numeroPasoE = (pasoE < 10) ? "0" + String.valueOf(pasoE) : String.valueOf(pasoE) ;
	    String[] valor = {"INF00", numeroPaso};
	    histPasos.put(numeroPasoE, valor); 
	    int contadorLinea = 0;
	    while((linea = lectorJSOFINF.readLine()) != null) {
	    	contadorLinea ++;
	    	switch (contadorLinea) {
	    	case 2:
	    		linea = linea.replace(Constantes.STEP_START, "//" + letraPaso + numeroPaso);
				break;
	    	case 3:
	    		linea = linea.replace("XXXXXXXX,", "'" + datos.get("TAULA") + "',");
	    		break;
	    	case 4:
	    		linea = linea.replace("XXXXXXXX,", "'" + datos.get("COL") + "',");
	    		break;
	    	case 5:
	    		int aux = Integer.parseInt(datos.get("LONG"));
	    		linea = linea.replace("XXXX,", Integer.toString(aux) + ",");
	    		break;
	    	case 6:
	    		linea = linea.replace("&YYYYYY,", datos.get("DATBAIXA") + ",");
	    		break;
	    	case 7:
	    		linea = linea.replace("XXXXXXX", "'" + datos.get("TIPREM") + "'");
	    		break;
	    	default:
				break;
			}
	    	System.out.println(Constantes.WRITING + linea);
	    	writerCortex.write(linea.replaceAll(Constantes.END_SPACES,""));
	    	writerCortex.newLine();
	    }
	    lectorJSOFINF.close();
	    writeReports(datos, writerCortex, pasoE, letraPaso);
	    writeIF(datos, writerCortex);
	    writeComments(datos, writerCortex);
	    writeCondicionales(datos, writerCortex);  
	}

	public void writeJFTPS123(Map<String, String> datos, String letraPaso, int pasoE, BufferedWriter writerCortex) throws IOException, ExceptionCortex {
		// TODO Auto-generated method stub
		//----------------Fichero de plantilla JSOFINF--------------------------
	    FileReader ficheroJFTPS123 = new FileReader("C:\\Cortex\\Plantillas\\JFTPS123.txt");
	    BufferedReader lectorJFTPS123 = new BufferedReader(ficheroJFTPS123);	
	    //----------------Variables------------------------------------------
	    String linea;
	    pasoS += 2;
	    String numeroPaso = (pasoS < 10) ? "0" + String.valueOf(pasoS) : String.valueOf(pasoS) ;
	    String numeroPasoE = (pasoE < 10) ? "0" + String.valueOf(pasoE) : String.valueOf(pasoE) ;
	    String[] valor = {"TSF02", numeroPaso};
	    histPasos.put(numeroPasoE, valor); 
	    int contadorLinea = 0, spaces = 0;
	    while((linea = lectorJFTPS123.readLine()) != null) {
	    	contadorLinea ++;
	    	switch (contadorLinea) {
	    	case 2:
	    		linea = linea.replace(Constantes.STEP_START, "//" + letraPaso + numeroPaso);
				break;
	    	case 3:
	    		StringBuffer des = new StringBuffer("DES=" + datos.get("DES") + ",");
	    	    spaces = 40 - des.length();  		
	    		for (int j = 0; j < spaces; j++) {
	    			des.append(" ");
	    		}
	    		linea = linea.replace(Constantes.CAMPO_DESTINO, des);
	    		break;
	    	case 4:
	    		StringBuffer sqlin = new StringBuffer(Constantes.SQLIN_EQUALS + datos.get(Constantes.SQLIN) + "',");
	    	    spaces = 40 - sqlin.length();  		
	    		for (int j = 0; j < spaces; j++) {
	    			sqlin.append(" ");
	    		}
	    		linea = linea.replace("SQLIN=,                                 ", sqlin);
	    		break;
	    	case 5:
	    		if(datos.get(Constantes.FDEST).contains("_")) {
	    			String aux = "'" + datos.get(Constantes.FDEST) + "'";
	    			datos.replace(Constantes.FDEST, aux);
	    		}
	    		if(datos.get(Constantes.FDEST).contains("_&")) {
//	    			String aux = datos.get("FDEST");
//	    			aux = aux.replaceAll("_&", "-&");
//	    			datos.replace("FDEST", aux);
					Avisos.LOGGER.log(Level.INFO, letraPaso + String.valueOf(pasoE) + " // Revisar fichero -  contiene _& ");
	    			System.out.println(Constantes.LOG_FICHERO_CON);
	    	    	writerCortex.write(Constantes.LOG_FICHERO_CON);
	    	    	writerCortex.newLine();
	    		}
	    		if(datos.get(Constantes.FDEST).contains("*")) {
	    			System.out.println(Constantes.LOG_FICHERO_ASTERISCOS);
			    	writerCortex.write(Constantes.LOG_FICHERO_ASTERISCOS);
			    	writerCortex.newLine();
					Avisos.LOGGER.log(Level.INFO, letraPaso + String.valueOf(pasoE) + " // Fichero con * - Avisar Aplicacion ");
	    		}
	    		StringBuffer fit = new StringBuffer("FIT=" + datos.get(Constantes.FDEST));
	    		if(datos.containsKey("MSG") || datos.containsKey("DIR")) {
	    			fit.append(",");
	    		}
	    		spaces = 40 - fit.length();  		
	    		for (int j = 0; j < spaces; j++) {
	    			fit.append(" ");
	    		}
	    		linea = linea.replace(Constantes.CAMPO_FIT, fit);
	    		break;
	    	case 6:
	    		if(datos.containsKey("DIR")) {
	    			linea = linea.replace("//*", "// "); 
	    			StringBuffer dir = new StringBuffer(Constantes.DIR_EQUALS + datos.get("DIR") + "'");
		    		if(datos.containsKey("MSG")) {
		    			dir.append(",");
		    		}
		    		spaces = 40 - dir.length();  		
		    		for (int j = 0; j < spaces; j++) {
		    			dir.append(" ");
		    		}
		    		linea = linea.replace(Constantes.CAMPO_DIR, dir);
	    		}
	    		break;
	    	case 7:
	    		if(datos.containsKey("MSG")) {
	    			linea = linea.replace("//*", "// ");
	    			if(!datos.containsKey("MSG2")) { 
		    			StringBuffer msg = new StringBuffer(Constantes.MSG_EQUALS + datos.get("MSG").replace("-", ",") + "'");
			    		spaces = 40 - msg.length();  		
			    		for (int j = 0; j < spaces; j++) {
			    			msg.append(" ");
			    		}
			    		linea = linea.replace(Constantes.CAMPO_MSG, msg);
	    			}else {
	    				StringBuffer msg = new StringBuffer(Constantes.MSG_EQUALS + datos.get("MSG").replace("-", ",")
	    						+ datos.get("MSG2").trim().replace("-", ",") + "'");
	    				if (msg.length() > 68) {
	    					Avisos.LOGGER.log(Level.INFO, letraPaso + String.valueOf(pasoE) + " // Variable MSG excede de la longitud permitida - " + msg);
	    	    			System.out.println(Constantes.LOG_REVISAR_LONGITUD);
	    	    	    	writerCortex.write(Constantes.LOG_REVISAR_LONGITUD);
	    	    	    	writerCortex.newLine();
	    				}
	    				linea = linea.replace(Constantes.CAMPO_MSG_2, msg);
	    			}
	    		}
	    		break;
	    	default:
				break;
			}
	    	System.out.println(Constantes.WRITING + linea);
	    	writerCortex.write(linea.replaceAll(Constantes.END_SPACES,""));
	    	writerCortex.newLine();
	    }
	    lectorJFTPS123.close();	
	    writeReports(datos, writerCortex, pasoE, letraPaso);
	    writeIF(datos, writerCortex);
	    writeComments(datos, writerCortex);
	    writeCondicionales(datos, writerCortex);  
	}

	public void writeJFTPVER(Map<String, String> datos, String letraPaso, int pasoE, BufferedWriter writerCortex) throws IOException, ExceptionCortex {
		// TODO Auto-generated method stub
		//----------------Fichero de plantilla JFTPVER--------------------------
	    FileReader ficheroJFTPVER = new FileReader("C:\\Cortex\\Plantillas\\JFTPVER.txt");
	    BufferedReader lectorJFTPVER = new BufferedReader(ficheroJFTPVER);	
	    //----------------Variables------------------------------------------
	    String linea;
	    pasoS += 2;
	    String numeroPaso = (pasoS < 10) ? "0" + String.valueOf(pasoS) : String.valueOf(pasoS) ;
	    String numeroPasoE = (pasoE < 10) ? "0" + String.valueOf(pasoE) : String.valueOf(pasoE) ;
	    String[] valor = {"F01", numeroPaso};
	    histPasos.put(numeroPasoE, valor); 
	    int contadorLinea = 0, spaces = 0;
	    while((linea = lectorJFTPVER.readLine()) != null) {
	    	contadorLinea ++;
	    	switch (contadorLinea) {
	    	case 2:
	    		linea = linea.replace(Constantes.STEP_START, "//" + letraPaso + numeroPaso);
				break;
	    	case 3:
	    		StringBuffer orig = new StringBuffer(Constantes.CAMPO_ORIG + datos.get("ORIG") + ",");
	    	    spaces = 40 - orig.length();  		
	    		for (int j = 0; j < spaces; j++) {
	    			orig.append(" ");
	    		}
	    		linea = linea.replace("ORIG=SERVIDOR_ORIGEN,                   ", orig);
	    		break;
	    	case 4:
	    		if(datos.get(Constantes.FITXER).contains("_")) {
	    			String aux = "'" + datos.get(Constantes.FITXER) + "'";
	    			datos.replace(Constantes.FITXER, aux);
	    		}
	    		if(datos.get(Constantes.FITXER).contains("_&")) {
//	    			String aux = datos.get("FITXER");
//	    			aux = aux.replaceAll("_&", "-&");
//	    			datos.replace("FITXER", aux);
					Avisos.LOGGER.log(Level.INFO, letraPaso + String.valueOf(pasoE) + " // Revisar fichero -  contiene _& ");
	    			System.out.println(Constantes.LOG_FICHERO_CON);
	    	    	writerCortex.write(Constantes.LOG_FICHERO_CON);
	    	    	writerCortex.newLine();
	    		}
	    		if(datos.get(Constantes.FITXER).contains("*")) {
	    			System.out.println(Constantes.LOG_FICHERO_ASTERISCOS);
			    	writerCortex.write(Constantes.LOG_FICHERO_ASTERISCOS);
			    	writerCortex.newLine();
					Avisos.LOGGER.log(Level.INFO, letraPaso + String.valueOf(pasoE) + " // Fichero con * - Avisar Aplicacion ");
	    		}
	    		StringBuffer fit = new StringBuffer("FIT=" + datos.get(Constantes.FITXER));
	    		if(datos.containsKey("DIR")) {
	    			fit.append(",");
	    		}
	    		spaces = 40 - fit.length();  		
	    		for (int j = 0; j < spaces; j++) {
	    			fit.append(" ");
	    		}
	    		linea = linea.replace(Constantes.CAMPO_FIT, fit);
	    		break;
	    	case 5:
	    		if(datos.containsKey("DIR")) {
	    			linea = linea.replace("//*", "// "); 
	    			StringBuffer dir = new StringBuffer(Constantes.DIR_EQUALS + datos.get("DIR") + "'");
		    		spaces = 40 - dir.length();  		
		    		for (int j = 0; j < spaces; j++) {
		    			dir.append(" ");
		    		}
		    		linea = linea.replace(Constantes.CAMPO_DIR, dir);
	    		}
	    		break;
	    	default:
				break;
			}
	    	System.out.println(Constantes.WRITING + linea);
	    	writerCortex.write(linea.replaceAll(Constantes.END_SPACES,""));
	    	writerCortex.newLine();
	    }
	    lectorJFTPVER.close();	
	    writeReports(datos, writerCortex, pasoE, letraPaso);
	    writeIF(datos, writerCortex);
	    writeComments(datos, writerCortex);
	    writeCondicionales(datos, writerCortex);  
	}

	public void writeJMAIL123(Map<String, String> datos, String letraPaso, int pasoE, BufferedWriter writerCortex) throws IOException, ExceptionCortex {
		// TODO Auto-generated method stub
		//----------------Fichero de plantilla JMAIL123--------------------------
	    FileReader ficheroJMAIL123 = new FileReader("C:\\Cortex\\Plantillas\\JMAIL123.txt");
	    BufferedReader lectorJMAIL123 = new BufferedReader(ficheroJMAIL123);	
	    //----------------Variables------------------------------------------
	    String linea;
		String fi = "";
	    pasoS += 2;
	    String numeroPaso = (pasoS < 10) ? "0" + String.valueOf(pasoS) : String.valueOf(pasoS) ;
	    String numeroPasoE = (pasoE < 10) ? "0" + String.valueOf(pasoE) : String.valueOf(pasoE) ;
	    String[] valor = {"MAIL06", numeroPaso};
	    histPasos.put(numeroPasoE, valor);
	    int contadorLinea = 0;
	    
	    ArrayList<String> salida = new ArrayList<String>();
	    
	    //----------------Método---------------------------------------------
	    while((linea = lectorJMAIL123.readLine()) != null) {
	    	contadorLinea ++;
	    	switch (contadorLinea) {
	    	case 2:
	    		linea = linea.replace(Constantes.STEP_START, "//" + letraPaso + numeroPaso);
				break;
	    	case 3:
	    		StringBuffer sqlin = new StringBuffer(Constantes.SQLIN_EQUALS + datos.get(Constantes.SQLIN) + "',");
	    		for (int k = sqlin.length(); k < 42; k++) {
	    			sqlin.append(" ");
	    		}
	    		linea = linea.replace("SQLIN='XXXXXXXX_XX',                      ", sqlin);
	    		break;
	    	case 4:
	    		if(datos.get(Constantes.SORTIDA1).contains("_")) {
	    			String aux = "'" + datos.get(Constantes.SORTIDA1) + "'";
	    			datos.replace(Constantes.SORTIDA1, aux);
	    		}
	    		if(datos.get(Constantes.SORTIDA1).contains("_&")) {
//	    			String aux = datos.get("SORTIDA");
//	    			aux = aux.replaceAll("_&", "-&");
//	    			datos.replace("SORTIDA", aux);
					Avisos.LOGGER.log(Level.INFO, letraPaso + String.valueOf(pasoE) + " // Revisar fichero -  contiene _& ");
	    			System.out.println(Constantes.LOG_FICHERO_CON);
	    	    	writerCortex.write(Constantes.LOG_FICHERO_CON);
	    	    	writerCortex.newLine();
	    		}
	    		StringBuffer fitTxt = new StringBuffer(Constantes.CAMPO_FITTXT + datos.get(Constantes.SORTIDA1));
	    		for (int k =  fitTxt.length(); k < 42; k++) {
	    			fitTxt.append(" ");
	    		}
	    		linea = linea.replace("FITTXT=                                   ", fitTxt);
	    		break;
	    	case 6:
	    		linea = (datos.get(Constantes.ASUNTO) == null) ? linea.trim() : linea.trim() + datos.get(Constantes.ASUNTO);
	    		break;
	    	case 7:
	    		linea = (datos.get(Constantes.ADREMI) == null) ? linea.trim() : linea.trim() + datos.get(Constantes.ADREMI);
	    		break;
	    	case 8:
	    		if (datos.get(Constantes.ADRDES) == null && fi == "") {
					linea = linea.trim();
				}
				else {
					salida = (ArrayList<String>) MetodosAux.checkLineSize(Constantes.ADRDES, linea, fi, datos); 
					linea = salida.get(0);
					fi = salida.get(1);
				}
    			break;
	    	case 9:
	    		if (datos.get(Constantes.ADRDE1) == null && fi == "") {
					linea = linea.trim();
				}
				else {
					salida = (ArrayList<String>) MetodosAux.checkLineSize(Constantes.ADRDE1, linea, fi, datos); 
					linea = salida.get(0);
					fi = salida.get(1);
				}
    			break;
	    	case 10:
	    		if (datos.get(Constantes.ADRDE2) == null && fi == "") {
					linea = linea.trim();
				}
				else {
					salida = (ArrayList<String>) MetodosAux.checkLineSize(Constantes.ADRDE2, linea, fi, datos); 
					linea = salida.get(0);
					fi = salida.get(1);
				}
    			break;
	    	case 11:
	    		if (datos.get(Constantes.ADRDE3) == null && fi == "") {
					linea = linea.trim();
				}
				else {
					salida = (ArrayList<String>) MetodosAux.checkLineSize(Constantes.ADRDE3, linea, fi, datos); 
					linea = salida.get(0);
					fi = salida.get(1);
					if (!fi.isEmpty()) {
		    			Avisos.LOGGER.log(Level.INFO, letraPaso + String.valueOf(pasoE) + " // No caben todos los correos - Falta añadir: " + fi);
		    			System.out.println(Constantes.WRITING + "***** No caben todos los correos. Revisar  ****");
		    	    	writerCortex.write("***** No caben todos los correos. Revisar  ****");
		    	    	writerCortex.newLine();
		    	    	fi = "";
					}
				}
    			break;
	    	case 12:
	    		linea = (datos.get(Constantes.TIPMAIL) == null) ? linea.trim() : linea.replace("???", datos.get(Constantes.TIPMAIL));
	    		break;
	    	case 14:
	    		linea = (datos.get(Constantes.UIDPETI) == null) ? linea.trim() : linea.trim() + datos.get(Constantes.UIDPETI);
	    		break;
	    	case 15:
	    		linea = (datos.get(Constantes.SORTIDA1) == null) ? linea.trim() : linea.replace("------.TXT", datos.get(Constantes.SORTIDA1));
	    		break;
	    	case 16:
	    		linea = (datos.get(DATENVI) == null) ? linea.trim() : linea.trim() + datos.get(DATENVI);
	    		break;
	    	case 17:
	    		linea = (datos.get(Constantes.HORENVI) == null) ? linea.trim() : linea.trim() + datos.get(Constantes.HORENVI);
	    		break;
	    	case 18:
				if (datos.get(Constantes.DADA721) == null && fi == "") {
					linea = linea.trim();
				}
				else {
					salida = (ArrayList<String>) MetodosAux.checkLineSize(Constantes.DADA721, linea, fi, datos); 
					linea = salida.get(0);
					fi = salida.get(1);
				}
	    		break;
	    	case 19:
				if (datos.get(Constantes.DADA722) == null && fi == "") {
					linea = linea.trim();
				}
				else {
					salida = (ArrayList<String>) MetodosAux.checkLineSize(Constantes.DADA722, linea, fi, datos); 
					linea = salida.get(0);
					fi = salida.get(1);
				}
	    		break;
	    	case 20:
				if (datos.get(Constantes.DADA723) == null && fi == "") {
					linea = linea.trim();
				}
				else {
					salida = (ArrayList<String>) MetodosAux.checkLineSize(Constantes.DADA723, linea, fi, datos); 
					linea = salida.get(0);
					fi = salida.get(1);
				}
				break;
	    	case 21:
				if (datos.get(Constantes.DADA724) == null && fi == "") {
					linea = linea.trim();
				}
				else {
					salida = (ArrayList<String>) MetodosAux.checkLineSize(Constantes.DADA724, linea, fi, datos); 
					linea = salida.get(0);
					fi = salida.get(1);
				}
				break;
	    	case 22:
				if (datos.get(Constantes.DADA725) == null && fi == "") {
					linea = linea.trim();
				}
				else {
					salida = (ArrayList<String>) MetodosAux.checkLineSize(Constantes.DADA725, linea, fi, datos); 
					linea = salida.get(0);
					fi = salida.get(1);
				}
				break;
	    	default:
				break;
			}
	    	System.out.println(Constantes.WRITING + linea);
	    	writerCortex.write(linea.replaceAll(Constantes.END_SPACES,""));
	    	writerCortex.newLine();
	    }
	    lectorJMAIL123.close();	
	    writeReports(datos, writerCortex, pasoE, letraPaso);
	    writeIF(datos, writerCortex);
	    writeComments(datos, writerCortex);
	    writeCondicionales(datos, writerCortex);  
	}
	
	public void writeJIEBGENE(Map<String, String> datos, String letraPaso, int pasoE, BufferedWriter writerCortex) throws IOException, ExceptionCortex {
		// TODO Auto-generated method stub
		//----------------Fichero de plantilla JIEBGCOPY--------------------------
	    FileReader ficheroJIEBGENE = new FileReader("C:\\Cortex\\Plantillas\\JIEBGENE.txt");
	    BufferedReader lectorJIEBGENE = new BufferedReader(ficheroJIEBGENE);	
	    //----------------Variables------------------------------------------
	    String linea;
	    pasoS += 2;
	    Map<String, String> infoFichOut = new HashMap<String, String>();
	    Map<String, String> infoFichIn = new HashMap<String, String>();
	    String numeroPaso = (pasoS < 10) ? "0" + String.valueOf(pasoS) : String.valueOf(pasoS) ;
	    String numeroPasoE = (pasoE < 10) ? "0" + String.valueOf(pasoE) : String.valueOf(pasoE) ;
	    String[] valor = {"INF00", numeroPaso};
	    histPasos.put(numeroPasoE, valor);
	    infoFichIn = metodosAux.infomultiDSN(pasoE, letraPaso, "SYSUT1");
	    infoFichOut = metodosAux.infoFichero(pasoE, letraPaso, "SYSUT2");
	    int contadorLinea = 0;
	    
	    while((linea = lectorJIEBGENE.readLine()) != null) {
	    	contadorLinea ++;
	    	switch (contadorLinea) {
	    	case 2:
	    		linea = linea.replace(Constantes.STEP_START, "//" + letraPaso + numeroPaso);
	    		if(metodosAux.infoDSN(pasoE, letraPaso, "SYSUT2").contains(Constantes.CORTEX)) {
	    			Avisos.LOGGER.log(Level.INFO,Constantes.LOG_LIBRERIA_CORTEX);
	    			writerCortex.write(Constantes.LOG_LIBRERIA_CORTEX);
	    	    	writerCortex.newLine();
	    		}
	    		linea = linea.replace("APL.YYYYYYYY.NOMMEM2.&FAAMMDDV", metodosAux.infoDSN(pasoE, letraPaso, "SYSUT2"));
	    		break;
	    	case 3:
	    		linea = linea.replace(Constantes.STEP_START, "//" + letraPaso + numeroPaso);
	    		break;
	    	case 4:
	    		for(int i = 0; infoFichIn.containsKey(Constantes.DSN + i); i++) {
	    			String lineaEditada = linea;
	    			if(infoFichIn.get(Constantes.DSN + i).contains(Constantes.CORTEX)) {
		    			Avisos.LOGGER.log(Level.INFO,Constantes.LOG_LIBRERIA_CORTEX);
		    			writerCortex.write(Constantes.LOG_LIBRERIA_CORTEX);
		    	    	writerCortex.newLine();
		    		}
	    			if(i==0) {
	    				lineaEditada = lineaEditada.replace("APL.XXXXXXXX.NOMMEM1.&FAAMMDDV", infoFichIn.get(Constantes.DSN + i));
	    			}else {
	    				lineaEditada = lineaEditada.replace("//SYSUT1", "//      ");
	    				lineaEditada = lineaEditada.replace("APL.XXXXXXXX.NOMMEM1.&FAAMMDDV", infoFichIn.get(Constantes.DSN + i));
	    			}
	    			System.out.println(Constantes.WRITING + lineaEditada);
	    	    	writerCortex.write(lineaEditada);
	    	    	writerCortex.newLine();
	    		}
	    		linea = "";
	    		break;
	    	case 5:
	    		if(metodosAux.infoDSN(pasoE, letraPaso, "SYSUT2").contains(Constantes.CORTEX)) {
	    			Avisos.LOGGER.log(Level.INFO,Constantes.LOG_LIBRERIA_CORTEX);
	    			writerCortex.write(Constantes.LOG_LIBRERIA_CORTEX);
	    	    	writerCortex.newLine();
	    		}
	    		linea = linea.replace("APL.YYYYYYYY.NOMMEM2.&FAAMMDDV", metodosAux.infoDSN(pasoE, letraPaso, "SYSUT2"));
	    		break;
	    	case 7:
	    		if(metodosAux.infoDSN(pasoE, letraPaso, "SYSUT1").contains(Constantes.CORTEX)) {
	    			Avisos.LOGGER.log(Level.INFO,Constantes.LOG_LIBRERIA_CORTEX);
	    			writerCortex.write(Constantes.LOG_LIBRERIA_CORTEX);
	    	    	writerCortex.newLine();
	    		}
	    		linea = linea.replace("APL.XXXXXXXX.NOMMEM1.&FAAMMDDV,", metodosAux.infoDSN(pasoE, letraPaso, "SYSUT1"));
	    		if(infoFichOut.containsKey(Constantes.MGMTCLAS)) {
	    			linea = linea.trim() + ",";
	    		}
	    		break;
	    	case 8:
	    		if(infoFichOut.containsKey(Constantes.MGMTCLAS)) {
	    			linea = linea.replace(Constantes.EXLIXXXX, infoFichOut.get(Constantes.MGMTCLAS));
	    		}else {
	    			linea = linea.replace("// ", "//*");
	    		}
	    		break; 
	    	default:
				break;
			}
	    	if(!linea.equals("")) {
		    	System.out.println(Constantes.WRITING + linea);
		    	writerCortex.write(linea.replaceAll(Constantes.END_SPACES,""));
		    	writerCortex.newLine();
	    	}
	    }
	    lectorJIEBGENE.close();	
	    writeReports(datos, writerCortex, pasoE, letraPaso);
	    writeIF(datos, writerCortex);
	    writeComments(datos, writerCortex);
	    writeCondicionales(datos, writerCortex);  
	}

	public void writeJBORRARFPasos(Map<String, String> datos, String letraPaso, int pasoE,
			BufferedWriter writerCortex) throws IOException, ExceptionCortex {
		// TODO Auto-generated method stub
		//----------------Fichero de plantilla JBORRAF--------------------------
	    FileReader ficheroJBORRAF = new FileReader("C:\\Cortex\\Plantillas\\JBORRAF.txt");
	    BufferedReader lectorJBORRAF = new BufferedReader(ficheroJBORRAF);	
	    //----------------Variables------------------------------------------
	    String linea;
	    ArrayList<String> lineaProc;
	    int contadorLinea = 0;
	    pasoS += 2;
	    String numeroPaso = (pasoS < 10) ? "0" + String.valueOf(pasoS) : String.valueOf(pasoS) ;
	    //----------------Método---------------------------------------------
	    
	    while((linea = lectorJBORRAF.readLine()) != null) {
	    	contadorLinea ++;
	    	switch (contadorLinea) {
	    	case 2:
	    		linea = linea.replace(Constantes.DELETE_STEP_START, "//" + letraPaso + numeroPaso);
	    		break;
	    	default:
				break;
	    	}
	    	System.out.println(Constantes.WRITING + linea);
	    	writerCortex.write(linea);
	    	writerCortex.newLine();
	    }
	    lectorJBORRAF.close();	 
	    Avisos.LOGGER.log(Level.INFO, letraPaso + String.valueOf(pasoE) + "// DSN no encontrada PROC - Acudir a: " );
	    System.out.println("Escribimos: Revisar SYSIN Cortex");
    	writerCortex.write("***** REVISAR SYSIN Cortex");
    	writerCortex.newLine();
    	if (mainApp.withProc) {
    		lineaProc = (ArrayList<String>) metodosAux.buscaInfoProc(pasoE, letraPaso, "SYSIN");
		    for(int i = 0; i < lineaProc.size(); i++) {
		    	Avisos.LOGGER.log(Level.INFO, letraPaso + String.valueOf(pasoE) + lineaProc.get(i));
		    	System.out.println(Constantes.WRITING + lineaProc.get(i).replace("//", "**"));
		    	writerCortex.write(lineaProc.get(i).replace("//", "**"));
		    	writerCortex.newLine();
		    }
    	}
	    
	    writeReports(datos, writerCortex, pasoE, letraPaso);
	    writeIF(datos, writerCortex);
	    writeComments(datos, writerCortex);
	    writeCondicionales(datos, writerCortex);  
	}

	public void writeJFIVERDS(Map<String, String> datos, String letraPaso, int pasoE, BufferedWriter writerCortex) throws IOException, ExceptionCortex {
		// TODO Auto-generated method stub
		//----------------Fichero de plantilla JFIVERDS--------------------------
	    FileReader ficheroJFIVERDS = new FileReader("C:\\Cortex\\Plantillas\\JFIVERDS.txt");
	    BufferedReader lectorJFIVERDS = new BufferedReader(ficheroJFIVERDS);	
	    //----------------Variables------------------------------------------
	    String linea;
	    ArrayList<String> lineaProc;
	    int contadorLinea = 0;
	    pasoS += 2;
	    String numeroPaso = (pasoS < 10) ? "0" + String.valueOf(pasoS) : String.valueOf(pasoS) ;
	    String numeroPasoE = (pasoE < 10) ? "0" + String.valueOf(pasoE) : String.valueOf(pasoE) ;
	    String[] valor = {"A00TS", numeroPaso};
	    histPasos.put(numeroPasoE, valor);
	    //----------------Método---------------------------------------------
	    lineaProc = (ArrayList<String>) metodosAux.buscaInfoProc(pasoE, letraPaso, "SYSIN");
	    
	    while((linea = lectorJFIVERDS.readLine()) != null) {
	    	contadorLinea ++;
	    	switch (contadorLinea) {
	    	case 2:
	    		linea = linea.replace(Constantes.STEP_START, "//" + letraPaso + numeroPaso);
	    		break;
	    	default:
				break;
	    	}
	    	System.out.println(Constantes.WRITING + linea);
	    	writerCortex.write(linea.replaceAll(Constantes.END_SPACES,""));
	    	writerCortex.newLine();
	    }
	    lectorJFIVERDS.close();	 
	    Avisos.LOGGER.log(Level.INFO, letraPaso + String.valueOf(pasoE) + "// DSN no encontrada PROC - Acudir a: " );
	    System.out.println("Escribimos: Revisar SYSIN Cortex");
    	writerCortex.write("***** REVISAR SYSIN Cortex");
    	writerCortex.newLine();
	    for(int i = 0; i < lineaProc.size(); i++) {
	    	Avisos.LOGGER.log(Level.INFO, letraPaso + String.valueOf(pasoE) + lineaProc.get(i));
	    	System.out.println(Constantes.WRITING + lineaProc.get(i).replace("//", "**"));
	    	writerCortex.write(lineaProc.get(i).replace("//", "**"));
	    	writerCortex.newLine();
	    }
	    
	    writeReports(datos, writerCortex, pasoE, letraPaso);
	    writeIF(datos, writerCortex);
	    writeComments(datos, writerCortex);
	    writeCondicionales(datos, writerCortex);  
	}
	
	public void writeJFTBSEND(Map<String, String> datos, String letraPaso, int pasoE, BufferedWriter writerCortex) throws IOException, ExceptionCortex {
		// TODO Auto-generated method stub
		//----------------Fichero de plantilla JFTBSEND--------------------------
	    FileReader ficheroJFTBSEND = new FileReader("C:\\Cortex\\Plantillas\\JFTBSEND.txt");
	    BufferedReader lectorJFTBSEND = new BufferedReader(ficheroJFTBSEND);	
	    //----------------Variables------------------------------------------
	    String linea;
	    pasoS += 2;
	    String numeroPaso = (pasoS < 10) ? "0" + String.valueOf(pasoS) : String.valueOf(pasoS) ;
	    String numeroPasoE = (pasoE < 10) ? "0" + String.valueOf(pasoE) : String.valueOf(pasoE) ;
	    String[] valor = {"FTBSEND", numeroPaso};
	    histPasos.put(numeroPasoE, valor);
	    int contadorLinea = 0, spaces = 0;
	    
	    while((linea = lectorJFTBSEND.readLine()) != null) {
	    	contadorLinea ++;
	    	switch (contadorLinea) {
	    	case 2:
	    		linea = linea.replace(Constantes.STEP_START, "//" + letraPaso + numeroPaso);
	    		break;
	    	case 3:
	    		StringBuffer des = new StringBuffer("DES=" + datos.get("DES") + ",");
	    		spaces = 40 - des.length();
	    		for (int j = 0; j < spaces; j++) {
	    			des.append(" ");
	    		}
	    		linea = linea.replace(Constantes.CAMPO_DESTINO, des);
				break;
	    	case 4:
	    		String dsn = metodosAux.infoFTP(pasoE, letraPaso, datos.get("FHOST"));
	    		if (dsn.equals("")){
	    			Avisos.LOGGER.log(Level.INFO, letraPaso + String.valueOf(pasoE) + " // DSN Fichero no encontrada ");
	    			System.out.println(Constantes.LOG_REVISAR_FICHERO);
	    	    	writerCortex.write(Constantes.LOG_REVISAR_FICHERO);
	    	    	writerCortex.newLine();
	    		}
	    		if(dsn.contains(Constantes.CORTEX)) {
	    			Avisos.LOGGER.log(Level.INFO,Constantes.LOG_LIBRERIA_CORTEX);
	    			writerCortex.write(Constantes.LOG_LIBRERIA_CORTEX);
	    	    	writerCortex.newLine();
	    		}
	    	    StringBuffer host = new StringBuffer("HOST=Z." + dsn + ",");
	    	    spaces = 40 - host.length();  		
	    		for (int j = 0; j < spaces; j++) {
	    			host.append(" ");
	    		}
	    		linea = linea.replace("HOST=,                                  ", host);
	    		break;
	    	case 5:
	    		if(datos.get(Constantes.FDEST).contains("_")) {
	    			String aux = "'" + datos.get(Constantes.FDEST) + "'";
	    			datos.replace(Constantes.FDEST, aux);
	    		}
	    		if(datos.get(Constantes.FDEST).contains("_&")) {
//	    			String aux = datos.get("FDEST");
//	    			aux = aux.replaceAll("_&", "-&");
//	    			datos.replace("FDEST", aux);
					Avisos.LOGGER.log(Level.INFO, letraPaso + String.valueOf(pasoE) + " // Revisar fichero -  contiene _& ");
	    			System.out.println(Constantes.LOG_FICHERO_CON);
	    	    	writerCortex.write(Constantes.LOG_FICHERO_CON);
	    	    	writerCortex.newLine();
	    		}
	    		if(datos.get(Constantes.FDEST).contains("*")) {
	    			System.out.println(Constantes.LOG_FICHERO_ASTERISCOS);
			    	writerCortex.write(Constantes.LOG_FICHERO_ASTERISCOS);
			    	writerCortex.newLine();
					Avisos.LOGGER.log(Level.INFO, letraPaso + String.valueOf(pasoE) + " // Fichero con * - Avisar Aplicacion ");
	    		}
	    		StringBuffer fit = new StringBuffer("FIT=" + datos.get(Constantes.FDEST));
	    		if(datos.containsKey("MSG") || datos.containsKey("DIR")) {
	    			fit.append(",");
	    		}
	    		spaces = 40 - fit.length();  		
	    		for (int j = 0; j < spaces; j++) {
	    			fit.append(" ");
	    		}
	    		String aux = linea = linea.replace(Constantes.CAMPO_FIT, fit);
	    		if(aux.length() > 72) {
	    			linea = linea.replace("FIT=nomfichred                          <== nombre fich red", fit);
	    		}else {
	    			linea = linea.replace(Constantes.CAMPO_FIT, fit);
	    		}
	    		break;
	    	case 6:
	    		if(datos.containsKey("DIR")) {
	    			linea = linea.replace("//*", "// "); 
	    			StringBuffer dir = new StringBuffer(Constantes.DIR_EQUALS + datos.get("DIR") + "'");
		    		if(datos.containsKey("MSG")) {
		    			dir.append(",");
		    		}
		    		spaces = 40 - dir.length();  		
		    		for (int j = 0; j < spaces; j++) {
		    			dir.append(" ");
		    		}
		    		linea = linea.replace(Constantes.CAMPO_DIR, dir);
	    		}
	    		break;
	    	case 7:
	    		if(datos.containsKey("MSG")) {
	    			linea = linea.replace("//*", "// ");
	    			if(!datos.containsKey("MSG2")) { 
		    			StringBuffer msg = new StringBuffer(Constantes.MSG_EQUALS + datos.get("MSG").replace("-", ",") + "'");
			    		spaces = 40 - msg.length();  		
			    		for (int j = 0; j < spaces; j++) {
			    			msg.append(" ");
			    		}
			    		linea = linea.replace(Constantes.CAMPO_MSG, msg);
	    			}else {
	    				StringBuffer msg = new StringBuffer(Constantes.MSG_EQUALS + datos.get("MSG").replace("-", ",")
	    						+ datos.get("MSG2").trim().replace("-", ",") + "'");
	    				if (msg.length() > 68) {
	    					Avisos.LOGGER.log(Level.INFO, letraPaso + String.valueOf(pasoE) + " // Variable MSG excede de la longitud permitida - " + msg);
	    	    			System.out.println(Constantes.LOG_REVISAR_LONGITUD);
	    	    	    	writerCortex.write(Constantes.LOG_REVISAR_LONGITUD);
	    	    	    	writerCortex.newLine();
	    				}
	    				linea = linea.replace(Constantes.CAMPO_MSG_2, msg);
	    			}
	    		}
	    		break;	
	    	default:
				break;
			}
	    	if(!linea.equals("")) {
		    	System.out.println(Constantes.WRITING + linea);
		    	writerCortex.write(linea.replaceAll(Constantes.END_SPACES,""));
		    	writerCortex.newLine();
	    	}
	    }
	    lectorJFTBSEND.close();	
	    writeReports(datos, writerCortex, pasoE, letraPaso);
	    writeIF(datos, writerCortex);
	    writeComments(datos, writerCortex);
	    writeCondicionales(datos, writerCortex);  
	}

	public void writeJPGM(Map<String, String> datos, String letraPaso, int pasoE, BufferedWriter writerCortex) throws IOException, ExceptionCortex {
		// TODO Auto-generated method stub
		//----------------Fichero de plantilla JPGM--------------------------
	    FileReader ficheroJPGM = new FileReader("C:\\Cortex\\Plantillas\\JPGM.txt");
	    BufferedReader lectorJPGM = new BufferedReader(ficheroJPGM);	
	    //----------------Variables------------------------------------------
	    String linea, nombre;
	    pasoS += 2;
	    String numeroPaso = (pasoS < 10) ? "0" + String.valueOf(pasoS) : String.valueOf(pasoS) ;
	    String numeroPasoE = (pasoE < 10) ? "0" + String.valueOf(pasoE) : String.valueOf(pasoE) ;
	    String[] valor = {"JPGM", numeroPaso};
	    histPasos.put(numeroPasoE, valor);
	    int contadorLinea = 0;
	    Map<String, String> infoFich = new HashMap<String, String>();
	    
	    //----------------Método---------------------------------------------
	    
	    //--------------- Miramos si hay archivos para borrar antes de ejecutar:
	    for (int i = 1; datos.containsKey(Constantes.SALIDA + String.valueOf(i)); i++) {
	    	nombre = datos.get(Constantes.SALIDA + String.valueOf(i));
		    infoFich = metodosAux.infoFichero(pasoE, letraPaso, nombre);
		    if(infoFich.get("DISP").equals("NEW")) {
		    	datos.replace(Constantes.BORRAR + i, nombre);
		    	writeJBORRAF(datos, numeroPaso, i, letraPaso, writerCortex, pasoE);
		    }
	    }
	    
	    //---------------- Escribimos la plantilla JDB2
	    while((linea = lectorJPGM.readLine()) != null) {
	    	contadorLinea ++;
	    	switch (contadorLinea) {
	    	case 2:
	    		linea = linea.replace(Constantes.STEP_START, "//" + letraPaso + numeroPaso);
	    		linea = linea.replace("NOMPROGR", datos.get("PGM"));
	    		if(!datos.containsKey("PARM")) {
	    			linea = linea.replace(datos.get("PGM") + ",PARM=&VAR1-&VAR2-...", datos.get("PGM"));
	    		}else {
	    			if (metodosAux.checkLiteralesPARDB2(datos.get("PARM").replace("*", "-"))) {
		    			writerCortex.write("****** LITERALES EN LOS PARAMETROS DEL PROGRAMA: " + datos.get(Constantes.PARDB2) + "*****");
		    	    	writerCortex.newLine();
		    	    	Avisos.LOGGER.log(Level.INFO, letraPaso + String.valueOf(pasoE) + " // Literales en el programa: "
		    	    			+ datos.get("PARM"));
		    		}
	    			linea = linea.replace("&VAR1-&VAR2-...", "('" + datos.get("PARM").replace("*", "-") + "')");
	    		}	    		
				break;
			default:
				break;
	    	}
	    	System.out.println(Constantes.WRITING + linea);
	    	writerCortex.write(linea.replaceAll(Constantes.END_SPACES,""));
	    	writerCortex.newLine();
	    }
	    lectorJPGM.close();
//--------------- Miramos si hay ficheros Cortex:
	    if (datos.containsKey(Constantes.F_CORTEX)) {
	    	writeFCortex(datos, writerCortex);
	    }
//--------------- Miramos si hay ficheros de entrada:
	    for (int i = 1; datos.containsKey(Constantes.ENTRADA + String.valueOf(i)); i++) {
	    	writeJFICHENT(datos, i, letraPaso, writerCortex, pasoE);
	    }
//--------------- Miramos si hay ficheros de Salida:
	    for (int i = 1; datos.containsKey(Constantes.SALIDA + String.valueOf(i)); i++) {
	    	writeJFICHSAL(datos, i, letraPaso, writerCortex, pasoE);
	    }
//--------------- Miramos si hay reportes para informar:
	    writeReports(datos, writerCortex, pasoE, letraPaso);
//--------------- Miramos si hay IF o ENDIF:
	    writeIF(datos, writerCortex);
//--------------- Miramos si hay Comentarios:
	    writeComments(datos, writerCortex);
//--------------- Escribimos la alerta de Condicionales:
	    writeCondicionales(datos, writerCortex);    
	    
	}
}
