public class GoblinRoom extends GenericRoom {
    private Enemy enemy;

    public GoblinRoom(String description, Enemy enemy) {
        super(description);
        this.enemy = enemy;
    }

    public Enemy getEnemy() { return enemy; }

    public void attackEnemy(int dmg) {
        if (enemy != null) enemy.takeDamage(dmg);
    }

    public void removeEnemy() { this.enemy = null; }
}
