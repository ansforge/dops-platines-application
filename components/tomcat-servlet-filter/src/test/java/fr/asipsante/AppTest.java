package fr.asipsante;

import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import fr.asipsante.tomcat.filter.IpAddressMatcher;

/**
 * Unit test for simple App.
 */
public class AppTest {
	/**
	 * Rigorous Test :)
	 */
	@Test
	public void shouldAnswerWithTrue() {
		boolean check = false;
		
		String IP_CHECK = "192.168.0.200, 192.168.0.0/24, 192.168.0.1, 192.168.0.1";
		String ip = "192.168.0.1";
		List<String> addresses = Arrays.asList(IP_CHECK.split("\\s*,\\s*"));
		for (String address : addresses) {
			System.out.println(address);
			IpAddressMatcher ipAddressMatcher = new IpAddressMatcher(address);
			check = ipAddressMatcher.matches(ip);
			if (check) {
				break;
			}
		}
		assertTrue(check);
	}
}
