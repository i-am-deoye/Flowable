package moses_ayankoya.flowjava.lib;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Registry<T extends AbstractFlowableFragment> {
    private Class<T> fragmentClass;
    private String headerTitle;


    public Registry(Class<T> fragmentClass, String headerTitle) {
        this.fragmentClass = fragmentClass;
        this.headerTitle = headerTitle;
    }


    public Class<T> getFragmentClass() {
        return fragmentClass;
    }

    public String getHeaderTitle() {
        return headerTitle;
    }


    public static class Builder {
        private Map<Integer, Registry> container = new HashMap<>();
        private Integer rootIdentifier;

        public Builder create(Integer identifier, Registry registry) {
            if (container.containsKey(identifier))
                throw new RuntimeException("Duplicate keys or identifiers not allowed!!");
            if (container.isEmpty()) this.rootIdentifier = identifier;
            container.put(identifier, registry);
            return this;
        }

        public Map<Integer, Registry> getContainer() {
            return container;
        }
        public Registry getRootRegistry() {
            Objects.requireNonNull(rootIdentifier);
            return container.isEmpty() ? null : container.get(rootIdentifier);
        }
        public Integer getRootIdentifier() { return rootIdentifier; }
    }
}
