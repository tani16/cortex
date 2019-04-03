package com.cortex.project.templates;

import java.util.Map;
import java.util.logging.Level;

import com.cortex.project.Avisos;
import com.cortex.project.Constantes;
import com.cortex.project.ExceptionCortex;
import com.cortex.project.MetodosAux;
/**
 * Esta clase se encarga de modificar la plantilla JFICHSAL
 * @author Juan Daniel Sanchez Ortiz
 * @version 14/02/2019
 */
public class JFTPSEND {
// Variables de Entrada
		String letra = "";
		String numeroPaso = "";
		int pasoE = 0;
		Map<String, String> datos;
		
// Variables internas
		int spaces = 0;
		
// Variables de Salida		
		String avisos = "";
		
/*
 * Constructor de la clase. Recibe los siguientes parámetros:
 * @param letra: Letra del JCL que se está migrando
 * @param nombre: Nombre del fichero de salida que se está escribiendo
 * @param pasoE: Paso del JCL antiguo donde se encuentra este fichero de salida
 * @param infoFich: Información del fichero recuperada por el método "metodosAux.infoFichero" 
 * @param datos: Datos recuperados del PCL relativo al paso que se está migrando.
 */		
	public JFTPSEND(String letra, String numeroPaso, int pasoE, Map<String, String> datos) {
			super();
			this.letra = letra;
			this.numeroPaso = numeroPaso;
			this.pasoE = pasoE;
			this.datos = datos;
		}

/*
 * Recupera el valor de la variable de salida avisos
 * @return avisos: Mensaje a imprimir en los logs y en el JCL migrado avisando de alguna incidencia
 */
	public String getAvisos() {
		return avisos;
	}
	
/*
 * Método principal de la Clase. Trata la linea recibida y la devuelve lista para escribir en el JCL a migrar.
 * @param linea: Linea leida de la plantilla JFICHSAL
 * @param index: Número de línea en la plantilla
 * @return Línea procesada lista para escribir en el fichero a migrar
 */
	public String processJFTPSEND(String linea, int index) throws ExceptionCortex {
		avisos = "";
		
		switch (index) {
    	case 2:
    		linea = jftpsendLine2(linea);
			break;
    	case 3:
    		linea = jftpsendLine3(linea);
			break;
    	case 4:
    		linea = jftpsendLine4(linea);
    		break;
    	case 5:
    		linea = jftpsendLine5(linea);
    		break;
    	case 6:
    		linea = jftpsendLine6(linea);
    		break;
    	case 7:
    		linea = jftpsendLine7(linea);
    		break;
		default:
			break;
		}
		
		return linea.replaceAll(Constantes.getEndSpaces(),"");
	}
	
	private String jftpsendLine2(String linea) {
		linea = linea.replace(Constantes.getStepStart(), "//" + letra + numeroPaso);
		
		return linea;
	}
	
	private String jftpsendLine3(String linea) {
		//Calculamos cuantos espacios hay que añadir detrás para que no se muevan los comentarios de posición
		StringBuilder des = new StringBuilder("DES=" + datos.get("DES") + ",");
		spaces = 40 - des.length();
		for (int j = 0; j < spaces; j++) {
			des.append(" ");
		}
		linea = linea.replace(Constantes.getCampoDestino(), des);
		
		return linea;
	}
	
	private String jftpsendLine4(String linea) throws ExceptionCortex {
		MetodosAux metodosAux = new MetodosAux();
		String dsn = "";
		try {
			dsn = metodosAux.infoFTP(pasoE, letra, datos.get("FHOST"));
		} catch (ExceptionCortex e) {
			String mensaje = "Error recupendando la DSN";
			Avisos.LOGGER.log(Level.SEVERE, mensaje);
			throw new ExceptionCortex(4, "jftpsendLine4","","");
		}
		if (dsn.equals(Constantes.getEmpty())){
			avisos = "****" + letra + pasoE + " // DSN Fichero no encontrada " + "****";
		}
		if(dsn.contains(Constantes.getCortex())) {
			avisos = Constantes.getLogLibreriaCortex();
		}
	    StringBuilder host = new StringBuilder("HOST=Z." + dsn + ",");
	    spaces = 40 - host.length();  		
		for (int j = 0; j < spaces; j++) {
			host.append(" ");
		}
		linea = linea.replace("HOST=,                                  ", host);
		
		return linea;
	}
	
	private String jftpsendLine5(String linea) {
		if(datos.get(Constantes.getFdest()).contains("_")) {
			String aux = "'" + datos.get(Constantes.getFdest()) + "'";
			datos.replace(Constantes.getFdest(), aux);
		}
		if(datos.get(Constantes.getFdest()).contains("_&")) {
//			String aux = datos.get("FDEST");
//			aux = aux.replaceAll("_&", "-&");
//			datos.replace("FDEST", aux);
			avisos = "****" + letra + pasoE + " // Revisar fichero -  contiene _& " + "****";
		}
		if(datos.get(Constantes.getFdest()).contains("*")) {
	    	avisos = "****" + letra + pasoE + " // Fichero con * - Avisar Aplicacion " + "****";
		}
		StringBuilder fit = new StringBuilder("FIT=" + datos.get(Constantes.getFdest()));
		if(datos.containsKey("MSG") || datos.containsKey("DIR")) {
			fit.append(",");
		}
		spaces = 40 - fit.length();  		
		for (int j = 0; j < spaces; j++) {
			fit.append(" ");
		}
		String aux = linea = linea.replace(Constantes.getCampoFit(), fit);
		if(aux.length() > 72) {
			linea = linea.replace("FIT=nomfichred                          <== nombre fich red", fit);
		}else {
			linea = linea.replace(Constantes.getCampoFit(), fit);
		}
		
		return linea;
	}
	
	private String jftpsendLine6(String linea) {
		if(datos.containsKey("DIR")) {
			linea = linea.replace("//*", "// "); 
			StringBuilder dir = new StringBuilder(Constantes.getDirEquals() + datos.get("DIR") + "'");
    		if(datos.containsKey("MSG")) {
    			dir.append(",");
    		}
    		spaces = 40 - dir.length();  		
    		for (int j = 0; j < spaces; j++) {
    			dir.append(" ");
    		}
    		linea = linea.replace(Constantes.getCampoDir(), dir);
		}
		
		return linea;
	}
	
	private String jftpsendLine7(String linea) {
		if(datos.containsKey("MSG")) {
			linea = linea.replace("//*", "// ");
			if(!datos.containsKey("MSG2")) { 
    			StringBuilder msg = new StringBuilder(Constantes.getMsgEquals() + datos.get("MSG").replace("-", ",") + "'");
	    		spaces = 40 - msg.length();  		
	    		for (int j = 0; j < spaces; j++) {
	    			msg.append(" ");
	    		}
	    		linea = linea.replace(Constantes.getCampoMsg(), msg);
			}else {
				StringBuilder msg = new StringBuilder(Constantes.getMsgEquals() + datos.get("MSG").replace("-", ",")
						+ datos.get("MSG2").trim().replace("-", ",") + "'");
				if (msg.length() > 68) {
					avisos ="****" + letra + pasoE + " // Variable MSG excede de la longitud permitida - " + msg + "****";
				}
				linea = linea.replace(Constantes.getCampoMsg2(), msg);
			}
		}		
		return linea;
	}
}
