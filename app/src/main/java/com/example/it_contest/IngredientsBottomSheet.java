package com.example.it_contest;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.it_contest.model.Ingredient;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.List;

public class IngredientsBottomSheet extends BottomSheetDialogFragment {

    private static final String ARG_INGREDIENTS = "ingredients";
    private static final String ARG_TITLE = "title";

    public static IngredientsBottomSheet newInstance(String title, List<Ingredient> ingredients) {
        IngredientsBottomSheet fragment = new IngredientsBottomSheet();
        Bundle args = new Bundle();
        args.putString(ARG_TITLE, title);
        args.putSerializable(ARG_INGREDIENTS, (java.io.Serializable) ingredients);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_sheet_ingredients, container, false);

        TextView tvTitle = view.findViewById(R.id.tv_ingredient_title);
        RecyclerView rvIngredients = view.findViewById(R.id.rv_ingredients);

        if (getArguments() != null) {
            String title = getArguments().getString(ARG_TITLE);
            List<Ingredient> ingredients = (List<Ingredient>) getArguments().getSerializable(ARG_INGREDIENTS);

            tvTitle.setText(title);
            rvIngredients.setLayoutManager(new LinearLayoutManager(getContext()));
            rvIngredients.setAdapter(new IngredientAdapter(ingredients));
        }

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        View bottomSheet = getDialog().findViewById(com.google.android.material.R.id.design_bottom_sheet);
        if (bottomSheet != null) {
            bottomSheet.setBackground(null); // ✅ 기본 배경 제거
        }
    }
}
