import java.io.*;

import com.sun.jna.platform.win32.Advapi32Util;
import static com.sun.jna.platform.win32.WinReg.HKEY_CURRENT_USER;

public class InstallCheckstyleFromAnywhere {

    public static void main(String[] args) throws Exception {
    	System.out.println("Exporting checkstyle...");
        exportResource("/checkstyle.jar");
        exportResource("/cs1331-checkstyle.xml");
        System.out.println("Creating command batch files...");
        File localDirectory = new File("");
        PrintStream ps = new PrintStream(new File("checkstyle.bat"));
        ps.println("@echo off");
        ps.println("java -jar " + localDirectory.getAbsolutePath() + File.separator + "checkstyle.jar %*");
        ps.close();
        ps = new PrintStream(new File("checkstyle_with_config.bat"));
        ps.println("@echo off");
        ps.println("java -jar "
        		+ localDirectory.getAbsolutePath() + File.separator + "checkstyle.jar -c "
        		+ localDirectory.getAbsolutePath() + File.separator + " cs1331-checkstyle.xml %*");
        ps.close();
        BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("Do you want to automatically add the current directory to your PATH? Or do you want to do that manually?");
        System.out.println("NOTE: Your PATH could potentially be damaged, which could potentially cause serious problems.");
        System.out.println("By entering \"YES\" to continue you acknowledge you and you alone are responsible if your PATH is damaged.");
        if (consoleReader.readLine().equals("YES")){
	        System.out.println("Adding current directory to registry...");
	    	Advapi32Util.registrySetStringValue(HKEY_CURRENT_USER, "Environment", "Path",
	    			Advapi32Util.registryGetStringValue(HKEY_CURRENT_USER, "Environment", "Path")
	    			+ ";" + localDirectory.getAbsolutePath());
    	}
    	System.out.println("Done. Restart command prompt to test.");
    }

    public static String exportResource(String resourceName) throws Exception {
        InputStream stream = null;
        OutputStream resStreamOut = null;
        String jarFolder;
        try {
            stream = InstallCheckstyleFromAnywhere.class.getResourceAsStream(resourceName);
            if(stream == null) {
                throw new Exception("Cannot get resource \"" + resourceName + "\" from Jar file.");
            }
            int readBytes;
            byte[] buffer = new byte[4096];
            jarFolder = new File(InstallCheckstyleFromAnywhere.class.getProtectionDomain()
                .getCodeSource().getLocation().toURI().getPath()).getParentFile().getPath().replace(File.separatorChar, '/');
            resStreamOut = new FileOutputStream(jarFolder + resourceName);
            while ((readBytes = stream.read(buffer)) > 0) {
                resStreamOut.write(buffer, 0, readBytes);
            }
        } catch (Exception ex) {
            throw ex;
        } finally {
            if (stream != null) {
                stream.close();
            }
            if (resStreamOut != null) {
                resStreamOut.close();
            }
        }

        return jarFolder + resourceName;
    }

}