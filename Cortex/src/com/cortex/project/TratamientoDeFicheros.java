package com.cortex.project;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.logging.Level;

public interface TratamientoDeFicheros {

	static FileReader openFile(String tipo) throws ExceptionCortex {
		FileReader fileReader = null;
		String ruta = "";
		switch (tipo) {
		case "PROC":
			ruta = Constantes.RUTA_PROC;
			break;
		case "CNTL":
			ruta = Constantes.RUTA_CNTL;
			break;
		case "LISTADO":
			ruta = Constantes.RUTA_LISTADO;
			break;
		case "PCL":
			ruta = Constantes.RUTA_PCL;
			break;
		default:
			break;
		}
		
		try {
			fileReader = new FileReader(ruta + mainApp.programa.substring(0,6) + Constantes.EXTENSION_TXT);
		} catch (FileNotFoundException e) {
			Avisos.LOGGER.log(Level.SEVERE, Constantes.LOG_PROC_NOT_FOUND);
			throw new ExceptionCortex(2, "infoDSN", tipo, "File");
		}
		return fileReader;
	}
}
