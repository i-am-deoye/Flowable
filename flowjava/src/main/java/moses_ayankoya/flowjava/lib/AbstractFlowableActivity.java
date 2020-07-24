package moses_ayankoya.flowjava.lib;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.Stack;


public abstract class AbstractFlowableActivity extends AppCompatActivity implements FlowableFragmentRegistry, FlowableNavigationController {
    private Registry.Builder registryBuilder;
    private Integer frameId;
    private Stack<Integer> session = new Stack<>();
    private Map<Integer, AbstractFlowableFragment> sessionFragment = new HashMap<>();
    private Integer currentFragmentId;
    private String currentFragmentIdStateKey = "current:fragment:id:state:key";
    private String currentModelIdStateKey = "current:model:id:state:key";
    private Serializable currentModel;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        registryBuilder = register();
        init();
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        if (Objects.isNull(currentFragmentId)) return;
        Bundle _savedInstanceState;

        if (Objects.isNull(savedInstanceState)) _savedInstanceState = new Bundle();
        else _savedInstanceState = savedInstanceState;

        _savedInstanceState.putInt(currentFragmentIdStateKey, currentFragmentId);
        _savedInstanceState.putSerializable(currentModelIdStateKey, currentModel);
        super.onSaveInstanceState(_savedInstanceState);

    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        Integer currentId = savedInstanceState.getInt(currentFragmentIdStateKey);
        Serializable currentModel = savedInstanceState.getSerializable(currentModelIdStateKey);
        if (Objects.isNull(currentId)) return;
        currentFragmentId = currentId;
        this.currentModel = currentModel;
    }


    private void init() {
        if (session.isEmpty()) {
            Registry registry = registryBuilder.getRootRegistry();
            if (!restoreFragmentIfAvailable()) innerNextTo(registry, null, registryBuilder.getRootIdentifier());
        }
    }

    private Boolean restoreFragmentIfAvailable() {
        if (Objects.isNull(currentFragmentId)) return false;
        present(currentFragmentId, currentModel);
        return true;
    }

    private void addFragmentToActivity(@NonNull Fragment fragment) {
        Objects.requireNonNull(getSupportFragmentManager());
        Objects.requireNonNull(fragment);
        Objects.requireNonNull(frameId);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(frameId, fragment);
        transaction.commit();
    }

    private void replaceFragmentInActivity(@NonNull Fragment fragment) {
        Objects.requireNonNull(getSupportFragmentManager());
        Objects.requireNonNull(fragment);
        Objects.requireNonNull(frameId);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(frameId, fragment, fragment.getClass().getSimpleName());
        transaction.commit();
    }

    protected void headerTitle(String title) {

    }


    @Override
    public Registry.Builder register() {
        return null;
    }

    public void setFrameId(Integer frameId) {
        this.frameId = frameId;
    }


    @Override
    public <T extends Serializable> void present(Integer fragmentRootId, T model) {
        if (Objects.nonNull(registryBuilder) && (currentFragmentId != fragmentRootId)) {
            if (registryBuilder.getContainer().containsKey(fragmentRootId)) {
                Registry registry = registryBuilder.getContainer().get(fragmentRootId);
                innerNextTo(registry, model, fragmentRootId);
            }
        } else {
            throw new RuntimeException("Registry Builder not configured");
        }
    }

    private <T extends Serializable> void innerNextTo(Registry registry, T model, Integer fragmentIdentifier) {
        Objects.requireNonNull(registry);
        Objects.requireNonNull(registry.getFragmentClass());
        Objects.requireNonNull(registry.getHeaderTitle());

        try {

            headerTitle(registry.getHeaderTitle());
            AbstractFlowableFragment fragment = (AbstractFlowableFragment) registry.getFragmentClass().newInstance();
            fragment.setStepModel(model);

            if (getSupportFragmentManager().getFragments().isEmpty()) {
                addFragmentToActivity(fragment);
            } else {
                replaceFragmentInActivity(fragment);
            }

            if (/*Objects.nonNull(currentFragmentId) && */ currentFragmentId != fragmentIdentifier) {
                currentFragmentId = fragmentIdentifier;
                session.add(currentFragmentId);
                sessionFragment.put(currentFragmentId, fragment);
            }
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void previous() {

        if (Objects.nonNull(registryBuilder) && !session.isEmpty()) {

            if (session.size() == 1) return;
            Integer beforePrevious = session.pop();
            sessionFragment.remove(beforePrevious);
            Integer previousFragmentId = session.pop();

            if (registryBuilder.getContainer().containsKey(previousFragmentId)) {
                innerPrevious(previousFragmentId);
            }
        } else {
            throw new RuntimeException("Registry Builder not configured");
        }
    }

    private void innerPrevious(Integer previousFragmentId) {
        Registry registry = registryBuilder.getContainer().get(previousFragmentId);
        Objects.requireNonNull(registry);
        Objects.requireNonNull(registry.getFragmentClass());
        Objects.requireNonNull(registry.getHeaderTitle());

        try {

            headerTitle(registry.getHeaderTitle());
            AbstractFlowableFragment fragment = sessionFragment.get(previousFragmentId);
            replaceFragmentInActivity(fragment);
            currentFragmentId = previousFragmentId;

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void toRoot() {
        if (Objects.nonNull(registryBuilder) && !session.isEmpty() && (session.size() > 1) ) {
            session.removeAllElements();
            innerPrevious(registryBuilder.getRootIdentifier());
            session.add(registryBuilder.getRootIdentifier());
            Set<Integer> sessionFragmentKeys = sessionFragment.keySet();
            for (Integer key : sessionFragmentKeys) {
                sessionFragment.remove(key);
            }
        } else {
            throw new RuntimeException("Registry Builder not configured");
        }
    }

    @Override
    public void onBackPressed() {
        if (session.size() == 1) {
            super.onBackPressed();
            return;
        }

        previous();
    }
}
