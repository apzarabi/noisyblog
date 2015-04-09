package templater;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class Q2 {
	public static void main(String[] args) throws IOException {
		File a = new File("a/a/a/a");
		System.out.println(a.exists());
		a.createNewFile();
	}
}
