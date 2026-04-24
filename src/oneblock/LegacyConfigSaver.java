// Copyright © 2025 MrMarL. The MIT License (MIT).
package oneblock;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;

import org.bukkit.configuration.file.FileConfiguration;

import com.cryptomorin.xseries.XMaterial;

public class LegacyConfigSaver {
	protected static File file;
	
	public static void Save(final FileConfiguration fc, final File f) {
		file = f;
		// 1.8.x - 1.17.x
		if (!XMaterial.supports(1,18)) try {
	        ArrayList<String> inputStr1 = new ArrayList<String>();
	        try (BufferedReader fileIn = new BufferedReader(new FileReader(f))) {
	            String line;
	            while ((line = fileIn.readLine()) != null)
	                inputStr1.add(line);
	        }
	        ArrayList<String> inputStr2 = new ArrayList<String>();
	        inputStr2.addAll(Arrays.asList(fc.saveToString().split("\n")));
	        StringBuffer inputBuffer = new StringBuffer();

	        int i = 0;
	        for (String a:inputStr1) {
	        	if (i >= inputStr2.size())
	        		break;
	        	if (a.contains("#") || a.isEmpty())
	        		inputBuffer.append(a);
	        	else
	        		inputBuffer.append(inputStr2.get(i++));
    			inputBuffer.append('\n');
	        }

	        while (i < inputStr2.size()) {
	        	inputBuffer.append(inputStr2.get(i++));
	        	inputBuffer.append('\n');
	        }

	        try (FileOutputStream fileOut = new FileOutputStream(f)) {
	            fileOut.write(inputBuffer.toString().getBytes(java.nio.charset.StandardCharsets.UTF_8));
	        }
	        return;
		} 
		catch (Exception e) {
			Oneblock.plugin.getLogger().warning("Legacy config save failed for " + f + ": " + e.getMessage());
		}
		
		// 1.18+
		try { fc.save(f); } 
		catch (Exception e) {
			Oneblock.plugin.getLogger().warning("Config save failed for " + f + ": " + e.getMessage());
		}
	}
	
	public static void Save (final FileConfiguration fc) {
		Save(fc, file);
	}
}