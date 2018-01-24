package com.starfalling.ourfood;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.starfalling.ourfood.model.Material;

import java.util.List;

import io.realm.Realm;


public class MaterialUtils {
    public static void saveMaterial(final Context context, final Material material) {
        final Realm realm = Realm.getDefaultInstance();
        if (material.getId() == null) {
            realm.executeTransactionAsync(new Realm.Transaction() {
                @Override
                public void execute(Realm bgRealm) {
                    Material materialNew = new Material();
                    materialNew.setName(material.getName());
                    materialNew.setPrice(material.getPrice());
                    materialNew.setId(String.valueOf(System.currentTimeMillis()));
                    materialNew.setPriceUnit(material.getPriceUnit());
                    bgRealm.copyToRealmOrUpdate(materialNew);
                }
            }, new Realm.Transaction.OnSuccess() {
                @Override
                public void onSuccess() {
                    Toast.makeText(context, "新增材料" + material.getName() + "成功!", Toast.LENGTH_SHORT).show();
                    realm.close();
                }
            }, new Realm.Transaction.OnError() {
                @Override
                public void onError(Throwable error) {
                    Toast.makeText(context, "新增材料時發生錯誤,請再試一次: error = " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    realm.close();
                }
            });
        } else {
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    try {
                        final Material result = realm.where(Material.class).equalTo("id", material.getId()).findFirst();
                        if (result != null) {
                            result.setName(material.getName());
                            result.setPriceUnit(material.getPriceUnit());
                            result.setPrice(material.getPrice());
                        }
                    } catch (Exception e) {
                        Toast.makeText(context, "編輯材料時發生錯誤,請再試一次: error = " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                    realm.close();
                }
            });
        }
    }

    public static void deleteMaterial(final Context context, final Material material) {
        final Realm realm = Realm.getDefaultInstance();

        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                try {
                    final Material result = realm.where(Material.class).equalTo("id", material.getId()).findFirst();
                    result.deleteFromRealm();
                } catch (Exception e) {
                    Toast.makeText(context, "刪除材料時發生錯誤,請再試一次: error = " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
                realm.close();
            }
        });
    }

    public static void showAddMaterialDialog(final Context context) {
        showAddMaterialDialog(context, null, null);
    }

    public static void showAddMaterialDialog(final Context context, final Material material, final View.OnClickListener listener) {
        MultiInputMaterialDialogBuilder.InputValidator nonEmptyInputValidator = new MultiInputMaterialDialogBuilder.InputValidator() {
            @Override
            public CharSequence validate(CharSequence input) {
                return TextUtils.isEmpty(input) ? context.getString(android.R.string.no) : null;
            }
        };
        new MultiInputMaterialDialogBuilder(context)
                .addInput(InputType.TYPE_CLASS_TEXT, material == null ? "" : material.getName(), "材料名稱", nonEmptyInputValidator)
                .addInput(InputType.TYPE_CLASS_NUMBER, material == null ? "" : String.valueOf(material.getPrice()), "材料價格(數字)", nonEmptyInputValidator)
                .addInput(InputType.TYPE_CLASS_TEXT, material == null ? "" : material.getPriceUnit(), "材料單位(例如豆腐請寫'盒'", nonEmptyInputValidator)
                .inputs(new MultiInputMaterialDialogBuilder.InputsCallback() {
                    @Override
                    public void onInputs(MaterialDialog dialog, List<CharSequence> inputs, boolean allInputsValidated) {
                        if (allInputsValidated) {
                            String name = inputs.get(0).toString();
                            long money = Long.parseLong(inputs.get(1).toString());
                            String unit = inputs.get(2).toString();
                            Material materialNew = new Material();
                            materialNew.setId(material == null ? null : material.getId());
                            materialNew.setName(name);
                            materialNew.setPrice(money);
                            materialNew.setPriceUnit(unit);
                            MaterialUtils.saveMaterial(context, materialNew);

                            if (listener != null) {
                                listener.onClick(null);
                            }
                            dialog.dismiss();
                        } else {
                            Toast.makeText(context, "請輸入正確的資料", Toast.LENGTH_SHORT).show();
                        }
                    }

                })
                .title(material == null ? "新增材料" : "編輯材料")
                .positiveText("完成")
                .cancelable(true)
                .canceledOnTouchOutside(true)
                .build()
                .show();

    }

    public static void showDeleteDialog(Context context, Material material) {
        showDeleteDialog(context, material, null);
    }

    public static void showDeleteDialog(final Context context, final Material material, final MaterialDialog.SingleButtonCallback callback) {
        if (material == null) {
            return;
        }
        new MaterialDialog.Builder(context)
                .title("是否確定刪除材料" + material.getName() + "?")
                .cancelable(true)
                .canceledOnTouchOutside(true)
                .positiveText("確定")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        deleteMaterial(context, material);
                        if (callback != null) {
                            callback.onClick(dialog, which);
                        }
                    }
                })
                .build().show();
    }
}
