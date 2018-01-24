package com.starfalling.ourfood;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        findViewById(R.id.add_material_btn).setOnClickListener(this);
        findViewById(R.id.add_purchase_btn).setOnClickListener(this);
        findViewById(R.id.all_material_btn).setOnClickListener(this);
        findViewById(R.id.all_purchase_btn).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.add_material_btn:
                MaterialUtils.showAddMaterialDialog(this);
                break;
            case R.id.add_purchase_btn:
                PurchaseUtils.showAddPurchaseDateDialog(this);
                break;
            case R.id.all_material_btn:
                startActivity(new Intent(this, MaterialActivity.class));
                break;
            case R.id.all_purchase_btn:
                startActivity(new Intent(this, PurchaseActivity.class));
                break;
        }
    }

}
