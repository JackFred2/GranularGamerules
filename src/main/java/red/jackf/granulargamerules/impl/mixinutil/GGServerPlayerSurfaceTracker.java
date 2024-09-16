package red.jackf.granulargamerules.impl.mixinutil;

public interface GGServerPlayerSurfaceTracker {
    /**
     * Update whether a server player is under the surface.
     * @return If the value has changed this tick.
     */
    boolean gg$updateIsUnderSurface();
}
