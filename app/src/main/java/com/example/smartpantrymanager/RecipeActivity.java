
package com.example.smartpantrymanager;

import android.database.Cursor;
import android.os.Bundle;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class RecipeActivity extends AppCompatActivity {

    private DatabaseHelper databaseHelper;
    private LinearLayout recipeList;
    private TextView emptyMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        databaseHelper = new DatabaseHelper(this);

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(32, 24, 32, 24);

        TextView title = new TextView(this);
        title.setText("Suggested Recipes");
        title.setTextSize(26);
        title.setGravity(Gravity.CENTER);
        title.setPadding(0, 0, 0, 12);
        root.addView(title);

        TextView subtitle = new TextView(this);
        subtitle.setText("Recipes appear only when you have all required ingredients in sufficient quantities.");
        subtitle.setTextSize(15);
        subtitle.setPadding(0, 0, 0, 16);
        root.addView(subtitle);

        emptyMessage = new TextView(this);
        emptyMessage.setText(
                "No recipes match your pantry yet.\n\n" +
                        "Add the required ingredients and quantities to your pantry, " +
                        "then return here."
        );
        emptyMessage.setTextSize(17);
        emptyMessage.setGravity(Gravity.CENTER);
        emptyMessage.setPadding(16, 24, 16, 24);
        root.addView(emptyMessage);

        ScrollView scrollView = new ScrollView(this);
        recipeList = new LinearLayout(this);
        recipeList.setOrientation(LinearLayout.VERTICAL);
        scrollView.addView(recipeList);

        root.addView(scrollView, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, 0, 1
        ));

        Button backButton = new Button(this);
        backButton.setText("Back to Pantry");
        backButton.setOnClickListener(v -> finish());
        root.addView(backButton);

        setContentView(root);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (databaseHelper != null && recipeList != null) {
            loadSuggestedRecipes();
        }
    }

    private void loadSuggestedRecipes() {
        recipeList.removeAllViews();

        Cursor cursor = databaseHelper.getSuggestedRecipes();
        int recipeCount = 0;

        try {
            while (cursor.moveToNext()) {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                String name = cursor.getString(
                        cursor.getColumnIndexOrThrow("name"));
                String instructions = cursor.getString(
                        cursor.getColumnIndexOrThrow("instructions"));

                LinearLayout card = new LinearLayout(this);
                card.setOrientation(LinearLayout.VERTICAL);
                card.setPadding(20, 16, 20, 16);

                LinearLayout.LayoutParams cardParams =
                        new LinearLayout.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.WRAP_CONTENT);
                cardParams.setMargins(0, 0, 0, 20);
                card.setLayoutParams(cardParams);

                TextView recipeName = new TextView(this);
                recipeName.setText(name);
                recipeName.setTextSize(20);
                recipeName.setPadding(0, 0, 0, 8);
                card.addView(recipeName);

                TextView recipeInstructions = new TextView(this);
                recipeInstructions.setText(instructions);
                recipeInstructions.setTextSize(15);
                card.addView(recipeInstructions);

                TextView ingredientsTitle = new TextView(this);
                ingredientsTitle.setText("\nRequired ingredients:");
                ingredientsTitle.setTextSize(16);
                card.addView(ingredientsTitle);

                Cursor ingredientsCursor =
                        databaseHelper.getRecipeIngredients(id);

                try {
                    while (ingredientsCursor.moveToNext()) {
                        String ingredientName =
                                ingredientsCursor.getString(0);
                        double quantity = ingredientsCursor.getDouble(1);
                        String unit = ingredientsCursor.getString(2);

                        TextView ingredient = new TextView(this);
                        ingredient.setText(
                                "\u2022 " + ingredientName + ": " +
                                        quantity + " " + unit);
                        ingredient.setTextSize(15);
                        card.addView(ingredient);
                    }
                } finally {
                    ingredientsCursor.close();
                }

                recipeList.addView(card);
                recipeCount++;
            }
        } finally {
            cursor.close();
        }

        emptyMessage.setVisibility(
                recipeCount == 0 ? TextView.VISIBLE : TextView.GONE);
    }
}
