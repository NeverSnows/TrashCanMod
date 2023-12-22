package net.neversnows.trashcanmod.util;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.world.phys.Vec3;

public class ModUtils {
    private ModUtils(){};

    public static Vec3 parseToVec3(Vec3i source){
        return new Vec3(source.getX(), source.getY(), source.getZ());
    }

    public static BlockPos parseToBlockPos(Vec3 source){
        return new BlockPos((int)Math.floor(source.x), (int)Math.floor(source.y), (int)Math.floor(source.z));
    }
}
