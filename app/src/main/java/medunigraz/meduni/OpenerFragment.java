package medunigraz.meduni;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;



public class OpenerFragment extends Fragment {



    public OpenerFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static OpenerFragment newInstance() {
        OpenerFragment fragment = new OpenerFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_opener, container, false);

        return view;
    }

}
