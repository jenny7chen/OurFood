package com.starfalling.ourfood;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.starfalling.ourfood.model.Material;

import java.util.List;

import io.realm.Realm;

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
                showAddMaterialDialog();
                break;
            case R.id.add_purchase_btn:
                showAddPurchaseDialog();
                break;
            case R.id.all_material_btn:
                break;
            case R.id.all_purchase_btn:
                break;
        }
    }

    private void showAddMaterialDialog() {
        new MultiInputMaterialDialogBuilder(this)
                .addInput(InputType.TYPE_CLASS_TEXT, "材料名稱", "", nonEmptyInputValidator)
                .addInput(InputType.TYPE_CLASS_NUMBER, "材料價格(數字)", "", nonEmptyInputValidator)
                .addInput(InputType.TYPE_CLASS_TEXT, "材料單位(例如豆腐請寫'盒'", "", nonEmptyInputValidator)
                .inputs(new MultiInputMaterialDialogBuilder.InputsCallback() {
                    @Override
                    public void onInputs(MaterialDialog dialog, List<CharSequence> inputs, boolean allInputsValidated) {
                        if (allInputsValidated) {
                            String name = inputs.get(0).toString();
                            long money = Long.parseLong(inputs.get(1).toString());
                            String unit = inputs.get(2).toString();
                            saveMaterial(name, money, unit);
                            dialog.dismiss();
                        } else {
                            Toast.makeText(MainActivity.this, "請輸入正確的資料", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .title("新增材料")
                .positiveText("完成")
                .cancelable(true)
                .canceledOnTouchOutside(true)
                .build().show();

    }

    public MultiInputMaterialDialogBuilder.InputValidator nonEmptyInputValidator = new MultiInputMaterialDialogBuilder.InputValidator() {
        @Override
        public CharSequence validate(CharSequence input) {
            return TextUtils.isEmpty(input) ? getString(android.R.string.no) : null;
        }
    };

    private void saveMaterial(final String name, final long price, final String unit) {
        final Realm realm = Realm.getDefaultInstance();
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm bgRealm) {
                Material material = new Material();
                material.setName(name);
                material.setPrice(price);
                material.setId(String.valueOf(System.currentTimeMillis()));
                material.setPriceUnit(unit);
                realm.copyToRealmOrUpdate(material);
            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                Toast.makeText(MainActivity.this, "新增材料" + name + "成功!", Toast.LENGTH_SHORT).show();
                realm.close();
            }
        }, new Realm.Transaction.OnError() {
            @Override
            public void onError(Throwable error) {
                Toast.makeText(MainActivity.this, "新增材料時發生錯誤,請再試一次", Toast.LENGTH_SHORT).show();
                realm.close();
            }
        });
    }

    private void showAddPurchaseDialog() {


    }
}
