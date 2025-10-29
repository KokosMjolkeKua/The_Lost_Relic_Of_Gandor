public class DragonRoom extends GenericRoom {
    private Enemy dragon;

    public DragonRoom(String description, Enemy dragon) {
        super(description);
        this.dragon = dragon;
    }

    public Enemy getDragon() { return dragon; }

    public void attackDragon(int dmg) {
        if (dragon != null) dragon.takeDamage(dmg);
    }
}
