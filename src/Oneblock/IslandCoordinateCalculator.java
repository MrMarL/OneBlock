package Oneblock;

import org.bukkit.Location;

public final class IslandCoordinateCalculator {
	
	public static int findNearestRegionId(Location loc) {
	    if (loc == null) return 0;
	    int nearestId = 0;
	    int minDistSq = Integer.MAX_VALUE;
	    int locX = loc.getBlockX();
	    int locZ = loc.getBlockZ();
	    int size = PlayerInfo.size();
	    
	    int halfStoSquared = (Oneblock.offset / 2) * (Oneblock.offset / 2);
	    
	    int X = 0, Z = 0;
	    
	    for (int i = 0; i < size; i++) {
	    	if (!Oneblock.CircleMode) {
		    	if (X > Z)
		    		if (X > -Z)
		    			Z--;
		    		else
		    			X--;
		    	else if (-X > Z || X == Z && Z < 0)
		    		Z++;
		    	else
		    		X++;
	    	} 
	    	else X = i; 
	    	
	        int dx = (X * Oneblock.offset + Oneblock.x) - locX;
	        int dz = (Z * Oneblock.offset + Oneblock.z) - locZ;
	        int distSq = dx * dx + dz * dz;
	        
	        if (distSq < minDistSq) {
	            minDistSq = distSq;
	            nearestId = i;
	            
	            if (minDistSq <= halfStoSquared) {
	                break;
	            }
	        }
	    }
	    return nearestId;
	}
	
	public static int[] getById(int id, int x, int z, int diameter, boolean CircleMode) {
		if (!CircleMode) return new int[] {id * diameter + x, z, id};
		
		return getByIdGibrid(id, x, z, diameter);
	}
	
	private static int[] getByIdIter(int id, int x, int z, int diameter) {
		int X = 0, Z = 0;
		for (int i = 0; i < id; i++) {
			if (X > Z)
			    if (X > -Z)
				    Z--;
				else
				    X--;
			else if (-X > Z || X == Z && Z < 0)
				Z++;
			else
				X++;
		}
		X = X * diameter + x;
		Z = Z * diameter + z;
		return new int[] {X, Z, id};
	}
	
	private static int[] getByIdGibrid(int id, int x, int z, int diameter) {
	    if (id <= 30) return getByIdIter(id, x, z, diameter);

	    int ring = (int) Math.floor((Math.sqrt(id) + 1) / 2);
	    int firstInRing = (2 * ring - 1) * (2 * ring - 1) + 1;
	    int posInRing = id + 1 - firstInRing;
	    int sideLength = 2 * ring;
	    int side = posInRing / sideLength;
	    int offset = posInRing % sideLength;

	    int X, Z;

	    switch (side) {
	        case 0:
	            X = ring;
	            Z = ring - 1 - offset;
	            break;
	        case 1:
	            X = ring - 1 - offset;
	            Z = -ring;
	            break;
	        case 2:
	            X = -ring;
	            Z = -ring + 1 + offset;
	            break;
	        default:
	        	X = -ring + 1 + offset;
	            Z = ring;
	    }

	    X = X * diameter + x;
	    Z = Z * diameter + z;
	    return new int[] {X, Z, id};
	}
}
