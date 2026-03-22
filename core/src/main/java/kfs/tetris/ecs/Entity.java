package kfs.tetris.ecs;

public class Entity {
    public final long id;

    public Entity(long id) {
        this.id = id;
    }

    @Override
    public int hashCode() {
        return Long.hashCode(id);
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof Entity && ((Entity) o).id == id;
    }
}
