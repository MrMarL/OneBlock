package Oneblock.Utils;

import net.md_5.bungee.api.ChatColor;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {
    public static boolean findMethod(final Class<?> cl, String name) {
        return Arrays.stream(cl.getMethods()).anyMatch(m -> m.getName().equals(name));
    }
	
    public static String translateColorCodes(String message) {
        if (message == null) return null;
        
        message = translateHexColorCodes(message);
        return ChatColor.translateAlternateColorCodes('&', message);
    }
    
    private static String translateHexColorCodes(String message) {
    	if (!isHexColorSupported()) return message;
    	
        Pattern hexPattern = Pattern.compile("&#([A-Fa-f0-9]{6})");
        Matcher matcher = hexPattern.matcher(message);
        StringBuffer buffer = new StringBuffer();
        
        while (matcher.find()) {
            String hexColor = matcher.group(1);
            matcher.appendReplacement(buffer, ChatColor.of("#" + hexColor).toString());
        }
        matcher.appendTail(buffer);
        
        return buffer.toString();
    }

    public static boolean isHexColorSupported() {
        try {
            ChatColor.of("#FFFFFF");
            return true;
        } catch (Exception e) { return false; }
    }
}