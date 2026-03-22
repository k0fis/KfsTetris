package kfs.tetris.ecs;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class KfsWorld implements KfsSystem {

    private long nextId;
    private final Map<Class<? extends KfsComp>, Map<Entity, KfsComp>> components;
    private final List<KfsSystem> systems;

    protected KfsWorld() {
        this.nextId = 1;
        this.components = new HashMap<>();
        this.systems = new ArrayList<>();
    }

    protected void reset() {
        components.clear();
        this.nextId = 1;
    }

    protected synchronized Entity createEntity() {
        return new Entity(nextId++);
    }

    public void deleteEntity(Entity entity) {
        for (Map.Entry<Class<? extends KfsComp>, Map<Entity, KfsComp>> entry : components.entrySet()) {
            entry.getValue().remove(entity);
        }
    }

    public <T extends KfsComp> void addComponent(Entity e, T component) {
        components
            .computeIfAbsent(component.getClass(), k -> new HashMap<>())
            .put(e, component);
    }

    public <T extends KfsComp> T getComponent(Entity e, Class<T> type) {
        return type.cast(components.getOrDefault(type, Map.of()).get(e));
    }

    public List<Entity> getEntitiesWith(Class<? extends KfsComp> c1) {
        return new ArrayList<>(components.getOrDefault(c1, Map.of()).keySet());
    }

    public void addSys(KfsSystem system) {
        systems.add(system);
    }

    @SuppressWarnings("unchecked")
    public <T> T getSystem(Class<T> type) {
        for (KfsSystem system : systems) {
            if (type.isAssignableFrom(system.getClass())) {
                return (T) system;
            }
        }
        return null;
    }

    @Override
    public void init() {
        runSystems(KfsSystem::init);
    }

    @Override
    public void update(float delta) {
        runSystems(s -> s.update(delta));
    }

    @Override
    public void render(SpriteBatch batch) {
        runSystems(s -> s.render(batch));
    }

    @Override
    public void done() {
        runSystems(KfsSystem::done);
    }

    protected void runSystems(Consumer<? super KfsSystem> us) {
        systems.forEach(us);
    }
}
