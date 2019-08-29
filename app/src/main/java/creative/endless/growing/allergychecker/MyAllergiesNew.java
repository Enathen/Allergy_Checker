package creative.endless.growing.allergychecker;

import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicInteger;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;


public class MyAllergiesNew extends Fragment implements SearchView.OnQueryTextListener {
    ListViewAdapter adapter;
    ListView listView;
    ArrayList<View> parentViews = new ArrayList<>();
    private LinearLayout parentFrame;
    private ViewGroup container;


    public MyAllergiesNew() {
        // Required empty public constructor
    }

    public static MyAllergiesNew newInstance() {
        MyAllergiesNew fragment = new MyAllergiesNew();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public static Fragment getInstance() {
        return new MyAllergiesNew();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_allergies, container, false);
        parentFrame = view.findViewById(R.id.parent);
        this.container = container;

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        new SetupAllergyView(parentFrame, new AllergyList(getContext()).getMyAllergies(), ValidateAllergiesPreferences.setupAllergy()).execute();

        SearchView searchView = (SearchView) parentFrame.findViewById(R.id.searchBarAllergies);
        TreeMap<Integer, ArrayList<Integer>> keys = new AllergyList(getContext()).getMyAllergies();
        ArrayList<Integer> keysString = new ArrayList<>();
        for (ArrayList<Integer> key : keys.values()) {
            keysString.addAll(key);

        }
        adapter = new ListViewAdapter(getContext(), keysString, ValidateAllergiesPreferences.setupAllergy());
        searchView = parentFrame.findViewById(R.id.searchBarAllergies);
        //change icon color
        listView = (ListView) parentFrame.findViewById(R.id.listViewAllergies);
        listView.setAdapter(adapter);
        adapter.filter("ABCDEFGHJI");
        searchView.setOnQueryTextListener(this);
        final SearchView finalSearchView = searchView;
        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                finalSearchView.setQuery("", true);
                return false;
            }
        });
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public boolean onQueryTextSubmit(String s) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String s) {
        int filter;
        if (s.equals("")) {
            filter = adapter.filter("ABCDEFGHJI");
            for (View parentView : parentViews) {
                parentView.setVisibility(View.VISIBLE);
            }

        } else {
            filter = adapter.filter(s);
            for (View parentView : parentViews) {
                parentView.setVisibility(View.GONE);
            }
        }
        setListViewHeightBasedOnChildren(listView, filter, listView.getSelectedView());
        return true;
    }

    private void setListViewHeightBasedOnChildren(ListView listView, int amount, View view) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }
        int totalHeight = 0;
        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.UNSPECIFIED);
        for (int i = 0; i < listAdapter.getCount(); i++) {
            view = listAdapter.getView(i, view, listView);
            if (i == 0) {
                view.setLayoutParams(new ViewGroup.LayoutParams(desiredWidth, LinearLayout.LayoutParams.WRAP_CONTENT));
            }

            view.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += view.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (amount));
        listView.setLayoutParams(params);
    }


    private class SetupAllergyView extends AsyncTask<Object, Object, Object> {

        private LinearLayout parentView;
        private TreeMap<Integer, ArrayList<Integer>> myAllergyPreference;
        private HashMap<Integer, Integer> hashMap;

        public SetupAllergyView(LinearLayout parentView, TreeMap<Integer, ArrayList<Integer>> myAllergyPreference, HashMap<Integer, Integer> hashMap) {

            this.parentView = parentView;
            this.myAllergyPreference = myAllergyPreference;
            this.hashMap = hashMap;
        }

        @Override
        protected Object doInBackground(Object... objects) {
            final AtomicInteger atomicInteger = new AtomicInteger(0);

            for (final Integer integer : myAllergyPreference.keySet()) {
                getActivity().runOnUiThread(new Runnable() {
                    boolean shown = true;
                    ArrayList<View> views = new ArrayList<>();
                    ArrayList<CheckBox> childCheckbox = new ArrayList<>();
                    private CheckBox parentCB;

                    @Override
                    public void run() {
                        if (isAdded() && getActivity() != null) {
                            final LinearLayout inflate = (LinearLayout) getLayoutInflater().inflate(R.layout.allergy_parent_list_item, container, false);
                            final LinearLayout parent = inflate.findViewById(R.id.linLayAllergies);
                            parentViews.add(inflate);
                            ((TextView) parent.findViewById(R.id.name)).setText(getString(integer));
                            final ImageView chevron = parent.findViewById(R.id.chevron);
                            parentCB = parent.findViewById(R.id.checkBox);
                            parentCB.setChecked(PreferenceManager.getDefaultSharedPreferences(getContext()).getBoolean(getString(integer), false));

                            parentCB.setOnCheckedChangeListener(parentCheckboxListener());

                            chevron.setOnClickListener(insertChildrenAllergies(inflate, chevron));
                            parent.setOnClickListener(insertChildrenAllergies(inflate, chevron));
                            parentView.addView(inflate);

                        }

                    }

                    @NotNull
                    private CompoundButton.OnCheckedChangeListener parentCheckboxListener() {
                        return new CompoundButton.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                                for (CheckBox checkbox : childCheckbox) {
                                    checkbox.setChecked(b);
                                }
                                if(childCheckbox.isEmpty()){

                                }
                                PreferenceManager.getDefaultSharedPreferences(getContext()).edit().putBoolean(getString(integer), b).apply();

                            }
                        };
                    }

                    @NotNull
                    private View.OnClickListener insertChildrenAllergies(final LinearLayout inflate, final ImageView chevron) {
                        return new View.OnClickListener() {

                            @Override
                            public void onClick(View view) {
                                if (!views.isEmpty()) {
                                    for (View view1 : views) {
                                        view1.setVisibility(shown ? View.GONE : View.VISIBLE);
                                        chevron.setRotation(shown ? -90 : 90);
                                    }
                                    shown = !shown;
                                } else {
                                    chevron.setRotation(90);

                                    for (final Integer allergy : myAllergyPreference.get(integer)) {
                                        final View child = getLayoutInflater().inflate(R.layout.allergy_child_list_item, container, false);
                                        ((TextView) child.findViewById(R.id.name)).setText(getString(allergy));
                                        inflate.addView(child);
                                        views.add(child);
                                        final CheckBox cb = child.findViewById(R.id.checkBox);
                                        childCheckbox.add(cb);
                                        cb.setChecked(PreferenceManager.getDefaultSharedPreferences(getContext()).getBoolean(getString(allergy), false));

                                        child.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                cb.setChecked(!cb.isChecked());
                                            }
                                        });
                                        ((CheckBox) child.findViewById(R.id.checkBox)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                                            @Override
                                            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                                                boolean checkAll = true;
                                                int i = 0;
                                                for (CheckBox checkbox : childCheckbox) {
                                                    if (!checkbox.isChecked()) {
                                                        checkAll = false;
                                                        i++;
                                                    }
                                                }
                                                if (checkAll) {
                                                    parentCB.setOnCheckedChangeListener(null);
                                                    parentCB.setChecked(true);
                                                    parentCB.setOnCheckedChangeListener(parentCheckboxListener());
                                                }
                                                if(i == 1){
                                                    parentCB.setOnCheckedChangeListener(null);
                                                    parentCB.setChecked(false);
                                                    parentCB.setOnCheckedChangeListener(parentCheckboxListener());
                                                }
                                                PreferenceManager.getDefaultSharedPreferences(getContext()).edit().putBoolean(getString(allergy), b).apply();

                                            }
                                        });
                                    }

                                }

                            }
                        };
                    }
                });

            }
            return null;
        }
    }
}

