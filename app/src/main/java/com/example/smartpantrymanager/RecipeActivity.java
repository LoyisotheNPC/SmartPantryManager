
package com.example.smartpantrymanager;

import android.content.Intent;
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
        subtitle.setText(
                "Only recipes you can make with your current pantry are shown."
        );
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
                ViewGroup.LayoutParams.MATCH_PARENT,
                0,
                1
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
                final int recipeId = cursor.getInt(
                        cursor.getColumnIndexOrThrow("id")
                );

                String name = cursor.getString(
                        cursor.getColumnIndexOrThrow("name")
                );

                LinearLayout card = new LinearLayout(this);
                card.setOrientation(LinearLayout.VERTICAL);
                card.setPadding(24, 20, 24, 20);
                card.setClickable(true);
                card.setFocusable(true);

                LinearLayout.LayoutParams cardParams =
                        new LinearLayout.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.WRAP_CONTENT
                        );

                cardParams.setMargins(0, 0, 0, 16);
                card.setLayoutParams(cardParams);

                TextView recipeName = new TextView(this);
                recipeName.setText(name);
                recipeName.setTextSize(20);
                card.addView(recipeName);

                TextView prompt = new TextView(this);
                prompt.setText("Tap to view ingredients and instructions");
                prompt.setTextSize(14);
                prompt.setPadding(0, 8, 0, 0);
                card.addView(prompt);

                card.setOnClickListener(v -> {
                    Intent intent = new Intent(
                            RecipeActivity.this,
                            RecipeDetailActivity.class
                    );

                    intent.putExtra("recipe_id", recipeId);
                    startActivity(intent);
                });

                recipeList.addView(card);
                recipeCount++;
            }
        } finally {
            cursor.close();
        }

        emptyMessage.setVisibility(
                recipeCount == 0 ? TextView.VISIBLE : TextView.GONE
        );
    }
}
