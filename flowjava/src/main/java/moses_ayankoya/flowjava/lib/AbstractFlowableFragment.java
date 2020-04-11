package moses_ayankoya.flowjava.lib;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.io.Serializable;
import java.util.Objects;

public abstract class AbstractFlowableFragment extends Fragment {
    private final String modelKey = "model:key:any:type";
    private FlowableNavigationController flowableNavigationController;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setFlowableNavigation();
    }

    private void setFlowableNavigation() {
        if (getActivity() instanceof FlowableNavigationController) {
            flowableNavigationController = (FlowableNavigationController) getActivity();
        } else {
            throw new RuntimeException("");
        }
    }


    final protected  <T extends Serializable> T getStepModel() {
        if (Objects.nonNull(getArguments())) {
            T model = (T) getArguments().getSerializable(modelKey);
            return model;
        }
        return null;
    }


    final public  <T extends Serializable>void setStepModel(T model) {
        Bundle info = new Bundle();
        info.putSerializable(modelKey, model);
        setArguments(info);
    }


    final  protected <T extends Serializable>void present(Integer fragmentRootId, T model) {
        Objects.requireNonNull(fragmentRootId);
        Objects.requireNonNull(flowableNavigationController);
        flowableNavigationController.present(fragmentRootId, model);
    }

    final protected void previous() {
        Objects.requireNonNull(flowableNavigationController);
        flowableNavigationController.previous();
    }

    final protected void toRoot() {
        Objects.requireNonNull(flowableNavigationController);
        flowableNavigationController.toRoot();
    }


}
