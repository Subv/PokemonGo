package pokemon;

public class Pokemon {
    int id;
    int attack;
    int defense;
    int current_hp;
    int max_hp;
    PokemonTemplate template;
    String nickname;


    /**
     * Checks whether the pokemon has reached the max attack and defense stats for his species, and
     * can evolve.
     * @return Whether the Pokemon can evolve to the next evolution.
     */
    public boolean CanEvolve() {
        return attack >= template.getMaxAttack() && defense >= template.getMaxDefense();
    }
}
