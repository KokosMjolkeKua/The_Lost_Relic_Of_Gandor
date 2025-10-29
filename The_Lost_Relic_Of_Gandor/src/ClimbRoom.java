public class ClimbRoom extends Room {
    private final boolean requiresShoes;
    private final boolean requiresHook;

    public ClimbRoom(String description, boolean requiresShoes, boolean requiresHook) {
        super(description);
        this.requiresShoes = requiresShoes;
        this.requiresHook = requiresHook;
    }

    public boolean canClimb(boolean hasShoes, boolean hasHook) {
        if (requiresShoes && !hasShoes) return false;
        if (requiresHook && !hasHook) return false;
        return true;
    }
}
