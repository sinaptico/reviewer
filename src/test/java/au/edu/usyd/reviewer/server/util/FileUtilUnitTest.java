package au.edu.usyd.reviewer.server.util;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class FileUtilUnitTest {

	@Test
	public void shouldEscapeFilename() {
		String filename = "new file + \\ / : * ? \" < > |";
		assertThat(FileUtil.escapeFilename(filename), equalTo("new file + - - - - - - - - -"));
	}
}
