import java.util.Scanner;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

public class Kriptografija {
	static final String USERS_FILE = "users.txt";

	public static void main(String[] args) throws IOException {
	   
	   Scanner scanner = new Scanner(System.in);
	   boolean exit = false;
	   while(!exit) {
		System.out.println("\nIzaberite jednu od sljedecih opcija: ");
		System.out.println("1. Registracija");
		System.out.println("2. Prijava");
		System.out.println("3. Zavrsi program");
		String choice = scanner.nextLine();
		switch(choice) {
		case "1":
			register();
			break;
		case "2":
			login();
			break;
		case "3":
			exit = true;
			System.exit(0);
		default:
			System.out.println("Neispravan unos!");
		}
	  }
	 scanner.close();
	}
	
	
	
	
	 static void register() {
		Scanner scanner = new Scanner(System.in);
		System.out.println("Unesite ime:");
		String name = scanner.nextLine();
		while(!name.matches("[a-zA-Z]+")) {
			System.out.println("\nIme bi trebalo da sadrzi samo slova. Pokusajte ponovo!");
			name = scanner.nextLine();
		}
		
		System.out.println("Unesite prezime:");
		String surname = scanner.nextLine();
		while(!surname.matches("[a-zA-Z]+")) {
			System.out.println("\nPrezime bi trebalo da sadrzi samo slova. Pokusajte ponovo!");
			surname = scanner.nextLine();
		}
		
		System.out.println("Unesite korisnicko ime(u vidu alfanumerickih karaktera):");
		String username = scanner.nextLine();
		if (usernameExists(username)) {
			System.out.println("\nUnesite drugo korisnicko ime, ovo vec postoji!");
			return;
		}
		while(!username.matches("[a-zA-Z0-9]+")) {
			System.out.println("\nKorisnicko ime bi trebalo da sadrzi samo alfanumericke karaktere. Pokusajte ponovo!");
			username = scanner.nextLine();
		}
		
		System.out.println("Unesite lozinku(u vidu alfanumerickih karaktera):");
		String password = scanner.nextLine();
		while(!password.matches("[a-zA-Z0-9]+")) {
			System.out.println("\nLozinka bi trebalo da sadrzi samo alfanumericke karaktere. Pokusajte ponovo!");
			password = scanner.nextLine();
		}
		
		System.out.println("Unesite naziv vase drzave(dvoslovna oznaka-podrazumijevano BA):");
		String country = scanner.nextLine();
		if(country.isEmpty()) {
			country = "BA";
		}
		System.out.println("Unesite naziv entiteta(dvoslovna oznaka-podrazumijevano RS):");
		String entity = scanner.nextLine();
		if(entity.isEmpty()) {
			entity = "RS";
		}
		System.out.println("Unesite naziv grada(podrazumijevano Banja Luka):");
		String city = scanner.nextLine();
		if(city.isEmpty()) {
			city = "Banja Luka";
		}
		System.out.println("Unesite naziv organizacije(podrazumijevano Elektrotehnicki fakultet):");
		String organization = scanner.nextLine();
		if(organization.isEmpty()) {
			organization = "Elektrotehnicki fakultet";
		}
		System.out.println("Unesite naziv organizacione jedinice(podrazumijevano ETF):");
	    String organizationUnit = scanner.nextLine();
	    if(organizationUnit.isEmpty()) {
	    	organizationUnit = "ETF";
	    }
        String commonName = name+" "+surname;
		System.out.println("Unesite email adresu:");
		String email = scanner.nextLine();
		
		
		try (BufferedWriter writer = new BufferedWriter(new FileWriter(USERS_FILE, true))) {
			writer.write(name + " " + surname + " " + username + " " + password + "\n");
			System.out.println("\nRegistracija je uspjesna!");
			
			
			String[] key = {"openssl", "genrsa", "-out", "private/" +username+ ".key", "-des3", "-passout", "pass:"+password, "2048"};
			execute(key);
			
			String[] request = {"openssl", "req", "-new", "-key", "private/"+username+".key", "-passin", "pass:"+password, "-config", "openssl.cnf", "-out", "requests/"+username+".csr",
					"-subj", "/C="+country+"/ST="+entity+"/L="+city+"/O="+organization+"/OU="+organizationUnit+"/CN="+commonName+"/emailAddress="+email};
			execute(request);
			
		    String[] cert = {"openssl", "ca", "-in", "requests/"+username+".csr", "-out", "certs/"+username+".crt", "-config", "openssl.cnf", "-passin", "pass:sigurnost", "-batch"};
			execute(cert);
		    
			
			String certPath = "certs/"+username+".crt";
			System.out.println("\nPutanja do kreiranog sertifikata je: "+ certPath);
			String keyPath = "private/"+username+".key";
			System.out.println("\nPutanja do kreiranog kljuca je: "+ keyPath);
		        
		} catch(IOException e) {
			System.out.println("\nGreska tokom registracije. Pokusajte ponovo!");
		}
	}
	
	
	
	static void login() throws IOException {
		Scanner scanner = new Scanner(System.in);
		System.out.println("Unesite putanju do vaseg sertifikata:");
		String certPath = scanner.nextLine();
		String[] ver = {"openssl", "verify", "-verbose","-CAfile", "rootca.pem", certPath};
		execute1(ver);
		
		
		System.out.println("\nUnesite korisnicko ime:");
		String username = scanner.nextLine();
		System.out.println("Unesite lozinku:");
		String password = scanner.nextLine();
		try (BufferedReader reader = new BufferedReader(new FileReader(USERS_FILE))) {
			String line;
			boolean found = false;
			while((line = reader.readLine()) != null) {
				String parts[] = line.split(" ");
				if(username.equals(parts[2]) && password.equals(parts[3])) {
					String h[] = {"openssl", "passwd", "-1", "-salt", "12345678", username+".txt"};
					String hash = execute2(h);
					String verif[] = {"openssl", "dgst", "-prverify", "private/"+username+".key", "-passin", "pass:"+password, "-signature", username+".dgst", hash};
					execute(verif);
					found = true;
					Scanner s = new Scanner(System.in);
					boolean exit = false;
					while(!exit) {
						System.out.println("\nIzaberite jednu od sljedecih opcija:");
						System.out.println("1. Rail fence");
						System.out.println("2. Myszkowski");
						System.out.println("3. Playfair");
						System.out.println("4. Zavrsi program");
						String ch = s.nextLine();
						switch(ch) {
						case "1":
							Scanner sc1 = new Scanner(System.in);
							System.out.println("Unesite tekst za enkripciju(do 100 karaktera):");
							String text1 = sc1.nextLine();
							System.out.println("Unesite kljuc za enkripciju:");
							int key1 = sc1.nextInt();
							if(text1.length() < 100) {
								RailFence railFence = new RailFence(text1, key1);
								StringBuilder cipher1 = railFence.encode();
								System.out.println("Sifrat je: " + cipher1);
								try (BufferedWriter writer1 = new BufferedWriter(new FileWriter(hash, true))) {
									writer1.write(text1 + " | " + "Rail fence" + " | " + key1 + " | " + cipher1 + '\n');
								} catch(IOException e) {
									System.out.println("\nGreska prilikom upisa u fajl!");
								}
								String dgst1[] = {"openssl", "dgst", "-sha256", "-sign", "private/"+username+".key", "-passin", "pass:"+password, "-out", username+".dgst", hash};
								execute(dgst1);
								String enc1[] = {"openssl", "aes-256-cbc", "-in", hash, "-out", username+".enc", "-k", password};
								execute(enc1);
								Scanner sk1 = new Scanner(System.in);
								System.out.println("\nDa li zelite da vidite sadrzaj svoje datoteke? (Odgovorite sa da ili ne.)");
								String unos = sk1.nextLine();
								if(unos.equals("da")) {
									String dec[] = {"openssl", "aes-256-cbc", "-d", "-k", password, "-in", username+".enc"};
									execute(dec);
									
								} else {
									break;
								}
							}
							else {
								System.out.println("Unijeli ste tekst duzi od 100 karaktera!");
							}
							break;
						case "2":
							Scanner sc2 = new Scanner(System.in);
							System.out.println("Unesite tekst za enkripciju(do 100 karaktera):");
							String text2 =sc2.nextLine();
							System.out.println("Unesite kljuc za enkripciju:");
							String key2 = sc2.nextLine();
							if(text2.length() < 100) {
								Myszkowski myszkowski = new Myszkowski(text2, key2);
								StringBuilder cipher2 = myszkowski.encode();
								System.out.println("Sifrat je: " + cipher2);
								try (BufferedWriter writer2 = new BufferedWriter(new FileWriter(hash, true))) {
									writer2.write(text2 + " | " + "Myszkowski" + " | " + key2 + " | " + cipher2 + '\n');
								} catch(IOException e) {
									System.out.println("\nGreska prilikom upisa u fajl!");
								}
								String dgst2[] = {"openssl", "dgst", "-sha256", "-sign", "private/"+username+".key", "-passin", "pass:"+password, "-out", username+".dgst", hash};
								execute(dgst2);
								String enc2[] = {"openssl", "aes-256-cbc", "-in", hash, "-out", username+".enc", "-k", password};
								execute(enc2);
								Scanner sk2 = new Scanner(System.in);
								System.out.println("\nDa li zelite da vidite sadrzaj svoje datoteke? (Odgovorite sa da ili ne.)");
								String unos = sk2.nextLine();
								if(unos.equals("da")) {
									String dec[] = {"openssl", "aes-256-cbc", "-d", "-k", password, "-in", username+".enc"};
									execute(dec);
									
								} else {
									break;
								}
								
							} else {
								System.out.println("Unijeli ste tekst duzi od 100 karaktera!");
							}
							break;
						case "3":
							Scanner sc3 = new Scanner(System.in);
							System.out.println("Unesite test za enkripciju(do 100 karaktera):");
							String text3 = sc3.nextLine();
							System.out.println("Unesite kljuc za enkripciju:");
							String key3 = sc3.nextLine();
							if(text3.length() < 100) {
								Playfair playfair = new Playfair(text3, key3);
								StringBuilder cipher3 = playfair.encode(text3, key3);
								System.out.println("Sifrat je: " + cipher3);
								try (BufferedWriter writer3 = new BufferedWriter(new FileWriter(hash, true))) {
									writer3.write(text3 + " | " + "Playfair" + " | " + key3 + " | " + cipher3 + '\n');
								} catch(IOException e) {
									System.out.println("\nGreska prilikom upisa u fajl!");
								}
								String dgst3[] = {"openssl", "dgst", "-sha256", "-sign", "private/"+username+".key", "-passin", "pass:"+password, "-out", username+".dgst", hash};
								execute(dgst3);
								String enc3[] = {"openssl", "aes-256-cbc", "-in", hash, "-out", username+".enc", "-k", password};
								execute(enc3);
								Scanner sk3 = new Scanner(System.in);
								System.out.println("\nDa li zelite da vidite sadrzaj svoje datoteke? (Odgovorite sa da ili ne.)");
								String unos = sk3.nextLine();
								if(unos.equals("da")) {
									String dec[] = {"openssl", "aes-256-cbc", "-d", "-k", password, "-in", username+".enc"};
									execute(dec);
									
								} else {
									break;
								}
								
							} else {
								System.out.println("Unijeli ste tekst duzi od 100 karaktera!");
							}
							break;
						case "4":
							System.exit(0);
						default:
							System.out.println("Neispravan unos!");
						}
						
					}
					break;
				}
			}
		    if(!found) {
		    	System.out.println("\nNeispravan unos korisnickog imena ili lozinke!");
		    }
		} catch(IOException e) {
			System.out.println("\nDoslo je do greske tokom prijave. Pokusajte ponovo!");
		}
	}
	
	static boolean usernameExists(String username) {
	    try (BufferedReader reader = new BufferedReader(new FileReader(USERS_FILE))) {
	        String line;
	        while ((line = reader.readLine()) != null) {
	            String[] parts = line.split(" ");
	            if (parts.length >= 3 && parts[2].equals(username)) {
	                return true;
	            }
	        }
	    } catch (IOException e) {
	        System.out.print("");
	    }
	    return false;
	}
	
	static void execute(String[] command) throws IOException {
		Process process = Runtime.getRuntime().exec(command);
		BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line;
        while ((line = reader.readLine()) != null) {
            System.out.println(line);
        }

        try {
            process.waitFor();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
	}
	
	static void execute1(String[] command) throws IOException {
		Process process = Runtime.getRuntime().exec(command);
		BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line;
        int linecount = 0;
        while ((line = reader.readLine()) != null) {
            linecount++;
        }

        try {
            process.waitFor();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        if(linecount == 1) {
        	System.out.println("Verifikacije je uspjela!");
        } else {
        	System.out.println("Verifikacija nije uspjela!");
        	System.exit(0);
        }
	}
	
	static String execute2(String[] command) throws IOException {
		Process process = Runtime.getRuntime().exec(command);
		BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line;
        if ((line = reader.readLine()) != null) {
            return line;
        }

        try {
            process.waitFor();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "error";
	}
	
}
