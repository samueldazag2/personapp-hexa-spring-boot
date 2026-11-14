package co.edu.javeriana.as.personapp.terminal.menu;

import java.util.Scanner;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
public class MenuPrincipal {

	private static final int SALIR = 0;
	private static final int MODULO_PERSONA = 1;
	private static final int MODULO_PROFESION = 2;
	private static final int MODULO_TELEFONO = 3;
	private static final int MODULO_ESTUDIO = 4;

	@Autowired
	private PersonaMenu personaMenu;

	@Autowired
	private ProfessionMenu professionMenu;

	@Autowired
	private PhoneMenu phoneMenu;

	@Autowired
	private StudyMenu studyMenu;

	private final Scanner keyboard = new Scanner(System.in);

	public void inicio() {
		boolean salir = false;
		while (!salir) {
			mostrarMenu();
			int opcion = CliInput.readInteger(keyboard, "Ingrese una opción: ");
			switch (opcion) {
			case SALIR:
				salir = true;
				break;
			case MODULO_PERSONA:
				personaMenu.iniciarMenu(keyboard);
				break;
			case MODULO_PROFESION:
				professionMenu.iniciarMenu(keyboard);
				break;
			case MODULO_TELEFONO:
				phoneMenu.iniciarMenu(keyboard);
				break;
			case MODULO_ESTUDIO:
				studyMenu.iniciarMenu(keyboard);
				break;
			default:
				System.out.println("La opción elegida no es válida.");
			}
		}
		keyboard.close();
	}

	private void mostrarMenu() {
		System.out.println("----------------------");
		System.out.println(MODULO_PERSONA + " para trabajar con Personas");
		System.out.println(MODULO_PROFESION + " para trabajar con Profesiones");
		System.out.println(MODULO_TELEFONO + " para trabajar con Teléfonos");
		System.out.println(MODULO_ESTUDIO + " para trabajar con Estudios");
		System.out.println(SALIR + " para salir");
	}
}
