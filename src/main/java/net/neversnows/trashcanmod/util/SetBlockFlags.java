package net.neversnows.trashcanmod.util;

public enum SetBlockFlags {
    /**Will cause a block update.*/
    UPDATE_BLOCKS(0b1),
    /**Will send the change to clients.*/
    UPDATE_CLIENTS(0b10),
    /**Will prevent the block from being re-rendered.*/
    PREVENT_RENDER(0b100),
    /**Will force any re-renders to run on the main thread instead.*/
    RENDER_ON_MAIN_THREAD(0b1000),
    /**Will prevent neighbor reactions (e.g. fences connecting, observers pulsing).*/
    PREVENT_NEIGHBOR_UPDATE(0b10000),
    /**Will prevent neighbor reactions from spawning drops.*/
    PREVENT_NEIGHBOR_DROPS(0b100000),
    /**Will signify the block is being moved.*/
    BLOCK_MOVED(0b1000000);

    public final int value;
    SetBlockFlags(int flag){
        this.value = flag;
    }

    /**@return All flags combined with the bitwise OR operator*/
    public static int getCompoundFlag(SetBlockFlags... flags){
        int output = 0;
        for(SetBlockFlags flag : flags){
            output |= flag.value;
        }
        return output;
    }
}
