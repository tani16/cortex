package com.cortex.project;

public final class Constantes {
	//Logs
	static final String LOG_PROC_NOT_FOUND = "Fichero PROC no encontrado";
	static final String LOG_TEMPLATES_NOT_FOUND = "Plantilla no encontrada: ";
	static final String LOG_LIBRERIA_CORTEX = "**** FICHERO DE LIBRERÍA CORTEX - AVISAR APLICACIÓN ****";
	static final String LOG_LRECL_NOT_FOUND = "******* LRECL NO INFORMADO EN PROC - IR AL PROGRAMA*********";
	static final String LOG_REVISAR_FICHERO = "***** REVISAR FICHERO - DSN NO ENCONTRADA *****";
	static final String LOG_FICHERO_CON = "*****REVISAR FICHERO CON _&*****";
	static final String LOG_FICHERO_ASTERISCOS = "******** FICHERO CON ASTERISCOS - AVISAR APLICACIÓN ******";
	static final String LOG_REVISAR_LONGITUD = "*****REVISAR LONGITUD MSG*****";
	
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
	static final String JSORT_TEMPLATE = "JSORT.txt";
	static final String JFTPSEND_TEMPLATE = "FTPSEND.txt";
	static final String JFTPREB_TEMPLATE = "JFTPREB.txt";
	static final String JFTPDEL_TEMPLATE = "JFTPDEL.txt";
	static final String JMAILMSG_TEMPLATE = "JMAILMSG.txt";
	static final String JFTPSAPP_TEMPLATE = "JFTPSAPP.txt";
	static final String JMAILANX_TEMPLATE = "JMAILANX.txt";
	static final String JFIVACIO_TEMPLATE = "JFIVACIO.txt";
	static final String JOPCREC_TEMPLATE = "JOPCREC.txt";
			
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
	static final String DSN = "DSN";
	static final String SORTOUT = "SORTOUT";
	static final String DADA725 = "DADA725";
	static final String DADA724 = "DADA724";
	static final String DADA723 = "DADA723";
	static final String DADA722 = "DADA722";
	static final String DADA721 = "DADA721";
	static final String HORENVI = "HORENVI";
	static final String DATAENVI = "DATAENVI";
	static final String IDEANEX = "IDEANEX";
	static final String UIDPETI = "UIDPETI";
	static final String TIPMAIL = "TIPMAIL";
	static final String CAMPO_DESTINO = "DES=destino,                            ";
	static final String FDEST = "FDEST";
	static final String CAMPO_FIT = "FIT=nomfichred                          ";
	static final String CAMPO_DIR = "DIR=XXX                                 ";
	static final String FITXER = "FITXER";
	static final String FORIG = "FORIG";
	static final String CAMPO_ORIG = "ORIG=";
	static final String CAMPO_MSG_2 = "MSG='UE----,UE----'                     <== aviso usuario (opc.)";
	static final String CAMPO_MSG = "MSG='UE----,UE----'                     ";
	static final String MSG_EQUALS = "MSG='";
	static final String DIR_EQUALS = "DIR='";
	static final String SQLIN = "SQLIN";
	static final String SQLIN_EQUALS = "SQLIN='";
	static final String CAMPO_FITTXT = "FITTXT=";
	static final String SORTIDA1 = "SORTIDA";
	

	private Constantes() {
	    throw new IllegalStateException("Utility class");
	  }


	public static String getLogProcNotFound() {
		return LOG_PROC_NOT_FOUND;
	}


	public static String getLogTemplatesNotFound() {
		return LOG_TEMPLATES_NOT_FOUND;
	}


	public static String getLogLibreriaCortex() {
		return LOG_LIBRERIA_CORTEX;
	}


	public static String getLogLreclNotFound() {
		return LOG_LRECL_NOT_FOUND;
	}


	public static String getLogRevisarFichero() {
		return LOG_REVISAR_FICHERO;
	}


	public static String getLogFicheroCon() {
		return LOG_FICHERO_CON;
	}


	public static String getLogFicheroAsteriscos() {
		return LOG_FICHERO_ASTERISCOS;
	}


	public static String getLogRevisarLongitud() {
		return LOG_REVISAR_LONGITUD;
	}


	public static String getRutaPcl() {
		return RUTA_PCL;
	}


	public static String getRutaListado() {
		return RUTA_LISTADO;
	}


	public static String getRutaProc() {
		return RUTA_PROC;
	}


	public static String getRutaCntl() {
		return RUTA_CNTL;
	}


	public static String getRutaTemplates() {
		return RUTA_TEMPLATES;
	}


	public static String getJdb2Template() {
		return JDB2_TEMPLATE;
	}


	public static String getJfichsalTemplate() {
		return JFICHSAL_TEMPLATE;
	}


	public static String getJfichentTemplate() {
		return JFICHENT_TEMPLATE;
	}


	public static String getJborrafTemplate() {
		return JBORRAF_TEMPLATE;
	}


	public static String getJmailtxtTemplate() {
		return JMAILTXT_TEMPLATE;
	}


	public static String getJsortTemplate() {
		return JSORT_TEMPLATE;
	}


	public static String getJftpsendTemplate() {
		return JFTPSEND_TEMPLATE;
	}


	public static String getJftprebTemplate() {
		return JFTPREB_TEMPLATE;
	}


	public static String getJftpdelTemplate() {
		return JFTPDEL_TEMPLATE;
	}


	public static String getJmailmsgTemplate() {
		return JMAILMSG_TEMPLATE;
	}


	public static String getJftpsappTemplate() {
		return JFTPSAPP_TEMPLATE;
	}


	public static String getJmailanxTemplate() {
		return JMAILANX_TEMPLATE;
	}


	public static String getJfivacioTemplate() {
		return JFIVACIO_TEMPLATE;
	}


	public static String getJopcrecTemplate() {
		return JOPCREC_TEMPLATE;
	}


	public static String getSystem() {
		return SYSTEM;
	}


	public static String getEmpty() {
		return EMPTY;
	}


	public static String getAmpersand() {
		return AMPERSAND;
	}


	public static String getEquals() {
		return EQUALS;
	}


	public static String getDefinicion() {
		return DEFINICION;
	}


	public static String getExtensionTxt() {
		return EXTENSION_TXT;
	}


	public static String getSpace() {
		return SPACE;
	}


	public static String getConsoleLine() {
		return CONSOLE_LINE;
	}


	public static String getLrecl() {
		return LRECL;
	}


	public static String getLectura() {
		return LECTURA;
	}


	public static String getMgmtclas() {
		return MGMTCLAS;
	}


	public static String getReportKey() {
		return REPORT_KEY;
	}


	public static String getSortida() {
		return SORTIDA;
	}


	public static String getProc() {
		return PROC;
	}


	public static String getCntl() {
		return CNTL;
	}


	public static String getBorrar() {
		return BORRAR;
	}


	public static String getStepStart() {
		return STEP_START;
	}


	public static String getPardb2() {
		return PARDB2;
	}


	public static String getWriting() {
		return WRITING;
	}


	public static String getSalida() {
		return SALIDA;
	}


	public static String getEntrada() {
		return ENTRADA;
	}


	public static String getfCortex() {
		return F_CORTEX;
	}


	public static String getEndSpaces() {
		return END_SPACES;
	}


	public static String getCortex() {
		return CORTEX;
	}


	public static String getDdname() {
		return DDNAME;
	}


	public static String getDummy() {
		return DUMMY;
	}


	public static String getComentario() {
		return COMENTARIO;
	}


	public static String getEndif() {
		return ENDIF;
	}


	public static String getExlixxxx() {
		return EXLIXXXX;
	}


	public static String getDefinicionFichero() {
		return DEFINICION_FICHERO;
	}


	public static String getTamanioFichero() {
		return TAMANIO_FICHERO;
	}


	public static String getDeleteStepStart() {
		return DELETE_STEP_START;
	}


	public static String getAdrde3() {
		return ADRDE3;
	}


	public static String getAdrde2() {
		return ADRDE2;
	}


	public static String getAdrde1() {
		return ADRDE1;
	}


	public static String getAdrdes() {
		return ADRDES;
	}


	public static String getAdremi() {
		return ADREMI;
	}


	public static String getAsunto() {
		return ASUNTO;
	}


	public static String getDsn() {
		return DSN;
	}


	public static String getSortout() {
		return SORTOUT;
	}


	public static String getDada725() {
		return DADA725;
	}


	public static String getDada724() {
		return DADA724;
	}


	public static String getDada723() {
		return DADA723;
	}


	public static String getDada722() {
		return DADA722;
	}


	public static String getDada721() {
		return DADA721;
	}


	public static String getHorenvi() {
		return HORENVI;
	}


	public static String getDataenvi() {
		return DATAENVI;
	}


	public static String getIdeanex() {
		return IDEANEX;
	}


	public static String getUidpeti() {
		return UIDPETI;
	}


	public static String getTipmail() {
		return TIPMAIL;
	}


	public static String getCampoDestino() {
		return CAMPO_DESTINO;
	}


	public static String getFdest() {
		return FDEST;
	}


	public static String getCampoFit() {
		return CAMPO_FIT;
	}


	public static String getCampoDir() {
		return CAMPO_DIR;
	}


	public static String getFitxer() {
		return FITXER;
	}


	public static String getForig() {
		return FORIG;
	}


	public static String getCampoOrig() {
		return CAMPO_ORIG;
	}


	public static String getCampoMsg2() {
		return CAMPO_MSG_2;
	}


	public static String getCampoMsg() {
		return CAMPO_MSG;
	}


	public static String getMsgEquals() {
		return MSG_EQUALS;
	}


	public static String getDirEquals() {
		return DIR_EQUALS;
	}


	public static String getSqlin() {
		return SQLIN;
	}


	public static String getSqlinEquals() {
		return SQLIN_EQUALS;
	}


	public static String getCampoFittxt() {
		return CAMPO_FITTXT;
	}


	public static String getSortida1() {
		return SORTIDA1;
	}
}
