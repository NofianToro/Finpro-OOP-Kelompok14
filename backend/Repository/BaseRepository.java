package Repository;

import java.util.*;

public abstract class BaseRepository<T, ID> {
    Map<ID, T> dataMap = new HashMap<>();
    protected List<T> allData = new ArrayList<>();

    public Optional<T> findById(ID id) {
        return Optional.ofNullable(dataMap.get(id));
    }

    public List<T> findAll() {
        return new ArrayList<>(allData);
    }
    public void deleteById(ID id) {
        T entity = dataMap.remove(id);
        if (entity != null) {
            allData.remove(entity);
        }
    }


    public abstract void save(T entity);
    public abstract ID getId(T entity);
}