package moses_ayankoya.flowjava.lib;

import java.io.Serializable;

public interface FlowableNavigationController {
    <T extends Serializable>void present(Integer fragmentRootId, T model);
    void previous();
    void toRoot();
}

