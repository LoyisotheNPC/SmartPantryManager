
package com.example.smartpantrymanager;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class AllRecipesActivity extends AppCompatActivity {

    private DatabaseHelper databaseHelper;
    private LinearLayout recipeList;
    private TextView recipeCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        databaseHelper = new DatabaseHelper(this);

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(24, 24, 24, 24);
        root.setBackgroundColor(0xFFF5F7FA);

        TextView title = new TextView(this);
        title.setText("All Recipes");
        title.setTextSize(28);
        title.setTextColor(0xFF263238);
        title.setGravity(Gravity.CENTER);
        title.setPadding(0, 12, 0, 12);
        root.addView(title);

        TextView subtitle = new TextView(this);
        subtitle.setText(
                "Browse all recipes, even if you don't have every ingredient."
        );
        subtitle.setTextSize(15);
        subtitle.setTextColor(0xFF546E7A);
        subtitle.setGravity(Gravity.CENTER);
        subtitle.setPadding(0, 0, 0, 12);
        root.addView(subtitle);

        recipeCount = new TextView(this);
        recipeCount.setTextSize(16);
        recipeCount.setTextColor(0xFF37474F);
        recipeCount.setPadding(0, 8, 0, 12);
        root.addView(recipeCount);

        ScrollView scrollView = new ScrollView(this);

        recipeList = new LinearLayout(this);
        recipeList.setOrientation(LinearLayout.VERTICAL);

        scrollView.addView(recipeList);

        LinearLayout.LayoutParams scrollParams =
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        0,
                        1
                );

        root.addView(scrollView, scrollParams);

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
            loadAllRecipes();
        }
    }

    private void loadAllRecipes() {
        recipeList.removeAllViews();

        Cursor cursor = null;
        int count = 0;

        try {
            cursor = databaseHelper.getAllRecipes();

            if (cursor != null) {
                int idColumn = cursor.getColumnIndexOrThrow("id");
                int nameColumn = cursor.getColumnIndexOrThrow("name");

                while (cursor.moveToNext()) {
                    int recipeId = cursor.getInt(idColumn);
                    String recipeName = cursor.getString(nameColumn);

                    addRecipeCard(recipeId, recipeName);
                    count++;
                }
            }

            recipeCount.setText(count + " recipes available");

            if (count == 0) {
                TextView emptyMessage = new TextView(this);
                emptyMessage.setText(
                        "No recipes found in the database."
                );
                emptyMessage.setTextSize(16);
                emptyMessage.setPadding(12, 20, 12, 20);
                recipeList.addView(emptyMessage);
            }

        } catch (SQLiteException | IllegalArgumentException e) {
            Toast.makeText(
                    this,
                    "Could not load recipes. Please check the database.",
                    Toast.LENGTH_LONG
            ).show();

            recipeCount.setText("Unable to load recipes.");

            android.util.Log.e(
                    "AllRecipesActivity",
                    "Error loading recipes",
                    e
            );

        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    private void addRecipeCard(int recipeId, String recipeName) {
        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(20, 16, 20, 16);
        card.setBackgroundColor(0xFFFFFFFF);
        card.setElevation(3);

        LinearLayout.LayoutParams cardParams =
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                );

        cardParams.setMargins(0, 0, 0, 16);
        recipeList.addView(card, cardParams);

        TextView nameText = new TextView(this);
        nameText.setText(recipeName);
        nameText.setTextSize(19);
        nameText.setTextColor(0xFF263238);
        card.addView(nameText);

        TextView descriptionText = new TextView(this);
        descriptionText.setText(
                "Tap to view ingredients and instructions"
        );
        descriptionText.setTextSize(14);
        descriptionText.setTextColor(0xFF607D8B);
        descriptionText.setPadding(0, 6, 0, 0);
        card.addView(descriptionText);

        card.setClickable(true);
        card.setFocusable(true);

        card.setOnClickListener(v -> {
            Intent intent = new Intent(
                    AllRecipesActivity.this,
                    RecipeDetailActivity.class
            );

            intent.putExtra("recipe_id", recipeId);
            startActivity(intent);
        });
    }
}
