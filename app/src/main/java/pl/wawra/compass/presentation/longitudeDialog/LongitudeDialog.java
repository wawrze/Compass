package pl.wawra.compass.presentation.longitudeDialog;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import pl.wawra.compass.R;
import pl.wawra.compass.base.BaseDialog;

public class LongitudeDialog extends BaseDialog {

    private final LongitudeDialogCallback callback;
    private LongitudeDialogViewModel viewModel;
    private ArrayAdapter<String> adapter;

    private TextView inputError;
    private Button cancelButton;
    private Button confirmButton;
    private AutoCompleteTextView input;

    private LongitudeDialog(LongitudeDialogCallback callback) {
        this.callback = callback;
    }

    public static void createAndShow(FragmentManager fragmentManager, LongitudeDialogCallback callback) {
        LongitudeDialog dialog = new LongitudeDialog(callback);
        if (fragmentManager != null) dialog.show(fragmentManager);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewModelProvider viewModelProvider = new ViewModelProvider(this);
        viewModel = viewModelProvider.get(LongitudeDialogViewModel.class);
        viewModel.setCallBack(callback);
        Context context = getContext();
        if (context != null) {
            adapter = new ArrayAdapter<>(
                    getContext(),
                    android.R.layout.simple_dropdown_item_1line,    // TODO: custom item layout
                    new ArrayList<String>()
            );
        }
        return inflater.inflate(R.layout.dialog_longitude, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel.getPreviousLongitudes().observe(
                getViewLifecycleOwner(),
                new Observer<List<String>>() {
                    @Override
                    public void onChanged(List<String> strings) {
                        adapter.addAll(strings);
                    }
                }
        );
        bindViewElements();
        setupInput();
        setupClickListeners();
    }

    private void bindViewElements() {
        inputError = Objects.requireNonNull(getView()).findViewById(R.id.dialog_longitude_input_error_message);
        cancelButton = getView().findViewById(R.id.dialog_longitude_cancel_button);
        confirmButton = getView().findViewById(R.id.dialog_longitude_confirm_button);
        input = getView().findViewById(R.id.dialog_longitude_input);
    }

    private void setupInput() {
        input.setAdapter(adapter);
        input.setThreshold(1);
        input.setOnFocusChangeListener(
                new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {
                        if (hasFocus) input.showDropDown();
                    }
                }
        );
    }

    private void setupClickListeners() {
        cancelButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dismissAllowingStateLoss();
                    }
                }
        );
        confirmButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final String longitude = input.getText().toString();
                        viewModel.verifyLongitude(longitude).observe(
                                getViewLifecycleOwner(),
                                new Observer<Integer>() {
                                    @Override
                                    public void onChanged(Integer errorMessageRes) {
                                        if (errorMessageRes == 0) {
                                            inputError.setVisibility(View.GONE);
                                            onCorrectLongitude(longitude);
                                        } else {
                                            inputError.setText(errorMessageRes);
                                            inputError.setVisibility(View.VISIBLE);
                                        }
                                    }
                                }
                        );
                    }
                }
        );
    }

    private void onCorrectLongitude(String longitude) {
        viewModel.insertNewLongitude(longitude).observe(
                getViewLifecycleOwner(),
                new Observer<Boolean>() {
                    @Override
                    public void onChanged(Boolean isSuccess) {
                        if (isSuccess) {
                            Toast.makeText(
                                    getContext(),
                                    getString(R.string.new_longitude_set),
                                    Toast.LENGTH_LONG
                            ).show();
                            if (viewModel.getCallBack() != null)
                                viewModel.getCallBack().onNewLongitude();
                            dismissAllowingStateLoss();
                        } else {
                            Toast.makeText(
                                    getContext(),
                                    getString(R.string.unknown_error),
                                    Toast.LENGTH_LONG
                            ).show();
                        }
                    }
                }
        );
    }

}
