package com.cortex.project;

public class ExceptionCortex extends Exception{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -5082094703184668073L;
	private final int codigoError;
	private final String metodo;
	private final String archivo;
	private final String tipo;
	
	public ExceptionCortex (int cod, String metodo, String archivo, String tipo) {
		super();
		this.codigoError = cod;
		this.metodo = metodo;
		this.archivo = archivo;
		this.tipo = tipo;
	}
	
	@Override
	public String getMessage() {
		String mensaje = "";
		
		if (tipo.equals("File")) {
			mensaje = codigoError + " - Archivo no encontrado " + archivo + " en el método " + metodo;
		}else {
			mensaje = codigoError + " - Error en el método " + metodo + " - " + tipo; 
		}
		
		return mensaje;
	}
	

}
