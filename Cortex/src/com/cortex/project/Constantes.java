package com.cortex.project;

public final class Constantes {
	//Logs
	static final String LOG_PROC_NOT_FOUND = "Fichero PROC no encontrado";
	static final String LOG_TEMPLATES_NOT_FOUND = "Plantilla no encontrada: ";
	static final String LOG_LIBRERIA_CORTEX = "**** FICHERO DE LIBRERÍA CORTEX - AVISAR APLICACIÓN ****";
	static final String LOG_LRECL_NOT_FOUND = "******* LRECL NO INFORMADO EN PROC - IR AL PROGRAMA*********";
	
	//Rutas
	static final String RUTA_PCL = "C:\\Cortex\\PCL.txt";
	static final String RUTA_LISTADO = "C:\\Cortex\\Listado.txt";
	static final String RUTA_PROC = "C:\\Cortex\\PROC\\";
	static final String RUTA_CNTL = "C:\\Cortex\\CNTL\\";
	static final String RUTA_TEMPLATES = "C:\\Cortex\\Plantillas\\";
	
	//Plantillas
	static final String JDB2_TEMPLATE = "JDB2.txt";
	static final String JFICHSAL_TEMPLATE = "JFICHSAL.txt";
	static final String JFICHENT_TEMPLATE = "JFICHENT.txt";
	static final String JBORRAF_TEMPLATE = "JBORRAF.txt";
	static final String JMAILTXT_TEMPLATE = "JMAILTXT.txt";
	
	//Literales
	static final String SYSTEM = "System";
	static final String EMPTY = "";
	static final String AMPERSAND = "&";
	static final String EQUALS = "=";
	static final String DEFINICION = "Definicion";
	static final String EXTENSION_TXT = ".txt";
	static final String SPACE = "SPACE";
	static final String CONSOLE_LINE = "----------------------------------------";
	static final String LRECL = "LRECL";
	static final String LECTURA = "Lectura";
	static final String MGMTCLAS = "MGMTCLAS";
	static final String REPORT_KEY = "ReportKey";
	static final String SORTIDA = "SORTIDA=";
	static final String PROC = "PROC";
	static final String CNTL = "CNTL";
	static final String BORRAR = "Borrar";
	static final String STEP_START = "//---";
	static final String PARDB2 = "PARDB2";
	static final String WRITING = "Escribimos: ";
	static final String SALIDA = "Salida";
	static final String ENTRADA = "Entrada";
	static final String F_CORTEX = "FCortex";
	static final String END_SPACES = "\\s*$";
	static final String CORTEX = "CORTEX.";
	static final String DDNAME = "DDNAME--";
	static final String DUMMY = "DUMMY";
	static final String COMENTARIO = "Comentario";
	static final String ENDIF = "ENDIF";
	static final String EXLIXXXX = "EXLIXXXX";
	static final String DEFINICION_FICHERO = "APL.XXXXXXXX.NOMMEM.&FAAMMDDV";
	static final String TAMANIO_FICHERO = "(LONGREG,(KKK,KK))";
	static final String DELETE_STEP_START = "//---D-";
	static final String ADRDE3 = "ADRDE3";
	static final String ADRDE2 = "ADRDE2";
	static final String ADRDE1 = "ADRDE1";
	static final String ADRDES = "ADRDES";
	static final String ADREMI = "ADREMI";
	static final String ASUNTO = "ASUNTO";
	

	private Constantes() {
	    throw new IllegalStateException("Utility class");
	  }
}
