// Copyright © 2025 MrMarL. The MIT License (MIT).
package Oneblock;

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
		if (!XMaterial.supports(18)) try {
	        BufferedReader fileIn = new BufferedReader(new FileReader(f));
	        StringBuffer inputBuffer = new StringBuffer();
	        String line;

	        ArrayList<String> inputStr1 = new ArrayList<String>();
	        ArrayList<String> inputStr2 = new ArrayList<String>();
	        while ((line = fileIn.readLine()) != null)
	        	inputStr1.add(line);
	        fileIn.close();
	        inputStr2.addAll(Arrays.asList(fc.saveToString().split("\n")));
	        inputBuffer = new StringBuffer();
	        
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
	        
	        FileOutputStream fileOut = new FileOutputStream(f);
	        fileOut.write(inputBuffer.toString().getBytes());
	        fileOut.close();
	        return;
		} 
		catch (Exception e) {}
		
		// 1.18+
		try { fc.save(f); } 
		catch (Exception e) {}
	}
	
	public static void Save (final FileConfiguration fc) {
		Save(fc, file);
	}
}