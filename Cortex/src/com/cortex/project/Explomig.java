package com.cortex.project;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import javax.swing.JOptionPane;

public class Explomig {
	
	static ArrayList<String> jclTest = new ArrayList<>();
	static ArrayList<String> cntl = new ArrayList<>();
	
	public static void main(String[] args) throws IOException {
		String programa;

		
	    programa = JOptionPane.showInputDialog("Introduzca el nombre del programa:");
		programa = programa.toUpperCase(); 
		mainApp.programa = programa;
		mainApp.letraPaso = programa.substring(5,6);
		
		Explomig.migracionPREP();
		
	}
	
	public static void migracionPREP() throws IOException {
		LectorPasos lectorPasos = new LectorPasos();
		String linea;
		boolean librerias = true;	
		int i = 0;
		//-------------------------------------Ficheros-------------------------------------------------		    
	    FileWriter ficheroPREP = new FileWriter("C:\\Cortex\\EXPLOMIG\\" + mainApp.programa.substring(0,6) + ".txt");
	    
	    try (BufferedWriter writerPREP = new BufferedWriter(ficheroPREP)){
	    			
	    //----------------------------------------------------------------------------------------------	

		    jclTest = lectorJCLTest();
			
			linea = jclTest.get(0).replace(mainApp.programa + "Z", mainApp.programa + " ");
		    writerPREP.write(linea);
			writerPREP.newLine();
			
			writerPREP.write(jclTest.get(1));
			writerPREP.newLine();
			
		    if (mainApp.withCntl) {
			    migracionWithCntl(lectorPasos, writerPREP);
		    }else {
				writerPREP.write(jclTest.get(2));
				writerPREP.newLine();
		    	writerPREP.write("******* Cabecera No migrada a PREP *****");
				writerPREP.newLine();
		    }
		
			for(i = 3; i < jclTest.size(); i++) {
				if (librerias) {
					if(jclTest.get(i).startsWith("//JOBLIB  DD  ") || jclTest.get(i).startsWith("//        DD  ")) {
						linea = "";
					}else if (jclTest.get(i).startsWith("//*%")){
						librerias = false;
						linea = jclTest.get(i);
					}else {
						linea = jclTest.get(i);
					}
				}else {
					linea = jclTest.get(i);
					linea = linea.replace("PARMS=Z.", "PARMS=");
					linea = linea.replace("DSN=Z.",   "DSN=");
					linea = linea.replace("SRSTAT 'Z.", "SRSTAT '");
				}	
				if (!linea.equals("")) {
					writerPREP.write(linea);
					writerPREP.newLine();
				}
			}
		    Avisos.LOGGER.log(Level.INFO, "**** Migración a PREP realizada CORRECTAMENTE *****");	
	    }catch (Exception e) {
	    	Avisos.LOGGER.log(Level.SEVERE, "Error al escribir el fichero de migraciónPREP");
		}
	}

	private static void migracionWithCntl(LectorPasos lectorPasos, BufferedWriter writerPREP) throws ExceptionCortex{
		String linea;
		FileReader ficheroCNTL = null;
		Map<String, String> datos = new HashMap<>();
		
		try {
			ficheroCNTL = new FileReader("C:\\Cortex\\CNTL\\" + mainApp.programa.substring(0,6) + ".txt");
		}catch (Exception e) {
			Avisos.LOGGER.log(Level.SEVERE, "Fichero del CNTL no encontrado");
		}
		
		try (BufferedReader lectorCNTL = new BufferedReader(ficheroCNTL)){
			cntl.add(lectorCNTL.readLine());
		}catch (Exception e) {
			Avisos.LOGGER.log(Level.SEVERE, "Error al leer el archivo CNTL");
		}
		datos = lectorPasos.leerPaso(cntl);

		linea = jclTest.get(2).replace("MSGCLASS=X", "MSGCLASS=" + datos.get("MSGCLASS"));
		if(datos.containsKey("NOTIFY")) {
			linea = linea.replace("NOTIFY=&SYSUID", "NOTIFY=" + datos.get("NOTIFY"));
		}else {
			linea = linea.replace("NOTIFY=&SYSUID", "NOTIFY=EXRR");
		}
		
		try {
			writerPREP.write(linea);
			writerPREP.newLine();
		} catch (IOException e) {
			Avisos.LOGGER.log(Level.SEVERE, "Error al Escribir el fichero migrado a PREP");
		}
		
		
	}

	private static ArrayList<String> lectorJCLTest() {
		FileReader ficheroTEST = null;
		ArrayList<String> jcl = new ArrayList<>();
		
		try {
			ficheroTEST = new FileReader("C:\\Cortex\\Migrados\\" + mainApp.programa.substring(0,6) + ".txt");
		} catch (FileNotFoundException e1) {
			Avisos.LOGGER.log(Level.SEVERE, "Fichero del jcl TEST no encontrado");
		}	
		String linea;
		 
		try (BufferedReader lectorTEST = new BufferedReader(ficheroTEST)){
			while ((linea = lectorTEST.readLine()) != null) {
				jcl.add(linea);
			}
		}catch (Exception e) {
			Avisos.LOGGER.log(Level.SEVERE, "Error al leer el JCL de TEST");
		}
		return jcl;
		
	}
}
