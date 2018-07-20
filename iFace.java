import java.io.*;
import java.util.Scanner;

public class iFace{
	private static Scanner input;
	
	//<Constants/>
	private static final String path = "/home/valerio/Documentos/iFace/iFace/data/";
	
	private static final int MAX_ACCT = 5;
	private static final int MAX_ATTR = 5;
	private static final int MAX_CMNT = 5;
	private static final int MAX_FRIENDS = 5;
	private static final int MAX_MESSAGES = 5;
	private static final int MAX_RQST = 5;
	
	private static final int ACCT_COL = 3;
	private static final int ACCT_EMAIL = 0;
	private static final int ACCT_NAME = 1;
	private static final int ACCT_PASSWORD = 2;
	
	private static final int ATTR_COL = 2;
	private static final int ATTR_KEY = 0;
	private static final int ATTR_VALUE = 1;
	
	private static final int CMNT_COL = 3;
	private static final int CMNT_NAME = 0;
	private static final int CMNT_DESCRIPTION = 1;
	private static final int CMNT_CREATOR = 2;
	
	private static final int FRIEND_COL = 1;
	private static final int FRIEND_EMAIL = 0;
	
	private static final int MESSAGES_COL = 3;
	private static final int MESSAGE_SRC = 0;
	private static final int MESSAGE_DEST = 1;
	private static final int MESSAGE_MESSAGE = 2;
	
	private static final int RQST_COL = 3;
	private static final int RQST_SRC = 0;
	private static final int RQST_DEST = 1;
	private static final int RQST_TYPE = 2;
	private static final int RQST_INVITE = 0;
	private static final int RQST_INVITE_FAILURE = 1;
	private static final int RQST_INVITE_SUCCESS = 2;
	private static final int RQST_ACCOUNT_CLOSURE = 3;
	//</Constants>
	
	//<Variables/>
	private static String accounts[][];
	private static String communities[][];
	private static String requests[][];
	private static String currUserAcct[];
	private static String currUserAttr[][];
	private static String currUserCmnt[][];
	private static String currUserFriends[][];
	private static String currUserMessages[][];
	//</Variables>
	
	public static void main(String args[]){
		input = new Scanner(System.in);
		
		accounts = new String[MAX_ACCT][ACCT_COL];
		communities = new String[MAX_CMNT][CMNT_COL];
		requests = new String[MAX_RQST][RQST_COL];

		currUserAcct = new String[ACCT_COL];
		currUserAttr = new String[MAX_ATTR][ATTR_COL];
		currUserCmnt = new String[MAX_CMNT][1];
		currUserFriends = new String[MAX_FRIENDS][FRIEND_COL];
		currUserMessages = new String[MAX_MESSAGES][MESSAGES_COL];
		
		loadDatabase("accounts.txt", accounts, ACCT_COL, MAX_ACCT);
		loadDatabase("communities.txt", communities, CMNT_COL, MAX_CMNT);
		loadDatabase("requests.txt", requests, RQST_COL, MAX_RQST);
		
		if(currUserAcct[ACCT_EMAIL] == null){
			loginMenu();
		}
		
		saveDatabase("accounts.txt", accounts, ACCT_COL, MAX_ACCT);
		saveDatabase("communities.txt", communities, CMNT_COL, MAX_CMNT);
		saveDatabase("requests.txt", requests, RQST_COL, MAX_RQST);
		
		input.close();
	}

	private static void homeMenu(){
		String currUserFolder = "users/" + currUserAcct[ACCT_EMAIL] + "/";
		loadDatabase(currUserFolder + "attr.txt", currUserAttr, ATTR_COL, MAX_ATTR);
		loadDatabase(currUserFolder + "communities.txt", currUserCmnt, 1, MAX_CMNT);
		loadDatabase(currUserFolder + "friends.txt", currUserFriends, FRIEND_COL, MAX_FRIENDS);
		loadDatabase(currUserFolder + "messages.txt", currUserMessages, MESSAGES_COL, MAX_MESSAGES);

		solveRequests();
		
		String options[] = {"Meu perfil", "Comunidades", "Amigos", "Voltar"};
		clearScreen();
		while(true){
			showTitle("Início");
			switch(displayMenuOptions(options)){
			case 1:
				profileMenu();
				break;
			case 2:
				communityMenu();
				break;
			case 3:
				friendsMenu();
				break;
			default:
				saveDatabase(currUserFolder + "attr.txt", currUserAttr, ATTR_COL, MAX_ATTR);
				saveDatabase(currUserFolder + "friends.txt", currUserFriends, FRIEND_COL, MAX_FRIENDS);
				saveDatabase(currUserFolder + "communities.txt", currUserCmnt, 1, MAX_CMNT);
				saveDatabase(currUserFolder + "messages.txt", currUserMessages, MESSAGES_COL, MAX_MESSAGES);
				for(int i = 0;i < ACCT_COL;i++){
					currUserAcct[i] = null;
				}
				clearDatabase(currUserAttr, ATTR_COL, MAX_ATTR);
				clearDatabase(currUserFriends, FRIEND_COL, MAX_FRIENDS);
				clearDatabase(currUserCmnt, 1, MAX_CMNT);
				clearDatabase(currUserMessages, MESSAGES_COL, MAX_MESSAGES);
				return;
			}
			clearScreen();
		}
	}

	//<Profile/>
	private static void profileMenu(){
		String options[] = {"Ver perfil", "Editar perfil", "Voltar"};
		while(true){
			clearScreen();
			showTitle("Meu perfil");
			switch(displayMenuOptions(options)){
			case 1:
				showProfile();
				break;
			case 2:
				editProfileMenu();
				break;
			default:
				return;
			}
		}
	}
	private static void showProfile(){
		clearScreen();
		showTitle("Meu perfil");
		System.out.println("Nome: " + currUserAcct[ACCT_NAME]);
		System.out.println("E-mail: " + currUserAcct[ACCT_EMAIL]);
		showTitle("Sobre mim");
		for(int i = 0;i < MAX_ATTR;i++){
			if(currUserAttr[i][ATTR_KEY] != null){
				System.out.println(currUserAttr[i][ATTR_KEY] + ": " + currUserAttr[i][ATTR_VALUE]);
			}
		}
		getchar();
	}
	private static void editProfileMenu(){
		String options[] = {"Adicionar atributo", "Editar atributo", "Apagar atributo", "Voltar"};
		while(true){
			clearScreen();
			showTitle("Editar perfil");
			switch(displayMenuOptions(options)){
			case 1:
				addAttribute();
				break;
			case 2:
				editAttribute();
				break;
			case 3:
				eraseAttribute();
				break;
			default:
				return;
			}
			
		}
	}
	private static void addAttribute(){
		clearScreen();
		showTitle("Adicionar atributo");
		String attrName = askLine("Nome do atributo");
		int index = databaseFind(currUserAttr, attrName, ATTR_KEY, MAX_ATTR);
		if(index != -1){
			System.out.println("Não foi possível adicionar o atributo.");
			getchar();
			return;
		}
		else{
			int i;
			for(i = 0;i < MAX_ATTR;i++){
				if(currUserAttr[i][ATTR_KEY] == null){
					break;
				}
			}
			if(i < MAX_ATTR){
				currUserAttr[i][ATTR_KEY] = attrName; 
				currUserAttr[i][ATTR_VALUE] = askLine("Valor do atributo");
			}
		}
	}
	private static void editAttribute(){
		clearScreen();
		showTitle("Editar atributo");
		String attrName = askLine("Nome do atributo");
		int index = databaseFind(currUserAttr, attrName, ATTR_KEY, MAX_ATTR);
		if(index != -1){
			currUserAttr[index][ATTR_VALUE] = askLine("Valor do atributo");
			return;
		}
		System.out.println("Atributo não encontrado");
		getchar();
	}
	private static void eraseAttribute(){
		clearScreen();
		showTitle("Apagar atributo");
		String attrName = askLine("Nome do atributo");
		int index = databaseFind(currUserAttr, attrName, ATTR_KEY, MAX_ATTR);
		if(index != -1){
			currUserAttr[index][ATTR_KEY] = null;
			currUserAttr[index][ATTR_VALUE] = null;
			return;
		}
		System.out.println("O atributo não existe");
		getchar();
	}
	//</Profile>
	
	//<Login/>	
	private static void loginMenu(){
		String menu[] = {"Entrar", "Criar conta", "Sair"};
		while(true){
			clearScreen();
			showTitle("iFace");
			switch(displayMenuOptions(menu)){
			case 1:
				signIn();
				break;
			case 2:
				signUp();
				break;
			default:
				return;
			}
			if(currUserAcct[ACCT_EMAIL] != null){
				homeMenu();
			}
		}
	}
	private static void signIn(){
		clearScreen();
		showTitle("Entrar");
		String email = askLine("E-mail");
		String password = askLine("Senha");
		int index = databaseFind(accounts, email, ACCT_EMAIL, MAX_ACCT);
		if(index != -1){
			if(accounts[index][ACCT_PASSWORD].equals(password)){
				for(int j = 0;j < ACCT_COL;j++){
					currUserAcct[j] = accounts[index][j];
				}
			}
			else{
				System.out.println("Senha incorreta");
				getchar();
			}
		}
		else{
			System.out.println(index + "Não existe nehuma conta associada a <" + email + ">");
			getchar();
		}
	}
	private static void signUp(){
		clearScreen();
		showTitle("Criar nova conta");
		String name = askLine("Nome");
		String email = askLine("E-mail");
		String password = askLine("Senha");
		String passwordConfirm = askLine("Confirme a senha");
		if(password.equals(passwordConfirm)){
			String items[] = {email, name, password};
			databaseInsert(accounts, items, items.length, MAX_ACCT);
			currUserAcct[ACCT_NAME] = name;
			currUserAcct[ACCT_EMAIL] = email;
			currUserAcct[ACCT_PASSWORD] = password;
			File userDatabase = new File(path + "users/" + email);
			if(userDatabase.exists() == false){
				userDatabase.mkdir();
				String files[] = {"attr", "friends", "communities", "messages"};
				createFiles(path + "users/" + email + "/", files);
			}
			System.out.println("Conta criada com sucesso");
			getchar();
		}
		else{
			System.out.println("As senhas não são iguais");
			getchar();
		}
	}
	//</Login>
	
	//<Communities/>
	private static void communityMenu(){
		String options[] = {"Minhas comunidades", "Criar comunidade", "Adicionar comunidade", "Todas as comunidades", "Voltar"};
		while(true){
			clearScreen();
			showTitle("Comunidades");
			switch(displayMenuOptions(options)){
			case 1:
				showCommunities();
				break;
			case 2:
				createCommunity();
				break;
			case 3:
				addCommunity();
				break;
			case 4:
				showAllCommunities();
				break;
			default:
				return;
			}
		}
	}
	private static void showAllCommunities(){
		clearScreen();
		showTitle("Todas as comunidades");
		for(int i = 0;i < MAX_CMNT;i++){
			if(communities[i][CMNT_NAME] != null){
				printCommunity(communities[i]);
			}
		}
		getchar();
	}
	private static void showCommunities(){
		clearScreen();
		showTitle("Minhas comunidades");
		for(int i = 0;i < MAX_CMNT;i++){
			if(currUserCmnt[i][0] != null){
				printCommunity(communities[databaseFind(communities, currUserCmnt[i][0], CMNT_NAME, MAX_CMNT)]);
			}
		}
		getchar();
	}
	private static void createCommunity(){
		clearScreen();
		showTitle("Criar comunidade");
		String cmntName = askLine("Nome da comunidade");
		int index = databaseFind(communities, cmntName, CMNT_NAME, MAX_CMNT);
		if(index != -1){
			System.out.println("A comunidade já existe ou não é possível criar mais comunidades");
			getchar();
			return;
		}
		else{
			int i;
			for(i = 0;i < MAX_CMNT;i++){
				if(communities[i][CMNT_NAME] == null){
					break;
				}
			}
			if(i < MAX_CMNT){
				communities[i][CMNT_NAME] = cmntName; 
				communities[i][CMNT_DESCRIPTION] = askLine("Descrição da comunidade");
				communities[i][CMNT_CREATOR] = currUserAcct[ACCT_NAME];
				String items[] = {cmntName};
				databaseInsert(currUserCmnt, items, items.length, MAX_CMNT);
				System.out.println("Comunidade criada");
				getchar();
			}
			else{
				System.out.println("Não foi possível criar a comunidade");
				getchar();
			}
		}
	}
	private static void addCommunity(){
		clearScreen();
		showTitle("Adicionar comunidade");
		String cmntName = askLine("Nome da comunidade");
		if(databaseFind(communities, cmntName, CMNT_NAME, MAX_CMNT) != -1){
			String items[] = {cmntName};
			databaseInsert(currUserCmnt, items, items.length, MAX_CMNT);
			System.out.println("Comunidade adicionada");
			getchar();
		}
		else{
			System.out.println("Comunidade não encontrada ou você já faz parte dela. Você pode consultar todas as comunidades em 'Mostrar todas comunidades'");
			getchar();
		}
	}
	private static void printCommunity(String community[]){
		System.out.println("------------------");
		System.out.println("Nome: "  + community[CMNT_NAME]);
		System.out.println("Criada por: " + community[CMNT_CREATOR]);
		System.out.println("Descrição: " + community[CMNT_DESCRIPTION]);
		System.out.println("------------------");
	}
	//</Communities>
	
	//<Friends/>
	private static void friendsMenu(){
		String options[] = {"Meus amigos", "Adicionar amigo", "Voltar"};
		while(true){
			clearScreen();
			showTitle("Amigos");
			switch(displayMenuOptions(options)){
			case 1:
				printDatabase(currUserFriends, FRIEND_COL, MAX_FRIENDS);
				getchar();
				break;
			case 2:
				clearScreen();
				String email = askLine("E-mail do usuário");
				if(databaseFind(accounts, email, ACCT_EMAIL, MAX_ACCT) != -1 && databaseFind(currUserFriends, email, FRIEND_EMAIL, MAX_FRIENDS) == -1 && !email.equals(currUserAcct[ACCT_EMAIL])){
					addRequest(currUserAcct[ACCT_EMAIL], email, RQST_INVITE);
					System.out.println("Solicitação de amizade enviada");
					getchar();
				}
				else{
					System.out.println("Usuário não encontrado");
					getchar();
				}
				break;
			default:
				return;
			}
		}
	}
	//</Friends>
	
	//<Requests/>
	private static void addRequest(String src, String dest, int type){
		String items[] = {src, dest, Integer.toString(type)};
		databaseInsert(requests, items, RQST_COL, MAX_RQST);
	}
	private static void solveRequests(){
		for(int i = 0;i < MAX_RQST;i++){
			if(requests[i][0] != null){
				if(requests[i][RQST_DEST].equals(currUserAcct[ACCT_EMAIL])){
					clearScreen();
					switch(Integer.valueOf(requests[i][RQST_TYPE])){
					case RQST_INVITE:
						showTitle("Solicitação de amizade");
						System.out.println(requests[i][RQST_SRC] + " te enviou uma solicitação de amizade");
						String options[] = {"Aceitar", "Recusar"};
						switch(displayMenuOptions(options)){
						case 1:
							addRequest(currUserAcct[ACCT_EMAIL], requests[i][RQST_SRC], RQST_INVITE_SUCCESS);
							String items[] = {requests[i][RQST_SRC]};
							databaseInsert(currUserFriends, items, items.length, MAX_FRIENDS);
							getchar();
							break;
						case 2:
							addRequest(currUserAcct[ACCT_EMAIL], requests[i][RQST_DEST], RQST_INVITE_FAILURE);
							break;
						}
						break;
					case RQST_INVITE_SUCCESS:
						System.out.println(requests[i][RQST_SRC] + " aceitou sua solicitação de amizade!");
						String items[] = {requests[i][RQST_SRC]};
						databaseInsert(currUserFriends, items, items.length, MAX_FRIENDS);
						getchar();
						break;
					case RQST_INVITE_FAILURE:
						System.out.println(requests[i][RQST_SRC] + " recusou sua solicitação de amizade");
						getchar();
						break;
					case RQST_ACCOUNT_CLOSURE:
						databaseRemove(currUserFriends, requests[i][RQST_SRC], FRIEND_EMAIL, RQST_COL, MAX_RQST);
						break;
					}
					for(int j = 0;j < RQST_COL;j++){
						requests[i][j] = null;
					}
				}
			}
		}
	}
	//</Requests>
	
	//<Input/>
	private static void getchar(){
		System.out.println("\nAperte ENTER para continuar");
		input.nextLine();
	}
	private static int readInt(){
		int read = input.nextInt();
		input.nextLine();
		return read;
	}	
	private static String readLine(){
		return input.nextLine();
	}
	private static String askLine(String q){
		System.out.print(q + ": ");
		return readLine();
	}	
	private static int displayMenuOptions(String items[]){
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
	//</Input>
	
	private static void showTitle(String title){
		System.out.println("\n" + title + "\n");
	}
	private static void clearScreen(){
		for(int i = 0;i < 100;i++){
			System.out.print("\n");
		}
	}
	
	private static void loadDatabase(String filename, String dest[][], int columns, int maxRows){
		for(int i = 0;i < maxRows;i++){
			for(int j = 0;j < columns;j++){
				dest[i][j] = null;
			}
		}
		try{
			File database = new File(path + filename);
			FileReader fr = new FileReader(database);
			BufferedReader br = new BufferedReader(fr);
			String line = br.readLine();
			for(int i = 0;line != null;i++){
				for(int j = 0;j < columns;j++){
					dest[i][j] = line;
					line = br.readLine();
				}
			}
			
			br.close();
		}
		catch (IOException e){
			e.printStackTrace();
		}
	}
	private static void printDatabase(String database[][], int columns, int maxRows){
		String separator = "-----------";
		for(int i = 0;i < maxRows;i++){
			if(database[i][0] != null){
				System.out.println(separator);
				for(int j = 0;j < columns;j++){
					System.out.println(database[i][j]);
				}
				System.out.println(separator);
			}
		}
	}
	private static void saveDatabase(String filename, String source[][], int columns, int maxRows){
		try{
			File database = new File(path + filename);
			FileWriter fw = new FileWriter(database);
			BufferedWriter bw = new BufferedWriter(fw);
			for(int i = 0;i < maxRows;i++){
				if(source[i][0] != null){
					for(int j = 0;j < columns;j++){
						bw.write(source[i][j]);
						bw.newLine();
					}
				}
			}
			bw.close();
		}
		catch (IOException e){
			e.printStackTrace();
		}
	}
	private static void createFiles(String directory, String files[]){
		for(int i = 0;i < files.length;i++){
			try{
				File f = new File(directory + files[i] + ".txt");
				f.createNewFile();
			}
			catch (IOException e){
				e.printStackTrace();
			}
		}
	}
	private static int databaseFind(String database[][], String key, int column, int maxRows){
		for(int i = 0;i < maxRows;i++){
			if(database[i][0] != null){
				if(database[i][column].equals(key)){
					return i;
				}
			}
		}
		return -1;
	}	
	private static void databaseInsert(String database[][], String items[], int columns, int maxRows){
		for(int i = 0;i < maxRows;i++){
			if(database[i][0] == null){
				for(int j = 0;j < columns;j++){
					database[i][j] = items[j];
				}
				return;
			}
		}
	}
	private static void clearDatabase(String database[][], int columns, int rows){
		for(int i = 0;i < rows;i++){
			for(int j = 0;j < columns;j++){
				database[i][j] = null;
			}
		}
	}
	private static void databaseRemove(String database[][], String key, int column, int columns, int maxRows){
		for(int i = 0;i < maxRows;i++){
			if(database[i][0] != null){
				if(database[i][column].equals(key)){
					for(int j = 0;j < columns;j++){
						database[i][j] = null;
					}
				}
			}
		}
	}
}