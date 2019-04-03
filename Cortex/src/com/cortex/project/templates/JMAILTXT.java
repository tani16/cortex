package com.cortex.project.templates;

import java.util.ArrayList;
import java.util.Map;
import com.cortex.project.Constantes;
import com.cortex.project.MetodosAux;
/**
 * Esta clase se encarga de modificar la plantilla JMAILTXT
 * @author Juan Daniel Sanchez Ortiz
 * @version 14/02/2019
 */
public class JMAILTXT {
// Variables de Entrada
		String letra = "";
		String numeroPaso = "";
		Map<String, String> datos;
		String fi = "";
		ArrayList<String> salida;
		
// Variables de Salida		
		String avisos = "";
		boolean masMail = false;
/*
 * Constructor de la clase. Recibe los siguientes parámetros:
 * @param letra: Letra del JCL que se está migrando
 * @param nombre: Nombre del fichero de salida que se está escribiendo
 * @param pasoE: Paso del JCL antiguo donde se encuentra este fichero de salida
 * @param infoFich: Información del fichero recuperada por el método "metodosAux.infoFichero" 
 * @param datos: Datos recuperados del PCL relativo al paso que se está migrando.
 */		
	public JMAILTXT(String letra, String numeroPaso, Map<String, String> datos) {
			super();
			this.letra = letra;
			this.numeroPaso = numeroPaso;
			this.datos = datos;
			this.fi = "";
//			this.salida.clear();
		}

/*
 * Recupera el valor de la variable de salida avisos
 * @return avisos: Mensaje a imprimir en los logs y en el JCL migrado avisando de alguna incidencia
 */
	public String getAvisos() {
		return avisos;
	}
	
	public boolean isMasMail() {
		return masMail;
	}
	
/*
 * Método principal de la Clase. Trata la linea recibida y la devuelve lista para escribir en el JCL a migrar.
 * @param linea: Linea leida de la plantilla JMAILTXT
 * @param index: Número de línea en la plantilla
 * @return Línea procesada lista para escribir en el fichero a migrar
 */
	public String processJMAILTXT(String linea, int index) {
		avisos = "";
    	switch (index) {
    	case 2:
    		linea = jmailtxtLine2(linea);
			break;
    	case 4:
    		linea = jmailtxtLine4(linea);
    		break;
    	case 5:
    		linea = jmailtxtLine5(linea);
    		break;
    	case 6:
    		linea = jmailtxtLine6(linea);
			break;
    	case 7:
    		linea = jmailtxtLine7(linea);
			break;
    	case 8:
    		linea = jmailtxtLine8(linea);
			break;
    	case 9:
    		linea = jmailtxtLine9(linea);
			break;
    	case 10:
    		linea = jmailtxtLine10(linea);
    		break;
    	case 12:
    		linea = jmailtxtLine12(linea);
    		break;
    	case 13:
    		linea = jmailtxtLine13(linea);
    		break;
    	case 14:
    		linea = jmailtxtLine14(linea);
    		break;
    	case 15:
    		linea = jmailtxtLine15(linea);
    		break;
    	case 16:
			linea = jmailtxtLine16(linea);
			break;
    	case 17:
			linea = jmailtxtLine17(linea);
			break;
    	case 18:
			linea = jmailtxtLine18(linea);
			break;
    	case 19:
			linea = jmailtxtLine19(linea);
			break;
    	case 20:
    		linea = jmailtxtLine20(linea);
			break;
		default:
			break;
		}
		
		
		return linea.replaceAll(Constantes.getEndSpaces(),"");
	}


	private String jmailtxtLine2(String linea) {
	
		linea = linea.replace(Constantes.getStepStart(), "//" + letra + numeroPaso);
		
		return linea;
	}
	
	private String jmailtxtLine4(String linea) {
		linea = (datos.get(Constantes.getAsunto()) == null) ? linea.trim() : linea.trim() + datos.get(Constantes.getAsunto());
		return linea;
	}
	
	private String jmailtxtLine5(String linea) {
		linea = (datos.get(Constantes.getAdremi()) == null) ? linea.trim() : linea.trim() + datos.get(Constantes.getAdremi());
		return linea;
	}

	private String jmailtxtLine6(String linea) {
		if (datos.get(Constantes.getAdrdes()) == null && fi.equals("")) {
			linea = linea.trim();
		}
		else {
			salida = (ArrayList<String>) MetodosAux.checkLineSize(Constantes.getAdrdes(), linea, fi, datos); 
			linea = salida.get(0);
			fi = salida.get(1);
		}
		return linea;
	}
	
	private String jmailtxtLine7(String linea) {
		if (datos.get(Constantes.getAdrde1()) == null && fi.equals("")) {
			linea = linea.trim();
		}
		else {
			salida = (ArrayList<String>) MetodosAux.checkLineSize(Constantes.getAdrde1(), linea, fi, datos); 
			linea = salida.get(0);
			fi = salida.get(1);
		}
		return linea;
	}
	
	private String jmailtxtLine8(String linea) {
		if (datos.get(Constantes.getAdrde2()) == null && fi.equals("")) {
			linea = linea.trim();
		}
		else {
			salida = (ArrayList<String>) MetodosAux.checkLineSize(Constantes.getAdrde2(), linea, fi, datos); 
			linea = salida.get(0);
			fi = salida.get(1);
		}
		return linea;
	}
	
	private String jmailtxtLine9(String linea) {
		if (datos.get(Constantes.getAdrde3()) == null && fi.equals("")) {
			linea = linea.trim();
			masMail = false;
		}
		else {
			salida = (ArrayList<String>) MetodosAux.checkLineSize(Constantes.getAdrde3(), linea, fi, datos); 
			linea = salida.get(0);
			fi = salida.get(1);
			if (!fi.isEmpty()) {
				masMail = true;
				datos.put(Constantes.getAdrdes(), fi);
				datos.put(Constantes.getAdrde1(), "");
				datos.put(Constantes.getAdrde2(), "");
//    			Avisos.LOGGER.log(Level.INFO, letraPaso + String.valueOf(pasoE) + " // No caben todos los correos - Falta añadir: " + fi);
//    			System.out.println("Escribimos: " + "***** No caben todos los correos. Revisar  ****");
//    	    	writerCortex.write("***** No caben todos los correos. Revisar  ****");
//    	    	writerCortex.newLine();
    	    	fi = "";
			}
		}
		return linea;
	}

	private String jmailtxtLine10(String linea) {
		linea = (datos.get(Constantes.getTipmail()) == null) ? linea.trim() : linea.trim() + datos.get(Constantes.getTipmail());
		return linea;
	}

	private String jmailtxtLine12(String linea) {
		linea = (datos.get(Constantes.getUidpeti()) == null) ? linea.trim() : linea.trim() + datos.get(Constantes.getUidpeti());
		return linea;
	}
	
	private String jmailtxtLine13(String linea) {
		linea = (datos.get(Constantes.getIdeanex()) == null) ? linea.trim() : linea.trim() + datos.get(Constantes.getIdeanex());
		return linea;
	}
	
	private String jmailtxtLine14(String linea) {
		linea = (datos.get(Constantes.getDataenvi()) == null) ? linea.trim() : linea.trim() + datos.get(Constantes.getDataenvi());
		return linea;
	}

	private String jmailtxtLine15(String linea) {
		linea = (datos.get(Constantes.getHorenvi()) == null) ? linea.trim() : linea.trim() + datos.get(Constantes.getHorenvi());
		return linea;
	}
	
	private String jmailtxtLine16(String linea) {
		if (datos.get(Constantes.getDada721()) == null && fi.equals("")) {
			linea = linea.trim();
		}
		else {
			salida = (ArrayList<String>) MetodosAux.checkLineSize(Constantes.getDada721(), linea, fi, datos); 
			linea = salida.get(0);
			fi = salida.get(1);
		}
		
		return linea;
	}
	
	private String jmailtxtLine17(String linea) {
		if (datos.get(Constantes.getDada722()) == null && fi.equals("")) {
			linea = linea.trim();
		}
		else {
			salida = (ArrayList<String>) MetodosAux.checkLineSize(Constantes.getDada722(), linea, fi, datos); 
			linea = salida.get(0);
			fi = salida.get(1);
		}
		
		return linea;
	}
	
	private String jmailtxtLine18(String linea) {
		if (datos.get(Constantes.getDada723()) == null && fi.equals("")) {
			linea = linea.trim();
		}
		else {
			salida = (ArrayList<String>) MetodosAux.checkLineSize(Constantes.getDada723(), linea, fi, datos); 
			linea = salida.get(0);
			fi = salida.get(1);
		}
		return linea;
	}
	
	private String jmailtxtLine19(String linea) {
		if (datos.get(Constantes.getDada724()) == null && fi.equals("")) {
			linea = linea.trim();
		}
		else {
			salida = (ArrayList<String>) MetodosAux.checkLineSize(Constantes.getDada724(), linea, fi, datos); 
			linea = salida.get(0);
			fi = salida.get(1);
		}
		return linea;
	}
	
	private String jmailtxtLine20(String linea) {
		//Revisar nombre variable
		if (datos.get(Constantes.getDada725()) == null && fi.equals("")) {
			linea = linea.trim();
		}
		else {
			salida = (ArrayList<String>) MetodosAux.checkLineSize(Constantes.getDada725(), linea, fi, datos); 
			linea = salida.get(0);
			fi = salida.get(1);
		}
		return linea;
	}

}