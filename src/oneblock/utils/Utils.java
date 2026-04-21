package oneblock.utils;

import net.md_5.bungee.api.ChatColor;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.World;

public class Utils {
    public static final boolean isWorldMinHeightSupported = findMethod(World.class, "getMinHeight");
	
    public static boolean findMethod(final Class<?> cl, String name) {
        return Arrays.stream(cl.getMethods()).anyMatch(m -> m.getName().equals(name));
    }
	
    public static String translateColorCodes(String message) {
        if (message == null) return null;
        
        message = translateHexColorCodes(message);
        return org.bukkit.ChatColor.translateAlternateColorCodes('&', message);
    }
    
    private static String translateHexColorCodes(String message) {
    	if (!HEX_COLOR_SUPPORTED) return message;
    	
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

    /**
     * Cached result of the one-time hex-colour-support probe. The probe itself
     * is a try/catch on a method that doesn't exist on pre-1.16 servers; caching
     * the outcome keeps the per-message {@link #translateColorCodes} path
     * exception-free.
     */
    private static final boolean HEX_COLOR_SUPPORTED = probeHexColorSupport();

    /** @return {@code true} if {@link ChatColor#of(String)} is available on this server's BungeeChat API. */
    public static boolean isHexColorSupported() {
        return HEX_COLOR_SUPPORTED;
    }

    /**
     * One-shot feature probe. Intentionally swallows {@link NoSuchMethodError}
     * (method absent on pre-1.16 API jars) and {@link Exception} (defensive:
     * hosts with shaded / relocated BungeeChat can throw). Returning {@code false}
     * is the correct graceful-degradation path, so no log is emitted.
     */
    private static boolean probeHexColorSupport() {
        try {
            ChatColor.of("#FFFFFF");
            return true;
        } catch (NoSuchMethodError | Exception e) {
            return false;
        }
    }
    
    public static int getWorldMinHeight(World world) {
        if (isWorldMinHeightSupported) 
            return world.getMinHeight();
        return 0;
    }
}