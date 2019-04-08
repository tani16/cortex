package com.cortex.project;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

public class MetodosAux {

	//	static Avisos  avisos = new Avisos();
	LectorPasos lectorPasos =  new LectorPasos();
/**
 * Recibe una cadena y comprueba si tiene literales, devolviendo verdadero si es así
 * 
 * @param param
 * @return 
 */
	public boolean checkLiteralesPARDB2(String param) {
		if (!param.startsWith(Constantes.AMPERSAND)) {
			return true;
		}
		for(int i = 1; i < param.length(); i++) {
			if(param.charAt(i) == '-' && param.charAt(i+1) != '&') {
				return true;				
			}
		}
		return false;
	}
	
	/**
	 * Recibe una cadena, y la transforma quitando los '-'
	 * @param PARDB2
	 * @return
	 */
	public String tratarLiteralesPARDB2(String parDB2) {
		int index = parDB2.indexOf(Constantes.EQUALS);
		
		while (index != -1) {
			for (int i = index; i >= 0; i--) {
				if (parDB2.charAt(i) == '-' || i == 0) {
					i = i > 0 ? i + 1 : i;
					String aux = parDB2.substring(i, index + 1);
					parDB2 = parDB2.replace(aux, Constantes.EMPTY);
					break;
				}
			}
			index = parDB2.indexOf(Constantes.EQUALS, index+1);
		}
/*		if (index != -1) {
			
		}*/
		
		return parDB2;
	}

	public List<String> buscaInfoProc(int pasoE, String letraPaso, String nombre) throws ExceptionCortex {
		boolean seguir = true;
		boolean buscar = false;	
		String linea;
		ArrayList<String> infoFichero = new ArrayList<>();
		//----------------Fichero de plantilla JPROC--------------------------
	    FileReader ficheroPROC = null;
		try {
			ficheroPROC = new FileReader("C:\\Cortex\\PROC\\" + mainApp.programa.substring(0,6) + ".txt");
		} catch (FileNotFoundException e) {
			Avisos.LOGGER.log(Level.SEVERE, Constantes.LOG_PROC_NOT_FOUND);
			throw new ExceptionCortex(7, "buscaInfoProc", "PROC", "File");
		}
		
		try(BufferedReader lectorPROC = new BufferedReader(ficheroPROC)){
	    
			String numeroPaso;    
		    numeroPaso = (pasoE < 10) ? "0" + pasoE : String.valueOf(pasoE) ;
		    
		    while((linea = lectorPROC.readLine()) != null && seguir) {
		    	if(linea.startsWith("//" + letraPaso + numeroPaso)) {
		    		buscar = true;
		    	}
		    	if(buscar && linea.startsWith("//" + nombre + " ")) {
	    			infoFichero.add(linea);
	    			linea = lectorPROC.readLine();
	    			while (linea.startsWith("//  ")) {
						infoFichero.add(linea);
						linea = lectorPROC.readLine();
					}
	    			buscar = false;
	    			seguir = false;	
		    	}	    	
		    }
		}catch (Exception e) {
			Avisos.LOGGER.log(Level.SEVERE, "Error en la lectura del PRO - buscaInfoProc");
			throw new ExceptionCortex(7, "buscaInfoProc", "PROC", Constantes.LECTURA);
		}	
	    
	    return infoFichero;
	}
	
	public Map<String, String> infoFichero(int pasoE, String letraPaso, String nombre) throws ExceptionCortex {
		ArrayList<String> infoFichero;
		Map<String, String> infoFich = new HashMap<>();
		
		if(mainApp.withProc) {
			infoFichero = (ArrayList<String>) buscaInfoProc(pasoE, letraPaso, nombre);
		    
		    String clave;
		    String valor;
	    	long primario = 0;
	    	long secundario = 0; 
	    	long tamanio; 
	    	int numDSN = 0;
		    for(int j = 0; j < infoFichero.size(); j++) {
		    	int index = 1;
			    while (index != -1) {
					index = infoFichero.get(j).indexOf('=', index);
					if (index != -1) {
						clave = lectorPasos.leerClave(infoFichero.get(j), index);
						valor = lectorPasos.leerValor(infoFichero.get(j), index);
						valor = valor.replace("(",Constantes.EMPTY);
						if (!clave.equals(Constantes.EMPTY) && !valor.equals(Constantes.EMPTY)) {
							if (clave.equals("DSN") && (infoFich.containsKey("DSN") || infoFich.containsKey("DSN" + numDSN))) {
								numDSN++;
								clave += numDSN;
							}
							infoFich.put(clave, valor);
						}
					}
					if (index != - 1) {
						index ++;
					}
				}
			    if(infoFichero.get(j).contains(Constantes.SPACE) && !infoFich.get(Constantes.SPACE).equals("CYL") && !infoFich.get(Constantes.SPACE).equals("TRK")) {
			    	if (infoFich.containsKey(Constantes.LRECL)) {
			    		int ini = 1;
			    		int fin = 2;
				    	ini = infoFichero.get(j).lastIndexOf('(');
				    	for(int i = ini; i < infoFichero.get(j).length(); i++) {
				    		if(infoFichero.get(j).charAt(i) == ',') {
				    			fin = i;
				    			break;
				    		}
				    	}
				    	tamanio = Long.valueOf(infoFichero.get(j).substring(ini + 1, fin));
				    	primario = Long.parseLong(infoFich.get(Constantes.SPACE)) * tamanio / Long.parseLong(infoFich.get(Constantes.LRECL)) / 1000;
				    	primario = primario < 5 ? 10 : primario;
				    	
				    	ini = fin;
				    	for(int i = ini; i < infoFichero.get(j).length(); i++) {
				    		if(infoFichero.get(j).charAt(i) == ')') {
				    			fin = i;
				    			break;
				    		}
				    	}
				    	tamanio = Long.valueOf(infoFichero.get(j).substring(ini + 1, fin));
				    	secundario = Long.parseLong(infoFich.get(Constantes.SPACE)) * tamanio / Long.parseLong(infoFich.get(Constantes.LRECL)) / 1000; 
				    	secundario = secundario < 3 ? 3 : secundario;
			    	}else {
						infoFich.put(Constantes.LRECL,Constantes.LRECL);
			    	}
			    }else {
			    	if(infoFichero.get(j).contains(Constantes.SPACE) && infoFich.get(Constantes.SPACE).equals("CYL")) {
			    		primario = 15;
			    		secundario = 1;
			    		String mensaje = letraPaso + String.valueOf(pasoE) + " //Comprobar fichero CYL migrado correctamente";
						Avisos.LOGGER.log(Level.INFO, mensaje);
			    		//infoFich.put("LRECL","CYL");
			    	}
			    }
		    }
		    
		    clave = Constantes.DEFINICION;
		    valor = "(" + infoFich.get(Constantes.LRECL) + ",(" + primario + "," + secundario + "))";
			infoFich.put(clave, valor);
			
			if(!infoFich.containsKey("DSN")) {
				clave = "DUMMY";
				valor = infoFichero.get(0);
				infoFich.put(clave, valor);
			}
			else {
				if(infoFich.get("DSN").endsWith("XP")) {
					infoFich.replace("DISP", "TEMP");
				}
			}
		}else {
			infoFich.put("DSN", nombre);
			infoFich.put(Constantes.DEFINICION, "(LONGREG,(KKK,KK))");
			infoFich.put("DISP", "NEW");
			infoFich.put(Constantes.LRECL, "NO");
		}
		System.out.println("------- Datos sacados del Fichero:  -------");
	    infoFich.forEach((k,v) -> System.out.println(k + "-" + v));
	    System.out.println(Constantes.CONSOLE_LINE);
		
		return infoFich;
	}

	public Map<String, String> infoReportes(String nombre, int pasoE, String letraPaso) throws ExceptionCortex {
		ArrayList<String> infoFichero;
		Map<String, String> infoRep = new HashMap<>();
		String clave; 
		String valor;
		
		if (mainApp.withProc) {
			infoFichero = (ArrayList<String>) buscaInfoProc(pasoE, letraPaso, nombre);
			
			if (infoFichero.size() == 1) {
				clave = Constantes.REPORT_KEY;
				valor = infoFichero.get(0);
				
			}else {
				clave = Constantes.REPORT_KEY;
				valor = "* Error al leer línea de Reporte - Nombre reporte: " + nombre; 
				String mensaje = letraPaso + String.valueOf(pasoE) + " // Error al leer el reporte - Nombre reporte: " + nombre;
				Avisos.LOGGER.log(Level.INFO, mensaje);
			}
				
			infoRep.put(clave, valor);
		}else {
			clave = Constantes.REPORT_KEY;
			valor = "Sacar reporte del PROC - nombre:" + nombre;
			infoRep.put(clave, valor);
		}
		return infoRep;
	}

	public String infoFTP(int pasoE, String letraPaso, String fhost) throws ExceptionCortex  {
		boolean seguir = true;
		boolean buscar = false;	
		String linea;
//		String clave;
		String valor = fhost;
		int index = 0;
		
		if(mainApp.withProc) {
			//----------------Fichero de plantilla JPROC--------------------------
		    FileReader ficheroPROC = TratamientoDeFicheros.openFile("PROC");
			
			try (BufferedReader lectorPROC = new BufferedReader(ficheroPROC)){
			    String numeroPaso;    
			    numeroPaso = (pasoE < 10) ? "0" + pasoE : String.valueOf(pasoE) ;
			    
			    while((linea = lectorPROC.readLine()) != null && seguir) {
			    	if(linea.startsWith("//" + letraPaso + numeroPaso)) {
			    		buscar = true;
			    	}
			    	if(buscar && linea.contains(fhost + ".") && linea.contains("DSN=")){
		    			index = linea.indexOf('=', index);
//		    			clave = lectorPasos.leerClave(linea, index);
						valor = lectorPasos.leerValor(linea, index);
						buscar = false;
						seguir = false;
			    	}	    	
			    }
			}catch (Exception e) {
				Avisos.LOGGER.log(Level.SEVERE, Constantes.LOG_PROC_NOT_FOUND);
				throw new ExceptionCortex(9, "infoFTP", "PROC", Constantes.LECTURA);
			}
		}
		
		return valor;
	}

//	public Map<String, String> infoSort(int paso, String letraPaso) throws IOException {
//	    Map<String, String> infoFich   = new HashMap<String, String>();
//	    String clave, valor;
//	    
//	    if (mainApp.withProc) {
//		    infoFich   = infoFichero(paso, letraPaso, "SORTOUT");    
//	    }else {
//	    	infoFich   = infoFichero(paso, letraPaso, "SORTOUT");  
//	    }
//		return infoFich;
//	}
	
	public List<String> infoSORTIN(int pasoE, String letraPaso) throws ExceptionCortex {
		ArrayList<String> infoFichero = new ArrayList<>();
		ArrayList<String> infoFicheroProc;
		int ini = 0;
		int fin = 0;
		
		if(mainApp.withProc) {
			String primero;
			String segundo;
			String tercero;
			infoFicheroProc = (ArrayList<String>) buscaInfoProc(pasoE, letraPaso, "SORTIN");
			for(int i = 0; i < infoFicheroProc.size(); i++) {
				fin = infoFicheroProc.get(i).indexOf("DSN=");
				if (fin != -1) {
					primero = infoFicheroProc.get(i).substring(0, fin);
					ini = fin;
					fin = infoFicheroProc.get(i).indexOf(",DISP=");
					tercero = infoFicheroProc.get(i).substring(ini, ini + 4) + "Z." + infoFicheroProc.get(i).substring(ini + 4, fin);
					ini = fin + 1;
					segundo = infoFicheroProc.get(i).substring(ini) + ",";
					infoFichero.add(primero + segundo + tercero);	
				}
			}
		}else {
			infoFichero.add("**** No encontrado fichero SORTIN");
			String mensaje = letraPaso + String.valueOf(pasoE) + " //Comprobar fichero CYL migrado correctamente";
			Avisos.LOGGER.log(Level.INFO, mensaje);
		}
		return infoFichero;
	}

	public Map<String, String> infoFtpReb(int pasoE, String letraPaso) throws ExceptionCortex {
		Map<String, String> infoFich;
		
		infoFich = infoFichero(pasoE, letraPaso, "SORTI1");

		return infoFich;
	}
	
	public String infoDSN(int pasoE, String letraPaso, String name) throws ExceptionCortex {
		boolean seguir = true;
		boolean buscar = false;	
		String linea;
//		String clave;
		String valor = name;
		int index = 0;
		
		if(mainApp.withProc) {
			//----------------Fichero de plantilla JPROC--------------------------
		    FileReader ficheroPROC = TratamientoDeFicheros.openFile("PROC");
			
			try(BufferedReader lectorPROC = new BufferedReader(ficheroPROC)){	    
			    String numeroPaso;    
			    numeroPaso = (pasoE < 10) ? "0" + pasoE : String.valueOf(pasoE) ;
			    
			    while((linea = lectorPROC.readLine()) != null && seguir) {
			    	if(linea.startsWith("//" + letraPaso + numeroPaso)) {
			    		buscar = true;
			    	}
			    	if(buscar && linea.startsWith("//" + name + "  ")){
		    			index = linea.indexOf('=', index);
//		    			clave = lectorPasos.leerClave(linea, index);
						valor = lectorPasos.leerValor(linea, index);
						buscar = false;
						seguir = false;			    		
			    	}	    	
			    }
			}catch (Exception e) {
				Avisos.LOGGER.log(Level.SEVERE, "Error leyendo el PROC");
				throw new ExceptionCortex(1, "infoDSN", "PROC", Constantes.LECTURA);
			}
		}
		
		return valor;
	}
	
	public void infoJFUSION(Map<String, String> datos, int pasoE, String letraPaso) throws ExceptionCortex {
		Map<String, String> infoFich;
		String[] ficheros;
		int contadorFicheros = 0;
		String clave; 
		String valor;
		
		pasoE -= 2;
		for(int i = 1; datos.containsKey("FICHA" + i); i++) {
			ficheros = datos.get("FICHA" + i).split(",");
			ficheros[0] = ficheros[0].replace("ENTRADA=", Constantes.EMPTY);
			
			if(ficheros[0].contains(Constantes.SORTIDA1)) {
				infoFich = infoFichero(pasoE, letraPaso, ficheros[0].replace(Constantes.SORTIDA_EQUALS, Constantes.EMPTY));
				if (infoFich.containsKey(Constantes.MGMTCLAS)){
					datos.put(Constantes.MGMTCLAS, infoFich.get(Constantes.MGMTCLAS));
				}
				datos.put(Constantes.DEFINICION, infoFich.get(Constantes.DEFINICION));
				datos.put("DSN", infoFich.get("DSN"));
				datos.put("SALIDA", ficheros[0].replace(Constantes.SORTIDA1, Constantes.EMPTY));
				
			}else {
				for (int j = 0; j < ficheros.length; j++) {
					contadorFicheros++;
					clave = "DSN" + contadorFicheros;
					valor = infoDSN(pasoE, letraPaso, ficheros[j]);
					datos.put(clave, valor);
					datos.put("FICH" + contadorFicheros, ficheros[j]);
				}
			}	
		}
		
	}
	
	public static List<String> checkLineSize(String cabecera, String linea, String fi, Map<String, String> datos) {
		ArrayList<String> salida = new ArrayList<>();
		if((linea.trim() + fi + datos.get(cabecera)).length() < 72) {
			if(datos.get(cabecera) == null) {
				salida.add(0 ,linea.trim() + fi.trim());
			}else {
				salida.add(0 ,linea.trim() + fi + datos.get(cabecera));
			}
			salida.add(1, Constantes.EMPTY);			 
		}
		else{
			if (fi.isEmpty()) {
				fi = linea.trim()+ fi.trim() + datos.get(cabecera);
			}else {
				String masdatos = datos.get(cabecera) != null ? datos.get(cabecera) : Constantes.EMPTY;
				fi = linea.trim()+ fi.trim() + " " + masdatos;
			}
			for(int i = 72; i > 0; i--) {
				int index = fi.lastIndexOf(' ', i);
				index = index == -1 ? fi.lastIndexOf(';', i) : index;
				
				if(index != -1) {
					salida.add(0, fi.substring(0, index));
					salida.add(1, fi.substring(index + 1).trim() + " ");
					break;
				}
			}			
		}		
		return salida;
	}

	public Map<String, String> infomultiDSN(int pasoE, String letraPaso, String name) throws ExceptionCortex {
		boolean seguir = true;
		boolean buscar = false;	
		String linea;
		String clave;
		String valor = Constantes.EMPTY;
		int index = 0;
		int contador=0;
		Map<String, String> datos = new HashMap<>();
		
		
		if(mainApp.withProc) {
			//----------------Fichero de plantilla JPROC--------------------------
		    FileReader ficheroPROC = TratamientoDeFicheros.openFile("PROC");
		    try (BufferedReader lectorPROC = new BufferedReader(ficheroPROC)){		    
			    String numeroPaso;    
			    numeroPaso = (pasoE < 10) ? "0" + pasoE : String.valueOf(pasoE) ;
			    
			    while((linea = lectorPROC.readLine()) != null && seguir) {
			    	if(linea.startsWith("//" + letraPaso + numeroPaso)) {
			    		buscar = true;
			    	}
			    	if(buscar) {
			    		if(linea.startsWith("//" + name + "  ") || linea.startsWith("// ")){
			    			index = linea.indexOf('=', index);
			    			clave = lectorPasos.leerClave(linea, index)+ contador;
							valor = lectorPasos.leerValor(linea, index);
							datos.put(clave, valor);
							contador++;
			    		}/*else if(linea.startsWith("//  ")) {
			    			index = linea.indexOf('=', index);
			    			clave = lectorPasos.leerClave(linea, index)+ contador;
							valor = lectorPasos.leerValor(linea, index);
							datos.put(clave, valor);
							contador++;
			    		}*/else if(linea.startsWith("//SYSUT2")){
							buscar = false;
							seguir = false;
			    		}
			    	}	    	
			    }
		    }catch (Exception e) {
		    	Avisos.LOGGER.log(Level.SEVERE, "Error leyendo el PROC");
		    	throw new ExceptionCortex(4, "infomultiDSN", "PROC", Constantes.LECTURA);
		    }
		}else {
			datos.put("DSN0", "Leer el PROC para sacar archivos");
		}
		System.out.println("------- Datos sacados del Fichero:  -------");
	    datos.forEach((k,v) -> System.out.println(k + "-" + v));
	    System.out.println(Constantes.CONSOLE_LINE);
		return datos;
	}

	public Map<String, String> cabecera() throws ExceptionCortex {
		boolean seguir = true;
		boolean buscar = false;	
		String linea;
		String clave;
		String valor = Constantes.EMPTY;
		int index = 0;
		int contador = 0;
		int numVariable = 0;
		int numOPC = 0; 
		int finIndex = 0;
		Map<String, String> datos = new HashMap<>();
		
		//----------------Fichero de plantilla CNTL--------------------------
	    FileReader ficheroCNTL = TratamientoDeFicheros.openFile(Constantes.CNTL);
		
		try (BufferedReader lectorCNTL = new BufferedReader(ficheroCNTL)){
		    while((linea = lectorCNTL.readLine()) != null && seguir) {
		    	if(linea.startsWith("//REPORT ")) {
		    		buscar = true;
		    	}
		    	if(buscar) {
		    		if(linea.startsWith("//*%")){
		    			index = linea.lastIndexOf('%');
	    				clave = "OPC" + numOPC;
	    				valor = linea.substring(index+1);
						datos.put(clave, valor);
						numOPC++;
		    		}
	    			else if(linea.startsWith("//"+ mainApp.programa.substring(0,6))) {
	    				continue;
	    			}else if(linea.startsWith("// ")) {
	    				index = linea.lastIndexOf(' ');
	    				clave = "Variable" + numVariable;
	    				valor = linea.substring(index+1);
	    				if(isNombreEqualsValor(valor)) {
	    					datos.put("aviso", "REVISAR: Nombre de la variable igual a su valor");
	    				}
	    				if(valor.charAt(valor.length()-1)==',') {
	    					finIndex = valor.length() + index;
	    					valor = linea.substring(index+1,finIndex);	
	    				}
	    				if(valor.endsWith("=")) {
	    					valor += "''";
	    				}
						datos.put(clave, valor);
						numVariable++;
	    			}else if(linea.startsWith("//*")) {
	    				index = linea.lastIndexOf('*');
	    				clave = "Comentario" + contador;
	    				valor = linea.substring(index+1);
						datos.put(clave, valor);
						contador++;
	    			}
		    	}	    	
		    }
		}catch (Exception e) {
			Avisos.LOGGER.log(Level.SEVERE, "Error leyendo el CNTL");
			throw new ExceptionCortex(6, "cabecera", "CNTL", Constantes.LECTURA);
		}
		System.out.println("------- Datos sacados de la Cabecera:  -------");
	    datos.forEach((k,v) -> System.out.println(k + "-" + v));
	    System.out.println(Constantes.CONSOLE_LINE);
		return datos;
	}

	private boolean isNombreEqualsValor(String valor) {
		
		int index = 0;
		String variable = "";
		String literal ="";
		index = valor.indexOf(Constantes.EQUALS);
		variable = valor.substring(0, index);
		literal = valor.substring(index + 1);

		return literal.contains(variable);
	}

}
