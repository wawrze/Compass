package pl.wawra.compass.presentation.targetDialog;

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

import kotlin.Pair;
import pl.wawra.compass.R;
import pl.wawra.compass.base.BaseDialog;

public class TargetDialog extends BaseDialog {

    private final TargetDialogCallback callback;
    private TargetDialogViewModel viewModel;
    private ArrayAdapter<String> longitudeAdapter;
    private ArrayAdapter<String> latitudeAdapter;

    private AutoCompleteTextView latitudeInput;
    private AutoCompleteTextView longitudeInput;
    private TextView latitudeInputError;
    private TextView longitudeInputError;
    private Button cancelButton;
    private Button confirmButton;

    private TargetDialog(TargetDialogCallback callback) {
        this.callback = callback;
    }

    public static void createAndShow(FragmentManager fragmentManager, TargetDialogCallback callback) {
        TargetDialog dialog = new TargetDialog(callback);
        if (fragmentManager != null) dialog.show(fragmentManager);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewModelProvider viewModelProvider = new ViewModelProvider(this);
        viewModel = viewModelProvider.get(TargetDialogViewModel.class);
        viewModel.setCallBack(callback);
        Context context = getContext();
        if (context != null) {
            longitudeAdapter = new ArrayAdapter<>(getContext(), R.layout.item_dropdown, new ArrayList<String>());
            latitudeAdapter = new ArrayAdapter<>(getContext(), R.layout.item_dropdown, new ArrayList<String>());
        }
        return inflater.inflate(R.layout.dialog_target, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel.getPreviousLatitudes().observe(
                getViewLifecycleOwner(),
                new Observer<List<String>>() {
                    @Override
                    public void onChanged(List<String> strings) {
                        latitudeAdapter.addAll(strings);
                    }
                }
        );
        viewModel.getPreviousLongitudes().observe(
                getViewLifecycleOwner(),
                new Observer<List<String>>() {
                    @Override
                    public void onChanged(List<String> strings) {
                        longitudeAdapter.addAll(strings);
                    }
                }
        );
        bindViewElements();
        setupInputs();
        setupClickListeners();
    }

    private void bindViewElements() {
        latitudeInput = Objects.requireNonNull(getView()).findViewById(R.id.dialog_target_latitude_input);
        longitudeInput = Objects.requireNonNull(getView()).findViewById(R.id.dialog_target_longitude_input);
        latitudeInputError = Objects.requireNonNull(getView()).findViewById(R.id.dialog_target_latitude_input_error_message);
        longitudeInputError = Objects.requireNonNull(getView()).findViewById(R.id.dialog_target_longitude_input_error_message);
        cancelButton = Objects.requireNonNull(getView()).findViewById(R.id.dialog_target_cancel_button);
        confirmButton = Objects.requireNonNull(getView()).findViewById(R.id.dialog_target_confirm_button);
    }

    private void setupInputs() {
        latitudeInput.setAdapter(latitudeAdapter);
        latitudeInput.setThreshold(1);
        latitudeInput.setOnFocusChangeListener(
                new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {
                        if (hasFocus) latitudeInput.showDropDown();
                    }
                }
        );
        longitudeInput.setAdapter(longitudeAdapter);
        longitudeInput.setThreshold(1);
        longitudeInput.setOnFocusChangeListener(
                new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {
                        if (hasFocus) longitudeInput.showDropDown();
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
                        final String latitude = latitudeInput.getText().toString();
                        final String longitude = longitudeInput.getText().toString();
                        viewModel.verifyTarget(latitude, longitude).observe(
                                getViewLifecycleOwner(),
                                new Observer<Pair<Integer, Integer>>() {
                                    @Override
                                    public void onChanged(Pair<Integer, Integer> errorMessagesRes) {
                                        int latitudeErrorMessageRes = errorMessagesRes.getFirst();
                                        int longitudeErrorMessageRes = errorMessagesRes.getSecond();

                                        if (latitudeErrorMessageRes == 0 && longitudeErrorMessageRes == 0) {
                                            latitudeInputError.setVisibility(View.INVISIBLE);
                                            longitudeInputError.setVisibility(View.INVISIBLE);
                                            onCorrectTarget(latitude, longitude);
                                        } else {
                                            if (latitudeErrorMessageRes != 0) {
                                                latitudeInputError.setText(latitudeErrorMessageRes);
                                                latitudeInputError.setVisibility(View.VISIBLE);
                                            } else {
                                                latitudeInputError.setVisibility(View.INVISIBLE);
                                            }
                                            if (longitudeErrorMessageRes != 0) {
                                                longitudeInputError.setText(longitudeErrorMessageRes);
                                                longitudeInputError.setVisibility(View.VISIBLE);
                                            } else {
                                                longitudeInputError.setVisibility(View.INVISIBLE);
                                            }
                                        }
                                    }
                                }
                        );
                    }
                }
        );
    }

    private void onCorrectTarget(String latitude, String longitude) {
        viewModel.insertNewTarget(latitude, longitude).observe(
                getViewLifecycleOwner(),
                new Observer<Boolean>() {
                    @Override
                    public void onChanged(Boolean isSuccess) {
                        if (isSuccess) {
                            Toast.makeText(
                                    getContext(),
                                    getString(R.string.new_target_set),
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
