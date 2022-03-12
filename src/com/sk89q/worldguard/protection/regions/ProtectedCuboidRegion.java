package com.sk89q.worldguard.protection.regions;

import java.awt.geom.Area;
import java.util.List;

import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldedit.math.BlockVector2;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.domains.DefaultDomain;

public class ProtectedCuboidRegion extends ProtectedRegion {

	public ProtectedCuboidRegion(String format, BlockVector bl1, BlockVector bl2) {super(format, false);}
	public ProtectedCuboidRegion(String format, BlockVector3 bl1, BlockVector3 bl2) {super(format, false);}

	public DefaultDomain getMembers() {return null;}
	public boolean isPhysicalArea() {return false;}
	public List<BlockVector2> getPoints() {return null;}
	public int volume() {return 0;}
	public boolean contains(BlockVector3 pt) {return false;}
	public RegionType getType() {return null;}
	Area toArea() {return null;}
}
