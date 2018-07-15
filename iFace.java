import java.util.Scanner;

public class iFace{
	private static Scanner input;
	
	public static void main(String args[]){
		input = new Scanner(System.in);
		String menu[] = {"Entrar", "Criar conta", "Sair"};
		displayMenuOptions(menu);
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
		
		return 0;
	}
}