Flowable helps the interactions, which users navigate across any point where an event is being taking place from differient parts of contents in the app.

[![](https://jitpack.io/v/i-am-deoye/Flowable.svg)](https://jitpack.io/#i-am-deoye/Flowable)

# Setup
#### Step 1. Add it in your root build.gradle at the end of repositories:

```
allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
```
#### Step 2. Add the dependency
```
dependencies {
	        implementation 'com.github.i-am-deoye:Flowable:1.0.1'
	}
```

# Usage

You need to extend *AbstractFlowableActivity abstract class*, and also register all required fragment classes. While this is done, it is mandatory to specify an **id name** of parent layout in your activity layout.

```
public class MainActivity extends AbstractFlowableActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public Registry.Builder register() {
        this.setFrameId(R.id.rootLayout);
        Registry.Builder builder = new Registry.Builder();

        Registry mainRegistry = new Registry(MainFragment.class, "Main Fragment");
        Registry mainRegistry1 = new Registry(MainFragment1.class, "Main Fragment 1");

        builder.create(R.id.mainFragment, mainRegistry)
                .create(R.id.mainFragment1, mainRegistry1);

        return builder;
    }
}
```


You also need to extend *AbstractFlowableFragment abstract class*, which contain the APIs for navigation and passing of data to another fragment if required :

```
<T extends Serializable>void present(Integer fragmentRootId, T model);
void previous();
void toRoot();
```

```
public class MainFragment extends AbstractFlowableFragment {

    private Button nextButton;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_main, container, false);
        nextButton = root.findViewById(R.id.nextButton);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Model model = new Model();
                model.name = "Model 1";
                present(R.id.mainFragment1, model);
            }
        });
        return root;
    }
}
```

```
public class MainFragment1 extends AbstractFlowableFragment {

    private Button backButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_main1, container, false);
        Model model = getStepModel();
        backButton = root.findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toRoot();
            }
        });
        return root;
    }
}
```
