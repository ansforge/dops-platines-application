package fr.plugin.soapui;

import org.junit.Test;
import static org.junit.Assert.*;

public class VifhTest {
	
	@Test
	public void generateVihf() {
		Vihf token = new Vihf("CN=CLIENT_WS_ROR,OU=318751275100020,O=AGENCE DES SYSTEMES D'INFORMATION PARTAG,ST=Paris (75),C=FR");
		token.setProfil(new Profil("0"));
		assertEquals(37,countLines(token.generateVIHF()));
	}

	private static int countLines(String str){
		   String[] lines = str.split("\r\n|\r|\n");
		   return  lines.length;
		}
	
}
