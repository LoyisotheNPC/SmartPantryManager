
package com.example.smartpantrymanager;

import android.database.Cursor;
import android.os.Bundle;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class RecipeDetailActivity extends AppCompatActivity {

    private DatabaseHelper databaseHelper;
    private int recipeId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        databaseHelper = new DatabaseHelper(this);
        recipeId = getIntent().getIntExtra("recipe_id", -1);

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(32, 24, 32, 24);

        TextView title = new TextView(this);
        title.setText("Recipe Details");
        title.setTextSize(26);
        title.setGravity(Gravity.CENTER);
        root.addView(title);


        TextView recipeName = new TextView(this);
        recipeName.setTextSize(22);
        recipeName.setPadding(0, 24, 0, 16);
        root.addView(recipeName);

        TextView ingredientsHeading = new TextView(this);
        ingredientsHeading.setText("Required Ingredients");
        ingredientsHeading.setTextSize(19);
        root.addView(ingredientsHeading);

        LinearLayout ingredientsLayout = new LinearLayout(this);
        ingredientsLayout.setOrientation(LinearLayout.VERTICAL);
        ingredientsLayout.setPadding(0, 8, 0, 24);
        root.addView(ingredientsLayout);

        TextView instructionsHeading = new TextView(this);
        instructionsHeading.setText("Cooking Instructions");
        instructionsHeading.setTextSize(19);
        root.addView(instructionsHeading);

        TextView instructionsText = new TextView(this);
        instructionsText.setTextSize(16);
        instructionsText.setPadding(0, 8, 0, 24);
        root.addView(instructionsText);

        Button backButton = new Button(this);
        backButton.setText("Back to Recipes");
        backButton.setOnClickListener(v -> finish());

        root.addView(backButton);
        setContentView(root);

        loadRecipe(recipeName, ingredientsLayout, instructionsText);
    }

    private void loadRecipe(TextView recipeName,
                            LinearLayout ingredientsLayout,
                            TextView instructionsText) {

        if (recipeId < 0) {
            recipeName.setText("Recipe not found.");
            return;
        }

        Cursor recipes = databaseHelper.getAllRecipes();

        try {
            boolean found = false;

            while (recipes.moveToNext()) {
                int id = recipes.getInt(
                        recipes.getColumnIndexOrThrow("id"));

                if (id == recipeId) {
                    recipeName.setText(recipes.getString(
                            recipes.getColumnIndexOrThrow("name")));

                    instructionsText.setText(recipes.getString(
                            recipes.getColumnIndexOrThrow("instructions")));

                    found = true;
                    break;
                }
            }

            if (!found) {
                recipeName.setText("Recipe not found.");
                return;
            }
        } finally {
            recipes.close();
        }

        Cursor ingredients =
                databaseHelper.getRecipeIngredients(recipeId);

        try {
            while (ingredients.moveToNext()) {
                String name = ingredients.getString(0);
                double quantity = ingredients.getDouble(1);
                String unit = ingredients.getString(2);

                TextView item = new TextView(this);
                item.setText("\u2022 " + name + ": " +
                        quantity + " " + unit);
                item.setTextSize(16);
                item.setPadding(0, 4, 0, 4);
                ingredientsLayout.addView(item);
            }
        } finally {
            ingredients.close();
        }
    }
}

