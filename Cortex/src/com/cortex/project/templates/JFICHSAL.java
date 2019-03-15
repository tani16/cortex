package com.cortex.project.templates;

import java.util.Map;
import com.cortex.project.Constantes;
/**
 * Esta clase se encarga de modificar la plantilla JFICHSAL
 * @author Juan Daniel Sanchez Ortiz
 * @version 14/02/2019
 */
public class JFICHSAL {
// Variables de Entrada
		String letra = "";
		String nombre = "";
		int pasoE = 0;
		Map<String, String> infoFich;
		Map<String, String> datos;
		
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
	public JFICHSAL(String letra, String nombre, int pasoE, Map<String, String> infoFich, Map<String, String> datos) {
			super();
			this.letra = letra;
			this.nombre = nombre;
			this.pasoE = pasoE;
			this.infoFich = infoFich;
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
	public String processJFICHSAL(String linea, int index) {
		avisos = "";
		switch (index) {
    	case 3:
    		linea = jfichsalLine3_14(linea);
    		break;
    	case 5:
    		linea = jfichsalLine5(linea);
    		break;
    	case 6:
    		linea = jfichsalLine6(linea);
    		break;
    	case 9:
    		linea = jfichsalLine9(linea);
    		break;
    	case 11:
    		linea = jfichsalLine11(linea);
    		break;
    	case 14:
    		linea = jfichsalLine3_14(linea);
    		break;
    	case 16:
    		linea = jfichsalLine5(linea);
    		break;
    	case 17:
    		linea = jfichsalLine17(linea);
    		break;
    	default:
			break;
    	}
    	
    	if(infoFich.get("DISP").equals("NEW") && index > 6) {
    		//No escribimos el resto de ficheros (mod, temp)
    		linea = "";
    	}
    	if(infoFich.get("DISP").equals("MOD") && index < 12) {
    		//No escribimos el resto de ficheros (new, temp)
    		linea = "";
    	}
    	if(infoFich.get("DISP").equals("TEMP") && (index < 7 || index > 11)) {
    		//No escribimos el resto de ficheros (new, mod)
    		linea = "";
    	} 
		
		
		return linea.replaceAll(Constantes.getEndSpaces(),"");
	}

	/*
	 * Trata las líneas 3 y 14 de la plantilla JFICHSAL. Inserta la DSN
	 * @param linea: Linea 3 o 14 de la plantilla
	 * @return Línea tratada con la DSN correcta
	 */
	private String jfichsalLine3_14(String linea) {
		linea = linea.replace(Constantes.getDdname(), nombre);
		if(infoFich.get(Constantes.getDsn()).contains(Constantes.getCortex())) {
			avisos = Constantes.getLogLibreriaCortex();
		}
		linea = linea.replace(Constantes.getDefinicionFichero(), infoFich.get(Constantes.getDsn()));
		return linea;
	}
	
	/*
	 * Trata la línea 5 de la plantilla JFICHSAL. Inserta MGMTCLAS o comenta la línea
	 * @param linea: Linea 5 de la plantilla
	 * @return Línea tratada con la MGMTCLAS correcta o comentada
	 */
	private String jfichsalLine5(String linea) {
		if(infoFich.containsKey(Constantes.getMgmtclas())) {
			linea = linea.replace(Constantes.getExlixxxx(), infoFich.get(Constantes.getMgmtclas()));
		}else {
			linea = linea.replace("// ", "//*");
		}
		return linea;
	}
	
	/*
	 * Trata la línea 6 de la plantilla JFICHSAL. Inserta la definición del fichero
	 * @param linea: Linea 6 de la plantilla
	 * @return Línea tratada con la definición del fichero
	 */
	private String jfichsalLine6(String linea) {
		if (infoFich.get("DISP").equals("NEW") && infoFich.get(Constantes.getLrecl()).equals(Constantes.getLrecl())) {
			avisos = Constantes.getLogLreclNotFound();
		}else {
			linea = linea.replace(Constantes.getTamanioFichero(), infoFich.get(Constantes.getDefinicion()));
		}
		return linea;
	}
	
	/*
	 * Trata la línea 9 de la plantilla JFICHSAL. Inserta la DSN del fichero
	 * @param linea: Linea 9 de la plantilla
	 * @return Línea tratada con la DSN del fichero
	 */	
	private String jfichsalLine9(String linea) {
		linea = linea.replace(Constantes.getDdname(), nombre);
		if(infoFich.get(Constantes.getDsn()).contains(Constantes.getCortex())) {
			avisos = Constantes.getLogLibreriaCortex();
		}
		linea = linea.replace("APL.XXXXXXXX.NOMMEM.XP", infoFich.get(Constantes.getDsn()));
		return linea;
	}

	/*
	 * Trata la línea 11 de la plantilla JFICHSAL. Inserta la Definición del fichero
	 * @param linea: Linea 11 de la plantilla
	 * @return Línea tratada con la Definición del fichero
	 */	
	private String jfichsalLine11(String linea) {
		if (infoFich.get("DISP").equals("TEMP") && infoFich.get(Constantes.getLrecl()).equals(Constantes.getLrecl())) {
			avisos = Constantes.getLogLreclNotFound();
		}else {
			linea = linea.replace(Constantes.getTamanioFichero(), infoFich.get(Constantes.getDefinicion()));
		}
		return linea;
	}

//	private String jfichsalLine14(String linea) {
//		linea = linea.replace(Constantes.getDdname(), nombre);
//		if(infoFich.get(Constantes.getDsn()).contains(Constantes.getCortex())) {
//			avisos = Constantes.getLogLibreriaCortex();
//		}
//		linea = linea.replace(Constantes.getDefinicionFichero(), infoFich.get(Constantes.getDsn()));
//		return linea;
//	}

	/*
	 * Trata la línea 17 de la plantilla JFICHSAL. Inserta la Definición del fichero
	 * @param linea: Linea 17 de la plantilla
	 * @return Línea tratada con la Definición del fichero
	 */	
	private String jfichsalLine17(String linea) {
		if (infoFich.get("DISP").equals("MOD") && infoFich.get(Constantes.getLrecl()).equals(Constantes.getLrecl())) {
			avisos = Constantes.getLogLreclNotFound();
		}else {
			linea = linea.replace(Constantes.getTamanioFichero(), infoFich.get(Constantes.getDefinicion()));
		}
		return linea;
	}

}
