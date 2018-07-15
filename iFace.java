import java.util.Scanner;

public class iFace{
	private static Scanner input;
	
	public static void main(String args[]){
		input = new Scanner(System.in);
		
		login();
		
		input.close();
	}
	
	public static void login(){
		String menu[] = {"Entrar", "Criar conta", "Sair"};
		
		while(true){
			switch(displayMenuOptions(menu)){
			case 1:
				break;
			case 2:
				break;
			case 3:
				return;
			}
		}
	}
	
	public static int readInt(){
		int read = input.nextInt();
		input.nextLine();
		return read;
	}
	
	public static String readLine(){
		return input.nextLine();
	}
	
	public static String askLine(String q){
		System.out.println(q + ": ");
		return readLine();
	}
	
	public static int displayMenuOptions(String items[]){
		int selectedItem = 0;
		
		for(int i = 0;i < items.length;i++){
			System.out.println((i + 1) + ". " + items[i]);
		}
		
		while(selectedItem < 1 || selectedItem > items.length){
			System.out.print(">");
			selectedItem = readInt();
			if(selectedItem < 1 || selectedItem > items.length){
				System.out.println("Opção inválida");
			}
		}
		
		return selectedItem;
	}
	
	public static void showTitle(String title){
		System.out.println("\n" + title + "\n");
	}
	
}